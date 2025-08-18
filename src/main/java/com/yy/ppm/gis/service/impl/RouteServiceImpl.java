package com.yy.ppm.gis.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.ppm.gis.dto.route.TRoutesDTO;
import com.yy.ppm.gis.po.TKeypointsPO;
import com.yy.ppm.gis.po.TRoutesPO;
import com.yy.ppm.gis.mapper.RouteMapper;

import com.yy.ppm.gis.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 路线维护Service业务层处理
 */
@RequiredArgsConstructor
@Service
public class RouteServiceImpl implements RouteService {

    @Resource
    RouteMapper routeMapper;

    @Autowired
    private Snowflake snowflake;
    /**
     * 关键点录入
     */
    @Override
    public void addKeyPoint(TKeypointsPO point) {
        point.setPointId(snowflake.nextId());

        routeMapper.insertKeyPoint(point);
    }

    /**
     * 关键点查询
     * @param id 关键点主键ID
     */
    @Override
    public List<TKeypointsPO> listPoints(Long id) {
        return routeMapper.getAllKeyPoints(id,null);
    }

    /**
     * 修改关键点
     */
    @Override
    public void editKeyPoint(TKeypointsPO point) {

        routeMapper.updateKeyPoint(point);
    }

    /**
     * 删除关键点
     */
    @Override
    @Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
    public void deleteKeyPoint(Long id) {
        routeMapper.deleteRouteByPointId(id);

        routeMapper.deleteSysKeyPoint(id);
    }

    /**
     * 路线信息录入
     */
    @Override
    public void addRouteInfo(TRoutesPO route) {
        routeMapper.insertRouteInfo(route);
    }

    /**
     * 路线信息查询
     */
    @Override
    public List<TRoutesDTO> getAllRouteInfo(Long routeId) {
        return routeMapper.getAllRoutes(routeId);
    }

    /**
     * 路线信息修改
     */
    @Override
    public void editRouteInfo(TRoutesPO route) {

        routeMapper.updateRouteInfo(route);
    }

    /**
     * 路线信息删除
     */
    @Override
    public void deleteRouteInfo(Long id) {
        routeMapper.deleteRouteById(id);
    }

