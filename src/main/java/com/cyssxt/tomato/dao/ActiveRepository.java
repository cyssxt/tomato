package com.cyssxt.tomato.dao;

import com.cyssxt.common.dao.BaseRepository;
import com.cyssxt.tomato.entity.ActiveLogEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

public interface ActiveRepository extends BaseRepository<ActiveLogEntity> {
    @Query("select B.getuiId from ActiveLogEntity A,UserPushRelationEntity B, UserInfoEntity C where A.createTime<:lastTime and A.userId=B.userId and  C.delFlag=0 and A.userId=B.rowId group by B.getuiId")
    List<String> getAllClientIdOverDays(@Param("lastTime") Timestamp timestamp);
}
