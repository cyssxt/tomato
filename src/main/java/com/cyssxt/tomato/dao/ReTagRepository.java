package com.cyssxt.tomato.dao;

import com.cyssxt.common.dao.BaseRepository;
import com.cyssxt.tomato.entity.ReTagEntity;
import com.cyssxt.tomato.entity.TagInfoEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface ReTagRepository extends BaseRepository<ReTagEntity> {

    ReTagEntity findFirstByTagIdAndContentIdAndContentTypeAndDelFlagFalse(String tagId,String contentId,Byte contentType);

    List<ReTagEntity> findByContentIdAndContentTypeAndDelFlagFalse(String contentId,Byte contentType);
    @Modifying
    @Query("update ReTagEntity set delFlag=true where rowId not in :notTagIds and contentId=:contentId and contentType=:contentType")
    int updateTag(@Param("notTagIds") List<String> notTagIds, @Param("contentId") String contentId, @Param("contentType") Byte contentType);

    @Query("from ReTagEntity where contentId=:todoId and delFlag=false")
    List<ReTagEntity> tags(@Param("todoId")String todoId);

    @Modifying
    @Transactional
    @Query("update ReTagEntity set delFlag=true where contentId=:todoId and rowId not in :reIds and delFlag=false")
    int delOther(@Param("todoId")String todoId,@Param("reIds") List<String> reIds);

    ReTagEntity findFirstByTagIdAndContentIdAndContentType(String tagId,String contentId,Byte contentType);

    @Modifying
    @Query("update ReTagEntity set delFlag=true where contentId=:contentId and contentType=:contentType")
    @Transactional
    int delByContentId(@Param("contentId") String contentId, @Param("contentType") Byte contentType);
//    @Modifying
//    @Query("update ReTagEntity set delFlag=true where rowId not in :rowIds and contentId=:contentId and contentType=:contentType")
//    int updateTag(@Param("rowIds") List<String> rowIds,@Param("contentId")String contentId,@Param("contentType")Byte contentType);
}
