package com.jozz.venus.enums;

/**
 * 方法名枚举类
 */
public enum MethodEnum {
    SAVE("save"),
    FIND_ONE("findOne"),
    DELETE_BY_ID("deleteById"),
    EXIST("exist");

    public String name;

    MethodEnum(String name) {
        this.name = name;
    }

    public static MethodEnum match(String name) {
        for (MethodEnum methodEnum : MethodEnum.values()) {
            if (methodEnum.name().equalsIgnoreCase(name)) {
                return methodEnum;
            }
        }
        return null;
    }
}
