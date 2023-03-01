package cn.shu.heartsound.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import cn.shu.heartsound.R;
import cn.shu.heartsound.databinding.ActivityHospitalBinding;
import cn.shu.heartsound.pojo.jsonResult.DataJsonResult;
import cn.shu.heartsound.pojo.record.RecordListResult;
import cn.shu.heartsound.pojo.self.HospitalResult;
import cn.shu.heartsound.tools.TokenUtils;
import cn.shu.heartsound.viewmodel.HospitalViewModel;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HospitalActivity extends AppCompatActivity {

    private HospitalViewModel mHospitalViewModel;
    private ActivityHospitalBinding mActivityHospitalBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHospitalViewModel = new ViewModelProvider(this).get(HospitalViewModel.class);
        mActivityHospitalBinding = ActivityHospitalBinding.inflate(getLayoutInflater());
        mActivityHospitalBinding.setHospitalViewmodel(mHospitalViewModel);
        mActivityHospitalBinding.setLifecycleOwner(this);
        initHospitalDetail();
        try {
            initDetailNet();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setContentView(mActivityHospitalBinding.getRoot());
        mActivityHospitalBinding.hospitalToolbar.setTitle("医院详情");
        setSupportActionBar(mActivityHospitalBinding.hospitalToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void initHospitalDetail() {
        mHospitalViewModel.getMutableLiveData_hospital_result().observe(this, new Observer<HospitalResult>() {
            @Override
            public void onChanged(HospitalResult hospitalResult) {
                if(hospitalResult != null){
//                    mActivityHospitalBinding.(hospitalResult);
                    mActivityHospitalBinding.hospitalName.setText(hospitalResult.getHospitalName());
                    mActivityHospitalBinding.hospitalAddress.setText(hospitalResult.getAddress());
                    mActivityHospitalBinding.hospitalTel.setText(hospitalResult.getHospitalTel());
                    mActivityHospitalBinding.hospitalLayoutHospitalDetail.setText(hospitalResult.getDetail());
                }
            }
        });
    }
    private void initDetailNet() throws IOException {
        new Thread(){
            @Override
            public void run(){
                super.run();
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                RequestBody body = new FormBody.Builder()
                        .build();
                Log.d("TAG", "run99: "+ TokenUtils.GetUserTelephone(getApplicationContext()));
                Request request = new Request.Builder()
                        .url("http://192.168.31.123:8080/hospital/detailp")
                        .method("POST", body)
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .addHeader("telephone", TokenUtils.GetUserTelephone(getApplicationContext()))
                        .addHeader("accessid", TokenUtils.GetUserAccessId(getApplicationContext()))
                        .build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    if(response.isSuccessful()){
                        DataJsonResult<HospitalResult> dataJsonResult = new Gson().fromJson(response.body().string()
                                , new TypeToken<DataJsonResult<HospitalResult>>(){
                                }.getType());
                        mHospitalViewModel.setMutableLiveData_hospital_result(dataJsonResult.getData());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    @Override
    public void onResume() {
        super.onResume();
        try {
            initDetailNet();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}