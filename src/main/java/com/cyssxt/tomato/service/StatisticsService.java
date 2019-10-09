package com.cyssxt.tomato.service;

import com.cyssxt.common.exception.ValidException;
import com.cyssxt.common.request.BaseReq;
import com.cyssxt.common.response.ResponseData;
import com.cyssxt.common.utils.QueryUtil;
import com.cyssxt.tomato.constant.SQL_NAME_CONSTANT;
import com.cyssxt.tomato.constant.TimeTypeConstant;
import com.cyssxt.tomato.controller.request.FinishListReq;
import com.cyssxt.tomato.controller.request.TimeDistributeReq;
import com.cyssxt.tomato.dto.*;
import com.cyssxt.tomato.listener.TomatoUserLoginListener;
import com.cyssxt.tomato.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class StatisticsService {
    private final static Logger logger = LoggerFactory.getLogger(StatisticsService.class);

    @Resource
    TodoService todoService;

    @Resource
    ProjectService projectService;

    @PersistenceContext
    EntityManager entityManager;

    public ResponseData timeDistribute(TimeDistributeReq req) throws ValidException {
        logger.info("timeDistribute={}", req);
        ResponseData responseData = ResponseData.getDefaultSuccessResponse(req);
        String userId = TomatoUserLoginListener.getUserId();
        TimeArea time = TimeUtil.getTimeArea(req.getSequence(),TimeTypeConstant.get(req.getType()));
        time.setFormat(req.getFormat());
        Integer start = time.getStartDateNo();
        Integer end = time.getEndDateNo();
        Timestamp startTime = time.getStart();
        Timestamp endTime = time.getEnd();
        logger.info("startTime={},endTime={},userId={}",startTime,endTime,userId);
        TimeDto timeDto = todoService.timeDistribute(start, end, userId);//待办统计
        Long total = projectService.totals(startTime, endTime, userId);
        List<DutyDistributeDto> duties = todoService.dutyDistribute(start, end, userId);//责任统计
        TimeDistributeDto timeDistributeDto = new TimeDistributeDto(timeDto, duties, total);
        responseData.setData(timeDistributeDto);
        responseData.setExtra(time);
        return responseData;
    }

    public ResponseData conumTime(ConsumTimeReq req) throws ValidException {
        String userId = TomatoUserLoginListener.getUserId();
        Integer monthNo = Optional.ofNullable(req.getMonthNo()).orElse(0);
        TimeArea timeArea = TimeUtil.getTimeArea(monthNo,TimeTypeConstant.MONTH);
        timeArea.setFormat(req.getFormat());
        final Integer startDateNo = timeArea.getStartDateNo();
        final Integer endDateNo = timeArea.getEndDateNo();
        String sql = "select sum(COALESCE(A.consum_time,0)) as consum_time,A.date_no from to_do_day_info A,to_dos B where A.date_no>=:startDateNo and A.date_no<=:endDateNo " +
                "and A.del_flag=0 and B.del_flag=0 and A.user_id=:userId " +
                "group by date_no order by date_no desc";
        List<ConsumTimeDto> result = QueryUtil.applyNativeListWithIct(sql, entityManager, new QueryUtil.ReqParameter() {
            @Override
            public void initParam(Query query, BaseReq req) throws ValidException {
                query.setParameter("userId",userId);
                query.setParameter("startDateNo",startDateNo);
                query.setParameter("endDateNo",endDateNo);
            }
        }, ConsumTimeDto.class);
        ResponseData responseData = ResponseData.getDefaultSuccessResponse(req);
        responseData.setData(result);
        responseData.setExtra(timeArea);
        return responseData;
    }

    public ResponseData finishDegrees(FinishListReq req) throws ValidException {
        ResponseData responseData = ResponseData.getDefaultSuccessResponse(req);
        String userId = TomatoUserLoginListener.getUserId();
        TimeArea timeArea = TimeUtil.getTimeArea(req.getSequence(),TimeTypeConstant.get(req.getType()));
        timeArea.setFormat(req.getFormat());
        List<FinishResultDto> result = QueryUtil.applyNativeListWithIctByFile(SQL_NAME_CONSTANT.FINISH_DEGREE, entityManager, (query, req1) -> {
            query.setParameter("userId",userId);
            query.setParameter("startDateNo",timeArea.getStartDateNo());
            query.setParameter("endDateNo",timeArea.getEndDateNo());
        },FinishResultDto.class);
        responseData.setData(result);
        responseData.setExtra(timeArea);
        return responseData;
    }
}
