package com.cyssxt.tomato.service;

import com.cyssxt.common.bean.Copy;
import com.cyssxt.common.dao.BaseRepository;
import com.cyssxt.common.exception.ValidException;
import com.cyssxt.common.hibernate.transformer.KeyTransformer;
import com.cyssxt.common.hibernate.transformer.IgnoreCaseResultTransformer;
import com.cyssxt.common.request.BaseReq;
import com.cyssxt.common.response.PageResponse;
import com.cyssxt.common.response.ResponseData;
import com.cyssxt.common.utils.CommonUtils;
import com.cyssxt.common.utils.DateUtils;
import com.cyssxt.common.utils.JpaUtil;
import com.cyssxt.common.utils.QueryUtil;
import com.cyssxt.tomato.constant.ContentTypeConstant;
import com.cyssxt.tomato.constant.TaskStatusConstant;
import com.cyssxt.tomato.controller.request.*;
import com.cyssxt.tomato.entity.ActionEntity;
import com.cyssxt.tomato.entity.MoveInterface;
import com.cyssxt.tomato.entity.ProjectInfoEntity;
import com.cyssxt.tomato.errors.MessageCode;
import com.cyssxt.tomato.listener.TomatoUserLoginListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class ActionService<T extends ActionEntity,V extends ActionCreateReq,Q extends ActionPageReq,D,E>{

    private final static Logger logger = LoggerFactory.getLogger(ActionService.class);
    public static final int DETAULT_NOT_TYPE = -10;
    @PersistenceContext
    EntityManager entityManager;

    @Resource
    TagService tagService;

    public ActionPageParameter getDefaultActionPageParameter(){
        return new ActionPageParameter<Q>() {
            @Override
            public void append(Query query, Q q) {

            }
        };
    }


    public abstract BaseRepository getRepository();

    public abstract Byte getContentType();

    public abstract T createEntity(V v) throws ValidException;
    public T copyEntity(T t){
        String oldId = t.getRowId();
        T newObj = (T) t.clone();
        String newId = CommonUtils.generatorKey();
        newObj.setRowId(newId);
        //拷贝标签
        tagService.copy(oldId,newId,getContentType());
        Timestamp now = DateUtils.getCurrentTimestamp();
        newObj.setCreateTime(now);
        newObj.setUpdateTime(now);
        return newObj;
    }
    public void onCopy(T t,String oldId,String parentId,Byte parentType) throws ValidException {
        logger.info("onCopy={},{},{},{}",t,oldId,parentId,parentType);
    }

    /**
     * 拷贝
     * @param req
     * @return
     * @throws ValidException
     */
    public ResponseData copy(MoveReq req) throws ValidException {
        String contentId = req.getContentId();
        T t = JpaUtil.check(contentId,getRepository(),true);
        String oldId = t.getRowId();
        t = copyEntity(t);
        String parentId = req.getParentId();
        Byte parentType = req.getParentType();
        if(StringUtils.isEmpty(parentId)){
            parentId = t.getParentId();
        }
        if(parentType==null && t.getParentType()!=null && t.getParentType().byteValue()!= DETAULT_NOT_TYPE){
            parentType = t.getParentType();
        }
        onCopy(t,oldId,parentId,parentType);
        getRepository().save(t);
        ResponseData responseData = ResponseData.getDefaultSuccessResponse(req);
        responseData.setData(t.getRowId());
        return responseData;
    }

    public void onCancel(T t){

    }

    public ResponseData cancel( CancelReq req) throws ValidException {
        T t = JpaUtil.check(req.getContentId(),getRepository(),true);
        t.setStatus(TaskStatusConstant.WAIT.getValue());
        t.setUpdateTime(DateUtils.getCurrentTimestamp());
        getRepository().save(t);
        onCancel(t);
        return ResponseData.getDefaultSuccessResponse(req);
    }

    public abstract class ActionPageParameter<X extends ActionPageReq> implements QueryUtil.PageParameter<X> {

        @Override
        public void initParam(Query query, X x) throws ValidException {
            String searchKey = x.getSearchKey();
            if(!StringUtils.isEmpty(searchKey)){
                query.setParameter("searchKey",searchKey);
            }
            String userId = TomatoUserLoginListener.getUserId();
            query.setParameter("userId",userId);
            List<String> tagIds = x.getTagIds();
            if(!CollectionUtils.isEmpty(tagIds)){
                query.setParameter("tagIds",tagIds);
            }
            append(query,x);
        }

        public abstract void append(Query query,X x);
    }
    public void onFinish(FinishReq req,T t) throws ValidException {

    }

    public ResponseData finish(FinishReq req) throws ValidException {
        T t = updateStatus(req,TaskStatusConstant.FINISH);
        onFinish(req,t);
        return ResponseData.getDefaultSuccessResponse(req);
    }

    public ResponseData start(FinishReq req) throws ValidException {
        updateStatus(req,TaskStatusConstant.RUNNING);
        return ResponseData.getDefaultSuccessResponse(req);
    }



    public ResponseData end(FinishReq req) throws ValidException {
        updateStatus(req,TaskStatusConstant.FINISH);
        return ResponseData.getDefaultSuccessResponse(req);
    }

    public boolean finishFilter(T t,FinishReq req){
        return true;
    }

    public void beforeFinish(FinishReq req,T t){}

    private T updateStatus(FinishReq req,TaskStatusConstant taskStatusConstant) throws ValidException {
        String contentId = req.getContentId();
        T actionEntity = JpaUtil.check(contentId, getRepository(), true);
        if(finishFilter(actionEntity,req)) {
            actionEntity.setStatus(taskStatusConstant.getValue());
            actionEntity.setFinishTime(DateUtils.getCurrentTimestamp());
            beforeFinish(req,actionEntity);
            getRepository().save(actionEntity);
        }
        return actionEntity;
    }

    public ResponseData create(V v) throws ValidException {
        T t = createEntity(v);
        ResponseData responseData =  save(v, (key, o) -> !"rowId".equals(key),t);
        onInsert(t,v);
        return responseData;
    }
    public void onInsert(T t,V v) throws ValidException {}
    public void onUpdate(T t,T old,V v) throws ValidException {}

    public ResponseData save(V v, Copy.Filter filter,T t) throws ValidException {
        String userId = TomatoUserLoginListener.getUserId();
        v.parse(t,filter);
        t.setUpdateTime(DateUtils.getCurrentTimestamp());
        t.setUserId(userId);
        beforeSave(t,v,userId);
        getRepository().save(t);
        afterSave(t,userId);
        String rowId = t.getRowId();
        ResponseData responseData =  ResponseData.getDefaultSuccessResponse(v);
        responseData.setData(rowId);
        return responseData;
    }

    protected void afterSave(T t,String userId){}

    protected void beforeSave(T t,V v,String userId) throws ValidException {}

    public void onDel(T t){}
    /**
     * 删除对象
     * @param req
     * @return
     * @throws ValidException
     */
    public ResponseData del(DelReq req) throws ValidException {
        T t = JpaUtil.check(req.getContentId(),getRepository(),true);
        checkSelf(t);
        t.setDelFlag(true);
        getRepository().save(t);
        onDel(t);
        return ResponseData.getDefaultSuccessResponse(req);
    }

    /**
     * 校验是否是当前用户创建
     * @param actionEntity
     * @throws ValidException
     */
    public void checkSelf(ActionEntity actionEntity) throws ValidException {
        String userId = actionEntity.getUserId();
        String createUserId = actionEntity.getUserId();
        if(!userId.equals(createUserId)){
            throw new ValidException(MessageCode.NOT_SELF_CONTENT);
        }
    }

    public void onMove(String oldVal,Byte oldType,String newVal,Byte newType,String contentId) throws ValidException {
        logger.info("onMove={},{},{},{},{}",oldVal,oldType,newVal,newType,contentId);
    }
//    public ResponseData bathcMove(BatchMoveReq req) throws ValidException {
//        List<String> contentIds = req.getContentIds();
//        if(CollectionUtils.isEmpty(contentIds)) {
//            for(String contentId:contentIds) {
//                MoveReq moveReq = new MoveReq();
//                moveReq.setContentId(contentId);
//                moveReq.setParentType(req.getType());
//                moveReq.setParentId(req.getParentId());
//                move(moveReq);
//            }
//        }
//        return ResponseData.getDefaultSuccessResponse(req);
//    }
    public ResponseData move(MoveReq req) throws ValidException {
        String contentId = req.getContentId();
        T t = JpaUtil.check(contentId,getRepository(),true);
        if(t instanceof MoveInterface) {
            MoveInterface move = (MoveInterface)t;
            String oldVal = move.getParentId();
            Byte oldType = move.getParentType();
            String newVal = req.getParentId();
            checkSelf(t);
            t.setParentId(req.getParentId());
            move.setParentType(req.getParentType());
            t.setUpdateTime(DateUtils.getCurrentTimestamp());
            onMove(oldVal, oldType, newVal,req.getParentType(),contentId);
            getRepository().save(t);
        }
        return ResponseData.getDefaultSuccessResponse(req);
    }

//    public abstract PageResponse<D> getResult(Q req,String userId) throws ValidException;

    //是否通过userId过滤
    public boolean filterUserId(){
        return true;
    }
    //是否通过delFlag过滤
    public boolean filterDelFlag(){
        return true;
    }
    //是否过滤tagId
    public boolean filterTagId(){
        return true;
    }
    public final String whereCause(Q req){
        List<String> list = new ArrayList();
        list.add("1=1");
        if(filterDelFlag()) {
            list.add("(A.DEL_FLAG!=1 OR A.DEL_FLAG IS NULL)");
        }
        if(filterUserId()) {
            list.add("A.USER_ID=:userId");
        }
        List<String> tagIds = req.getTagIds();
        if(filterTagId() && !CollectionUtils.isEmpty(tagIds)) {
            list.add("A.row_id in (select C.content_id from re_tag C where C.del_flag=0 and C.tag_id in :tagIds and content_type="+getContentType()+" )");
        }
        where(req,list);
        return String.join(" and ",list);
    }

    /**
     * 列表接口拼接where语句
     * @param req
     * @param list
     */
    public void where(Q req,List<String> list){}
    public String orderBy(Q req){ return " A.create_time desc,A.row_id desc";}
    public void afterList(KeyTransformer transformer,List<D> list,Q req,ResponseData responseData){
        logger.info("afterList={},transformer",list);
    }
    public KeyTransformer getTransformer(Class alias){
        return new IgnoreCaseResultTransformer(alias);
    }

    /**
     * 获取最终sql
     * @return
     */
    public final String getFullSql(Q q){
        return getFullSqlOfWhere(q,"");
    }

    public final String getFullSqlOfWhere(Q q,String where){
        StringBuffer stringBuffer = new StringBuffer();
        String listSql = getListSql(q);
        stringBuffer.append(listSql);
        stringBuffer.append(" WHERE ");
        stringBuffer.append(whereCause(q));
        stringBuffer.append(where);
        stringBuffer.append(" order by "+orderBy(q));
        return stringBuffer.toString();
    }
    /**
     * 列表接口
     * @param q
     * @return
     */
    public  ResponseData list(Q q) throws ValidException {
        ResponseData responseData = ResponseData.getDefaultSuccessResponse(q);
        String sql = getFullSql(q);
        KeyTransformer transformer = getTransformer(getDtoClass());
        List<D> result;
        if(q.getPageSize()!=null && q.getPageSize().intValue()!=-1) {
            PageResponse<D> pageResponse = QueryUtil.applyNativePage(sql, entityManager, q, getPageParameter(),transformer);
            result = pageResponse.getItems();
            responseData.setData(pageResponse);
        }else {
            result = QueryUtil.applyNativeList(sql,q,entityManager,getPageParameter(),transformer);
            responseData.setData(result);
        }
        afterList(transformer,result,q,responseData);
        return responseData;
    }

    public List<D> getList(Q q) throws ValidException {
        String sql = getFullSql(q);
        KeyTransformer transformer = getTransformer(getDtoClass());
        List<D> result = QueryUtil.applyNativeList(sql,q,entityManager,getPageParameter(),transformer);
        return result;
    }

    protected ActionPageParameter<Q> getPageParameter(){
        return getDefaultActionPageParameter();
    }
    protected abstract Class getDtoClass();

    public abstract String getListSql(Q req);

    public ResponseData<E> info(InfoReq req) throws ValidException{
        ResponseData responseData = ResponseData.getDefaultSuccessResponse(req);
        E e = detail(req);
        responseData.setData(e);
        return responseData;
    };
    public abstract E detail(InfoReq req) throws ValidException;

    public ResponseData batchDel(BatchDelReq req) throws ValidException {
        List<String> contentIds = req.getContentIds();
        String userId = TomatoUserLoginListener.getUserId();
        int length = batchDel(contentIds,userId);
        ResponseData  responseData =  ResponseData.getDefaultSuccessResponse(req);
        responseData.setData(length);
        return responseData;
    }

    /**
     * 批量删除 子类可实现此方法
     * @param contentIds
     * @return
     */
    public int batchDel(List<String> contentIds,String userId) throws ValidException {
        if(!CollectionUtils.isEmpty(contentIds)){
            return 0;
        }
        for(String contentId:contentIds){
            DelReq req = new DelReq();
            req.setContentId(contentId);
            del(req);
        }
        return contentIds.size();
    }

    public ResponseData update(V v) throws ValidException {
        String rowId = StringUtils.isEmpty(v.getRowId())?v.getContentId():v.getRowId();
        v.setRowId(rowId);
        T t = JpaUtil.check(rowId,getRepository(),true);
        T old = (T) t.clone();
        ResponseData responseData =  save(v,null,t);
        onUpdate(t,old,v);
        return responseData;
    }

    /**
     * 校验当前的动作是否是当前用户创建
     * @param actionEntity
     * @throws ValidException
     */
    public void checkUserAuth(ActionEntity actionEntity) throws ValidException {
        String userId = TomatoUserLoginListener.getUserId();
        if(actionEntity!=null && !userId.equals(actionEntity.getUserId())){
            throw new ValidException(MessageCode.USER_NOT_AUTH);
        }
    }

    public static void main(String[] args) throws ValidException {
        ProjectInfoEntity projectInfoEntity = new ProjectInfoEntity();
        ProjectSaveReq projectSaveReq = new ProjectSaveReq();
        projectSaveReq.parse(projectInfoEntity,null);
    }
}
