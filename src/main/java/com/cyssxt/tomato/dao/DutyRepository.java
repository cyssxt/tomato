package com.cyssxt.tomato.dao;

import com.cyssxt.common.dao.BaseRepository;
import com.cyssxt.tomato.dto.DutyDistributeDto;
import com.cyssxt.tomato.dto.DutyDto;
import com.cyssxt.tomato.entity.DutyInfoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DutyRepository extends BaseRepository<DutyInfoEntity> {
    @Query("select new com.cyssxt.tomato.dto.DutyDto(A.rowId,A.createTime,A.title,A.color) from DutyInfoEntity A where " +
            " (A.delFlag=false or A.delFlag is null) and A.userId=:userId order by A.createTime")
    Page<DutyDto> list(@Param("userId") String userId, Pageable toPageable);

    @Query("from DutyInfoEntity A where A.delFlag=0 ")
    List<DutyDistributeDto> distribute();
}
