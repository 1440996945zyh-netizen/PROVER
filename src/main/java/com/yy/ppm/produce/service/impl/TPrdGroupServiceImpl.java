package com.yy.ppm.produce.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.enums.SalaryStatusEnum;
import com.yy.ppm.common.mapper.CommonMapper;
import com.yy.ppm.common.mapper.PublicMapper;
import com.yy.ppm.produce.bean.dto.GroupQueryDTO;
import com.yy.ppm.produce.bean.dto.salary.SalaryQueryDTO;
import com.yy.ppm.produce.bean.dto.salary.SalaryQueryExamineDTO;
import com.yy.ppm.produce.bean.po.TPrdGroupDetailPO;
import com.yy.ppm.produce.bean.po.TPrdGroupPO;
import com.yy.ppm.produce.bean.po.TPrdSalaryPO;
import com.yy.ppm.produce.mapper.TPrdGroupMapper;
import com.yy.ppm.produce.mapper.TPrdSalaryMapper;
import com.yy.ppm.produce.service.TPrdGroupService;
import com.yy.ppm.produce.service.TPrdSalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * @Auther chenfs
 * @Description
 * @Date 2023-10-12 17:03
 */
@Service
public class TPrdGroupServiceImpl implements TPrdGroupService {

    @Autowired
    private TPrdGroupMapper tPrdGroupMapper;

    @Resource
    private SecurityUtils securityUtils;

    @Resource
    private CommonMapper commonMapper;

    @Autowired
    private Snowflake snowflake;


    @Override
    public Pages<TPrdGroupPO> listGroup(GroupQueryDTO query, PageParameter parameter) {
        return PageHelperUtils.limit(parameter, () -> {
            return tPrdGroupMapper.listGroup(query);
        });
    }

    @Override
    public List<TPrdGroupPO> listGroupNo(GroupQueryDTO query) {
        return tPrdGroupMapper.listGroup(query);
    }

    @Transactional
    @Override
    public int save(TPrdGroupPO tPrdGroupPO) {
        List<TPrdGroupDetailPO> tPrdGroupDetailPOList = new ArrayList<>();
        TPrdGroupDetailPO tPrdGroupDetailPO = new TPrdGroupDetailPO();
        if (tPrdGroupPO.getId() == null) {
            //新增
            //id
            Long id = snowflake.nextId();
            tPrdGroupPO.setId(id);
            int count = tPrdGroupMapper.save(tPrdGroupPO);
            if (count != 0) {
                List<TPrdGroupDetailPO> detailPOList = tPrdGroupPO.getDetailPOList();
                if (detailPOList != null && detailPOList.size() != 0) {
                    for (TPrdGroupDetailPO pos : detailPOList) {
                        if(pos.getDeptId()!=null && !"".equals(pos.getDeptId())){
                            String[] deptIdArray = pos.getDeptId().split(",");
                            for (int i = 0; i < deptIdArray.length; i++) {
                                tPrdGroupDetailPO = new TPrdGroupDetailPO();
                                tPrdGroupDetailPO.setId(snowflake.nextId());
                                tPrdGroupDetailPO.setGroupId(id);
                                tPrdGroupDetailPO.setDeptId(deptIdArray[i]);
                                tPrdGroupDetailPO.setProcessDetailCode(pos.getProcessDetailCode());
                                tPrdGroupDetailPO.setProcessDetailName(pos.getProcessDetailName());
                                tPrdGroupDetailPOList.add(tPrdGroupDetailPO);
                            }
                        }else{
                            pos.setId(snowflake.nextId());
                            pos.setGroupId(id);
                            tPrdGroupDetailPOList.add(pos);
                        }
                    }
                    tPrdGroupMapper.saveDetail(tPrdGroupDetailPOList);
                } else {
                    throw new BusinessRuntimeException("请添加数据");
                }
            }
            return count;
        } else {
            //修改
            int count = tPrdGroupMapper.update(tPrdGroupPO);
            //删除服务表和主票货关系表
            commonMapper.delete("T_PRD_GROUP_DETAIL", "GROUP_ID", tPrdGroupPO.getId() + "");
            if (count != 0) {
                List<TPrdGroupDetailPO> detailPOList = tPrdGroupPO.getDetailPOList();
                if (detailPOList != null && detailPOList.size() != 0) {
                    for (TPrdGroupDetailPO pos : detailPOList) {
                        if(pos.getDeptId()!=null && !"".equals(pos.getDeptId())){
                            String[] deptIdArray = pos.getDeptId().split(",");
                            for (int i = 0; i < deptIdArray.length; i++) {
                                tPrdGroupDetailPO = new TPrdGroupDetailPO();
                                tPrdGroupDetailPO.setId(snowflake.nextId());
                                tPrdGroupDetailPO.setGroupId(tPrdGroupPO.getId());
                                tPrdGroupDetailPO.setDeptId(deptIdArray[i]);
                                tPrdGroupDetailPO.setProcessDetailCode(pos.getProcessDetailCode());
                                tPrdGroupDetailPO.setProcessDetailName(pos.getProcessDetailName());
                                tPrdGroupDetailPOList.add(tPrdGroupDetailPO);
                            }
                        }else{
                            pos.setId(snowflake.nextId());
                            pos.setGroupId(tPrdGroupPO.getId());
                            tPrdGroupDetailPOList.add(pos);
                        }
                    }
                    tPrdGroupMapper.saveDetail(tPrdGroupDetailPOList);
                } else {
                    throw new BusinessRuntimeException("请添加数据");
                }
            }
            return count;
        }

    }

