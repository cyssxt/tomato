package com.cyssxt.tomato.dao;

import com.cyssxt.common.dao.BaseRepository;
import com.cyssxt.tomato.dto.TagDto;
import com.cyssxt.tomato.entity.ReTagEntity;
import com.cyssxt.tomato.entity.TagInfoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface TagRepository extends BaseRepository<TagInfoEntity> {
    @Query("select new com.cyssxt.tomato.dto.TagDto(A.rowId,A.tagName) from TagInfoEntity A where " +
            " A.delFlag=false or A.delFlag is null and A.userId=:userId ")
    Page<TagDto> list(@Param("userId") String userId, Pageable toPageable);

    @Query("select new com.cyssxt.tomato.dto.TagDto(A.rowId,A.tagName) from TagInfoEntity A where " +
            " (A.delFlag=false or A.delFlag is null) and A.tagName like :searchKey and A.userId=:userId ")
    Page<TagDto> list(@Param("userId") String userId,@Param("searchKey")String searchKey, Pageable toPageable);

    @Query("select new com.cyssxt.tomato.dto.TagDto(A.rowId,A.tagName,B.contentId) from TagInfoEntity A,ReTagEntity B where " +
            " (A.delFlag=false or A.delFlag is null) and B.delFlag=false and B.contentId in :contentIds and B.contentType=:type " +
            " and A.rowId=B.tagId")
    List<TagDto> tags(@Param("contentIds") List<String> contentIds,@Param("type")Byte type);

    @Query("select new com.cyssxt.tomato.dto.TagDto(A.rowId,A.tagName,B.contentId) from TagInfoEntity A,ReTagEntity B where " +
            " (A.delFlag=false or A.delFlag is null)  and B.delFlag=false and B.contentId=:contentId and B.contentType=:type " +
            " and A.rowId=B.tagId")
    List<TagDto> tags(@Param("contentId") String contentId,@Param("type")Byte type);

    @Transactional
    @Modifying
    @Query("update TagInfoEntity set delFlag=true where rowId in :contentIds and userId=:userId ")
    int batchDel(@Param("contentIds") List<String> contentIds,@Param("userId")String userId);
}
