package cn.shu.heartsound.fragment;

import static android.content.ContentValues.TAG;
import static cn.shu.heartsound.constant.DurationsKt.LARGE_COLLAPSE_DURATION;
import static cn.shu.heartsound.constant.DurationsKt.LARGE_EXPAND_DURATION;
import static cn.shu.heartsound.constant.DurationsKt.MEDIUM_EXPAND_DURATION;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Fade;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;

import cn.shu.heartsound.HeartSoundApplication;
import cn.shu.heartsound.R;
import cn.shu.heartsound.activity.DoctorDetailActivity;
import cn.shu.heartsound.activity.EmergencyActivity;
import cn.shu.heartsound.activity.HospitalActivity;
import cn.shu.heartsound.activity.JudgeActivity;
import cn.shu.heartsound.activity.RecordActivity;
import cn.shu.heartsound.adapter.MedicineAdapter;
import cn.shu.heartsound.databinding.PatientFragmentBinding;
import cn.shu.heartsound.pojo.history.LastResult;
import cn.shu.heartsound.pojo.medicine.MedicineListResult;
import cn.shu.heartsound.pojo.medicine.MedicineResult;
import cn.shu.heartsound.pojo.patient.PatientResult;
import cn.shu.heartsound.presenter.PatientPresenter;
import cn.shu.heartsound.tools.TokenUtils;
import cn.shu.heartsound.transition.InterpolatorsKt;
import cn.shu.heartsound.transition.TransitionsKt;
import cn.shu.heartsound.viewmodel.PatientViewModel;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PatientFragment extends Fragment {
    private Menu menu;
    private PatientViewModel patientViewModel;
    private PatientFragmentBinding patientFragmentBinding;
    private HeartSoundApplication heartSoundApplication;
    private PatientPresenter patientPresenter;
    private MedicineAdapter medicineAdapter;
    private RecyclerView medicineRecyclerView;
    MediaPlayer mediaPlayer;
    Handler handler = new Handler();
    public static PatientFragment newInstance() {
        return new PatientFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        patientViewModel = new ViewModelProvider(getActivity()).get(PatientViewModel.class);
        patientFragmentBinding = DataBindingUtil.inflate(inflater,R.layout.patient_fragment,container,false);
        patientFragmentBinding.setPatientViewmodel(patientViewModel);
        patientFragmentBinding.setLifecycleOwner(getActivity());
        heartSoundApplication = (HeartSoundApplication) getActivity().getApplication();
        patientPresenter = new PatientPresenter(this);
        initCardViewListener();
        setFadeDuration();
        setHasOptionsMenu(true);//需要添加这行代码，来在fragment中获得menu
//        initAudioPlayer();
        setMedicineRecyclerView();
        try {
            initMedicineList();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return patientFragmentBinding.getRoot();
    }
    private void initMedicineList() throws IOException {
        new Thread(){
            @Override
            public void run(){
                super.run();
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                RequestBody body = new FormBody.Builder()
                        .build();
                Log.d("TAG", "run87: "+ TokenUtils.GetUserTelephone(getActivity()));
                Request request = new Request.Builder()
                        .url("http://192.168.31.123:8080/medicine/listlatest")
                        .method("POST", body)
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .addHeader("telephone", TokenUtils.GetUserTelephone(getActivity()))
                        .addHeader("accessid", TokenUtils.GetUserAccessId(getActivity()))
                        .build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    if(response.isSuccessful()){
                        MedicineListResult dataJsonResult = new MedicineListResult();
                        dataJsonResult = new Gson().fromJson(response.body().string()
                                , new TypeToken<MedicineListResult>(){
                                }.getType());
                        if(dataJsonResult.getResultArrayList().size()>0){
                            patientViewModel.setMutableLiveData_medicine_list(dataJsonResult.getResultArrayList());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    private void setMedicineRecyclerView(){
        medicineRecyclerView = patientFragmentBinding.patientFragmentDrugList;
        medicineAdapter = new MedicineAdapter(getActivity());
        medicineRecyclerView.setHasFixedSize(true);
        medicineRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        medicineRecyclerView.setAdapter(medicineAdapter);
//        if(medicineResults != null && medicineResults.size() > 0){
//            medicineAdapter.updateMedicineList(medicineResults);
//        }
        patientViewModel.getMutableLiveData_medicine_list().observe(getActivity(), new Observer<ArrayList<MedicineResult>>() {
            @Override
            public void onChanged(ArrayList<MedicineResult> medicineListResult) {
                if (medicineListResult != null && medicineListResult.size() > 0) {
                    medicineAdapter.updateMedicineList(medicineListResult);
                }
            }
        });
    }
    public PatientViewModel getPatientViewModel() {
        return patientViewModel;
    }

    private void updateUri(String uri){
        if(uri!=null){
            prepareMediaPlayer(uri);
            patientFragmentBinding.patientFragmentPlayBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mediaPlayer == null){
                        initAudioPlayer();
                    }
                    if(mediaPlayer.isPlaying()){
                        handler.removeCallbacks(updater);
                        mediaPlayer.pause();
                        patientFragmentBinding.patientFragmentPlayBtn.setImageResource(R.drawable.play_circle);
                    }else{
                        mediaPlayer.start();
                        patientFragmentBinding.patientFragmentPlayBtn.setImageResource(R.drawable.pause_circle);
                        updateSeekbar();
                    }
                }
            });
        }
    }
    private void initAudioPlayer(){
        patientFragmentBinding.patientFragmentSeekbar.setMax(100);
        patientFragmentBinding.patientFragmentSeekbar.setClickable(false);
        patientFragmentBinding.patientFragmentSeekbar.setEnabled(false);
        patientFragmentBinding.patientFragmentSeekbar.setFocusable(false);

        mediaPlayer = new MediaPlayer();
        if(patientViewModel.getMutableLiveData_patient_last_result().getValue()!=null){
            String uri = patientViewModel.getMutableLiveData_patient_last_result().getValue().getAudio();
            if(uri!=null){
                prepareMediaPlayer(uri);
                patientFragmentBinding.patientFragmentPlayBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(mediaPlayer.isPlaying()){
                            handler.removeCallbacks(updater);
                            mediaPlayer.pause();
                            patientFragmentBinding.patientFragmentPlayBtn.setImageResource(R.drawable.play_circle);
                        }else{
                            mediaPlayer.start();
                            patientFragmentBinding.patientFragmentPlayBtn.setImageResource(R.drawable.pause_circle);
                            updateSeekbar();
                        }
                    }
                });
            }
        }
    }

    private void prepareMediaPlayer(String uri){
        try {
            patientFragmentBinding.patientFragmentSeekbar.setProgress(0);
            mediaPlayer.reset();
            mediaPlayer.setDataSource(uri);
            mediaPlayer.prepare();
        } catch (Exception exception){
            Log.d(TAG, "prepareMediaPlayer: "+exception.getMessage());
        }
    }
    private Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateSeekbar();
        }
    };
    private void updateSeekbar(){
        if(mediaPlayer.isPlaying()){
            patientFragmentBinding.patientFragmentSeekbar.setProgress((int)(((float)mediaPlayer.getCurrentPosition()/mediaPlayer.getDuration())*100));
            handler.postDelayed(updater,1000);
        }else{
            patientFragmentBinding.patientFragmentPlayBtn.setImageResource(R.drawable.play_circle);
            patientFragmentBinding.patientFragmentSeekbar.setProgress(0);
        }
    }

    private void setFadeDuration() {
        Fade fade = new Fade(Fade.OUT);
        fade.setDuration(LARGE_EXPAND_DURATION / 2);
        fade.setInterpolator(InterpolatorsKt.getFAST_OUT_LINEAR_IN());
        this.setExitTransition(fade);

        Fade fade_in = new Fade(Fade.IN);
        fade_in.setDuration(LARGE_COLLAPSE_DURATION);
        fade_in.setInterpolator(InterpolatorsKt.getLINEAR_OUT_SLOW_IN());
        fade_in.setStartDelay(LARGE_COLLAPSE_DURATION / 2);
        this.setReenterTransition(fade_in);
    }

    private String getPeriord() {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("HH");
        String str = df.format(date);
        int a = Integer.parseInt(str);
        if (a >= 0 && a <= 6) {
            return ("凌晨好！");
        }else if (a > 6 && a <= 12) {
            return("上午好！");
        }else if (a > 12 && a <= 13) {
            return("中午好！");
        }else if (a > 13 && a <= 18) {
            return("下午好！");
        }else{
            return("晚上好！");
        }
    }

    private void initCardViewListener(){
        patientPresenter.getPatientInfo(heartSoundApplication.getTelephone());
        //此处子线程拉取患者信息，保存到viewModel中，后面配合使用监听更新
        patientViewModel.getMutableLiveData_patient_result().observe(getActivity(), new Observer<PatientResult>() {
            @Override
            public void onChanged(PatientResult patientResult) {
                patientFragmentBinding.patientFragmentPatientPeriod.setText(getPeriord());
                patientFragmentBinding.patientFragmentPatientName.setText(patientResult.getPatientName());
                patientFragmentBinding.patientFragmentPatientId.setText("患者序号: " + patientResult.getPatientId());
                patientFragmentBinding.patientFragmentPatientRelationName.setText("联系人姓名: " + patientResult.getRelationName());
                patientFragmentBinding.patientFragmentPatientRelationTel.setText("联系人电话: " + patientResult.getRelationTel());

//                patientFragmentBinding.patientFragmentPatientDiseaseType.setText(patientResult.getDisease());
                patientPresenter.getPatientLastJudge(patientResult.getPatientId());
            }
        });
        patientViewModel.getMutableLiveData_patient_last_result().observe(getActivity(), new Observer<LastResult>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onChanged(LastResult lastResult) {
                if (lastResult!= null){
                    //表明有诊断记录
                    patientFragmentBinding.patientFragmentHaveAiJudged.setVisibility(View.VISIBLE);
                    patientFragmentBinding.patientFragmentNoAiJudge.setVisibility(View.GONE);
                    patientFragmentBinding.patientFragmentLastAiJudgeDate.setText(lastResult.getDate().toString().substring(0,lastResult.getDate().toString().length()-2));
                    patientFragmentBinding.patientFragmentLastAiJudgeResult.setText(lastResult.getFresult());
                    patientFragmentBinding.patientFragmentPatientDiseaseRate.setText(lastResult.getRate());
                    String time_str = "";
                    LocalDate localDate = Instant.ofEpochMilli(lastResult.getDate().getTime()).atZone(ZoneOffset.ofHours(8)).toLocalDate();
                    Period p = Period.between(localDate, LocalDate.now());
                    if(p.getDays()>0){
                        time_str = p.getDays()+"天前";
                    }else{
                        Date date_now = new Date();
                        Date date_last = lastResult.getDate();
                        long time_diff = date_now.getTime() - date_last.getTime();
                        long time_diff_hour = time_diff/1000/60/60;
                        if(time_diff_hour>0){
                            time_str = time_diff_hour+"小时前";
                        }else{
                            long time_diff_min = time_diff/1000/60;
                            if(time_diff_min>0){
                                time_str = time_diff_min+"分钟前";
                            }else{
                                long time_diff_sec = time_diff/1000;
                                if(time_diff_sec>0){
                                    time_str = time_diff_sec+"秒前";
                                }
                            }
                        }
                    }
                    Log.d(TAG, "onChanged: "+time_str);
                    patientFragmentBinding.patientFragmentPatientHeartRateDate.setText(time_str);
                    updateUri(lastResult.getAudio());
                    Log.d(TAG, "onChanged: "+lastResult);
                    if(lastResult.getDid()!=null){
                        patientFragmentBinding.patientFragmentHaveDoctorJudged.setVisibility(View.VISIBLE);
                        patientFragmentBinding.patientFragmentNoDoctorJudge.setVisibility(View.GONE);
                        patientFragmentBinding.patientFragmentLastDoctorJudgeDate.setText(lastResult.getDate().toString().substring(0,lastResult.getDate().toString().length()-2));
                        patientFragmentBinding.patientFragmentLastDoctorJudgeName.setText(lastResult.getDid());
                        patientFragmentBinding.patientFragmentLastDoctorJudgeResult.setText(lastResult.getDresult());
                    }else{
                        patientFragmentBinding.patientFragmentHaveDoctorJudged.setVisibility(View.GONE);
                        patientFragmentBinding.patientFragmentNoDoctorJudge.setVisibility(View.VISIBLE);
                    }
                } else {
                    patientFragmentBinding.patientFragmentNoAiJudge.setVisibility(View.VISIBLE);
                    patientFragmentBinding.patientFragmentHaveAiJudged.setVisibility(View.GONE);
                }
            }
        });
        patientFragmentBinding.patientFragmentTouchToSeeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransAnimationGo();
            }
        });
        patientFragmentBinding.patientFragmentTouchToOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransAnimationBack();
            }
        });
        patientFragmentBinding.patientFragmentBeginNewJudgement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle_tojudge = new Bundle();
                bundle_tojudge.putString("Telephone", TokenUtils.GetUserTelephone(getActivity()));
                Intent intent = new Intent(getActivity(), JudgeActivity.class);
                intent.putExtras(bundle_tojudge);
                getActivity().startActivity(intent);
            }
        });
        patientFragmentBinding.patientFragmentDoctorJudgement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(view);
