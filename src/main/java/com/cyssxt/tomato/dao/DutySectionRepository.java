package com.cyssxt.tomato.dao;

import com.cyssxt.common.dao.BaseRepository;
import com.cyssxt.tomato.entity.DutySectionEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DutySectionRepository extends BaseRepository<DutySectionEntity> {

    DutySectionEntity findFirstByContentIdAndContentTypeAndDutyId(String contentId,Byte contentType,String dutyId);

    @Query("select max(A.sort) from DutySectionEntity A where A.delFlag=false and A.dutyId=:dutyId")
    Integer maxSort(@Param("dutyId") String dutyId);
}
