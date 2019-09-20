package com.discern.discern.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class RedisService  {


    @Autowired
    @Qualifier("torrent_redis")
    RedisClient redisClient;

    public void setData(String key,String value, long liveTime){
    	redisClient.set(key, value, liveTime);
    }
    public String getData(String key){
    	return redisClient.get(key);
    }
    
    public void setObject(String key, Object value, long liveTime){
    	redisClient.setObject(key, value, liveTime);
    }
    
    public void getObject(String key){
    	redisClient.getObject(key);
    }
    
    public void setByte(final byte[] key, final byte[] value, final long liveTime){
    	redisClient.set(key, value, liveTime);
    }
    
    public byte[] getByte(final byte[] key){
    	return redisClient.get(key);
    }
    
    public long removeData(String key){
    	return redisClient.del(key);
    }


    /**
     *
     * @param key
     * @param value
     * @return
     */
    public Long rpush(final String key, final Object value){
        return redisClient.rpush(key,value);
    }
    /**
     *
     * @param key
     * @param value
     * @return
     */
    public Long lpush(final String key, final Object value){
        return redisClient.lpush(key,value);
    }

    /**
     *
     * @param key
     * @return
     */
    public Object leftPop(String key){
        return redisClient.leftPop(key);
    }
    /**
     *
     * @param key
     * @return
     */
    public Object rightPop(String key){
        return redisClient.rightPop(key);
    }
}
