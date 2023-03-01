package cn.shu.dao;

import cn.shu.dataobject.Doctor;
import cn.shu.dataobject.DoctorDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DoctorMapper {
    int insert(Doctor record);

    int insertSelective(Doctor record);

    Doctor selectByPrimaryKey(Integer doctorId);

    int updateByPrimaryKeySelective(Doctor record);

    int updateByPrimaryKey(Doctor record);
    List<DoctorDetail> getDoctorList();

    DoctorDetail getDoctorDetail(Integer doctorId);
}