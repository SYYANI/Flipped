package cn.shu.heartsound.pojo.login;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserLoginResult implements Serializable {

    private String accessid;

    private String type;

    private String errCode;

    private String errMsg;

    private String telephone;

    public String getTelephone() {
        return telephone;
    }

    public String getErrCode() {
        return errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public String getAccessid() {
        return accessid;
    }

    public String getType() {
        return type;
    }
}
