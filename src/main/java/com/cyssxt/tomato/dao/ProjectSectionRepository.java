package com.cyssxt.tomato.dao;

import com.cyssxt.common.dao.BaseRepository;
import com.cyssxt.tomato.dto.ProjectSectionDto;
import com.cyssxt.tomato.entity.ProjectSectionEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface ProjectSectionRepository extends BaseRepository<ProjectSectionEntity> {

    ProjectSectionEntity findFirstByTodoIdAndProjectId(String todoId, String projectId);

    @Query("select max(A.sort) from ProjectSectionEntity A where A.delFlag=false and A.projectId=:projectId")
    Integer maxSort(@Param("projectId") String projectId);

    @Modifying
    @Transactional
    @Query("update ProjectSectionEntity set updateTime=CURRENT_TIMESTAMP,delFlag=true where rowId not in :notDelIds and projectId=:projectId")
    int delByProjectId(@Param("notDelIds") List<String> notDelIds,@Param("projectId") String projectId);

    @Query("select new com.cyssxt.tomato.dto.ProjectSectionDto(coalesce(B.title, A.title),coalesce(B.status,0),A.todoId,coalesce(B.actionFlag,false),coalesce(B.tagFlag,false),B.executeTime,A.sort) from ProjectSectionEntity A" +
            " left join ToDosEntity B on A.todoId=B.rowId " +
            " where A.projectId=:projectId and A.delFlag!=true order by A.sort asc")
    List<ProjectSectionDto> items(@Param("projectId")String projectId);

    ProjectSectionEntity findFirstByTitleAndProjectId(String title, String projectId);
}
