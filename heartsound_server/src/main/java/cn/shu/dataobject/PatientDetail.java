package cn.shu.dataobject;

import lombok.Data;

@Data
public class PatientDetail {
    private Integer patientId;

    private String patientName;

    private String relationName;

    private String relationTel;

    private String photo;

    private String hospitalId;
}
