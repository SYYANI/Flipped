package cn.shu.controller;

import cn.shu.dao.DoctorMapper;
import cn.shu.dao.RecordMapper;
import cn.shu.dao.UserMapper;
import cn.shu.dataobject.Doctor;
import cn.shu.dataobject.DoctorDetail;
import cn.shu.dataobject.RecordDetail;
import cn.shu.dataobject.User;
import cn.shu.error.CommonErrorException;
import cn.shu.error.EmCommonError;
import cn.shu.response.CommonReturnType;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/doctor")
@CrossOrigin
public class DoctorController extends BaseController {
    @Autowired
    private DoctorMapper doctorMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RecordMapper recordMapper;
    //获取医生列表
    @RequestMapping(value = "/list",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getDoctorList(@RequestHeader("telephone") String telephone,
                                          @RequestHeader("accessid") String accessid) throws CommonErrorException {
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
        List<DoctorDetail> doctorList = doctorMapper.getDoctorList();
        return CommonReturnType.create(doctorList);
    }

    //获取医生详情
    @RequestMapping(value = "/detaild",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getDoctorDetailD(@RequestHeader("telephone") String telephone,
                                            @RequestHeader("accessid") String accessid) throws CommonErrorException{
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
        DoctorDetail doctorDetail = doctorMapper.getDoctorDetail(user.getUserId());
//        Doctor doctor = doctorMapper.selectByPrimaryKey(Integer.valueOf(doctorid));
        if(doctorDetail == null){
            throw new CommonErrorException(EmCommonError.USER_NOT_EXIST);
        }
        return CommonReturnType.create(doctorDetail);
    }
    //获取医生详情
    @RequestMapping(value = "/detailp",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getDoctorDetailP(@RequestHeader("telephone") String telephone,
                                            @RequestHeader("accessid") String accessid) throws CommonErrorException{
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
        RecordDetail record = recordMapper.getLeastRecordListByPatientId(user.getUserId());
        DoctorDetail doctorDetail = doctorMapper.getDoctorDetail(record.getDoctorId());
        if(doctorDetail == null){
            throw new CommonErrorException(EmCommonError.USER_NOT_EXIST);
        }
        return CommonReturnType.create(doctorDetail);
    }
    //添加、更新医生
    @RequestMapping(value = "/update",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType updateDoctor(@RequestHeader("telephone") String telephone,
                                         @RequestHeader("accessid") String accessid,
                                         @RequestParam("doctorname") String doctorname,
                                         @RequestParam("hospitalid") String hospitalid,
                                         @RequestParam("officelocation") String officelocation,
                                         @RequestParam("officetel") String officetel,
                                         @RequestParam("title") String title) throws CommonErrorException{
        if(org.apache.commons.lang3.StringUtils.isEmpty(telephone)||
                org.apache.commons.lang3.StringUtils.isEmpty(accessid)||
                org.apache.commons.lang3.StringUtils.isEmpty(doctorname)||
                org.apache.commons.lang3.StringUtils.isEmpty(hospitalid)||
                org.apache.commons.lang3.StringUtils.isEmpty(officelocation)||
                org.apache.commons.lang3.StringUtils.isEmpty(officetel)||
                org.apache.commons.lang3.StringUtils.isEmpty(title)){
            throw new CommonErrorException(EmCommonError.PARAMETER_VALIDATION_ERROR);
        }
        User user = userMapper.selectByTelephone(telephone);
        if(user == null || !user.getAccessid().equals(accessid)){
            if(user == null){
                throw new CommonErrorException(EmCommonError.USER_NOT_EXIST);
            }
            throw new CommonErrorException(EmCommonError.LOGIN_OUT_TIME);
        }
        if(user.getType() == -1){
            //医生
            user.setType(1);
        }
        userMapper.updateByPrimaryKeySelective(user);
        Doctor doctor = doctorMapper.selectByPrimaryKey(user.getUserId());
        if(doctor == null){
            doctor = new Doctor();
            doctor.setDoctorName(doctorname);
            doctor.setHospitalId(Integer.valueOf(hospitalid));
            doctor.setOfficeLocation(officelocation);
            doctor.setOfficeTel(officetel);
            doctor.setTitle(title);
            doctor.setDoctorId(user.getUserId());
            doctorMapper.insert(doctor);
        } else{
            doctor.setDoctorName(doctorname);
            doctor.setHospitalId(Integer.valueOf(hospitalid));
            doctor.setOfficeLocation(officelocation);
            doctor.setOfficeTel(officetel);
            doctor.setTitle(title);
            doctorMapper.updateByPrimaryKey(doctor);
        }
        return CommonReturnType.create(null);
    }
}
