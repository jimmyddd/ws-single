package com.jm.model;


public class WSMessageDTO extends WSBaseEntity {
    private Object params;
    private Object expansion;

    public WSMessageDTO() {
    }

    public Object getParams() {
        return this.params;
    }

    public Object getExpansion() {
        return this.expansion;
    }

    public void setParams(Object params) {
        this.params = params;
    }

    public void setExpansion(Object expansion) {
        this.expansion = expansion;
    }

    public String toString() {
        return "WSMessageDTO(params=" + this.getParams() + ", expansion=" + this.getExpansion() + ")";
    }


}
