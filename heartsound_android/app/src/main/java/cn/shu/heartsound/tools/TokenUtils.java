package cn.shu.heartsound.tools;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenUtils {
    private static final String TAG = "TokenUtils";

    /**
     * 获取用户权限令牌
     *
     * @param context
     * @return
     */
    public static String GetUserAccessId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Heartsound", Context.MODE_PRIVATE);
        return sharedPreferences.getString("accessid", null);
    }

    /**
     * 获取用户类型
     *
     * @param context
     * @return
     */
    public static String GetUserType(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Heartsound", Context.MODE_PRIVATE);
        return sharedPreferences.getString("type", null);
    }

    /**
     * 获取用户号码
     *
     * @param context
     * @return
     */
    public static String GetUserTelephone(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Heartsound", Context.MODE_PRIVATE);
        return sharedPreferences.getString("telephone", null);
    }

    /**
     * 删除用户保存的权限和刷新令牌信息
     *
     * @param context
     */
    public static void ClearUserToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Heartsound", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }

    /**
     * 保存用户令牌信息
     *
     * @param accessid
     * @param type
     * @param context
     */
    public static void SaveUserToken(String accessid, String type, String telephone, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Heartsound", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("accessid", accessid);
        editor.putString("type", type);
        editor.putString("telephone", telephone);
        editor.apply();
    }

}
