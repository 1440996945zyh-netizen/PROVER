--*************************表结构初始化**************************--
DROP TABLE IF EXISTS `httpjob_details`;
CREATE TABLE `httpjob_details`  (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `JOB_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '作业名称（与JOB_GROUP组成唯一标识）',
  `JOB_GROUP` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '作业组名（与JOB_NAME组成唯一标识）',
  `DESCRIPTION` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '任务描述',
  `REQUEST_TYPE` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'HTTP请求类型（GET, POST, POST_JSON等）',
  `HTTP_URL` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'HTTP接口地址',
  `HTTP_PARAMS` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'HTTP请求参数（JSON格式字符串）',
  `CREATE_TIME` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPDATE_TIME` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'HTTP任务详情表，业务系统扩展，存储HTTP任务的额外配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for httpjob_logs
-- ----------------------------
DROP TABLE IF EXISTS `httpjob_logs`;
CREATE TABLE `httpjob_logs`  (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `JOB_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '作业名称',
  `JOB_GROUP` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '作业组名',
  `REQUEST_TYPE` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'HTTP请求类型',
  `HTTP_URL` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'HTTP接口地址',
  `HTTP_PARAMS` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'HTTP请求参数',
  `FIRE_TIME` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '触发时间',
  `RESULT` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '执行结果（成功/失败及详情）',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'HTTP任务日志表，业务系统扩展，存储HTTP任务执行日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for m_constants
-- ----------------------------
DROP TABLE IF EXISTS `m_constants`;
CREATE TABLE `m_constants`  (
  `TYPE_CD` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '常量组类型',
  `GROUP_CD` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '常量组分组编号',
  `CD` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编号',
  `NM` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称',
  `SORT_NUM` int(11) NOT NULL COMMENT '排序',
  `REMARK` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`TYPE_CD`, `CD`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for m_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `m_dict_data`;
CREATE TABLE `m_dict_data`  (
  `ID` bigint(20) NULL DEFAULT NULL,
  `DICT_TYPE` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字典类型编号',
  `DICT_LABEL` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字典名称',
  `DICT_VALUE` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字典编号',
  `SORT_NUM` int(11) NULL DEFAULT NULL,
  `STATUS` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '可用状态(0: 停用, 1: 可用)',
  `REMARK` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `CREATE_BY` bigint(20) NULL DEFAULT NULL,
  `CREATE_BY_NAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `CREATE_TIME` datetime NULL DEFAULT NULL,
  `UPDATE_BY` bigint(20) NULL DEFAULT NULL,
  `UPDATE_BY_NAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `UPDATE_TIME` datetime NULL DEFAULT NULL,
  `DICT_ENG_LABEL` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字典英文标签'
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for m_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `m_dict_type`;
CREATE TABLE `m_dict_type`  (
  `ID` bigint(20) NULL DEFAULT NULL,
  `DICT_TYPE` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `DICT_NAME` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `IS_OPEN` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `STATUS` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `REMARK` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `CREATE_BY` bigint(20) NULL DEFAULT NULL,
  `CREATE_BY_NAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `CREATE_TIME` datetime NULL DEFAULT NULL,
  `UPDATE_BY` bigint(20) NULL DEFAULT NULL,
  `UPDATE_BY_NAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `UPDATE_TIME` datetime NULL DEFAULT NULL,
  `SORT_NUM` int(11) NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_blob_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_blob_triggers`;
CREATE TABLE `qrtz_blob_triggers`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '调度器名称',
  `TRIGGER_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '触发器名称',
  `TRIGGER_GROUP` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '触发器组名',
  `BLOB_DATA` blob NULL COMMENT '二进制数据（序列化的Trigger对象）',
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  CONSTRAINT `qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'BLOB触发器表，存储二进制格式的Trigger信息（较少使用）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_calendars
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_calendars`;
CREATE TABLE `qrtz_calendars`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '调度器名称',
  `CALENDAR_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '日历名称',
  `CALENDAR` blob NOT NULL COMMENT '日历对象（序列化后的二进制数据）',
  PRIMARY KEY (`SCHED_NAME`, `CALENDAR_NAME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '日历表，存储调度中使用的日历信息（用于排除特定日期）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_cron_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_cron_triggers`;
CREATE TABLE `qrtz_cron_triggers`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '调度器名称',
  `TRIGGER_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '触发器名称',
  `TRIGGER_GROUP` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '触发器组名',
  `CRON_EXPRESSION` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'CRON表达式（核心调度规则，如\"0/5 * * * * ?\"）',
  `TIME_ZONE_ID` varchar(80) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '时区ID（如\"Asia/Shanghai\"）',
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  CONSTRAINT `qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'CRON触发器表，存储CronTrigger类型的额外信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_fired_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_fired_triggers`;
CREATE TABLE `qrtz_fired_triggers`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '调度器名称',
  `ENTRY_ID` varchar(95) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '唯一ID（通常为\"实例名+时间戳\"）',
  `TRIGGER_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '触发器名称',
  `TRIGGER_GROUP` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '触发器组名',
  `INSTANCE_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '触发该任务的调度器实例名',
  `FIRED_TIME` bigint(13) NOT NULL COMMENT '实际触发时间（时间戳）',
  `SCHED_TIME` bigint(13) NOT NULL COMMENT '计划触发时间（时间戳）',
  `PRIORITY` int(11) NOT NULL COMMENT '优先级',
  `STATE` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '状态',
  `JOB_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '作业名称',
  `JOB_GROUP` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '作业组名',
  `IS_NONCONCURRENT` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否禁止并发执行',
  `REQUESTS_RECOVERY` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否请求恢复',
  PRIMARY KEY (`SCHED_NAME`, `ENTRY_ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '已触发的触发器表，记录所有已触发的Trigger执行历史' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_job_details
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_job_details`;
CREATE TABLE `qrtz_job_details`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '调度器名称，用于区分多个调度器实例',
  `JOB_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '作业名称，与JOB_GROUP组成唯一标识',
  `JOB_GROUP` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '作业组名，与JOB_NAME组成唯一标识',
  `DESCRIPTION` varchar(250) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '作业描述信息',
  `JOB_CLASS_NAME` varchar(250) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '作业实现类的全限定名（如com.example.MyJob）',
  `IS_DURABLE` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '是否持久化（即使没有Trigger也保留）：Y-是，N-否',
  `IS_NONCONCURRENT` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '是否禁止并发执行：Y-是（@DisallowConcurrentExecution），N-否',
  `IS_UPDATE_DATA` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '是否更新JobDataMap：Y-是，N-否',
  `REQUESTS_RECOVERY` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '是否请求恢复（失败后是否重新执行）：Y-是，N-否',
  `JOB_DATA` blob NULL COMMENT '作业数据，序列化的JobDataMap对象，存储任务参数',
  PRIMARY KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '作业详情表，存储所有Job的元数据信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_locks
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_locks`;
CREATE TABLE `qrtz_locks`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '调度器名称',
  `LOCK_NAME` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '锁名称（TRIGGER_ACCESS, JOB_ACCESS等）',
  PRIMARY KEY (`SCHED_NAME`, `LOCK_NAME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '锁表，实现集群环境下的同步锁机制' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_paused_trigger_grps
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_paused_trigger_grps`;
CREATE TABLE `qrtz_paused_trigger_grps`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '调度器名称',
  `TRIGGER_GROUP` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '被暂停的触发器组名',
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_GROUP`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '暂停的触发器组表，记录被暂停的触发器组' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_scheduler_state
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_scheduler_state`;
CREATE TABLE `qrtz_scheduler_state`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '调度器名称',
  `INSTANCE_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '调度器实例名（集群中唯一标识）',
  `LAST_CHECKIN_TIME` bigint(13) NOT NULL COMMENT '最后检查时间（时间戳）',
  `CHECKIN_INTERVAL` bigint(13) NOT NULL COMMENT '检查间隔（毫秒）',
  PRIMARY KEY (`SCHED_NAME`, `INSTANCE_NAME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '调度器状态表，存储集群环境下各节点状态' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_simple_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_simple_triggers`;
CREATE TABLE `qrtz_simple_triggers`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '调度器名称',
  `TRIGGER_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '触发器名称',
  `TRIGGER_GROUP` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '触发器组名',
  `REPEAT_COUNT` bigint(7) NOT NULL COMMENT '重复次数（-1表示无限重复）',
  `REPEAT_INTERVAL` bigint(12) NOT NULL COMMENT '重复间隔（毫秒）',
  `TIMES_TRIGGERED` bigint(10) NOT NULL COMMENT '已触发次数',
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  CONSTRAINT `qrtz_simple_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '简单触发器表，存储SimpleTrigger类型的额外信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_simprop_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_simprop_triggers`;
CREATE TABLE `qrtz_simprop_triggers`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '调度器名称',
  `TRIGGER_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '触发器名称',
  `TRIGGER_GROUP` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '触发器组名',
  `STR_PROP_1` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字符串属性1',
  `STR_PROP_2` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字符串属性2',
  `STR_PROP_3` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字符串属性3',
  `INT_PROP_1` int(11) NULL DEFAULT NULL COMMENT '整数属性1',
  `INT_PROP_2` int(11) NULL DEFAULT NULL COMMENT '整数属性2',
  `LONG_PROP_1` bigint(20) NULL DEFAULT NULL COMMENT '长整数属性1',
  `LONG_PROP_2` bigint(20) NULL DEFAULT NULL COMMENT '长整数属性2',
  `DEC_PROP_1` decimal(13, 4) NULL DEFAULT NULL COMMENT '小数属性1',
  `DEC_PROP_2` decimal(13, 4) NULL DEFAULT NULL COMMENT '小数属性2',
  `BOOL_PROP_1` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '布尔属性1（Y/N）',
  `BOOL_PROP_2` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '布尔属性2（Y/N）',
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  CONSTRAINT `qrtz_simprop_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '触发器扩展属性表，存储Trigger的各种类型扩展属性（Quartz 2.2.0+）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_triggers`;
CREATE TABLE `qrtz_triggers`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '调度器名称',
  `TRIGGER_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '触发器名称，与TRIGGER_GROUP组成唯一标识',
  `TRIGGER_GROUP` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '触发器组名，与TRIGGER_NAME组成唯一标识',
  `JOB_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '关联的作业名称',
  `JOB_GROUP` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '关联的作业组名',
  `DESCRIPTION` varchar(250) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '触发器描述',
  `NEXT_FIRE_TIME` bigint(13) NULL DEFAULT NULL COMMENT '下一次触发时间（时间戳，核心字段）',
  `PREV_FIRE_TIME` bigint(13) NULL DEFAULT NULL COMMENT '上一次触发时间（时间戳）',
  `PRIORITY` int(11) NULL DEFAULT NULL COMMENT '优先级（数值越大优先级越高）',
  `TRIGGER_STATE` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '触发器状态：WAITING(等待), PAUSED(暂停), ACQUIRED(已获取), EXECUTING(执行中), COMPLETE(完成), ERROR(错误)',
  `TRIGGER_TYPE` varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '触发器类型：SIMPLE(简单), CRON(CRON表达式), BLOB(二进制)',
  `START_TIME` bigint(13) NOT NULL COMMENT '开始时间（时间戳）',
  `END_TIME` bigint(13) NULL DEFAULT NULL COMMENT '结束时间（时间戳）',
  `CALENDAR_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '关联的日历名称（QRTZ_CALENDARS表）',
  `MISFIRE_INSTR` smallint(2) NULL DEFAULT NULL COMMENT '错过触发处理策略（misfire instruction）',
  `JOB_DATA` blob NULL COMMENT '触发器数据，序列化的Trigger对象数据',
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  CONSTRAINT `qrtz_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) REFERENCES `qrtz_job_details` (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '触发器基础表，存储所有Trigger的通用信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_custom_region
-- ----------------------------
DROP TABLE IF EXISTS `sys_custom_region`;
CREATE TABLE `sys_custom_region`  (
  `ID` bigint(20) NOT NULL COMMENT '主键',
  `USER_ACCOUNT` varchar(70) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '登录账号',
  `MENU_NAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
  `MENU_ROUTER` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '前端路由',
  `SORT_NUM` int(4) NULL DEFAULT NULL COMMENT '排序号',
  `CREATE_BY` bigint(20) NOT NULL COMMENT '创建人ID',
  `CREATE_BY_NAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建人名字',
  `CREATE_TIME` datetime NOT NULL COMMENT '创建时间',
  `UPDATE_BY` bigint(20) NULL DEFAULT NULL COMMENT '更新人ID',
  `UPDATE_BY_NAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人名字',
  `UPDATE_TIME` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `MENU_ID` bigint(20) NOT NULL COMMENT '菜单ID',
  `USER_ID` bigint(20) NOT NULL COMMENT '用户ID',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`  (
  `DEPT_ID` bigint(20) NULL DEFAULT NULL COMMENT 'ID',
  `PARENT_ID` bigint(20) NULL DEFAULT NULL COMMENT '上级组织机构ID',
  `DEPT_CODE` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '组织机构编号',
  `DEPT_NAME` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '组织机构名称',
  `ORDER_NO` int(11) NULL DEFAULT NULL COMMENT '排序号',
  `REMARK` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `PARENT_IDS` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '祖级列表',
  `CHIEF` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '负责人',
  `DESCR` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
  `DEPT_LEVEL` int(11) NULL DEFAULT NULL COMMENT '级别：0 集团 1.公司 2.部门 3班组 4作业班组',
  `DEPT_TYPE` int(11) NULL DEFAULT NULL COMMENT '组织机构类别，0代表从OA同步，1代表本地组织机构',
  `DEPT_NO` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '组织机构编号，用于懒加载，四位一层，如0001，它的子组织为00010001-00019999，，00010001的子组织为000100010001-000100019999，以此类推',
  `PARENT_DEPT_CODE` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '父组织机构code',
  `CREATE_BY` bigint(20) NULL DEFAULT NULL COMMENT '创建者ID',
  `CREATE_BY_NAME` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建者姓名',
  `CREATE_TIME` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  `UPDATE_BY` bigint(20) NULL DEFAULT NULL COMMENT '修改者ID',
  `UPDATE_BY_NAME` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '修改者姓名',
  `UPDATE_TIME` datetime(6) NULL DEFAULT NULL COMMENT '修改时间',
  `STATUS` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '是否可用（0不可用 1可用）',
  `ID` bigint(20) NULL DEFAULT NULL COMMENT 'ID',
  `IS_LABOR` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否为劳务队 字典:0-否,1-是 （部门）',
  `IS_MACHINE` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否为机械队 字典:0-否,1-是（部门）',
  `IS_PROJECT` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否为项目组 字典:0-否,1-是（部门）',
  `IN_OUT_TYPE` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'I :内部，O:外部',
  `IS_WORK_COMPANY` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否作业公司 （公司）',
  `IS_TALLY_COMPANY` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否作业公司 （公司）',
  `IS_TALLY_DEPT` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否理货部门',
  `CAN_DISPATCH_DEPT` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '可配工部门',
  `IS_SALARY_BY_PROCESS` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否根据作业过程计件（1 是，0 否）',
  `TICKET_LEVEL` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '1:一级签票  2:二级签票'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '组织架构表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_file
-- ----------------------------
DROP TABLE IF EXISTS `sys_file`;
CREATE TABLE `sys_file`  (
  `ID` bigint(20) NOT NULL COMMENT '附件id',
  `BUSINESS_ID` bigint(20) NULL DEFAULT NULL COMMENT '业务主键',
  `BUSINESS_TYPE` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '业务类型',
  `FILE_BUCKET` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '桶',
  `FILE_PATH` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '服务器路径',
  `FILE_SAVE_NAME` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件保存在服务器名称',
  `FILE_NAME` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件名',
  `FILE_ICON` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件显示图标',
  `FILE_TYPE` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件类型（0：临时文件 1：正式文件）',
  `FILE_URL` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件url',
  `CREATE_BY` bigint(20) NULL DEFAULT NULL COMMENT '创建者ID',
  `CREATE_BY_NAME` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建者姓名',
  `CREATE_TIME` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  `UPDATE_BY` bigint(20) NULL DEFAULT NULL COMMENT '修改者ID',
  `UPDATE_BY_NAME` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '修改者姓名',
  `UPDATE_TIME` datetime(6) NULL DEFAULT NULL COMMENT '修改时间',
  `FILE_SUFFIX` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件后缀',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '附件表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_file_business
-- ----------------------------
DROP TABLE IF EXISTS `sys_file_business`;
CREATE TABLE `sys_file_business`  (
  `BUSINESS_ID` bigint(20) NOT NULL COMMENT '业务id',
  `FILE_ID` bigint(20) NOT NULL COMMENT '附件id',
  PRIMARY KEY (`BUSINESS_ID`, `FILE_ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '业务附件中间表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_login_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_login_log`;
CREATE TABLE `sys_login_log`  (
  `ID` bigint(20) NOT NULL COMMENT '主键id',
  `USER_ID` bigint(20) NULL DEFAULT NULL COMMENT '登录人Id',
  `ACC_NO` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '登录人账号',
  `USER_NAME` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '登录人',
  `POST` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '岗位',
  `DEPT` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '部门',
  `COMPANY_NAME` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '所属公司',
  `LOGIN_TIME` datetime NULL DEFAULT NULL COMMENT '登录时间',
  `LOGIN_IP` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '登录ip',
  `CHANNEL_TYPE` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '登录渠道,字典:01-PC,02-安卓APP,03-IOS APP',
  `UQ_MARK` bigint(20) NULL DEFAULT NULL COMMENT '登录用户唯一标记',
  `OS` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作系统',
  `BROWSER` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '浏览器',
  `LOCATION` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '登录地点',
  `STATUS` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '状态',
  `ERROR_MSG` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '错误消息',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '登录日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `ID` bigint(20) NOT NULL COMMENT '菜单ID',
  `MENU_NAME` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单名称',
  `PARENT_ID` bigint(20) NULL DEFAULT 0 COMMENT '父菜单ID',
  `ORDER_NUM` int(11) NULL DEFAULT 0 COMMENT '显示顺序',
  `PATH` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '路由地址',
  `COMPONENT` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '组件路径',
  `QUERY` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '路由参数',
  `IS_FRAME` tinyint(4) NULL DEFAULT 1 COMMENT '是否为外链（0是 1否）',
  `IS_CACHE` tinyint(4) NULL DEFAULT 0 COMMENT '是否缓存（0缓存 1不缓存）',
  `MENU_TYPE` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `VISIBLE` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `STATUS` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `PERMS` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '权限标识',
  `ICON` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '#' COMMENT '菜单图标',
  `CREATE_BY` bigint(20) NULL DEFAULT NULL COMMENT '创建者ID',
  `CREATE_TIME` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  `UPDATE_BY` bigint(20) NULL DEFAULT NULL COMMENT '修改者ID',
  `UPDATE_TIME` datetime(6) NULL DEFAULT NULL COMMENT '修改时间',
  `REMARK` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '备注',
  `MENU_ICON_COLOR` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图标颜色',
  `DATA_TYPE` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '数据类别（PC；APP)',
  `CREATE_BY_NAME` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建者姓名',
  `UPDATE_BY_NAME` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '修改者姓名',
  `ICON_APP` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'APP图标',
  `LINK` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '外链地址',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '菜单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_oper_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log`  (
  `OPER_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作ID',
  `TITLE` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '模块标题',
  `BUSINESS_TYPE` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '业务类型（4其它 1新增 2修改 3删除）',
  `METHOD` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '方法名称',
  `REQUEST_METHOD` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '请求方式',
  `OPER_TYPE` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'PC/APP（0其它 1后台用户 2手机端用户）',
  `OPER_USER_NAME` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作人员姓名',
  `OPER_URL` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '请求URL',
  `OPER_IP` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '主机地址',
  `OPER_LOCATION` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作地点',
  `STATUS` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作状态 正常,异常',
  `OPER_TIME` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作时间',
  `OPER_PARAM` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '请求参数',
  `JSON_RESULT` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '返回参数',
  `ERROR_MSG` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '错误消息',
  `OPER_USER_ID` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作人员ID',
  `COST_TIME` bigint(20) NULL DEFAULT NULL COMMENT '耗时'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_parameter
-- ----------------------------
DROP TABLE IF EXISTS `sys_parameter`;
CREATE TABLE `sys_parameter`  (
  `ID` bigint(20) NOT NULL COMMENT '主键',
  `PARAM_CD` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '参数编号',
  `PARAM_NM` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '参数名称',
  `PARAM_VAL` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '参数值',
  `REMARK` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `CREATE_BY` bigint(20) NULL DEFAULT NULL COMMENT '创建者ID',
  `CREATE_BY_NAME` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建者姓名',
  `CREATE_TIME` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  `UPDATE_BY` bigint(20) NULL DEFAULT NULL COMMENT '修改者ID',
  `UPDATE_BY_NAME` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '修改者姓名',
  `UPDATE_TIME` datetime(6) NULL DEFAULT NULL COMMENT '修改时间',
  `FLAG` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标记是用户(1)还是系统管理员(2)',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统参数' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_parameter_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_parameter_log`;
CREATE TABLE `sys_parameter_log`  (
  `ID` bigint(20) NOT NULL COMMENT 'id',
  `PARAM_CD_OLD` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '修改前参数编号',
  `PARAM_NM_OLD` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '修改前参数名称',
  `PARAM_VAL_OLD` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '修改前参数值',
  `REMARK_OLD` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '修改前备注',
  `PARAM_CD_NEW` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '修改后参数编号',
  `PARAM_NM_NEW` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '修改后参数名称',
  `PARAM_VAL_NEW` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '修改后参数值',
  `REMARK_NEW` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '修改后备注',
  `CREATE_BY` bigint(20) NULL DEFAULT NULL COMMENT '创建者ID',
  `CREATE_BY_NAME` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建者姓名',
  `CREATE_TIME` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  `UPDATE_BY` bigint(20) NULL DEFAULT NULL COMMENT '修改者ID',
  `UPDATE_BY_NAME` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '修改者姓名',
  `UPDATE_TIME` datetime(6) NULL DEFAULT NULL COMMENT '修改时间',
  `FLAG` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '1用户2系统管理员',
  `OPERATION_TYPE` int(11) NULL DEFAULT NULL COMMENT '操作类型（1:新增，2:修改，0:删除）',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统参数日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `ID` bigint(20) NOT NULL COMMENT 'ID',
  `DEPT_ID` bigint(20) NULL DEFAULT NULL COMMENT '组织架构ID',
  `ROLE_NAME` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色名称',
  `STATUS` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色状态(0:不可用，1:可用)',
  `DELETED` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否删除(0:否，1:是)',
  `CREATE_BY` bigint(20) NULL DEFAULT NULL COMMENT '创建者ID',
  `CREATE_BY_NAME` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建者姓名',
  `CREATE_TIME` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  `UPDATE_BY` bigint(20) NULL DEFAULT NULL COMMENT '修改者ID',
  `UPDATE_BY_NAME` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '修改者姓名',
  `UPDATE_TIME` datetime(6) NULL DEFAULT NULL COMMENT '修改时间',
  `ROLE_CODE` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色code',
  `ROLE_SORT` int(11) NULL DEFAULT NULL COMMENT '显示顺序',
  `MENU_CHECK_STRICTLY` tinyint(4) NULL DEFAULT NULL COMMENT '菜单树选择项是否关联显示',
  `REMARK` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '备注',
  `DEPT_CHECK_STRICTLY` tinyint(4) NULL DEFAULT NULL COMMENT '部门树选择项是否关联显示',
  `DATA_SCOPE` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  `ROLE_CLASS` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色类（目前主要作用与生产组织标准化）',
  `DEPT_NAME` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '部门名称',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_role_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_dept`;
CREATE TABLE `sys_role_dept`  (
  `ROLE_ID` bigint(20) NULL DEFAULT NULL COMMENT '角色id',
  `DEPT_ID` bigint(20) NULL DEFAULT NULL COMMENT '部门id',
  `DEPT_NO` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '组织机构编号'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '部门角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `ROLE_ID` bigint(20) NULL DEFAULT NULL COMMENT '角色主键',
  `MENU_ID` bigint(20) NULL DEFAULT NULL COMMENT '菜单主键'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色菜单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_role_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_user`;
CREATE TABLE `sys_role_user`  (
  `ROLE_ID` bigint(20) NULL DEFAULT NULL COMMENT '角色主键',
  `USER_ID` bigint(20) NULL DEFAULT NULL COMMENT '人员主键'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色用户' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `ID` bigint(20) NOT NULL COMMENT '主键',
  `USER_ACCOUNT` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '登录账号',
  `PASSWD` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密码',
  `USER_NAME` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户姓名',
  `USER_TYPE` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户类型（00系统用户 01注册用户）',
  `DEPT_ID` bigint(20) NULL DEFAULT NULL COMMENT '组织架构主键(所属部门)',
  `EMAIL` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `STATUS` int(11) NULL DEFAULT NULL COMMENT '状态1：在用，0停用',
  `SORT_NUM` int(11) NULL DEFAULT NULL COMMENT '排序号',
  `REMARK` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `PSD_UPD_DATE` datetime NULL DEFAULT NULL COMMENT '下次密码更新时间',
  `SEX` int(11) NULL DEFAULT NULL COMMENT '性别(1：男，0：女)',
  `TEL` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `MOBILE` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `ADDRESS` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '地址',
  `IS_SUPERADMIN` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否超级管理员',
  `USER_SOURCE` int(11) NULL DEFAULT NULL COMMENT '用户来源(0：OA系统  1：自行录入)',
  `POSTS` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '兼职岗位（多个用，隔开）',
  `CREATE_BY` bigint(20) NULL DEFAULT NULL COMMENT '创建者ID',
  `CREATE_BY_NAME` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建者姓名',
  `CREATE_TIME` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  `UPDATE_BY` bigint(20) NULL DEFAULT NULL COMMENT '修改者ID',
  `UPDATE_BY_NAME` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '修改者姓名',
  `UPDATE_TIME` datetime(6) NULL DEFAULT NULL COMMENT '修改时间',
  `IS_LABOR` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否劳务 字典:0-否,1-是',
  `POST_CODE` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '岗位编号 字典POST',
  `POST_NAME` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '岗位名称 字典POST',
  `UNIT_TYPE_CODE` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '单位类型 字典 UNIT_TYPE',
  `UNIT_TYPE_NAME` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '单位类型 字典 UNIT_TYPE',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;


CREATE TABLE ws_offline_message (
    id          DECIMAL(30,0) PRIMARY KEY COMMENT '主键ID',
    sender_id   DECIMAL(30,0) COMMENT '发送者ID（主键）',
    receiver_id DECIMAL(30,0) COMMENT '接收者ID（主键）',
    content     VARCHAR(500) COMMENT '消息内容',
    message_type CHAR(1) COMMENT '消息类型（0:即时通信;1:待办消息）',
    is_sent     CHAR(1) COMMENT '是否发送（0:未发送;1:已发送）',
    create_time DATETIME COMMENT '创建时间',
    create_by   VARCHAR(50) COMMENT '创建者'
) COMMENT = '离线消息表';


--********************表数据初始化********************--

INSERT INTO qrtz_locks (SCHED_NAME,LOCK_NAME) VALUES
	 ('BusinessScheduler','STATE_ACCESS'),
	 ('BusinessScheduler','TRIGGER_ACCESS');
INSERT INTO qrtz_scheduler_state (SCHED_NAME,INSTANCE_NAME,LAST_CHECKIN_TIME,CHECKIN_INTERVAL) VALUES
	 ('BusinessScheduler','jokerZhang1756947772951',1756954314233,20000),
	 ('BusinessScheduler','幕1756953968575',1756954302632,20000);
INSERT INTO sys_custom_region (ID,USER_ACCOUNT,MENU_NAME,MENU_ROUTER,SORT_NUM,CREATE_BY,CREATE_BY_NAME,CREATE_TIME,UPDATE_BY,UPDATE_BY_NAME,UPDATE_TIME,MENU_ID,USER_ID) VALUES
	 (1950801562947424256,'wfkjgs511','登录日志','',NULL,1,'超级管理员','2025-07-31 14:11:51',NULL,'',NULL,1673914307110375424,1),
	 (1953264117997506560,'wfkjgs168','作业通知单','',NULL,1947580305313501184,'系统管理员','2025-08-07 09:17:10',NULL,'',NULL,1675696068857303040,1947580305313501184),
	 (1953264179616026624,'wfkjgs168','进口交接清单','',NULL,1947580305313501184,'系统管理员','2025-08-07 09:17:24',NULL,'',NULL,1826423080470319104,1947580305313501184),
	 (1953264227888271360,'wfkjgs168','船舶预报','',NULL,1947580305313501184,'系统管理员','2025-08-07 09:17:36',NULL,'',NULL,1675696670014312448,1947580305313501184),
	 (1953264236776001536,'wfkjgs168','船舶动态','',NULL,1947580305313501184,'系统管理员','2025-08-07 09:17:38',NULL,'',NULL,1668092432824274944,1947580305313501184),
	 (1953264252362035200,'wfkjgs168','作业计划','',NULL,1947580305313501184,'系统管理员','2025-08-07 09:17:42',NULL,'',NULL,1683296281491017728,1947580305313501184),
	 (1953264260247326720,'wfkjgs168','二次配工','',NULL,1947580305313501184,'系统管理员','2025-08-07 09:17:44',NULL,'',NULL,1685826564085911552,1947580305313501184),
	 (1953264317923201024,'wfkjgs168','客户预缴','',NULL,1947580305313501184,'系统管理员','2025-08-07 09:17:57',NULL,'',NULL,1702127968039604224,1947580305313501184),
	 (1953264369932570624,'wfkjgs168','港存动态','',NULL,1947580305313501184,'系统管理员','2025-08-07 09:18:10',NULL,'',NULL,1668166754679001088,1947580305313501184),
	 (1953264501537247232,'wfkjgs168','杂项计费','',NULL,1947580305313501184,'系统管理员','2025-08-07 09:18:41',NULL,'',NULL,1702518475077062656,1947580305313501184);
INSERT INTO sys_custom_region (ID,USER_ACCOUNT,MENU_NAME,MENU_ROUTER,SORT_NUM,CREATE_BY,CREATE_BY_NAME,CREATE_TIME,UPDATE_BY,UPDATE_BY_NAME,UPDATE_TIME,MENU_ID,USER_ID) VALUES
	 (1957692086232944640,'superme','角色管理','',NULL,1,'超级管理员','2025-08-19 14:32:20',NULL,'',NULL,101,1),
	 (1957692142008799232,'superme','客户管理','',NULL,1,'超级管理员','2025-08-19 14:32:33',NULL,'',NULL,1674289755199770624,1),
	 (1957692147142627328,'superme','合同管理','',NULL,1,'超级管理员','2025-08-19 14:32:34',NULL,'',NULL,1674275446163902464,1),
	 (1958350823973064704,'superme','用户管理','',NULL,1,'超级管理员','2025-08-21 10:09:55',NULL,'',NULL,100,1),
	 (1960148616282771456,'superme','字典管理','',NULL,1,'超级管理员','2025-08-26 09:13:42',NULL,'',NULL,1650782134576746496,1),
	 (1960148628920209408,'superme','泊位管理','',NULL,1,'超级管理员','2025-08-26 09:13:45',NULL,'',NULL,1665594162239639552,1),
	 (1960945249719291904,'superme','作业通知单','',NULL,1,'超级管理员','2025-08-28 13:59:14',NULL,'',NULL,1675696068857303040,1);
INSERT INTO sys_dept (DEPT_ID,PARENT_ID,DEPT_CODE,DEPT_NAME,ORDER_NO,REMARK,PARENT_IDS,CHIEF,DESCR,DEPT_LEVEL,DEPT_TYPE,DEPT_NO,PARENT_DEPT_CODE,CREATE_BY,CREATE_BY_NAME,CREATE_TIME,UPDATE_BY,UPDATE_BY_NAME,UPDATE_TIME,STATUS,ID,IS_LABOR,IS_MACHINE,IS_PROJECT,IN_OUT_TYPE,IS_WORK_COMPANY,IS_TALLY_COMPANY,IS_TALLY_DEPT,CAN_DISPATCH_DEPT,IS_SALARY_BY_PROCESS,TICKET_LEVEL) VALUES
	 (NULL,1677242790846795776,'HZZX','货主自卸',99,'','0,9999999999,1677242790846795776','货主','',2,NULL,'000100010007','',1,'超级管理员','0001-01-01 14:06:25.972000',1,'超级管理员','0001-01-01 09:05:35.212000','1',1725757333449084928,'0','1','0','O','','','','','',''),
	 (NULL,9999999999,'5','集装箱码头有限公司',3,'','0,9999999999','12111','',1,NULL,'00010018','',1,'超级管理员','0001-01-01 08:45:11.551000',1,'超级管理员','2025-07-28 11:41:00.758000','1',1746694986872786944,'','','','I','1','1','','','',''),
	 (NULL,1746694986872786944,'1','集装箱场站',0,'','0,9999999999,1746694986872786944','123','',2,NULL,'000100180001','',1,'超级管理员','0001-01-01 08:46:29.769000',NULL,'',NULL,'1',1746695314942857216,'1','1','0','I','','','','','',''),
	 (NULL,1677242790846795776,'安全环保科技部','安全环保科技部',0,'','0,9999999999,1677242790846795776','李振华','',2,NULL,'000100010008','',1,'超级管理员','0001-01-01 16:29:21.091000',NULL,'',NULL,'1',1752972390025007104,'0','0','0','I','','','','','',''),
	 (NULL,1677242790846795776,'公司领导','公司领导',0,'','0,9999999999,1677242790846795776','孙超','',2,NULL,'000100010010','',1,'超级管理员','0001-01-01 09:07:47.742000',1,'超级管理员','0001-01-01 09:28:13.485000','1',1794173485476614144,'0','0','0','I','0','0','','','',''),
	 (NULL,9999999999,'WBHQWL','外包单位2',27,'','0,9999999999','海清物流','',1,NULL,'00010019','',1,'超级管理员','0001-01-01 09:30:11.704000',1,'超级管理员','2025-07-28 13:33:38.965000','1',1844551035826343936,'','','','O','0','1','','','',''),
	 (NULL,1710197535592812544,'MPMJB','木片门机队',0,'','0,9999999999,1677242790846795776,1710197535592812544','1','',4,NULL,'0001000100050004','',1,'超级管理员','0001-01-01 18:16:54.382000',1,'超级管理员','0001-01-01 18:18:44.976000','1',1750100354164461568,'','','','I','','','','','',''),
	 (NULL,1710197535592812544,'JXDD','机械大队门机队',0,'','0,9999999999,1677242790846795776,1710197535592812544','1','',4,NULL,'0001000100050005','',1,'超级管理员','0001-01-01 18:17:31.394000',1,'超级管理员','0001-01-01 18:18:12.853000','1',1750100509404041216,'','','','I','','','','','',''),
	 (NULL,1677242790846795776,'开票申请','开票申请',0,'','0,9999999999,1677242790846795776','侯伟','',2,NULL,'000100010009','',1,'超级管理员','0001-01-01 16:45:01.725000',NULL,'',NULL,'1',1752976335329955840,'0','0','0','I','','','','','',''),
	 (9999999999,0,'1','港口集团',1,'','0','131231','',0,NULL,'0001','',NULL,'',NULL,1,'超级管理员','2025-07-28 11:40:02.486000','1',9999999999,'','','','I','','','0','','','');
INSERT INTO sys_dept (DEPT_ID,PARENT_ID,DEPT_CODE,DEPT_NAME,ORDER_NO,REMARK,PARENT_IDS,CHIEF,DESCR,DEPT_LEVEL,DEPT_TYPE,DEPT_NO,PARENT_DEPT_CODE,CREATE_BY,CREATE_BY_NAME,CREATE_TIME,UPDATE_BY,UPDATE_BY_NAME,UPDATE_TIME,STATUS,ID,IS_LABOR,IS_MACHINE,IS_PROJECT,IN_OUT_TYPE,IS_WORK_COMPANY,IS_TALLY_COMPANY,IS_TALLY_DEPT,CAN_DISPATCH_DEPT,IS_SALARY_BY_PROCESS,TICKET_LEVEL) VALUES
	 (NULL,1677243491865989120,'调度1班','调度1班',0,'','0,9999999999,1677242790846795776,1677243491865989120','测试','',4,NULL,'0001000100020001','',1,'超级管理员','0001-01-01 15:50:41.490000',1,'超级管理员','0001-01-01 19:23:48.370000','1',1702228359729254400,'1','0','1','I','','','0','','',''),
	 (NULL,9999999999,'DGZYGS','散货码头有限公司',1,'','0,9999999999','负责人','',1,NULL,'00010001','',1,'超级管理员','0001-01-01 17:06:57.660000',1,'超级管理员','2025-07-28 11:40:29.225000','1',1677242790846795776,'','','','I','1','1','0','','',''),
	 (NULL,9999999999,'XGZYGS','西作业区码头有限公司',2,'','0,9999999999','负责人','',1,NULL,'00010002','',1,'超级管理员','0001-01-01 17:07:40.679000',1,'超级管理员','2025-07-28 11:40:47.861000','1',1677242971285753856,'','','','I','1','1','0','','',''),
	 (NULL,9999999999,'ZGZYGS','XXX港有限公司',0,'','0,9999999999','负责人','',1,NULL,'00010003','',1,'超级管理员','0001-01-01 17:08:11.213000',1,'超级管理员','2025-07-28 11:40:18.978000','1',1677243099354632192,'','','','I','1','1','0','','',''),
	 (NULL,1677242790846795776,'DD','调度室',0,'','0,9999999999,1677242790846795776','负责人','',2,NULL,'000100010002','',1,'超级管理员','0001-01-01 17:09:44.795000',1,'超级管理员','0001-01-01 21:03:51.071000','1',1677243491865989120,'0','0','1','I','','','1','','','1'),
	 (NULL,1677242790846795776,'KCD','库场队',0,'','0,9999999999,1677242790846795776','负责人','',2,NULL,'000100010003','',1,'超级管理员','0001-01-01 17:10:16.735000',1,'超级管理员','0001-01-01 21:04:04.748000','1',1677243625832058880,'0','0','0','I','','','1','','','1'),
	 (NULL,1677242790846795776,'LJD','流机队',0,'','0,9999999999,1677242790846795776','负责人','',2,NULL,'000100010006','',1,'超级管理员','0001-01-01 16:00:55.449000',1,'超级管理员','0001-01-01 21:44:11.541000','1',1710203467898949632,'0','1','0','I','','','0','00010010,00010011,00010012,00010013,00010014,00010015,00010016,000100010006,000100010007','1','2'),
	 (NULL,1677242790846795776,'装卸队','装卸队',1,'','0,9999999999,1677242790846795776','于佳','',2,NULL,'00010006','',1706505409818398720,'刘强','0001-01-01 14:22:28.444000',1,'超级管理员','0001-01-01 09:53:40.495000','1',1706554813472444416,'0','1','0','I','','','0','00010010,00010011,00010012,00010013,00010014,00010015,00010016,000100010006,000100010007','',''),
	 (NULL,9999999999,'JFZX','计费中心',5,'','0,9999999999','负责人','',2,NULL,'00010009','',1,'超级管理员','0001-01-01 10:16:43.022000',1,'超级管理员','0001-01-01 18:31:29.583000','1',1712291172539568128,'0','0','0','I','','','0','','',''),
	 (NULL,9999999999,'SCYXZX','市场营销中心',3,'','0,9999999999','郑毅恒','',2,NULL,'00010004','',1,'超级管理员','0001-01-01 11:14:59.721000',1,'超级管理员','0001-01-01 18:31:08.735000','1',1706507632908570624,'0','0','0','I','','','0','','','');
INSERT INTO sys_dept (DEPT_ID,PARENT_ID,DEPT_CODE,DEPT_NAME,ORDER_NO,REMARK,PARENT_IDS,CHIEF,DESCR,DEPT_LEVEL,DEPT_TYPE,DEPT_NO,PARENT_DEPT_CODE,CREATE_BY,CREATE_BY_NAME,CREATE_TIME,UPDATE_BY,UPDATE_BY_NAME,UPDATE_TIME,STATUS,ID,IS_LABOR,IS_MACHINE,IS_PROJECT,IN_OUT_TYPE,IS_WORK_COMPANY,IS_TALLY_COMPANY,IS_TALLY_DEPT,CAN_DISPATCH_DEPT,IS_SALARY_BY_PROCESS,TICKET_LEVEL) VALUES
	 (NULL,9999999999,'ZDDZX','总调度中心',4,'','0,9999999999','总调','',2,NULL,'00010005','',1,'超级管理员','0001-01-01 11:15:30.941000',1,'超级管理员','0001-01-01 18:31:17.944000','1',1706507763854741504,'0','0','0','I','','','0','','',''),
	 (NULL,1677242790846795776,'GJD','固机队',0,'','0,9999999999,1677242790846795776','负责人','',2,NULL,'000100010005','',1,'超级管理员','0001-01-01 15:37:21.079000',1,'超级管理员','0001-01-01 08:26:04.381000','1',1710197535592812544,'1','1','1','I','','','0','','','1'),
	 (NULL,9999999999,'CWB','财务部',6,'','0,9999999999','负责人','',2,NULL,'00010007','',1,'超级管理员','0001-01-01 21:04:28.029000',1,'超级管理员','0001-01-01 18:31:34.247000','1',1712091796328288256,'0','0','0','I','','','0','','',''),
	 (NULL,9999999999,'WBHS','外包单位1',21,'','0,9999999999','恒森','',1,NULL,'00010010','',1,'超级管理员','0001-01-01 18:30:27.103000',1,'超级管理员','2025-07-28 13:37:41.817000','1',1714589752134602752,'','','','O','0','0','0','','',''),
	 (NULL,1714595442093854720,'HSZXD3B','外包装卸3班',3,'','0,9999999999,1714589752134602752,1714595442093854720','恒森3班','',4,NULL,'0001001000010003','',1,'超级管理员','0001-01-01 19:10:12.833000',1,'超级管理员','2025-07-28 13:38:16.417000','1',1714599758611484672,'','','','O','','','0','','',''),
	 (NULL,1714595442093854720,'HSZXD4B','外包装卸4班',4,'','0,9999999999,1714589752134602752,1714595442093854720','恒森4班','',4,NULL,'0001001000010004','',1,'超级管理员','0001-01-01 19:10:41.866000',1,'超级管理员','2025-07-28 13:38:22.083000','1',1714599880384712704,'','','','O','','','0','','',''),
	 (NULL,1714595442093854720,'HSZXD5B','外包装卸5班',5,'','0,9999999999,1714589752134602752,1714595442093854720','恒森5班','',4,NULL,'0001001000010005','',1,'超级管理员','0001-01-01 19:10:56.067000',1,'超级管理员','2025-07-28 13:38:27','1',1714599939948023808,'','','','O','','','0','','',''),
	 (NULL,1714595442093854720,'HSZXD6B','外包装卸6班',6,'','0,9999999999,1714589752134602752,1714595442093854720','恒森6班','',4,NULL,'0001001000010006','',1,'超级管理员','0001-01-01 19:11:10.559000',1,'超级管理员','2025-07-28 13:38:32.057000','1',1714600000731877376,'','','','O','','','0','','',''),
	 (NULL,1677243491865989120,'DD2B','调度2班',2,'','0,9999999999,1677242790846795776,1677243491865989120','调度2班','',4,NULL,'0001000100020002','',1,'超级管理员','0001-01-01 23:24:34.552000',NULL,'',NULL,'1',1715026158766133248,'','','','I','','','0','','',''),
	 (NULL,1714595442093854720,'HSZXD1B','外包装卸1班',1,'','0,9999999999,1714589752134602752,1714595442093854720','恒森1班','',4,NULL,'0001001000010001','',1,'超级管理员','0001-01-01 19:09:41.339000',1,'超级管理员','2025-07-28 13:38:00.495000','1',1714599626516074496,'','','','O','','','0','','','');
INSERT INTO sys_dept (DEPT_ID,PARENT_ID,DEPT_CODE,DEPT_NAME,ORDER_NO,REMARK,PARENT_IDS,CHIEF,DESCR,DEPT_LEVEL,DEPT_TYPE,DEPT_NO,PARENT_DEPT_CODE,CREATE_BY,CREATE_BY_NAME,CREATE_TIME,UPDATE_BY,UPDATE_BY_NAME,UPDATE_TIME,STATUS,ID,IS_LABOR,IS_MACHINE,IS_PROJECT,IN_OUT_TYPE,IS_WORK_COMPANY,IS_TALLY_COMPANY,IS_TALLY_DEPT,CAN_DISPATCH_DEPT,IS_SALARY_BY_PROCESS,TICKET_LEVEL) VALUES
	 (NULL,1714595442093854720,'HSZXD2B','外包装卸2班',2,'','0,9999999999,1714589752134602752,1714595442093854720','恒森2班','',4,NULL,'0001001000010002','',1,'超级管理员','0001-01-01 19:09:59.288000',1,'超级管理员','2025-07-28 13:38:09.543000','1',1714599701799636992,'','','','O','','','0','','',''),
	 (NULL,1714589752134602752,'HSZXD','外包装卸队',1,'','0,9999999999,1714589752134602752','恒森装卸队','',2,NULL,'000100100001','',1,'超级管理员','0001-01-01 18:53:03.695000',1,'超级管理员','2025-07-28 13:37:49.716000','1',1714595442093854720,'1','0','0','O','','','0','','','2'),
	 (NULL,1677243491865989120,'DD3B','调度3班',3,'','0,9999999999,1677242790846795776,1677243491865989120','调度3班','',4,NULL,'0001000100020003','',1,'超级管理员','0001-01-01 23:25:00.466000',NULL,'',NULL,'1',1715026267457327104,'','','','I','','','0','','',''),
	 (NULL,1677243625832058880,'KC1B','库场1班',1,'','0,9999999999,1677242790846795776,1677243625832058880','库场1班','',4,NULL,'0001000100030001','',1,'超级管理员','0001-01-01 23:25:19.458000',NULL,'',NULL,'1',1715026347115548672,'','','','I','','','0','','',''),
	 (NULL,1677243625832058880,'KC2B','库场2班',2,'','0,9999999999,1677242790846795776,1677243625832058880','库场2班','',4,NULL,'0001000100030002','',1,'超级管理员','0001-01-01 23:25:38.745000',NULL,'',NULL,'1',1715026428011089920,'','','','I','','','0','','',''),
	 (NULL,1677243625832058880,'KC3B','库场3班',3,'','0,9999999999,1677242790846795776,1677243625832058880','库场3班','',4,NULL,'0001000100030003','',1,'超级管理员','0001-01-01 23:25:58.550000',NULL,'',NULL,'1',1715026511079280640,'','','','I','','','0','','',''),
	 (NULL,1710197535592812544,'MJ1B','门机1班',1,'','0,9999999999,1677242790846795776,1710197535592812544','门机1班','',4,NULL,'0001000100050001','',1,'超级管理员','0001-01-01 23:32:23.868000',NULL,'',NULL,'1',1715028127220109312,'','','','I','','','0','','',''),
	 (NULL,1710197535592812544,'MJ2B','门机2班',2,'','0,9999999999,1677242790846795776,1710197535592812544','门机2班','',4,NULL,'0001000100050002','',1,'超级管理员','0001-01-01 23:32:57.148000',NULL,'',NULL,'1',1715028266806546432,'','','','I','','','0','','',''),
	 (NULL,1710197535592812544,'MJ3B','门机3班',3,'','0,9999999999,1677242790846795776,1710197535592812544','门机3班','',4,NULL,'0001000100050003','',1,'超级管理员','0001-01-01 23:33:16.711000',NULL,'',NULL,'1',1715028348859715584,'','','','I','','','0','','',''),
	 (NULL,1710203467898949632,'LJ1B','装载机1班',1,'','0,9999999999,1677242790846795776,1710203467898949632','装载机1班','',4,NULL,'0001000100060001','',1,'超级管理员','0001-01-01 23:34:17.159000',1,'超级管理员','0001-01-01 14:52:13.888000','1',1715028602397003776,'','','','I','','','0','00010010,00010011,00010012,00010013,00010014,00010015,00010016,000100010006','','');
INSERT INTO sys_dept (DEPT_ID,PARENT_ID,DEPT_CODE,DEPT_NAME,ORDER_NO,REMARK,PARENT_IDS,CHIEF,DESCR,DEPT_LEVEL,DEPT_TYPE,DEPT_NO,PARENT_DEPT_CODE,CREATE_BY,CREATE_BY_NAME,CREATE_TIME,UPDATE_BY,UPDATE_BY_NAME,UPDATE_TIME,STATUS,ID,IS_LABOR,IS_MACHINE,IS_PROJECT,IN_OUT_TYPE,IS_WORK_COMPANY,IS_TALLY_COMPANY,IS_TALLY_DEPT,CAN_DISPATCH_DEPT,IS_SALARY_BY_PROCESS,TICKET_LEVEL) VALUES
	 (NULL,1710203467898949632,'LJ2B','装载机2班',2,'','0,9999999999,1677242790846795776,1710203467898949632','装载机2班','',4,NULL,'0001000100060002','',1,'超级管理员','0001-01-01 23:34:33.830000',1,'超级管理员','0001-01-01 14:52:31.373000','1',1715028672320245760,'','','','I','','','0','00010010,00010011,00010012,00010013,00010014,00010015,00010016,000100010006','',''),
	 (NULL,1710203467898949632,'LJ3B','装载机3班',3,'','0,9999999999,1677242790846795776,1710203467898949632','装载机3班','',4,NULL,'0001000100060003','',1,'超级管理员','0001-01-01 23:34:54.583000',1,'超级管理员','0001-01-01 14:52:56.945000','1',1715028759364636672,'','','','I','','','0','00010010,00010011,00010012,00010013,00010014,00010015,00010016,000100010006','',''),
	 (NULL,1710197535592812544,'GJDWXB','固机队维修班',0,'','0,9999999999,1677242790846795776,1710197535592812544','固机队维修班','',3,NULL,'0001000100050006','',1,'超级管理员','0001-01-01 14:27:39.911000',NULL,'',NULL,'1',1785194286694928384,'','','','I','','','','','',''),
	 (NULL,1710203467898949632,'YJBZ','应急班组',40,'','0,9999999999,1677242790846795776,1710203467898949632','应急班组','',4,NULL,'0001000100060004','',1,'超级管理员','0001-01-01 10:20:29.383000',1,'超级管理员','0001-01-01 10:20:41.487000','1',1922839471867432960,'','','','I','','','','','',NULL);
INSERT INTO sys_menu (ID,MENU_NAME,PARENT_ID,ORDER_NUM,`PATH`,COMPONENT,QUERY,IS_FRAME,IS_CACHE,MENU_TYPE,VISIBLE,STATUS,PERMS,ICON,CREATE_BY,CREATE_TIME,UPDATE_BY,UPDATE_TIME,REMARK,MENU_ICON_COLOR,DATA_TYPE,CREATE_BY_NAME,UPDATE_BY_NAME,ICON_APP,LINK) VALUES
	 (1,'系统管理',0,10,'/system','Layout','',1,0,'M','0','0','','系统管理',NULL,NULL,1,'2025-08-19 18:29:26.258000','','#f75e5e','PC','','超级管理员','',''),
	 (100,'用户管理',1,1,'user','system/user/index','',1,0,'C','0','0','system:user','#',NULL,NULL,1654326618765791232,'0001-01-01 11:35:52.093000','','','PC','','李嫚伶','',''),
	 (101,'角色管理',1,2,'role','system/role/index','',1,0,'C','0','0','system:role','#',NULL,NULL,1654326618765791232,'0001-01-01 11:49:30.872000','','','PC','','李嫚伶','',''),
	 (102,'菜单管理',1,3,'menu','system/menu/index','',1,0,'C','0','0','system:menu','#',NULL,NULL,1654326618765791232,'0001-01-01 11:49:20.508000','','','PC','','李嫚伶','',''),
	 (103,'部门管理',1,4,'dept','system/dept/index','',1,0,'C','0','0','system:dept','#',NULL,NULL,1654326618765791232,'0001-01-01 11:49:58.273000','','','PC','','李嫚伶','',''),
	 (107,'日志管理',1,12,'log','system/operLog/index','',1,0,'M','0','0','system:operlog:query','#',NULL,NULL,1,'0001-01-01 10:45:37.543000','','','PC','','超级管理员','',''),
	 (108,'在线用户管理',1,1,'online','system/online/index','',1,0,'C','0','0','system:online','#',NULL,NULL,1,'2025-08-22 16:44:59.470000','','','PC','','超级管理员','',''),
	 (120,'操作管理',107,1,'operLog','system/log/operLog/index','',1,0,'C','0','0','system:operlog:query','#',NULL,NULL,1,'0001-01-01 10:44:35.491000','','','PC','','超级管理员','',NULL),
	 (1007,'角色查询',101,1,'','','',1,0,'F','0','0','system:role:query','',1,'0001-01-01 10:10:02.165000',NULL,NULL,'','','PC','','','',''),
	 (1008,'角色新增',101,2,'','','',1,0,'F','0','0','system:role:insert','',1,'0001-01-01 10:11:00.302000',NULL,NULL,'','','PC','','','','');
INSERT INTO sys_menu (ID,MENU_NAME,PARENT_ID,ORDER_NUM,`PATH`,COMPONENT,QUERY,IS_FRAME,IS_CACHE,MENU_TYPE,VISIBLE,STATUS,PERMS,ICON,CREATE_BY,CREATE_TIME,UPDATE_BY,UPDATE_TIME,REMARK,MENU_ICON_COLOR,DATA_TYPE,CREATE_BY_NAME,UPDATE_BY_NAME,ICON_APP,LINK) VALUES
	 (1009,'角色修改',101,3,'','','',1,0,'F','0','0','system:role:update','',1,'0001-01-01 10:11:31.685000',NULL,NULL,'','','PC','','','',''),
	 (1010,'角色删除',101,4,'','','',1,0,'F','0','0','system:role:delete','',1,'0001-01-01 10:11:59.388000',NULL,NULL,'','','PC','','','',''),
	 (1012,'菜单查询',102,1,'','','',1,0,'F','0','0','system:menu:query','#',NULL,NULL,NULL,NULL,'','','PC','','','',''),
	 (1013,'菜单新增',102,2,'','','',1,0,'F','0','0','system:menu:insert','#',NULL,NULL,NULL,NULL,'','','PC','','','',''),
	 (1014,'菜单修改',102,3,'','','',1,0,'F','0','0','system:menu:update','#',NULL,NULL,NULL,NULL,'','','PC','','','',''),
	 (1015,'菜单删除',102,4,'','','',1,0,'F','0','0','system:menu:delete','#',NULL,NULL,NULL,NULL,'','','PC','','','',NULL),
	 (1016,'用户查询',100,1,'','','',1,0,'F','0','0','system:user:query','#',NULL,NULL,1,'0001-01-01 15:23:47.186000','','','PC','','','',NULL),
	 (1650673752989634560,'部门查询',103,1,'','','',1,0,'F','0','0','system:dept:query','',1,'0001-01-01 09:31:05.419000',1,'0001-01-01 09:32:12.795000','','','PC','','','',''),
	 (1650673967012384768,'部门新增',103,2,'','','',1,0,'F','0','0','system:dept:add','',1,'0001-01-01 09:31:56.432000',1,'0001-01-01 09:33:46.197000','','','PC','','','',NULL),
	 (1650674269715304448,'部门修改',103,3,'','','',1,0,'F','0','0','system:dept:update','',1,'0001-01-01 09:33:08.602000',NULL,NULL,'','','PC','','','','');
INSERT INTO sys_menu (ID,MENU_NAME,PARENT_ID,ORDER_NUM,`PATH`,COMPONENT,QUERY,IS_FRAME,IS_CACHE,MENU_TYPE,VISIBLE,STATUS,PERMS,ICON,CREATE_BY,CREATE_TIME,UPDATE_BY,UPDATE_TIME,REMARK,MENU_ICON_COLOR,DATA_TYPE,CREATE_BY_NAME,UPDATE_BY_NAME,ICON_APP,LINK) VALUES
	 (1650674376749748224,'部门删除',103,4,'','','',1,0,'F','0','0','system:dept:delete','',1,'0001-01-01 09:33:34.121000',NULL,NULL,'','','PC','','','',''),
	 (1650758221268389888,'用户新增',100,2,'','','',1,0,'F','0','0','system:user:add','',1,'0001-01-01 15:06:44.239000',1,'0001-01-01 11:43:35.315000','','','PC','','超级管理员','',''),
	 (1650758468912680960,'用户修改',100,3,'','','',1,0,'F','0','0','system:user:update','',1,'0001-01-01 15:07:43.255000',NULL,NULL,'','','PC','','','',''),
	 (1650758645090226176,'用户删除',100,4,'','','',1,0,'F','0','0','system:user:delete','',1,'0001-01-01 15:08:25.259000',NULL,NULL,'','','PC','','','',''),
	 (1650781794146062336,'基础数据',0,20,'/master','Layout','',1,0,'M','0','0','','基础数据',1,'0001-01-01 16:40:24.432000',1,'2025-08-22 16:08:57.035000','','#f75e5e','PC','','超级管理员','',NULL),
	 (1650782134576746496,'字典管理',1650781794146062336,10,'dict','master/dict/index','',1,0,'C','0','0','master:dict:list','',1,'0001-01-01 16:41:45.589000',1,'0001-01-01 21:30:38.771000','','','PC','','超级管理员','',NULL),
	 (1650793188639772672,'系统参数',1,7,'parameter','system/parameter/index','',1,0,'C','0','0','system:parameter:query','',1,'0001-01-01 17:25:41.090000',NULL,NULL,'','','PC','','','',''),
	 (1650794193620176896,'参数查询（刷新）',1650793188639772672,1,'','','',1,0,'F','0','0','system:parameter:query','',1,'0001-01-01 17:29:40.689000',1,'0001-01-01 11:49:20.943000','','','PC','','超级管理员','',''),
	 (1654709997994643456,'查询',108,1,'','','',1,0,'F','0','0','system:online:query','',1,'0001-01-01 12:49:41.206000',1674702805547487232,'0001-01-01 17:30:20.901000','','','PC','','戴莹','',''),
	 (1659475042725138432,'参数保存',1650793188639772672,1,'','','',1,0,'F','0','0','system:parameter:save','',1651420073635745792,'0001-01-01 16:24:16.400000',NULL,NULL,'','','PC','樊琪','','','');
INSERT INTO sys_menu (ID,MENU_NAME,PARENT_ID,ORDER_NUM,`PATH`,COMPONENT,QUERY,IS_FRAME,IS_CACHE,MENU_TYPE,VISIBLE,STATUS,PERMS,ICON,CREATE_BY,CREATE_TIME,UPDATE_BY,UPDATE_TIME,REMARK,MENU_ICON_COLOR,DATA_TYPE,CREATE_BY_NAME,UPDATE_BY_NAME,ICON_APP,LINK) VALUES
	 (1669602954426060800,'状态修改',100,6,'','','',1,0,'F','0','0','system:user:updateStatus','',1,'0001-01-01 15:08:58.716000',NULL,NULL,'','','PC','超级管理员','','',''),
	 (1669612529061597184,'重置密码',100,7,'','','',1,0,'F','0','0','system:user:resetPsd','',1,'0001-01-01 15:47:01.487000',1,'0001-01-01 15:47:14.341000','','','PC','超级管理员','超级管理员','',''),
	 (1673914307110375424,'登录日志',107,0,'loginLog','system/log/loginLog/index','',1,0,'C','0','0','system:loginLog:query','',1,'0001-01-01 12:40:45.296000',1,'0001-01-01 10:44:27.499000','','','PC','超级管理员','超级管理员','',''),
	 (1674712019447713792,'强退',108,2,'','','',1,0,'F','0','0','system:online:offline','',1674702805547487232,'0001-01-01 17:30:34.740000',NULL,NULL,'','','PC','戴莹','','',NULL),
	 (1686584629194264576,'分配用户',101,5,'','','',1,0,'F','0','0','system:role:dispatchUser','',1,'0001-01-01 11:48:05.484000',NULL,NULL,'','','PC','超级管理员','','',NULL),
	 (1713816596675432448,'版本管理',1,8,'version','system/version/index','',1,0,'C','0','0','system:version','#',1,'0001-01-01 15:18:12.478000',NULL,NULL,'','','PC','超级管理员','','',NULL),
	 (1713817315356839936,'版本查询',1713816596675432448,1,'','','',1,0,'F','0','0','system:version:query','',1,'0001-01-01 15:21:03.825000',NULL,NULL,'','','PC','超级管理员','','',''),
	 (1713817583268007936,'版本保存',1713816596675432448,2,'','','',1,0,'F','0','0','system:version:add','',1,'0001-01-01 15:22:07.699000',1,'0001-01-01 16:06:44.378000','','','PC','超级管理员','超级管理员','',''),
	 (1713817694215737344,'版本修改',1713816596675432448,3,'','','',1,0,'F','0','0','system:version:update','',1,'0001-01-01 15:22:34.151000',NULL,NULL,'','','PC','超级管理员','','',''),
	 (1713817773160927232,'版本删除',1713816596675432448,4,'','','',1,0,'F','0','0','system:version:delete','',1,'0001-01-01 15:22:52.973000',NULL,NULL,'','','PC','超级管理员','','',NULL);
INSERT INTO sys_menu (ID,MENU_NAME,PARENT_ID,ORDER_NUM,`PATH`,COMPONENT,QUERY,IS_FRAME,IS_CACHE,MENU_TYPE,VISIBLE,STATUS,PERMS,ICON,CREATE_BY,CREATE_TIME,UPDATE_BY,UPDATE_TIME,REMARK,MENU_ICON_COLOR,DATA_TYPE,CREATE_BY_NAME,UPDATE_BY_NAME,ICON_APP,LINK) VALUES
	 (1783656313134387200,'删除',1650793188639772672,3,'','','',1,0,'F','0','0','system:parameter:delete','',1,'0001-01-01 08:36:18.440000',NULL,NULL,'','','PC','超级管理员','','',NULL),
	 (1963114304710512640,'代码示例',0,3,'/example','Layout',NULL,1,0,'M','0','0',NULL,'component',1,'2025-09-03 13:38:17.805000',1,'2025-09-03 14:02:46.333000',NULL,'#5a59a6','PC','超级管理员','超级管理员',NULL,NULL),
	 (1963120134881153024,'工具类示例',1963114304710512640,0,'utilityClass','example/utilityClass/index',NULL,1,0,'C','0','0',NULL,NULL,1,'2025-09-03 14:01:27.821000',NULL,NULL,NULL,'#000000','PC','超级管理员',NULL,NULL,NULL),
	 (1963424682262794240,'打印示例',1963114304710512640,2,'hiprint','example/hiprint/index',NULL,1,0,'C','0','0',NULL,NULL,1,'2025-09-04 10:11:37.580000',NULL,NULL,NULL,'#000000','PC','超级管理员',NULL,NULL,NULL);
INSERT INTO sys_user (ID,USER_ACCOUNT,PASSWD,USER_NAME,USER_TYPE,DEPT_ID,EMAIL,STATUS,SORT_NUM,REMARK,PSD_UPD_DATE,SEX,TEL,MOBILE,ADDRESS,IS_SUPERADMIN,USER_SOURCE,POSTS,CREATE_BY,CREATE_BY_NAME,CREATE_TIME,UPDATE_BY,UPDATE_BY_NAME,UPDATE_TIME,IS_LABOR,POST_CODE,POST_NAME,UNIT_TYPE_CODE,UNIT_TYPE_NAME) VALUES
	 (1,'superme','lNz9Q+D/830j80iOIbI7Yw1chybDOcM4','超级管理员','00',9999999999,'',1,1673,'',NULL,NULL,'','','','1',1,'',NULL,'',NULL,1,'超级管理员','2025-08-06 14:21:11.857000','0','','','01','');






























