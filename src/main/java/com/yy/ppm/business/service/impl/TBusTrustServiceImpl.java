package com.yy.ppm.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yy.common.enums.CommonEnum;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.concurrent.DistributedLock;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.produce.bean.SyncDTO;
import com.yy.ppm.business.bean.dto.*;
import com.yy.ppm.business.bean.dto.trust.TrustCargoDTO;
import com.yy.ppm.business.bean.dto.trust.TrustDTO;
import com.yy.ppm.business.bean.po.TBusCargoInfoPO;
import com.yy.ppm.business.bean.po.TBusTrustCargoPO;
import com.yy.ppm.business.bean.po.TBusTrustPO;
import com.yy.ppm.business.mapper.TBusCargoInfoMapper;
import com.yy.ppm.business.mapper.TBusTrustCargoMapper;
import com.yy.ppm.business.mapper.TBusTrustMapper;
import com.yy.ppm.business.service.TBusTrustService;
import com.yy.ppm.common.enums.AutoNumEnum;
import com.yy.ppm.common.enums.BusTrustStatusEnum;
import com.yy.ppm.common.enums.DistributedLockKeyPrefixEnum;
import com.yy.ppm.common.mapper.CommonMapper;
import com.yy.ppm.common.service.BusinessCommonService;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.common.service.SysFileService;
import com.yy.ppm.dispatch.bean.dto.TBusTrustLocationDTO;
import com.yy.ppm.dispatch.bean.dto.TBusTrustLocationSearchDTO;
import com.yy.ppm.dispatch.bean.po.TBusTrustLocationPO;
import com.yy.ppm.dispatch.mapper.TBusTrustLocationMapper;
import com.yy.ppm.dispatch.service.TBusTrustLocationService;
import com.yy.ppm.finance.bean.dto.TFdBankCustomerPrepaymentDTO;
import com.yy.ppm.finance.bean.po.TFdBankCustomerPrepaymentPO;
import com.yy.ppm.finance.mapper.TFdBankCustomerPrepaymentMapper;
import com.yy.ppm.master.bean.dto.MCargoDTO;
import com.yy.ppm.master.bean.po.MTrustTypePO;
import com.yy.ppm.master.mapper.MCargoMapper;
import com.yy.ppm.produce.bean.po.TPrdWaterElectricityPO;
import com.yy.ppm.statement.bean.po.TBusHandoverlistPO;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author yy
 * @version 1.0.0
 * @ClassName 作业指令表(TBusTrust)ServiceImpl
 * @Description
 * @createTime 2023年07月05日 09:21:00
 */
@Service
public class TBusTrustServiceImpl implements TBusTrustService {

    @Resource
    private TBusTrustMapper tBusTrustMapper;
    @Resource
    private TBusTrustCargoMapper tBusTrustCargoMapper;
    @Resource
    private CommonMapper commonMapper;
    @Resource
    private Snowflake snowflake;
    @Resource
    private BusinessCommonService businessCommonService;
    @Resource
    private CommonService commonService;
    @Resource
    private SecurityUtils securityUtils;
    @Resource
    private TBusCargoInfoMapper tBusCargoInfoMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private SysFileService sysFileService;

    @Resource
    private MCargoMapper mCargoTypeMapper;

    @Resource
    private TBusTrustLocationService tBusTrustLocationService;

    private static final Pattern PATTERN = Pattern.compile("^\\S+\\s\\S+\\s\\S+\\s\\S+$");

    @Resource
    private TBusTrustLocationMapper tBusTrustLocationMapper;

    private static final String PAYMENT_CONTROL_SWITCH = "PAYMENT_CONTROL_SWITCH";//预缴控制开关参数编号
    private static final String TRUST_CARGO_CHECK = "TRUST_CARGO_CHECK";//票货通知单货物信息校验
    @Getter
    enum statusEnum {

        _10("10", "开启"),

        _20("20", "停止");

        private final String code;

        private final String remark;

