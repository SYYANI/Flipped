package cn.shu.heartsound.model;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import java.io.IOException;

import cn.shu.heartsound.constant.RequestConstant;
import cn.shu.heartsound.exception.ResponseStatusCodeException;
import cn.shu.heartsound.network.GuideNetwork;
import cn.shu.heartsound.pojo.guide.GuideResult;
import cn.shu.heartsound.pojo.jsonResult.DataJsonResult;
import cn.shu.heartsound.tools.TokenUtils;

public class GuideModel {
    private static final String TAG = "guideModel";
    private GuideNetwork guideNetwork = new GuideNetwork();
    /**
     * 验证accessId有效性
     *
     * @param accessId
     */
    public void ValidateAccessId(final Handler handler, final String accessId, String telephone, final Context context) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                try {
                    DataJsonResult<GuideResult> result = guideNetwork.ValidateAccessId(accessId, telephone, context);
                    if (("success").equals(result.getStatus())) {
                        Log.d(TAG, "run: "+result.getData());
                        if (result.getData() != null) {
                            bundle.putBoolean("Validate", result.getData().getValidate());
//                            bundle.putString("type",result.getData().getType());
                            TokenUtils.SaveUserToken(accessId, result.getData().getType(), telephone, context);
                            message.what = RequestConstant.REQUEST_SUCCESS;
                        } else {
                            message.what = RequestConstant.SERVER_ERROR;
                        }
                    } else {
                        bundle.putString("Message", result.getData().getErrMsg());
                        message.what = RequestConstant.REQUEST_FAILURE;
                    }
                } catch (NullPointerException ignored) {

                } catch (IOException e) {
                    message.what = RequestConstant.REQUEST_TIMEOUT;
                } catch (ResponseStatusCodeException e) {
                    message.what = RequestConstant.SERVER_ERROR;
                } catch (Exception e) {
                    message.what = RequestConstant.UNKNOWN_ERROR;
                }
                message.setData(bundle);
                handler.sendMessage(message);
            }
        }.start();
    }

}
