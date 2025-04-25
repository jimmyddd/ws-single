package com.jm.model;


public class WSResponseVO  extends WSBaseEntity{
    private Object data;

    public WSResponseVO() {
    }

    public Object getData() {
        return this.data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String toString() {
        return "WSResponseVO(data=" + this.getData() + ")";
    }

}
