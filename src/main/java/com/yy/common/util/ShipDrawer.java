package com.yy.common.util;

import cn.hutool.core.bean.BeanUtil;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @Author linqi
 * @Description 画船
 * @Date 2020/12/29 10:20
 */
@Setter
public class ShipDrawer {

    /**
     * 首揽经度
     */
    private Double beginBollardLon;

    /**
     * 首揽纬度
     */
    private Double beginBollardLat;

    /**
     * 尾揽经度
     */
    private Double endBollardLon;

    /**
     * 尾揽纬度
     */
    private Double endBollardLat;

    /**
     * 舷型 10左舷/20右舷
     */
    private String gunwale;

    /**
     * 船宽，单位米
     */
    private Double width;

    private ShipDrawer() {
    }

    public static ShipDrawerBuilder newBuilder() {
        return new ShipDrawerBuilder();
    }

    @Getter
    public static class ShipDrawerBuilder {

        /**
         * 首揽经度
         */
        @NotNull(message = "首揽经度不能为空")
        private Double beginBollardLon;

        /**
         * 首揽纬度
         */
        @NotNull(message = "首揽纬度不能为空")
        private Double beginBollardLat;

        /**
         * 尾揽经度
         */
        @NotNull(message = "尾揽经度不能为空")
        private Double endBollardLon;

        /**
         * 尾揽纬度
         */
        @NotNull(message = "尾揽纬度不能为空")
        private Double endBollardLat;

        /**
         * 舷型 10左舷/20右舷
         */
        @NotBlank(message = "舷型不能为空")
        private String gunwale;

        /**
         * 船宽，单位米
         */
        @NotNull(message = "船宽不能为空")
        private Double width;

        private ShipDrawerBuilder() {
        }

        public ShipDrawerBuilder beginBollardLon(Double beginBollardLon) {
            this.beginBollardLon = beginBollardLon;
            return this;
        }

        public ShipDrawerBuilder beginBollardLat(Double beginBollardLat) {
            this.beginBollardLat = beginBollardLat;
            return this;
        }

        public ShipDrawerBuilder endBollardLon(Double endBollardLon) {
            this.endBollardLon = endBollardLon;
            return this;
        }

        public ShipDrawerBuilder endBollardLat(Double endBollardLat) {
            this.endBollardLat = endBollardLat;
            return this;
        }

        public ShipDrawerBuilder gunwale(String gunwale) {
            this.gunwale = gunwale;
            return this;
        }

        public ShipDrawerBuilder width(Double width) {
            this.width = width;
            return this;
        }

        public ShipDrawer build() {
            ShipDrawer drawer = new ShipDrawer();
            ValidatorUtils.FieldBean bean = ValidatorUtils.validator(this);
            if (bean.isSuccess()) {
                throw new IllegalArgumentException(bean.getMsg());
            }
            BeanUtil.copyProperties(this, drawer);
            return drawer;
        }
    }

    @Setter
    @Getter
    public static class ShipPolygon {

        /**
         * 船头靠岸一侧经度
         */
        private Double lon1;

        /**
         * 船头靠岸一侧维度
         */
        private Double lat1;

        /**
         * 船尾靠岸一侧经度
         */
        private Double lon2;

        /**
         * 船尾靠岸一侧维度
         */
        private Double lat2;

        /**
         * 船尾靠海一侧经度
         */
        private Double lon3;

        /**
         * 船尾靠海一侧维度
         */
        private Double lat3;

        /**
         * 船头靠海一侧经度
         */
        private Double lon4;

        /**
         * 船头靠海一侧维度
         */
        private Double lat4;

        /**
         * 船头中心点经度
         */
        private Double lon5;

        /**
         * 船头中心点维度
         */
        private Double lat5;
    }

    /**
     * 画船
     */
    public ShipPolygon draw() {
        //A点，首缆坐标
        MyLonLat A = new MyLonLat(beginBollardLon, beginBollardLat);
        //B点，尾缆坐标
        MyLonLat B = new MyLonLat(endBollardLon, endBollardLat);
        //baAngle，BA连线与正北方向的角度
        double baAngle = getAngle(B, A);
        //bcAngle，BC连线与正北方向的角度
        double bcAngle = spin(baAngle, 90, gunwale.equals("10"));
        //C点，船尾靠海一侧坐标
        MyLonLat C = getMyLonLat(B, width, bcAngle);
        //D点，船头靠海一侧坐标
        MyLonLat D = getMyLonLat(C, getDistance(A, B), baAngle);

        //查找A点和D点坐标的中心点E
        double adAngle = getAngle(A, D);
        //E点（船头中心点坐标）
        MyLonLat E = getMyLonLat(A, width / 2., adAngle);

        //点A和点D缩进一个船头的长度，固定设置为船宽*0.618
        MyLonLat indent_A = getMyLonLat(A, width * 0.618, spin(baAngle, 180));
        MyLonLat indent_D = getMyLonLat(D, width * 0.618, spin(baAngle, 180));

        ShipPolygon polygon = new ShipPolygon();
        polygon.setLon1(indent_A.m_Longitude);
        polygon.setLat1(indent_A.m_Latitude);
        polygon.setLon2(B.m_Longitude);
        polygon.setLat2(B.m_Latitude);
        polygon.setLon3(C.m_Longitude);
        polygon.setLat3(C.m_Latitude);
        polygon.setLon4(indent_D.m_Longitude);
        polygon.setLat4(indent_D.m_Latitude);
        polygon.setLon5(E.m_Longitude);
        polygon.setLat5(E.m_Latitude);
        return polygon;
    }

