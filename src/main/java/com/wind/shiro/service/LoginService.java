package com.wind.shiro.service;

import java.util.List;
import java.util.Map;

/**
 * @author Lilandong
 * @date 2017/11/21
 * @desc 模拟从数据库中获取数据
 */
public interface LoginService {
    public Map<String,List<String>> permissions(String userId);
}
