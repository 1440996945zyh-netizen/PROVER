package com.yy.ppm.system.bean.dto;

import com.yy.ppm.system.bean.po.SysParameterPO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * 系统参数(SysParameter)DTO
 *
 * @author 张超
 * @date 2021-03-02 16:30:22
 */
@Getter
@Setter
@ToString
public class SysParameterDTO extends SysParameterPO implements Serializable {

    private static final long serialVersionUID = -61182335484537068L;




    @Override
    public boolean equals( Object obj) {
        if(this == obj){
            return true;//地址相等
        }

        if(obj == null){
            return false;//非空性：对于任意非空引用x，x.equals(null)应该返回false。
        }

        if(obj instanceof SysParameterDTO){
            SysParameterDTO other = (SysParameterDTO) obj;
            //需要比较的字段相等，则这两个对象相等
            if(equalsStr(super.getParamCd(), other.getParamCd())
                    && equalsStr(super.getParamNm(), other.getParamNm())
                    && equalsStr(super.getParamVal(), other.getParamVal())
                    && equalsStr(super.getRemark(), other.getRemark())){
                return true;
            }
        }
        return  false;
    }



    private boolean equalsStr(String str1, String str2){
        if(StringUtils.isEmpty(str1) && StringUtils.isEmpty(str2)){
            return true;
        }
        if(!StringUtils.isEmpty(str1) && str1.equals(str2)){
            return true;
        }
        return false;
    }
/*    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (name == null ? 0 : name.hashCode());
        result = 31 * result + (age == null ? 0 : age.hashCode());
        return result;
    }*/


}