    /**
     * 左右路径生成
     */
    @Override
    @Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
    public void generateNavigationRoute() {
        //路线信息
        HashMap<String, HashMap<String, String>> LxxxMap = new HashMap<>();
        //关键点信息
        HashMap<String, HashMap<String, String>> LkxxMap = new HashMap<>();
        //数组长度
        int shallLength = 1;
        //关键点id集合
        List<Integer> pointIdList = null;

        /*
         * Floyd - Warshall（弗洛伊德算法）
         */
        FloydWarShall shall;

        //先删除原有的所有最优路线
        routeMapper.delNavigationRoute();

        //获取所有状态为在用的关键点(并且 TYPE != 3 ：1路口 2入口 3出口 )
        HashMap parameterMap = new HashMap();
        parameterMap.put("status", 1);
        List<HashMap> keypointList = routeMapper.getKeyPointList(parameterMap);
        for (int i = 0; i < keypointList.size(); i++) {
            //获取某一个点
            HashMap pointMap  = keypointList.get(i);
            //获取该点ID对应的值
            String id = pointMap.get("POINT_ID").toString();
            //取最大的关键点ID，作为数组长度 id是自增的
            if(i == 0){
                shallLength = Integer.parseInt(id);
            }
            LkxxMap.put(id, pointMap);
        }

        //获取路线
        List<HashMap> routeList = routeMapper.getRouteList();
        for (int i = 0; i < routeList.size(); i++) {
            HashMap routeMap  = routeList.get(i);
            String id = routeMap.get("ID").toString();
            LxxxMap.put(id, routeMap);
        }

        pointIdList = new ArrayList<>();
        Object obj;
        //Map.keySet(): 获取Map集合的所有键名
        for(String key : LkxxMap.keySet()){
            HashMap<String, String> LkxxMapA = LkxxMap.get(key);
            obj = LkxxMapA.get("POINT_ID");
            pointIdList.add(Integer.valueOf(obj.toString()));
        }

        int qdxh = 0;//起点
        int zdxh = 0;//终点
        int index = 0;

        List<HashMap> list = new ArrayList<>();
        HashMap newMap;
        //开始遍历计算任意两点之间的最短路径
        for (int a = 0;a < keypointList.size();a++) {
            //起点
            qdxh = Integer.parseInt(keypointList.get(a).get("POINT_ID").toString());
            for (int b = 0; b < keypointList.size();b++) {
                //终点
                zdxh = Integer.parseInt(keypointList.get(b).get("POINT_ID").toString());
                //是同一个点,则跳过
                if (qdxh == zdxh) {
                    continue;
                }
                //v:顶点的个数
                //shallLength:数组长度
                shall = new FloydWarShall(shallLength+2);

                //获取路线的起始点坐标以及对应的经纬度
                for(String key : LxxxMap.keySet()){
                    HashMap<String, String> LxxxMapA = LxxxMap.get(key);
                    obj = LxxxMapA.get("BEGINPID");
                    int fpoint = Integer.valueOf(obj.toString());

                    obj = LxxxMapA.get("ENDPID");
                    int tpoint = Integer.valueOf(obj.toString());

                    obj = LxxxMapA.get("LXLX");
                    int lxlx = Integer.valueOf(obj.toString());

                    HashMap<String, String> LkxxMapB = LkxxMap.get(fpoint+"");
                    HashMap<String, String> LkxxMapC = LkxxMap.get(tpoint+"");
                    //起点的经纬度
                    obj = LkxxMapB.get("LONGITUDE");
                    double lon1 = Double.valueOf(obj.toString());
                    obj = LkxxMapB.get("LATITUDE");
                    double lat1 = Double.valueOf(obj.toString());
                    //终点的经纬度
                    obj = LkxxMapC.get("LONGITUDE");
                    double lon2 = Double.valueOf(obj.toString());
                    obj = LkxxMapC.get("LATITUDE");
                    double lat2 = Double.valueOf(obj.toString());

                    //初始化所有的道路信息
                    DecimalFormat df = new DecimalFormat("#");
                    //根据坐标计算两点的距离(单位：米)
                    int distance = Integer.parseInt(df.format(getDistance(lon1,lat1,lon2,lat2)));
                    //添加边(起点、终点、两点间的距离、单双行)
                    shall.addEdge(fpoint, tpoint, distance, lxlx);
                }

                //查询是否存在中间点使两点的距离更短
                shall.findShortestPath(pointIdList);

                //打印a->b最短路径
                String shortestLine = shall.getShortestPath(qdxh, zdxh);
                //获取a->b最短路径的距离
                int distance = (int)Math.round(shall.distTo(qdxh,zdxh));

                //将不连通的点，即距离为999999的数据筛除
                if(distance != 999999){
                    newMap = new HashMap();
                    newMap.put("qdxh", qdxh);
                    newMap.put("zdxh", zdxh);
                    newMap.put("routes", shortestLine);
                    newMap.put("distance", distance);
                    list.add(newMap);
                    index++;
                    if (index > 100) {
                        routeMapper.addNavigationRoute(list);
                        list.clear();
                        list = new ArrayList<>();
                        index = 0;
                    }
                    if(b == keypointList.size()-1){
                        routeMapper.addNavigationRoute(list);
                        list.clear();
                        list = new ArrayList<>();
                        index = 0;
                    }
                }
            }
        }

        routeMapper.addNavigationRoute(list);
        //清空
        list.clear();
        LxxxMap.clear();
        LkxxMap.clear();
    }

    public double getDistance(double var0, double var2, double var4, double var6) {
        double var8 = var2 / 180.0 * Math.PI;
        double var10 = var0 / 180.0 * Math.PI;
        double var12 = var6 / 180.0 * Math.PI;
        double var14 = var4 / 180.0 * Math.PI;
        return Math.acos(Math.sin(var8) * Math.sin(var12) + Math.cos(var8) * Math.cos(var12) * Math.cos(var14 - var10)) * 6370693.485653058;
    }
}
