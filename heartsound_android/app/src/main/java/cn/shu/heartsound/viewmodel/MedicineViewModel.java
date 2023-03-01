package cn.shu.heartsound.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;

import cn.shu.heartsound.pojo.medicine.MedicineResult;

public class MedicineViewModel extends ViewModel {
    private MutableLiveData<HashMap<Integer,ArrayList<MedicineResult>>> medicineMapLiveData = new MutableLiveData<HashMap<Integer,ArrayList<MedicineResult>>>();
    public MedicineViewModel() {
    }
    public MedicineViewModel(MutableLiveData<HashMap<Integer,ArrayList<MedicineResult>>> medicineMapLiveData) {
        this.medicineMapLiveData = medicineMapLiveData;
    }
    public MedicineViewModel(HashMap<Integer,ArrayList<MedicineResult>> medicineMap) {
        this.medicineMapLiveData = new MutableLiveData<HashMap<Integer,ArrayList<MedicineResult>>>();
        this.medicineMapLiveData.setValue(medicineMap);
    }
    public MutableLiveData<HashMap<Integer,ArrayList<MedicineResult>>> getMedicineMapLiveData(){
        if(medicineMapLiveData==null){
            medicineMapLiveData = new MutableLiveData<HashMap<Integer,ArrayList<MedicineResult>>>();
        }
        return medicineMapLiveData;
    }

    public ArrayList<MedicineResult> getMedicineList(int type){
        if(medicineMapLiveData.getValue()==null){
            return null;
        }
        return medicineMapLiveData.getValue().get(type);
    }

    public void setMedicineList(int type,ArrayList<MedicineResult> medicineList){
        if(medicineMapLiveData.getValue()==null){
            medicineMapLiveData = new MutableLiveData<HashMap<Integer,ArrayList<MedicineResult>>>();
        }
        HashMap<Integer,ArrayList<MedicineResult>> medicineMap = medicineMapLiveData.getValue();
        if(medicineMap==null){
            medicineMap = new HashMap<Integer,ArrayList<MedicineResult>>();
        }
        medicineMap.put(type,medicineList);
        medicineMapLiveData.postValue(medicineMap);
    }

//    public MutableLiveData<ArrayList<MedicineResult>> getmutableLiveData_medicine_list() {
//        if(mutableLiveData_medicine_list == null){
//            mutableLiveData_medicine_list = new MutableLiveData<ArrayList<MedicineResult>>();
//        }
//        return mutableLiveData_medicine_list;
//    }
//    public void setmutableLiveData_medicine_list(ArrayList<MedicineResult> mutableLiveData_medicine_list) {
//        this.mutableLiveData_medicine_list.postValue(mutableLiveData_medicine_list);
//    }
}
