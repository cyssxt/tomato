package com.cyssxt.tomato.dao;

import com.cyssxt.common.dao.BaseRepository;
import com.cyssxt.tomato.controller.request.UserLogMonthDto;
import com.cyssxt.tomato.controller.request.UserLogPageReq;
import com.cyssxt.tomato.dto.UserLogDto;
import com.cyssxt.tomato.entity.UserLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserLogRepository extends BaseRepository<UserLogEntity> {
    @Query("select new com.cyssxt.tomato.dto.UserLogDto(A.rowId,A.createTime,A.title,A.introduce,A.imgUrl) from UserLogEntity A where " +
            " (A.delFlag=false or A.delFlag is null) and A.userId=:userId order by A.createTime desc ")
    Page<UserLogDto> list(@Param("userId") String userId, Pageable pageable);


    @Query("select new com.cyssxt.tomato.dto.UserLogDto(A.rowId,A.createTime,A.title,A.introduce,A.imgUrl) from UserLogEntity A where " +
            " (A.delFlag=false or A.delFlag is null) and A.userId=:userId order by A.updateTime desc ")
    Page<UserLogDto> listByUpdateTime(@Param("userId") String userId, Pageable pageable);

    @Query("select new com.cyssxt.tomato.dto.UserLogDto(A.rowId,A.updateTime,A.title,A.introduce,A.imgUrl) from UserLogEntity A where " +
            " (A.delFlag=false or A.delFlag is null) and A.monthNo=:monthNo and A.userId=:userId order by A.createTime desc ")
    List<UserLogDto> list(@Param("monthNo")Integer monthNo,@Param("userId")String userId);

    @Query("select new com.cyssxt.tomato.dto.UserLogDto(A.rowId,A.updateTime,A.title,A.introduce,A.imgUrl) from UserLogEntity A where " +
            " (A.delFlag=false or A.delFlag is null) and A.monthNo=:monthNo and A.userId=:userId order by A.updateTime desc ")
    List<UserLogDto> listUpdateTime(@Param("monthNo")Integer monthNo,@Param("userId")String userId);

    @Query("select monthNo from UserLogEntity where userId=:userId and delFlag=false group by monthNo order by monthNo desc")
    Page<String> months(@Param("userId") String userId, Pageable pageable);
}
