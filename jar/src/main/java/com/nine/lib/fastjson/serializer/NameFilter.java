package com.nine.lib.fastjson.serializer;

public interface NameFilter {

    String process(Object source, String name, Object value);
}
