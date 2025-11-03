package com.yy.ppm.mySpecialInfo.service.impl;

import com.yy.common.util.SecurityUtils;
import com.yy.ppm.common.enums.MySpecialInfoEnum;
import com.yy.ppm.mySpecialInfo.bean.dto.TMySpecialInfoDTO;
import com.yy.ppm.mySpecialInfo.mapper.TMySpecialInfoMapper;
import com.yy.ppm.mySpecialInfo.service.TMySpecialInfoService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @ClassName 个人特别信息表(TMySpecialInfo)ServiceImpl
 * @author zws
 * @version 1.0.0
 * @Description
 * @createTime 2025年01月17日 10:17:00
 */
@Service
public class TMySpecialInfoServiceImpl implements TMySpecialInfoService {

    @Resource
    private TMySpecialInfoMapper tMySpecialInfoMapper;

    @Resource
    private SecurityUtils securityUtils;


    /**
      * 查询页数
      *
      * @param
      * @return 实体
      */
     @Override
     public TMySpecialInfoDTO getPageNum(TMySpecialInfoDTO dto) {
         dto.setType(MySpecialInfoEnum.myProjectEnum.PAGE_NUM.getCode());
         dto.setUserId(securityUtils.getLoginUserId());
         return tMySpecialInfoMapper.getDetail(dto);
     }

    /**
     * 关注项目
     *
     * @param
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean careSave(TMySpecialInfoDTO tMySpecialInfoDTO) {

        TMySpecialInfoDTO dto =new TMySpecialInfoDTO();
        dto.setType(MySpecialInfoEnum.myProjectEnum.PROJECT.getCode());
        dto.setProjectId(tMySpecialInfoDTO.getProjectId());
        dto.setUserId(securityUtils.getLoginUserId());

        //先删后插
        tMySpecialInfoMapper.noCareDelete(dto);

        return tMySpecialInfoMapper.insert(dto) == 1;


    }
    /**
     * 取关项目
     *
     * @param
     * @return 是否成功
     */
    @Override
    public boolean noCareSave(TMySpecialInfoDTO dto) {
        dto.setType(MySpecialInfoEnum.myProjectEnum.PROJECT.getCode());
        dto.setUserId(securityUtils.getLoginUserId());
        return tMySpecialInfoMapper.noCareDelete(dto) == 1;

    }

//    /**
//     * 关注项目批量保存
//     *
//     * @param list
//     * @return 是否成功
//     */
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public boolean projectInsert(List<TMySpecialInfoDTO> list) {
//
//        commonMapper.delete("T_MY_SPECIAL_INFO", "USER_ID", securityUtils.getLoginUserId().toString());
//
//        //验证有无重复
//        Set<Long> projectIdSet = new HashSet<>();
//        for (TMySpecialInfoDTO dto : list) {
//            if (projectIdSet.contains(dto.getProjectId())) {
//                throw new BusinessRuntimeException("请选择不同的项目！");
//            }
//            projectIdSet.add(dto.getProjectId());
//        }
//
//        Long sortNum=1L;
//        for (TMySpecialInfoDTO dto : list) {
//            dto.setUserId(securityUtils.getLoginUserId());
//            dto.setSortNum(sortNum);
//            dto.setType(MySpecialInfoEnum.myProjectEnum.PROJECT.getCode());
//            sortNum++;
//            dto.setCreateBy(securityUtils.getLoginUserId());
//            dto.setCreateByName(securityUtils.getLoginUserName());
//            dto.setCreateTime(new Date());
//        }
//
//        return tMySpecialInfoMapper.projectInsert(list) > 0;
//
//    }

    /**
     * 删除
     *
     * @param  id
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {

        return tMySpecialInfoMapper.deleteById(id) == 1;

    }


    /**
     * 修改分页数
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePageNum(TMySpecialInfoDTO dto) {
        dto.setUserId(securityUtils.getLoginUserId());
        dto.setType(MySpecialInfoEnum.myProjectEnum.PAGE_NUM.getCode());
        tMySpecialInfoMapper.deletePageNum(dto);
        return tMySpecialInfoMapper.insert(dto)==1;
    }
}

