package com.yy.ppm.business.service;



import com.yy.common.page.Pages;
import com.yy.ppm.business.bean.dto.PoundbillDTO;
import com.yy.ppm.business.bean.dto.PoundbillSearchDTO;
import java.util.*;

 /**
 * @ClassName 单船测试记录(TStdShipRecord)Service
 * @author makejava
 * @version 1.0.0
 * @Description
 * @createTime 2023年12月31日 10:35:00
 */
public interface PoundbillService {

    /**
     * 获取列表（翻页）
     * @param searchDTO
     * @return 对象列表
     */
    public Pages<PoundbillDTO> getPageList(PoundbillSearchDTO searchDTO);
	
	public List<PoundbillDTO> getDetailListByCondition(PoundbillSearchDTO searchDTO);
    
}

