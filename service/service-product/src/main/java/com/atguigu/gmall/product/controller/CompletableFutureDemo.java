package com.atguigu.gmall.product.controller;

import java.util.concurrent.*;
import java.util.function.Supplier;


public class CompletableFutureDemo {


    public static void main(String[] args) throws ExecutionException, InterruptedException {









































        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                3,
                100,
                3,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(3),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );






        CompletableFuture<String> completableFutureA = CompletableFuture.supplyAsync(() -> {
            return "hello";
        },threadPoolExecutor);


        CompletableFuture<Void> completableFutureB = completableFutureA.thenAcceptAsync((c) -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(c + ":\tB");
        },threadPoolExecutor);


        CompletableFuture<Void> completableFutureC = completableFutureA.thenAcceptAsync((c) -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(c + ":\tC");
        },threadPoolExecutor);

        System.out.println(completableFutureB.get());
        System.out.println(completableFutureC.get());

    }
}