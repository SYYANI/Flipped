package cn.shu.heartsound.pojo.self;

import java.util.ArrayList;

import cn.shu.heartsound.pojo.jsonResult.JsonResult;

public class HospitalListResult extends JsonResult {
    private ArrayList<HospitalResult> data;

    public ArrayList<HospitalResult> getResultArrayList() {
        return data;
    }
}
