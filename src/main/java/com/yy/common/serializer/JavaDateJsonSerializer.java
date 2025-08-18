package com.yy.common.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期自定义序列化
 */
public class JavaDateJsonSerializer extends JsonSerializer<Date> {

    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    public JavaDateJsonSerializer() {
    }

    public JavaDateJsonSerializer(String format) {
        df = new SimpleDateFormat(format);
    }

    public void serialize(Date date, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        if (null != date) {
            gen.writeString(this.df.format(date));
        }
    }

}
