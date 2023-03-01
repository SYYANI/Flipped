package cn.shu.dao;

import cn.shu.dataobject.File;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FileMapper {
    int insert(File record);

    int insertSelective(File record);

    File selectByPrimaryKey(Integer fileId);

    int updateByPrimaryKeySelective(File record);

    int updateByPrimaryKey(File record);

    List<File> getFileList(Integer pid);

    File getLastFile(Integer pid);
}