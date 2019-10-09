package com.cyssxt.tomato.dao;

import com.cyssxt.common.dao.BaseRepository;
import com.cyssxt.tomato.entity.UserPushRelationEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface UserPushRepository extends BaseRepository<UserPushRelationEntity> {

    UserPushRelationEntity findFirstByUserIdAndClientType(String userId,Byte clientType);

    @Query("select A.getuiId from UserPushRelationEntity A, UserInfoEntity B where B.delFlag=0 and A.userId=B.rowId group by A.getuiId")
    List<String> getAllClientId();


}
