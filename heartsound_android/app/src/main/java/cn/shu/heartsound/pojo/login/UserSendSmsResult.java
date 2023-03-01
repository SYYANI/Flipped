package cn.shu.heartsound.pojo.login;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserSendSmsResult implements Serializable {
    private String errCode;
    private String errMsg;

    public UserSendSmsResult() {
        super();
    }

    public UserSendSmsResult(String errCode, String errMsg) {
        super();
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public String getErrCode() {
        return errCode;
    }


    public String getErrMsg() {
        return errMsg;
    }

}
