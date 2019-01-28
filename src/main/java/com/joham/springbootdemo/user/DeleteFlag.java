package com.joham.springbootdemo.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author joham
 */
public enum DeleteFlag {

    /**
     * 未删除
     */
    NO,

    /**
     * 已删除
     */
    YES;

    private DeleteFlag() {
    }

    @JsonCreator
    public static DeleteFlag fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }
}
