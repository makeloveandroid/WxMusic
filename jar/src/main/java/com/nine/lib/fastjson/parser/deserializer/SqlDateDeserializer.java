package com.nine.lib.fastjson.parser.deserializer;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import com.nine.lib.fastjson.JSONException;
import com.nine.lib.fastjson.parser.DefaultJSONParser;
import com.nine.lib.fastjson.parser.JSONScanner;
import com.nine.lib.fastjson.parser.JSONToken;

public class SqlDateDeserializer extends AbstractDateDeserializer implements ObjectDeserializer {

    public final static SqlDateDeserializer instance = new SqlDateDeserializer();

    @SuppressWarnings("unchecked")
    protected <T> T cast(DefaultJSONParser parser, Type clazz, Object fieldName, Object val) {
        if (val == null) {
            return null;
        }

        if (val instanceof Date) {
            val = new java.sql.Date(((Date) val).getTime());
        } else if (val instanceof Number) {
            val = (T) new java.sql.Date(((Number) val).longValue());
        } else if (val instanceof String) {
            String strVal = (String) val;
            if (strVal.length() == 0) {
                return null;
            }

            long longVal;

            JSONScanner dateLexer = new JSONScanner(strVal);
            if (dateLexer.scanISO8601DateIfMatch()) {
                longVal = dateLexer.getCalendar().getTimeInMillis();
            } else {

                DateFormat dateFormat = parser.getDateFormat();
                try {
                    Date date = (Date) dateFormat.parse(strVal);
                    return (T) new java.sql.Date(date.getTime());
                } catch (ParseException e) {
                    // skip
                }

                longVal = Long.parseLong(strVal);
            }
            return (T) new java.sql.Date(longVal);
        } else {
            throw new JSONException("parse error : " + val);
        }

        return (T) val;
    }

    public int getFastMatchToken() {
        return JSONToken.LITERAL_INT;
    }
}
