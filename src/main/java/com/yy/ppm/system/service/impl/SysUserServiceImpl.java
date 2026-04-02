package com.yy.ppm.system.service.impl;

import java.util.*;

import cn.hutool.core.collection.CollUtil;
import com.yy.common.flowable.enums.CommonStatusEnum;
import com.yy.common.flowable.utils.CollectionUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.enums.SelectEnum;
import com.yy.ppm.common.mapper.SelectMapper;
import com.yy.ppm.common.service.SysFileService;
import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yy.common.enums.CommonConstants;
import com.yy.common.enums.CommonEnum;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.common.util.JasyptUtils;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.common.util.str.StringUtil;
import com.yy.ppm.auth.service.UserCacheService;
import com.yy.ppm.common.mapper.CommonMapper;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.system.bean.dto.ProfileDTO;
import com.yy.ppm.system.bean.dto.SysUserDTO;
import com.yy.ppm.system.bean.dto.SysUserSearchDTO;
import com.yy.ppm.system.mapper.SysDeptMapper;
import com.yy.ppm.system.mapper.SysUserMapper;
import com.yy.ppm.system.service.SysUserService;

import cn.hutool.core.lang.Snowflake;

import static com.yy.common.flowable.constants.GlobalErrorCodeConstants.USER_IS_DISABLE;
import static com.yy.common.flowable.constants.GlobalErrorCodeConstants.USER_NOT_EXISTS;
import static com.yy.common.flowable.constants.GlobalErrorCodeConstants.POST_NOT_EXISTS;
import static com.yy.common.flowable.utils.ServiceExceptionUtil.exception;

/**
 * 用户服务实现
 * @author yy
 * @date 2021-02-25
 */
@Service
public class SysUserServiceImpl implements SysUserService {

	/**
	 * 日志组件
	 */
	private static final MicroLogger LOGGER = new MicroLogger(SysUserServiceImpl.class);

	@Resource
	private SecurityUtils securityUtils;

	@Resource
	SysUserMapper sysUserMapper;
	@Resource
	SysDeptMapper sysDeptMapper;

	@Resource
	private CommonMapper baseMapper;

	@Resource
	private SysFileService sysFileService;
	@Resource
	private SelectMapper selectMapper;

	private final RedisTemplate<String, String> redisTemplate;

	private final UserCacheService userCacheService;

	private final CommonService baseService;

	private final Snowflake snowflake;

	public SysUserServiceImpl(
			RedisTemplate<String, String> redisTemplate,
			UserCacheService userCacheService,
			CommonService baseService,
			Snowflake snowflake
	){
		this.redisTemplate = redisTemplate;
		this.userCacheService = userCacheService;
		this.baseService = baseService;
		this.snowflake = snowflake;
	}
	@Override
	public Pages<SysUserDTO> getList(SysUserSearchDTO sysUserSearchDTO) {
		final String methodName = "SysUserServiceImpl:getList";
		LOGGER.enter(methodName, "业务执行");
		Pages<SysUserDTO> pages = PageHelperUtils.limit(sysUserSearchDTO, () -> {
			return sysUserMapper.getList(sysUserSearchDTO);
		});
		LOGGER.exit(methodName, StringUtils.EMPTY);
		return pages;
	}

	@Override
	public SysUserDTO getById(Long id) {

		final String methodName = "SysUserServiceImpl:etByGid";
		LOGGER.enter(methodName, "业务执行");

		// 用户信息
		SysUserDTO sysUserDTO = sysUserMapper.getById(id);
		// 用户角色信息
		sysUserDTO.setRoleIds(sysUserMapper.getRoleListByUserId(id));

		LOGGER.exit(methodName, StringUtils.EMPTY);
		return sysUserDTO;
	}

