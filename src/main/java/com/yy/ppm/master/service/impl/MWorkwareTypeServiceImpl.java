package com.yy.ppm.master.service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.master.bean.dto.MWorkwareTypeDTO;
import com.yy.ppm.master.bean.po.MWorkwareTypeModelPO;
import com.yy.ppm.master.bean.po.MWorkwareTypePO;
import com.yy.ppm.master.mapper.MWorkwareTypeMapper;
import com.yy.ppm.master.service.MWorkwareTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 工属具Service业务层处理
 *
 */
@RequiredArgsConstructor
@Service
public class MWorkwareTypeServiceImpl implements MWorkwareTypeService {


    @Resource
    private Snowflake snowflake;

    @Resource
    private CommonService commonService;

    @Resource
    MWorkwareTypeMapper bWorkwareTypeMapper;

    /**
     * 查询工属具类型
     * */
    @Override
    public Pages<MWorkwareTypeDTO> listBWorkwareType(PageParameter pageQuery, String name) {
        //查询工属具类型
        Pages<MWorkwareTypeDTO> typePages = PageHelperUtils.limit(pageQuery,
            () -> bWorkwareTypeMapper.selectBWorkwareType(name));

        typePages.getPages().forEach(type->{
            //查询工属具型号
            List<MWorkwareTypeModelPO> so = bWorkwareTypeMapper.selectBWorkwareTypeModel(type.getWorkwareTypeCode());

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
     * 根据id查询工属具类型
     * */
    @Override
    public MWorkwareTypeDTO selectBWorkwareTypeById(Long id) {

        MWorkwareTypeDTO bo = bWorkwareTypeMapper.selectBWorkwareTypeById(id);

        bo.setList(bWorkwareTypeMapper.selectBWorkwareTypeModel(bo.getWorkwareTypeCode()));

        return bo;
    }

    /**
     * 新增工具属类型
     * */
    @Override
    @Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
    public void insertBWorkwareType(MWorkwareTypeDTO bo) {

        bo.setId(snowflake.nextId());

        String maxCode = bWorkwareTypeMapper.getMaxTypeCode();
        if(maxCode==null || maxCode.length()==0){
            maxCode = "0001";
        }else{
            Integer temp = Integer.parseInt(maxCode);
            temp++;
            //不足4位 前面补0
            maxCode = org.apache.commons.lang3.StringUtils.leftPad(temp+"",4, "0");
        }

        bo.setWorkwareTypeCode(maxCode);

        commonService.isRepeate("m_workware_type", "workware_type_name", bo.getWorkwareTypeName(), StringUtil.getString(bo.getId()), "类型名称", null);
        /**
         * 新增工属具类型
         * */
        bWorkwareTypeMapper.insertBWorkwareType(bo);

        if(bo.getList()!=null && bo.getList().size()>0){
            bo.getList().forEach(model->{
                String maxModelCode = null;
                if(model.getTypeCode()!=null){
                    maxModelCode = bWorkwareTypeMapper.getMaxModelCode(model.getTypeCode());
                }

                if(maxModelCode==null || maxModelCode.length()==0){
                    maxModelCode = bo.getWorkwareTypeCode()+"0001";
                }else{
                    Integer temp = Integer.parseInt(maxModelCode);
                    temp++;
                    //不足8位 前面补0
                    maxModelCode = org.apache.commons.lang3.StringUtils.leftPad(temp+"",8, "0");
                }

                model.setModelCode(maxModelCode);
                model.setId(snowflake.nextId());
                model.setTypeCode(bo.getWorkwareTypeCode());

                int count = bWorkwareTypeMapper.getCountByType(model);
                if(count>=1){
                    throw new BusinessRuntimeException("类型："+bo.getWorkwareTypeName()+"，型号:"+model.getModelName()+"重复");
                }

                bWorkwareTypeMapper.insertBWorkwareTypeModel(model);
            });
        }
    }

    /**
     * 修改工属具类型
     * */
    @Override
    @Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
    public void updateBWorkwareType(MWorkwareTypeDTO bo) {

        //修改工属具类型
        bWorkwareTypeMapper.updateBWorkwareType(bo);

         // 先删除所有工属具型号
        bWorkwareTypeMapper.deleteBWorkwareTypeModelByTypeCode(bo.getWorkwareTypeCode());

         //获取子配置列表
        if(bo.getList()!=null && bo.getList().size()>0){
            //找出最大的modelCode
            Optional<MWorkwareTypeModelPO> typeModel = bo.getList().stream()
                .filter(model-> model.getModelCode()!=null && model.getModelCode().length()>0)
                .max(Comparator.comparing(MWorkwareTypeModelPO::getModelCode));

            bo.getList().forEach(model->{
                if(model.getModelCode()==null || model.getModelCode().length()==0){
                    String maxModelCode = null;

                    if(typeModel.isPresent()){
                        maxModelCode = typeModel.get().getModelCode();
                    }
                    if(maxModelCode==null || maxModelCode.length()==0){
                        maxModelCode = bo.getWorkwareTypeCode()+"0001";
                    }else{
                        Integer temp = Integer.parseInt(maxModelCode);
                        temp++;
                        //不足8位 前面补0
                        maxModelCode = org.apache.commons.lang3.StringUtils.leftPad(temp+"",8, "0");
                    }
                    model.setModelCode(maxModelCode);
                }

                model.setTypeCode(bo.getWorkwareTypeCode());

                int count = bWorkwareTypeMapper.getCountByType(model);
                if(count>=1){
                    throw new BusinessRuntimeException("类型："+bo.getWorkwareTypeName()+"，型号:"+model.getModelName()+"重复");
                }

                bWorkwareTypeMapper.insertBWorkwareTypeModel(model);
            });
        }
    }

    /**
     * 删除工属具类型
     * */
    @Override
    @Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
    public void deleteBWorkwareType(Long id) {
        /**
         * 先删除工属具型号
         * */
        bWorkwareTypeMapper.deleteBWorkwareTypeModel(id);
        /**
         * 再删除机械类型
         * */
        bWorkwareTypeMapper.deleteBWorkwareType(id);
    }

}
