package com.mu.activity.common.request;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author 沐
 * Date: 2023-03-10 0:24
 * version: 1.0
 */
@Getter
@Setter
@ApiModel
public class PageRequest {
    @NotNull(message = "pageNum不能为空")
    Integer pageNum;
    @NotNull(message = "pageSize不能为空")
    Integer pageSize;
}