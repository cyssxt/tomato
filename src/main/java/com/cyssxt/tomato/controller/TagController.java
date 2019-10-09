package com.cyssxt.tomato.controller;

import com.cyssxt.common.annotation.ApiVersion;
import com.cyssxt.common.annotation.Authorization;
import com.cyssxt.common.exception.ValidException;
import com.cyssxt.common.response.ResponseData;
import com.cyssxt.tomato.controller.request.BatchDelReq;
import com.cyssxt.tomato.controller.request.TagCreateReq;
import com.cyssxt.tomato.controller.request.TagPageReq;
import com.cyssxt.tomato.controller.request.TagTodoPageReq;
import com.cyssxt.tomato.service.ActionService;
import com.cyssxt.tomato.service.TagService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@ApiVersion
@RequestMapping(value="/{version}/tag/",method = RequestMethod.POST)
public class TagController extends ActionController<TagCreateReq, TagPageReq>{

    @Resource
    TagService tagService;

    @Override
    public ActionService getActionService() {
        return tagService;
    }

    @Authorization
    @RequestMapping(value="todos")
    public ResponseData tagTodo(@RequestBody @Valid TagTodoPageReq req, BindingResult result) throws ValidException {
        return tagService.todos(req);
    }
    @Authorization
    @RequestMapping(value="batchDel")
    public ResponseData batchDel(@RequestBody @Valid BatchDelReq req, BindingResult result) throws ValidException {
        return tagService.batchDel(req);
    }

}
