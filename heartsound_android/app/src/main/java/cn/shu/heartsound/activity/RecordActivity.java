package cn.shu.heartsound.activity;

import static cn.shu.heartsound.constant.DurationsKt.LARGE_COLLAPSE_DURATION;
import static cn.shu.heartsound.constant.DurationsKt.LARGE_EXPAND_DURATION;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.transition.Fade;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;

import cn.shu.heartsound.R;
import cn.shu.heartsound.adapter.RecordAdapter;
import cn.shu.heartsound.databinding.ActivityRecordBinding;
import cn.shu.heartsound.pojo.history.HistoryListResult;
import cn.shu.heartsound.pojo.record.RecordListResult;
import cn.shu.heartsound.pojo.record.RecordResult;
import cn.shu.heartsound.tools.TokenUtils;
import cn.shu.heartsound.transition.InterpolatorsKt;
import cn.shu.heartsound.viewmodel.RecordViewModel;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RecordActivity extends AppCompatActivity {

    private RecordViewModel recordViewModel;
    private ActivityRecordBinding recordBinding;
    private RecyclerView recordRecyclerView;
    private SwipeRefreshLayout recordSwipeRefreshLayout;
    private RecordAdapter recordAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            initRecordList();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recordViewModel = new ViewModelProvider(this).get(RecordViewModel.class);
        recordBinding = ActivityRecordBinding.inflate(getLayoutInflater());
        recordBinding.setRecordViewmodel(recordViewModel);
        recordBinding.setLifecycleOwner(this);
        recordRecyclerView = recordBinding.recordLayoutRecyclerView;
        setRecordRecyclerView();
        initSwipeRefreshLayout();
        try {
            initRecordList();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setContentView(recordBinding.getRoot());
        recordBinding.recordToolbar.setTitle("病历记录");
        setSupportActionBar(recordBinding.recordToolbar);
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
    @Override
    public void onResume() {
        super.onResume();
        try {
            initRecordList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void setRecordRecyclerView(){
        recordAdapter = new RecordAdapter(this);
        recordRecyclerView.setHasFixedSize(true);
        recordRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recordRecyclerView.setAdapter(recordAdapter);
        recordViewModel.getmutableLiveData_record_list().observe(this, new Observer<ArrayList<RecordResult>>() {
                    @Override
                    public void onChanged(ArrayList<RecordResult> recordResults) {
                        recordSwipeRefreshLayout.setRefreshing(false);
                        recordAdapter.updateRecordList(recordResults);
                    }
                }
        );
    }
    private void initSwipeRefreshLayout() {
        recordSwipeRefreshLayout = recordBinding.recordLayoutSwipe;
        recordSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    initRecordList();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void initRecordList() throws IOException {
        new Thread(){
            @Override
            public void run(){
                super.run();
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                RequestBody body = new FormBody.Builder()
                        .build();
                Log.d("TAG", "run99: "+TokenUtils.GetUserTelephone(getApplicationContext()));
                Request request = new Request.Builder()
                        .url("http://192.168.31.123:8080/record/getdetailbypatientp")
                        .method("POST", body)
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .addHeader("telephone", TokenUtils.GetUserTelephone(getApplicationContext()))
                        .addHeader("accessid", TokenUtils.GetUserAccessId(getApplicationContext()))
                        .build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    if(response.isSuccessful()){
                        RecordListResult dataJsonResult = new RecordListResult();
                        dataJsonResult = new Gson().fromJson(response.body().string()
                                , new TypeToken<RecordListResult>(){
                                }.getType());
                        recordViewModel.setmutableLiveData_record_list(dataJsonResult.getResultArrayList());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}