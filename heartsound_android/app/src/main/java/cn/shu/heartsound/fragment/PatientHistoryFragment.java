package cn.shu.heartsound.fragment;

import static cn.shu.heartsound.constant.DurationsKt.LARGE_COLLAPSE_DURATION;
import static cn.shu.heartsound.constant.DurationsKt.LARGE_EXPAND_DURATION;

import androidx.activity.OnBackPressedCallback;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.transition.Fade;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;

import cn.shu.heartsound.R;
import cn.shu.heartsound.adapter.PatientHistoryAdapter;
import cn.shu.heartsound.databinding.PatientFragmentBinding;
import cn.shu.heartsound.databinding.PatientHistoryFragmentBinding;
import cn.shu.heartsound.pojo.history.HistoryListResult;
import cn.shu.heartsound.pojo.history.LastResult;
import cn.shu.heartsound.pojo.jsonResult.DataJsonResult;
import cn.shu.heartsound.pojo.patient.PatientResult;
import cn.shu.heartsound.tools.TokenUtils;
import cn.shu.heartsound.transition.InterpolatorsKt;
import cn.shu.heartsound.viewmodel.PatientHistoryViewModel;
import cn.shu.heartsound.viewmodel.PatientViewModel;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PatientHistoryFragment extends Fragment {

    private PatientHistoryViewModel patientHistoryViewModel;
    private PatientHistoryFragmentBinding patientHistoryFragmentBinding;
    private RecyclerView patient_history_recyclerview;
    private PatientHistoryAdapter patientHistoryAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    public static PatientHistoryFragment newInstance() {
        return new PatientHistoryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        patientHistoryViewModel = new ViewModelProvider(getActivity()).get(PatientHistoryViewModel.class);
        patientHistoryFragmentBinding = DataBindingUtil.inflate(inflater,R.layout.patient_history_fragment,container,false);
        patientHistoryFragmentBinding.setPatientHistoryViewmodel(patientHistoryViewModel);
        patientHistoryFragmentBinding.setLifecycleOwner(getActivity());
        setFadeDuration();
        initSwipeRefreshLayout();
        patient_history_recyclerview = patientHistoryFragmentBinding.patientHistoryRecyclerView;
        setPatientHistoryListRecyclerView();
        try {
            initStudentCourseList();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return patientHistoryFragmentBinding.getRoot();
//        return inflater.inflate(R.layout.patient_history_fragment, container, false);
    }
    private void initSwipeRefreshLayout(){
        swipeRefreshLayout = patientHistoryFragmentBinding.patientHistorySwipe;
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    initStudentCourseList();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void initStudentCourseList() throws IOException {
        new Thread(){
            @Override
            public void run(){
                super.run();
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
//                MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
//                RequestBody body = RequestBody.create(mediaType, "");

                RequestBody body = new FormBody.Builder()
                        .add("telephone", TokenUtils.GetUserTelephone(getContext()))
                        .build();
                Log.d("TAG", "run99: "+TokenUtils.GetUserTelephone(getContext()));
                Request request = new Request.Builder()
                        .url("http://192.168.31.123:8080/file/listp")
                        .method("POST", body)
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    if(response.isSuccessful()){
                        HistoryListResult dataJsonResult = new HistoryListResult();
                        dataJsonResult = new Gson().fromJson( response.body().string()
                                , new TypeToken<HistoryListResult>() {
                                }.getType());
                        patientHistoryViewModel.setMutableLiveData_patient_history_list(dataJsonResult.getResultArrayList());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void setPatientHistoryListRecyclerView() {
        patient_history_recyclerview.setHasFixedSize(true);
        patient_history_recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        patientHistoryAdapter = new PatientHistoryAdapter(getActivity());
        patient_history_recyclerview.setAdapter(patientHistoryAdapter);
        patientHistoryViewModel.getMutableLiveData_patient_history_list().observe(getActivity(), new Observer<ArrayList<LastResult>>() {
            @Override
            public void onChanged(ArrayList<LastResult> lastResults) {
                swipeRefreshLayout.setRefreshing(false);
                patientHistoryAdapter.updateHistoryList(lastResults);
            }
        });
    }

    private void setFadeDuration() {
        Fade fade = new Fade(Fade.OUT);
        fade.setDuration(LARGE_EXPAND_DURATION / 2);
        fade.setInterpolator(InterpolatorsKt.getFAST_OUT_LINEAR_IN());
        this.setExitTransition(fade);

        Fade fade_in = new Fade(Fade.IN);
        fade_in.setDuration(LARGE_COLLAPSE_DURATION /2);
        fade_in.setInterpolator(InterpolatorsKt.getLINEAR_OUT_SLOW_IN());
        fade_in.setStartDelay(LARGE_COLLAPSE_DURATION / 2);
        this.setReenterTransition(fade_in);
    }
    @Override
    public void onResume() {
        super.onResume();
        try {
            initStudentCourseList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}