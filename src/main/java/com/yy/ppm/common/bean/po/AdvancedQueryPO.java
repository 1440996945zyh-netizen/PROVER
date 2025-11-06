package com.yy.ppm.common.bean.po;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class AdvancedQueryPO  {
        /**
         *连接条件 */
        private String connectType;
        /**
         *组内匹配 */
        private String filterType;
        private List<AdvancedConditionsPO> conditions;

        public AdvancedQueryPO() {}


}
