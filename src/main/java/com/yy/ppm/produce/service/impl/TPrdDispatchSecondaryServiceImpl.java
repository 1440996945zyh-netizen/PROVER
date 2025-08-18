package com.yy.ppm.produce.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.util.SecurityUtils;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.mapper.PublicMapper;
import com.yy.ppm.produce.bean.dto.DispatchSecondaryBatchReq;
import com.yy.ppm.produce.bean.dto.TPrdDispatchSecondManResultType;
import com.yy.ppm.produce.bean.dto.TPrdDispatchSecondaryDTO;
import com.yy.ppm.produce.bean.dto.TPrdDispatchSecondarySearchDTO;
import com.yy.ppm.produce.bean.dto.workTicket.TPrdWorkTicketDetailDTO;
import com.yy.ppm.produce.mapper.TPrdDispatchSecondaryMapper;
import com.yy.ppm.produce.service.TPrdDispatchSecondaryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @ClassName 作业计划派工表（二次配工）(TPrdDispatchSecondary)ServiceImpl
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月30日 18:16:00
 */
@Service
public class TPrdDispatchSecondaryServiceImpl implements TPrdDispatchSecondaryService {

    @Resource
    private TPrdDispatchSecondaryMapper tPrdDispatchSecondaryMapper;

    @Resource
    public PublicMapper publicMapper;

    @Resource
	private Snowflake snowflake;

    @Resource
    private SecurityUtils securityUtils;

    /**
     * 获取二次派工信息(全部)
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public List<TPrdDispatchSecondaryDTO> getAllList(TPrdDispatchSecondarySearchDTO searchDTO) {
        List<TPrdDispatchSecondaryDTO> result = null;
    	if(searchDTO.getDispatchType() == 1L){
            result = tPrdDispatchSecondaryMapper.getList(searchDTO);
        }
        //装卸队派工查询的时候根据部门id获取 公司
        if(searchDTO.getDispatchType() == 2L){
            result = tPrdDispatchSecondaryMapper.getListLabor(searchDTO);
            for (TPrdDispatchSecondaryDTO item : result) {
                //根据部门获取公司
               List<Map<String,String>> tmpInfo= tPrdDispatchSecondaryMapper.getCompanyByDeptId(item.getDeptParentId()==null?item.getDeptId():item.getDeptParentId());
               tmpInfo.forEach(o->{
                   if("1".equals(String.valueOf(o.get("deptLevel")))){
                       item.setCompanyName(String.valueOf(o.get("deptName")));
                   }
               });
            }
        }
        return result;
    }

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public List<TPrdDispatchSecondaryDTO> getList(TPrdDispatchSecondarySearchDTO searchDTO) {
        List<TPrdDispatchSecondaryDTO> result = null ;

    	// 查询当前登陆人所在部门的部门CODE add by zcc 23/10/20
    	List<Map<String, Object>> userInfoAndDeptInfoList = publicMapper.getUserInfoAndDeptInfo(securityUtils.getLoginUserId());

    	if(searchDTO.getDispatchType() == 1L){
            if(!CollectionUtils.isEmpty(userInfoAndDeptInfoList)) {
                if(userInfoAndDeptInfoList.get(0).get("isSuperadmin") != null
                        && "1".equals(userInfoAndDeptInfoList.get(0).get("isSuperadmin").toString())) {

                } else {
                    if(userInfoAndDeptInfoList.get(0).get("canDispatchDept") != null) {
                        String canDispatchDept = "";
                        for (String data : userInfoAndDeptInfoList.get(0).get("canDispatchDept").toString().split(",")) {
                            canDispatchDept += ("OR T2.DEPT_NO like '" + data + "%'");
                        }

                        searchDTO.setCanDispatchDept(canDispatchDept);
                    }
                    searchDTO.setDeptNo(userInfoAndDeptInfoList.get(0).get("deptNo").toString());
                    if("0001000100050006".equals(userInfoAndDeptInfoList.get(0).get("deptNo"))){
                        //特殊处理，固机队维修班的权限用固机队的
                        searchDTO.setDeptNo("000100010005");
                    }
                }
            }
            result = tPrdDispatchSecondaryMapper.getList(searchDTO);
        }


        //装卸队派工查询的时候根据部门id获取 公司
        if(searchDTO.getDispatchType() == 2L){
            if(!CollectionUtils.isEmpty(userInfoAndDeptInfoList)) {
                if(userInfoAndDeptInfoList.get(0).get("isSuperadmin") != null
                        && "1".equals(userInfoAndDeptInfoList.get(0).get("isSuperadmin").toString())) {

                } else {
                    searchDTO.setDeptCode(userInfoAndDeptInfoList.get(0).get("deptNo").toString());
                    if("0001000100050006".equals(userInfoAndDeptInfoList.get(0).get("deptNo"))){
                        //特殊处理，固机队维修班的权限用固机队的
                        searchDTO.setDeptCode("000100010005");
                    }
                }
            }
            result = tPrdDispatchSecondaryMapper.getListLabor(searchDTO);
            for (TPrdDispatchSecondaryDTO item : result) {
                //根据部门获取公司
               List<Map<String,String>> tmpInfo= tPrdDispatchSecondaryMapper.getCompanyByDeptId(item.getDeptId());
               tmpInfo.forEach(o->{
                   if("1".equals(String.valueOf(o.get("deptLevel")))){
                       item.setCompanyName(String.valueOf(o.get("deptName")));
                   }
               });
            }
        }

        return result;
    }

    /**
      * 查询单条记录
      *
      * @param id
      * @return 实体
      */
     @Override
     public TPrdDispatchSecondaryDTO getDetail(Long id) {
         return tPrdDispatchSecondaryMapper.getById(id);
     }

