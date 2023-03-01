package cn.shu.heartsound.pojo.jsonResult;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonResult implements Serializable {

    private String status;

    public JsonResult() {
    }

    public JsonResult(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "JsonResult{" +
                "status=" + status +
                '}';
    }
}
