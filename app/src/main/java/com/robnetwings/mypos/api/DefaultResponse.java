package com.robnetwings.mypos.api;

import com.google.gson.annotations.SerializedName;

public class DefaultResponse {
    @SerializedName("error")
    private boolean err;
    @SerializedName("message")
    private String msg;

    public DefaultResponse(boolean z, String str) {
        this.err = z;
        this.msg = str;
    }

    public boolean isErr() {
        return this.err;
    }

    public String getMsg() {
        return this.msg;
    }
}