    /**
     * 保存
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doSave(TPrdDispatchSecondaryDTO dto) {
        List<Map<String,Object>> ticketInfoList = tPrdDispatchSecondaryMapper.getWorkTicketInfo(dto.getWorkPlanId());
        if (!ticketInfoList.isEmpty()){
            throw new BusinessRuntimeException(ticketInfoList.get(0).get("trustNo")+"已进行了"+ticketInfoList.get(0).get("allotType"));
        }

        // 新增
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());
            return tPrdDispatchSecondaryMapper.insert(dto) == 1;

            // 修改
        } else {
            return tPrdDispatchSecondaryMapper.update(dto) == 1;
        }

    }

    /**
     * 批量保存
     * @param dto
     * @return
     */

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doSaveBatch(DispatchSecondaryBatchReq dto) {

        if(dto==null){
            throw new BusinessRuntimeException("没有数据~");
        }

        if (CollectionUtils.isEmpty(dto.getWorkPlanIds())) {
            throw new BusinessRuntimeException("请选择要派工的工班计划~");
        }

        if (StringUtil.isEmpty(dto.getDispatchType())) {
            throw new BusinessRuntimeException("请选择配工类型~");
        }
//        if(dto.getLaborStatus()==null || !dto.getLaborStatus().equals("1")){
//            if (CollectionUtils.isEmpty(dto.getDispatchSecondaryList())) {
//                throw new BusinessRuntimeException("二次派工信息不能为空~");
//            }
//        }

        //循环保存
        dto.getWorkPlanIds().stream().forEach(o->{

            List<Map<String,Object>> ticketInfoList = tPrdDispatchSecondaryMapper.getWorkTicketInfo(o);
            if (!ticketInfoList.isEmpty()){
                throw new BusinessRuntimeException(ticketInfoList.get(0).get("trustNo")+"已进行了"+ticketInfoList.get(0).get("allotType"));
            }

            for (TPrdDispatchSecondaryDTO dis: dto.getDispatchSecondaryList()) {
                dis.setId(snowflake.nextId());
                dis.setWorkPlanId(o);
                dis.setDispatchType(dto.getDispatchType());
            }

            String deptNo = "";
			String canDispatchDept = "";

            // 查询当前用户所在的部门 CODE add by zcc 23/10/20
        	List<Map<String, Object>> userInfoAndDeptInfoList = publicMapper.getUserInfoAndDeptInfo(securityUtils.getLoginUserId());
        	if(!CollectionUtils.isEmpty(userInfoAndDeptInfoList)) {
        		if(userInfoAndDeptInfoList.get(0).get("isSuperadmin") != null
        				&& "1".equals(userInfoAndDeptInfoList.get(0).get("isSuperadmin").toString())) {

        		} else {
        			if(userInfoAndDeptInfoList.get(0).get("canDispatchDept") != null) {

        				for (String data : userInfoAndDeptInfoList.get(0).get("canDispatchDept").toString().split(",")) {
        					canDispatchDept += ("OR T2.DEPT_NO like '" + data + "%'");
    					}
        			}

                    deptNo = userInfoAndDeptInfoList.get(0).get("deptNo").toString();
                    if("0001000100050006".equals(userInfoAndDeptInfoList.get(0).get("deptNo"))){
                        //特殊处理，固机队维修班的权限用固机队的
                        canDispatchDept = "OR T2.DEPT_NO like '000100010005%'";
                    }
        		}
        	}
        	//机械派工删除旧信息
        	if("1".equals(dto.getDispatchType())){
                tPrdDispatchSecondaryMapper.deleteDispatch(o, dto.getDispatchType(), deptNo, canDispatchDept);
            }
        	//装卸队派工删除旧信息
        	if("2".equals(dto.getDispatchType())){
                tPrdDispatchSecondaryMapper.deleteDispatchLabor(o, dto.getDispatchType());

            }
            // 删除旧信息

            // 批量保存
            if (!CollectionUtils.isEmpty(dto.getDispatchSecondaryList())) {
                tPrdDispatchSecondaryMapper.insertBatch(
                        dto.getDispatchSecondaryList(),
                        securityUtils.getLoginUserId(),
                        securityUtils.getLoginUserName(),
                        new Date());
            }
        });
        return Boolean.TRUE;
    }

