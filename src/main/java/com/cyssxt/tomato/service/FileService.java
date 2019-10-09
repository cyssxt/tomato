package com.cyssxt.tomato.service;

import com.cyssxt.common.exception.ValidException;
import com.cyssxt.common.response.ResponseData;
import com.cyssxt.common.utils.CommonUtils;
import com.cyssxt.common.utils.DateUtils;
import com.cyssxt.tomato.dao.FileReposity;
import com.cyssxt.tomato.entity.FileEntity;
import com.cyssxt.tomato.errors.MessageCode;
import com.cyssxt.tomato.listener.TomatoUserLoginListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileService {
    private final static Logger logger = LoggerFactory.getLogger(FileService.class);
    private final static String UPLOAD_PATH = "upload_path";

    @Resource
    private FileReposity fileReposity;

    /**
     * 文件上传
     * @param files
     * @param httpSession
     * @param responseData
     * @throws IOException
     */
    public ResponseData uploadFile(MultipartFile[] files, HttpSession httpSession, ResponseData responseData) throws IOException, ValidException {
        if(files==null || files.length==0){
            throw new ValidException(MessageCode.FILE_NOT_FOUND);
        }
        /**
         * 校验用户是否登录
         */
        String userId = TomatoUserLoginListener.getUserId();
        String contextPath = httpSession.getServletContext().getRealPath("/");
        List<FileEntity> tFileEntityList = new ArrayList<>();
        for (MultipartFile multipartFile:files){
            logger.debug("uploadFile,fileName={}",multipartFile.getOriginalFilename());
            FileEntity fileEntity = save(multipartFile,contextPath,userId);
            tFileEntityList.add(fileEntity);
            String descFilePath = fileEntity.getFileFullPath();
            String fullFilePath = fileEntity.getFileFullPath()+ File.separator+fileEntity.getFileName();
            File dirFile = new File(descFilePath);
            if(!dirFile.exists()){
                dirFile.mkdirs();
            }
            File newFile = new File(fullFilePath);
            multipartFile.transferTo(newFile);
            newFile.createNewFile();
        }
        responseData.setData(tFileEntityList);
        return responseData;
    }

    /**
     * 保存文件
     * @param multipartFile
     * @param contextPath
     * @param userId
     * @return
     * @throws ValidException
     */
    public FileEntity save(MultipartFile multipartFile, String contextPath, String userId) throws ValidException {
        String originalFilename = multipartFile.getOriginalFilename();
        logger.info("contextPath={}",contextPath);
        String suffix = multipartFile.getOriginalFilename().substring(originalFilename.lastIndexOf(".") + 1);
        String dateStr = DateUtils.getCurrentDateFormatStr(DateUtils.YYYYMMDD);
        String newFileName = CommonUtils.generatorKey()+"."+suffix;
        String descFilePath = contextPath+File.separator+UPLOAD_PATH+File.separator+dateStr;
        String filePath = String.format("%s/%s/%s",UPLOAD_PATH,dateStr,newFileName);
        logger.debug("save file,fileName={},filepaht={}",newFileName,descFilePath);
        FileEntity fileEntity = new FileEntity();
        fileEntity.setFilePath(filePath);
        fileEntity.setFileFullPath(descFilePath);
        fileEntity.setDateStr(dateStr);
        fileEntity.setOriginName(originalFilename);
        fileEntity.setFileName(newFileName);
        fileEntity.setUserId(userId);
        fileReposity.save(fileEntity);
        return fileEntity;
    }
}
