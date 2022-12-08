package com.jas.takeaway.common;

/**
 * 基於ThreadLocal封裝工具類，保存和獲取當前登陸用戶id
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
