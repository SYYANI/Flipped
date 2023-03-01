package cn.shu.heartsound.presenter;

import android.util.Log;

import cn.shu.heartsound.activity.MainActivity;
import cn.shu.heartsound.fragment.PatientFragment;
import cn.shu.heartsound.network.PatientNetwork;
import cn.shu.heartsound.pojo.history.LastResult;
import cn.shu.heartsound.pojo.jsonResult.DataJsonResult;
import cn.shu.heartsound.pojo.patient.PatientResult;
import cn.shu.heartsound.viewmodel.PatientViewModel;

public class PatientPresenter {
    private PatientFragment patientFragment;
    private PatientViewModel patientViewModel;
    private PatientNetwork patientNetwork;

    public PatientPresenter(final PatientFragment patientFragment) {
        this.patientFragment = patientFragment;
        this.patientViewModel = patientFragment.getPatientViewModel();
    }

    public void getPatientInfo(String telephone){
        patientNetwork = new PatientNetwork();
        new Thread(){
            @Override
            public void run(){
                super.run();
                try {
                    Log.d("TAG", "run: "+telephone);
                    DataJsonResult<PatientResult> result = patientNetwork.getPatientResult(telephone,patientFragment.getActivity());
                    if(result == null){
                        result = new DataJsonResult<PatientResult>();
                    }
                    patientViewModel.setMutableLiveData_patient_result(result.getData());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    public void getPatientLastJudge(String pid){
        patientNetwork = new PatientNetwork();
        new Thread(){
            @Override
            public void run(){
                super.run();
                try {

                    DataJsonResult<LastResult> result = patientNetwork.getPatientLastJudge(pid,patientFragment.getActivity());
                    if(result == null){
                        result = new DataJsonResult<LastResult>();
                    }
                    patientViewModel.setMutableLiveData_patient_last_result(result.getData());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
