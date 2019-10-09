package com.cyssxt.tomato.dao;

import com.cyssxt.common.dao.BaseRepository;
import com.cyssxt.tomato.dto.ProjectDto;
import com.cyssxt.tomato.entity.ProjectInfoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.sql.Timestamp;


public interface ProjectRepository extends BaseRepository<ProjectInfoEntity> {

    @Query("select new com.cyssxt.tomato.dto.ProjectDto(A.rowId,A.title,A.remark,A.status,A.sort,A.parentId,B.title) from ProjectInfoEntity A" +
            " left join DutyInfoEntity B on A.parentId= B.rowId where " +
            " A.userId=:userId and (A.delFlag=false or A.delFlag is null) order by A.sort asc ")
    Page<ProjectDto> list(@Param("userId")String userId, Pageable pageable);

    @Query("select coalesce(count(A.rowId),0) from ProjectInfoEntity A where A.delFlag=0 and A.finishTime>=:start and A.status=:status and A.finishTime<=:end and A.userId=:userId")
    Long count(@Param("start") Timestamp start, @Param("end") Timestamp end, @Param("userId") String userId, @Param("status") Byte status);


    @Transactional
    @Modifying
    @Query("update ToDosEntity set finishTime=CURRENT_TIMESTAMP,status=2 where parentId=:projectId and delFlag=false and parentType=1 and (status!=2 or status is null)")
    int finishTodo(@Param("projectId")String projectId);
}
