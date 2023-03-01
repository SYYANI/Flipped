package cn.shu.controller;

import cn.shu.dao.MedicineMapper;
import cn.shu.dao.RecordMapper;
import cn.shu.dao.UserMapper;
import cn.shu.dataobject.Medicine;
import cn.shu.dataobject.RecordDetail;
import cn.shu.dataobject.User;
import cn.shu.error.CommonErrorException;
import cn.shu.error.EmCommonError;
import cn.shu.response.CommonReturnType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/medicine")
@CrossOrigin
public class MedicineController extends BaseController{
    @Autowired
    private MedicineMapper medicineMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RecordMapper recordMapper;
    //获取药品列表
    @RequestMapping(value = "/list",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getMedicineList(@RequestHeader("telephone") String telephone,
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
        List<Medicine> medicineList = medicineMapper.getMedicineList();
        return CommonReturnType.create(medicineList);
    }
    //获取药品详情
    @RequestMapping(value = "/detail",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getMedicineDetail(@RequestHeader("telephone") String telephone,
                                              @RequestHeader("accessid") String accessid,
                                              @RequestParam("medicineid") String medicineid) throws CommonErrorException{
        if(org.apache.commons.lang3.StringUtils.isEmpty(telephone)||
                org.apache.commons.lang3.StringUtils.isEmpty(accessid)||
                org.apache.commons.lang3.StringUtils.isEmpty(medicineid)){
            throw new CommonErrorException(EmCommonError.PARAMETER_VALIDATION_ERROR);
        }
        User user = userMapper.selectByTelephone(telephone);
        if(user == null || !user.getAccessid().equals(accessid)){
            if(user == null){
                throw new CommonErrorException(EmCommonError.USER_NOT_EXIST);
            }
            throw new CommonErrorException(EmCommonError.LOGIN_OUT_TIME);
        }
        Medicine medicine = medicineMapper.selectByPrimaryKey(Integer.valueOf(medicineid));
        return CommonReturnType.create(medicine);
    }
    //增加药品
    @RequestMapping(value = "/add",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType addMedicine(@RequestHeader("telephone") String telephone,
                                        @RequestHeader("accessid") String accessid,
                                        @RequestParam("medicinename") String medicinename,
                                        @RequestParam("instruction") String instruction,
                                        @RequestParam("recordid") String recordid) throws CommonErrorException{
        if(org.apache.commons.lang3.StringUtils.isEmpty(telephone)||
                org.apache.commons.lang3.StringUtils.isEmpty(accessid)||
                org.apache.commons.lang3.StringUtils.isEmpty(medicinename)||
                org.apache.commons.lang3.StringUtils.isEmpty(instruction)||
                org.apache.commons.lang3.StringUtils.isEmpty(recordid)){
            throw new CommonErrorException(EmCommonError.PARAMETER_VALIDATION_ERROR);
        }
        User user = userMapper.selectByTelephone(telephone);
        if(user == null || !user.getAccessid().equals(accessid)){
            if(user == null){
                throw new CommonErrorException(EmCommonError.USER_NOT_EXIST);
            }
            throw new CommonErrorException(EmCommonError.LOGIN_OUT_TIME);
        }
        Medicine medicine = new Medicine();
        medicine.setMedicineName(medicinename);
        medicine.setRecordId(Integer.valueOf(recordid));
        medicine.setInstruction(instruction);
        medicineMapper.insert(medicine);
        return CommonReturnType.create(null);
    }

    //获得关联病历的药物列表
    @RequestMapping(value = "/listbyrecord",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getMedicineListByRecord(@RequestHeader("telephone") String telephone,
                                                    @RequestHeader("accessid") String accessid,
                                                    @RequestParam("recordid") String recordid) throws CommonErrorException{
        if(org.apache.commons.lang3.StringUtils.isEmpty(telephone)||
                org.apache.commons.lang3.StringUtils.isEmpty(accessid)||
                org.apache.commons.lang3.StringUtils.isEmpty(recordid)){
            throw new CommonErrorException(EmCommonError.PARAMETER_VALIDATION_ERROR);
        }
        User user = userMapper.selectByTelephone(telephone);
        if(user == null || !user.getAccessid().equals(accessid)){
            if(user == null){
                throw new CommonErrorException(EmCommonError.USER_NOT_EXIST);
            }
            throw new CommonErrorException(EmCommonError.LOGIN_OUT_TIME);
        }
        List<Medicine> medicineList = medicineMapper.getMedicineListByRecord(Integer.valueOf(recordid));
        return CommonReturnType.create(medicineList);
    }
    //获得关联病历的药物列表
    @RequestMapping(value = "/listlatest",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getMedicineListLatest(@RequestHeader("telephone") String telephone,
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
        List<Medicine> medicineList;
        if(record == null){
            medicineList = new ArrayList<>();
        }else{
            medicineList = medicineMapper.getMedicineListByRecord(record.getRecordId());
        }
        return CommonReturnType.create(medicineList);
    }
}
