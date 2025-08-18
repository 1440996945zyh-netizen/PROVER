package com.yy.common.util.geojson;

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.geom.Geometry;

public class GeoJsonUtils {


    /**
     * 判断图像是否相交冲突
     * @param wktPolygon1
     * @param wktPolygon2
     * String wktPolygon1 = "POLYGON ((0 0, 4 0, 4 4, 0 4, 0 0))";
     * String wktPolygon2 = "POLYGON ((0 0, 4 0, 4 4, 0 4, 0 0))";
     * @return
     * @throws ParseException
     */
    public static boolean isConflict(String wktPolygon1,String wktPolygon2) throws ParseException {
        GeometryFactory geometryFactory = new GeometryFactory();
        WKTReader reader = new WKTReader(geometryFactory);
        Geometry polygon1 = reader.read(wktPolygon1);
        Geometry polygon2 = reader.read(wktPolygon2);
        return polygon1.intersects(polygon2);
    }

    /**
     * 判断图像是否相互覆盖
     * @param wktPolygon1
     * @param wktPolygon2
     * String wktPolygon1 = "POLYGON ((0 0, 4 0, 4 4, 0 4, 0 0))";
     * String wktPolygon2 = "POLYGON ((0 0, 4 0, 4 4, 0 4, 0 0))";
     * @return
     * @throws ParseException
     */
    public static boolean isOverlaps(String wktPolygon1,String wktPolygon2) throws ParseException {
        GeometryFactory geometryFactory = new GeometryFactory();
        WKTReader reader = new WKTReader(geometryFactory);
        Geometry polygon1 = reader.read(wktPolygon1);
        Geometry polygon2 = reader.read(wktPolygon2);
        return polygon1.overlaps(polygon2);
    }

}