	/**
	 * 新建、修改用户
	 * @param sysUserDTO
	 * @return
	 */
	@Override
	@Transactional
	public int save(SysUserDTO sysUserDTO) {
		final String methodName = "SysUserServiceImpl:save";
		LOGGER.enter(methodName, "业务执行");

		// 手机号重复
		if (!StringUtil.isEmpty(sysUserDTO.getMobile())) {
			baseService.isRepeate("SYS_USER", "MOBILE", sysUserDTO.getMobile(), StringUtil.getString(sysUserDTO.getId()), "手机号", null );
		}
		// 验证账号重复
		if (!StringUtil.isEmpty(sysUserDTO.getUserAccount())) {
			baseService.isRepeate("SYS_USER", "USER_ACCOUNT", sysUserDTO.getUserAccount(), StringUtil.getString(sysUserDTO.getId()), "登录账号", null);
		}
		// 邮箱重复
		if (!StringUtil.isEmpty(sysUserDTO.getEmail())) {
			baseService.isRepeate("SYS_USER", "EMAIL", sysUserDTO.getMobile(), StringUtil.getString(sysUserDTO.getId()), "邮箱", null);
		}
		// 排序号
		if(sysUserDTO.getSortNum() == null){
			sysUserDTO.setSortNum(Long.valueOf(baseService.getNextValue("sys_user","sort_num",null)));
		}
		//文件ID空指针防护：先判断是否为null，再判断size
		if (sysUserDTO.getFileIds() != null && sysUserDTO.getFileIds().size() > 1) {
			throw new BusinessRuntimeException("只能上传一个签名文件");
		}

		int count = 0;

		// 新增的场合
		if (sysUserDTO.getId() == null) {

			sysUserDTO.setId(snowflake.nextId());
			// 密码加密
			sysUserDTO.setPasswd(JasyptUtils.encrypt(CommonConstants.DEF_PASSWORD));

			// TODO 下次更新密码事件
			sysUserDTO.setPsdUpdDate(new Date());
			count = sysUserMapper.insert(sysUserDTO);

			// 修改的场合
		} else {
			//删除该用户的用户角色
			baseMapper.delete("sys_role_user","user_id",StringUtil.getString(sysUserDTO.getId()));

			count = sysUserMapper.update(sysUserDTO);
		}

		// 批量新增用户角色信息
		if (null != sysUserDTO.getRoleIds() && sysUserDTO.getRoleIds().size() > 0) {
			for (Long roleId : sysUserDTO.getRoleIds()) {
				sysUserMapper.insertRoleUser(roleId, sysUserDTO.getId());
			}
		}

		// 如果是停用状态，立刻重置缓存
		if (CommonEnum.IsUsed.UNUSED.getCode().equals(sysUserDTO.getStatus())) {
			userCacheService.cleanCacheByAccNo(sysUserDTO.getUserAccount());
		}

		sysFileService.saveFileBusRelation(sysUserDTO.getFileIds(),sysUserDTO.getId().toString());

		LOGGER.exit(methodName, StringUtils.EMPTY);
		return count;
	}

	/**
	 * 删除
	 * @param idList
	 * @return
	 */
	@Transactional
	@Override
	public int deleteById(List<Long> idList) {
		int count = 0;
		final String methodName = "SysUserServiceImpl:delete";

		LOGGER.enter(methodName, "业务执行");
		for(Long id : idList){
			SysUserDTO sysUserDTO = sysUserMapper.getById(id);
			if (sysUserDTO != null) {
				// 删除人员
				count = baseMapper.deleteById("sys_user", id);
				// 删除人员角色
				baseMapper.delete("sys_role_user","user_id",id.toString());
				// 清缓存
				userCacheService.cleanCacheByAccNo(sysUserDTO.getUserAccount());
			}
		}
		LOGGER.exit(methodName, StringUtils.EMPTY);

		return count;
	}

	@Override
	@Transactional
	public int resetpassword(Long id) {
		final String methodName = "SysUserServiceImpl：save";
		LOGGER.enter(methodName, "业务执行");

		SysUserDTO sysUserDTO = new SysUserDTO();
		sysUserDTO.setId(id);
		// 加密
		sysUserDTO.setPasswd(JasyptUtils.encrypt(CommonConstants.DEF_PASSWORD));
		// 更新密码
		int count = sysUserMapper.updatePassword(sysUserDTO);

		LOGGER.exit(methodName, StringUtils.EMPTY);
		return count;
	}

	/**
	 * 修改手机号
	 * @param sysUserDTO
	 * @return
	 */
	@Override
	public int updatePhone(SysUserDTO sysUserDTO) {
		final String methodName = "SysUserServiceImpl：updatePhone";
		LOGGER.enter(methodName, "业务执行");

		int count = sysUserMapper.updatePkInfo(sysUserDTO);
		LOGGER.exit(methodName, StringUtils.EMPTY);
		return count;
	}

	/**
	 * 修改邮箱
	 * @param sysUserDTO
	 * @return
	 */
	@Override
	public int updateEmail(SysUserDTO sysUserDTO) {
		final String methodName = "SysUserServiceImpl：updateEmail";
		LOGGER.enter(methodName, "业务执行");
		int count = sysUserMapper.updatePkInfo(sysUserDTO);
		LOGGER.exit(methodName, StringUtils.EMPTY);
		return count;
	}

