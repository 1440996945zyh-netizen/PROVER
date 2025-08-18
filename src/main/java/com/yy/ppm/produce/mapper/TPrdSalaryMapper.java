package com.yy.ppm.produce.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.master.bean.dto.MPiecePriceDTO;
import com.yy.ppm.produce.bean.dto.TPrdSalaryResultDTO;
import com.yy.ppm.produce.bean.dto.salary.*;
import com.yy.ppm.produce.bean.po.TPrdSalaryPO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.cursor.Cursor;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-04 17:03
 */
public interface TPrdSalaryMapper {

    Page<TPrdSalaryResultDTO> listSalary(SalaryQueryDTO query);

    Map<String,String> getSalarySumNew(SalaryQueryDTO query);

    Cursor<TPrdSalaryDTO> cursorListSalary(SalaryQueryDTO query);

    List<TPrdSalaryGroupByProcessDTO> listSalaryGroupByProcess(SalaryQueryDTO query);

    Cursor<TPrdSalaryExcelDTO> cursorListSalary2(SalaryQueryDTO query);

    TPrdSalaryPO getSalartById(Long id);


    void examine(@Param("list") List<TPrdSalaryPO> list);

    void examineHr(SalaryQueryExamineDTO dto);

    List<Map<String,Object>> getPrice(TPrdSalaryPO salartById);

    List<TPrdSalaryPO> getSalarySum(SalaryQueryExamineDTO dto);

    @Edit
    void insertSum(List<TPrdSalaryPO> list);

    void deleteSum(SalaryQueryExamineDTO dto);

    //hr（计件工资）审核
    List<TPrdSalaryResultDTO> getSalary(SalaryQueryExamineDTO dto);

    //计件作业量审核
    List<TPrdSalaryResultDTO> getSalary2(SalaryQueryExamineDTO dto);

    List<String> getSalartType(TPrdSalaryPO salartById);
    /**
     * 查询所有计件单价
     * @return
     */
    List<MPiecePriceDTO> getAllPiecePrice();
    /**
     * 查询所有按照过程计件的部门
     * @return
     */
    List<Long> getAllProcessDept();

    @Edit
    int insertSalary(@Param("salaryList") List<TPrdSalaryPO> salaryList);

    List<TPrdSalaryPO> queryByOddId(@Param("ids") List<Long> ids);

    void deleteByOddId(@Param("ids") List<Long> ids);

    Integer getSalaryByCheckTimes(@Param("checkTimeList") List<String> checkDateList);
}
