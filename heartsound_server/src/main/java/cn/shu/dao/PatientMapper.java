package cn.shu.dao;

import cn.shu.dataobject.Patient;
import cn.shu.dataobject.PatientDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface PatientMapper {
    int insert(Patient record);

    int insertSelective(Patient record);

    Patient selectByPrimaryKey(Integer patientId);

    int updateByPrimaryKeySelective(Patient record);

    int updateByPrimaryKey(Patient record);
    List<Patient> getPatientList();

    PatientDetail selectByPatientId(Integer patientId);

    List<Patient> getPatientByDoctorId(Integer doctorId);
}