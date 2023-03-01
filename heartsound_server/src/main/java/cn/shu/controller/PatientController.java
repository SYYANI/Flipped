package cn.shu.controller;

import cn.shu.dao.PatientMapper;
import cn.shu.dao.RecordMapper;
import cn.shu.dao.UserMapper;
import cn.shu.dataobject.*;
import cn.shu.error.CommonErrorException;
import cn.shu.error.EmCommonError;
import cn.shu.response.CommonReturnType;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/patient")
@CrossOrigin
public class PatientController extends BaseController {
    @Autowired
    private PatientMapper patientMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RecordMapper recordMapper;
    //获取患者列表
    @RequestMapping(value = "/list",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getPatientList(@RequestHeader("telephone") String telephone) throws CommonErrorException {
        if(org.apache.commons.lang3.StringUtils.isEmpty(telephone)){
            throw new CommonErrorException(EmCommonError.PARAMETER_VALIDATION_ERROR);
        }
        User user = userMapper.selectByTelephone(telephone);
        if(user == null){
            throw new CommonErrorException(EmCommonError.USER_NOT_EXIST);
        }
        List<Patient> patientList = patientMapper.getPatientList();
        return CommonReturnType.create(patientList);
    }
    //查找医生关联病人
    @RequestMapping(value = "/listd",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getAllPatientList(@RequestHeader String telephone,
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
        List<Patient> patientList = patientMapper.getPatientList();
        return CommonReturnType.create(patientList);
    }
    //查找医生关联病人
    @RequestMapping(value = "/listbydoctor",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getRecordDetailByDoctor(@RequestHeader String telephone,
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
        List<Patient> recordDetailList = patientMapper.getPatientByDoctorId(user.getUserId());
        return CommonReturnType.create(recordDetailList);
    }
    //获取患者详情
    @RequestMapping(value = "/detail",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getPatientDetail(@RequestHeader("telephone") String telephone,
                                             @RequestParam("patientid") String patientid) throws CommonErrorException{
        if(org.apache.commons.lang3.StringUtils.isEmpty(telephone)||
           org.apache.commons.lang3.StringUtils.isEmpty(patientid)){
            throw new CommonErrorException(EmCommonError.PARAMETER_VALIDATION_ERROR);
        }
        User user = userMapper.selectByTelephone(telephone);
        if(user == null){
            throw new CommonErrorException(EmCommonError.USER_NOT_EXIST);
        }
        Patient patient = patientMapper.selectByPrimaryKey(Integer.valueOf(patientid));
        return CommonReturnType.create(patient);
    }
    //获取患者详情
    @RequestMapping(value = "/detailp",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getPatientDetailP(@RequestParam("telephone") String telephone) throws CommonErrorException{
        if(org.apache.commons.lang3.StringUtils.isEmpty(telephone)){
            throw new CommonErrorException(EmCommonError.PARAMETER_VALIDATION_ERROR);
        }
        User user = userMapper.selectByTelephone(telephone);
        if(user == null){
            throw new CommonErrorException(EmCommonError.USER_NOT_EXIST);
        }
        PatientDetail patient = patientMapper.selectByPatientId(user.getUserId());
        if(patient == null){
            Patient patient_not_detail;
            patient_not_detail = patientMapper.selectByPrimaryKey(user.getUserId());
            if(patient_not_detail == null){
                patient_not_detail = new Patient();
            }
            patient = new PatientDetail();
            patient.setPatientId(patient_not_detail.getPatientId());
            patient.setPatientName(patient_not_detail.getPatientName());
            patient.setRelationName(patient_not_detail.getRelationName());
            patient.setRelationTel(patient_not_detail.getRelationTel());
            patient.setPhoto(user.getPhoto());
        }
        return CommonReturnType.create(patient);
    }

    //添加、更新患者
    @RequestMapping(value = "/update",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType updatePatient(@RequestHeader("telephone") String telephone,
                                          @RequestParam("patientname") String patientname,
                                          @RequestParam("relationname") String relationname,
                                          @RequestParam("relationtelephone") String relationtelephone) throws CommonErrorException{
        if(org.apache.commons.lang3.StringUtils.isEmpty(telephone)||
              org.apache.commons.lang3.StringUtils.isEmpty(patientname)||
              org.apache.commons.lang3.StringUtils.isEmpty(relationname)||
              org.apache.commons.lang3.StringUtils.isEmpty(relationtelephone)){
                throw new CommonErrorException(EmCommonError.PARAMETER_VALIDATION_ERROR);
          }
        User user = userMapper.selectByTelephone(telephone);
        if(user == null){
            throw new CommonErrorException(EmCommonError.USER_NOT_EXIST);
        }
        if(user.getType() == -1){
            //患者
            user.setType(0);
        }
        userMapper.updateByPrimaryKeySelective(user);
//        if(user == null || !user.getAccessid().equals(accessid)){
//            if(user == null){
//                throw new CommonErrorException(EmCommonError.USER_NOT_EXIST);
//            }
//            throw new CommonErrorException(EmCommonError.LOGIN_OUT_TIME);
//        }
        Patient patient = patientMapper.selectByPrimaryKey(user.getUserId());
        if(patient == null){
            patient = new Patient();
            patient.setPatientId(user.getUserId());
            patient.setPatientName(patientname);
            patient.setRelationName(relationname);
            patient.setRelationTel(relationtelephone);
            patientMapper.insert(patient);
        } else {
            patient.setPatientName(patientname);
            patient.setRelationName(relationname);
            patient.setRelationTel(relationtelephone);
            patientMapper.updateByPrimaryKey(patient);
        }
        PatientDetail patientDetail = patientMapper.selectByPatientId(user.getUserId());
        return CommonReturnType.create(patientDetail);
    }
}
