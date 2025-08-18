package com.yy.ppm.common.enums;

import lombok.Getter;

/**
 * 作业过程类型枚举
 */
@Getter
public enum ProcessEnum {

	PROCESS_QCC("CHE/CHANG", "车/场"),
	PROCESS_CHECHUAN("CHE/CHUAN", "车/船"),
	PROCESS_CHANGNCHE("CHANG/CHE", "场/车"),
	PROCESS_CHANGCHELX("CHANG/CHE/LX", "场(陆销)/车"),
	PROCESS_CC("CHUAN/CHANG", "船/场"),
	PROCESS_CHANGCHUAN("CHANG/CHUAN", "场/船"),
	PROCESS_XIANGCHANG("XIANG/CHANG", "箱/场"),
	PROCESS_CHECHANG("CHE-CHANG", "车-场"),
	PROCESS_CHEXZK("CHE-XZK", "车-熏蒸库"),
	PROCESS_CHEDJQ("CHE-DJQ", "车-待检区"),
	PROCESS_CHEXZKDJQ("CHE-XZK-DJQ", "车(待检区)-熏蒸库"),
	PROCESS_DJQCHE("DJQ-CHE", "待检区-车"),
	PROCESS_DJQXZK("DJQ-XZK", "待检区-熏蒸库"),
	PROCESS_XZKCHE("XZK-CHE", "熏蒸库-车"),
	PROCESS_CHECHANGXZK("CHE-CHANG-XZK", "车(熏蒸库)-场"),
	PROCESS_JIASHUI("JIASHUI", "加水"),
	PROCESS_JIEDIAN("JIEDIAN", "接电"),
	PROCESS_QINGCANG("QINGCANG", "树皮清舱"),
	PROCESS_SPQC("SPQC", "树皮清舱熏蒸"),
	PROCESS_XUNZHENG("XUNZHENG", "熏蒸"),
	PROCESS_FENXUAN("FENXUAN", "分选"),
	PROCESS_XCJC("XCJC", "卸船检尺"),
	PROCESS_FHJC1("FHJC-1", "发货检尺(卸船已检)"),
	PROCESS_FHJC0("FHJC-0", "发货检尺(卸船未检)"),


	PROCESS_CHUANAN("CHUAN-AN", "船-岸"),

	PROCESS_ANCHE("AN-CHE", "岸-车"),

	PROCESS_CHEAN("CHE-AN", "车-岸"),

	PROCESS_ANCHUAN("AN-CHUAN", "岸-船");


	ProcessEnum(String code, String comment) {
		this.code = code;
		this.comment = comment;
	}

	/**
	 * 枚举code 费目类型ID
	 **/
	private String code;

	/**
	 * 枚举注释 费目类型名称
	 **/
	private String comment;

}