	/**
	 * 修改状态
	 * @param sysUserDTO
	 * @return
	 */
	@Override
	public int updateStatus(SysUserDTO sysUserDTO) {
		final String methodName = "SysUserServiceImpl：updateStatus";
		LOGGER.enter(methodName, "业务执行");
		int count = sysUserMapper.updatePkInfo(sysUserDTO);
		LOGGER.exit(methodName, StringUtils.EMPTY);
		return count;
	}


	/**
	 * 修改密码
	 * @param sysUserDTO
	 * @return
	 */
	@Override
	public int updatePassword(SysUserDTO sysUserDTO) {
		final String methodName = "SysUserServiceImpl:updatePassword";
		LOGGER.enter(methodName, "业务执行");
		sysUserDTO.setPasswd(JasyptUtils.encrypt(sysUserDTO.getPasswd()));
		int count = sysUserMapper.updatePassword(sysUserDTO);
		LOGGER.exit(methodName, StringUtils.EMPTY);
		return count;
	}

    @Override
    public ProfileDTO profile() {
		ProfileDTO profileDTO = new ProfileDTO();
        // 获取用户信息
		SysUserDTO sysUserDTO = sysUserMapper.getById(securityUtils.getLoginUserId());
		// 拷贝
		BeanUtils.copyProperties(sysUserDTO, profileDTO);

		// 是否超级管理员
		profileDTO.setAdmin("1".equals(sysUserDTO.getIsSuperadminLabel()));
		profileDTO.setUserId(sysUserDTO.getId());
		profileDTO.setUserName(sysUserDTO.getUserAccount());
		profileDTO.setNickName(sysUserDTO.getUserName());
		profileDTO.setAdmin("1".equals(sysUserDTO.getIsSuperadmin()));
		profileDTO.setDeptId(sysUserDTO.getDeptId());
		profileDTO.setStatus(sysUserDTO.getStatus().toString());
		profileDTO.setEmail(sysUserDTO.getEmail());
		profileDTO.setPhonenumber(sysUserDTO.getTel());
		profileDTO.setSex(sysUserDTO.getSex().toString());

		// 获取部门信息
		profileDTO.setDept(sysDeptMapper.getById(sysUserDTO.getDeptId()));
		// 获取角色信息
		//profileDTO.setRoles(sysUserMapper.getRoleListByUserId(securityUtils.getLoginUserId()));
		return profileDTO;
	}

	/**
	 * 根据用户ids批量查询用户信息
	 */
	@Override
	public List<SysUserDTO> getUserList(Collection<Long> ids) {
		return sysUserMapper.getUserList(ids);
	}

	/**
	 * 校验用户们是否有效。如下情况，视为无效：
	 * 1. 用户编号不存在
	 * 2. 用户被禁用
	 *
	 * @param ids 用户编号数组
	 */
	@Override
	public void validateUserList(Collection<Long> ids) {
		if (CollUtil.isEmpty(ids)) {
			return;
		}
		// 获得岗位信息
		List<SysUserDTO> users = sysUserMapper.getUserList(ids);
		Map<Long, SysUserDTO> userMap = CollectionUtils.convertMap(users, SysUserDTO::getId);
		// 校验
		ids.forEach(id -> {
			SysUserDTO user = userMap.get(id);
			if (user == null) {
				throw exception(USER_NOT_EXISTS);
			}
			if (!CommonStatusEnum.ENABLE.getStatus().equals(user.getStatus().intValue())) {
				throw exception(USER_IS_DISABLE, user.getUserName());
			}
		});
	}

	/**
	 * 校验岗位是否有效。如下情况，视为无效：
	 * 1. 岗位编号不存在
	 *
	 * @param postKeys 岗位编号数组
	 */
	@Override
	public void validPostList(Set<String> postKeys) {
		if(StringUtil.isEmpty(postKeys)){
			return;
		}
		// 获得岗位信息
		String condition = " DICT_TYPE = 'POST' AND STATUS = '1'";
		List<Map<String, Object>> posts = selectMapper.getLocalSelect(
				SelectEnum.DICT.getTableName(),
				SelectEnum.DICT.getValueName(),
				SelectEnum.DICT.getLabelName(),
				condition);
		Map<Object, Map<String, Object>> postMap = CollectionUtils.convertMap(posts, post -> post.get("value"));

		postKeys.forEach(postKey -> {
			// 获取当前岗位信息
			Map<String, Object> post = postMap.get(postKey);
			// 校验1：岗位是否存在
			if (post == null) {
				throw exception(POST_NOT_EXISTS);
			}
		});
	}

	/**
	 * 岗位key查询用户
	 * */
	@Override
	public Set<Long> getUserIdListByPostKeys(Set<String> postKeys) {
		return sysUserMapper.getUserPostIdListByPostKey(postKeys);

	}


}
