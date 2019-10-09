package com.cyssxt.tomato.dao;

import com.cyssxt.common.dao.BaseRepository;
import com.cyssxt.tomato.entity.ToDoDayInfoEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.beans.Transient;
import java.util.List;


public interface TodoDayRepository extends BaseRepository<ToDoDayInfoEntity> {

    @Query(value = "select count(row_id) from to_do_day_info where (del_flag=0 or del_flag is null)" +
            " and user_id=:userId and date_no=:dateNo ",nativeQuery = true)
    Long count(@Param("userId")String userId, @Param("dateNo") String dateNo);

    ToDoDayInfoEntity findFirstByDateNoAndToDoId(Integer dateNo, String todoId);
    ToDoDayInfoEntity findFirstByToDoId(String todoId);

    @Transactional
    @Modifying
    @Query("update ToDoDayInfoEntity set pushFlag=1,finishTime=CURRENT_TIMESTAMP where rowId in :rowIds")
    int updatePushFlag(@Param("rowIds") List<String> rowIds);

    @Transactional
    @Modifying
    @Query("update ToDoDayInfoEntity set delFlag=1,updateTime=CURRENT_TIMESTAMP where toDoId=:todoId")
    int delByTodoId(@Param("todoId") String todoId);

    @Modifying
    @Transactional
    @Query("update ToDoDayInfoEntity A set A.delFlag=true where A.delFlag=false and A.toDoId=:todoId and A.dateNo>=:today")
    int delAfterToday(@Param("today")Integer today,@Param("todoId")String todoId);

}
