package cn.shu.heartsound.pojo.history;

import java.util.ArrayList;

import cn.shu.heartsound.pojo.jsonResult.JsonResult;

public class HistoryListResult extends JsonResult {
    private ArrayList<LastResult> data;

    public ArrayList<LastResult> getResultArrayList() {
        return data;
    }
}
