package com.cyssxt.tomato.service;

import com.cyssxt.common.dao.BaseRepository;
import com.cyssxt.common.exception.ValidException;
import com.cyssxt.common.hibernate.transformer.Filter;
import com.cyssxt.common.hibernate.transformer.IgnoreCaseResultTransformer;
import com.cyssxt.common.hibernate.transformer.KeyTransformer;
import com.cyssxt.common.request.BaseReq;
import com.cyssxt.common.request.PageReq;
import com.cyssxt.common.response.PageResponse;
import com.cyssxt.common.response.ResponseData;
import com.cyssxt.common.utils.CommonUtils;
import com.cyssxt.common.utils.DateUtils;
import com.cyssxt.common.utils.QueryUtil;
import com.cyssxt.common.utils.QuerydslUtils;
import com.cyssxt.tomato.constant.ContentTypeConstant;
import com.cyssxt.tomato.controller.request.InfoReq;
import com.cyssxt.tomato.controller.request.TagCreateReq;
import com.cyssxt.tomato.controller.request.TagPageReq;
import com.cyssxt.tomato.controller.request.TagTodoPageReq;
import com.cyssxt.tomato.dao.ReTagRepository;
import com.cyssxt.tomato.dao.TagRepository;
import com.cyssxt.tomato.dto.TagDto;
import com.cyssxt.tomato.dto.TagResultDto;
import com.cyssxt.tomato.dto.TotalCount;
import com.cyssxt.tomato.entity.ReTagEntity;
import com.cyssxt.tomato.entity.TagInfoEntity;
import com.cyssxt.tomato.listener.TomatoUserLoginListener;
import com.gexin.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TagService extends ActionService<TagInfoEntity, TagCreateReq, TagPageReq, TagDto,TagDto>{

    private final static Logger logger = LoggerFactory.getLogger(TagService.class);

    public final static TagDto NO_TAG = new TagDto("-1","无标签");

    @Resource
    TagRepository tagRepository;

    @Resource
    ReTagRepository reTagRepository;

    @Override
    public BaseRepository getRepository() {
        return tagRepository;
    }

    @Override
    public Byte getContentType() {
        return ContentTypeConstant.TAG.getValue();
    }

    @Transactional
    public void updateReTags(List<String> tagIds, String contentId, ContentTypeConstant contentTypeConstant){
        Byte contentType = contentTypeConstant.getValue();
        logger.info("update retags={},contentId={},contentType={}",tagIds,contentId,contentType);
        List<String> reTagIds = new ArrayList<>();
        if(CollectionUtils.isEmpty(tagIds)){
            reTagRepository.delByContentId(contentId,contentType);
            return;
        }
        for(String tagId:tagIds) {
            ReTagEntity reTagEntity = reTagRepository.findFirstByTagIdAndContentIdAndContentType(tagId,contentId,contentType);
            if(reTagEntity==null){
                reTagEntity = new ReTagEntity();
            }
            String reTagId = reTagEntity.getRowId();
            reTagEntity.setContentId(contentId);
            reTagEntity.setContentType(contentType);
            reTagEntity.setTagId(tagId);
            reTagEntity.setDelFlag(false);
            logger.info("tagId={}", JSON.toJSONString(reTagEntity));
            reTagRepository.save(reTagEntity);
            reTagIds.add(reTagId);
        }
        if(!CollectionUtils.isEmpty(reTagIds)) {
            logger.info("reTagIds={},contentId={},contentType={}",reTagIds,contentId,contentType);
            reTagRepository.updateTag(reTagIds, contentId, contentType);
        }

    }

    public void copyTags(String oldId,String newId){
        List<ReTagEntity> reTagEntitis = reTagRepository.tags(oldId);
        List<String> reTagIds = new ArrayList<>();
        for(ReTagEntity reTagEntity:reTagEntitis){
            String tagId = reTagEntity.getTagId();
            ReTagEntity newInfo = reTagRepository.findFirstByTagIdAndContentIdAndContentType(tagId,newId,ContentTypeConstant.TODO.getValue());
            String rowId;
            if(newInfo==null) {
                newInfo = (ReTagEntity)reTagEntity.clone();
                rowId = CommonUtils.generatorKey();
                newInfo.setRowId(rowId);
            }else{
                newInfo.setDelFlag(false);
                rowId = newInfo.getRowId();
            }
            newInfo.setContentId(newId);
            newInfo.setUpdateTime(DateUtils.getCurrentTimestamp());
            reTagRepository.save(newInfo);
            reTagIds.add(rowId);
        }
        if(!CollectionUtils.isEmpty(reTagIds)) {
            reTagRepository.delOther(newId, reTagIds);
        }else{
            reTagRepository.delByContentId(newId,ContentTypeConstant.TODO.getValue());
        }
    }

    @Override
    public TagInfoEntity createEntity(TagCreateReq tagCreateReq) {
        TagInfoEntity tagInfoEntity =  new TagInfoEntity();
        return tagInfoEntity;
    }


    @Override
    protected Class getDtoClass() {
        return TagDto.class;
    }

    @Override
    public void where(TagPageReq req, List<String> list) {
        String searchKey = req.getSearchKey();
        if(!StringUtils.isEmpty(searchKey)){
            list.add("A.tag_name like :searchKey");
        }
    }

    @Override
    public boolean filterUserId() {
        return false;
    }

    @Override
    public boolean filterDelFlag() {
        return false;
    }

    @Override
    public boolean filterTagId() {
        return false;
    }

    @Override
    public String orderBy(TagPageReq req) {
        return " A.type asc,A.create_time desc";
    }

    @Override
    public String getListSql(TagPageReq req) {
        return "select tag_id,tag_name,0 as del_flag from " +
                "((select -1 as tag_id,'无标签' as tag_name,0 as type,now() as create_time) union all " +
                "(select row_id,tag_name,1 as type,A.create_time from tag_info A where  (A.del_flag!=1 or A.del_flag is null) and A.user_id=:userId)) A ";
    }

    @Override
    public TagDto detail(InfoReq req) throws ValidException {
        return null;
    }

    public List<TagDto> tags(List<String> contentIds,ContentTypeConstant contentTypeConstant){
        if(CollectionUtils.isEmpty(contentIds)){
            return new ArrayList<>();
        }
        return tagRepository.tags(contentIds,contentTypeConstant.getValue());
    }

    public Map<String,List<TagDto>> tagToMap(List<String> contentIds,ContentTypeConstant contentTypeConstant){
        if(CollectionUtils.isEmpty(contentIds)){
            return new HashMap<>();
        }
        List<TagDto> tagDtos =  tagRepository.tags(contentIds,contentTypeConstant.getValue());
        return tagDtos.stream().collect(Collectors.groupingBy(TagDto::getContentId));
    }

    public List<TagDto> tags(String contentId,ContentTypeConstant contentTypeConstant){
        return tagRepository.tags(contentId,contentTypeConstant.getValue());
    }

    @Override
    public int batchDel(List<String> contentIds, String userId) {
        if(CollectionUtils.isEmpty(contentIds)){
            return 0;
        }
        return tagRepository.batchDel(contentIds,userId);
    }

    public void copy(String contentId,String newContentId,Byte type){
        List<ReTagEntity> reTagEntities = reTagRepository.findByContentIdAndContentTypeAndDelFlagFalse(contentId,type);
        Timestamp now = DateUtils.getCurrentTimestamp();
        for(ReTagEntity reTagEntity:reTagEntities){
            ReTagEntity copy = (ReTagEntity) reTagEntity.clone();
            copy.setRowId(CommonUtils.generatorKey());
            copy.setCreateTime(now);
            copy.setUpdateTime(now);
            copy.setContentId(newContentId);
            reTagRepository.save(copy);
        }
    }

    public ResponseData todos(TagTodoPageReq req) throws ValidException {
        String tagId = req.getTagId();
        String userId = TomatoUserLoginListener.getUserId();
        StringBuffer normalSql = new StringBuffer("select * from ((select A.row_id,A.title,A.action_flag,A.tag_flag,A.STATUS,A.end_time,0 as content_type from to_dos A where A.del_flag=0 and A.user_id=:userId )\n" +
                "union all\n" +
                "(select A.row_id,A.title,false as action_flag,TRUE AS tag_flag,A.STATUS,A.end_time,1 as content_type from project_info A where A.del_flag=0  and A.user_id=:userId ))B");
        normalSql.append(" where B.row_id in (select T.content_id from "+TodoService.filterTags()+" T where T.tag_id=:tagId and T.content_type=B.content_type )");
        String normal = normalSql.toString()+" and (B.end_time is null or B.end_time>=now()) ";
        String overtime = normalSql.toString()+" and (B.end_time is not null and B.end_time<now()) ";
        QueryUtil.PageParameter reqParameter = new QueryUtil.PageParameter<PageReq>() {
            @Override
            public void initParam(Query query, PageReq req) throws ValidException {
                query.setParameter("userId",userId);
                query.setParameter("tagId",tagId);
            }
        };
        long normalTotal = QueryUtil.applyTotal(normal, entityManager,null,reqParameter);
        long overtimeTotal = QueryUtil.applyTotal(overtime,entityManager,null,reqParameter);
        List<String> todoIds = new ArrayList<>();
        List<String> projectIds = new ArrayList<>();
        KeyTransformer keyTransformer = new IgnoreCaseResultTransformer(TagResultDto.class, (Filter<TagResultDto>) tagResultDto -> {
            Byte contentType = tagResultDto.getContentType();
            String rowId = tagResultDto.getRowId();
            if(ContentTypeConstant.TODO.compare(contentType)){
                todoIds.add(rowId);
            }else if(ContentTypeConstant.PROJECT.compare(contentType)){
                projectIds.add(rowId);
            }
        });
        normalSql.append(" order by B.end_time desc ");
        PageResponse<TagResultDto> pageResponse  = QueryUtil.applyNativePage(normalSql.toString(), entityManager, req,reqParameter,keyTransformer);
        List<TagDto> todoTagsDtos = new ArrayList<>();
        if(!CollectionUtils.isEmpty(todoIds)){
            todoTagsDtos = tags(todoIds,ContentTypeConstant.TODO);
        }
        List<TagDto> todoProjectDtos = new ArrayList<>();
        if(!CollectionUtils.isEmpty(projectIds)){
            todoTagsDtos = tags(projectIds,ContentTypeConstant.PROJECT);
        }
        Map<String,List<TagDto>> todoTagMap = todoTagsDtos.stream().collect(Collectors.groupingBy(TagDto::getContentId));
        Map<String,List<TagDto>> projectTagMap = todoProjectDtos.stream().collect(Collectors.groupingBy(TagDto::getContentId));
        List<TagResultDto> tagResultDtos = pageResponse.getItems();
        if(!CollectionUtils.isEmpty(tagResultDtos)) {
            for (TagResultDto tagResultDto:tagResultDtos){
                Byte contentType = tagResultDto.getContentType();
                String rowId = tagResultDto.getRowId();
                if(ContentTypeConstant.TODO.compare(contentType)){
                    tagResultDto.setTags(todoTagMap.get(rowId));
                }else if(ContentTypeConstant.PROJECT.compare(contentType)){
                    tagResultDto.setTags(projectTagMap.get(rowId));
                }
            }
        }
        ResponseData responseData = ResponseData.getDefaultSuccessResponse(req);
        responseData.setData(pageResponse);
        responseData.setExtra(new TotalCount(normalTotal,overtimeTotal));
        return responseData;
    }
}
