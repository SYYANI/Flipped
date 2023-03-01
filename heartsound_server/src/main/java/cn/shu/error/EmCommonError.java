package cn.shu.error;

public enum EmCommonError implements CommonError{
    //通用参数类型10001
    PARAMETER_VALIDATION_ERROR(10001,"参数不合法"),
    //通用参数类型10002
    UNKNOWN_ERROR(10002,"未知错误"),
    //数据库键错误
    DATABASE_ERROR(10003,"数据库错误"),
    //空指针错误
    NULL_POINTER_ERROR(10004,"数据库错误"),
    //20000开头为用户信息相关定义
    USER_NOT_EXIST(20001,"用户不存在"),
    //验证码错误
    WRONG_OPT_CODE(20002,"验证码错误"),
    RECORD_NOT_EXIST(20004,"记录不存在"),
    //登录失效
    LOGIN_OUT_TIME(20003,"登录失效");
    ;

    private int errCode;
    private String errMsg;

    EmCommonError(int errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    @Override
    public int getErrCode() {
        return this.errCode;
    }

    @Override
    public String getErrMsg() {
        return this.errMsg;
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        this.errMsg = errMsg;
        return this;
    }
}
