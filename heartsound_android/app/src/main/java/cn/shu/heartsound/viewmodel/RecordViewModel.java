package cn.shu.heartsound.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import cn.shu.heartsound.pojo.record.RecordResult;

public class RecordViewModel extends ViewModel {
    private MutableLiveData<ArrayList<RecordResult>> mutableLiveData_record_list;
    public RecordViewModel() {
    }
    public RecordViewModel(MutableLiveData<ArrayList<RecordResult>> mutableLiveData_record_list) {
        this.mutableLiveData_record_list = mutableLiveData_record_list;
    }
    public MutableLiveData<ArrayList<RecordResult>> getmutableLiveData_record_list() {
        if(mutableLiveData_record_list == null){
            mutableLiveData_record_list = new MutableLiveData<ArrayList<RecordResult>>();
        }
        return mutableLiveData_record_list;
    }
    public void setmutableLiveData_record_list(ArrayList<RecordResult> mutableLiveData_record_list) {
        this.mutableLiveData_record_list.postValue(mutableLiveData_record_list);
    }
}
