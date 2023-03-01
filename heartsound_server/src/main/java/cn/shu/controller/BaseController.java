package cn.shu.controller;


import cn.shu.error.CommonErrorException;
import cn.shu.error.EmCommonError;
import cn.shu.response.CommonReturnType;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class BaseController {

    public static final String CONTENT_TYPE_FORMED="application/x-www-form-urlencoded";

    //定义handler
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Object handlerException(HttpServletRequest request, Exception exception){
        Map<String,Object> responseData = new HashMap<>();
        if(exception instanceof CommonErrorException){
            CommonErrorException commonErrorException = (CommonErrorException) exception;
            responseData.put("errCode", commonErrorException.getErrCode());
            responseData.put("errMsg", commonErrorException.getErrMsg());
        }else if (exception instanceof MissingServletRequestParameterException){
            responseData.put("errCode", EmCommonError.PARAMETER_VALIDATION_ERROR.getErrCode());
            responseData.put("errMsg", EmCommonError.PARAMETER_VALIDATION_ERROR.getErrMsg());
        }else if(exception instanceof DataIntegrityViolationException){
            responseData.put("errCode", EmCommonError.DATABASE_ERROR.getErrCode());
            responseData.put("errMsg", EmCommonError.DATABASE_ERROR.getErrMsg());
            System.out.println("errMsg"+ exception.getCause().fillInStackTrace());
//            responseData.put("errMsg", exception.getCause().fillInStackTrace());
        } else if(exception instanceof NullPointerException){
            responseData.put("errCode", EmCommonError.NULL_POINTER_ERROR.getErrCode());
            responseData.put("errMsg", EmCommonError.NULL_POINTER_ERROR.getErrMsg());
            System.out.println("errMsg"+ exception.getMessage());
//            responseData.put("errMsg", exception.getCause().fillInStackTrace());
        } else{
//            CommonErrorException commonErrorException = (CommonErrorException) exception;
            responseData.put("errCode", EmCommonError.UNKNOWN_ERROR.getErrCode());
            responseData.put("errMsg", EmCommonError.UNKNOWN_ERROR.getErrMsg());
            System.out.println("errMsg: "+ exception.getMessage());
        }
        return CommonReturnType.create(responseData,"failed");
    }
}