    @Override
    public TPrdGroupPO getById(Long id) {
        return tPrdGroupMapper.getById(id);
    }

    @Override
    public List<TPrdGroupDetailPO> insertGroup(TPrdGroupPO tPrdGroupPO) {
        List<TPrdGroupDetailPO> newList = tPrdGroupMapper.getNewGroup(tPrdGroupPO);
//        TPrdGroupPO po = tPrdGroupMapper.getById(tPrdGroupPO.getId());
//        List<TPrdGroupDetailPO> detailPOList = po.getDetailPOList();
//        //最终List
//        List<TPrdGroupDetailPO> newList = new ArrayList<>();
//        //过滤未出勤的班组
////        Iterator<TPrdGroupDetailPO> iter = detailPOList.iterator();
////        while (iter.hasNext()) {
////            TPrdGroupDetailPO tPrdGroupDetailPO = iter.next();
////            //根据日期班次班组查询是否点名
////            tPrdGroupMapper.getDeptId(tPrdGroupPO.getWorkDate(), tPrdGroupPO.getClassCode());
////            if (count == 0) {
////                //删除该条数据
////                iter.remove();
////            }
////        }
//        if (po != null) {
////            List<Map<String, Object>> mapList = tPrdGroupMapper.getProcess(po.getProcessCode());
//            //本班次所有的点名列表
//            List<Map<String, Object>> rollList = tPrdGroupMapper.getDeptId(tPrdGroupPO.getWorkDate(), tPrdGroupPO.getClassCode());
//            //查询劳务派工所有班组
//            List<Map<String, Object>> deptIds = tPrdGroupMapper.selectDetpId(tPrdGroupPO.getPlanId());
//            for (TPrdGroupDetailPO tPrdGroupDetailPO : detailPOList) {
//                if(rollList != null && rollList.size() != 0){
//                    for (Map<String, Object> roll : rollList) {
//                        TPrdGroupDetailPO pos = new TPrdGroupDetailPO();
//                        pos.setProcessDetailCode(tPrdGroupDetailPO.getProcessDetailCode());
//                        pos.setProcessDetailName(tPrdGroupDetailPO.getProcessDetailName());
//                        pos.setDeptId(Long.parseLong(roll.get("deptId").toString()));
//                        pos.setDeptName(roll.get("deptName").toString());
//                        newList.add(pos);
//                    }
//                }
//                if(deptIds != null && deptIds.size() != 0){
//                    for (Map<String, Object> deptMap : deptIds) {
//                        TPrdGroupDetailPO pos = new TPrdGroupDetailPO();
//                        pos.setProcessDetailCode(tPrdGroupDetailPO.getProcessDetailCode());
//                        pos.setProcessDetailName(tPrdGroupDetailPO.getProcessDetailName());
//                        pos.setDeptId(Long.parseLong(deptMap.get("deptId").toString()));
//                        pos.setDeptName(deptMap.get("deptName").toString());
//                        newList.add(pos);
//                    }
//                }
//
//            }
//            // 使用Comparator对象对作业过程和班次名称列表进行排序
//            Collections.sort(newList, new Comparator<TPrdGroupDetailPO>() {
//                @Override
//                public int compare(TPrdGroupDetailPO s1, TPrdGroupDetailPO s2) {
//                    int nameCompare = s1.getProcessDetailCode().compareTo(s2.getProcessDetailCode());
//                    if (nameCompare != 0) {
//                        return nameCompare;
//                    } else {
//                        return s1.getDeptName().compareTo(s2.getDeptName());
//                    }
//                }
//            });
//        } else {
//            throw new BusinessRuntimeException("请选择分组");
//        }
        return newList;
    }


}
