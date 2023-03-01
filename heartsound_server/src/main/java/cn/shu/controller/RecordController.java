package cn.shu.controller;

import cn.shu.dao.RecordMapper;
import cn.shu.dao.UserMapper;
import cn.shu.dataobject.Record;
import cn.shu.dataobject.RecordDetail;
import cn.shu.dataobject.User;
import cn.shu.error.CommonErrorException;
import cn.shu.error.EmCommonError;
import cn.shu.response.CommonReturnType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/record")
@CrossOrigin
public class RecordController extends BaseController {
    @Autowired
    private RecordMapper recordMapper;
    @Autowired
    private UserMapper userMapper;
    //获取病历列表
    @RequestMapping(value = "/list",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getRecordList(@RequestHeader String telephone,
                                          @RequestHeader String accessid) throws CommonErrorException {
        if(org.apache.commons.lang3.StringUtils.isEmpty(telephone)||
                org.apache.commons.lang3.StringUtils.isEmpty(accessid)){
            throw new CommonErrorException(EmCommonError.PARAMETER_VALIDATION_ERROR);
        }
        User user = userMapper.selectByTelephone(telephone);
        if(user == null || !user.getAccessid().equals(accessid)){
            if(user == null){
                throw new CommonErrorException(EmCommonError.USER_NOT_EXIST);
            }
            throw new CommonErrorException(EmCommonError.LOGIN_OUT_TIME);
        }
        List<RecordDetail> recordList = recordMapper.getRecordList();
        return CommonReturnType.create(recordList);
    }
    //查找病人关联病历
    @RequestMapping(value = "/getdetailbypatient",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getRecordDetailByPatient(@RequestHeader String telephone,
                                                     @RequestHeader String accessid,
                                                     @RequestParam String patientid) throws CommonErrorException{
        if(org.apache.commons.lang3.StringUtils.isEmpty(telephone)||
                org.apache.commons.lang3.StringUtils.isEmpty(accessid)||
                org.apache.commons.lang3.StringUtils.isEmpty(patientid)){
            throw new CommonErrorException(EmCommonError.PARAMETER_VALIDATION_ERROR);
        }
        User user = userMapper.selectByTelephone(telephone);
        if(user == null || !user.getAccessid().equals(accessid)){
            if(user == null){
                throw new CommonErrorException(EmCommonError.USER_NOT_EXIST);
            }
            throw new CommonErrorException(EmCommonError.LOGIN_OUT_TIME);
        }
        List<RecordDetail> recordDetailList = recordMapper.getRecordListByPatientId(Integer.valueOf(patientid));
        return CommonReturnType.create(recordDetailList);
    }
    //查找病人关联病历
    @RequestMapping(value = "/getdetailbypatientp",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getRecordDetailByPatientP(@RequestHeader String telephone,
                                                     @RequestHeader String accessid) throws CommonErrorException{
        if(org.apache.commons.lang3.StringUtils.isEmpty(telephone)||
                org.apache.commons.lang3.StringUtils.isEmpty(accessid)){
            throw new CommonErrorException(EmCommonError.PARAMETER_VALIDATION_ERROR);
        }
        User user = userMapper.selectByTelephone(telephone);
        if(user == null || !user.getAccessid().equals(accessid)){
            if(user == null){
                throw new CommonErrorException(EmCommonError.USER_NOT_EXIST);
            }
            throw new CommonErrorException(EmCommonError.LOGIN_OUT_TIME);
        }
        List<RecordDetail> recordDetailList = recordMapper.getRecordListByPatientId(Integer.valueOf(user.getUserId()));
        return CommonReturnType.create(recordDetailList);
    }
    //查找医生关联病历
    @RequestMapping(value = "/getdetailbydoctor",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getRecordDetailByDoctor(@RequestHeader String telephone,
                                                    @RequestHeader String accessid,
                                                    @RequestParam String doctorid) throws CommonErrorException{
        if(org.apache.commons.lang3.StringUtils.isEmpty(telephone)||
                org.apache.commons.lang3.StringUtils.isEmpty(accessid)||
                org.apache.commons.lang3.StringUtils.isEmpty(doctorid)){
            throw new CommonErrorException(EmCommonError.PARAMETER_VALIDATION_ERROR);
        }
        User user = userMapper.selectByTelephone(telephone);
        if(user == null || !user.getAccessid().equals(accessid)){
            if(user == null){
                throw new CommonErrorException(EmCommonError.USER_NOT_EXIST);
            }
            throw new CommonErrorException(EmCommonError.LOGIN_OUT_TIME);
        }
        List<RecordDetail> recordDetailList = recordMapper.getRecordListByDoctorId(Integer.valueOf(doctorid));
        return CommonReturnType.create(recordDetailList);
    }
    //获取病历详情
    @RequestMapping(value = "/detail",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getRecordDetail(@RequestHeader("telephone") String telephone,
                                            @RequestHeader("accessid") String accessid,
                                            @RequestParam("recordid") String recordid) throws CommonErrorException {
        if (org.apache.commons.lang3.StringUtils.isEmpty(telephone) ||
                org.apache.commons.lang3.StringUtils.isEmpty(accessid) ||
                org.apache.commons.lang3.StringUtils.isEmpty(recordid)) {
            throw new CommonErrorException(EmCommonError.PARAMETER_VALIDATION_ERROR);
        }
        User user = userMapper.selectByTelephone(telephone);
        if (user == null || !user.getAccessid().equals(accessid)) {
            if (user == null) {
                throw new CommonErrorException(EmCommonError.USER_NOT_EXIST);
            }
            throw new CommonErrorException(EmCommonError.LOGIN_OUT_TIME);
        }
//        Record record = recordMapper.selectByPrimaryKey(Integer.valueOf(recordid));
        RecordDetail recordDetail = recordMapper.getRecordDetail(Integer.valueOf(recordid));
        return CommonReturnType.create(recordDetail);
    }
    //添加病历
    @RequestMapping(value = "/add",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType addRecord(@RequestHeader("telephone") String telephone,
                                      @RequestHeader("accessid") String accessid,
                                      @RequestParam("patientid") String patientid,
                                      @RequestParam(value = "detail",required = false) String detail,
                                      @RequestParam("diseaseid") String dieaseid) throws CommonErrorException {
        if (org.apache.commons.lang3.StringUtils.isEmpty(telephone) ||
                org.apache.commons.lang3.StringUtils.isEmpty(accessid) ||
                org.apache.commons.lang3.StringUtils.isEmpty(patientid) ||
                org.apache.commons.lang3.StringUtils.isEmpty(dieaseid)) {
            throw new CommonErrorException(EmCommonError.PARAMETER_VALIDATION_ERROR);
        }
        User user = userMapper.selectByTelephone(telephone);
        if (user == null || !user.getAccessid().equals(accessid)) {
            if (user == null) {
                throw new CommonErrorException(EmCommonError.USER_NOT_EXIST);
            }
            throw new CommonErrorException(EmCommonError.LOGIN_OUT_TIME);
        }
        Record record = new Record();
        record.setPatientId(Integer.valueOf(patientid));
        record.setDoctorId(user.getUserId());
        record.setDiseaseId(Integer.valueOf(dieaseid));
        if(detail != null){
            record.setDetail(detail);
        }
        recordMapper.insert(record);
        return CommonReturnType.create(null);
    }
    //添加病历
    @RequestMapping(value = "/update",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType updateRecord(@RequestHeader("telephone") String telephone,
                                         @RequestHeader("accessid") String accessid,
                                         @RequestParam("patientid") String patientid,
                                         @RequestParam(value = "detail",required = false) String detail,
                                         @RequestParam("diseaseid") String dieaseid,
                                         @RequestParam("recordid") String recordid) throws CommonErrorException {
        if (org.apache.commons.lang3.StringUtils.isEmpty(telephone) ||
                org.apache.commons.lang3.StringUtils.isEmpty(accessid) ||
                org.apache.commons.lang3.StringUtils.isEmpty(patientid) ||
                org.apache.commons.lang3.StringUtils.isEmpty(dieaseid) ||
                org.apache.commons.lang3.StringUtils.isEmpty(recordid)) {
            throw new CommonErrorException(EmCommonError.PARAMETER_VALIDATION_ERROR);
        }
        User user = userMapper.selectByTelephone(telephone);
        if (user == null || !user.getAccessid().equals(accessid)) {
            if (user == null) {
                throw new CommonErrorException(EmCommonError.USER_NOT_EXIST);
            }
            throw new CommonErrorException(EmCommonError.LOGIN_OUT_TIME);
        }
        Record record = recordMapper.selectByPrimaryKey(Integer.valueOf(recordid));
        if(record!=null){
            record.setPatientId(Integer.valueOf(patientid));
            record.setDoctorId(user.getUserId());
            record.setDiseaseId(Integer.valueOf(dieaseid));
            if(detail != null){
                record.setDetail(detail);
            }
            recordMapper.updateByPrimaryKey(record);
        }else{
            throw new CommonErrorException(EmCommonError.RECORD_NOT_EXIST);
        }
        return CommonReturnType.create(null);
    }
}
