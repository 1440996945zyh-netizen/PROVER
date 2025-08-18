package com.yy.ppm.statement.service.storageAmountCalculate;

import com.yy.common.page.Pages;
import com.yy.ppm.statement.bean.dto.StorageDemoDTO;

public interface StorageAmountCalculateDemoService {

    void calculate();


    Pages<StorageDemoDTO> getList(StorageDemoDTO dto);
}
