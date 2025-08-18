package com.yy.common.enums;

import lombok.Getter;

/**
 * 磅房、海关对照
 * @author zcc
 *
 */
@Getter
public enum CustomsPoundEnum {
	
	CUSTOMS_POUND_Z01("Z01", "4310040001"),
	CUSTOMS_POUND_Z02("Z02", "4310040002"),
	CUSTOMS_POUND_Z03("Z03", "4310040003"),
	CUSTOMS_POUND_Z04("Z04", "4310040004"),
	CUSTOMS_POUND_Z05("Z05", "4310040005"),
	CUSTOMS_POUND_Z06("Z06", "4310040006"),
	CUSTOMS_POUND_Z07("Z07", "4310040007"),
	CUSTOMS_POUND_Z08("Z08", "4310040008"),
	CUSTOMS_POUND_Z09("Z09", "4310021009"),
	CUSTOMS_POUND_Z10("Z10", "4310021010"),
	CUSTOMS_POUND_Z11("Z11", "4310021011"),
	CUSTOMS_POUND_Z12("Z12", "4310011012"),
	CUSTOMS_POUND_Z13("Z13", "4310010013"),
	CUSTOMS_POUND_Z14("Z14", "4310010014"),
	CUSTOMS_POUND_Z15("Z15", "4310011015"),
	CUSTOMS_POUND_D10("D10", "4310040014");

    private final String poundNo;// 磅房编号
    
    private final String customsNo;// 通道号

    CustomsPoundEnum(String poundNo, String customsNo) {
        this.poundNo = poundNo;
        this.customsNo = customsNo;
    }
    
    /**
     * 获取通道号
     * @param poundNo
     * @return
     */
    public static String getCustomsNo(String poundNo) {
    	for (CustomsPoundEnum customsPound : CustomsPoundEnum.values()) {
			if(customsPound.poundNo.equals(poundNo)) {
		        return customsPound.customsNo;
			}
		}
        return "";
    }
}
