package cn.shu.heartsound.pojo.jsonResult;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataJsonResult<T> extends JsonResult {

    private static final String TAG = "result";
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public DataJsonResult() {
        super();
    }

    public DataJsonResult(String status, T data) {
        super(status);
        this.data = data;
    }

    public DataJsonResult(String status) {
        super(status);
    }

    @Override
    public String toString() {
        if(data!=null){
            return "DataJsonResult{" +
                    "data=" + data +
                    '}';
        }
        return "null";
    }
}

