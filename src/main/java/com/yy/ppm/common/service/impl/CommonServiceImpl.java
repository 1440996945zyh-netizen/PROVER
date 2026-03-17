package com.yy.ppm.common.service.impl;

import com.yy.common.enums.CommonEnum;
import com.yy.common.log.MicroLogger;
import com.yy.common.util.DateUtils;
import com.yy.common.util.MessageUtils;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.bean.dto.CheckDTO;
import com.yy.ppm.common.enums.AutoNumEnum;
import com.yy.ppm.common.enums.SerialNumberPrefixEnum;
import com.yy.ppm.common.mapper.CommonMapper;
import com.yy.ppm.common.service.CommonService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

    @Resource
    private RedisTemplate<String, String> redisTemplate;


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


        int count = baseMapper.checkRepeat(tableName, conditionColNm, conditionColVal, id, keyValues);

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



        int count = baseMapper.isRepeat(tableName, conditionColNm, conditionColVal, id, keyValues);

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



    /**
     * 生成单号
     * 生成规则：前缀-日期(yyyyMMdd)-4位序列号（前面补0）
     * 格式示例：P0-20251212-0001
     *
     * @param prefix 单号前缀
     * @return 生成的单号
     */
    @Override
    public String generateSerialNumber(String prefix) {
        final String methodName = "generateSerialNumber";
        LOGGER.enter(methodName, "prefix:" + prefix);
        // 获取今天的日期，格式为 yyyyMMdd
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String key = prefix + today;
        Long count = redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, 24, TimeUnit.HOURS);
        // 将序列号格式化为4位数（前面补0）
        String serialNumber = String.format("%04d", count);
        // 最终单号：前缀-日期-四位数序列号
        String result = prefix + "-" + today + "-" + serialNumber;
        LOGGER.exit(methodName, "生成的单号:" + result);
        return result;
    }

    /**
     * 生成单号（使用枚举）
     * 生成规则：前缀-日期(yyyyMMdd)-4位序列号（前面补0）
     * 格式示例：PO-20251212-0001
     *
     * @param prefixEnum 单号前缀枚举
     * @return 生成的单号
     */
    @Override
    public String generateSerialNumber(SerialNumberPrefixEnum prefixEnum) {
        if (prefixEnum == null) {
            throw new IllegalArgumentException("单号前缀枚举不能为空");
        }
        return generateSerialNumber(prefixEnum.getCode());
    }


}