    /**
     * 获取AB连线与正北方向的角度
     *
     * @param A A点的经纬度
     * @param B B点的经纬度
     * @return AB连线与正北方向的角度（0~360）
     */
    public static double getAngle(MyLonLat A, MyLonLat B) {
        double dx = (B.m_RadLo - A.m_RadLo) * A.Ed;
        double dy = (B.m_RadLa - A.m_RadLa) * A.Ec;
        double angle;
        angle = Math.atan(Math.abs(dx / dy)) * 180. / Math.PI;
        double dLo = B.m_Longitude - A.m_Longitude;
        double dLa = B.m_Latitude - A.m_Latitude;
        if (dLo > 0 && dLa <= 0) {
            angle = (90. - angle) + 90;
        } else if (dLo <= 0 && dLa < 0) {
            angle = angle + 180.;
        } else if (dLo < 0 && dLa >= 0) {
            angle = (90. - angle) + 270;
        }
        return angle;
    }

    /**
     * 逆时针角度旋转
     *
     * @param angle  当前角度
     * @param degree 旋转角度
     * @return 旋转后角度
     */
    public static double spin(double angle, double degree) {
        return (angle - degree) >= 0 ? angle - degree : 360 - (degree - angle);
    }

    /**
     * 角度旋转
     *
     * @param angle       当前角度
     * @param degree      旋转角度
     * @param isClockwise true顺时针/false逆时针
     * @return 旋转后角度
     */
    public static double spin(double angle, double degree, boolean isClockwise) {
        if (isClockwise) {
            return (angle + degree) < 360 ? angle + degree : angle + degree - 360;
        } else {
            return spin(angle, degree);
        }
    }

    /**
     * 求B点经纬度
     *
     * @param A        已知点的经纬度
     * @param distance AB两地的距离 单位米
     * @param angle    AB连线与正北方向的夹角（0~360）
     * @return B点的经纬度
     */
    public static MyLonLat getMyLonLat(MyLonLat A, double distance, double angle) {

        double dx = distance * Math.sin(Math.toRadians(angle));
        double dy = distance * Math.cos(Math.toRadians(angle));

        double bjd = (dx / A.Ed + A.m_RadLo) * 180. / Math.PI;
        double bwd = (dy / A.Ec + A.m_RadLa) * 180. / Math.PI;

        return new MyLonLat(bjd, bwd);
    }

    /**
     * 求A、B两点之间的距离
     *
     * @param A A点的经纬度
     * @param B B点的经纬度
     * @return AB两地的距离 单位米
     */
    public static double getDistance(MyLonLat A, MyLonLat B) {
        double radLat1 = rad(A.m_Latitude);
        double radLat2 = rad(B.m_Latitude);
        double a = radLat1 - radLat2;
        double b = rad(A.m_Longitude) - rad(B.m_Longitude);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * MyLonLat.Rc;
        return s;
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    @Getter
    private static class MyLonLat {
        final static double Rc = 6378137;
        final static double Rj = 6356725;
        double m_LoDeg, m_LoMin, m_LoSec;
        double m_LaDeg, m_LaMin, m_LaSec;
        double m_Longitude, m_Latitude;
        double m_RadLo, m_RadLa;
        double Ec;
        double Ed;

        public MyLonLat(double longitude, double latitude) {
            m_LoDeg = (int) longitude;
            m_LoMin = (int) ((longitude - m_LoDeg) * 60);
            m_LoSec = (longitude - m_LoDeg - m_LoMin / 60.) * 3600;

            m_LaDeg = (int) latitude;
            m_LaMin = (int) ((latitude - m_LaDeg) * 60);
            m_LaSec = (latitude - m_LaDeg - m_LaMin / 60.) * 3600;

            m_Longitude = longitude;
            m_Latitude = latitude;
            m_RadLo = longitude * Math.PI / 180.;
            m_RadLa = latitude * Math.PI / 180.;
            Ec = Rj + (Rc - Rj) * (90. - m_Latitude) / 90.;
            Ed = Ec * Math.cos(m_RadLa);
        }
    }
}
