package com.yy.ppm.produce.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.google.api.client.util.Lists;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.DateUtils;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.common.util.SpringUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.enums.SalaryStatusEnum;
import com.yy.ppm.common.enums.WorkTicketStatusEnum;
import com.yy.ppm.common.service.BusinessCommonService;
import com.yy.ppm.produce.bean.dto.TicketTonDTO;
import com.yy.ppm.produce.bean.dto.workTicket.TPrdWorkTicketDTO;
import com.yy.ppm.produce.bean.dto.workTicket.TPrdWorkTicketDetailDTO;
import com.yy.ppm.produce.bean.po.TPrdPortStorageDetailPO;
import com.yy.ppm.produce.bean.po.TPrdSalaryPO;
import com.yy.ppm.produce.bean.po.TPrdWorkPlanPO;
import com.yy.ppm.produce.mapper.TPrdShipAdjustMapper;
import com.yy.ppm.produce.mapper.TPrdWorkTicketMapper;
import com.yy.ppm.produce.service.TPrdShipAdjustService;
import com.yy.ppm.produce.service.TPrdWorkTicketService;
import com.yy.ppm.statement.bean.dto.busHandoverlist.TBusHandoverlistDTO;
import com.yy.ppm.system.bean.dto.SysDeptDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Auther chenfs
 * @Description
 * @Date 2023-10-16 14:03
 */
@Service
public class TPrdShipAdjustServiceImpl implements TPrdShipAdjustService {

    @Autowired
    private TPrdShipAdjustMapper tPrdShipAdjustMapper;

    @Autowired
    private TPrdWorkTicketMapper tPrdWorkTicketMapper;

    @Resource
    private SecurityUtils securityUtils;

    @Autowired
    private BusinessCommonService businessCommonService;

    @Autowired
    private Snowflake snowflake;


    @Override
    public Pages<TBusHandoverlistDTO> list(Long shipvoyageItemId,String shipName, String voyage, PageParameter parameter) {
        return PageHelperUtils.limit(parameter, () -> {
            return tPrdShipAdjustMapper.list(shipvoyageItemId,shipName,voyage);
        });
    }

