package cn.shu.heartsound.presenter;

import static java.lang.Thread.sleep;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


import java.lang.ref.WeakReference;

import cn.shu.heartsound.activity.GuideActivity;
import cn.shu.heartsound.activity.LoginActivity;
import cn.shu.heartsound.activity.MainActivity;
import cn.shu.heartsound.constant.RequestConstant;
import cn.shu.heartsound.model.GuideModel;
import cn.shu.heartsound.tools.StringUtils;
import cn.shu.heartsound.tools.TokenUtils;

public class GuidePresenter {
    private static final String TAG = "guidePresenter";
    private GuideActivity guideActivity;

    private GuideModel guideModel;

    private GuideActivityHandler guideActivityHandler;

    private Handler delayHandler = new Handler();

    private String telephone;

    public GuidePresenter(GuideActivity guideActivity){
        this.guideActivity = guideActivity;
        this.guideModel = new GuideModel();
        this.guideActivityHandler = new GuideActivityHandler(guideActivity);
        telephone = TokenUtils.GetUserTelephone(this.guideActivity);
        Init();
    }

    public static class GuideActivityHandler extends Handler {
        private GuideActivity guideActivity;

        GuideActivityHandler(GuideActivity guideActivity){
            this.guideActivity = new WeakReference<>(guideActivity).get();
        }
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch(msg.what){
                case RequestConstant.REQUEST_TIMEOUT:
                    //检验令牌超时，跳转到登录界面
                    guideActivity.ShowToast("刷新登录凭证超时，请尝试重新登录");
                    guideActivity.startActivity(new Intent(guideActivity, LoginActivity.class));
                    guideActivity.finish();
                    break;

                case RequestConstant.REQUEST_SUCCESS:
                    //检验令牌成功
                    Bundle bundle = msg.getData();
                    Boolean validate = bundle.getBoolean("Validate");
                    if(validate){
                        Bundle bundle_tomain = new Bundle();
                        bundle_tomain.putString("Accessid", TokenUtils.GetUserAccessId(this.guideActivity));
                        bundle_tomain.putString("Type", TokenUtils.GetUserType(this.guideActivity));
//                        if(TokenUtils.GetUserType(this.guideActivity).equals("-1")){
//                            this.guideActivity.ShowToast("请更新个人信息！");
//                        }
                        bundle_tomain.putString("Telephone", TokenUtils.GetUserTelephone(this.guideActivity));
                        Intent intent = new Intent(guideActivity, MainActivity.class);
                        intent.putExtras(bundle_tomain);
                        try {
                            sleep(600);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        guideActivity.startActivity(intent);
                    }else{
                        guideActivity.startActivity(new Intent(guideActivity, LoginActivity.class));
                    }
                    guideActivity.finish();
                    break;

                case RequestConstant.REQUEST_FAILURE:
                    //检验令牌失败
                    Bundle bundle_fail = msg.getData();
                    String message = bundle_fail.getString("Message");
                    guideActivity.ShowToast("刷新登录凭证失败，错误信息为" + message + "。请尝试重新登录");
                    guideActivity.startActivity(new Intent(guideActivity, LoginActivity.class));
                    guideActivity.finish();
                    break;

                case RequestConstant.SERVER_ERROR:
                    //服务器异常
                    guideActivity.ShowToast("服务暂不可用，请尝试重新登录");
                    guideActivity.startActivity(new Intent(guideActivity, LoginActivity.class));
                    guideActivity.finish();
                    break;

                case RequestConstant.UNKNOWN_ERROR:
                    //未知异常
                    guideActivity.ShowToast("出现未知异常，请尝试重新登录");
                    guideActivity.startActivity(new Intent(guideActivity, LoginActivity.class));
                    guideActivity.finish();
                    break;
            }

        }
    }
    private void Init(){
        //检查用户登录状态
        String accessId = TokenUtils.GetUserAccessId(guideActivity.getApplicationContext());
        if (StringUtils.isNotBlank(accessId) && telephone != null) {
            guideModel.ValidateAccessId(guideActivityHandler, accessId, telephone, guideActivity.getApplicationContext());
        } else {
            //延时跳转到登录界面
            Intent intent = new Intent(guideActivity, LoginActivity.class);
            StartActivityOnDelayed(intent);
        }
    }
    /**
     * 延时跳转Activity
     *
     * @param intent
     */
    private void StartActivityOnDelayed(final Intent intent) {
        delayHandler.postDelayed(new Runnable() {
            public void run() {
                guideActivity.startActivity(intent);
                guideActivity.finish();
            }
        }, 1500);
    }
    /**
     * 移除所有的回调和消息，防止内存泄露
     */
    public void RemoveCallBacksAndMessages() {
        guideActivityHandler.removeCallbacksAndMessages(null);
        delayHandler.removeCallbacksAndMessages(null);
    }
}
