package com.yy.ppm.master.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.MapUtils;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.bean.dto.CheckDTO;
import com.yy.ppm.common.mapper.CommonMapper;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.master.bean.dto.MDictDataDTO;
import com.yy.ppm.master.bean.dto.MDictTypeDTO;
import com.yy.ppm.master.bean.dto.MDictTypeSearchDTO;
import com.yy.ppm.master.bean.po.MDictDataPO;
import com.yy.ppm.master.bean.po.MDictTypePO;
import com.yy.ppm.master.mapper.MDictMapper;
import com.yy.ppm.master.service.MDictService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Description 字典及字典类型操作实现类
 *
 * @author 孙琦
 * @date 2023-4-26 16:57:35
 */
@Service
public class MDictServiceImpl implements MDictService {
    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(MDictServiceImpl.class);
    /**
     * 雪花算法
     **/
    private final Snowflake snowflake;

    private final CommonService commonService;

    public MDictServiceImpl(
            CommonService commonService,
            Snowflake snowflake
    ){
        this.commonService = commonService;
        this.snowflake = snowflake;
    }

    @Resource
    private MDictMapper dictMapper;

    @Resource
    private CommonMapper commonMapper;

    /**
     * 查询字典类型列表
     *
     * @param mDictTypeSearchDTO  字典实体类
     * @return 响应数据
     */
    @Override
    public Pages<MDictTypeDTO> getDictTypeList(MDictTypeSearchDTO mDictTypeSearchDTO) {
        final String methodName = "DictServiceImpl:getAllDictTypeList";
        LOGGER.enter(methodName, "业务执行");

        Pages<MDictTypeDTO> dictTypeList = PageHelperUtils.limit(mDictTypeSearchDTO, () -> dictMapper.getDictTypeList(mDictTypeSearchDTO));

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return dictTypeList;
    }


    /**
     * 新增、修改字典类型
     * @param po
     */
    @Override
    public void insertOrUpdateDictType(MDictTypeDTO po) {
        final String methodName = "DictServiceImpl:insertDictType";
        LOGGER.enter(methodName, "业务执行");

        commonService.isRepeate("M_DICT_TYPE", "DICT_TYPE", po.getDictType(), StringUtil.getString(po.getId()), "字典类型编号", null);

        commonService.isRepeate("M_DICT_TYPE", "DICT_NAME", po.getDictName(), StringUtil.getString(po.getId()), "字典类型名称", null);

        if(po.getId()==null){
            po.setId(snowflake.nextId());
            dictMapper.insertDictType(po);

        }else{
            dictMapper.updateDictType(po);

        }
        LOGGER.exit(methodName, StringUtils.EMPTY);
    }

    /**
     * 根据id删除字典类型
     * @param id
     */
    @Override
    public void deleteDictTypeById(Long id) {

        final String methodName = "DictServiceImpl:deleteDictTypeById";
        LOGGER.enter(methodName, "业务执行");

        MDictTypeDTO dict = dictMapper.getDictTypeById(id);

        Integer count = commonMapper.getCount("M_DICT_DATA", "DICT_TYPE", dict.getDictType());

        if(count!=null && count>0){

            throw new BusinessRuntimeException("该字典类型包含子数据不能删除！");

        }else{

            count = dictMapper.deleteDictTypeById(id);
            if(count<=0){
                throw new BusinessRuntimeException("字典类型删除失败！");
            }
        }

        LOGGER.exit(methodName, StringUtils.EMPTY);
    }

    /**
     * 根据id查询字典类型
     *
     * @param id
     * @return
     */
    @Override
    public MDictTypeDTO getDictTypeById(Long id) {
        final String methodName = "DictServiceImpl:getDictById";
        LOGGER.enter(methodName, "业务执行");

        MDictTypeDTO dictType = dictMapper.getDictTypeById(id);

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return dictType;
    }

            //字典类型操作↑
            //字典操作↓

    /**
     * 根据id查询字典
     *
     * @param id
     * @return
     */
    @Override
    public MDictDataDTO getDictById(Long id) {
        final String methodName = "DictServiceImpl:getDictById";
        LOGGER.enter(methodName, "业务执行");

        MDictDataDTO dict = dictMapper.getDictById(id);

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return dict;
    }



    /**
     * 新增、修改字典
     * @param po
     */
    @Override
    public void insertOrUpdateDict(MDictDataDTO po) {
        final String methodName = "DictServiceImpl:insertDict";
        LOGGER.enter(methodName, "业务执行");

        // 其他条件
        List<CheckDTO> keyValues = new ArrayList<>();
        keyValues.add(CheckDTO.buildDTO("DICT_TYPE", po.getDictType()));
        commonService.isRepeate("M_DICT_DATA", "DICT_VALUE", po.getDictValue(), StringUtil.getString(po.getId()), "字典编号", keyValues);
        commonService.isRepeate("M_DICT_DATA", "DICT_LABEL", po.getDictLabel(), StringUtil.getString(po.getId()), "字典名称", keyValues);

        if(po.getId()==null){
            po.setId(snowflake.nextId());
            dictMapper.insertDict(po);
        }else{
            dictMapper.updateDict(po);
        }

        LOGGER.exit(methodName, StringUtils.EMPTY);
    }


    /**
     * 根据字典类型获取字典
     * @param pageParameter
     * @param typeCd
     * @return
     */
    @Override
    public Pages<MDictDataDTO> getDictListByType(PageParameter pageParameter, String typeCd) {
        final String methodName = "DictServiceImpl:getAllDictListByType";
        LOGGER.enter(methodName, "业务执行");

        Pages<MDictDataDTO> dictyList = PageHelperUtils.limit(pageParameter, () -> dictMapper.getDictListByType(typeCd));

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return dictyList;
    }


    /**
     * 删除字典
     * @param id
     */
    @Override
    public void deleteDictById(Long id) {
        final String methodName = "DictServiceImpl:deleteDictById";
        LOGGER.enter(methodName, "业务执行");

        Integer count = dictMapper.deleteDictById(id);
        if(count<=0){
                throw new BusinessRuntimeException("字典删除失败！");
            }

        LOGGER.exit(methodName, StringUtils.EMPTY);
    }


    /**
     * 查询全部字典列表
     * @param pageParameter
     * @param po
     * @return
     */
    @Override
    public Pages<MDictDataDTO> getDictList(PageParameter pageParameter, MDictDataDTO po) {
        final String methodName = "DictServiceImpl:getAllDictTypeList";
        LOGGER.enter(methodName, "业务执行");

        Pages<MDictDataDTO> dictList = PageHelperUtils.limit(pageParameter,
                () -> dictMapper.getDictList(po));

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return dictList;
    }

//
//    /**
//     * 修改字典
//     * @param po
//     */
//    @Override
//    public void updateDict(DictPO po) {
//
//    }


}
