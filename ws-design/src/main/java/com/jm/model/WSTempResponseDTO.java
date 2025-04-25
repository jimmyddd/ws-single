package com.jm.model;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class WSTempResponseDTO {

    private Object data;

    private Object saveParams;
}
