package com.liu.utils;

/**
 * 用于暂时存放用户id，涉及用户id不用从request的token中取出
 * ThreadLocal是Java中的一种线程局部变量机制，用于确保每个线程都拥有自己的变量副本。
 *
 * ThreadLocal提供了一种线程安全的变量访问方式，使得多线程环境下的变量隔离变得简单。
 * 在多线程编程中，经常需要为每个线程分配独立的资源或状态，而不希望这些资源或状态被其它线程所共享。
 * ThreadLocal正是为了解决这个问题而生。
 * 它通过为每个使用该变量的线程提供独立的变量副本，从而避免了线程间的数据竞争
 */
public class UserHolder {
    private static ThreadLocal<String> userHolder = new ThreadLocal<>();

    public static String getUserId() {
        return userHolder.get();
    }

    public static void setUserId(String userId) {
        userHolder.set(userId);
    }

    public static void removeUserId() {
        userHolder.remove();
    }
}
