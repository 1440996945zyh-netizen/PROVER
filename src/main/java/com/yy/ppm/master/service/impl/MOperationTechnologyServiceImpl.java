package com.yy.ppm.master.service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.ppm.master.bean.dto.MOperationTechnologyDTO;
import com.yy.ppm.master.bean.po.MOperationTechnologyMachinePO;
import com.yy.ppm.master.bean.po.MOperationTechnologyPO;
import com.yy.ppm.master.bean.po.MOperationTechnologyWorkerPO;
import com.yy.ppm.master.bean.po.MOperationTechnologyWorkwarPO;
import com.yy.ppm.master.mapper.MOperationTechnologyMapper;
import com.yy.ppm.master.service.MOperationTechnologyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 作业工艺Service业务层处理
 * */
@RequiredArgsConstructor
@Service
public class MOperationTechnologyServiceImpl implements MOperationTechnologyService {

    @Resource
    MOperationTechnologyMapper operationTechniqueMapper;

    @Resource
    private Snowflake snowflake;
    /**
     * 作业工艺查询
     * */
    @Override
    public Pages<MOperationTechnologyPO> selectAllTechnique(String code, PageParameter pageQuery, String name) {

        //分页查询所有作业工艺
        Pages<MOperationTechnologyPO> typePages = PageHelperUtils.limit(pageQuery,
            () -> operationTechniqueMapper.selectAllTechnique(code,name));

        //拼接型号数据
        StringBuffer cargoCodes = new StringBuffer();
        StringBuffer cargoNames = new StringBuffer();
        typePages.getPages().forEach(type->{

            cargoCodes.append(type.getCargoCodes()).append(",");
            cargoNames.append(type.getCargoNames()).append(",");

            if(cargoCodes.length()>0){
                type.setCargoCodes(cargoCodes.toString().substring(0,cargoCodes.length()-1));
            }else{
                type.setCargoCodes("");
            }

            if(cargoNames.length()>0){
                type.setCargoNames(cargoNames.toString().substring(0,cargoNames.length()-1));
            }else{
                type.setCargoNames("");
            }
        });

        return typePages;
    }


    /**
     * 修改作业工艺
     * */
    @Override
    @Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
    public void updateTechnique(MOperationTechnologyDTO bo) {
        /**
         * 修改作业工艺
         * */
        operationTechniqueMapper.updateTechnique(bo);

        /**
         * 先删除所有子配置
         * */
        operationTechniqueMapper.deleteMachineById(bo.getId());
        operationTechniqueMapper.deleteWorkerById(bo.getId());
        operationTechniqueMapper.deleteWorkwarById(bo.getId());


        /**
         * 批量添加机械配置
         * */
        if(bo.getListMachine()!=null){
            bo.getListMachine().forEach( so->{
                so.setId(snowflake.nextId());
                so.setTechnologyId(bo.getId());
                operationTechniqueMapper.insertMachine(so);
            });
        }

        /**
         * 批量添加工人配置
         * */
        if(bo.getListWorker()!=null){
            bo.getListWorker().forEach( so->{
                so.setId(snowflake.nextId());
                so.setTechnologyId(bo.getId());
                operationTechniqueMapper.insertWorker(so);
            });
    }

        /**
         * 批量添加工属具配置
         * */
        if(bo.getListWorkwar()!=null){
            bo.getListWorkwar().forEach( so->{
                so.setId(snowflake.nextId());
                so.setTechnologyId(bo.getId());
                operationTechniqueMapper.insertWorkwar(so);
            });
        }
}
    /**
     * 新增作业工艺
     * */
    @Override
    @Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
    public void insertTechnique(MOperationTechnologyDTO bo) {

        bo.setId(snowflake.nextId());

        /**
         * 新增作业工艺
         * */
        operationTechniqueMapper.insertTechnique(bo);

        /**
         * 批量添加机械配置
         * */
        if ( bo.getListMachine()!=null){
            bo.getListMachine().forEach(so->{
                so.setId(snowflake.nextId());
                so.setTechnologyId(bo.getId());
                operationTechniqueMapper.insertMachine(so);
            });
        }

        /**
         * 批量添加工人配置
         * */
        if ( bo.getListMachine()!=null){
            bo.getListWorker().forEach(so->{
                so.setId(snowflake.nextId());
                so.setTechnologyId(bo.getId());
                operationTechniqueMapper.insertWorker(so);
            });
        }

        /**
         * 批量添加工属具配置
         * */
        if(bo.getListWorkwar()!=null){
            bo.getListWorkwar().forEach(so->{
                    so.setId(snowflake.nextId());
                    so.setTechnologyId(bo.getId());
                    operationTechniqueMapper.insertWorkwar(so);
                });
        }
    }

    /**
     * 根据id查询某一个作业工艺
     * */
    @Override
    public MOperationTechnologyDTO selectTechniqueById(Long id) {
        MOperationTechnologyDTO bo = operationTechniqueMapper.selectTechniqueById(id);
        /**
         * 查询机械，工人，工属具
         * */
        if(bo!=null){
            bo.setListMachine(operationTechniqueMapper.selectMachineById(id));
            bo.setListWorker(operationTechniqueMapper.selectWorkerById(id));
            bo.setListWorkwar(operationTechniqueMapper.selectWorkwarById(id));
        }
        return bo;
    }

    /**
     * 删除作业工艺
     * */
    @Override
    @Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
    public void deleteTechniqueById(Long id) {
        /**
         * 删除子配置
         * */
        operationTechniqueMapper.deleteMachineById(id);
        operationTechniqueMapper.deleteWorkerById(id);
        operationTechniqueMapper.deleteWorkwarById(id);
        /**
         * 删除作业工艺
         * */
        operationTechniqueMapper.deleteTechniqueById(id);
    }

    /**
     * 查询作业工艺
     * */
    @Override
    public List<MOperationTechnologyPO> selectTechnique(String code, String name) {
        return operationTechniqueMapper.selectTechnique(code, name);
    }
}
