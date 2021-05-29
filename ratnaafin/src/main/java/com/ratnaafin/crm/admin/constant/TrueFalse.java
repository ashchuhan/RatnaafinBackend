package com.ratnaafin.crm.admin.constant;

public enum TrueFalse {
    TRUE (1, "true", true),
    FALSE (0, "false", false);

    private int code;
    private String value;
    private boolean bValue;

    private TrueFalse(int code, String value, boolean bValue) {
        this.code = code;
        this.value = value;
        this.bValue = bValue;
    }

    public int getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public boolean getBvalue() {
        return bValue;
    }
}
