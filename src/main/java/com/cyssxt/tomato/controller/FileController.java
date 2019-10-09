package com.cyssxt.tomato.controller;

import com.cyssxt.common.annotation.ApiVersion;
import com.cyssxt.common.annotation.Authorization;
import com.cyssxt.common.controller.BaseController;
import com.cyssxt.common.exception.ValidException;
import com.cyssxt.common.response.ResponseData;
import com.cyssxt.tomato.service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

@ApiVersion
@RequestMapping(value="/{version}/file/",method = RequestMethod.POST)
@RestController
public class FileController extends BaseController {
    @Resource
    private FileService fileService;

    /**
     *
     * @param file 上传文件内容
     * @param reqId 请求id
     * @param sessionId 会话id
     * @return
     */
    @Authorization
    @RequestMapping(value = "upload")
    public ResponseData upload(@RequestParam("file") MultipartFile[] file, @RequestParam(value="reqId",required =false)String reqId, @RequestParam("sessionId")String sessionId) throws IOException, ValidException {
        ResponseData responseData = ResponseData.getDefaultSuccessResponse();
        responseData.setReqId(reqId);
        responseData.setSessionId(sessionId);
        return fileService.uploadFile(file,session,responseData);
    }
}
