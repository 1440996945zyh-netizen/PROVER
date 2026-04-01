package com.yy.ppm.common.bean.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * 树状结构顶层元素
 *
 * @author
 **/
@Getter
@Setter
public class Node implements Serializable {

    private static final long serialVersionUID = -4405388942387953237L;

    /**
     * Gid
     **/
    protected String id;

    /**
     * 父级Gid
     **/
    protected String parentId;

    /**
     * 排序
     **/
    protected Integer sortNum;

    /**
     * 关联子节点
     **/
    protected List<Node> children = null;

    /**
     * 是否有子节点
     */
    protected Boolean hasChildren;

    @Override
    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }

        Node otherNode = (Node) otherObject;
        return Objects.equals(this.id, otherNode.id);
    }

    @Override
    public final int hashCode() {
        return this.id.hashCode() * 31;
    }

    @Override
    public String toString() {
        ToStringBuilder sb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        sb.append("id", this.id);
        sb.append("parent_Id", this.parentId);
        sb.append("sort_num", this.sortNum);
        sb.append("children", this.children);
        return sb.build();
    }
}
