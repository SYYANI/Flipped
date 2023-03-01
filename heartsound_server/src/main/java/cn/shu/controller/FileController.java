package cn.shu.controller;


import cn.shu.dao.DoctorMapper;
import cn.shu.dao.FileMapper;
import cn.shu.dao.UserMapper;
import cn.shu.dataobject.Doctor;
import cn.shu.dataobject.File;
import cn.shu.dataobject.User;
import cn.shu.error.CommonErrorException;
import cn.shu.error.EmCommonError;
import cn.shu.response.CommonReturnType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/file")
@CrossOrigin
public class FileController extends BaseController {
    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DoctorMapper doctorMapper;
    //接受诊断结果并将OSS链接加入到数据库
    @RequestMapping(value = "/checkoss",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType checkOss(@RequestParam("bucket") String bucket,
                                     @RequestParam("object") String object,
                                     @RequestParam("jresult") String jresult,
                                     @RequestParam("telephone") String telephone,
                                     @RequestParam("rate") String rate) throws CommonErrorException {
        //入参校验
        if(org.apache.commons.lang3.StringUtils.isEmpty(bucket)
                || org.apache.commons.lang3.StringUtils.isEmpty(object)
                || org.apache.commons.lang3.StringUtils.isEmpty(jresult)
                || org.apache.commons.lang3.StringUtils.isEmpty(telephone)
                || org.apache.commons.lang3.StringUtils.isEmpty(rate)){
            throw new CommonErrorException(EmCommonError.PARAMETER_VALIDATION_ERROR);
        }
        String cname = "https://syyan.site";
        String ossUrl = cname+"/"+object;
        User user = userMapper.selectByTelephone(telephone);
        Map<String,Object> judgeResult = new HashMap<>();

        judgeResult.put("0","主动脉瓣狭窄_AS");
        judgeResult.put("1","二尖瓣狭窄_MS");
        judgeResult.put("2","二尖瓣返流_MR");
        judgeResult.put("3","二尖瓣脱垂_MVP");
        judgeResult.put("4","正常");

        File file = new File();
        file.setPatientId(user.getUserId());
        file.setAudioUrl(ossUrl);
        file.setAiResult((String) judgeResult.get(jresult));
        file.setRate(rate);
//        file.setPid(user.getId());
//        file.setAudio(ossUrl);
//        file.setFresult((String) judgeResult.get(jresult));
        fileMapper.insertSelective(file);
        return CommonReturnType.create(null);
    }
    //获取诊断记录列表
    @RequestMapping(value = "/list",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getFileList(@RequestParam("patientid") String patientid) throws CommonErrorException {
        if(org.apache.commons.lang3.StringUtils.isEmpty(patientid)){
            throw new CommonErrorException(EmCommonError.PARAMETER_VALIDATION_ERROR);
        }
        List<File> fileList = fileMapper.getFileList(Integer.valueOf(patientid));
        return CommonReturnType.create(fileList);
    }
    //获取诊断记录列表/手机
    @RequestMapping(value = "/listp",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getFileListP(@RequestParam("telephone") String telephone) throws CommonErrorException {
        if(org.apache.commons.lang3.StringUtils.isEmpty(telephone)){
            throw new CommonErrorException(EmCommonError.PARAMETER_VALIDATION_ERROR);
        }
        User user = userMapper.selectByTelephone(telephone);
        if(user == null){
            throw new CommonErrorException(EmCommonError.USER_NOT_EXIST);
        }
        List<File> fileList = fileMapper.getFileList(Integer.valueOf(user.getUserId()));
        return CommonReturnType.create(fileList);
    }

//    //获得最新一次诊断结果/手机
//    @RequestMapping(value = "/getlastp",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
//    @ResponseBody
//    public CommonReturnType getPatientLatestResultP(@RequestParam("telephone") String telephone) throws CommonErrorException {
//        if(org.apache.commons.lang3.StringUtils.isEmpty(telephone)){
//            throw new CommonErrorException(EmCommonError.PARAMETER_VALIDATION_ERROR);
//        }
//        User user = userMapper.selectByTelephone(telephone);
//        if(user == null){
//            throw new CommonErrorException(EmCommonError.USER_NOT_EXIST);
//        }
//        File file = fileMapper.getLastFile(Integer.valueOf(user.getUserId()));
//        return CommonReturnType.create(file);
//    }
    //获得最新一次诊断结果
    @RequestMapping(value = "/getlast",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getPatientLatestResult(@RequestParam("patientid") String pid) throws CommonErrorException {
        if(org.apache.commons.lang3.StringUtils.isEmpty(pid)){
            throw new CommonErrorException(EmCommonError.PARAMETER_VALIDATION_ERROR);
        }
        File file = fileMapper.getLastFile(Integer.valueOf(pid));
        return CommonReturnType.create(file);
    }
    //医生做出判断
    @RequestMapping(value = "/docjudge",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType docJudge(@RequestHeader("telephone") String telephone,
                                     @RequestHeader("accessid") String accessid,
                                     @RequestParam("fileid") String fileid,
                                     @RequestParam("docresult") String docresult) throws CommonErrorException {
        if(org.apache.commons.lang3.StringUtils.isEmpty(fileid)
                || org.apache.commons.lang3.StringUtils.isEmpty(docresult)
                || org.apache.commons.lang3.StringUtils.isEmpty(telephone)
                || org.apache.commons.lang3.StringUtils.isEmpty(accessid)){
            throw new CommonErrorException(EmCommonError.PARAMETER_VALIDATION_ERROR);
        }
        File file = fileMapper.selectByPrimaryKey(Integer.valueOf(fileid));
        file.setDocResult(docresult);
        fileMapper.updateByPrimaryKeySelective(file);
        return CommonReturnType.create(null);
    }
}
