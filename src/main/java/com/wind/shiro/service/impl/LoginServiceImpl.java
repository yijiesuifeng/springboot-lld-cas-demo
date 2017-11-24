package com.wind.shiro.service.impl;

import com.wind.shiro.service.LoginService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lilandong
 * @date 2017/11/21
 */
@Service
public class LoginServiceImpl implements LoginService {
    @Override
    public Map<String, List<String>> permissions(String userId) {
        List<String> permissions = new ArrayList<String>();
        List<String> roles = new ArrayList<>();
        if ("1".equals(userId)) {
            permissions.add("emp:add");
            roles.add("emp");
        } else if ("2".equals(userId)) {
            permissions.add("dept:add");
            permissions.add("dept:edit");
            roles.add("dept");
        } else {
            permissions.add("dept:add");
            permissions.add("dept:edit");
            roles.add("dept");
        }
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        map.put("permissions", permissions);
        map.put("roles", roles);
        return map;
    }
}
