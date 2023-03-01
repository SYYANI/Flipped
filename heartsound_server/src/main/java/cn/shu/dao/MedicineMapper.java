package cn.shu.dao;

import cn.shu.dataobject.Medicine;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface MedicineMapper {
    int insert(Medicine record);

    int insertSelective(Medicine record);

    Medicine selectByPrimaryKey(Integer medicineId);

    int updateByPrimaryKeySelective(Medicine record);

    int updateByPrimaryKey(Medicine record);

    List<Medicine> getMedicineList();

    List<Medicine> getMedicineListByRecord(Integer recordId);

}