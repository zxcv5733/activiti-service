package com.joker.enums;

/**
 * @author: Li dong
 * @date: 2023/5/2 19:57
 * @description:
 */
public enum ApproveTypeEnum {
    /**
     * 待审批
     */
    AWAIT_APPROVAL(-1, "待审批"),

    /**
     * 通过
     */
    PASS(1, "审批通过"),

    /**
     * 拒绝
     */
    REJECT(0, "审批不通过");

    private Integer key;

    private String value;

    ApproveTypeEnum(Integer key, String value){
        this.key = key;
        this.value = value;
    }

    public Integer getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
