package com.yy.ppm.system.service;

import com.yy.ppm.system.bean.dto.SysParameterDTO;

import java.util.List;

/**
 * 系统参数(SysParameter)表服务接口
 *
 * @author 张超
 * @date 2021-03-02 16:29:12
 */
public interface SysParameterService {

    /**
     * 获取数据列表
     *
     * @return
     */
    List<SysParameterDTO> getList(SysParameterDTO sysParameterDTO);

    /**
     * 保存系统参数
     *
     * @param parameterList
     * @return
     */
    void save(List<SysParameterDTO> parameterList);

    SysParameterDTO getConfig(String code);

    void saveUser(List<SysParameterDTO> parameterList);

    List<SysParameterDTO> getUserList(SysParameterDTO sysParameterDTO);

    boolean deleteUserById(Long id);

    boolean deleteById(Long id);
}
