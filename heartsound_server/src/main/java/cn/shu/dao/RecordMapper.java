package cn.shu.dao;

import cn.shu.dataobject.Record;
import cn.shu.dataobject.RecordDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface RecordMapper {
    int insert(Record record);

    int insertSelective(Record record);

    Record selectByPrimaryKey(Integer recordId);

    int updateByPrimaryKeySelective(Record record);

    int updateByPrimaryKey(Record record);

    List<RecordDetail> getRecordList();

    List<RecordDetail> getRecordListByPatientId(Integer patientId);

    List<RecordDetail> getRecordListByDoctorId(Integer doctorId);

    RecordDetail getRecordDetail(Integer recordId);

    RecordDetail getLeastRecordListByPatientId(Integer patientId);

}