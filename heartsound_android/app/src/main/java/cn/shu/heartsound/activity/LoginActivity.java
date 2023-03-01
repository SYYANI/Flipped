package cn.shu.heartsound.activity;

import static cn.shu.heartsound.activity.MainActivity.setStatusColor;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.shu.heartsound.vciv.VerificationCodeInputView;

import cn.shu.heartsound.R;
import cn.shu.heartsound.presenter.LoginPresenter;
import cn.shu.heartsound.view.dialog.ProgressDialogCreator;
import cn.shu.heartsound.view.widget.PhoneEditText;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, VerificationCodeInputView.OnInputListener {

    private static final String TAG = "login";
    private EditText loginUsernameEditText;
    private String smsCaptcha;
    private Button getSmsButton1;
    private Button getSmsButton2;
    private TextView loginAgreement;
    private TextView loginPrivacyPolicy;
    private Dialog dialog;
    private long exitTime = 0;
    private LoginPresenter loginPresenter;
    private ConstraintLayout constraintLayout_input_phone;
    private ConstraintLayout constraintLayout_input_captcha;

    private TimeCountUtil mTimeCountUtil;
    private PhoneEditText phoneEditText;
    private CheckBox checkBox;
    private boolean isPhoneEleven;
    private boolean idSend;
    private VerificationCodeInputView verificationCodeInput;
    private String phoneNumberInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusColor(this, true, true);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        //初始化控件
        InitView();
        //配置加载Presenter
        mTimeCountUtil = new TimeCountUtil(getSmsButton2, 30000, 1000);
        loginPresenter = new LoginPresenter(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            dialog.dismiss();
        }
        //移除所有的回调和消息，防止内存泄露
        loginPresenter.RemoveCallBacksAndMessages();
    }

    /**
     * 初始化控件
     */
    private void InitView() {
        loginUsernameEditText = findViewById(R.id.user_name_input);
        getSmsButton1 = findViewById(R.id.button_get_sms_captcha1);
        getSmsButton2 = findViewById(R.id.button_get_sms_captcha2);
        loginAgreement = findViewById(R.id.loginAgreement);
        loginPrivacyPolicy = findViewById(R.id.loginPrivacyPolicy);
        constraintLayout_input_phone = findViewById(R.id.constraintLayout);
        constraintLayout_input_captcha = findViewById(R.id.input_captcha);
        phoneEditText = findViewById(R.id.user_name_input);
        verificationCodeInput = findViewById(R.id.vciv_code2);
        checkBox = findViewById(R.id.checkBox);

        SetOnclickEvent();
    }

    private void SetOnclickEvent() {
        getSmsButton1.setOnClickListener(this);
        getSmsButton2.setOnClickListener(this);
        loginAgreement.setOnClickListener(this);
        loginPrivacyPolicy.setOnClickListener(this);
        verificationCodeInput.setOnInputListener(this);
        phoneEditText.setOnPhoneEditTextChangeListener(new PhoneEditText.OnPhoneEditTextChangeListener() {
            @Override
            public void onTextChange(String s, boolean isEleven) {
                phoneNumberInput = s;
                isPhoneEleven = isEleven;
            }
        });
    }

    /**
     * 验证码输入结束，登录
     */
    @Override
    public void onComplete(String code) {
        Log.d(TAG, "onComplete: " + code + "," + phoneNumberInput);
        //登录
        loginPresenter.UserLogin(phoneNumberInput, code);
    }

    @Override
    public void onInput() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_get_sms_captcha1:
                if (checkBox.isChecked()) {
                    if (isPhoneEleven) {
                        if (!idSend) {
                            mTimeCountUtil.start();
                            loginPresenter.UserSendSms(phoneNumberInput);
                        }
                        TransAnimationGo();
                    } else {
                        Toast.makeText(getApplicationContext(), "手机号码不符合规范，请检查后输入", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "请阅读并勾选用户协议与隐私政策", Toast.LENGTH_SHORT).show();
                }
                //发送验证码
                break;
            case R.id.button_get_sms_captcha2:
                mTimeCountUtil.start();
                loginPresenter.UserSendSms(phoneNumberInput);
                //重发验证码
                break;
            case R.id.loginAgreement:
                //用户协议
                loginPresenter.ShowAgreement();
                break;
            case R.id.loginPrivacyPolicy:
                //隐私政策
                loginPresenter.ShowPrivacyPolicy();
                break;
        }
    }

    /**
     * 过渡动画进入
     */
    public void TransAnimationGo() {
        Animation mHideAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mHideAction.setDuration(300);
        Animation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, +1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(300);
        constraintLayout_input_phone.setAnimation(mHideAction);
        constraintLayout_input_captcha.setAnimation(mShowAction);
        constraintLayout_input_phone.setVisibility(View.GONE);
        constraintLayout_input_captcha.setVisibility(View.VISIBLE);
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            //收起虚拟键盘
            inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    /**
     * 过渡动画返回
     */
    public void TransAnimationBack() {
        Animation mHideAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, +1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mHideAction.setDuration(300);
        Animation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(300);
        constraintLayout_input_captcha.setAnimation(mHideAction);
        constraintLayout_input_phone.setAnimation(mShowAction);
        constraintLayout_input_captcha.setVisibility(View.GONE);
        constraintLayout_input_phone.setVisibility(View.VISIBLE);
    }

    /**
     * 显示Toast提示
     *
     * @param message
     */
    public void ShowToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示进度条
     */
    public void ShowProgressDialog() {
        if (dialog == null) {
            dialog = new ProgressDialogCreator().GetProgressDialogCreator(this);
            dialog.show();
        } else {
            dialog.show();
        }
    }

    /**
     * 隐藏进度条
     */
    public void HideProgressDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public class TimeCountUtil extends CountDownTimer {
        private Button mButton;

        public TimeCountUtil(Button button, long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            this.mButton = button;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // 按钮不可用
            idSend = true;
            mButton.setEnabled(false);
            String showText = millisUntilFinished / 1000 + "秒后可重新发送";
            mButton.setText(showText);
            mButton.setTextSize(14);
        }

        @Override
        public void onFinish() {
            // 按钮设置可用
            idSend = false;
            mButton.setEnabled(true);
            mButton.setText("重新获取验证码");
        }
    }

    /**
     * 处理返回操作
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (constraintLayout_input_captcha.getVisibility() == View.VISIBLE && (System.currentTimeMillis() - exitTime) > 500) {
                TransAnimationBack();
                exitTime = System.currentTimeMillis();
            } else if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finishAffinity();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}