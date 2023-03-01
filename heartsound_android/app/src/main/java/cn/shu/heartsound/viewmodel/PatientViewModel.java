package cn.shu.heartsound.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import cn.shu.heartsound.pojo.history.LastResult;
import cn.shu.heartsound.pojo.medicine.MedicineResult;
import cn.shu.heartsound.pojo.patient.PatientResult;

public class PatientViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<PatientResult> mutableLiveData_patient_result;
    private MutableLiveData<LastResult> mutableLiveData_patient_last_result;
    private MutableLiveData<ArrayList<MedicineResult>> mutableLiveData_medicine_list;
    public PatientViewModel() {
    }

    public PatientViewModel(MutableLiveData<PatientResult> mutableLiveData_patient_result) {
        this.mutableLiveData_patient_result = mutableLiveData_patient_result;
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

    public MutableLiveData<LastResult> getMutableLiveData_patient_last_result() {
        if(mutableLiveData_patient_last_result == null){
            mutableLiveData_patient_last_result = new MutableLiveData<LastResult>();
        }
        return mutableLiveData_patient_last_result;
    }

    public void setMutableLiveData_patient_last_result(LastResult mutableLiveData_patient_last_result) {
        this.mutableLiveData_patient_last_result.postValue(mutableLiveData_patient_last_result);
    }

    public MutableLiveData<ArrayList<MedicineResult>> getMutableLiveData_medicine_list() {
        if(mutableLiveData_medicine_list == null){
            mutableLiveData_medicine_list = new MutableLiveData<ArrayList<MedicineResult>>();
        }
        return mutableLiveData_medicine_list;
    }
    public void setMutableLiveData_medicine_list(ArrayList<MedicineResult> mutableLiveData_medicine_list) {
        this.mutableLiveData_medicine_list.postValue(mutableLiveData_medicine_list);
    }
}