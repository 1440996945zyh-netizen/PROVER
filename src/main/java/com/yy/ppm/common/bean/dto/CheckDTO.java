package com.yy.ppm.common.bean.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.List;

/**
 * 树状结构顶层元素
 *
 * @author
 **/
@Getter
@Setter
public class CheckDTO implements Serializable {

    private static final long serialVersionUID = -4405388942387953237L;

    /**
     * Gid
     **/
    protected String key;

    /**
     * 父级Gid
     **/
    protected Object value;


    public static CheckDTO buildDTO(String key, Object value) {
        CheckDTO dto = new CheckDTO();
        dto.setKey(key);
        dto.setValue(value);
        return dto;
    }
}
