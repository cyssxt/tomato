package com.cyssxt.tomato.controller;

import com.cyssxt.common.annotation.ApiVersion;
import com.cyssxt.common.annotation.Authorization;
import com.cyssxt.common.controller.BaseController;
import com.cyssxt.common.exception.ValidException;
import com.cyssxt.common.response.ResponseData;
import com.cyssxt.tomato.controller.request.DelReq;
import com.cyssxt.tomato.controller.request.ProjectCreateReq;
import com.cyssxt.tomato.controller.request.ProjectPageReq;
import com.cyssxt.tomato.controller.request.ProjectSaveReq;
import com.cyssxt.tomato.service.ActionService;
import com.cyssxt.tomato.service.ProjectService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@ApiVersion
@RestController
@RequestMapping(value="/{version}/project/")
public class ProjectController extends ActionController<ProjectCreateReq, ProjectPageReq> {
    @Resource
    ProjectService projectService;

    @Authorization
    @RequestMapping(value="save")
    public ResponseData save(@RequestBody @Valid ProjectCreateReq req, BindingResult result) throws ValidException {
        return projectService.save(req);
    }

    @Override
    public ActionService getActionService() {
        return projectService;
    }
}