        statusEnum(String code, String remark) {
            this.code = code;
            this.remark = remark;
        }
    }

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public Pages<TBusTrustDTO> getList(TBusTrustSearchDTO searchDTO) {
        Pages<TBusTrustDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return tBusTrustMapper.getList(searchDTO);
        });
        if(searchDTO.getTrustType().equals("2")){
            for (TBusTrustDTO y : pages.getPages()) {
                TBusTrustDTO dto = tBusTrustMapper.getById(y.getId());
                boolean isOrderByCreateTime = "装船".equals(dto.getType());
                List<TBusTrustCargoDTO> cargoList = tBusTrustCargoMapper.getSignList(y.getId(), isOrderByCreateTime);
                for(TBusTrustCargoDTO x : cargoList){
                    //过磅量
                    BigDecimal tonCount = tBusTrustCargoMapper.getCount(x.getBusinessNo());
                    if(tonCount!=null && x.getTon()!=null && tonCount.compareTo(x.getTon())>=0){
                        y.setTonFlag("1");
                        break;
                    }else{
                        y.setTonFlag("2");
                    }
                }
            }
        }
        Date currentDate = new Date();
        for (TBusTrustDTO y : pages.getPages()) {
            if(y.getEndTime()!=null && y.getEndTime().before(currentDate)){
                y.setStatusLabel("已过期");
            }
        }
        return pages;
    }

    /**
     * 查询是否已经下发通知单
     * @param shipVoyageId
     * @return 对象列表
     */
    @Override
    public Map<String,Object> isTrust(Long shipVoyageId) {
        TBusTrustSearchDTO searchDTO = new TBusTrustSearchDTO();
        searchDTO.setShipvoyageId(shipVoyageId);
        List<TBusTrustDTO> list = tBusTrustMapper.exportList(searchDTO);
        if(list.isEmpty()){
            Map<String,Object> map = Maps.newHashMap();
            map.put("isTrust","0");
            return map;
        }else{
            Map<String,Object> map = Maps.newHashMap();
            map.put("isTrust","1");
            return map;
        }
    }

    @Override
    public Pages<TBusTrustDTO> getStorageYardList(TBusTrustSearchDTO searchDTO) {
        /*if (StringUtil.isEmpty(searchDTO.getStatus())) {
            searchDTO.setStatus("30");//默认查询状态是已发布的
        }*/
        Pages<TBusTrustDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return tBusTrustMapper.getStorageYardList(searchDTO);
        });
        //显示船名航次
        List<Long> trustIds = pages.getPages().stream().filter(v1 -> v1.getShipvoyageItemId() == null).map(TBusTrustPO::getId).collect(Collectors.toList());
        if (!trustIds.isEmpty()) {
            List<Map<String, Object>> shipvoyageItems = tBusTrustMapper.listShipvoyageItemByTrustIds(trustIds);
            pages.getPages()
                    .stream().filter(v1 -> trustIds.contains(v1.getId()))
                    .forEach(v1 -> {
                        if("疏港".equals(v1.getType())){
                            String shipNameVoyages = shipvoyageItems.stream()
                                    .filter(v2 -> v1.getId().equals(Long.valueOf(String.valueOf(v2.get("trustId")))))
                                    .map(v2 -> String.valueOf(v2.get("shipNameVoyage")))
                                    .collect(Collectors.joining("，"));
                            v1.setShipNameVoyage(shipNameVoyages);
                        }
                    });
        }
        /*
        List<Long> trustIdss = pages.getPages().stream().filter(v1 -> v1.getShipvoyageItemId() != null).map(TBusTrustPO::getId).collect(Collectors.toList());
        if (!trustIdss.isEmpty()) {
            List<Map<String, Object>> shipvoyageItems = tBusTrustMapper.listShipvoyageItemByTrustIds(trustIdss);
            pages.getPages()
                    .stream().filter(v1 -> trustIdss.contains(v1.getId()))
                    .forEach(v1 -> {
                        if("疏港".equals(v1.getType())){
                            String shipNameVoyages = shipvoyageItems.stream()
                                    .filter(v2 -> v1.getId().equals(Long.valueOf(String.valueOf(v2.get("trustId")))))
                                    .map(v2 -> String.valueOf(v2.get("shipNameVoyage")))
                                    .collect(Collectors.joining("，"));
                            v1.setShipNameVoyage(shipNameVoyages);
                        }
                    });
        }*/

        pages.getPages().forEach(o->{
            TBusTrustLocationSearchDTO tmpDto = new TBusTrustLocationSearchDTO();
            tmpDto.setTrustId(o.getId());
            List<TBusTrustLocationDTO> list = tBusTrustLocationService.getListByCondition(tmpDto);
            if(org.apache.commons.collections.CollectionUtils.isNotEmpty(list)){
                List<TBusTrustLocationDTO.Location> locations = Lists.newArrayList();
                List<String> regionIdsTarget = Lists.newArrayList();
                String storehouseName = new String();
                for (int i = 0; i < list.size(); i++) {
                    if(i==0){
                        storehouseName +=list.get(i).getStorehouseName()+"/"+list.get(i).getRegionName();
                    }else {
                        storehouseName +=","+list.get(i).getStorehouseName()+"/"+list.get(i).getRegionName();
                    }
                    TBusTrustLocationDTO.Location location = new TBusTrustLocationDTO.Location();
                    BeanUtil.copyProperties(list.get(i),location);
                    locations.add(location);
                    regionIdsTarget.add(list.get(i).getRegionId());
                }
                o.setMassNamesTarget(storehouseName);
            }
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
    public TBusTrustDTO getDetail(Long id) {
        TBusTrustDTO dto = tBusTrustMapper.getById(id);
        boolean isOrderByCreateTime = "装船".equals(dto.getType());
        List<TBusTrustCargoDTO> cargoList = tBusTrustCargoMapper.getSignList(id, isOrderByCreateTime);
        cargoList.forEach(o->{
            o.setTrustType(dto.getType());
            //过磅量
            BigDecimal tonCount = tBusTrustCargoMapper.getCount(o.getBusinessNo());
            if(tonCount!=null && o.getTon()!=null && tonCount.compareTo(o.getTon())>=0){
                o.setTonFlag("1");
            }else{
                o.setTonFlag("2");
            }
        });
        dto.setCargoList(cargoList);
        cargoList.forEach(v1 -> {
            if (StringUtils.isNotBlank(v1.getHatchs())) {
                String[] hatchNumArr = Arrays.stream(v1.getHatchs().split(",")).toArray(String[]::new);
                v1.setHatchArr(hatchNumArr);
            } else {
                v1.setHatchArr(new String[0]);
            }
        });
        if (dto.getShipvoyageItemId() != null) {
            dto.setShipInfo(tBusTrustCargoMapper.getShipInfo(dto.getShipvoyageItemId()));
        }
        return dto;
    }

    /**
     * 新增/修改表单校验
     *
     * @param dto
     */
    public void insertUpdateValidate(TBusTrustDTO dto) {
        List<MTrustTypePO> trustTypes = tBusTrustMapper.listTrustType();
        MTrustTypePO trustType = trustTypes.stream()
                .filter(v1 -> dto.getType().equals(v1.getTrustType()))
                .findFirst()
                .orElseThrow(() -> new BusinessRuntimeException("错误的通知单类型"));

        if ("1".equals(trustType.getIsSelectProcess())) {
            if (StringUtils.isBlank(dto.getProcessCode())) {
                throw new BusinessRuntimeException("【" + dto.getType() + "】操作过程编码不能为空");
            }
            if (StringUtils.isBlank(dto.getProcessName())) {
                throw new BusinessRuntimeException("【" + dto.getType() + "】操作过程名称不能为空");
            }
        }

        if (!"0".equals(trustType.getIsCreateCargo())) {
            if (CollectionUtils.isEmpty(dto.getCargoList())) {
                if (!"杂项".equals(dto.getType())) {
                    throw new BusinessRuntimeException("【" + dto.getType() + "】票货不能为空");
                }
            } else {
                if ("杂项".equals(dto.getType())) {
                    if (dto.getCargoList().size() > 1) {
                        throw new BusinessRuntimeException("杂项只能选一个货物信息~");
                    }
                }
            }
        }
        if ("1".equals(trustType.getIsCreateCargo())) {
            boolean anyMatch = dto.getCargoList().stream().anyMatch(v1 -> v1.getCargoInfoId() != null);
            if (anyMatch) {
                throw new BusinessRuntimeException("【" + dto.getType() + "】只能添加票货");
            }
        }
        if ("2".equals(trustType.getIsCreateCargo())) {
            boolean anyMatch = dto.getCargoList().stream().anyMatch(v1 -> v1.getCargoInfoId() == null);
            if (anyMatch) {
                throw new BusinessRuntimeException("【" + dto.getType() + "】只能选择票货");
            }
        }
    }

    /**
     * 保存
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(TBusTrustDTO dto) {
        insertUpdateValidate(dto);

        List<MTrustTypePO> trustTypes = tBusTrustMapper.listTrustType();
        MTrustTypePO trustType = trustTypes.stream()
                .filter(v1 -> dto.getType().equals(v1.getTrustType()))
                .findFirst()
                .orElseThrow(null);
        dto.setTrustType(trustType.getTrustGroupType());

        // 待审核
        dto.setStatus(BusTrustStatusEnum.DSH.getCode());

        dto.setId(snowflake.nextId());
        dto.setTrustNo(commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.TRUST_NO, trustType.getShortType() + new DateTime(new Date()).toString("yyMMdd")));

        sysFileService.saveFileBusRelation(dto.getFileIds(), dto.getId());


        tBusTrustMapper.insert(dto);

        if (dto.getCargoList() != null) {
            for (int i = 0; i < dto.getCargoList().size(); i++) {
                TBusTrustCargoDTO cargo = dto.getCargoList().get(i);
                if("疏港".equals(dto.getTrustType())||"陆销".equals(dto.getTrustType())||"装船".equals(dto.getTrustType())){
                    if((!"1".equals(cargo.getIsRelease()) )&& (!"30".equals(cargo.getIsPrePay()))){
                        throw new BusinessRuntimeException("票货未进行货物预缴");
                    }
                }
                cargo.setId(snowflake.nextId());
                cargo.setTrustId(dto.getId());
                MCargoDTO mCargoDTO = mCargoTypeMapper.getCargoByCargoCode(cargo.getCargoCode());
                cargo.setCargoName(mCargoDTO.getCargoName());
                String nextBusinessNo = commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.BUSINESS_NO, dto.getTrustNo() + "_");
                cargo.setBusinessNo(nextBusinessNo);
                if (ArrayUtils.isNotEmpty(cargo.getHatchArr())) {
                    String hatchs = String.join(",", cargo.getHatchArr());
                    cargo.setHatchs(hatchs);
                }
                if (CommonEnum.YesNoMode.YES.getCode().equals(cargo.getIsSecondWeigh())) {
                    cargo.setStatus(CommonEnum.YesNoMode.YES.getCode());
                } else {
                    cargo.setStatus(CommonEnum.YesNoMode.NO.getCode());
                }
                 if("卸船".equals(dto.getType())){

                     List<TBusCargoInfoPO> cargoInfoList = tBusTrustCargoMapper.getcargoinfoList(Collections.singletonList(cargo.getCargoInfoId()));
                     cargoInfoList.forEach(o->{
                         if(!o.getCompanyId().equals(dto.getCompanyId())){
                             throw new BusinessRuntimeException("票货的作业公司与通知单的作业公司不符<br/>"+o.getCargoInfoNo());
                         }
                     });

                    List<TBusHandoverListDTO> handoverListDTOS = tBusTrustCargoMapper.handoverListByCargoInfoId("卸",Arrays.asList(cargo.getCargoInfoId()));
                    handoverListDTOS = handoverListDTOS.stream().filter(o -> o.getTrustCargoId() != null).collect(Collectors.toList());
                    if(!handoverListDTOS.isEmpty()){
                        throw new BusinessRuntimeException(cargo.getCargoInfoNo() + "已经下发过卸船通知单");
                    }
                    tBusTrustCargoMapper.updateXCHandoverlist(cargo.getId(),cargo.getCargoInfoId(),securityUtils.getLoginUserId(),securityUtils.getLoginUserName(),new Date());
                }
                tBusTrustCargoMapper.insert(cargo);

            }
        }

       /* // 更新航次子表转水前船名船号
        if ("1".equals(dto.getIsWeiqiaoPoundRemark())) {
            Matcher matcher = PATTERN.matcher(Optional.ofNullable(dto.getPoundRemark()).orElse(StringUtils.EMPTY));
            if (!matcher.matches()) {
                throw new BusinessRuntimeException("错误的魏桥磅单备注格式，应由空格拆分为四部分");
            }
            if (dto.getShipvoyageItemId() != null) {
                TDisShipvoyageItemPO shipvoyageItem = new TDisShipvoyageItemPO();
                shipvoyageItem.setId(dto.getShipvoyageItemId());
                Iterator<String> poundRemarkArr = Arrays.stream(dto.getPoundRemark().split(StringUtils.SPACE)).iterator();
                if (poundRemarkArr.hasNext()) {
                    shipvoyageItem.setPreChangeShipName(poundRemarkArr.next());
                }
                if (poundRemarkArr.hasNext()) {
                    shipvoyageItem.setPreChangeShipNo(poundRemarkArr.next());
                }
                tBusTrustMapper.updateShipvoyageItem(shipvoyageItem);
            }
        }*/

        verifyOverflow(dto.getId());
    }

    /**
     * 修改
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(TBusTrustDTO dto) {
        insertUpdateValidate(dto);
        DistributedLock.newBuilder().store(redisTemplate)
                .key(DistributedLockKeyPrefixEnum.SHIPVOYAGE_BUS_TRUST_KEY.getCode() + dto.getShipvoyageId())
                .build().run(() -> {
                    TBusTrustDTO oldData = tBusTrustMapper.getById(dto.getId());
                    if (oldData == null) {
                        throw new BusinessRuntimeException("指令不存在~");
                    }
                    if (!BusTrustStatusEnum.DSH.getCode().equals(oldData.getStatus())) {
                        throw new BusinessRuntimeException("只有待审核的指令可以修改~");
                    }

                    sysFileService.saveFileBusRelation(dto.getFileIds(), dto.getId());

                    List<MTrustTypePO> trustTypes = tBusTrustMapper.listTrustType();
                    MTrustTypePO trustType = trustTypes.stream()
                            .filter(v1 -> dto.getType().equals(v1.getTrustType()))
                            .findFirst()
                            .orElseThrow(null);
                    dto.setTrustType(trustType.getTrustGroupType());


                    tBusTrustMapper.update(dto);
                    //先清除卸船交接清单上的trustCargoInfoId 后面新生成的trustCargoId再进行重新复制 trustcago表是先删后插的
            if("卸船".equals(dto.getType())){
                List<TBusTrustCargoPO> trustCargoListByTrustId = tBusTrustMapper.getTrustCargoListByTrustId(dto.getId());
                if(!trustCargoListByTrustId.isEmpty()){
                    List<Long> cargoInfoIds = trustCargoListByTrustId.stream().map(o -> Optional.of(o.getCargoInfoId()).orElseThrow(() -> new BusinessRuntimeException("更新交接清单时没有获取到票货信息"))).collect(Collectors.toList());

                    //校验卸船通知单作业公司和票货的作业公司是否一致
                    List<TBusCargoInfoPO> cargoInfoList = tBusTrustCargoMapper.getcargoinfoList(cargoInfoIds);
                    cargoInfoList.forEach(o->{
                        if(!o.getCompanyId().equals(dto.getCompanyId())){
                            throw new BusinessRuntimeException("票货的作业公司与通知单的作业公司不符<br/>"+o.getCargoInfoNo());
                        }
                    });
                    cargoInfoIds.forEach(o->{
                        tBusTrustCargoMapper.updateXCHandoverlist(null,o,securityUtils.getLoginUserId(),securityUtils.getLoginUserName(),new Date());
                    });
                }
            }


                    commonMapper.delete("T_BUS_TRUST_CARGO", "TRUST_ID", StringUtil.getString(dto.getId()));
                    //集港通知单同步删除客户委托单的子表信息并新增

                    if (dto.getCargoList() != null) {
                        for (int i = 0; i < dto.getCargoList().size(); i++) {
                            TBusTrustCargoDTO cargo = dto.getCargoList().get(i);
                            cargo.setId(snowflake.nextId());
                            cargo.setTrustId(dto.getId());
                            MCargoDTO mCargoDTO = mCargoTypeMapper.getCargoByCargoCode(cargo.getCargoCode());
                            cargo.setCargoName(mCargoDTO.getCargoName());
                            String nextBusinessNo = commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.BUSINESS_NO, dto.getTrustNo() + "_");
                            cargo.setBusinessNo(nextBusinessNo);
                            if (ArrayUtils.isNotEmpty(cargo.getHatchArr())) {
                                String hatchs = String.join(",", cargo.getHatchArr());
                                cargo.setHatchs(hatchs);
                            } else {
                                cargo.setHatchs(null);
                            }
                            if (CommonEnum.YesNoMode.YES.getCode().equals(cargo.getIsSecondWeigh())) {
                                cargo.setStatus(CommonEnum.YesNoMode.YES.getCode());
                            } else {
                                cargo.setStatus(CommonEnum.YesNoMode.NO.getCode());
                            }
                            tBusTrustCargoMapper.insert(cargo);
                            if("卸船".equals(dto.getType())
                                    ||"集港".equals(dto.getType())
                                    ||"拆箱集港".equals(dto.getType())){
                                updatePreAmountStatus(dto.getId());
                            }
                            if("卸船".equals(dto.getType())){
                                tBusTrustCargoMapper.updateXCHandoverlist(cargo.getId(),cargo.getCargoInfoId(),securityUtils.getLoginUserId(),securityUtils.getLoginUserName(),new Date());
                            }
                        }

                    }

                    /*// 更新航次子表转水前船名船号
                    if ("1".equals(dto.getIsWeiqiaoPoundRemark())) {
                        Matcher matcher = PATTERN.matcher(Optional.ofNullable(dto.getPoundRemark()).orElse(StringUtils.EMPTY));
                        if (!matcher.matches()) {
                            throw new BusinessRuntimeException("错误的魏桥磅单备注格式，应由空格拆分为四部分");
                        }
                        if (dto.getShipvoyageItemId() != null) {
                            TDisShipvoyageItemPO shipvoyageItem = new TDisShipvoyageItemPO();
                            shipvoyageItem.setId(dto.getShipvoyageItemId());
                            Iterator<String> poundRemarkArr = Arrays.stream(dto.getPoundRemark().split(StringUtils.SPACE)).iterator();
                            if (poundRemarkArr.hasNext()) {
                                shipvoyageItem.setPreChangeShipName(poundRemarkArr.next());
                            }
                            if (poundRemarkArr.hasNext()) {
                                shipvoyageItem.setPreChangeShipNo(poundRemarkArr.next());
                            }
                            tBusTrustMapper.updateShipvoyageItem(shipvoyageItem);
                        }
                    }*/
                });

        verifyOverflow(dto.getId());
    }
    @Resource
    private TFdBankCustomerPrepaymentMapper tFdBankCustomerPrepaymentMapper;
    private void updatePreAmountStatus(Long id){
        //集港和卸船
        List<TBusTrustCargoDTO> trustCargoDTOS = tBusTrustCargoMapper.getList(id);
        List<TFdBankCustomerPrepaymentDTO> prepaymentDTO = tBusTrustMapper.getPrepayment(id);
        Map<Long, List<TFdBankCustomerPrepaymentDTO>> map = prepaymentDTO.stream().collect(Collectors.groupingBy(TFdBankCustomerPrepaymentDTO::getCargoInfoId));
        for (TBusTrustCargoDTO trustCargoDTO : trustCargoDTOS) {
            List<TFdBankCustomerPrepaymentDTO> list = map.get(trustCargoDTO.getCargoInfoId());
            BigDecimal sum = BigDecimal.ZERO;
            if(CollectionUtils.isNotEmpty(list)){
                for (TFdBankCustomerPrepaymentDTO tFdBankCustomerPrepaymentDTO : list) {
                    sum = sum.add(tFdBankCustomerPrepaymentDTO.getPrepaymentAmount());
                }
            }
            BigDecimal val = sum.subtract(Optional.ofNullable(trustCargoDTO.getEstAmount()).orElse(BigDecimal.ZERO));
//            BigDecimal val = sum.subtract(ObjectUtil.isNotEmpty(trustCargoDTO)?(ObjectUtil.isNotEmpty(trustCargoDTO.getEstAmount())?trustCargoDTO.getEstAmount():BigDecimal.ZERO):BigDecimal.ZERO);
            String isPrePay = "";
            if(val.compareTo(BigDecimal.ZERO)<=0){
                if(sum.equals(BigDecimal.ZERO)){
                    //未预缴
                    isPrePay = "10";
                }else{
                    //部分预缴
                    isPrePay = "20";
                }
            }else{
                //完全预缴
                isPrePay = "30";
            }
            TBusCargoInfoDTO cargoInfoDTO = new TBusCargoInfoDTO();
            cargoInfoDTO.setId(trustCargoDTO.getCargoInfoId());
            cargoInfoDTO.setCargoInfoId(trustCargoDTO.getCargoInfoId());
            cargoInfoDTO.setIsPrePay(isPrePay);
            if(trustCargoDTO.getCargoInfoId()!=null){
                tBusCargoInfoMapper.updateIsPrePay(cargoInfoDTO);
            }
        }
    }


    /**
     * 驳回
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reject(TBusTrustDTO dto) {
        DistributedLock.newBuilder().store(redisTemplate)
                .key(DistributedLockKeyPrefixEnum.SHIPVOYAGE_BUS_TRUST_KEY.getCode() + dto.getShipvoyageId())
                .build().run(() -> {
            TBusTrustDTO oldData = tBusTrustMapper.getById(dto.getId());
            if (oldData == null) {
                throw new BusinessRuntimeException("指令不存在~");
            }
            if (!BusTrustStatusEnum.DSH.getCode().equals(oldData.getStatus())) {
                throw new BusinessRuntimeException("只有待发布的指令可以驳回~");
            }
            if (oldData.getIsReject() != null && oldData.getIsReject() == 1) {
                throw new BusinessRuntimeException("该指令已被驳回~");
            }
            tBusTrustMapper.reject(dto);
        });

    }

    /**
     * 发布
     *
     * @param id
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doRelease(Long id) {
        TBusTrustDTO oldData = tBusTrustMapper.getById(id);
        if (oldData == null) {
            throw new BusinessRuntimeException("指令不存在~");
        }

        if (StringUtil.getInt(oldData.getStatus()) >= Integer.parseInt(BusTrustStatusEnum.YFB.getCode())) {
            throw new BusinessRuntimeException("指令已发布过~");
        }

        // 预估金额的通知单类型，发布判断有无预缴
        List<MTrustTypePO> trustTypes = tBusTrustMapper.listTrustType();
        MTrustTypePO trustType = trustTypes.stream()
                .filter(v1 -> oldData.getType().equals(v1.getTrustType()))
                .findFirst()
                .orElseThrow(null);
        if ("1".equals(trustType.getIsEstimateAmount())) {
            List<TFdBankCustomerPrepaymentPO> bankCustomerPrepayments = tBusTrustMapper.listBankCustomerPrepayment(id);
            if (bankCustomerPrepayments.isEmpty()) {
//                throw new BusinessRuntimeException("未预缴无法发布");
            }
        }

        TBusTrustDTO dto = new TBusTrustDTO();
        dto.setId(id);
        // 已发布
        dto.setStatus(BusTrustStatusEnum.YFB.getCode());
        dto.setReleaseBy(securityUtils.getLoginUserId());
        dto.setReleaseByName(securityUtils.getLoginUserName());

        int count = tBusTrustMapper.updateNotNull(dto);

        if("1".equals(oldData.getTrustType())){
            SyncDTO syncDto = new SyncDTO();
            syncDto.setId(snowflake.nextId());
            syncDto.setBizId(id);
        //    syncDto.setBizType(BusSyncEnum.LOAD_UNLOAD_HEAD.getCode());
            syncDto.setIsDelete("0");
            //SpringUtils.getBean(BusSyncServiceImpl.class).insertBatch(syncDto);
        }
        // 指令货物
        List<TBusTrustCargoDTO> cargoList = tBusTrustCargoMapper.getList(id);

        if("1".equals(oldData.getTrustType())) {
            for (TBusTrustCargoDTO tBusTrustCargoDTO : cargoList) {
                SyncDTO syncCargoDto = new SyncDTO();
                syncCargoDto.setId(snowflake.nextId());
                syncCargoDto.setBizId(tBusTrustCargoDTO.getId());
               // syncCargoDto.setBizType(BusSyncEnum.LOAD_UNLOAD_LIST.getCode());
                syncCargoDto.setIsDelete("0");
               // SpringUtils.getBean(BusSyncServiceImpl.class).insertBatch(syncCargoDto);
            }
        }
        if("2".equals(oldData.getTrustType())) {
            for (TBusTrustCargoDTO tBusTrustCargoDTO : cargoList) {
                SyncDTO syncCargoDto = new SyncDTO();
                syncCargoDto.setId(snowflake.nextId());
                syncCargoDto.setBizId(tBusTrustCargoDTO.getId());
              //  syncCargoDto.setBizType(BusSyncEnum.CHSPIP_HEAD.getCode());
                syncCargoDto.setIsDelete("0");
               // SpringUtils.getBean(BusSyncServiceImpl.class).insertBatch(syncCargoDto);
            }
        }

        boolean anyMatch = Stream.of("卸船", "集港", "拆箱集港").anyMatch(v1 -> v1.equals(oldData.getType()));
        if (anyMatch) {
            anyMatch = cargoList.stream().anyMatch(v1 -> v1.getContractRateId() == null);
            if (anyMatch) {
                throw new BusinessRuntimeException("存在未选择单价的票货信息，无法发布~");
            }
        }
        //获取预缴开关，Y代表打开，放货指令发布时控制预缴。N代表关闭，放货指令发布时不进行控制。
        String paymentControlSwitch = tBusTrustMapper.getPaymentControlSwitch(PAYMENT_CONTROL_SWITCH);
        if("Y".equals(paymentControlSwitch)){
            //校验是否预缴
            if(Stream.of("陆销", "疏港", "装船").anyMatch(v1 -> v1.equals(oldData.getType()))){
                //获取该指令是否存在未预缴的票货信息
                List<Map<String,Object>> checkList = tBusTrustMapper.getPrePayInfoByTrustId(id);
              if(checkList.isEmpty()){
                    //throw new BusinessRuntimeException("没有获取到票货信息");
                }else {
                  List<Map<String,Object>> payTypeCheckList = tBusTrustMapper.getPrePayTypeByTrustId(checkList.stream().map(o->Long.valueOf(String.valueOf(o.get("cargoInfoId")))).collect(Collectors.toList()));
                  Map<Long, String> collect = payTypeCheckList.stream().collect(Collectors.toMap(o -> Long.valueOf(String.valueOf(o.get("cargoInfoId"))), o -> String.valueOf(o.get("payType")), (k1, k2) -> k1));

                  checkList.forEach(tmpCheck-> {
                        System.out.println(tmpCheck.get("isRelease").toString() + "_" + tmpCheck.get("isPrePay")+
                                "_"+tmpCheck.get("cargoInfoId")+"_"+collect.get(Long.valueOf(String.valueOf(tmpCheck.get("cargoInfoId")))));
                        //只有合同上是预付费的才做校验
                        if("10".equals(collect.get(Long.valueOf(String.valueOf(tmpCheck.get("cargoInfoId")))))){
                            if(!"1".equals(String.valueOf( tmpCheck.get("isRelease")))
                                    &&!"30".equals(String.valueOf(tmpCheck.get("isPrePay")))){
                                throw new BusinessRuntimeException(String.valueOf(tmpCheck.get("cargoInfoNo"))+"没有预缴");
                            }
                        }
                    });
                }

            }
        }

        // 为后面插入票货与船名航次关联关系提供票货信息
        List<TBusCargoInfoPO> tmpCargoInfoList;

        if ("1".equals(trustType.getIsCreateCargo())) {
            tmpCargoInfoList = new ArrayList<>();
            for (TBusTrustCargoDTO temp : cargoList) {
                TBusCargoInfoDTO busCargoInfoDTO = new TBusCargoInfoDTO();
                BeanUtil.copyProperties(temp, busCargoInfoDTO);
                busCargoInfoDTO.setParentId(null);
                busCargoInfoDTO.setRootId(null);
                busCargoInfoDTO.setId(snowflake.nextId());
                // 票货编号
                busCargoInfoDTO.setCargoInfoNo(commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.MAIN_CARGO_INFO, null));
                busCargoInfoDTO.setRightsQuantity(temp.getTon());
                busCargoInfoDTO.setSurplusRightsQuantity(temp.getTon());
                busCargoInfoDTO.setTradeType(oldData.getTradeType());
                busCargoInfoDTO.setShipvoyageItemId(oldData.getShipvoyageItemId());
                busCargoInfoDTO.setShipvoyageId(oldData.getShipvoyageId());
                busCargoInfoDTO.setShipName(oldData.getShipName());
                busCargoInfoDTO.setVoyage(oldData.getVoyage());
                busCargoInfoDTO.setCompanyId(oldData.getCompanyId());
                busCargoInfoDTO.setCompanyName(oldData.getCompanyName());
                if("卸船".equals(oldData.getType())){
                    busCargoInfoDTO.setSource("10");
                }else if("集港".equals(oldData.getType())||"拆箱集港".equals(oldData.getType())){
                    busCargoInfoDTO.setSource("20");
                }
                busCargoInfoDTO.setTrustId(id);
                tBusCargoInfoMapper.insert(busCargoInfoDTO);
                //为记录票货的船名航次做准备
                tmpCargoInfoList.add(busCargoInfoDTO);

                TBusTrustCargoDTO busTrustCargoDTO = new TBusTrustCargoDTO();
                busTrustCargoDTO.setId(temp.getId());
                busTrustCargoDTO.setCargoInfoId(busCargoInfoDTO.getId());
                tBusTrustCargoMapper.updateNotNull(busTrustCargoDTO);
            }
        }else{
            tmpCargoInfoList = tBusTrustCargoMapper.getCargoInfoListByTrustId(id);
        }
        //卸船通知单回写指令id 卸船通知单校验计划量
        if("卸船".equals(oldData.getType())){
            if (!tmpCargoInfoList.isEmpty()) {
                //通知单的量不能超过交接清单的量、判断通知单上的作业公司和卸船交接清单上的作业公司是否一致
                List<TBusHandoverListDTO> tmpHandoverList = tBusTrustCargoMapper.gethandoverListByCargoInfoId(tmpCargoInfoList.stream().map(TBusCargoInfoPO::getId).distinct().collect(Collectors.toList()));
                List<TBusCargoInfoPO> cargoInfoList = tBusTrustCargoMapper.getcargoinfoList(tmpCargoInfoList.stream().map(TBusCargoInfoPO::getId).distinct().collect(Collectors.toList()));
                String trustCargoCheck = tBusTrustMapper.getPaymentControlSwitch(TRUST_CARGO_CHECK);
                cargoInfoList.forEach(o->{
                    if(!o.getCompanyId().equals(oldData.getCompanyId())){
                        throw new BusinessRuntimeException("票货的作业公司与通知单的作业公司不符<br/>"+o.getCargoInfoNo());
                    }
                    if("Y".equals(trustCargoCheck)){
                        if(!o.getCargoCode().equals(tmpCargoInfoList.stream().filter(tmpV->o.getId().equals(tmpV.getId())).findFirst()
                                .orElseThrow(()->new BusinessRuntimeException("通知单发布，货物信息校验信息获取失败")).getCargoCode())){
                            throw new BusinessRuntimeException("票货的货物信息与通知单的货物信息不符<br/>"+o.getCargoInfoNo());
                        }
                    }
                });
                //判断是否超交接清单上的货物浮动量的上限
                BigDecimal minTon = tmpHandoverList.stream().map(o -> {
                    return o.getTon().subtract(o.getTon().multiply(Optional.of(o.getFloatTon()).orElse(new BigDecimal("1")).divide(BigDecimal.valueOf(1000))));
                }).collect(Collectors.toList()).stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
                BigDecimal maxTon = tmpHandoverList.stream().map(o -> {
                    return o.getTon().add(o.getTon().multiply(Optional.of(o.getFloatTon()).orElse(new BigDecimal("1")).divide(new BigDecimal(1000))));
                }).collect(Collectors.toList()).stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

                if(oldData.getPlanTon().compareTo(maxTon)>0){
                    throw  new BusinessRuntimeException("通知单计划量超过交接单上货物重量的浮动上限，重量浮动上限："+maxTon);
                }

                //回写卸船指令id
                List<Long> tmpTrustIds = tBusTrustCargoMapper.getCargoTrustIdByIds(tmpCargoInfoList.stream().map(TBusCargoInfoPO::getId).distinct().collect(Collectors.toList()));
                if(!tmpTrustIds.isEmpty()){
                    throw new BusinessRuntimeException("票货重复下发卸船通知单！卸船票货已经下发过卸船通知单");
                }
                tBusTrustCargoMapper.updateXCCargoInfo(tmpCargoInfoList.stream().map(TBusCargoInfoPO::getId).distinct().collect(Collectors.toList()),id,securityUtils.getLoginUserId(),securityUtils.getLoginUserName(),new Date());
            }
        }

        //装卸船的通知单的船名航次信息写入 cargo_ship 表
        if("装船".equals(oldData.getType())||"卸船".equals(oldData.getType())){
            //获取该票货对应的所有的船舶信息表的数据  比对区分 增删改
            if(!tmpCargoInfoList.isEmpty()){
                tmpCargoInfoList.stream().forEach(o->{
                    //好像是为了防止连点重复审核了
                List<TBusCargoShipDTO> tmpCargoShipList = tBusTrustCargoMapper.getCargoShipWithTrust(id,o.getId(),oldData.getShipvoyageItemId());
                    if(tmpCargoShipList.isEmpty()){
                        //删除 cargoInfoId和trustId 对应的carogShip表记录
                        tBusTrustCargoMapper.deleteCargoShipByCargoTrust(o.getId(),id);

                        TBusCargoShipDTO tBusCargoShipDTO = new TBusCargoShipDTO();
                        tBusCargoShipDTO.setId(snowflake.nextId());
                        tBusCargoShipDTO.setType("装船".equals(oldData.getType())?"1":("卸船".equals(oldData.getType())?"2":""));
                        tBusCargoShipDTO.setTrustId(id);
                        tBusCargoShipDTO.setShipName(oldData.getShipName());
                        tBusCargoShipDTO.setShipVoyageId(oldData.getShipvoyageId());
                        tBusCargoShipDTO.setShipVoyageItemId(oldData.getShipvoyageItemId());
                        tBusCargoShipDTO.setVoyage(oldData.getVoyage());
                        tBusCargoShipDTO.setCargoInfoId(o.getId());
                        tBusTrustCargoMapper.insertCargoShip(tBusCargoShipDTO);
                    }
                });
            }
        }



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
        TBusTrustDTO oldData = tBusTrustMapper.getById(id);
        if (oldData == null) {
            throw new BusinessRuntimeException("指令不存在~");
        }

        if (!BusTrustStatusEnum.DSH.getCode().equals(oldData.getStatus())) {
            throw new BusinessRuntimeException("只有待审核的指令可以删除~");
        }
        List<TBusTrustCargoPO> trustCargoListByTrustId = tBusTrustMapper.getTrustCargoListByTrustId(id);

        if (!trustCargoListByTrustId.isEmpty()) {
            trustCargoListByTrustId.forEach(cargo->{
                tBusTrustCargoMapper.updateXCHandoverlist(null,cargo.getCargoInfoId(),securityUtils.getLoginUserId(),securityUtils.getLoginUserName(),new Date());
            });
        }

        // 删除附件及关系表
        sysFileService.delete(null, oldData.getId());

        // 删除指令货物
        commonMapper.delete("T_BUS_TRUST_CARGO", "TRUST_ID", StringUtil.getString(id));

        // 删除指令
        return tBusTrustMapper.deleteById(id) == 1;
    }

    /**
     * 查询票货信息
     *
     * @return
     */
    @Override
    public Pages<TBusCargoInfoDTO> getTicketInfo(Long companyId, String tradeType, Long cargoAgentId, String cargoOwnerId, PageParameter pageParameter, String isLuxiao, String isShugang,String scn,String shipvoyageItemId, String cargoInfoNo, String businessNo,String trustType) {
        return PageHelperUtils.limit(pageParameter, () -> {
            Page<TBusCargoInfoDTO> page = tBusTrustMapper.getPortStorageInfo(companyId, tradeType, cargoAgentId, cargoOwnerId, isLuxiao, isShugang, scn, shipvoyageItemId, cargoInfoNo, businessNo,trustType);
            if (!page.isEmpty()) {
                List<Long> ids = page.stream().map(TBusCargoInfoDTO::getCargoInfoId).collect(Collectors.toList());
                List<Map<String, Object>> handoverlistTons = tBusCargoInfoMapper.listHandoverlistTon(ids);
                List<Map<String, Object>> trustCargoTons = tBusCargoInfoMapper.listTrustCargoTon(ids);
                List<Map<String, Object>> weightGoodss = tBusCargoInfoMapper.listWeightGoodss(ids);

                page.forEach(v1 -> {
                    List<Map<String, Object>> _handoverlistTons = handoverlistTons.stream().filter(v2 -> v1.getCargoInfoId().equals(Long.valueOf(String.valueOf(v2.get("id"))))).collect(Collectors.toList());
                    Map<String, Object> trustCargoTon = trustCargoTons.stream().filter(v2 -> v1.getCargoInfoId().equals(Long.valueOf(String.valueOf(v2.get("id"))))).findFirst().orElse(Collections.emptyMap());
                    Map<String, Object> weightGoods = weightGoodss.stream().filter(v2 -> v1.getCargoInfoId().equals(Long.valueOf(String.valueOf(v2.get("id"))))).findFirst().orElse(Collections.emptyMap());
                    v1.setHandoverlistTon(_handoverlistTons.stream().filter(v2 -> !"卸船".equals(v1.getType()) || "卸".equals(v2.get("loadUnload"))).map(v2 -> new BigDecimal(String.valueOf(v2.get("handoverlistTon")))).reduce(BigDecimal.ZERO, BigDecimal::add));
                    v1.setTrustCargoTon(trustCargoTon.get("trustCargoTon") == null ? null : new BigDecimal(String.valueOf(trustCargoTon.get("trustCargoTon"))));
                    v1.setWeightGoods(weightGoods.get("weightGoods") == null ? null : new BigDecimal(String.valueOf(weightGoods.get("weightGoods"))));
                    v1.setBalanceTon(Optional.ofNullable(v1.getTon()).orElse(BigDecimal.ZERO).subtract(Optional.ofNullable(v1.getTrustCargoTon()).orElse(BigDecimal.ZERO)));
                    if (CommonEnum.YesNoMode.YES.getCode().equals(v1.getIsClear())) {
                        v1.setBalancePortStorageTon(BigDecimal.ZERO);
                    } else {
                        v1.setBalancePortStorageTon(Optional.ofNullable(v1.getHandoverlistTon()).orElse(BigDecimal.ZERO).subtract(Optional.ofNullable(v1.getWeightGoods()).orElse(BigDecimal.ZERO)).multiply("卸船".equals(v1.getType()) ? BigDecimal.ONE : BigDecimal.valueOf(-1)));
                    }
                });
            }
            return page;
        });
    }

    @Override
    public Pages<TBusCargoInfoDTO> getOrderCargoName(String billNo, String shipvoyageItemId, String cargoInfoNo,PageParameter pageParameter) {
        return PageHelperUtils.limit(pageParameter, () -> {
            Page<TBusCargoInfoDTO> page = tBusTrustMapper.getOrderCargoName(billNo,shipvoyageItemId,cargoInfoNo);
            if (!page.isEmpty()) {
                List<Long> ids = page.stream().map(TBusCargoInfoDTO::getCargoInfoId).collect(Collectors.toList());
                List<Map<String, Object>> handoverlistTons = tBusCargoInfoMapper.listHandoverlistTon(ids);
                List<Map<String, Object>> trustCargoTons = tBusCargoInfoMapper.listTrustCargoTon(ids);
                List<Map<String, Object>> weightGoodss = tBusCargoInfoMapper.listWeightGoodss(ids);
                page.forEach(v1 -> {
                    List<Map<String, Object>> _handoverlistTons = handoverlistTons.stream().filter(v2 -> v1.getCargoInfoId().equals(Long.valueOf(String.valueOf(v2.get("id"))))).collect(Collectors.toList());
                    Map<String, Object> trustCargoTon = trustCargoTons.stream().filter(v2 -> v1.getCargoInfoId().equals(Long.valueOf(String.valueOf(v2.get("id"))))).findFirst().orElse(Collections.emptyMap());
                    Map<String, Object> weightGoods = weightGoodss.stream().filter(v2 -> v1.getCargoInfoId().equals(Long.valueOf(String.valueOf(v2.get("id"))))).findFirst().orElse(Collections.emptyMap());
                    v1.setHandoverlistTon(_handoverlistTons.stream().filter(v2 -> !"卸船".equals(v1.getType()) || "卸".equals(v2.get("loadUnload"))).map(v2 -> new BigDecimal(String.valueOf(v2.get("handoverlistTon")))).reduce(BigDecimal.ZERO, BigDecimal::add));
                    v1.setTrustCargoTon(trustCargoTon.get("trustCargoTon") == null ? null : new BigDecimal(String.valueOf(trustCargoTon.get("trustCargoTon"))));
                    v1.setWeightGoods(weightGoods.get("weightGoods") == null ? null : new BigDecimal(String.valueOf(weightGoods.get("weightGoods"))));
                    v1.setBalanceTon(Optional.ofNullable(v1.getTon()).orElse(BigDecimal.ZERO).subtract(Optional.ofNullable(v1.getTrustCargoTon()).orElse(BigDecimal.ZERO)));
                    if (CommonEnum.YesNoMode.YES.getCode().equals(v1.getIsClear())) {
                        v1.setBalancePortStorageTon(BigDecimal.ZERO);
                    } else {
                        v1.setBalancePortStorageTon(Optional.ofNullable(v1.getHandoverlistTon()).orElse(BigDecimal.ZERO).subtract(Optional.ofNullable(v1.getWeightGoods()).orElse(BigDecimal.ZERO)).multiply("卸船".equals(v1.getType()) ? BigDecimal.ONE : BigDecimal.valueOf(-1)));
                    }
                });
            }
            return page;
        });
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void cancelRelease(Long trustId,String type) {
        TBusTrustDTO trust = tBusTrustMapper.getById(trustId);

   		//校验是否下发了昼夜计划
        List<TBusTrustCargoPO> trustCargoListByTrustId = tBusTrustMapper.getTrustCargoListByTrustId(trustId);
        if(!trustCargoListByTrustId.isEmpty()){
            List<Long> checkDayNightPlan = tBusTrustMapper.getDayNightByTbtcIds(trustCargoListByTrustId.stream().map(TBusTrustCargoPO::getId).distinct().collect(Collectors.toList()));
            if (!checkDayNightPlan.isEmpty()) {
                throw new BusinessRuntimeException("该计划已经下发昼夜计划，请先删除昼夜计划");
            }
        }

        if("卸船".equals(trust.getType()) || "装船".equals(trust.getType())){
            List<TBusCargoInfoPO> cargoInfoListByTrustId = tBusTrustCargoMapper.getCargoInfoListByTrustId(trustId);
            if(!cargoInfoListByTrustId.isEmpty()){
                //清除票货上的trustId
                if("卸船".equals(trust.getType())){
                    tBusTrustCargoMapper.updateXCCargoInfo(cargoInfoListByTrustId.stream().map(TBusCargoInfoPO::getId).distinct().collect(Collectors.toList()),
                            null,
                            securityUtils.getLoginUserId(),
                            securityUtils.getLoginUserName(),
                            new Date());
                }


                //删除票货关联航次表
                cargoInfoListByTrustId.forEach(o->{
                    tBusTrustCargoMapper.deleteCargoShipByCargoTrust(o.getId(),trustId);
                });
            }
        }

        if (trust == null) {
            throw new BusinessRuntimeException("指令不存在~");
        }

        if (!"杂项".equals(trust.getType())) {
            if (!BusTrustStatusEnum.YFB.getCode().equals(trust.getStatus())) {
                throw new BusinessRuntimeException("仅已发布状态指令可撤销发布~");
            }
        } else {
            if (Stream.of(BusTrustStatusEnum.YFB, BusTrustStatusEnum.ZYZ).map(BusTrustStatusEnum::getCode).noneMatch(v1 -> v1.equals(trust.getStatus()))) {
                throw new BusinessRuntimeException("杂项通知单仅已发布和作业中状态指令可撤销发布~");
            }
        }

        List<TBusTrustLocationPO> trustLocations = tBusTrustMapper.listTrustLocation(trustId);
        if (!trustLocations.isEmpty()) {
            throw new BusinessRuntimeException("该指令已做库场计划，无法撤销发布~");
        }
        if(type != null && type.equals("卸船")){
            int count = tBusTrustMapper.getHandoverlistByTrustId(trustId);
            if(count>1){
                throw new BusinessRuntimeException("该卸船指令中的票货已生成交接清单，无法撤销发布~");
            }
        }

        // 修改指令状态为待发布
        TBusTrustDTO dto = new TBusTrustDTO();
        dto.setId(trustId);
        dto.setStatus(BusTrustStatusEnum.DSH.getCode());
        tBusTrustMapper.updateStatus(dto);

        // 取消票货关联；删除票货（若为其创建且未被其他指令关联）
        List<MTrustTypePO> trustTypes = tBusTrustMapper.listTrustType();
        MTrustTypePO trustType = trustTypes.stream()
                .filter(v1 -> trust.getType().equals(v1.getTrustType()))
                .findFirst()
                .orElseThrow(null);
        if ("1".equals(trustType.getIsCreateCargo())) {
            List<TBusTrustCargoPO> trustCargos = tBusTrustMapper.listTrustCargo(trustId);
            List<Long> cargoInfoIds = trustCargos.stream().map(TBusTrustCargoPO::getCargoInfoId).collect(Collectors.toList());
            trustCargos = tBusTrustMapper.listTrustCargoByCargoInfoId(cargoInfoIds);
            if (trustCargos.size() != cargoInfoIds.size()) {
                throw new BusinessRuntimeException("创建的票货已被其他指令关联，无法撤销发布~");
            }
            List<Long> trustCargoIds = trustCargos.stream().map(TBusTrustCargoPO::getId).collect(Collectors.toList());
            tBusTrustMapper.cleanTrustCargo(trustCargoIds);
            tBusTrustMapper.deleteCargoInfo(cargoInfoIds);
        }

        List<TPrdWaterElectricityPO> waterElectricitys = tBusTrustMapper.listWaterElectricity(trustId);
        if (!waterElectricitys.isEmpty()) {
            throw new BusinessRuntimeException("当前通知单存在关联的加水节电记录，无法撤销发布~");
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void updateAfterRelease(TrustDTO dto) {
        TBusTrustPO trust = tBusTrustMapper.getTrust(dto.getId());
        if (!BusTrustStatusEnum.YFB.getCode().equals(trust.getStatus()) && !BusTrustStatusEnum.ZYZ.getCode().equals(trust.getStatus())) {
            throw new BusinessRuntimeException("不支持的通知单状态");
        }
//        List<TFdBankCustomerPrepaymentDTO> prepaymentDTO = tBusTrustMapper.getPrepayment(dto.getId());
//        Map<Long, List<TFdBankCustomerPrepaymentDTO>> map = prepaymentDTO.stream().collect(Collectors.groupingBy(TFdBankCustomerPrepaymentDTO::getCargoInfoId));
//        List<TrustCargoDTO> trustCargoDTOS = dto.getUpdates();
//        List<TBusCargoInfoDTO> cargoInfoDTOS = Lists.newArrayList();
//        for (TrustCargoDTO trustCargoDTO : trustCargoDTOS) {
//            List<TFdBankCustomerPrepaymentDTO> list = map.get(trustCargoDTO.getCargoInfoId());
//            BigDecimal sum = BigDecimal.ZERO;
//            if(CollectionUtils.isNotEmpty(list)){
//                for (TFdBankCustomerPrepaymentDTO tFdBankCustomerPrepaymentDTO : list) {
//                    sum = sum.add(tFdBankCustomerPrepaymentDTO.getPrepaymentAmount());
//                }
//            }
//            BigDecimal val = sum.subtract(trustCargoDTO.getEstAmount());
//            String isPrePay = "";
//            if(val.compareTo(BigDecimal.ZERO)<0){
//                if(sum.equals(BigDecimal.ZERO)){
//                    //未预缴
//                    isPrePay = "10";
//                }else{
//                    //部分预缴
//                    isPrePay = "20";
//                }
//            }else{
//                //完全预缴
//                isPrePay = "30";
//            }
//            TBusCargoInfoDTO cargoInfoDTO = new TBusCargoInfoDTO();
//            cargoInfoDTO.setId(trustCargoDTO.getCargoInfoId());
//            cargoInfoDTO.setCargoInfoId(trustCargoDTO.getCargoInfoId());
//            cargoInfoDTO.setIsPrePay(isPrePay);
//            tBusCargoInfoMapper.updateIsPrePay(cargoInfoDTO);
//        }
        if("卸船".equals(trust.getType())
                ||"集港".equals(trust.getType())
                ||"拆箱集港".equals(trust.getType())){
            updatePreAmountStatus(dto.getId());
        }
        // 处理指令量
        trust.setPlanQuantity(dto.getPlanQuantity());
        trust.setPlanTon(dto.getPlanTon());
        trust.setStartTime(dto.getStartTime());
        trust.setEndTime(dto.getEndTime());
        trust.setIsWeiqiaoPoundRemark(dto.getIsWeiqiaoPoundRemark());
        trust.setPreChangeShipName(dto.getPreChangeShipName());
        trust.setPreChangeShipNo(dto.getPreChangeShipNo());
        trust.setPoundRemark(dto.getPoundRemark());
        trust.setEstAmount(dto.getEstAmount());
        tBusTrustMapper.updateTrust(trust);

        // 更新航次子表转水前船名船号
        /*if ("1".equals(trust.getIsWeiqiaoPoundRemark())) {
            Matcher matcher = PATTERN.matcher(Optional.ofNullable(dto.getPoundRemark()).orElse(StringUtils.EMPTY));
            if (!matcher.matches()) {
                throw new BusinessRuntimeException("错误的魏桥磅单备注格式，应由空格拆分为四部分");
            }
            if (trust.getShipvoyageItemId() != null) {
                TDisShipvoyageItemPO shipvoyageItem = new TDisShipvoyageItemPO();
                shipvoyageItem.setId(trust.getShipvoyageItemId());
                Iterator<String> poundRemarkArr = Arrays.stream(trust.getPoundRemark().split(StringUtils.SPACE)).iterator();
                if (poundRemarkArr.hasNext()) {
                    shipvoyageItem.setPreChangeShipName(poundRemarkArr.next());
                }
                if (poundRemarkArr.hasNext()) {
                    shipvoyageItem.setPreChangeShipNo(poundRemarkArr.next());
                }
                tBusTrustMapper.updateShipvoyageItem(shipvoyageItem);
            }
        }*/

        // 附件
        sysFileService.saveFileBusRelation(dto.getFileIds(), dto.getId());

        List<MTrustTypePO> trustTypes = tBusTrustMapper.listTrustType();
        MTrustTypePO trustType = trustTypes.stream()
                .filter(v1 -> trust.getType().equals(v1.getTrustType()))
                .findFirst()
                .orElseThrow(null);

        if (CollectionUtils.isNotEmpty(dto.getUpdates())) {
            // 处理要求更新的指令票货
            List<TBusTrustCargoPO> trustCargos = tBusTrustMapper.listTrustCargo(dto.getId());
            dto.getUpdates().forEach(v1 -> {
                TBusTrustCargoPO oldTrustCargo = trustCargos.stream().filter(v2 -> v1.getId().equals(v2.getId())).findFirst().orElseThrow(null);
                v1.set_status(oldTrustCargo.getStatus());
                if (CommonEnum.YesNoMode.NO.getCode().equals(Optional.ofNullable(oldTrustCargo.getIsSecondWeigh()).orElse(CommonEnum.YesNoMode.NO.getCode()))) {
                    if (CommonEnum.YesNoMode.YES.getCode().equals(v1.getIsSecondWeigh())) {
                        v1.set_status(CommonEnum.YesNoMode.YES.getCode());
                    }
                }
            });
            tBusTrustMapper.updateTrustCargo(dto.getUpdates());
            if ("1".equals(trustType.getIsCreateCargo())) {
                // 处理要求更新的票货量
                dto.getUpdates()
                        .forEach(v1 -> {
                            TBusTrustCargoPO trustCargo = trustCargos.stream().filter(v2 -> v1.getId().equals(v2.getId())).findFirst().orElseThrow(null);
                            businessCommonService.updateSurplusBusCargoInfo(
                                    trustCargo.getCargoInfoId(),
                                    Optional.ofNullable(v1.getQuantity()).orElse(0L) - Optional.ofNullable(trustCargo.getQuantity()).orElse(0L),
                                    Optional.ofNullable(v1.getTon()).orElse(BigDecimal.ZERO).subtract(Optional.ofNullable(trustCargo.getTon()).orElse(BigDecimal.ZERO)),
                                    "+"
                            );
                        });
            }
        }

        //卸船交接清单校验量不能超过交接清单量
        if("卸船".equals(trust.getType())){
            List<Long> cargoInfoIds = dto.getUpdates().stream().map(o -> Optional.of(o.getCargoInfoId()).orElseThrow(() -> new BusinessRuntimeException("没有票货信息"))).distinct().collect(Collectors.toList());
           if(dto.getInserts()!=null&&dto.getInserts().size()>0){
               cargoInfoIds.addAll(dto.getInserts().stream().map(o -> Optional.of(o.getCargoInfoId()).orElseThrow(() -> new BusinessRuntimeException("没有票货信息"))).distinct().collect(Collectors.toList()));
           }

            List<TBusCargoInfoPO> cargoInfoList = tBusTrustCargoMapper.getcargoinfoList(cargoInfoIds);
            String trustCargoCheck = tBusTrustMapper.getPaymentControlSwitch(TRUST_CARGO_CHECK);

            cargoInfoList.forEach(o->{
                if(!o.getCompanyId().equals(trust.getCompanyId())){
                    throw new BusinessRuntimeException("票货的作业公司与通知单的作业公司不符<br/>"+o.getCargoInfoNo());
                }
                if("Y".equals(trustCargoCheck)){
                    if(CollectionUtils.isNotEmpty(dto.getUpdates())){
                        if(!o.getId().equals(dto.getUpdates().stream().filter(tmpV->o.getId().equals(tmpV.getCargoInfoId())).findFirst()
                                .orElseThrow(()->new BusinessRuntimeException("通知单发布后新增选择票货，货物信息校验信息获取失败")).getCargoInfoId())){
                            throw new BusinessRuntimeException("票货的货物信息与通知单的货物信息不符<br/>"+o.getCargoInfoNo());
                        }
                    }
                }
            });


            List<TBusHandoverListDTO> tmpHandoverList = tBusTrustCargoMapper.gethandoverListByCargoInfoId(cargoInfoIds);
            //判断是否超交接清单上的货物浮动量的上限
            BigDecimal minTon = tmpHandoverList.stream().map(o -> {
                return o.getTon().subtract(o.getTon().multiply(o.getFloatTon().divide(BigDecimal.valueOf(1000))));
            }).collect(Collectors.toList()).stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
            BigDecimal maxTon = tmpHandoverList.stream().map(o -> {
                return o.getTon().add(o.getTon().multiply(o.getFloatTon().divide(BigDecimal.valueOf(1000))));
            }).collect(Collectors.toList()).stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

            if(dto.getPlanTon().compareTo(maxTon)>0){
                throw  new BusinessRuntimeException("通知单计划量超过交接单上货物重量的浮动上限，重量浮动上限："+maxTon);
            }

        }

        if (CollectionUtils.isNotEmpty(dto.getInserts())) {
            // 处理要求新增的指令票货
            if (!"装船".equals(trust.getType())) {
                throw new BusinessRuntimeException("仅装船通知单支持发布后新增货物信息");
            }
            List<TBusTrustCargoDTO> tmpCargoInfoList = new ArrayList<>();
            for (int i = 0; i < dto.getInserts().size(); i++) {
                TBusTrustCargoDTO cargo = dto.getInserts().get(i);
                cargo.setId(snowflake.nextId());
                cargo.setTrustId(dto.getId());
                MCargoDTO mCargoDTO = mCargoTypeMapper.getCargoByCargoCode(cargo.getCargoCode());
                cargo.setCargoName(mCargoDTO.getCargoName());
                String nextBusinessNo = commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.BUSINESS_NO, trust.getTrustNo() + "_");
                cargo.setBusinessNo(nextBusinessNo);
                if (ArrayUtils.isNotEmpty(cargo.getHatchArr())) {
                    String hatchs = String.join(",", cargo.getHatchArr());
                    cargo.setHatchs(hatchs);
                }
                if (CommonEnum.YesNoMode.YES.getCode().equals(cargo.getIsSecondWeigh())) {
                    cargo.setStatus(CommonEnum.YesNoMode.YES.getCode());
                } else {
                    cargo.setStatus(CommonEnum.YesNoMode.NO.getCode());
                }
                tBusTrustCargoMapper.insert(cargo);

                //为 装船通知单新增票货时保存票货和船名航次信息 做票货数据支持
                tmpCargoInfoList.add(cargo);
            }

            //写票货航次表
            if(!tmpCargoInfoList.isEmpty()){
                List<Long> collect = tmpCargoInfoList.stream().map(TBusTrustCargoDTO::getCargoInfoId).collect(Collectors.toList());
                if(!collect.isEmpty()){
                    collect.forEach(o->{
                        //好像是为了防止连点重复审核了
                        List<TBusCargoShipDTO> tmpCargoShipList = tBusTrustCargoMapper.getCargoShipWithTrust(trust.getId(),o,trust.getShipvoyageItemId());
                        if(tmpCargoShipList.isEmpty()){
                            //删除 cargoInfoId和trustId 对应的carogShip表记录
                            tBusTrustCargoMapper.deleteCargoShipByCargoTrust(o,trust.getId());

                            TBusCargoShipDTO tBusCargoShipDTO = new TBusCargoShipDTO();
                            tBusCargoShipDTO.setId(snowflake.nextId());
                            tBusCargoShipDTO.setType("1");
                            tBusCargoShipDTO.setTrustId(trust.getId());
                            tBusCargoShipDTO.setShipName(trust.getShipName());
                            tBusCargoShipDTO.setShipVoyageId(trust.getShipvoyageId());
                            tBusCargoShipDTO.setShipVoyageItemId(trust.getShipvoyageItemId());
                            tBusCargoShipDTO.setVoyage(trust.getVoyage());
                            tBusCargoShipDTO.setCargoInfoId(o);
                            tBusTrustCargoMapper.insertCargoShip(tBusCargoShipDTO);
                        }
                    });
                }
            }

        }
        if (CollectionUtils.isNotEmpty(dto.getInserts())) {
            if("装船".equals(trust.getType())) {
                for(TBusTrustCargoDTO trustCargoDto:dto.getInserts()){
                        SyncDTO syncCargoDto = new SyncDTO();
                        syncCargoDto.setId(snowflake.nextId());
                        syncCargoDto.setBizId(trustCargoDto.getId());
//                        syncCargoDto.setBizType(BusSyncEnum.LOAD_UNLOAD_LIST.getCode());
                        syncCargoDto.setIsDelete("0");
                     //   SpringUtils.getBean(BusSyncServiceImpl.class).insertBatch(syncCargoDto);
                }
            }
        }

        String paymentControlSwitch = tBusTrustMapper.getPaymentControlSwitch(PAYMENT_CONTROL_SWITCH);
        if("Y".equals(paymentControlSwitch)){
            //校验是否预缴
            if(Stream.of("陆销", "疏港", "装船").anyMatch(v1 -> v1.equals(trust.getType()))){
                //获取该指令是否存在未预缴的票货信息
                List<Map<String,Object>> checkList = tBusTrustMapper.getPrePayInfoByTrustId(trust.getId());
                if(checkList.isEmpty()){
                    //throw new BusinessRuntimeException("没有获取到票货信息");
                }else {
                    List<Map<String,Object>> payTypeCheckList = tBusTrustMapper.getPrePayTypeByTrustId(checkList.stream().map(o->Long.valueOf(String.valueOf(o.get("cargoInfoId")))).collect(Collectors.toList()));
                    Map<Long, String> collect = payTypeCheckList.stream().collect(Collectors.toMap(o -> Long.valueOf(String.valueOf(o.get("cargoInfoId"))), o -> String.valueOf(o.get("payType")), (k1, k2) -> k1));

                    checkList.forEach(tmpCheck-> {
                        System.out.println(tmpCheck.get("isRelease").toString() + "_" + tmpCheck.get("isPrePay")+
                                "_"+tmpCheck.get("cargoInfoId")+"_"+collect.get(Long.valueOf(String.valueOf(tmpCheck.get("cargoInfoId")))));
                        //只有合同上是预付费的才做校验
                        if("10".equals(collect.get(Long.valueOf(String.valueOf(tmpCheck.get("cargoInfoId")))))){
                            if(!"1".equals(String.valueOf( tmpCheck.get("isRelease")))
                                    &&!"30".equals(String.valueOf(tmpCheck.get("isPrePay")))){
                                throw new BusinessRuntimeException(String.valueOf(tmpCheck.get("cargoInfoNo"))+"没有预缴");
                            }
                        }
                    });
                }

            }
        }

        verifyOverflow(dto.getId());
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void updateConsigner(TrustDTO dto) {
            // 更新委托人
            if (CollectionUtils.isNotEmpty(dto.getUpdates())) {
                Long planQuantity = 0L;
                BigDecimal planTon = new BigDecimal("0.00");
                for (TrustCargoDTO item: dto.getUpdates()) {
                    if (item.getConsignerId() == null) {
                        item.setConsignerName(null);
                    }
                    item.setType(dto.getType());
                    tBusTrustCargoMapper.updateConsigner(item);
                    if (item.getQuantity() != null) {
                        planQuantity += item.getQuantity();
                    }
                    if (item.getTon() != null) {
                        planTon = planTon.add(item.getTon());
                    }
                }
                if ("疏港".equals(dto.getType())) { // 疏港通知单可以修改计划件数重量，同步修改通知单的计划量
                    TBusTrustDTO tBusTrustDTO = new TBusTrustDTO();
                    tBusTrustDTO.setId(dto.getId());
                    tBusTrustDTO.setPlanQuantity(planQuantity);
                    tBusTrustDTO.setPlanTon(planTon);
                    tBusTrustMapper.updateQuantityTon(tBusTrustDTO);
                }
                sysFileService.saveFileBusRelation(dto.getFileIds(),dto.getId());
            }
    }

    @Override
    public List<Map<String, Object>> listContract(Long cargoOwnerId, String cargoCode, String tradeType) {
        return tBusTrustMapper.listContract(cargoOwnerId, cargoCode, tradeType);
    }
    @Override
    public Map<String, Object> getPreferentialRate(Long contractId, String contractName,String cargoCode) {
        Map<String, Object> result = Maps.newHashMap();
        Map<String, Object> preferentialRate = tBusTrustMapper.getPreferentialRate(contractId,contractName,cargoCode);
        List<Map<String, Object>> tieredRate = tBusTrustMapper.getPreferentialTieredRate(contractId,contractName,cargoCode);
        Map<String, Object> stepAccumulation = tBusTrustMapper.getStepAccumulation(contractId,contractName,cargoCode);
        result.put("preferentialRate",preferentialRate);
        result.put("tieredRate",tieredRate);
        result.put("stepAccumulation",stepAccumulation);
        result.put("isPreferential",(CollectionUtil.isEmpty(preferentialRate)&&CollectionUtil.isEmpty(tieredRate))?0:1);
        result.put("isTieredRate",(CollectionUtil.isEmpty(preferentialRate)&&!CollectionUtil.isEmpty(tieredRate))?1:0);

        return result;
    }

    @Override
    public List<MTrustTypePO> listTrustType() {
        return tBusTrustMapper.listTrustType();
    }

    @Override
    public List<Map<String, Object>> listShipvoyageItemFile(Long id) {
        return tBusTrustMapper.listShipvoyageItemFile(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean kcjhCancelAudit(Long id) {
        ArrayList<Long> longs = new ArrayList<>();
        longs.add(id);
        List<TBusTrustLocationDTO> listByTrustIds = tBusTrustLocationMapper.getListByTrustIds(longs);
        if(CollectionUtils.isEmpty(listByTrustIds)){
            throw new BusinessRuntimeException("没有安排场地，无需销审");
        }
        int countNumber =  tBusTrustMapper.getVehicleReservationByTrustId(id);
        if(countNumber>0){
            throw new BusinessRuntimeException("已派车不能撤销审核");
        }
        TBusTrustLocationDTO trustLocationDTO = new TBusTrustLocationDTO();
        trustLocationDTO.setTrustId(id);
        return tBusTrustLocationMapper.deleteByCondition(trustLocationDTO) >= 1;
    }

    /**
     * 校验出港量是否超出
     *
     * @param id
     */
    private void verifyOverflow(Long id) {
        //获取系统参数:VERIFY_OVERFLOW
        String verifyOverflow = tBusTrustMapper.getVerifyOverflow();
        boolean bool = CommonEnum.YesNoMode.YES.getCode().equals(verifyOverflow);
        if (!bool) {
            return;
        }

        TBusTrustPO trust = tBusTrustMapper.getById(id);
        bool = Stream.of("疏港", "装船").noneMatch(v1 -> v1.equals(trust.getType()));
        if (bool) {
            return;
        }

        List<TBusTrustCargoDTO> trustCargos = tBusTrustCargoMapper.getList(id);
        List<Long> cargoInfoIds = trustCargos.stream().map(TBusTrustCargoPO::getCargoInfoId).collect(Collectors.toList());
        List<TBusCargoInfoDTO> cargoInfos = tBusTrustMapper.listCargoInfo(cargoInfoIds);
        cargoInfos = cargoInfos.stream().filter(v1 -> ("10".equals(v1.getSource())) && StringUtils.isBlank(v1.getIsTos())).collect(Collectors.toList());
        if (cargoInfos.isEmpty()) {
            return;
        }
        List<Long> trustIds = cargoInfos.stream().map(TBusCargoInfoPO::getTrustId).collect(Collectors.toList());
        List<TBusTrustPO> trusts = tBusTrustMapper.listTrust(trustIds);
        cargoInfos = cargoInfos.stream()
                .filter(v1 -> "卸船".equals(trusts.stream().filter(v2 -> Optional.ofNullable(v1.getTrustId()).orElseThrow(()->new BusinessRuntimeException("票货"+v1.getCargoInfoNo()+"</br>缺失通知单编号")).equals(v2.getId())).findFirst().orElseThrow(()->new BusinessRuntimeException("校验超量过程没有筛选出票货")).getType()))
                .collect(Collectors.toList());
        if (cargoInfos.isEmpty()) {
            return;
        }

        cargoInfoIds = cargoInfos.stream().map(TBusCargoInfoPO::getId).collect(Collectors.toList());
        List<TBusHandoverlistPO> handoverlists = tBusTrustMapper.listHandoverlist(cargoInfoIds);

        List<TBusTrustCargoPO> _trustCargos = tBusTrustMapper.listTrustCargoByCargoInfoId2(cargoInfoIds);

        cargoInfos.forEach(v1 -> {
            BigDecimal weightLimit = Optional.ofNullable(handoverlists.stream().filter(v2 -> v1.getId().equals(v2.getCargoInfoId())).findFirst().orElse(new TBusHandoverlistPO()).getTon()).orElse(BigDecimal.ZERO);
            BigDecimal floatTon = BigDecimal.valueOf(v1.getFloatTon()).divide(BigDecimal.valueOf(1000)).multiply(weightLimit);
            BigDecimal weight = _trustCargos.stream().filter(v2 -> v1.getId().equals(v2.getCargoInfoId())).map(TBusTrustCargoPO::getTon).reduce(BigDecimal.ZERO, BigDecimal::add);
            if (weightLimit.add(floatTon).compareTo(weight) < 0) {
                throw new BusinessRuntimeException(String.format("票货【%s】疏港/装船量【%s】超出卸船交接清单量【%s(包含%s浮动量)】", v1.getCargoInfoNo(), weight, weightLimit.add(floatTon), floatTon));
            }
        });
    }

    //导出费用明细
    @Override
    public TrustFeeExportDTO exportFeeEvent(Long id) {
        TrustFeeExportDTO trustFeeExportDTO = new TrustFeeExportDTO();
        trustFeeExportDTO.setSheetName("Sheet1");
        TrustCargoDTO trustCargoDTO = new TrustCargoDTO();
        trustCargoDTO.setId(id);
        TBusTrustDTO dto = tBusTrustMapper.getTrustCargoInfo(trustCargoDTO);

        trustFeeExportDTO.setCargoOwnerName(dto.getCargoOwnerName());
        trustFeeExportDTO.setShipNameVoyage(dto.getShipNameVoyage());
        trustFeeExportDTO.setShipName(dto.getShipName());
        trustFeeExportDTO.setCargoName(dto.getCargoName());
        trustFeeExportDTO.setRate(new BigDecimal(dto.getDisRate()));
        trustFeeExportDTO.setEstAmount(dto.getEstAmount());
        trustFeeExportDTO.setPlanTon(dto.getPlanTon());
        trustFeeExportDTO.setCostInfo(dto.getPlanTon()+"*"+trustFeeExportDTO.getRate()+"*");
        if(StringUtils.isNotBlank(dto.getShipNameVoyage())){
            if(dto.getShipNameVoyage().split(",").length>1){
                trustFeeExportDTO.setShipNameVoyage("");
                trustFeeExportDTO.setShipName("");
            }
        }
        return trustFeeExportDTO;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public boolean isStopStatus(TBusTrustCargoDTO tBusTrustCargoDTO) {

        if(tBusTrustCargoDTO.getIsStop().equals("10")){
            TBusCargoInfoDTO dto = tBusTrustMapper.getisLogoutByCargoInfoId(tBusTrustCargoDTO.getCargoInfoId());
            if(dto.getIsClear()!=null && dto.getIsClear().equals("1")){
                throw new BusinessRuntimeException("该票货已完货无法开启");
            }
            if(dto.getIsLogout()!=null && dto.getIsLogout().equals("10")){
                throw new BusinessRuntimeException("该票货已作废无法开启");
            }
        }

        TrustStopLogRes insertDto = this.createTrustStopLogDto(tBusTrustCargoDTO.getId(),
                tBusTrustCargoDTO.getIsStop(),null,tBusTrustCargoDTO.getStopRemark(),"trust");
        if(!ObjectUtil.isEmpty(insertDto)){
            tBusTrustMapper.insertStopLog(Collections.singletonList(insertDto));
        }

        return tBusTrustMapper.isStopStatus(tBusTrustCargoDTO) == 1;
    }

    public TrustStopLogRes createTrustStopLogDto(Long trustCargoId,String status,String remark,String stopRemark,String serviceName){

        TrustStopLogRes insertDto = new TrustStopLogRes();
        insertDto.setId(snowflake.nextId());
        insertDto.setCreateBy("dsrewu".equals(serviceName)?1L:securityUtils.getLoginUserId());
        insertDto.setCreateByName("dsrewu".equals(serviceName)?"超级管理员":securityUtils.getLoginUserName());
        insertDto.setCreateTime(new Date());
        insertDto.setTrustCargoId(trustCargoId);
        insertDto.setStopRemark(stopRemark);
        if(statusEnum._10.getCode().equals(status)){
            //开启
            if (StringUtils.isNotBlank(remark)){
                insertDto.setRemark(remark);

            }else{
                insertDto.setRemark("计划开启");

            }
        }else if(statusEnum._20.getCode().equals(status)){
            //结束
            if (StringUtils.isNotBlank(remark)){
                insertDto.setRemark(remark);

            }else{
                insertDto.setRemark("计划关闭");

            }
        }else{
            insertDto.setRemark("其他状态"+status);
        }
        return insertDto;
    }

    @Override
    public TBusTrustDTO getTrustCargoById(Long id) {
        TBusTrustDTO dto = tBusTrustMapper.getById(id);
        boolean isOrderByCreateTime = "装船".equals(dto.getType());
        List<TBusTrustCargoDTO> cargoList = tBusTrustCargoMapper.getSignList(id, isOrderByCreateTime);
        cargoList.forEach(o->{
            o.setTrustType(dto.getType());
            BigDecimal tonCount = tBusTrustCargoMapper.getCount(o.getBusinessNo());
            if(tonCount!=null && o.getTon()!=null && tonCount.compareTo(o.getTon())>=0){
                o.setTonFlag("1");
            }else{
                o.setTonFlag("2");
            }
        });
        dto.setCargoList(cargoList);
        cargoList.forEach(v1 -> {
            if (StringUtils.isNotBlank(v1.getHatchs())) {
                String[] hatchNumArr = Arrays.stream(v1.getHatchs().split(",")).toArray(String[]::new);
                v1.setHatchArr(hatchNumArr);
            } else {
                v1.setHatchArr(new String[0]);
            }
        });
        if (dto.getShipvoyageItemId() != null) {
            dto.setShipInfo(tBusTrustCargoMapper.getShipInfo(dto.getShipvoyageItemId()));
        }
        return dto;
    }

    @Override
    public TBusTrustDTO getDetailAdd(Long id) {
        TBusTrustDTO dto = null;
        try {
            dto = tBusTrustMapper.getDetailAdd(id);
        } catch (Exception e) {
            // 处理异常，例如记录日志或抛出运行时异常
            e.printStackTrace();
        }
        return dto;
    }

    @Override
    public Pages<TrustStopLogRes> getStopLogList(Long trustCargoId, Long cargoInfoId, Long trustId) {
        TrustStopLogReq searchDTO = new TrustStopLogReq();
        searchDTO.setTrustId(trustId);
        searchDTO.setCargoInfoId(cargoInfoId);
        searchDTO.setTrustCargoId(trustCargoId);
        return  PageHelperUtils.limit(searchDTO, () -> {
            return tBusTrustMapper.getStopLogList(searchDTO);
        });

    }
}

