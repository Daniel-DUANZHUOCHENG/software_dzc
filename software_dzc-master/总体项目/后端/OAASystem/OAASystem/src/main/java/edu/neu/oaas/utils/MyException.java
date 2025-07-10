package edu.neu.oaas.utils;

public class MyException extends RuntimeException {
    private String msg;

    public MyException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
