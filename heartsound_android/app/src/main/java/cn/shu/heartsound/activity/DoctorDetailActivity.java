package cn.shu.heartsound.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import cn.shu.heartsound.R;
import cn.shu.heartsound.databinding.ActivityDoctorDetailBinding;
import cn.shu.heartsound.pojo.jsonResult.DataJsonResult;
import cn.shu.heartsound.pojo.self.DoctorDetailResult;
import cn.shu.heartsound.pojo.self.HospitalResult;
import cn.shu.heartsound.tools.TokenUtils;
import cn.shu.heartsound.viewmodel.DoctorDetailViewModel;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DoctorDetailActivity extends AppCompatActivity {

    private DoctorDetailViewModel mDoctorDetailViewModel;
    private ActivityDoctorDetailBinding mActivityDoctorDetailBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDoctorDetailViewModel = new ViewModelProvider(this).get(DoctorDetailViewModel.class);
        mActivityDoctorDetailBinding = ActivityDoctorDetailBinding.inflate(getLayoutInflater());
        mActivityDoctorDetailBinding.setDoctorDetailViewmodel(mDoctorDetailViewModel);
        mActivityDoctorDetailBinding.setLifecycleOwner(this);
        initDoctorDetail();
        try {
            initDoctorDetailNet();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setContentView(mActivityDoctorDetailBinding.getRoot());
        mActivityDoctorDetailBinding.doctorDetailToolbar.setTitle("医生详情");
        setSupportActionBar(mActivityDoctorDetailBinding.doctorDetailToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
    private void initDoctorDetail() {
        mDoctorDetailViewModel.getMutableLiveData_doctor_detail_result().observe(this, new Observer<DoctorDetailResult>() {
            @Override
            public void onChanged(DoctorDetailResult doctorResultResult) {
                if (doctorResultResult != null) {
                    mActivityDoctorDetailBinding.activityDoctorDoctorName.setText(doctorResultResult.getName());
                    mActivityDoctorDetailBinding.activityDoctorOffice.setText(doctorResultResult.getOfficeLocation());
                    mActivityDoctorDetailBinding.activityDoctorOfficePhone.setText(doctorResultResult.getOfficeTel());
                    mActivityDoctorDetailBinding.doctorLayoutDetail.setText(doctorResultResult.getTitle());
                }
            }
        });
    }
    private void initDoctorDetailNet() throws IOException {
        new Thread(){
            @Override
            public void run(){
                super.run();
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                RequestBody body = new FormBody.Builder()
                        .build();
                Log.d("TAG", "run77: "+ TokenUtils.GetUserTelephone(getApplicationContext()));
                Request request = new Request.Builder()
                        .url("http://192.168.31.123:8080/doctor/detailp")
                        .method("POST", body)
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .addHeader("telephone", TokenUtils.GetUserTelephone(getApplicationContext()))
                        .addHeader("accessid", TokenUtils.GetUserAccessId(getApplicationContext()))
                        .build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    if(response.isSuccessful()){
                        DataJsonResult<DoctorDetailResult> dataJsonResult = new Gson().fromJson(response.body().string()
                                , new TypeToken<DataJsonResult<DoctorDetailResult>>(){
                                }.getType());
                        mDoctorDetailViewModel.setMutableLiveData_doctor_detail_result(dataJsonResult.getData());
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
            initDoctorDetailNet();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}