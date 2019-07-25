package com.nine.lib.fastjson.serializer;

public interface ValueFilter {

    Object process(Object source, String name, Object value);
}
