package cn.shu.controller;

import cn.shu.config.RedisConfig;
import cn.shu.dao.UserMapper;
import cn.shu.dataobject.User;
import cn.shu.error.CommonErrorException;
import cn.shu.error.EmCommonError;
import cn.shu.response.CommonReturnType;
import cn.shu.util.SmsHelper;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.tea.TeaModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/user")
@CrossOrigin
public class UserController extends BaseController {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    @Autowired
    private UserMapper userMapper;

    @RequestMapping(value = "/getotp",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam("telephone") String telephone) throws Exception {
        //入参校验
        if(org.apache.commons.lang3.StringUtils.isEmpty(telephone)){
            throw new CommonErrorException(EmCommonError.PARAMETER_VALIDATION_ERROR);
        }
        //需要按照一定的规则生成otp验证码
        Random random = new Random();
        int randomInt = random.nextInt(8999);
        randomInt+=1000; //随机数范围在[1000,9999)之间
        String otpCode = String.valueOf(randomInt);

        //设置redis的key，这里设置为项目名:使用的字段:用户Id
        String redisKey="VERIFATIONCODE:CODE:"+telephone;
        //将这个验证码存入redis中，并设置失效时间为5分钟
        redisTemplate.opsForValue().set(redisKey,otpCode,300, TimeUnit.SECONDS);

        //将otp验证码同对应用户的手机号关联
        // 一般采取key value对的方式，key是手机号，value是生成码
        // 一般都放到redis中，天生是kv结构，而且相同key覆盖value，还能指定有效期。

//        com.aliyun.dysmsapi20170525.Client client = SmsHelper.createClient("LTAI5tA3ov5iVYLVe4BEFGkJ", "oBwkG9wH6GKS0BgL7vjTpvXOtUBug8");
//        com.aliyun.dysmsapi20170525.Client client = SmsHelper.createClient("", "");
//        SendSmsRequest sendSmsRequest = new SendSmsRequest()
//                .setSignName("深豹智能")
//                .setTemplateCode("SMS_225775056")
//                .setPhoneNumbers(telphone)
//                .setTemplateParam("{\"code\":\""+randomInt+"\"}");
//        SendSmsRequest sendSmsRequest = new SendSmsRequest()
//                .setSignName("阿里云短信测试")
//                .setTemplateCode("SMS_154950909")
//                .setPhoneNumbers(telephone)
//                .setTemplateParam("{\"code\":\""+randomInt+"\"}");
//        // 复制代码运行请自行打印 API 的返回值
//        SendSmsResponse resp = client.sendSms(sendSmsRequest);

//        System.out.println("telephone: " + telephone + " & otpCode: " + otpCode+"\n"+com.aliyun.teautil.Common.toJSONString(TeaModel.buildMap(resp.body)));

        //将otp验证码通过短信通道发送给用户,省略
        System.out.println("telephone: " + telephone + " & otpCode: " + otpCode+"\n");
        return CommonReturnType.create(null);
    }

    //校验短信验证码
    @RequestMapping(value = "/checkotp",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType checkOtp(@RequestParam("inputotp")String inputOpt,
                                     @RequestParam("telephone")String telephone) throws CommonErrorException {
        //入参校验
        if(org.apache.commons.lang3.StringUtils.isEmpty(telephone) || org.apache.commons.lang3.StringUtils.isEmpty(inputOpt)){
            throw new CommonErrorException(EmCommonError.PARAMETER_VALIDATION_ERROR);
        }

        //设置redis的key，这里设置为项目名:使用的字段:用户Id
        String redisKey="VERIFATIONCODE:CODE:"+telephone;
        String realCode= (String) redisTemplate.opsForValue().get(redisKey);
        if (realCode!=null&&realCode.equals(inputOpt)){
            redisTemplate.delete(redisKey);
            Map<String,Object> responseData = new HashMap<>();

            //生成UUID作为用户uuid标识符
            UUID uuid = UUID.randomUUID();
            String str = uuid.toString();
            String uuid_string = str.substring(0, 8) + str.substring(9, 13) + str.substring(14, 18) + str.substring(19, 23) + str.substring(24);

            responseData.put("accessid",uuid_string);
            User user = userMapper.selectByTelephone(telephone);
            if (user == null){
                user = new User();
                user.setTelephone(telephone);
                user.setAccessid(uuid_string);
                user.setType(-1);
                user.setPhoto("https://syyan.site/gerenzhongxin.png");
                userMapper.insertSelective(user);
                //默认type为-1
                responseData.put("type",-1);
            }else{
                user.setAccessid(uuid_string);
                userMapper.updateByTelephone(user);
                responseData.put("type",user.getType());
            }
            responseData.put("telephone",telephone);
            return CommonReturnType.create(responseData,"success");
        }else {
            throw new CommonErrorException(EmCommonError.WRONG_OPT_CODE);
        }
    }

    //用户打开app时，校验AccessID是否存在，如果存在，则返回用户信息
    @RequestMapping(value = "/checkaccess",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType checkAccess(@RequestParam("telephone") String telephone,
                                        @RequestParam("accessid") String accessid) throws CommonErrorException {
        //入参校验
        if(org.apache.commons.lang3.StringUtils.isEmpty(telephone)||
                org.apache.commons.lang3.StringUtils.isEmpty(accessid)){
            throw new CommonErrorException(EmCommonError.PARAMETER_VALIDATION_ERROR);
        }
        Map<String,Object> responseData = new HashMap<>();
        User user = userMapper.selectByTelephone(telephone);
        if(user==null) {
            throw new CommonErrorException(EmCommonError.USER_NOT_EXIST);
        }
        if(user.getAccessid().equals(accessid)){
            responseData.put("validate",true);
            responseData.put("type",user.getType());
        }else{
            responseData.put("validate",false);
        }
        return CommonReturnType.create(responseData,"success");
    }
}
