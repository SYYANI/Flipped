package cn.shu.dataobject;

import java.util.Date;

public class File {
    private Integer fileId;

    private Date date;

    private Integer patientId;

    private String rate;

    private String aiResult;

    private String docResult;

    private String audioUrl;

    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate == null ? null : rate.trim();
    }

    public String getAiResult() {
        return aiResult;
    }

    public void setAiResult(String aiResult) {
        this.aiResult = aiResult == null ? null : aiResult.trim();
    }

    public String getDocResult() {
        return docResult;
    }

    public void setDocResult(String docResult) {
        this.docResult = docResult == null ? null : docResult.trim();
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl == null ? null : audioUrl.trim();
    }
}