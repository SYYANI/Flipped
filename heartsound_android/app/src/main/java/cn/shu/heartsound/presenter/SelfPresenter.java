package cn.shu.heartsound.presenter;

import static android.os.SystemClock.sleep;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.util.Map;

import cn.shu.heartsound.constant.RequestConstant;
import cn.shu.heartsound.fragment.SelfFragment;
import cn.shu.heartsound.network.SelfNetwork;
import cn.shu.heartsound.pojo.jsonResult.DataJsonResult;
import cn.shu.heartsound.pojo.patient.PatientResult;
import cn.shu.heartsound.pojo.self.HospitalListResult;
import cn.shu.heartsound.recorder.utils.Log;
import cn.shu.heartsound.viewmodel.SelfViewModel;

public class SelfPresenter {
    private SelfFragment selfFragment;
    private SelfViewModel selfViewModel;
    private SelfNetwork selfNetwork;
    public SelfPresenter(final SelfFragment selfFragment) {
        this.selfFragment = selfFragment;
        this.selfViewModel = selfFragment.getSelfViewModel();
    }
    public void getSelfInfo(String telephone){
        selfNetwork = new SelfNetwork();
        new Thread(){
            @Override
            public void run(){
                super.run();
                try {
                    Log.d("TAG", "run: "+telephone);
                    DataJsonResult<PatientResult> result = selfNetwork.getPatientResult(telephone,selfFragment.getActivity());
                    if(result == null){
                        result = new DataJsonResult<PatientResult>();
                    }
                    selfViewModel.setMutableLiveData_patient_result(result.getData());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    public void getHospitalList(){
        selfNetwork = new SelfNetwork();
        new Thread(){
            @Override
            public void run(){
                super.run();
                try {
                    HospitalListResult result = selfNetwork.initHospitalList(selfFragment.getActivity());
                    if(result == null ){
                        result = new HospitalListResult();
                    }else {
                        selfViewModel.setMutableLiveData_hospital_list_result(result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    public void submitChange(Map<String,String> map,final Handler handler) {
        selfNetwork = new SelfNetwork();
        final Boolean[] changeSuccessful = {false};
        new Thread() {
            @Override
            public void run() {
                super.run();
                Message message = handler.obtainMessage();
                try {
                    DataJsonResult<PatientResult> result = selfNetwork.submitChange(map, selfFragment.getActivity());
                    if (result == null) {
                        result = new DataJsonResult<PatientResult>();
                    }
                    if(result.getStatus().equals("success")){
                        message.what = RequestConstant.UPDATE_INFO_SUCCESS;
                        selfViewModel.setMutableLiveData_patient_result(result.getData());
                    }else{
                        message.what = RequestConstant.UPDATE_INFO_FAILED;
                    }
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
