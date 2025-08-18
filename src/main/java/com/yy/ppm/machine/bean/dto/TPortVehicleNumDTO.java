package com.yy.ppm.machine.bean.dto;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TPortVehicleNumDTO extends BasePO implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1018223043866743937L;
	

    /**
     * 港区编码
     */
    private String portCode;
    /**
     * 车辆数
     */
    private Integer num;

}