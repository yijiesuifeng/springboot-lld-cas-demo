package com.wind.common.redis;

import java.util.List;

/**
 * @author Lilandong
 * @date 2017/11/12
 */
public interface IRedisService {
    public boolean set(String key, String value);

    public String get(String key);

    public boolean expire(String key,long expire);

    public <T> boolean setList(String key ,List<T> list);

    public <T> List<T> getList(String key,Class<T> clz);

    public long lpush(String key,Object obj);

    public long rpush(String key,Object obj);

    public String lpop(String key);

    public String rpop(String key);

    public boolean setObject(String key,Object object);

    public <T> T getObject(String key,T t);

    public boolean containsKey(String key);

    public Long delete(String key);

}
