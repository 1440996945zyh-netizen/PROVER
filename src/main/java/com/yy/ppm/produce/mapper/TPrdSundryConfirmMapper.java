package com.yy.ppm.produce.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.produce.bean.dto.TPrdSundryConfirmDTO;
import com.yy.ppm.produce.bean.dto.TPrdSundryConfirmSearchDTO;
import com.yy.ppm.produce.bean.po.TPoundPO;
import org.springframework.stereotype.Repository;

@Repository
public interface TPrdSundryConfirmMapper {

    Page<TPrdSundryConfirmDTO> getList(TPrdSundryConfirmSearchDTO searchDTO);

    int getById(Long id);

    @Edit
    void revokeConfirm(TPoundPO po);

    @Edit
    void confirm(TPoundPO po);
}
