package com.nine.remotemm;

public class ClazzManager {
    private static Class<?> getClazz(String packageName){
        try {
            Class<?> clazz = Class.forName(packageName);
            return clazz;
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
