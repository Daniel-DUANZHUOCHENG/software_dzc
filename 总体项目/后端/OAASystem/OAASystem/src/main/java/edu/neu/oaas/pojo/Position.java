package edu.neu.oaas.pojo;

public class Position {
    private Integer departmentId;
    private String position;

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "position{" +
                "departmentId=" + departmentId +
                ", position='" + position + '\'' +
                '}';
    }
}
