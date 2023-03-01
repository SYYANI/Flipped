package cn.shu.heartsound.network;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import cn.shu.heartsound.HeartSoundApplication;
import cn.shu.heartsound.constant.NetWorkTimeoutConstant;
import cn.shu.heartsound.exception.ResponseStatusCodeException;
import cn.shu.heartsound.pojo.guide.GuideResult;
import cn.shu.heartsound.pojo.jsonResult.DataJsonResult;
import cn.shu.heartsound.tools.OkHttpUtils;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

public class GuideNetwork {
    private static final String TAG = "tokenNetWork";

    /**
     * 验证令牌
     *
     * @param accessId
     * @param telephone
     * @param context
     * @return
     */
    public DataJsonResult<GuideResult> ValidateAccessId(String accessId, String telephone, Context context) throws Exception {
        HeartSoundApplication application = (HeartSoundApplication) context;

        OkHttpClient okHttpClient = OkHttpUtils.GetOkHttpClient(NetWorkTimeoutConstant.TOKEN_NETWORK_TIMEOUT, context);
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");

        RequestBody body = new FormBody.Builder()
                .add("accessid", accessId)
                .add("telephone", telephone)
                .build();

        Request request = new Request.Builder()
                .url("http://192.168.31.123:8080/user/checkaccess")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = okHttpClient.newCall(request).execute();

        if (response.isSuccessful()) {
            Log.d(TAG, "RefreshToken: success");
            return new Gson().fromJson(response.body().string()
                    , new TypeToken<DataJsonResult<GuideResult>>() {
                    }.getType());
        }
        throw new ResponseStatusCodeException("服务暂不可用");
    }
    public static String requestBodyToString(RequestBody requestBody) throws IOException, IOException {
        Buffer buffer = new Buffer();
        requestBody.writeTo(buffer);
        return buffer.readUtf8();
    }
}
