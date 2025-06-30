package edu.neu.oaas.pojo;

public class Conference {
    private Integer conferenceID;
    private String conferencename;
    private String creator;
    private String situation;
    private String coverpath;
    private String contentspath;
    private String starttime;
    private String endtime;
    private String state;
    private Integer tenantID;

    public Conference() {
    }

    public Conference(Integer conferenceID, String conferencename, String creator, String situation, String coverpath, String contentspath, String starttime, String endtime, String state, Integer tenantID) {
        this.conferenceID = conferenceID;
        this.conferencename = conferencename;
        this.creator = creator;
        this.situation = situation;
        this.coverpath = coverpath;
        this.contentspath = contentspath;
        this.starttime = starttime;
        this.endtime = endtime;
        this.state = state;
        this.tenantID = tenantID;
    }

    public Integer getConferenceID() {
        return conferenceID;
    }

    public void setConferenceID(Integer conferenceID) {
        this.conferenceID = conferenceID;
    }

    public String getConferencename() {
        return conferencename;
    }

    public void setConferencename(String conferencename) {
        this.conferencename = conferencename;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public String getCoverpath() {
        return coverpath;
    }

    public void setCoverpath(String coverpath) {
        this.coverpath = coverpath;
    }

    public String getContentspath() {
        return contentspath;
    }

    public void setContentspath(String contentspath) {
        this.contentspath = contentspath;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getTenantID() {
        return tenantID;
    }

    public void setTenantID(Integer tenantID) {
        this.tenantID = tenantID;
    }
}
