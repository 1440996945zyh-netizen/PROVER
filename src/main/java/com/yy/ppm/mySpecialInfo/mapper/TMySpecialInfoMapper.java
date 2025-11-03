package com.yy.ppm.mySpecialInfo.mapper;


import com.yy.framework.annotation.Edit;
import com.yy.ppm.mySpecialInfo.bean.dto.TMySpecialInfoDTO;
import org.springframework.stereotype.Repository;

/**
 * @ClassName 个人特别信息表(TMySpecialInfo)Mapper
 * @author zws
 * @version 1.0.0
 * @Description
 * @createTime 2025年01月17日 10:17:00
 */
@Repository
public interface TMySpecialInfoMapper {

 /**
  * 根据id获取个人特别信息表
  * @param
  * @return
  */
 public TMySpecialInfoDTO getDetail(TMySpecialInfoDTO dto);

 /**
  * 新建
  * @param tMySpecialInfoDTO
  * @return
  */
 @Edit
 public int insert(TMySpecialInfoDTO tMySpecialInfoDTO);



 /**
  * 根据id删除个人特别信息表
  * @param id 主键
  * @return
  */
 public int deleteById(Long id);

// int projectInsert(@Param("list") List<TMySpecialInfoDTO> list);

 /**
  * 取关项目
  * */
 int noCareDelete(TMySpecialInfoDTO dto);



 void deletePageNum(TMySpecialInfoDTO dto);
}

