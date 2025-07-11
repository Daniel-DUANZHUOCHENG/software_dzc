package edu.neu.oaas.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MyExceptionTest {

    @Test
    public void testMessageRetrieval() {
        MyException ex = new MyException("error");
        assertEquals("error", ex.getMsg());
        assertEquals("error", ex.getMessage());

        ex.setMsg("new");
        assertEquals("new", ex.getMsg());
    }
} 