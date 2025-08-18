package com.yy.ppm.master.service;


import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.master.bean.dto.MDictDataDTO;
import com.yy.ppm.master.bean.dto.MDictTypeDTO;
import com.yy.ppm.master.bean.dto.MDictTypeSearchDTO;
import com.yy.ppm.master.bean.po.MDictDataPO;
import com.yy.ppm.master.bean.po.MDictTypePO;

/**
 * @Description 字典及字典类型操作service类
 *
 * @author 孙琦
 * @date 2023-4-26 16:57:35
 */
public interface MDictService {


    /**
     * 查询全部字典类型
     * @param mDictTypeSearchDTO
     * @return
     */
    Pages<MDictTypeDTO> getDictTypeList(MDictTypeSearchDTO mDictTypeSearchDTO);


    /**
     * 新增、修改字典类型
     * @param po
     */
    void insertOrUpdateDictType(MDictTypeDTO po);

    /**
     * 删除字典类型
     * @param id
     */
    void deleteDictTypeById(Long id);

    /**
     * 新增、修改字典
     * @param po
     */
    void insertOrUpdateDict(MDictDataDTO po);

    /**
     * 根据类型查询 字典
     * @param pageParameter
     * @param typeCd
     * @return
     */
    Pages<MDictDataDTO> getDictListByType(PageParameter pageParameter, String typeCd);

    /**
     * 删除字典
     * @param id
     */
    void deleteDictById(Long id);

    /**
     * 查询全部字典
     * @param pageParameter
     * @param po
     * @return
     */
    Pages<MDictDataDTO> getDictList(PageParameter pageParameter, MDictDataDTO po);

//    /**
//     * 修改字典
//     * @param po
//     */
//    void updateDict(DictPO po);



//    /**
//     * 修改字典类型
//     * @param po
//     */
//    void updateDictType(DictTypePO po);


    /**
     * 根据id 查询字典
     * @param id
     * @return
     */
    MDictDataDTO getDictById(Long id);


    /**
     * 根据ID查询字典类型
     * @param id
     * @return
     */
    MDictTypeDTO getDictTypeById(Long id);
}
