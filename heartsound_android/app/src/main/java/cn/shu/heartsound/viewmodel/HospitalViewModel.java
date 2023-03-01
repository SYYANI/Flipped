package cn.shu.heartsound.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import cn.shu.heartsound.pojo.self.HospitalResult;

public class HospitalViewModel extends ViewModel {
    private MutableLiveData<HospitalResult> mutableLiveData_hospital_result;
    public HospitalViewModel() {
    }
    private HospitalViewModel(MutableLiveData<HospitalResult> mutableLiveData_hospital_result) {
        this.mutableLiveData_hospital_result = mutableLiveData_hospital_result;
    }
    public MutableLiveData<HospitalResult> getMutableLiveData_hospital_result() {
        if(mutableLiveData_hospital_result == null){
            mutableLiveData_hospital_result = new MutableLiveData<HospitalResult>();
        }
        return mutableLiveData_hospital_result;
    }
    public void setMutableLiveData_hospital_result(HospitalResult mutableLiveData_hospital_result) {
        this.mutableLiveData_hospital_result.postValue(mutableLiveData_hospital_result);
    }
}
