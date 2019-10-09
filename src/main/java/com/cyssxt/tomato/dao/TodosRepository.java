package com.cyssxt.tomato.dao;

import com.cyssxt.common.bean.CountBean;
import com.cyssxt.common.dao.BaseRepository;
import com.cyssxt.tomato.dto.ToDoDto;
import com.cyssxt.tomato.entity.ToDosEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;

public interface TodosRepository extends BaseRepository<ToDosEntity> {
    @Query("select new com.cyssxt.tomato.dto.ToDoDto(A.rowId,A.title) from ToDosEntity  A where " +
            "( A.delFlag=false or A.delFlag is null ) and A.userId=:userId order by A.createTime desc")
    Page<ToDoDto> list(@Param("userId") String userId, Pageable toPageable);

    @Query("select new com.cyssxt.common.bean.CountBean(count(id),'-1') from ToDosEntity  A " +
            " where A.parentId='-1' and A.status!=2 and A.showFlag=true and A.userId=:userId and A.delFlag=false ")
    CountBean countInbox(@Param("userId")String userId);

    @Query("select new com.cyssxt.tomato.dto.ToDoDto(A.rowId,A.title,A.executeTime) from ToDosEntity  A " +
            " where A.parentType=-1 and A.userId=:userId and (A.delFlag is null or A.delFlag=false) order by A.createTime desc")
    Page<ToDoDto> inbox(@Param("userId")String userId,Pageable pageable);

    @Query("select new com.cyssxt.common.bean.CountBean(sum(coalesce(B.planTime,0)),'-1') from ToDosEntity B where " +
            " B.userId=:userId and B.dateNo=:dateNo and (B.delFlag is null or B.delFlag=true)")
    CountBean countPlanTime(@Param("userId")String userId,@Param("dateNo")Integer dateNo);

    @Modifying
    @Transactional
    @Query("update ToDosEntity set nextExecuteTime=:timestamp where rowId=:todoId")
    int updateNextExecTime(@Param("todoId") String todoId, @Param("timestamp") Timestamp timestamp);

    ToDosEntity findFirstByDateNoAndRepeatId(Integer dateNo,String repeatId);

    @Modifying
    @Transactional
    @Query("update ToDosEntity set delFlag=true where repeatId=:todoId and dateNo>:dateNo")
    int delAfterToday(@Param("dateNo") Integer dateNo, @Param("todoId") String todoId);
    @Modifying
    @Transactional
    @Query("update ToDosEntity set pushFlag=true,pushTime=CURRENT_TIMESTAMP where rowId in :rowIds")
    int updatePushFlag(@Param("rowIds") List<String> rowIds);

    @Query("from ToDosEntity A where A.delFlag=false and A.repeatId=:repeatId and A.dateNo>:today")
    List<ToDosEntity> queryTodoByRepeatId(@Param("today") Integer today,@Param("repeatId") String repeatId);

    @Modifying
    @Transactional
    @Query("update ToDosEntity A set status=2,finishTime=current_timestamp where A.delFlag=false and (A.rowId=:repeatId or A.repeatId=:repeatId)")
    int updateTodoChild(@Param("repeatId")String repeatId);

    @Transactional
    @Modifying
    @Query("update ToDosEntity A set A.endFlag=true where  A.showFlag=false and A.parentId=:parentId and A.delFlag=false")
    int stopRepeatByParentId(@Param("parentId") String parentId);
}
