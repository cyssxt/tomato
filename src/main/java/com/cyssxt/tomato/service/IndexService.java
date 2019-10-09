package com.cyssxt.tomato.service;

import com.cyssxt.common.exception.ValidException;
import com.cyssxt.common.hibernate.transformer.DataTransformer;
import com.cyssxt.common.request.BaseReq;
import com.cyssxt.common.response.PageResponse;
import com.cyssxt.common.response.ResponseData;
import com.cyssxt.common.utils.QueryUtil;
import com.cyssxt.tomato.constant.ContentTypeConstant;
import com.cyssxt.tomato.controller.request.SearchReq;
import com.cyssxt.tomato.dto.*;
import com.cyssxt.tomato.listener.TomatoUserLoginListener;
import org.hibernate.transform.ResultTransformer;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class IndexService {

    @PersistenceContext
    EntityManager entityManager;

    @Resource
    TodoService todoService;

    @Resource
    ProjectService projectService;

    @Resource
    DutyService dutyService;
    public ResponseData search(SearchReq req) throws ValidException {
        String userId = TomatoUserLoginListener.getUserId();
        String sql = "select * from ((select A.create_time as createTime,A.title,A.row_id as rowId,0 as type,B.title as projectTitle,A.action_flag,A.tag_flag,A.status from to_dos A" +
                " left join project_info B on B.row_id=A.parent_id where A.title like :searchKey and A.del_flag!=1 and A.user_id=:userId)" +
                "union all " +
                "(select C.create_time as createTime,C.title,C.row_id as rowId,1 as type,D.title as parentTitle,false as action_flag,false as tag_flag,C.status from project_info C" +
                " left join duty_info D on D.row_id=C.parent_id" +
                " where C.title like :searchKey and C.del_flag!=1 and C.user_id=:userId)" +
                "union all " +
                "(select E.create_time as createTime,E.title,E.row_id as rowId,2 as type,'' as parentTitle,false as action_flag,false as tag_flag,0 as status from duty_info E where E.title like :searchKey and E.del_flag!=1 and E.user_id=:userId )" +
                "union all " +
                "(SELECT G.create_time AS createTime,G.title,G.row_id AS rowId,3 AS type,'' AS parentTitle,FALSE AS action_flag,FALSE AS tag_flag,0 as status FROM user_log G WHERE G.title LIKE :searchKey AND G.del_flag !=1 and G.user_id=:userId)" +
                ") as F";
        PageResponse pageResponse = QueryUtil.applyNativePageWithIct(sql, entityManager, req, (QueryUtil.PageParameter<SearchReq>) (query, searchReq) -> {
            query.setParameter("searchKey", QueryUtil.like(req.getSearchKey()));
            query.setParameter("userId",userId);
        }, SearchResultDto.class);
        ResponseData responseData =  ResponseData.getDefaultSuccessResponse(req);
        responseData.setData(pageResponse);
        return  responseData;
    }

    /**
     * 首页统计数据
     * @param req
     * @return
     * @throws ValidException
     */
    public ResponseData forecast(BaseReq req) throws ValidException {
        String userId = TomatoUserLoginListener.getUserId();
//        Calendar calendar = Calendar.getInstance();
//        String startTime = DateUtils.getDataFormatString(calendar.getTime(),DateUtils.YYYYMMDD);
//        calendar.add(Calendar.DATE,6);
//        String endTime = DateUtils.getDataFormatString(calendar.getTime(),DateUtils.YYYYMMDD);
        String sql ="SELECT ifnull(B.count,A.count) count,A.execute_time AS date,A.week_day AS weekDay FROM ((" +
                "SELECT 0 AS count,`date` AS execute_time,DAYOFWEEK(`date`) AS week_day FROM (" +
                "SELECT @s \\:=@s+1 AS `index`,DATE_FORMAT(DATE(DATE_ADD(CURRENT_DATE,INTERVAL @s DAY)),'%Y%m%d') AS `date` FROM mysql.help_topic,(" +
                "SELECT @s \\:=-1) temp WHERE @s< 5) A) UNION ALL (" +
                "SELECT 0 AS count,'-1' AS date,0 AS week_day)) A LEFT JOIN (" +
                "SELECT count(row_id) AS count,execute_time,week_day FROM (" +
                "SELECT IF (end_time< CURRENT_DATE,-1,DATE_FORMAT(end_time,'%Y%m%d')) AS execute_time,DAYOFWEEK(end_time) AS week_day,row_id,STATUS " +
                "FROM to_dos WHERE del_flag !=1 AND user_id=:userId and status!=2) A GROUP BY execute_time ORDER BY execute_time DESC) B ON A.execute_time=B.execute_time ORDER BY A.execute_time ASC";
        List list = QueryUtil.applyNativeListWithIct(sql, entityManager, (query,req1) -> {
//            query.setParameter("endTime",endTime);
            query.setParameter("userId",userId);
        }, ForecastDto.class);
        ResponseData responseData = ResponseData.getDefaultSuccessResponse(req);
        responseData.setData(list);
        return responseData;
    }

    public ResponseData staticinfo(BaseReq req) throws ValidException {
        String userId = TomatoUserLoginListener.getUserId();
        ResponseData responseData = ResponseData.getDefaultSuccessResponse(req);
        //收件箱统计
        Long inbox = todoService.totalInbox(userId);
        Long totay = todoService.totalTotay(userId);
        responseData.setData(new StaticinfoDto(inbox,totay));
        return responseData;
    }

    public ResponseData parents(SearchReq req) throws ValidException {
        String userId = TomatoUserLoginListener.getUserId();
        StringBuffer sb = new StringBuffer("select * from ((select '收件箱' as title,-1 as row_id,-1 as content_type, 1 as sort FROM dual) " +
                "union all " +
                "(select B.title,row_id,1 content_type,2 as sort from project_info B where B.del_flag=0 and B.user_id=:userId and COALESCE(B.parent_id,'')='') " +
                "union all " +
                "(select C.title,row_id,2 content_type,3 as sort from duty_info C where C.del_flag=0 and C.user_id=:userId)) D");
        String searchKey = req.getSearchKey();
        if(!StringUtils.isEmpty(searchKey)){
            sb.append(" where D.title like '%:searchKey%'");
        }
        sb.append(" order by sort asc");
        List<String> dutyIds = new ArrayList<>();
        ResultTransformer transformer =new DataTransformer(ParentDto.class, new DataTransformer.Filter() {
            @Override
            public void callback(Object result) {
                if(result instanceof ParentDto) {
                    ParentDto parentDto = (ParentDto)result;
                    Byte type = parentDto.getContentType();
                    if(ContentTypeConstant.DUTY.compare(type)){
                        dutyIds.add(parentDto.getRowId());
                    }
                }
            }
        });
        PageResponse pageResponse = QueryUtil.applyNativePage(sb.toString(), entityManager, req, (QueryUtil.PageParameter<SearchReq>) (query, searchReq) -> {
            if(!StringUtils.isEmpty(searchKey)){
                query.setParameter("searchKey",QueryUtil.like(searchKey));
            }
            query.setParameter("userId",userId);
        }, transformer);
        if(!CollectionUtils.isEmpty(dutyIds)) {
            Map<String, List<ProjectDto>> projectDtos = projectService.items(dutyIds);
            List<ParentDto> parentDtos = pageResponse.getItems();
            for (ParentDto parentDto : parentDtos) {
                if (ContentTypeConstant.DUTY.compare(parentDto.getContentType())) {
                    String dutyId = parentDto.getRowId();
                    parentDto.setChilds(projectDtos.get(dutyId));
                }
            }
        }
        ResponseData responseData = ResponseData.getDefaultSuccessResponse(req);
        responseData.setData(pageResponse);
        return responseData;
    }
}
