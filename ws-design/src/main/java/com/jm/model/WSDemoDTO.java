package com.jm.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WSDemoDTO extends WSBaseEntity {

    /**
     * 会话id
     */
    private String sessionId;

    /**
     * 数据id
     */
    private String dataId;
}
