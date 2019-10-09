package com.cyssxt.tomato.service;

import com.cyssxt.common.dao.BaseRepository;
import com.cyssxt.common.exception.ValidException;
import com.cyssxt.common.hibernate.transformer.StringTransformer;
import com.cyssxt.common.request.BaseReq;
import com.cyssxt.common.request.PageReq;
import com.cyssxt.common.response.PageResponse;
import com.cyssxt.common.response.ResponseData;
import com.cyssxt.common.utils.DateUtils;
import com.cyssxt.common.utils.QueryUtil;
import com.cyssxt.tomato.constant.ContentTypeConstant;
import com.cyssxt.tomato.constant.UserLogOrderTypeConstant;
import com.cyssxt.tomato.controller.request.InfoReq;
import com.cyssxt.tomato.controller.request.MonthLogReq;
import com.cyssxt.tomato.controller.request.UserCreateReq;
import com.cyssxt.tomato.controller.request.UserLogPageReq;
import com.cyssxt.tomato.dao.UserLogRepository;
import com.cyssxt.tomato.dto.UserLogDto;
import com.cyssxt.tomato.dto.UserLogInfoDto;
import com.cyssxt.tomato.entity.UserLogEntity;
import com.cyssxt.tomato.listener.TomatoUserLoginListener;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.persistence.Query;
import java.util.List;

@Service
public class UserLogService extends ActionService<UserLogEntity, UserCreateReq, UserLogPageReq, UserLogDto, UserLogInfoDto>{

    @Resource
    UserLogRepository userLogRepository;
    @Override
    public BaseRepository getRepository() {
        return userLogRepository;
    }

    @Override
    public Byte getContentType() {
        return ContentTypeConstant.USRLOG.getValue();
    }

    @Override
    public UserLogEntity createEntity(UserCreateReq userCreateReq) {
        UserLogEntity userLogEntity = new UserLogEntity();
        String dateNo = DateUtils.getCurrentDateFormatStr(DateUtils.YYYYMM);
        //生成日期
        userLogEntity.setMonthNo(Integer.valueOf(dateNo));
        return userLogEntity;
    }

    @Override
    protected Class getDtoClass() {
        return UserLogDto.class;
    }

//    @Override
//    public PageResponse<UserLogDto> getResult(UserLogPageReq req, String userId) {
//        Page<UserLogDto> page = null;
//        if(UserLogOrderTypeConstant.CREATE_TIME.compare(req.getOrderType())){
//            page = userLogRepository.list(userId,req.toPageable());
//        }else{
//            page = userLogRepository.listByUpdateTime(userId,req.toPageable());
//        }
//        return new PageResponse<>(page);
//    }


    @Override
    public String orderBy(UserLogPageReq req) {
        if(UserLogOrderTypeConstant.CREATE_TIME.compare(req.getOrderType())){
            return "A.create_time desc";
        }else{
            return "A.update_time desc";
        }
    }

    @Override
    public String getListSql(UserLogPageReq req) {
        return "select A.row_id,A.create_time,A.title,A.introduce,A.img_url from user_log A";
    }

//    @Override
//    public Class getListSqlClass() {
//        return UserLogDto.class;
//    }

    @Override
    public UserLogInfoDto detail(InfoReq req) {
        // TODO: 2019-01-15
        return null;
    }

    public ResponseData months(PageReq req) throws ValidException {
        String userId = TomatoUserLoginListener.getUserId();
        String sql = "select * from (select month_no from (select DATE_FORMAT(A.update_time,'%Y%m') month_no from user_log A where A.del_flag=0 and A.user_id=:userId) B group by month_no ) C order by month_no desc";
        PageResponse<String> page = QueryUtil.applyNativePage(sql, entityManager,req, new QueryUtil.PageParameter<PageReq>() {
            @Override
            public void initParam(Query query, PageReq req) throws ValidException {
                query.setParameter("userId",userId);
            }
        },new StringTransformer());
        ResponseData responseData = ResponseData.getDefaultSuccessResponse(req);
        responseData.setData(page);
        return responseData;
    }

    public ResponseData logsByMonth(MonthLogReq req) throws ValidException {
        String userId = TomatoUserLoginListener.getUserId();
        ResponseData responseData = ResponseData.getDefaultSuccessResponse(req);
        List<UserLogDto> result = null;
        if(UserLogOrderTypeConstant.CREATE_TIME.compare(req.getOrderType())){
            result = userLogRepository.list(req.getMonthNo(), userId);
        }else{
            result = userLogRepository.listUpdateTime(req.getMonthNo(), userId);
        }
        responseData.setData(result);
        return responseData;
    }
}
