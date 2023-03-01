package cn.shu.controller;

import cn.shu.dao.DoctorMapper;
import cn.shu.dao.HospitalMapper;
import cn.shu.dao.RecordMapper;
import cn.shu.dao.UserMapper;
import cn.shu.dataobject.Doctor;
import cn.shu.dataobject.Hospital;
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
@RequestMapping("/hospital")
@CrossOrigin
public class HospitalController extends BaseController {
    @Autowired
    private HospitalMapper hospitalMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RecordMapper recordMapper;
    @Autowired
    private DoctorMapper doctorMapper;
    //获取医院列表
    @RequestMapping(value = "/list",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getHospitalList(@RequestHeader("telephone") String telephone,
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
        List<Hospital> hospitalList = hospitalMapper.getHospitalList();
        return CommonReturnType.create(hospitalList);
    }

    //获取医院详情
    @RequestMapping(value = "/detail",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getHospitalDetail(@RequestHeader("telephone") String telephone,
                                              @RequestHeader("accessid") String accessid,
                                              @RequestParam("hospitalid") String hospitalid) throws CommonErrorException {
        if (org.apache.commons.lang3.StringUtils.isEmpty(telephone) ||
                org.apache.commons.lang3.StringUtils.isEmpty(accessid) ||
                org.apache.commons.lang3.StringUtils.isEmpty(hospitalid)) {
            throw new CommonErrorException(EmCommonError.PARAMETER_VALIDATION_ERROR);
        }
        User user = userMapper.selectByTelephone(telephone);
        if (user == null || !user.getAccessid().equals(accessid)) {
            if (user == null) {
                throw new CommonErrorException(EmCommonError.USER_NOT_EXIST);
            }
            throw new CommonErrorException(EmCommonError.LOGIN_OUT_TIME);
        }
        Hospital hospital = hospitalMapper.selectByPrimaryKey(Integer.valueOf(hospitalid));
        return CommonReturnType.create(hospital);
    }

    //获取医院详情
    @RequestMapping(value = "/detailp",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getHospitalDetailP(@RequestHeader("telephone") String telephone,
                                              @RequestHeader("accessid") String accessid) throws CommonErrorException {
        if (org.apache.commons.lang3.StringUtils.isEmpty(telephone) ||
                org.apache.commons.lang3.StringUtils.isEmpty(accessid)) {
            throw new CommonErrorException(EmCommonError.PARAMETER_VALIDATION_ERROR);
        }
        User user = userMapper.selectByTelephone(telephone);
        if (user == null || !user.getAccessid().equals(accessid)) {
            if (user == null) {
                throw new CommonErrorException(EmCommonError.USER_NOT_EXIST);
            }
            throw new CommonErrorException(EmCommonError.LOGIN_OUT_TIME);
        }
        RecordDetail record = recordMapper.getLeastRecordListByPatientId(user.getUserId());
        Doctor doctor = doctorMapper.selectByPrimaryKey(record.getDoctorId());
        Hospital hospital = hospitalMapper.selectByPrimaryKey(doctor.getHospitalId());
        return CommonReturnType.create(hospital);
    }
}
