package com.cyssxt.tomato.dao;

import com.cyssxt.common.dao.BaseRepository;
import com.cyssxt.tomato.entity.TimeActionEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface TimeRepository extends BaseRepository<TimeActionEntity> {
    @Transactional
    @Modifying
    @Query("update TimeActionEntity set pushFlag=1,pushTime=CURRENT_TIMESTAMP where rowId in :timeIds")
    int updatePushFlag(@Param("timeIds") List<String> timeIds);

    @Transactional
    @Modifying
    @Query("update TimeActionEntity set toDoId=:todoId where actionId=:actionId and toDoId is null")
    int updateByActionId(@Param("actionId") String actionId, @Param("todoId") String todoId);
}
