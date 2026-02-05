package com.site.mapper;

import com.site.domain.File;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface FileMapper {

    void save(File fileEntity);

    List<File> findFilesByBoardId(long bno);

    File findById(long fileId);

    void deleteByBoardId(long bno);
}
