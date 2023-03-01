package cn.shu.error;

public class CommonErrorException extends Exception implements CommonError{
    private CommonError commonError;
    public CommonErrorException(CommonError commonError){
        super();
        this.commonError = commonError;
    }
    public CommonErrorException(CommonError commonError, String errMsg){
        super();
        this.commonError = commonError;
        this.commonError = commonError.setErrMsg(errMsg);
    }
    @Override
    public int getErrCode() {
        return this.commonError.getErrCode();
    }

    @Override
    public String getErrMsg() {
        return this.commonError.getErrMsg();
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        this.commonError.setErrMsg(errMsg);
        return this;
    }
}
