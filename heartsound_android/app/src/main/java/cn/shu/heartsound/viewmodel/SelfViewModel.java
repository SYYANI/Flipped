package cn.shu.heartsound.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import cn.shu.heartsound.pojo.patient.PatientResult;
import cn.shu.heartsound.pojo.self.HospitalListResult;

public class SelfViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<PatientResult> mutableLiveData_patient_result;
    private MutableLiveData<HospitalListResult> mutableLiveData_hospital_list_result;
    public SelfViewModel() {
    }
    public SelfViewModel(MutableLiveData<PatientResult> mutableLiveData_patient_result) {
        this.mutableLiveData_patient_result = mutableLiveData_patient_result;
    }
    public MutableLiveData<HospitalListResult> getMutableLiveData_hospital_list_result() {
        if(mutableLiveData_hospital_list_result == null){
            mutableLiveData_hospital_list_result = new MutableLiveData<HospitalListResult>();
        }
        return mutableLiveData_hospital_list_result;
    }
    public void setMutableLiveData_hospital_list_result(HospitalListResult mutableLiveData_hospital_list_result) {
        this.mutableLiveData_hospital_list_result.postValue(mutableLiveData_hospital_list_result);
    }
    public HospitalListResult getHospitalListResult(){
        if(mutableLiveData_hospital_list_result == null){
            mutableLiveData_hospital_list_result = new MutableLiveData<HospitalListResult>();
        }
        return mutableLiveData_hospital_list_result.getValue();
    }
    public MutableLiveData<PatientResult> getMutableLiveData_patient_result() {
        if(mutableLiveData_patient_result == null){
            mutableLiveData_patient_result = new MutableLiveData<PatientResult>();
        }
        return mutableLiveData_patient_result;
    }
    public void setMutableLiveData_patient_result(PatientResult mutableLiveData_patient_result) {
        this.mutableLiveData_patient_result.postValue(mutableLiveData_patient_result);
    }
    public PatientResult getPatientResult(){
        if(mutableLiveData_patient_result == null){
            mutableLiveData_patient_result = new MutableLiveData<PatientResult>();
        }
        return mutableLiveData_patient_result.getValue();
    }
}