package edu.neu.oaas.pojo;

public class ConferenceReceipt {
    private Integer id;
    private Integer conferenceID;
    private String unit;
    private String name;
    private String gender;
    private String phone;
    private String email;
    private String roomType;
    private String arrivalMode;
    private String arrivalFlight;
    private String arrivalTime;
    private String departureMode;
    private String departureFlight;
    private String departureTime;
    private String remarks;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getConferenceID() {
        return conferenceID;
    }

    public void setConferenceID(Integer conferenceID) {
        this.conferenceID = conferenceID;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getArrivalMode() {
        return arrivalMode;
    }

    public void setArrivalMode(String arrivalMode) {
        this.arrivalMode = arrivalMode;
    }

    public String getArrivalFlight() {
        return arrivalFlight;
    }

    public void setArrivalFlight(String arrivalFlight) {
        this.arrivalFlight = arrivalFlight;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getDepartureMode() {
        return departureMode;
    }

    public void setDepartureMode(String departureMode) {
        this.departureMode = departureMode;
    }

    public String getDepartureFlight() {
        return departureFlight;
    }

    public void setDepartureFlight(String departureFlight) {
        this.departureFlight = departureFlight;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
