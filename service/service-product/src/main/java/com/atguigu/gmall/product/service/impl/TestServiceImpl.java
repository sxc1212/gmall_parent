package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.product.service.TestService;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * author:atGuiGu-mqx
 * date:2022/9/2 10:32
 * 描述：
 **/
@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    /*
    1.  在缓存中设置一个 key  num 初始化为 0    set num 0; String
    2.  获取缓存中 num 的数据
        a.  如果有数据 +1 并写入缓存
        b.  如果没有数据，则直接return
     */
    @Autowired
    private RedissonClient redissonClient;

    @Override
    public void testLock() {
        //  获取对象    Get Redis based implementation of java.util.concurrent.locks.Lock
        RLock lock = redissonClient.getLock("myLock"); //   初始化的时候，就会设置看门狗的过期时间
        //  上锁
        lock.lock();
        //  lock.lock(10,TimeUnit.SECONDS);
        boolean res = false;
        try {
            res = lock.tryLock(1, 1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //  判断
        if (res){
            try {
                //  执行业务逻辑！
                //  get num ;
                String numValue = redisTemplate.opsForValue().get("num");
                //  判断
                if (StringUtils.isEmpty(numValue)){
                    return;
                }
                //  如果有数据 +1 并写入缓存
                int num = Integer.parseInt(numValue);
                this.redisTemplate.opsForValue().set("num",String.valueOf(++num));
            }finally {
                lock.unlock();
            }
        }

        //        try {
        //            //  执行业务逻辑！
        //            //  get num ;
        //            String numValue = redisTemplate.opsForValue().get("num");
        //
        //            //  判断
        //            if (StringUtils.isEmpty(numValue)){
        //                return;
        //            }
        //
        //            //  如果有数据 +1 并写入缓存
        //            int num = Integer.parseInt(numValue);
        //            this.redisTemplate.opsForValue().set("num",String.valueOf(++num));
        //        } catch (NumberFormatException e) {
        //            e.printStackTrace();
        //        }finally {
        //            //  解锁：
        //            lock.unlock();
        //        }
    }

    @Override
    public String readLock() {
        //  获取锁对象：
        RReadWriteLock rwlock = redissonClient.getReadWriteLock("anyRWLock");
        rwlock.readLock().lock(10,TimeUnit.SECONDS);
        //  读取缓存中的内容。
        String msg = this.redisTemplate.opsForValue().get("msg");
        //  不写解锁！目的是测试互斥性！
        return msg;
    }

    @Override
    public String writeLock() {
        //  获取锁对象：
        RReadWriteLock rwlock = redissonClient.getReadWriteLock("anyRWLock");
        rwlock.writeLock().lock(10,TimeUnit.SECONDS);

        this.redisTemplate.opsForValue().set("msg",UUID.randomUUID().toString());
        //  不写解锁！目的是测试互斥性！
        return "写入完成....";
    }

//    @Override
//    public void testLock() {
//        //  setnx key value; 1 0
//        //  Boolean result = this.redisTemplate.opsForValue().setIfAbsent("lock", "ok");
//        //  set lock atguigu ex 10 nx
//        String uuid = UUID.randomUUID().toString();
//
//        Boolean result = this.redisTemplate.opsForValue().setIfAbsent("lock", uuid, 3, TimeUnit.SECONDS);
//        //  result = true 上锁成功！
//        if (result){
//            //  执行业务逻辑
//            //  get num ;
//            String numValue = redisTemplate.opsForValue().get("num");
//
//            //  判断
//            if (StringUtils.isEmpty(numValue)){
//                return;
//            }
//
//            //  如果有数据 +1 并写入缓存
//            int num = Integer.parseInt(numValue);
//            this.redisTemplate.opsForValue().set("num",String.valueOf(++num));
//
//            //  释放锁：
//            //            if (uuid.equals(this.redisTemplate.opsForValue().get("lock"))){
//            //                //  index1 即将要执行del ，但是突然释放锁了！
//            //                this.redisTemplate.delete("lock");
//            //            }
//            //  这个脚本只在客户端传入的值和键的口令串相匹配时，才对键进行删除
//            String scriptText = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
//            DefaultRedisScript defaultRedisScript = new DefaultRedisScript<>();
//            defaultRedisScript.setResultType(Long.class);
//            defaultRedisScript.setScriptText(scriptText);
//            //  第一个参数：defaultRedisScript 第二个参数：键值 第三个参数：口令串
//            this.redisTemplate.execute(defaultRedisScript, Arrays.asList("lock"),uuid);
//
//        } else {
//            //  等待
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            //  自旋
//            testLock();
//        }
//    }
}