    /**
     * 删除
     *
     * @param  id
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        //获取二次配工信息
        TPrdDispatchSecondaryDTO byId = tPrdDispatchSecondaryMapper.getById(id);

        List<Map<String,Object>> ticketInfoList = tPrdDispatchSecondaryMapper.getWorkTicketInfo(byId.getWorkPlanId());
        if (!ticketInfoList.isEmpty()){
            throw new BusinessRuntimeException(ticketInfoList.get(0).get("trustNo")+"已进行了"+ticketInfoList.get(0).get("allotType"));
        }
        return tPrdDispatchSecondaryMapper.deleteById(id) == 1;

    }

    /**
     * 批量删除
     *
     * @param  ids
     * @return 是否成功
     */
    @Override
    public boolean deleteByIds(List<Long> ids) {

        if (ids == null || ids.size() == 0) {
            throw new BusinessRuntimeException("请选择要删除的数据~");
        }
        for (Long id : ids) {
            //获取二次配工信息
            TPrdDispatchSecondaryDTO byId = tPrdDispatchSecondaryMapper.getById(id);

            List<Map<String,Object>> ticketInfoList = tPrdDispatchSecondaryMapper.getWorkTicketInfo(byId.getWorkPlanId());
            if (!ticketInfoList.isEmpty()){
                throw new BusinessRuntimeException(ticketInfoList.get(0).get("trustNo")+"已进行了"+ticketInfoList.get(0).get("allotType"));
            }
        }

        return tPrdDispatchSecondaryMapper.deleteByIds(ids) > 1;

    }

    /**
     * 查询劳务列表
     * @param workPlanId
     * @return
     */
    @Override
    public List<TPrdDispatchSecondManResultType> getLaborList(Long workPlanId) {
        return tPrdDispatchSecondaryMapper.getLaborList(workPlanId,null);
    }
    //回显派工用
    @Override
    public List<TPrdDispatchSecondManResultType> getEchoLaborList(Long workPlanId) {
        String deptNo = "";

        // 查询当前用户所在的部门 CODE add by zcc 23/10/20
        List<Map<String, Object>> userInfoAndDeptInfoList = publicMapper.getUserInfoAndDeptInfo(securityUtils.getLoginUserId());
        if(!CollectionUtils.isEmpty(userInfoAndDeptInfoList)) {
            if(userInfoAndDeptInfoList.get(0).get("isSuperadmin") != null
                    && "1".equals(userInfoAndDeptInfoList.get(0).get("isSuperadmin").toString())) {

            } else {
                deptNo = userInfoAndDeptInfoList.get(0).get("deptNo").toString();

                if("0001000100050006".equals(userInfoAndDeptInfoList.get(0).get("deptNo"))){
                    //特殊处理，固机队维修班的权限用固机队的
                    deptNo = "000100010005";
                }
            }
        }
        List<TPrdDispatchSecondManResultType> laborList = tPrdDispatchSecondaryMapper.getLaborList(workPlanId,null);

        List<TPrdDispatchSecondManResultType> laborListTmp;
        if(CollectionUtils.isEmpty(laborList)){
            return laborList;
        }
        laborListTmp = tPrdDispatchSecondaryMapper.getByworkplanId(workPlanId);
        TPrdDispatchSecondManResultType wu = new TPrdDispatchSecondManResultType();
        wu.setDeptParentId(999999999l);
        wu.setDeptParentName("无装卸部门");
        wu.setWorkPlanId(workPlanId);
        laborListTmp.add(wu);
        if(CollectionUtils.isEmpty(laborListTmp)){
            return  laborListTmp;
        }
        Map<Long, TPrdDispatchSecondManResultType> collect = laborList.stream().collect(Collectors.toMap(TPrdDispatchSecondManResultType::getDeptId, Function.identity()));
        String subProcessCode = "";
        String subProcessName = "";
        for(TPrdDispatchSecondManResultType o : laborListTmp){
            if(collect.get(o.getDeptId())!=null){
                TPrdDispatchSecondManResultType dispatchSecondManResultType = collect.get(o.getDeptId());
                o.setDeptId(dispatchSecondManResultType.getDeptId());
                o.setDeptName(dispatchSecondManResultType.getDeptName());
                subProcessCode = o.getSubProcessCode();
                subProcessName = o.getSubProcessName();
            }
        }
        wu.setSubProcessCode(subProcessCode);
        wu.setSubProcessName(subProcessName);
        return laborListTmp;

    }

    @Override
    public List<TPrdDispatchSecondManResultType> getLaborDeptList() {
        return tPrdDispatchSecondaryMapper.getLaborDeptList();
    }

    @Override
    public List<TPrdDispatchSecondManResultType> getLaborGroupList(String deptParentId) {
        return tPrdDispatchSecondaryMapper.getLaborGroupList(deptParentId);
    }
}
