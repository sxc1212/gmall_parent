package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.product.service.TestService;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public void testLock() {

        RLock lock = redissonClient.getLock("myLock");

        lock.lock();

        boolean res = false;
        try {
            res = lock.tryLock(1, 1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (res) {
            try {


                String numValue = redisTemplate.opsForValue().get("num");

                if (StringUtils.isEmpty(numValue)) {
                    return;
                }

                int num = Integer.parseInt(numValue);
                this.redisTemplate.opsForValue().set("num", String.valueOf(++num));
            } finally {
                lock.unlock();
            }
        }


    }

    @Override
    public String readLock() {

        RReadWriteLock rwlock = redissonClient.getReadWriteLock("anyRWLock");
        rwlock.readLock().lock(10, TimeUnit.SECONDS);

        String msg = this.redisTemplate.opsForValue().get("msg");

        return msg;
    }

    @Override
    public String writeLock() {

        RReadWriteLock rwlock = redissonClient.getReadWriteLock("anyRWLock");
        rwlock.writeLock().lock(10, TimeUnit.SECONDS);

        this.redisTemplate.opsForValue().set("msg", UUID.randomUUID().toString());

        return "写入完成....";
    }


}