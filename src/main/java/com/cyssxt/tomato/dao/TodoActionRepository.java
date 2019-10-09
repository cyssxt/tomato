package com.cyssxt.tomato.dao;

import com.cyssxt.common.dao.BaseRepository;
import com.cyssxt.tomato.dto.TodoActionDto;
import com.cyssxt.tomato.entity.ToDoActionsEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface TodoActionRepository extends BaseRepository<ToDoActionsEntity> {

    @Transactional
    @Modifying
    @Query("update ToDoActionsEntity set status=:status,finishTime=CURRENT_TIMESTAMP where rowId in :contentIds ")
    int updateStatus(@Param("contentIds") List<String> contentIds,@Param("status") Boolean status);

    @Query("select new com.cyssxt.tomato.dto.TodoActionDto(A.rowId,A.content,A.status) from ToDoActionsEntity A where " +
            " A.toDoId in :todoIds and (A.delFlag is null or A.delFlag=false)")
    List<TodoActionDto> actions(List<String> todoIds);

    @Modifying
    @Transactional
    @Query("update ToDoActionsEntity set delFlag=1 where rowId in :actionIds")
    int updateActions(@Param("actionIds") List<String> actionIds);

    List<ToDoActionsEntity> findByToDoIdAndDelFlagFalse(String todoId);

    ToDoActionsEntity findFirstByToDoIdAndContentAndSort(String todoId,String content,Integer sort);
    @Modifying
    @Transactional
    @Query("update ToDoActionsEntity set delFlag=true where rowId not in :actionIds and toDoId=:todoId")
    int delActions(@Param("todoId") String todoId, @Param("actionIds") List<String> actionIds);

    @Modifying
    @Transactional
    @Query("update ToDoActionsEntity set delFlag=true where toDoId=:todoId ")
    int delActions(@Param("todoId") String todoId);
}
