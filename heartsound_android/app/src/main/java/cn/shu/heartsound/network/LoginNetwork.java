package cn.shu.heartsound.network;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import cn.shu.heartsound.constant.NetWorkTimeoutConstant;
import cn.shu.heartsound.exception.ResponseStatusCodeException;
import cn.shu.heartsound.pojo.jsonResult.DataJsonResult;
import cn.shu.heartsound.pojo.login.UserLoginResult;
import cn.shu.heartsound.pojo.login.UserSendSmsResult;
import cn.shu.heartsound.tools.OkHttpUtils;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

public class LoginNetwork {
    private static final String TAG = "loginNetWork";

    /**
     * 用户登录
     *
     * @param username
     * @param password
     * @param context
     * @return
     */
    public DataJsonResult<UserLoginResult> UserLogin(String username, String password, Context context) throws Exception {
        OkHttpClient okHttpClient = OkHttpUtils.GetOkHttpClient(NetWorkTimeoutConstant.LOGIN_NETWORK_TIMEOUT, context);

        RequestBody body = new FormBody.Builder()
                .add("telephone", username)
                .add("inputotp", password)
                .build();

        Request request = new Request.Builder()
                .url("http://192.168.31.123:8080/user/checkotp")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = okHttpClient.newCall(request).execute();

//        Log.d(TAG, "UserLogin: "+response.body().string());
//        此处需注释 response只能被使用一次

        if (response.isSuccessful()) {
            Log.d(TAG, "RequestSend: success");
            DataJsonResult<UserLoginResult> dataJsonResult = new DataJsonResult<UserLoginResult>();
            dataJsonResult =new Gson().fromJson( response.body().string()
                    , new TypeToken<DataJsonResult<UserLoginResult>>() {
                    }.getType());
            Log.d(TAG, "UserLogin: "+dataJsonResult.toString());
            return dataJsonResult;
        } else {
            throw new ResponseStatusCodeException("服务暂不可用");
        }
    }

    /**
     * 用户发送验证码
     *
     * @param username
     * @param context
     * @return
     * @throws Exception
     */
    public DataJsonResult<UserSendSmsResult> UserSendSms(String username, Context context) throws Exception {
//        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
//                .build();
        OkHttpClient okHttpClient = OkHttpUtils.GetOkHttpClient(NetWorkTimeoutConstant.LOGIN_NETWORK_TIMEOUT, context);
//        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
//        RequestBody body = RequestBody.create(mediaType, "");

        RequestBody body = new FormBody.Builder()
                .add("telephone", username)
                .build();

        Request request = new Request.Builder()
                .url("http://192.168.31.123:8080/user/getotp" )
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = okHttpClient.newCall(request).execute();
//        Log.d(TAG, "UserLogin: "+response.body().string());

        if (response.isSuccessful()) {
            Log.d(TAG, "UserSendSms: success response");
            DataJsonResult<UserSendSmsResult> dataJsonResult = new DataJsonResult<>();
            dataJsonResult = new Gson().fromJson( response.body().string()
                    , new TypeToken<DataJsonResult<UserSendSmsResult>>() {
                    }.getType());
//            Log.d(TAG, "UserSendSms: "+dataJsonResult.toString());
            return dataJsonResult;
        }
        throw new ResponseStatusCodeException("服务暂不可用");
    }
    public static String requestBodyToString(RequestBody requestBody) throws IOException, IOException {
        Buffer buffer = new Buffer();
        requestBody.writeTo(buffer);
        return buffer.readUtf8();
    }
}
