package cn.shu.heartsound.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import cn.shu.heartsound.pojo.self.DoctorDetailResult;

public class DoctorDetailViewModel extends ViewModel {
    private MutableLiveData<DoctorDetailResult> mutableLiveData_doctor_detail_result;
    public DoctorDetailViewModel() {
    }
    public DoctorDetailViewModel(MutableLiveData<DoctorDetailResult> mutableLiveData_doctor_detail_result) {
        this.mutableLiveData_doctor_detail_result = mutableLiveData_doctor_detail_result;
    }
    public MutableLiveData<DoctorDetailResult> getMutableLiveData_doctor_detail_result() {
        if(mutableLiveData_doctor_detail_result == null){
            mutableLiveData_doctor_detail_result = new MutableLiveData<DoctorDetailResult>();
        }
        return mutableLiveData_doctor_detail_result;
    }
    public void setMutableLiveData_doctor_detail_result(DoctorDetailResult mutableLiveData_doctor_detail_result) {
        this.mutableLiveData_doctor_detail_result.postValue(mutableLiveData_doctor_detail_result);
    }
}
