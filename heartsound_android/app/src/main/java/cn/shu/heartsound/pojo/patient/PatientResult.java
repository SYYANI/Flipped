package cn.shu.heartsound.pojo.patient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientResult implements Serializable {
    private String patientId;

    private String patientName;

    private String relationName;

    private String relationTel;

    private String photo;

    private String errCode;

    private String errMsg;

    private String hospitalId;

    public String getHospitalId() {
        return hospitalId;
    }

    public String getPhoto() {
        return photo;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getRelationName() {
        return relationName;
    }

    public String getRelationTel() {
        return relationTel;
    }

    public String getErrCode() {
        return errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }
}
