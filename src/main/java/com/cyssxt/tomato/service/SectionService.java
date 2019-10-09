package com.cyssxt.tomato.service;

import com.cyssxt.common.exception.ValidException;
import com.cyssxt.common.utils.JpaUtil;
import com.cyssxt.tomato.dao.DutySectionRepository;
import com.cyssxt.tomato.dao.ProjectSectionRepository;
import com.cyssxt.tomato.dto.DutySectionItem;
import com.cyssxt.tomato.dto.ProjectSectionDto;
import com.cyssxt.tomato.entity.DutySectionEntity;
import com.cyssxt.tomato.entity.ProjectSectionEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Service
public class SectionService {

    @Resource
    ProjectSectionRepository projectSectionRepository;

    @Resource
    DutySectionRepository dutySectionRepository;
    private final static Integer MAX_SORT = 9999;


    public List<ProjectSectionDto> sections(String projectId){
        if(StringUtils.isEmpty(projectId)){
           return null;
        }
        return  projectSectionRepository.items(projectId);
    }

    public void updateTodoSection(String projectId, String todoId) throws ValidException {
        createSection(null,null,null,projectId,MAX_SORT,todoId);
    }

    public void delTodoSection(String projectId, String todoId) throws ValidException {
        ProjectSectionEntity projectSectionEntity = projectSectionRepository.findFirstByTodoIdAndProjectId(todoId,projectId);
        if(projectSectionEntity==null){
            return;
        }
        projectSectionEntity.setDelFlag(true);
        projectSectionRepository.save(projectSectionEntity);
    }
    public void createSection(List<String> sectionIds,String itemId,String title,String projectId,Integer sort,String todoId) throws ValidException {
        ProjectSectionEntity projectSectionEntity = null;//projectSectionRepository.findFirstByTodoIdAndProjectId(todoId,projectId);
        if(!StringUtils.isEmpty(itemId)){
            projectSectionEntity = JpaUtil.check(itemId,projectSectionRepository,false);
            if(projectSectionEntity!=null) {
                projectSectionEntity.setDelFlag(false);
            }else {
                projectSectionEntity = new ProjectSectionEntity();
            }
        }else{
            if(!StringUtils.isEmpty(todoId)) {
                projectSectionEntity = projectSectionRepository.findFirstByTodoIdAndProjectId(todoId, projectId);
            }else{
                if(!StringUtils.isEmpty(title)){
                    projectSectionEntity = projectSectionRepository.findFirstByTitleAndProjectId(title, projectId);
                }
            }
            if(projectSectionEntity==null){
                projectSectionEntity = new ProjectSectionEntity();
                itemId = projectSectionEntity.getRowId();
            }else {
                projectSectionEntity.setDelFlag(false);
            }
        }
        if(sectionIds!=null && !StringUtils.isEmpty(itemId)) {
            sectionIds.add(itemId);
        }
        projectSectionEntity.setTitle(title);
        projectSectionEntity.setProjectId(projectId);
        Integer oldSort = projectSectionEntity.getSort();
        sort = sort.intValue()==MAX_SORT.intValue()?(oldSort==null?nextProjectSort(projectId):oldSort):sort;
        sort = sort.intValue()==MAX_SORT.intValue()?nextProjectSort(projectId):sort;
        projectSectionEntity.setSort(sort);
        projectSectionEntity.setTodoId(todoId);
        projectSectionRepository.save(projectSectionEntity);
    }
    public Integer nextDutySort(String contentId){
        Integer maxSort = dutySectionRepository.maxSort(contentId);
        maxSort = Optional.ofNullable(maxSort).orElse(1);
        return maxSort+1;
    }

    public Integer nextProjectSort(String contentId){
        Integer maxSort = projectSectionRepository.maxSort(contentId);
        maxSort = Optional.ofNullable(maxSort).orElse(MAX_SORT);
        return maxSort+1;
    }

    public void updateDutySection(String contentId,Byte contentType,String dutyId){
        updateDutySection(contentId,contentType,dutyId,MAX_SORT);
    }
    public void updateDutySection(String contentId,Byte contentType,String dutyId,Integer sort){
        DutySectionEntity dutySectionEntity = dutySectionRepository.findFirstByContentIdAndContentTypeAndDutyId(contentId,contentType,dutyId);
        if(dutySectionEntity==null){
            dutySectionEntity = new DutySectionEntity();
            dutySectionEntity.setContentId(contentId);
            dutySectionEntity.setContentType(contentType);
            dutySectionEntity.setDutyId(dutyId);
        }else{
            dutySectionEntity.setDelFlag(false);
        }
        Integer oldSort = dutySectionEntity.getSort();
        sort = sort.intValue() == MAX_SORT.intValue() ? (oldSort==null?nextDutySort(dutyId):oldSort) : sort;
        dutySectionEntity.setSort(sort);
        dutySectionRepository.save(dutySectionEntity);
    }

    public void updateDutySections(String dutyId,List<DutySectionItem> items){
        if(CollectionUtils.isEmpty(items)){
            return;
        }
        for(int i=0;i< items.size();i++){
            DutySectionItem item = items.get(i);
            String contentId = item.getContentId();
            Byte contentType = item.getContentType();
            updateDutySection(contentId,contentType,dutyId,i);
        }
    }

    public void delDutySection(String dutyId,String contentId,Byte contentType){
        DutySectionEntity dutySectionEntity = dutySectionRepository.findFirstByContentIdAndContentTypeAndDutyId(contentId,contentType,dutyId);
        if(dutySectionEntity!=null){
            dutySectionEntity.setDelFlag(false);
            dutySectionRepository.save(dutySectionEntity);
        }

    }

}
