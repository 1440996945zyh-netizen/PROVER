package com.yy.ppm.system.mapper;

import com.yy.framework.annotation.Edit;
import com.yy.ppm.system.bean.dto.SysParameterDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统参数(SysParameter)Dao
 *
 * @author 张超
 * @date 2021-03-02 16:29:35
 */
public interface SysParameterMapper {

    /**
     * 获取系统参数列表
     *
     * @return
     */
    List<SysParameterDTO> getList(SysParameterDTO sysParameterDTO);

    /**
     * 新增系统参数
     *
     * @param sysParameterDTO 系统参数DTO
     * @return
     */
    @Edit
    int insert(SysParameterDTO sysParameterDTO);

    /**
     * 根据Key获得系统参数
     *
     * @return
     */
    SysParameterDTO getByKey(@Param(value="key") String key);

    List<SysParameterDTO> getUserList(SysParameterDTO sysParameterDTO);

    @Edit
    void update(SysParameterDTO dto);

    @Edit
    void updateVal(SysParameterDTO dto);

    int deleteById(Long id);

    int deleteUserById(Long id);

    SysParameterDTO getById(Long id);
}

