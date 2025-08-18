package com.yy.ppm.business.service.impl;

import com.google.common.collect.Lists;
import com.yy.common.condition.EnvironmentCondition;
import com.yy.common.util.SpringContextUtils;
import com.yy.common.util.SpringUtils;
import com.yy.ppm.business.bean.dto.TrustStopLogRes;
import com.yy.ppm.business.mapper.ScheduledForCloseTrustMapper;
import com.yy.ppm.business.mapper.TBusTrustMapper;
import com.yy.ppm.business.service.ScheduledForCloseTrustService;
import com.yy.ppm.produce.bean.po.TPrdSalaryPO;
import com.yy.ppm.produce.mapper.TPrdSalaryMapper;
import com.yy.ppm.statement.bean.dto.storageFee.TBusCargoInfoDTO;
import com.yy.ppm.system.bean.dto.SysParameterDTO;
import com.yy.ppm.system.mapper.SysParameterMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Component
@Conditional(EnvironmentCondition.class)
@Slf4j
public class ScheduledForCloseTrustServiceImpl implements ScheduledForCloseTrustService {

    @Resource
    ScheduledForCloseTrustMapper mapper;
    @Resource
    private TBusTrustMapper tBusTrustMapper;
    @Resource
    private SysParameterMapper sysParameterMapper;
    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    @Getter
    enum statusEnum {

        _1("1", "是"),

        _0("0", "否");

        private final String code;

        private final String remark;

        statusEnum(String code, String remark) {
            this.code = code;
            this.remark = remark;
        }
    }

    @Override
    @Scheduled(cron = "0 30 8 * * *")
    public void closeChaoQiShuGangTrust() {
        log.info("定时任务自动关闭超期对存费的疏港计划-----------------开始----------------");
//        IS_OPEN_SCH_CLOSE_SHUGANG
        SysParameterDTO ship_adjust_check = sysParameterMapper.getByKey("IS_OPEN_SCH_CLOSE_SHUGANG");
        if("Y".equals(ship_adjust_check.getParamVal())){
            List<Map<String,Object>> toStopList =mapper.getCargoInfo();
            List<Long> collect = toStopList.stream().map(o -> Long.valueOf(String.valueOf(o.get("trust_cargo_id")))).collect(Collectors.toList());
           if(CollectionUtils.isEmpty(toStopList)){
               log.error("定时任务关闭集疏港失败 没有要操作的数据");
               return;
           }
            ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            try {
                SqlSessionFactory sqlSessionFactory = sqlSessionTemplate.getSqlSessionFactory();
                SqlSession sqlSession = sqlSessionFactory.openSession();
                Connection connection = sqlSession.getConnection();
                try{
                    //3、设置手动提交
                    connection.setAutoCommit(false);
                    //4、获取Mapper
                    ScheduledForCloseTrustMapper mapper = sqlSession.getMapper(ScheduledForCloseTrustMapper.class);
                    //5、将传入List中的数据按200一组均分
                    List<List<Long>> lists = splitList(collect,100);
                    //6、新建任务列表
                    List<Callable<Integer>> callableList = new ArrayList<>();
                    //7、根据均分的数据分别新建Callable任务
                    lists.forEach(updateList -> {
                        Callable<Integer> callable = new Callable<Integer>() {
                            @Override
                            public Integer call() throws Exception {
                                try{
                                    log.info("自动关闭疏港计划{}", updateList.stream().map(Object::toString).collect(Collectors.joining(",")));
                                    ArrayList<TrustStopLogRes> trustStopLogRes = new ArrayList<>();
                                    updateList.forEach(stopItem->{
                                        trustStopLogRes.add(SpringUtils.getBean(TBusTrustServiceImpl.class) .createTrustStopLogDto(stopItem,"20","计划关闭","免堆存期超期，系统自动关闭","dsrewu"));
                                    });
                                    tBusTrustMapper.insertStopLog(trustStopLogRes);
                                    mapper.trustCargoSetStop(updateList);
                                }catch (Exception e){
                                    log.error("错误信息{}",e.getMessage());
                                    //插入失败返回
                                    return 0;
                                }
                                //插入成功返回成功提交数
                                return 1;
                            }
                        };
                        callableList.add(callable);
                    });

                    //8、任务放入线程池开始执行
                    List<Future<Integer>> futures = executorService.invokeAll(callableList);
                    //9、对比每个任务的返回值 <= 0 代表执行失败
                    for(Future<Integer> future : futures){
                        if(future.get() <= 0){
                            //12、只要有一组任务失败回滚整个connection
                            connection.rollback();

                            log.error("定时任务关闭集疏港失败子任务执行失败");
                            return;
                        }
                    }
                    //10、主线程和子线程都执行成功 直接提交
                    connection.commit();
                }catch (Exception e){
                    //11、主线程报错回滚
                    try {
                        connection.rollback();
                    } catch (SQLException ex) {
                        log.error("定时任务关闭集疏港失败"+ex.getMessage());
                    }
                    log.error("定时任务关闭集疏港失败"+e.getMessage());
                    //throw new BusinessRuntimeException("出现异常！");
                } finally {
                    executorService.shutdown();
                }
            }catch (Exception e){
                log.error("定时任务关闭集疏港失败"+e.getMessage());
            }
        }else{
            log.error("定时任务关闭集疏港失败 开关状态"+ship_adjust_check.getParamVal());
        }
        log.info("定时任务自动关闭超期对存费的疏港计划-----------------结束----------------");


    }
    private <T> List<List<T>> splitList(List<T> list, int len) {
        if (list == null || list.size() == 0 || len < 1) {
            return Lists.newArrayList();
        }
        List<List<T>> result = Lists.newArrayList();
        int size = list.size();
        int count = (size + len - 1) / len;
        for (int i = 0; i < count; i++) {
            List<T> subList = list.subList(i * len, ((i + 1) * len > size ? size : len * (i + 1)));
            result.add(subList);
        }
        return result;
    }
}
