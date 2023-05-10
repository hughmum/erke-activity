package com.mu.activity.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 沐
 * Date: 2023-03-10 9:17
 * version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Page<T> {
    long total;
    int pageSize;
    int pageNum;
    List<T> data;
}
