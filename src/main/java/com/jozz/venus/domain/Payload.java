package com.jozz.venus.domain;

import lombok.Data;

@Data
public class Payload {
    private Integer fromId;
    private Integer toId;
    private Integer type;
    private String content;
}
