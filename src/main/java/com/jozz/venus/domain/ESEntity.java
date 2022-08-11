package com.jozz.venus.domain;


import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@Accessors(chain = true)
@NoArgsConstructor
@RequiredArgsConstructor
public class ESEntity implements Serializable {
    /**
     * 展示第几页
     */
    @NonNull
    private Integer pageNumber;

    /**
     * 每页展示的条数
     */
    @NonNull
    private Integer pageSize;
    /**
     * 排序条件
     */
    private Sort sort;

    /**
     * range查询条件
     */
    private List<Range> ranges;
    /**
     * Match查询条件
     */
    private List<Match> matches;
    /**
     * terms查询条件
     */
    private List<Terms> terms;
    /**
     * nested查询条件
     */
    private List<Nested> nesteds;


    public static ESEntity of(Integer pageNumber, Integer pageSize) {
        return new ESEntity(pageNumber, pageSize);
    }


    @Getter
    @Setter
    @EqualsAndHashCode
    @Accessors(chain = true)
    @NoArgsConstructor
    @RequiredArgsConstructor
    public static class Sort {
        /**
         * 排序字段
         */
        @NonNull
        private String field;
        /**
         * 排序类型，例：desc
         */
        @NonNull
        private String type;

        public static Sort of(String field, String type) {
            return new Sort(field, type);
        }
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Range {
        /**
         * 时间字段
         */
        @NonNull
        private String field;
        /**
         * 开始时间
         */
        private Object from;
        /**
         * 结束时间
         */
        private Object to;

        public static Range of(String field, Object from, Object to) {
            return new Range(field, from, to);
        }
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    @Accessors(chain = true)
    @NoArgsConstructor
    @RequiredArgsConstructor
    public static class Match {
        /**
         * 条件Key
         */
        @NonNull
        private String field;
        /**
         * 条件Value
         */
        @NonNull
        private Object value;

        public Object getWildCardValue() {
//            if (value instanceof String) {
//                return "*" + value + "*";
//            }
            return value;
        }

        public String getOperationName() {
//            if (value instanceof String) {
//                return "wildcard";
//            }
            return "match";
        }


        public static Match of(String field, Object value) {
            return new Match(field, value);
        }
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    @Accessors(chain = true)
    @NoArgsConstructor
    @RequiredArgsConstructor
    public static class Terms {
        /**
         * 条件Key
         */
        @NonNull
        private String field;
        /**
         * 条件Value
         */
        @NonNull
        private List<Object> value;

        public static Terms of(String field, List<Object> value) {
            return new Terms(field, value);
        }
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    @Accessors(chain = true)
    @NoArgsConstructor
    @RequiredArgsConstructor
    public static class Nested {
        /**
         * path
         */
        @NonNull
        private String path;
        /**
         * Match查询条件
         */
        @NonNull
        private List<Match> matches;

        public static Nested of(String path, List<Match> matches) {
            return new Nested(path, matches);
        }
    }
}
