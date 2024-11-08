package com.jozz.venus.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PrivateMsgDelay implements Serializable {
    private static final long serialVersionUID = -466155134525695630L;

    private Integer id;
    private Integer fromId;
    private Integer toId;
    private String payload;
    private Date createTime;

}
