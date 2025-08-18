package com.yy.ppm.finance.controller;


import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.finance.bean.dto.FFeeItemDTO;
import com.yy.ppm.finance.bean.dto.FFeeItemSearchDTO;
import com.yy.ppm.finance.service.FFeeItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.yy.common.util.str.StringUtil.getString;

/**
 * (FFeeItem)表控制层
 *
 * @author 韩旭
 * @date 2021-03-29 11:11:12
 */
@RestController
@RequestMapping(value = "/api/external/freeItem")
@Validated
public class FFeeItemController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(FFeeItemController.class);
    /**
     * 服务对象
     */
    @Autowired
    private FFeeItemService fFeeItemService;

    @Autowired
    private CommonService commonService;

    /**
     * 根据实体类筛选数据列表
     *
     * @param fFeeItemSearchDTO 查询类
     * @return 统一数据封装
     */
    @GetMapping("/getlist")
    @PreAuthorize("hasAuthority('master:feeItem:query')")
    public Map<String, Object> getList(FFeeItemSearchDTO fFeeItemSearchDTO) {

        Pages<FFeeItemDTO> fFeeItemList = fFeeItemService.getList(fFeeItemSearchDTO);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(fFeeItemList);
    }

    /**
     * 根据id获取
     *
     * @return
     */
    @GetMapping("/getbyid/{id}")
    @PreAuthorize("hasAuthority('master:feeItem:query')")
    public Map<String, Object> getById(@PathVariable("id") Long id) {
        final String methodName = "getById";

        FFeeItemDTO fFeeItemDTO = fFeeItemService.getById(id);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(fFeeItemDTO);
    }

    /**
     * 新增
     *
     * @param fFeeItemDTO
     * @return
     */
    @PostMapping("/insert")
    @PreAuthorize("hasAuthority('master:feeItem:add')")
    public Map<String, Object> insert(@RequestBody FFeeItemDTO fFeeItemDTO) {
        final String methodName = "insert";
        LOGGER.enter("FFeeItemController:" + methodName + "[start]", "fFeeItemDTO:" + fFeeItemDTO);

        // 验证费目编号是否重复
        commonService.isRepeate("m_fee_item", "ITEM_CD", fFeeItemDTO.getItemCd() + "", getString(fFeeItemDTO.getId()), "费目编号", null);
        //验证费目名称是否重复
        commonService.isRepeate("m_fee_item", "ITEM_NM", fFeeItemDTO.getItemNm() + "", getString(fFeeItemDTO.getId()), "费目名称", null);

        //获取排序号
        if (fFeeItemDTO.getSortNum() == null) {
            fFeeItemDTO.setSortNum(commonService.getNextValue("m_fee_item", "sort_num", null));
        }

        int count = fFeeItemService.save(fFeeItemDTO);

        LOGGER.exit("FFeeItemController:" + methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().out("新增成功").toResult(count);
    }

    /**
     * 修改
     *
     * @param fFeeItemDTO
     * @return
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('master:feeItem:update')")
    public Map<String, Object> update(@RequestBody FFeeItemDTO fFeeItemDTO) {

        final String methodName = "update";
        LOGGER.enter("FFeeItemController:" + methodName + "[start]", "fFeeItemDTO:" + fFeeItemDTO);

        // 验证费目编号是否重复
        commonService.isRepeate("m_fee_item", "ITEM_CD", fFeeItemDTO.getItemCd() + "", getString(fFeeItemDTO.getId()), "费目编号", null);
        //验证费目名称是否重复
        commonService.isRepeate("m_fee_item", "ITEM_NM", fFeeItemDTO.getItemNm() + "", getString(fFeeItemDTO.getId()), "费目名称", null);

        int count = fFeeItemService.save(fFeeItemDTO);

        LOGGER.exit("FFeeItemController:" + methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().out("修改成功").toResult(count);
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @DeleteMapping("/deletebyid/{id}")
    @PreAuthorize("hasAuthority('master:feeItem:delete')")
    public Map<String, Object> deletebyid(@PathVariable("id") String id) {
        final String methodName = "deletebyid";
        LOGGER.enter("FFeeItemController:" + methodName + "[start]", "id:" + id);

        int count = commonService.delete("m_fee_item", "id", id);

        LOGGER.exit("FFeeItemController:" + methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().out("删除成功").toResult(count);
    }
}