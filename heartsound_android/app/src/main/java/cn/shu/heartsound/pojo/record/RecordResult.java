package cn.shu.heartsound.pojo.record;

import java.sql.Timestamp;
import java.util.Date;

public class RecordResult {
    private Integer recordId;

    private Integer patientId;

    private Integer doctorId;

    private Integer diseaseId;

    private String diseaseName;

    private String doctorName;

    private String patientName;

    private String hospitalName;

    private String detail;

    private java.sql.Timestamp createDate;

    private java.sql.Timestamp updateDate;

    public Integer getRecordId() {
        return recordId;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public Integer getDiseaseId() {
        return diseaseId;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public String getDetail() {
        return detail;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public Timestamp getUpdateDate() {
        return updateDate;
    }
}
