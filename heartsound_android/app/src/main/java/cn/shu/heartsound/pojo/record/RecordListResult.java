package cn.shu.heartsound.pojo.record;

import java.util.ArrayList;

import cn.shu.heartsound.pojo.jsonResult.JsonResult;

public class RecordListResult extends JsonResult {
    private ArrayList<RecordResult> data;

    public ArrayList<RecordResult> getResultArrayList() {
        return data;
    }
}
