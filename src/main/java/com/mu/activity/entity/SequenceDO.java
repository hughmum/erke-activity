package com.mu.activity.entity;

import lombok.Data;

/**
 * @author 沐
 * Date: 2023-03-09 17:51
 * version: 1.0
 */
@Data
public class SequenceDO {

    private String name;

    private Integer currentValue;

    private Integer step;


}