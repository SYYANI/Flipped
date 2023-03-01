package cn.shu.heartsound.network;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cn.shu.heartsound.constant.NetWorkTimeoutConstant;
import cn.shu.heartsound.exception.ResponseStatusCodeException;
import cn.shu.heartsound.pojo.history.LastResult;
import cn.shu.heartsound.pojo.jsonResult.DataJsonResult;
import cn.shu.heartsound.pojo.login.UserLoginResult;
import cn.shu.heartsound.pojo.patient.PatientResult;
import cn.shu.heartsound.tools.OkHttpUtils;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PatientNetwork {
    private static final String TAG = "patientNetWork";

    public DataJsonResult<PatientResult> getPatientResult(String telephone,Context context) throws Exception {
        OkHttpClient okHttpClient = OkHttpUtils.GetOkHttpClient(NetWorkTimeoutConstant.LOGIN_NETWORK_TIMEOUT, context);
//        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
//                .build();
        RequestBody body = new FormBody.Builder()
                .add("telephone", telephone)
                .build();

        Request request = new Request.Builder()
                .url("http://192.168.31.123:8080/patient/detailp")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        Response response = okHttpClient.newCall(request).execute();

//        Log.d(TAG, "UserLogin: "+response.body().string());
//        此处需注释 response只能被使用一次

        if (response.isSuccessful()) {
            Log.d(TAG, "RequestSend: success");
            DataJsonResult<PatientResult> dataJsonResult = new DataJsonResult<PatientResult>();
            dataJsonResult =new Gson().fromJson( response.body().string()
                    , new TypeToken<DataJsonResult<PatientResult>>() {
                    }.getType());
            Log.d(TAG, "UserLogin: "+dataJsonResult.toString());
            return dataJsonResult;
        } else {
            throw new ResponseStatusCodeException("服务暂不可用");
        }
    }

    public DataJsonResult<LastResult> getPatientLastJudge(String pid, Context context) throws Exception {
        OkHttpClient okHttpClient = OkHttpUtils.GetOkHttpClient(NetWorkTimeoutConstant.LOGIN_NETWORK_TIMEOUT, context);
//        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
//                .build();
        Log.d(TAG, "getPatientLastJudge66: "+pid);
        RequestBody body = new FormBody.Builder()
                .add("patientid", pid)
                .build();

        Request request = new Request.Builder()
                .url("http://192.168.31.123:8080/file/getlast")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        Response response = okHttpClient.newCall(request).execute();

//        Log.d(TAG, "UserLogin: "+response.body().string());
//        此处需注释 response只能被使用一次

        if (response.isSuccessful()) {
            Log.d(TAG, "RequestSend: success");
            DataJsonResult<LastResult> dataJsonResult = new DataJsonResult<LastResult>();
            dataJsonResult =new Gson().fromJson( response.body().string()
                    , new TypeToken<DataJsonResult<LastResult>>() {
                    }.getType());
            Log.d(TAG, "UserLogin: "+dataJsonResult.toString());
            return dataJsonResult;
        } else {
            throw new ResponseStatusCodeException("服务暂不可用");
        }
    }

}
