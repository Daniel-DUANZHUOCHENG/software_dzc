package edu.neu.oaas.pojo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import edu.neu.oaas.utils.IsoDateDeserializer;

import java.util.Date;

public class UserBehavior {
    private String action;
    private String page;

    @JsonDeserialize(using = IsoDateDeserializer.class)
    private Date timestamp;

    private Integer userId; // 添加 userId 属性

    // Getters and Setters
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
