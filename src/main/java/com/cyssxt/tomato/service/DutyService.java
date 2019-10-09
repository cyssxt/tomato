package com.cyssxt.tomato.service;

import com.cyssxt.common.dao.BaseRepository;
import com.cyssxt.common.exception.ValidException;
import com.cyssxt.common.hibernate.transformer.DataTransformer;
import com.cyssxt.common.hibernate.transformer.IgnoreCaseResultTransformer;
import com.cyssxt.common.hibernate.transformer.KeyTransformer;
import com.cyssxt.common.request.BaseReq;
import com.cyssxt.common.request.PageReq;
import com.cyssxt.common.response.ResponseData;
import com.cyssxt.common.utils.JpaUtil;
import com.cyssxt.common.utils.QueryUtil;
import com.cyssxt.tomato.constant.ContentTypeConstant;
import com.cyssxt.tomato.controller.request.*;
import com.cyssxt.tomato.dao.ColorRepository;
import com.cyssxt.tomato.dao.DutyRepository;
import com.cyssxt.tomato.dto.*;
import com.cyssxt.tomato.entity.ColorsEntity;
import com.cyssxt.tomato.entity.ColorsEntity_;
import com.cyssxt.tomato.entity.DutyInfoEntity;
import org.hibernate.transform.ResultTransformer;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DutyService extends ActionService<DutyInfoEntity,DutyCreateReq, DutyPageReq, DutyDto, DutyInfoDto>{

    @Resource
    DutyRepository dutyRepository;

    @Resource
    ColorRepository colorRepository;

    @Resource
    ProjectService projectService;

    @Resource
    TodoService todoService;

    @Resource
    SectionService sectionService;

    @Resource
    TagService tagService;

    @Override
    public BaseRepository getRepository() {
        return dutyRepository;
    }

    @Override
    public Byte getContentType() {
        return ContentTypeConstant.DUTY.getValue();
    }

    @Override
    public DutyInfoEntity createEntity(DutyCreateReq dutyCreateReq) {
        return new DutyInfoEntity();
    }

    @Override
    protected Class getDtoClass() {
        return DutyDto.class;
    }

    @Override
    public String getListSql(DutyPageReq req) {
        return "select A.row_id,A.create_time,A.title,A.color from duty_info A";
    }

    @Override
    public DutyInfoDto detail(InfoReq req) throws ValidException {
        String contentId = req.getContentId();
        DutyInfoDto dutyInfoDto = new DutyInfoDto();
        DutyInfoEntity dutyInfoEntity = JpaUtil.check(contentId,getRepository(),true);
        List<TagDto> tags = tagService.tags(contentId,ContentTypeConstant.DUTY);
        dutyInfoDto.setTags(tags);
        dutyInfoDto.setTitle(dutyInfoEntity.getTitle());
        dutyInfoDto.setColor(dutyInfoEntity.getColor());
        dutyInfoDto.setItems(sections(contentId));
        return dutyInfoDto;
    }

    public ResponseData colors(BaseReq req) {
        List<ColorsEntity> colorsEntities = colorRepository.findAll((Specification<ColorsEntity>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.notEqual(root.get(ColorsEntity_.delFlag),true));
            Predicate[] temp = new Predicate[predicates.size()];
            query.where(predicates.toArray(temp));
            return query.getRestriction();
        });
        ResponseData responseData = ResponseData.getDefaultSuccessResponse(req);
        responseData.setData(colorsEntities);
        return responseData;
    }

    public List<DutySectionDto> sections(String dutyId) throws ValidException {
        String sql = "select C.repeat_flag,A.content_id,A.content_type,if(A.content_type=1,B.title,C.title) title," +
                "if(A.content_type=1,B.status,C.status) status," +
                "if(A.content_type=1,B.execute_time,C.execute_time) execute_time," +
                "if(A.content_type=1,B.end_time,C.repeat_end_time) end_time," +
                "if(V.action_count>0,true,false) action_flag,if(W.tag_count>0,true,false) tag_flag from duty_section A " +
                "left join project_info B on B.row_id=A.content_id and A.content_type=1 and B.del_flag=0 " +
                "left join to_dos C on C.row_id=A.content_id and A.content_type=0 and C.del_flag=0  " +
                "left join (select count(T.row_id) action_count,T.to_do_id  from to_do_actions T where T.del_flag=0 group by T.to_do_id) V on V.to_do_id=C.row_id " +
                "left join (select count(R.tag_id) as tag_count,R.content_id from re_tag R where R.del_flag=0 and R.content_type=0 group by R.content_id) W on W.content_id=C.row_id " +
                "where A.duty_id=:dutyId and (ifnull(C.row_id,'')!='' or ifnull(B.row_id,'')!='') and A.del_flag=0 order by A.sort asc";
        List<String> todoIds = new ArrayList<>();
        List<String> projectIds = new ArrayList<>();
        ResultTransformer keyTransformer = new DataTransformer(DutySectionDto.class, result -> {
            if(result instanceof DutySectionDto){
                DutySectionDto dutySectionDto = ((DutySectionDto) result);
                Byte contentType = dutySectionDto.getContentType();
                String rowId = dutySectionDto.getContentId();
                if(ContentTypeConstant.PROJECT.compare(contentType)){
                    projectIds.add(rowId);
                }
                if(ContentTypeConstant.TODO.compare(contentType)){
                    todoIds.add(rowId);
                }
            }
        });
        List<DutySectionDto> items = QueryUtil.applyNativeList(sql, null, entityManager, (QueryUtil.PageParameter<PageReq>) (query, pageReq) -> query.setParameter("dutyId",dutyId),keyTransformer);
        Map<String,List<TagDto>> todoTags = tagService.tagToMap(todoIds,ContentTypeConstant.TODO);
        Map<String,List<TagDto>> projectTags = tagService.tagToMap(todoIds,ContentTypeConstant.PROJECT);
        for(DutySectionDto dutySectionDto:items) {
            String contentId = dutySectionDto.getContentId();
            Byte contentType = dutySectionDto.getContentType();
            List<TagDto> tagDtos = null;//todoTags.get(contentId);
            if (ContentTypeConstant.PROJECT.compare(contentType)){
                tagDtos = projectTags.get(contentId);
            }
            if (ContentTypeConstant.TODO.compare(contentType)){
                tagDtos = todoTags.get(contentId);
            }
            if(tagDtos==null){
                tagDtos = new ArrayList<>();
                tagDtos.add(TagService.NO_TAG);
            }
            dutySectionDto.setTags(tagDtos);
        }
        return items;
    }


    public ResponseData save(DutySaveReq req) throws ValidException {
        String contentId = req.getContentId();
        DutyInfoEntity dutyInfoEntity = JpaUtil.check(contentId,getRepository(),true);
        dutyInfoEntity.setTitle(req.getTitle());
        dutyInfoEntity.setColor(req.getColor());
        sectionService.updateDutySections(contentId,req.getItems());
        tagService.updateReTags(req.getTagIds(),contentId,ContentTypeConstant.DUTY);
        dutyRepository.save(dutyInfoEntity);
        return ResponseData.getDefaultSuccessResponse(req);
    }

    @Override
    public void onCopy(DutyInfoEntity dutyInfoEntity, String oldId,String parentId,Byte parentType) throws ValidException {
        super.onCopy(dutyInfoEntity, oldId,parentId,parentType);
        List<DutySectionDto> items = sections(oldId);
        for(DutySectionDto dutySectionDto:items){//拷贝子任务
            String contentId = dutySectionDto.getContentId();
            MoveReq req = new MoveReq(contentId,parentType,parentId);
            if(ContentTypeConstant.PROJECT.compare(dutySectionDto.getContentType())){
                projectService.copy(req);
            }else if(ContentTypeConstant.TODO.compare(dutySectionDto.getContentType())){
                todoService.copy(req);
            }
        }
    }

}
