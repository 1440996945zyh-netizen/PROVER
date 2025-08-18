package com.yy.ppm.gis.service;

import com.yy.ppm.gis.dto.onSiteDynamics.*;
import com.yy.ppm.runpile.bean.po.MStorageStackPositionPO;

import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author linqi
 * @Description
 * @Date 2023-06-06 17:18
 */
public interface OnSiteDynamicsService {

    List<Ship> listShip();

    List<Stack> listStack();

    Map<String, Object> getStack(Long id);

	Map<String, Object> getStackForMacApp(Long id);

    List<Car> listCar();

    List<CarHistory> listCarHistory(String macId, Date beginTime, @NotNull Date endTime);

    void insertStorageStackPosition(MStorageStackPositionPO storageStackPosition);

    void deleteStorageStackPosition(Long stackId);

    Map<String, Object> getInoutDetail(InoutDetailQueryDTO query);

	List<Stack> listStackForMac();

    List<Pile> listPile();
}
