package cn.shu.controller;

import cn.shu.dao.DiseaseMapper;
import cn.shu.dao.UserMapper;
import cn.shu.dataobject.Disease;
import cn.shu.dataobject.User;
import cn.shu.error.CommonErrorException;
import cn.shu.error.EmCommonError;
import cn.shu.response.CommonReturnType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/disease")
@CrossOrigin
public class DiseaseController extends BaseController {
    @Autowired
    private DiseaseMapper diseaseMapper;
    @Autowired
    private UserMapper userMapper;
    //获取疾病列表
    @RequestMapping(value = "/list",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getDiseaseList(@RequestHeader("telephone") String telephone,
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
        List<Disease> diseaseList = diseaseMapper.getDiseaseList();
        return CommonReturnType.create(diseaseList);
    }

    //获取疾病详情
    @RequestMapping(value = "/detail",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getDiseaseDetail(@RequestHeader("telephone") String telephone,
                                             @RequestHeader("accessid") String accessid,
                                             @RequestParam String diseaseid) throws CommonErrorException {
        if (org.apache.commons.lang3.StringUtils.isEmpty(telephone) ||
                org.apache.commons.lang3.StringUtils.isEmpty(accessid) ||
                org.apache.commons.lang3.StringUtils.isEmpty(diseaseid)) {
            throw new CommonErrorException(EmCommonError.PARAMETER_VALIDATION_ERROR);
        }
        User user = userMapper.selectByTelephone(telephone);
        if (user == null || !user.getAccessid().equals(accessid)) {
            if (user == null) {
                throw new CommonErrorException(EmCommonError.USER_NOT_EXIST);
            }
            throw new CommonErrorException(EmCommonError.LOGIN_OUT_TIME);
        }
        Disease disease = diseaseMapper.selectByPrimaryKey(Integer.valueOf(diseaseid));
        return CommonReturnType.create(disease);
    }
}
