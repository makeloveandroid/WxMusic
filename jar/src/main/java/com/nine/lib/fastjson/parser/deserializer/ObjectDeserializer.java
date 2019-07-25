package com.nine.lib.fastjson.parser.deserializer;

import java.lang.reflect.Type;

import com.nine.lib.fastjson.parser.DefaultJSONParser;

public interface ObjectDeserializer {
    <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName);
    
    int getFastMatchToken();
}
