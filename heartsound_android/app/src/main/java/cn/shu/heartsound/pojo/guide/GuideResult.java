package cn.shu.heartsound.pojo.guide;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GuideResult implements Serializable {

    private Boolean validate;

    private String type;

    private String errCode;

    private String errMsg;

    public String getType() {
        return type;
    }

    public boolean getValidate() {
        return validate;
    }

    public String getErrCode() {
        return errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }
}
