package cn.shu.heartsound.tools;

import android.content.Context;

import androidx.annotation.NonNull;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Dns;
import okhttp3.OkHttpClient;

public class OkHttpUtils {

    /**
     * DNS系统 留着以后可以使用腾讯或者阿里的dns服务
     */
    public static class OkHttpDns implements Dns {

        private static final Dns SYSTEM = Dns.SYSTEM;
        private static OkHttpDns instance = null;

        private OkHttpDns(Context context) {
        }

        public static OkHttpDns getInstance(Context context) {
            if (instance == null) {
                instance = new OkHttpDns(context);
            }
            return instance;
        }

        @Override
        public List<InetAddress> lookup(@NonNull String hostname) throws UnknownHostException {
            //走系统DNS服务解析域名
            return Dns.SYSTEM.lookup(hostname);
        }
    }

    /**
     * 获取OkHttpClient对象
     *
     * @param timeout 超时时间，单位为秒
     * @param context
     * @return
     */
    public static OkHttpClient GetOkHttpClient(int timeout, Context context) {
        return new OkHttpClient.Builder()
                .connectTimeout(timeout, TimeUnit.SECONDS).readTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS)
                .dns(OkHttpDns.getInstance(context)).build();
    }
}
