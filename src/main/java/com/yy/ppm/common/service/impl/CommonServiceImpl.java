package com.yy.ppm.common.service.impl;

import com.yy.common.enums.CommonEnum;
import com.yy.common.log.MicroLogger;
import com.yy.common.util.DateUtils;
import com.yy.common.util.MessageUtils;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.bean.dto.CheckDTO;
import com.yy.ppm.common.enums.AutoNumEnum;
import com.yy.ppm.common.mapper.CommonMapper;
import com.yy.ppm.common.service.CommonService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class CommonServiceImpl implements CommonService {
    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(CommonServiceImpl.class);

    @Resource
    private CommonMapper baseMapper;

    @Resource
    private MessageUtils messageUtils;

    @Override
    public int delete(String tableName, String columnName, String columnValue) {
        final String methodName = "delete";
        LOGGER.enter(methodName, "业务执行");

        int num = baseMapper.delete(tableName, columnName, columnValue);

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return num;
    }

    @Override
    public int deleteAll(String tableName) {
        final String methodName = "deleteAll";
        LOGGER.enter(methodName, "业务执行");

        int num = baseMapper.deleteAll(tableName);

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return num;
    }

    /**
     * 根据Gid删除
     * @param tableName
     * @param id
     * @return
     */
    @Override
    public int deleteById(String tableName, Long id) {
        final String methodName = "deleteByGid";
        LOGGER.enter(methodName, "业务执行");

        int num = baseMapper.deleteById(tableName, id);

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return num;
    }

    /**
     * 根据多个Gid删除
     * @param tableName
     * @param ids
     * @return
     */
    @Override
    public int deleteByIds(String tableName, List<Long> ids){
        final String methodName = "deleteByIds";
        LOGGER.enter(methodName, "业务执行");

        int num = baseMapper.deleteByIds(tableName, ids);

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return num;
    }

    /**
     * 验证简单的重复字段
     * @param tableName
     * @param conditionColNm
     * @param conditionColVal
     * @param id
     * @param errorKeyWord
     * @param otherCondition
     * @return
     */
    @Override
    public boolean isRepeate(String tableName, String conditionColNm, String conditionColVal, String id, String errorKeyWord, List<CheckDTO> keyValues, String... otherCondition) {
        final String methodName = "isRepeate";

        LOGGER.enter(methodName, "业务执行");

        // 自定义msg
        String selfMsg = (otherCondition != null && otherCondition.length == 1) ? otherCondition[0] : null;

        // 是否显示msg
        String showMsgFlag = (otherCondition != null && otherCondition.length == 2) ? otherCondition[1] : null;

        int isDelFlagExists = baseMapper.isDelFlagExists(tableName);

        int count = baseMapper.checkRepeat(tableName, conditionColNm, conditionColVal, id, keyValues, isDelFlagExists);

        if(count > 0) {

            if ("0".equals(showMsgFlag)) {
                return true;
            }

            String errorMsg = selfMsg;

            if (StringUtil.isEmpty(selfMsg)) {
                errorMsg = messageUtils.getMessage("yy.message.common.error.repeate", new Object[] {errorKeyWord});
            }

            throw new BusinessRuntimeException(errorMsg);
        }
        LOGGER.exit(methodName, StringUtils.EMPTY);
        return false;
    }

    @Override
    public boolean isRepeat(String tableName, String conditionColNm, String conditionColVal, String id, String errorKeyWord, List<CheckDTO> keyValues, String... otherCondition) {
        final String methodName = "isRepeate";

        LOGGER.enter(methodName, "业务执行");

        // 自定义msg
        String selfMsg = (otherCondition != null && otherCondition.length == 1) ? otherCondition[0] : null;

        // 是否显示msg
        String showMsgFlag = (otherCondition != null && otherCondition.length == 2) ? otherCondition[1] : null;

        int isDelFlagExists = baseMapper.isDelFlagExists(tableName);

        int count = baseMapper.isRepeat(tableName, conditionColNm, conditionColVal, id, keyValues, isDelFlagExists);

        if(count > 0) {

            if ("0".equals(showMsgFlag)) {
                return true;
            }

            String errorMsg = selfMsg;

            if (StringUtil.isEmpty(selfMsg)) {
                errorMsg = messageUtils.getMessage("yy.message.common.error.repeate", new Object[] {errorKeyWord});
            }

            throw new BusinessRuntimeException(errorMsg);
        }
        LOGGER.exit(methodName, StringUtils.EMPTY);
        return false;
    }


    @Override
    public int getNextValue(String tableName, String columnName,String otherCondition) {
        final String methodName = "getNextValue";
        LOGGER.enter(methodName, "业务执行");

        int num = baseMapper.getNextValue(tableName,columnName,otherCondition);

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return num;
    }

    @Override
    public String getAutoNum(AutoNumEnum.BusinessAutoEnum businessType, String parentCodeVal){

        Map<String, Object> params = AutoNumEnum.BusinessAutoEnum.getMapByCode(businessType.getCode());

        // 前缀like,前方一致
        String like = "";

        // 动态传值的，例如部门
        if ("动态传值".equals(params.get("startWidth"))) {
            like = parentCodeVal;
            parentCodeVal = "";
            // 新号的长度 = 父级的号长度 + 固定值
            params.put("newCodeLength", like.length() + Integer.parseInt(params.get("codeLength").toString()));
        } else {
            if (!StringUtil.isEmpty(StringUtil.getString(params.get("startWidth")))) {
                like += StringUtil.getString(params.get("startWidth"));
            }
            if (!StringUtil.isEmpty(StringUtil.getString(params.get("ymd")))) {
                if ("y".equals(StringUtil.getString(params.get("ymd")))) {
                    like += DateUtils.formatDate(new Date(), CommonEnum.DateFormatType.E_4.getCode());
                } else if ("ym".equals(StringUtil.getString(params.get("ymd")))) {
                    like += DateUtils.formatDate(new Date(), CommonEnum.DateFormatType.E_21.getCode());
                } else if ("ymd".equals(StringUtil.getString(params.get("ymd")))) {
                    like += DateUtils.formatDate(new Date(), CommonEnum.DateFormatType.E_11.getCode());
                }else if("yymd".equals(StringUtil.getString(params.get("ymd")))){
                    like += DateUtils.formatDate(new Date(), CommonEnum.DateFormatType.E_92.getCode());
                }else if("yymm".equals(StringUtil.getString(params.get("ymd")))){
                    like += DateUtils.formatDate(new Date(), CommonEnum.DateFormatType.E_93.getCode());
                }else if("yy".equals(StringUtil.getString(params.get("ymd")))){
                    like += DateUtils.formatDate(new Date(), CommonEnum.DateFormatType.E_94.getCode());
                }
            }
        }

        params.put("like", like);
        // 父字段的值
        params.put("parentCodeVal", parentCodeVal);

        return baseMapper.getAutoNum(params);

    }

}
