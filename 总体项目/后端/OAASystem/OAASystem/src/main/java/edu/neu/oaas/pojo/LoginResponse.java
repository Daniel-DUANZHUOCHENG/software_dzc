package edu.neu.oaas.pojo;

public class LoginResponse {
    private String message;
    private int userId;

    private User user;

    public LoginResponse(String message, int userId,User user) {
        this.message = message;
        this.userId = userId;
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
