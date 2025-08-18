package com.yy.common.enums;

import java.util.List;
import com.google.api.client.util.Lists;
import lombok.Getter;

/**
 * 用于统计集疏港作业量、装卸（直取、倒运）船作业量
 * 只使用子过程
 * @author zcc
 */
@Getter
public enum WorkProcessEnum {
	
	// 集、疏港的子过程
	WORK_PROCESS_10130001("10130001", "0", "1", "场-车(疏港)"),
	WORK_PROCESS_10030001("10030001", "0", "1", "车-场(集港)"),
	
	// 直取、直疏
	WORK_PROCESS_100120001("100120001", "1", "0", "船-车(卸船直取)"),
	WORK_PROCESS_100120003("100120003", "1", "0", "岸-车(前沿装车)"),
	WORK_PROCESS_10050001("10050001", "1", "0", "车-船(装船直取)"),
	WORK_PROCESS_10050002("10050002", "1", "0", "车-岸(前沿集港)"),
	
	// 装船
	WORK_PROCESS_10210002("10210002", "0", "0", "车-岸(装船倒运)"),
	WORK_PROCESS_10220001("10220001", "0", "0", "岸-船(装船)"),
	WORK_PROCESS_10250002("10250002", "0", "0", "车-船(倒运直装)"),
	WORK_PROCESS_10250005("10250005", "0", "0", "岸-船(装船)"),
	
	// 卸船
	WORK_PROCESS_10190001("10190001", "0", "0", "船-岸(卸船)"),
	WORK_PROCESS_10200002("10200002", "0", "0", "车-场(卸船倒运)"),
	WORK_PROCESS_10260002("10260002", "0", "0", "车-场(倒运入库)");

    private final String processCd;// 作业过程编码
    
    private final String isDirectAccess;// 是否直取（1.是  0是否）
    
    private final String isInOutPort;// 是否集疏港（1.是  0是否）

    private final String processNm;// 作业过程名称

    WorkProcessEnum(String processCd, String isDirectAccess, String isInOutPort, String processNm) {
        this.processCd = processCd;
        this.isDirectAccess = isDirectAccess;
        this.isInOutPort = isInOutPort;
        this.processNm = processNm;
    }
    
    /**
     * 是否直取、是否集疏港
     * @param code
     * @return
     */
    public static List<String> getDataList(String isDirectAccess, String isInOutPort) {
    	List<String> resList = Lists.newArrayList();
    	
    	for (WorkProcessEnum workProcess : WorkProcessEnum.values()) {
			if(workProcess.isDirectAccess.equals(isDirectAccess) 
					&& workProcess.isInOutPort.equals(isInOutPort)) {
				resList.add(workProcess.processCd);
			}
		}
        return resList;
    }
}
