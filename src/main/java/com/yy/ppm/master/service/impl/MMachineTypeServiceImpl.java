package com.yy.ppm.master.service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.enums.AutoNumEnum;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.master.bean.dto.MMachineTypeDTO;
import com.yy.ppm.master.bean.po.MMachineTypeModelPO;
import com.yy.ppm.master.bean.po.MMachineTypePO;
import com.yy.ppm.master.mapper.MMachineTypeMapper;
import com.yy.ppm.master.service.MMachineTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.*;

/**
 * 机械类型Service业务层处理
 * */
@RequiredArgsConstructor
@Service
public class MMachineTypeServiceImpl implements MMachineTypeService {

    @Resource
    MMachineTypeMapper bMachineTypeMapper;

    @Resource
    private CommonService commonService;

    @Resource
    private Snowflake snowflake;
    /**
     * 查询机械类型
     * */
    @Override
    public Pages<MMachineTypeDTO> listBMachineType(PageParameter pageQuery, String name) {
        //查询机械类型
        Pages<MMachineTypeDTO> typePages = PageHelperUtils.limit(pageQuery,
            () -> bMachineTypeMapper.selectBMachineType(name));

        typePages.getPages().forEach(type->{
            //查询机械型号
            List<MMachineTypeModelPO> so = bMachineTypeMapper.selectBMachineTypeModel(type.getMacTypeCode());

            //拼接型号数据
            StringBuffer modelCode = new StringBuffer();
            StringBuffer modelName = new StringBuffer();
            so.forEach(model->{
                modelCode.append(model.getModelCode()).append(",");
                modelName.append(model.getModelName()).append(",");
            });

            if(modelCode.length()>0){
                type.setModelCode(modelCode.toString().substring(0,modelCode.length()-1));
            }else{
                type.setModelCode("");
            }

            if(modelName.length()>0){
                type.setModelName(modelName.toString().substring(0,modelName.length()-1));
            }else{
                type.setModelName("");
            }
        });

        return typePages;
    }

    /**
     * 根据id查询机械类型
     * */
    @Override
    public MMachineTypeDTO selectBMachineTypeById(String id) {
        MMachineTypeDTO bo = bMachineTypeMapper.selectBMachineTypeById(id);
        if(bo!=null){
            bo.setList(bMachineTypeMapper.selectBMachineTypeModel(bo.getMacTypeCode()));
        }

        return bo;
    }

    /**
     * 修改机械类型
     * */
    @Override
    @Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
    public void saveBMachineType(MMachineTypeDTO bo) {

        // 重复性验证
        commonService.isRepeate("m_machine_type", "mac_type_name", bo.getMacTypeName(), StringUtil.getString(bo.getId()), "类型名称", null);

        List<String> modelList = new ArrayList<>();
        if(bo.getList()!=null && bo.getList().size()>0){
            bo.getList().forEach(model-> {
                if (modelList.indexOf(model.getModelName()) > 0) {
                    throw new BusinessRuntimeException("型号【" + model.getModelName() + "】重复~");
                }
                modelList.add(model.getModelName());
            });
        }

        if (bo.getId() == null) {

            bo.setMacTypeCode(commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.MACHINE_TYPE, null));

            bo.setId(snowflake.nextId());

            // 新增机械类型
            bMachineTypeMapper.insertBMachineType(bo);

        } else {
            //修改机械类型
            bMachineTypeMapper.updateBMachineType(bo);

            // 先删除所有机械型号
            bMachineTypeMapper.deleteBMachineTypeModelByTypeCode(bo.getMacTypeCode());
        }

        //获取子配置列表
        if(bo.getList()!=null && bo.getList().size()>0){
            bo.getList().forEach(model-> {
                model.setId(snowflake.nextId());
                model.setMacTypeCode(bo.getMacTypeCode());
                model.setModelCode(commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.MACHINE_MODEL, bo.getMacTypeCode()));
                bMachineTypeMapper.insertBMachineTypeModel(model);
            });
        }
    }

    /**
     * 删除机械类型
     * */
    @Override
    @Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
    public void deleteBMachineType(Long ids) {
        /**
         * 先删除机械型号
         * */
        bMachineTypeMapper.deleteBMachineTypeModel(ids);
        /**
         * 再删除机械类型
         * */
        bMachineTypeMapper.deleteBMachineType(ids);
    }

    /**
     * 机械型号查询
     * */
    @Override
    public List<MMachineTypeModelPO> listBMachineTypeModel() {
        return bMachineTypeMapper.listBMachineTypeModel();
    }

    @Override
    public List<Map<String, String>> getMacModelByTypeCode(String id) {
        List<MMachineTypeModelPO> mMachineTypeModelPOS = bMachineTypeMapper.getMacModelByTypeCode(id);

        List<Map<String, String>> arrayList = new ArrayList<>();
        for (MMachineTypeModelPO processWithSystem : mMachineTypeModelPOS) {
            Map<String, String> objectObjectHashMap = new HashMap<>();
            objectObjectHashMap.put("label", processWithSystem.getModelName());
            objectObjectHashMap.put("value", String.valueOf(processWithSystem.getModelCode()));
            arrayList.add(objectObjectHashMap);
        }
        return arrayList;
    }
}
