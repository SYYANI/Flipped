package cn.shu.heartsound.activity;

import static cn.shu.heartsound.activity.MainActivity.setStatusColor;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import cn.shu.heartsound.R;
import cn.shu.heartsound.presenter.GuidePresenter;

public class GuideActivity extends AppCompatActivity {
    private GuidePresenter guidePresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusColor(this,true,true);
        setContentView(R.layout.activity_guide);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        guidePresenter = new GuidePresenter(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        guidePresenter.RemoveCallBacksAndMessages();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 显示Toast提示
     *
     * @param message
     */
    public void ShowToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}