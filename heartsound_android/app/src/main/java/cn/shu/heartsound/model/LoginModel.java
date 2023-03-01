package cn.shu.heartsound.model;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;



import java.io.IOException;

import cn.shu.heartsound.constant.RequestConstant;
import cn.shu.heartsound.exception.ResponseStatusCodeException;
import cn.shu.heartsound.network.LoginNetwork;
import cn.shu.heartsound.pojo.jsonResult.DataJsonResult;
import cn.shu.heartsound.pojo.login.UserLoginResult;
import cn.shu.heartsound.pojo.login.UserSendSmsResult;

public class LoginModel {
    private LoginNetwork loginNetWork = new LoginNetwork();
    /**
     * 用户登录
     *
     * @param handler
     * @param username
     * @param password
     */
    public void UserLogin(final Handler handler, final String username, final String password, final Context context) {
        new Thread() {
            private static final String TAG = "loginModel";

            @Override
            public void run() {
                super.run();
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("Operation", "Login");
                try {
                    message.what = RequestConstant.SHOW_PROGRESS;
                    handler.sendMessage(message);
                    message = handler.obtainMessage();
                    DataJsonResult<UserLoginResult> result = loginNetWork.UserLogin(username, password, context);
                    if (("success").equals(result.getStatus())) {
//                        Log.d(TAG, "run: "+result.getData());
                        if (result.getData() != null) {
                            //发送服务器返回的用户和令牌信息
                            bundle.putString("Accessid", result.getData().getAccessid());
                            bundle.putString("Type", result.getData().getType());
                            bundle.putString("Telephone", result.getData().getTelephone());
                            message.what = RequestConstant.REQUEST_SUCCESS;
                        } else {
                            message.what = RequestConstant.SERVER_ERROR;
                        }
                    } else {
                        bundle.putString("Message", result.getData().getErrMsg());
                        message.what = RequestConstant.REQUEST_FAILURE;
                    }
                } catch (NullPointerException ignored) {

                } catch (IOException e) {
                    message.what = RequestConstant.REQUEST_TIMEOUT;
                } catch (ResponseStatusCodeException e) {
                    message.what = RequestConstant.SERVER_ERROR;
                } catch (Exception e) {
                    message.what = RequestConstant.UNKNOWN_ERROR;
                }
                message.setData(bundle);
                handler.sendMessage(message);
            }
        }.start();

    }

    /**
     *
     * 用户发送验证码
     * @param handler
     * @param username
     * @param context
     */
    public void UserSendSms(final Handler handler, final String username, final Context context) {
        new Thread() {
            private static final String TAG = "loginModel";
            @Override
            public void run() {
                super.run();
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("Operation", "SendSms");
                try {
                    message.what = RequestConstant.SHOW_PROGRESS;
                    handler.sendMessage(message);
                    message = handler.obtainMessage();
                    DataJsonResult<UserSendSmsResult> result = loginNetWork.UserSendSms(username, context);
                    Log.d(TAG, "run: "+result.getData());
                    if (("success").equals(result.getStatus())) {
                        message.what = RequestConstant.REQUEST_SUCCESS;
                    } else {
                        bundle.putString("Status", result.getStatus());
                        message.what = RequestConstant.REQUEST_FAILURE;
                    }
                } catch (NullPointerException ignored) {
                } catch (IOException e) {
                    message.what = RequestConstant.REQUEST_TIMEOUT;
                } catch (ResponseStatusCodeException e) {
                    message.what = RequestConstant.SERVER_ERROR;
                } catch (Exception e) {
                    message.what = RequestConstant.UNKNOWN_ERROR;
                }
                message.setData(bundle);
                handler.sendMessage(message);
            }
        }.start();

    }
}
