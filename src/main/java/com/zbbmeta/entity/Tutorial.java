package com.zbbmeta.entity;


import lombok.Data;

import java.io.Serializable;

/**
 * @TableName tb_tutorial
 */

@Data
public class Tutorial implements Serializable {
    private Long id;

    private String title;

    private String description;

    private Integer published;

    private static final long serialVersionUID = 1L;
}