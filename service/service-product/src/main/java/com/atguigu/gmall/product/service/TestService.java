package com.atguigu.gmall.product.service;

/**
 * author:atGuiGu-mqx
 * date:2022/9/2 10:31
 * 描述：
 **/
public interface TestService {
    //  测试本地锁
    void testLock();

    //  读锁
    String readLock();
    //写锁
    String writeLock();
}
