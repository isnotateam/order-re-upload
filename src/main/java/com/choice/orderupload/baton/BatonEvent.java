package com.choice.orderupload.baton;

/**
 * @author 林金成
 * @date 2018/9/14 15:31
 */
public class BatonEvent {
    private String groupid;
    private String storeid;
    private String workdate;
    private String filepath;

    public BatonEvent() {
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getStoreid() {
        return storeid;
    }

    public void setStoreid(String storeid) {
        this.storeid = storeid;
    }

    public String getWorkdate() {
        return workdate;
    }

    public void setWorkdate(String workdate) {
        this.workdate = workdate;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
}