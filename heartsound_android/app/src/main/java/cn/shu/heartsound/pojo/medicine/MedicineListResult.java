package cn.shu.heartsound.pojo.medicine;

import java.util.ArrayList;

import cn.shu.heartsound.pojo.jsonResult.JsonResult;

public class MedicineListResult extends JsonResult {
    private ArrayList<MedicineResult> data;

    public ArrayList<MedicineResult> getResultArrayList() {
        return data;
    }
}
