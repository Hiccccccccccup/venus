package com.jozz.venus.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class Order implements Serializable {
    private static final long serialVersionUID = -466155134525695630L;

    private Long id;
    private Long orderId;
    private Long userId;
    private String userName;

}