    @Override
    public List<TPrdWorkTicketDetailDTO> listTicket(Long shipvoyageItemId) {
        if (shipvoyageItemId == null) {
            throw new BusinessRuntimeException("无效的ID");
        }
        return tPrdShipAdjustMapper.listTicket(shipvoyageItemId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    @Override
    public void updateTon(TicketTonDTO ticketTonDTO) {
        List<TPrdWorkTicketDetailDTO> ticketList = ticketTonDTO.getTicketList();
        if (ticketList != null && ticketList.size() != 0) {
            //修改作业票的吨数
            tPrdShipAdjustMapper.updateTon(ticketList);
            for (TPrdWorkTicketDetailDTO dto : ticketList) {
                //修改计件工资吨数
                //根据作业票子表ID查询计件工资列表
                List<TPrdSalaryPO> listSalary = tPrdShipAdjustMapper.getSalaryList(dto.getId());
                if (listSalary != null && listSalary.size() != 0) {
                    for (TPrdSalaryPO pos : listSalary) {
                        if (!SalaryStatusEnum._10.getCode().equals(pos.getSalaryStatusCode())) {
                            throw new BusinessRuntimeException(dto.getProcessDetailName() + "作业过程的作业票的计件工资已审核,不能进行调整");
                        }
                        pos.setTon(pos.getTon().multiply(ticketTonDTO.getPercentage()));
                    }
                    //修改计件信息
                    tPrdShipAdjustMapper.updateSalaryList(listSalary);
                }

                //修改港存吨数
                //根据作业票子表ID查询港存明细信息
                List<TPrdPortStorageDetailPO> listStorage = tPrdShipAdjustMapper.getStorageDetailList(dto.getId());
                if (listStorage != null && listStorage.size() != 0) {
                    for (TPrdPortStorageDetailPO tPrdPortStorageDTO : listStorage) {
                        tPrdPortStorageDTO.setTon(dto.getTon());
                    }
                    businessCommonService.insertPortStorageDetail(listStorage);
                }

            }

        } else {
            throw new BusinessRuntimeException("请至少选中一行数据");
        }
    }

    @Override
    public Map<String, Object> getTicket(Long shipvoyageItemId) {
        List<TPrdWorkTicketDetailDTO> dataList = tPrdShipAdjustMapper.getTicketByShipVoyageItemID(shipvoyageItemId);
        List<TPrdWorkTicketDetailDTO> result = Lists.newArrayList();

        for (TPrdWorkTicketDetailDTO tPrdWorkTicketDetailDTO : dataList) {
            SysDeptDTO deptLevel2 = tPrdShipAdjustMapper.getDeptLevel2ByDeptId(tPrdWorkTicketDetailDTO.getDeptId());
            if (deptLevel2 == null) {
                throw new BusinessRuntimeException(deptLevel2.getDeptName() + "未找到部门信息，请联系管理员");
            }
            if ("1".equals(deptLevel2.getIsMachine()) && !deptLevel2.getDeptName().contains("固机队")) {
                result.add(tPrdWorkTicketDetailDTO);
            }
        }
        Map<String, Object> objectObjectHashMap = new HashMap<>();


        if (!CollectionUtils.isEmpty(result)) {
            objectObjectHashMap.put("result", result);
            for (TPrdWorkTicketDetailDTO o : result) {
                Integer count = tPrdWorkTicketMapper.getSalaryEx(o.getTpwtId());
                if (count > 0) {
                    objectObjectHashMap.put("errorMsg", DateUtils.formatDate(o.getWorkDate(), "yyyy-MM-dd") + o.getClassName() + o.getDeptName() + "该作业计划计件已审核,请先取消计件审核");
                    break;
//                    throw new BusinessRuntimeException("该作业计划计件已审核,请先取消计件审核");
                }
                if ("//".equals(o.getSrms())) {
                    o.setSrms("");
                }
                if ("//".equals(o.getSrmt())) {
                    o.setSrmt("");
                }
                if ("/".equals(o.getShipVoyageName())) {
                    o.setShipVoyageName("");
                }
            }

        }
        return objectObjectHashMap;
    }

    @Autowired
    private TPrdWorkTicketService tPrdWorkTicketService;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public boolean updateWorkTicket(List<TPrdWorkTicketDetailDTO> dtoList) {
        //整船调整
        if (CollectionUtils.isEmpty(dtoList)) {
            throw new BusinessRuntimeException("请选择调账信息之后再进行保存操作");
        }
        dtoList.forEach(o -> {
            if (o.getId() == null) {
                throw new BusinessRuntimeException("选择的数据中缺乏作业票详细信息");
            }
            if (o.getTpwtId() == null) {
                throw new BusinessRuntimeException("选择的数据中缺乏作业票信息");
            }
        });
        for (TPrdWorkTicketDetailDTO dto : dtoList) {
            //更新作业票子表
            tPrdShipAdjustMapper.updateWorkTicketBatch(Arrays.asList(dto));
        }
        // 使用流操作获取并去除重复的 TpwtId
        List<Long> uniqueTpwtIds = dtoList.stream()
                .map(TPrdWorkTicketDetailDTO::getTpwtId) // 获取每个对象的 TpwtId
                .distinct() // 去除重复的 TpwtId
                .collect(Collectors.toList()); // 将结果收集到列表中
        for (Long id : uniqueTpwtIds) {
            Long planId = tPrdShipAdjustMapper.getWorkPlanId(id);
            TPrdWorkPlanPO workPlan = tPrdWorkTicketMapper.getWorkPlan(planId);
            List<String> processIsTally = tPrdWorkTicketService.getProcessIsTally(workPlan.getProcessCode(), "2", workPlan.getCargoCode(), workPlan.getId());
            //作业票子表详情
            BigDecimal zero = BigDecimal.ZERO;
            BigDecimal ton = BigDecimal.ZERO;
            Integer quantity = 0;

            //根据作业票主表id 统计改作业票子表中的件数合，吨数合 后面用来更新主表
            List<TPrdWorkTicketDetailDTO> workTicketDetails = tPrdWorkTicketMapper.listWorkTicketDetail(id);
            for (TPrdWorkTicketDetailDTO dto : workTicketDetails) {
                for (String existingItem : processIsTally) {
                    if (existingItem.equals(dto.getProcessDetailCode())) {
                        if (dto.getTon().compareTo(zero) > 0) {
                            ton = ton.add(dto.getTon());
                        }
                        if (dto.getQuantity()!=null && dto.getQuantity() > 0) {
                            quantity += dto.getQuantity();
                        }
                        break;
                    }
                }
            }
            TPrdWorkTicketDetailDTO tmpDetail = new TPrdWorkTicketDetailDTO();
            tmpDetail.setWorkTicketId(id);
            tmpDetail.setTon(ton);
            if (quantity != null && quantity > 0) {
                tmpDetail.setQuantity(quantity);
            }
            //更新作业票主表
            int count = tPrdShipAdjustMapper.updateTicket(tmpDetail);
            if (count != 1) {
                throw new BusinessRuntimeException("更新作业票主表失败");
            }
        }

        //获取主表数据
        List<Long> tickerIds = dtoList.stream().map(TPrdWorkTicketDetailDTO::getTpwtId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(tickerIds)) {
            throw new BusinessRuntimeException("未统计出作业票主表集合");
        }
        List<TPrdWorkTicketDTO> ticketByIds = tPrdShipAdjustMapper.getTicketByIds(tickerIds);
        //主表集合转map <id,dto>
        Map<Long, List<TPrdWorkTicketDTO>> ticketMap = ticketByIds.stream().collect(Collectors.groupingBy(TPrdWorkTicketDTO::getId));
        if (CollectionUtils.isEmpty(ticketMap)) {
            throw new BusinessRuntimeException("未查询到作业票主表信息");
        }

        //前端传来的子表id集合，并获取数据库中子表数据  前端选中的会进行调整
        List<Long> detailList = dtoList.stream().map(TPrdWorkTicketDetailDTO::getId).collect(Collectors.toList());
        List<TPrdWorkTicketDetailDTO> ticketDetails = tPrdShipAdjustMapper.getTicketDetailByDetailIds(detailList);
        Map<Long, List<TPrdWorkTicketDetailDTO>> tmpTicketMap = ticketDetails.stream().collect(Collectors.groupingBy(TPrdWorkTicketDetailDTO::getWorkTicketId));
        if (CollectionUtils.isEmpty(tmpTicketMap)) {
            throw new BusinessRuntimeException("作业票子表中未收集到对应的主表信息");
        }

        tmpTicketMap.forEach((k, v) -> {
            if (CollectionUtils.isEmpty(ticketMap.get(k))) {
                throw new BusinessRuntimeException("没有查询到作业票信息");
            }
            TPrdWorkTicketDTO tPrdWorkTicketDTO = ticketMap.get(k).get(0);
            if (WorkTicketStatusEnum._20.getCode().equals(tPrdWorkTicketDTO.getWorkTicketStatus())) {
                SpringUtils.getBean(TPrdWorkTicketServiceImpl.class).commWorkTicketOperate(ticketMap.get(k).get(0), v, true, false, "整船调整");
            }
        });
        return true;
    }
}
