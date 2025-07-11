package edu.neu.oaas.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class IsoDateDeserializerTest {

    private final ObjectMapper mapper;

    public IsoDateDeserializerTest() {
        mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Date.class, new IsoDateDeserializer());
        mapper.registerModule(module);
    }

    /**
     * 正常情况：可反序列化符合 ISO 格式的日期字符串
     */
    @Test
    public void testDeserializeValidDate() throws Exception {
        String json = "\"2023-08-20T12:34:56.000Z\"";
        Date date = mapper.readValue(json, Date.class);
        assertNotNull(date);
    }

    /**
     * 异常情况：非法日期字符串抛出 RuntimeException
     */
    @Test
    public void testDeserializeInvalidDate() {
        String json = "\"invalid-date\"";
        assertThrows(RuntimeException.class, () -> mapper.readValue(json, Date.class));
    }
} 