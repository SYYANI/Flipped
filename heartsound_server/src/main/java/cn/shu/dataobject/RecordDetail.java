package cn.shu.dataobject;

import lombok.Data;
import java.util.Date;

@Data
public class RecordDetail {
    private Integer recordId;

    private Integer patientId;

    private Integer doctorId;

    private Integer diseaseId;

    private String diseaseName;

    private String doctorName;

    private String patientName;

    private String HospitalName;

    private String detail;

    private Date createDate;

    private Date updateDate;
}
