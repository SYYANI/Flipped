package cn.shu.heartsound.pojo.history;

import java.sql.Timestamp;

public class LastResult {
    private String fileId;

    private java.sql.Timestamp date;

    private String patientId;

    private String audioUrl;

    private String rate;

    private String aiResult;

    private String docResult;

    private String errCode;

    private String errMsg;

    public String getId() {
        return fileId;
    }

    public Timestamp getDate() {
        return date;
    }

    public String getPid() {
        return patientId;
    }

    public String getAudio() {
        return audioUrl;
    }

    public String getRate() {
        return rate;
    }

    public String getFresult() {
        return aiResult;
    }

    public String getDresult() {
        return docResult;
    }

    public String getDid() {
        return docResult;
    }

    public String getErrCode() {
        return errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }
}
