package com.yy.ppm.master.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.google.api.client.util.Lists;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.PinYin4jUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.bean.dto.SysFileDTO;
import com.yy.ppm.common.mapper.SysFileMapper;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.common.service.SysFileService;
import com.yy.ppm.master.bean.dto.MShipDTO;
import com.yy.ppm.master.bean.dto.MShipSearchDTO;
import com.yy.ppm.master.mapper.MShipBlackMapper;
import com.yy.ppm.master.service.MShipBlackService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class MShipBlackServiceImpl implements MShipBlackService {

    @Resource
    private MShipBlackMapper mShipBlackMapper;

    @Resource
    private Snowflake snowflake;
    @Resource
    private CommonService commonService;
    @Resource
    private SysFileService sysFileService;
    @Resource
    private SecurityUtils securityUtils;

    @Resource
    private SysFileMapper sysFileMapper;
    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public Pages<MShipDTO> getList(MShipSearchDTO searchDTO) {

        Pages<MShipDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return mShipBlackMapper.getList(searchDTO);
        });

        return pages;
    }

    /**
     * 查询单条记录
     *
     * @param id
     * @return 实体
     */
    @Override
    public MShipDTO getDetail(Long id) {
        return mShipBlackMapper.getById(id);
    }


    /**
     * 保存
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    //@Transactional(rollbackFor = Exception.class)
    public boolean doSave(MShipDTO dto) {

        // 名称重复 IMO重复 MMSI重复
        if(dto.getId()==null){
            MShipSearchDTO mShipSearchDTO = new MShipSearchDTO();
            mShipSearchDTO.setShipName(dto.getShipName());
            mShipSearchDTO.setImo(dto.getImo());
            mShipSearchDTO.setMmsi(dto.getMmsi());
            List<MShipDTO> shipList = mShipBlackMapper.getCheckList(mShipSearchDTO);
            if(!CollectionUtils.isEmpty(shipList)){
                throw new BusinessRuntimeException(dto.getShipName()+"-"+dto.getImo()+"-"+dto.getMmsi()+"船名-IMO-MMSI-重复");
            }
        }
        //如果助记码为空，则自动生成。
        if (StringUtil.isEmpty(dto.getShorthandCode())) {
            dto.setShorthandCode(PinYin4jUtils.getPinYinHeadChar(dto.getShipName(), dto.getShipName().length()));
        }
/*        if (CollectionUtils.isEmpty(dto.getFileIds())) {
            throw new BusinessRuntimeException("附件必须上传船舶证书、船舶规范");
        }*/
        int count = 0;
        /*// 通过时驳回意见清空
        if ("10".equals(dto.getStatus())) {
            dto.setApprovalBy(securityUtils.getLoginUserId());
            dto.setApprovalName(securityUtils.getLoginUserName());
            dto.setApprovalTime(new Date());
            dto.setIdea("");
        }*/
        // 新增
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());
            count =  mShipBlackMapper.insert(dto);
        } else {// 修改
            MShipDTO mShipDTO = mShipBlackMapper.getById(dto.getId());
            List<SysFileDTO> sysFileDTOS = sysFileMapper.getBusFiles(mShipDTO.getId(),"MASTER_SHIP_01");
            List<Long> ids = Lists.newArrayList();
            sysFileDTOS.forEach(e->ids.add(e.getId()));
            mShipDTO.setFileIds(ids);
//            dto.setBoHaiTongId(mShipDTO.getBoHaiTongId());
            dto.setUpdateBy(securityUtils.getLoginUserId());
            dto.setUpdateByName(securityUtils.getLoginUserName());
            dto.setUpdateTime(new Date());
/*
            saveShipLog(mShipDTO,dto);//保存船舶资料修改日志
*/
            count = mShipBlackMapper.update(dto);

/*            // 调用渤海通接口 进行传输数据
            String jsonString = com.alibaba.fastjson.JSONObject.toJSONString(dto);
            com.alibaba.fastjson.JSONObject dtoObject = com.alibaba.fastjson.JSONObject.parseObject(jsonString);
            bhtConnectService.requestInfo(dtoObject,"updateShipData");*/
        }
        // 附件保存
//        sysFileService.saveFileBusRelation(dto.getFileIds(), dto.getId());
        return count == 1;
    }

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(Long id) {

        // 删除附件及关系表
        sysFileService.delete(null, id);
        // 删除船舶
        return mShipBlackMapper.deleteById(id) == 1;

    }
}