//                navController.navigate(R.id.action_patientFragment_to_patientHistoryFragment)
//                Log.d(TAG, "onClick: "+menu.get);
                NavigationUI.onNavDestinationSelected(menu.getItem(1),navController);
            }
        });
        patientFragmentBinding.patientFragmentPatientRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RecordActivity.class);
                getActivity().startActivity(intent);
            }
        });
        patientFragmentBinding.patientFragmentSosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EmergencyActivity.class);
                getActivity().startActivity(intent);
            }
        });
        patientFragmentBinding.patientFragmentHospitalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), HospitalActivity.class);
                getActivity().startActivity(intent);
            }
        });
        patientFragmentBinding.patientFragmentDoctorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), DoctorDetailActivity.class);
                getActivity().startActivity(intent);
            }
        });
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu,menu);
        this.menu=menu;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        patientFragmentBinding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            initMedicineList();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(patientViewModel.getPatientResult() != null){
            patientPresenter.getPatientLastJudge(patientViewModel.getPatientResult().getPatientId());
        }
    }
    /**
     * 过渡动画进入
     *
     */
    public void TransAnimationGo() {
        Transition transition = TransitionsKt.fadeThrough().setDuration(MEDIUM_EXPAND_DURATION);
        TransitionManager.beginDelayedTransition(patientFragmentBinding.patientFragmentMoveConstrainParent,transition);
        patientFragmentBinding.patientFragmentUserInformation.setVisibility(View.GONE);
        patientFragmentBinding.patientFragmentMoreUserInformation.setVisibility(View.VISIBLE);
    }
    /**
     * 过渡动画返回
     *
     */
    public void TransAnimationBack() {
        Transition transition = TransitionsKt.fadeThrough().setDuration(MEDIUM_EXPAND_DURATION);
        TransitionManager.beginDelayedTransition(patientFragmentBinding.patientFragmentMoveConstrainParent,transition);
        patientFragmentBinding.patientFragmentMoreUserInformation.setVisibility(View.GONE);
        patientFragmentBinding.patientFragmentUserInformation.setVisibility(View.VISIBLE);
    }
}