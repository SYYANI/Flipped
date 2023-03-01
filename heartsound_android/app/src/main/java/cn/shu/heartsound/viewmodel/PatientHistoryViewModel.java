package cn.shu.heartsound.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import cn.shu.heartsound.pojo.history.LastResult;

public class PatientHistoryViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<ArrayList<LastResult>> mutableLiveData_patient_history_list;

    public PatientHistoryViewModel() {
    }

    public PatientHistoryViewModel(MutableLiveData<ArrayList<LastResult>> mutableLiveData_patient_history_list) {
        this.mutableLiveData_patient_history_list = mutableLiveData_patient_history_list;
    }

    public MutableLiveData<ArrayList<LastResult>> getMutableLiveData_patient_history_list() {
        if(mutableLiveData_patient_history_list == null){
            mutableLiveData_patient_history_list = new MutableLiveData<>();
        }
        return mutableLiveData_patient_history_list;
    }

    public void setMutableLiveData_patient_history_list(ArrayList<LastResult> mutableLiveData_patient_history_list) {
        this.mutableLiveData_patient_history_list.postValue(mutableLiveData_patient_history_list);
    }
}