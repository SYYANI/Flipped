package cn.shu.heartsound.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import cn.shu.heartsound.HeartSoundApplication;
import cn.shu.heartsound.activity.LoginActivity;
import cn.shu.heartsound.activity.MainActivity;
import cn.shu.heartsound.constant.RequestConstant;
import cn.shu.heartsound.model.LoginModel;
import cn.shu.heartsound.tools.StringUtils;
import cn.shu.heartsound.tools.TokenUtils;

public class LoginPresenter {

    private LoginActivity loginActivity;

    private LoginActivityHandler loginActivityHandler;

    private LoginModel loginModel;

    /**
     * 移除所有的回调和消息，防止内存泄露
     */
    public void RemoveCallBacksAndMessages() {
        loginActivityHandler.removeCallbacksAndMessages(null);
    }

    private static class LoginActivityHandler extends Handler {

        private static final String TAG = "LoginPresenter";
        private LoginActivity loginActivity;

        LoginActivityHandler(LoginActivity loginActivity) {
            this.loginActivity = new WeakReference<>(loginActivity).get();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case RequestConstant.SHOW_PROGRESS:
                    //显示进度条
                    loginActivity.ShowProgressDialog();
                    break;

                case RequestConstant.REQUEST_FAILURE:
                    //隐藏进度条
                    loginActivity.HideProgressDialog();
                    if(msg.getData().getString("Operation").equals("SendSms"))
                    {
                        loginActivity.ShowToast("短信发送失败");
                    } else if(msg.getData().getString("Operation").equals("Login")){
                        loginActivity.ShowToast("验证码错误");
                    }
                    //显示错误信息
//                    loginActivity.ShowToast(msg.getData().getString("Status"));
                    break;

                case RequestConstant.SERVER_ERROR:
                    //隐藏进度条
                    loginActivity.HideProgressDialog();
                    //显示错误信息
                    loginActivity.ShowToast("服务暂不可用，请稍后再试");
                    break;

                case RequestConstant.REQUEST_TIMEOUT:
                    //隐藏进度条
                    loginActivity.HideProgressDialog();
                    //显示错误信息
                    loginActivity.ShowToast("网络连接超时，请重试");
                    break;

                case RequestConstant.UNKNOWN_ERROR:
                    //隐藏进度条
                    loginActivity.HideProgressDialog();
                    //显示错误信息
                    loginActivity.ShowToast("出现未知异常，请联系管理员");
                    break;

                case RequestConstant.REQUEST_SUCCESS:
                    if(msg.getData().getString("Operation").equals("SendSms"))
                    {
//                        Log.d(TAG, "sms_code: " +  msg.getData().getString("sms_code"));
                        Toast.makeText(loginActivity, "验证码发送成功", Toast.LENGTH_SHORT).show();
                        loginActivity.HideProgressDialog();
                        loginActivity.TransAnimationGo();
                    } else if(msg.getData().getString("Operation").equals("Login")){
                        //登录成功，解析并缓存用户登录凭证
                        // Log.d(TAG,msg.getData().getString("Operation"));
                        HeartSoundApplication application = (HeartSoundApplication) loginActivity.getApplication();
                        String accessid = msg.getData().getString("Accessid");
                        String type = msg.getData().getString("Type");
//                        if(msg.getData().getString("Type").equals("-1")){
//                            loginActivity.ShowToast("请更新个人信息！");
//                        }
                        String telephone = msg.getData().getString("Telephone");
                        Log.d(TAG, "handleMessage: accessid: "+ accessid +" userType: " + type+" telephone " +telephone);
                        //此处加载个人信息
                        application.setAccessid(accessid);
                        application.setUserType(type);
                        application.setTelephone(telephone);
                        //保存令牌到SharedPreferences
                        TokenUtils.SaveUserToken(accessid, type, telephone, loginActivity.getApplicationContext());
                        //跳转到应用主界面
                        Intent intent = new Intent(loginActivity,MainActivity.class);

                        intent.putExtras(msg.getData());
                        loginActivity.startActivity(intent);
                        //隐藏进度条
                        loginActivity.HideProgressDialog();
                        loginActivity.finish();
                        break;
                    }
            }
        }

    }
    public LoginPresenter(final LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
        this.loginModel = new LoginModel();
        this.loginActivityHandler = new LoginActivityHandler(loginActivity);
    }
    /**
     * 用户登录操作
     *
     * @param username
     * @param password
     */
    public void UserLogin(final String username, final String password) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            loginActivity.ShowToast("账户信息不能为空");
        } else {
            InputMethodManager inputMethodManager = (InputMethodManager) loginActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                //收起虚拟键盘
                inputMethodManager.hideSoftInputFromWindow(loginActivity.getWindow().getDecorView().getWindowToken(), 0);
            }
            loginModel.UserLogin(loginActivityHandler, username, password, loginActivity.getApplicationContext());
        }
    }

    /**
     * 用户发送验证码
     *
     * @param username
     */
    public void UserSendSms(final String username){
        if (StringUtils.isBlank(username)) {
            loginActivity.ShowToast("账户信息不能为空");
        } else {
            InputMethodManager inputMethodManager = (InputMethodManager) loginActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                //收起虚拟键盘
                inputMethodManager.hideSoftInputFromWindow(loginActivity.getWindow().getDecorView().getWindowToken(), 0);
            }

            loginModel.UserSendSms(loginActivityHandler, username, loginActivity.getApplicationContext());
        }
    }

    /**
     * 显示用户协议
     */
    public void ShowAgreement() {
    }

    /**
     * 显示隐私政策
     */
    public void ShowPrivacyPolicy() {
    }
}
