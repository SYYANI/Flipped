package cn.shu.heartsound.network;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.Map;

import cn.shu.heartsound.constant.NetWorkTimeoutConstant;
import cn.shu.heartsound.exception.ResponseStatusCodeException;
import cn.shu.heartsound.pojo.history.HistoryListResult;
import cn.shu.heartsound.pojo.jsonResult.DataJsonResult;
import cn.shu.heartsound.pojo.patient.PatientResult;
import cn.shu.heartsound.pojo.self.HospitalListResult;
import cn.shu.heartsound.tools.OkHttpUtils;
import cn.shu.heartsound.tools.TokenUtils;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SelfNetwork {
    public static final String TAG = "SelfNetwork";
    public DataJsonResult<PatientResult> getPatientResult(String telephone, Context context) throws Exception {
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
            Log.d(TAG, "getPatientResult: "+dataJsonResult.toString());
            return dataJsonResult;
        } else {
            throw new ResponseStatusCodeException("服务暂不可用");
        }
    }

    public HospitalListResult initHospitalList(Context context) throws Exception {
        OkHttpClient okHttpClient = OkHttpUtils.GetOkHttpClient(NetWorkTimeoutConstant.LOGIN_NETWORK_TIMEOUT, context);
        RequestBody body = new FormBody.Builder()
                .build();
        Request request = new Request.Builder()
                .url("http://192.168.31.123:8080/hospital/list")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("telephone",TokenUtils.GetUserTelephone(context))
                .addHeader("accessid",TokenUtils.GetUserAccessId(context))
                .build();
        Response response = okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            Log.d(TAG, "RequestSend: success");
            HospitalListResult dataJsonResult = new HospitalListResult();
            dataJsonResult =new Gson().fromJson( response.body().string()
                    , new TypeToken<HospitalListResult>() {
                    }.getType());
            Log.d(TAG, "initHospitalList: "+dataJsonResult.toString());
            return dataJsonResult;
        } else {
            throw new ResponseStatusCodeException("服务暂不可用");
        }
    }
    public DataJsonResult<PatientResult> submitChange(Map<String,String> map, Context context) throws Exception {
        OkHttpClient okHttpClient = OkHttpUtils.GetOkHttpClient(NetWorkTimeoutConstant.LOGIN_NETWORK_TIMEOUT, context);
//        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
//                .build();
        RequestBody body = new FormBody.Builder()
                .add("patientname", map.get("patientname").toString())
                .add("relationname", map.get("relationname").toString())
                .add("relationtelephone", map.get("relationtelephone").toString())
                .build();

        Request request = new Request.Builder()
                .url("http://192.168.31.123:8080/patient/update")
                .method("POST", body)
                .addHeader("telephone", map.get("telephone").toString())
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
            Log.d(TAG, "getPatientResult: "+dataJsonResult.toString());
            return dataJsonResult;
        } else {
            throw new ResponseStatusCodeException("服务暂不可用");
        }
    }
}
