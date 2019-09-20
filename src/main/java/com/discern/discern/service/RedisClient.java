package com.discern.discern.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component("torrent_redis")
public class RedisClient {

    private static final String REDIS_ENCODING = "utf-8";

    @Autowired
    private RedisTemplate redisTemplate;

    public void setObject(final String key, final Object value, final long liveTime) {
        try {
            ValueOperations<String, Object> valueOper = redisTemplate.opsForValue();
            valueOper.set(key, value, liveTime, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setObject(final String key, final Object value) {
        try {
            ValueOperations<String, Object> valueOper = redisTemplate.opsForValue();
            valueOper.set(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setLongValue(final String key, final Long value, final long liveTime) {
        try {
            ValueOperations<String, String> valueOper = redisTemplate.opsForValue();
            valueOper.set(key, value.toString(), liveTime, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setLongValue(final String key, final Long value) {
        try {
            ValueOperations<String, Long> valueOper = redisTemplate.opsForValue();
            valueOper.set(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object getObject(final String key) {
        try {
            ValueOperations<String, Object> valueOper = redisTemplate.opsForValue();
            return valueOper.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long increment(final String key,long number) {
        try {
           Long result=  redisTemplate.opsForValue().increment(key,number);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param keys
     */
    public long del(final String... keys) {
        return (Long) redisTemplate.execute(new RedisCallback() {
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                long result = 0;
                for (String key : keys) {
                    result = connection.del(key.getBytes());
                }
                return result;
            }
        });
    }

    /**
     * @param key
     * @param value
     * @param liveTime
     */
    public void set(final byte[] key, final byte[] value, final long liveTime) {
        redisTemplate.execute(new RedisCallback() {
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                connection.set(key, value);
                if (liveTime > 0) {
                    connection.expire(key, liveTime);
                }
                return 1L;
            }
        });
    }

    public void setStr(final String key, final String value, final long liveTime) {
        redisTemplate.opsForValue().set(key,value,liveTime);
    }

    public Object getStr(final String key) {
       return redisTemplate.opsForValue().get(key);
    }


    /**
     * @param key
     * @param value
     * @param liveTime
     */
    public void set(final String key, final String value, final long liveTime) {
        this.set(key.getBytes(), value.getBytes(), liveTime);
    }

    /**
     * @param key
     * @param value
     */
    public void set(final String key, final String value) {
        this.set(key, value, 0L);
    }

    /**
     * @param key
     * @param value
     */
    public void set(final byte[] key, final byte[] value) {
        this.set(key, value, 0L);
    }

    public Long getIncrValue(final String key) {

        return (Long)redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer=redisTemplate.getStringSerializer();
                byte[] rowkey=serializer.serialize(key);
                byte[] rowval=connection.get(rowkey);
                try {
                    String val=serializer.deserialize(rowval);
                    return Long.parseLong(val);
                } catch (Exception e) {
                    return 0L;
                }
            }
        });
    }

    /**
     * @param key
     * @return
     */
    public String get(final String key) {
        return (String) redisTemplate.execute(new RedisCallback() {
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                try {
                    byte[] valueBytes = connection.get(key.getBytes());

                    if (valueBytes != null) {
                        return new String(valueBytes, REDIS_ENCODING);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

    /**
     * @param key
     * @return
     */
    public Set<String> gets(final String key) {
        return (Set<String>) redisTemplate.execute(new RedisCallback() {
            public Set<String> doInRedis(RedisConnection connection) throws DataAccessException {
                Set<String> result = new HashSet<String>();
                try {
                    Set<byte[]> valueBytesSet = connection.sMembers(key.getBytes());
                    if (valueBytesSet != null) {
                        for (byte[] valueBytes : valueBytesSet) {
                            result.add(new String(valueBytes, REDIS_ENCODING));
                        }
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return result;
            }
        });
    }

    /**
     * @param key
     * @return
     */
    public byte[] get(final byte[] key) {
        return (byte[]) redisTemplate.execute(new RedisCallback() {
            public byte[] doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.get(key);
            }
        });
    }

    /**
     * @param key
     * @return
     */
    public boolean exists(final String key) {
        return (Boolean) redisTemplate.execute(new RedisCallback() {
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.exists(key.getBytes());
            }
        });
    }

    /**
     * @return
     */
    public String flushDB() {
        return (String) redisTemplate.execute(new RedisCallback() {
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                connection.flushDb();
                return "ok";
            }
        });
    }

    /**
     * @return
     */
    public String ping() {
        return (String) redisTemplate.execute(new RedisCallback() {
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.ping();
            }
        });
    }

    public Set<String> keys(final String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * @param key
     * @param member
     */
    public void sAdd(String key, String member) {
        redisTemplate.opsForSet().add(key, member);
    }

    /**
     * @param key
     * @return
     */
    public Set<String> sMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     *
     * @param key
     * @param value
     * @return
     */
    public Long rpush(final String key, final Object value){
        try {
            ListOperations<String, Object> listOper = redisTemplate.opsForList();
            return listOper.rightPush(key,value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     *
     * @param key
     * @param value
     * @return
     */
    public Long lpush(final String key, final Object value){
        try {
            ListOperations<String, Object> listOper = redisTemplate.opsForList();
            return listOper.leftPush(key,value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param key
     * @return
     */
    public Object leftPop(String key){
        try {
            ListOperations<String, Object> listOper = redisTemplate.opsForList();
            return listOper.leftPop(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     *
     * @param key
     * @return
     */
    public Object rightPop(String key){
        try {
            ListOperations<String, Object> listOper = redisTemplate.opsForList();
            return listOper.rightPop(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
