package cn.shu.heartsound;

import android.app.Application;

import cn.shu.heartsound.recorder.IdealRecorder;
import cn.shu.heartsound.tools.TokenUtils;

public class HeartSoundApplication extends Application {
    private String accessid;
    private String telephone;
    private String userType;

    @Override
    public void onCreate() {
        super.onCreate();
        IdealRecorder.getInstance().init(this);
    }

    public String getAccessid() {
        if(accessid == null) {
            accessid = TokenUtils.GetUserAccessId(this);
        }
        return accessid;
    }

    public void setAccessid(String accessid) {
        this.accessid = accessid;
    }

    public String getTelephone() {
        if(telephone == null){
            telephone = TokenUtils.GetUserTelephone(this);
        }
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        if(this.userType == null){
            userType = TokenUtils.GetUserType(this);
        }
        this.userType = userType;
    }
}
