package cn.shu.dataobject;

import lombok.Data;

@Data
public class DoctorDetail{
    private String doctorId;
    private String name;
    private String hospitalId;
    private String hospitalName;
    private String officeLocation;
    private String officeTel;
    private String title;
    private String photo;
}