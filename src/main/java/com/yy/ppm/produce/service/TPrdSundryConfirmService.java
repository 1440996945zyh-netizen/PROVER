package com.yy.ppm.produce.service;

import com.yy.common.page.Pages;
import com.yy.ppm.produce.bean.dto.TPrdSundryConfirmDTO;
import com.yy.ppm.produce.bean.dto.TPrdSundryConfirmSearchDTO;

public interface TPrdSundryConfirmService {
    Pages<TPrdSundryConfirmDTO> getList(TPrdSundryConfirmSearchDTO searchDTO);

    void confirm(Long id);

    void revokeConfirm(Long id );
}
