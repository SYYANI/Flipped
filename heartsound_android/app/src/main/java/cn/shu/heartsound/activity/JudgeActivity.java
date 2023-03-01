package cn.shu.heartsound.activity;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import cn.shu.heartsound.R;
import cn.shu.heartsound.databinding.ActivityJudgeBinding;
import cn.shu.heartsound.pojo.jsonResult.DataJsonResult;
import cn.shu.heartsound.pojo.judge.JudgeResult;
import cn.shu.heartsound.pojo.patient.PatientResult;
import cn.shu.heartsound.recorder.IdealRecorder;
import cn.shu.heartsound.recorder.StatusListener;
import cn.shu.heartsound.tools.TokenUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class JudgeActivity extends AppCompatActivity {
    private ActivityJudgeBinding activityJudgeBinding;
    Handler handler;
    private IdealRecorder idealRecorder;
    private IdealRecorder.RecordConfig recordConfig;
    private Map<String,Uri> videoUri = new HashMap<>();
    private Integer  frag = 1;
    private String telephone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        telephone = bundle.getString("Telephone");
//        telephone= TokenUtils.GetUserTelephone(this);
        setStatusColor(this,true,false);
        activityJudgeBinding = ActivityJudgeBinding.inflate(getLayoutInflater());
        setContentView(activityJudgeBinding.getRoot());
        initHeartView();
        idealRecorder = IdealRecorder.getInstance();
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
                    showAlertDialoDelete(msg.getData().getString("Kind"));
//                    Toast.makeText(JudgeActivity.this, msg.getData().getString("Kind"), Toast.LENGTH_SHORT).show();
                }
            }
        };
        recordConfig = new IdealRecorder.RecordConfig(MediaRecorder.AudioSource.MIC, 48000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

    }

    public void initHeartView(){
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.detect_in_progress;
        Uri uri  = Uri.parse(videoPath);
        videoUri.put("detect_in_progress",uri);

        videoPath = "android.resource://" + getPackageName() + "/" + R.raw.detect_pre_progress;
        uri  = Uri.parse(videoPath);
        videoUri.put("detect_pre_progress",uri);

        videoPath = "android.resource://" + getPackageName() + "/" + R.raw.detect_post_progress;
        uri  = Uri.parse(videoPath);
        videoUri.put("detect_post_progress",uri);

        videoPath = "android.resource://" + getPackageName() + "/" + R.raw.detect_paused;
        uri  = Uri.parse(videoPath);
        videoUri.put("detect_paused",uri);

        activityJudgeBinding.activityJudgeHeartRateVideo.setVideoURI(videoUri.get("detect_paused"));
//        MediaController mediaController = new MediaController(this);
//        mediaController.setAnchorView(activityJudgeBinding.activityJudgeHeartRateVideo);
//        mediaController.setVisibility(View.INVISIBLE);
//        activityJudgeBinding.activityJudgeHeartRateVideo.setMediaController(mediaController);
        activityJudgeBinding.activityJudgeHeartRateVideo.setMediaController(null);
//        activityJudgeBinding.activityJudgeHeartRateVideo.setVideoURI(uri);
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityJudgeBinding.activityJudgeHeartRateVideo.start();
        //循环
//        activityJudgeBinding.activityJudgeHeartRateVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mp.setLooping(true);
//            }
//        });
        activityJudgeBinding.activityJudgeHeartRateVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if(frag == 2){
                    activityJudgeBinding.activityJudgeHeartRateVideo.setVideoURI(videoUri.get("detect_in_progress"));
                    activityJudgeBinding.activityJudgeHeartRateVideo.start();
                }else if(frag == 3){
                    activityJudgeBinding.activityJudgeHeartRateVideo.setVideoURI(videoUri.get("detect_post_progress"));
                    activityJudgeBinding.activityJudgeHeartRateVideo.start();
                    frag = 1;
                }else {
                    activityJudgeBinding.activityJudgeHeartRateVideo.setVideoURI(videoUri.get("detect_paused"));
                    activityJudgeBinding.activityJudgeHeartRateVideo.start();
                }
            }
        });
        activityJudgeBinding.registerRecordBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                readyRecord();
                return true;
            }
        });
        activityJudgeBinding.registerRecordBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_UP:
                        stopRecord();
                        return false;
                }
                return false;
            }
        });
    }

    public static void setStatusColor(Activity activity, boolean isTranslate, boolean isDarkText) {
        //如果系统为6.0及以上，就可以使用Android自带的方式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = activity.getWindow();
            View decorView = window.getDecorView();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS); //可有可无
            decorView.setSystemUiVisibility((isTranslate ? View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN : 0) | (isDarkText ? View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR : 0));
            window.setStatusBarColor(activity.getResources().getColor(R.color.black));
        }
    }

    private StatusListener statusListener = new StatusListener() {
        @Override
        public void onStartRecording() {
            activityJudgeBinding.waveLineView.startAnim();
            activityJudgeBinding.activityJudgeTips.setText("正在检测...请稍等");
            activityJudgeBinding.activityJudgeHeartRateVideo.setVideoURI(videoUri.get("detect_pre_progress"));
            frag = 2 ;
            activityJudgeBinding.registerRecordBtn.setText("检测中");
//            tips.setText("开始录音");
        }

        @Override
        public void onRecordData(short[] data, int length) {

//            for (int i = 0; i < length; i += 220) {
//                waveView.addData(data[i]);
//            }
            Log.d(TAG, "onRecordData: " + "current buffer size is " + length);
//            Log.d("JudgeActivity", "current buffer size is " + length);
        }

        @Override
        public void onVoiceVolume(int volume) {
            double myVolume = (volume - 40) * 4;
            activityJudgeBinding.waveLineView.setVolume((int) myVolume);
            Log.d("JudgeActivity", "current volume is " + volume);
        }

        @Override
        public void onRecordError(int code, String errorMsg) {
            activityJudgeBinding.activityJudgeTips.setText("出现错误" + errorMsg);
        }

        @Override
        public void onFileSaveFailed(String error) {
            Toast.makeText(JudgeActivity.this, "文件保存失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFileSaveSuccess(String fileUri) {
            activityJudgeBinding.registerRecordBtn.setText("长按开始");
//            Toast.makeText(JudgeActivity.this, "文件保存成功,路径是" + fileUri, Toast.LENGTH_SHORT).show();
            final String path = fileUri;
            new Thread(){
                @Override
                public void run(){
                    super.run();
                    Message message = handler.obtainMessage();
                    Bundle bundle = new Bundle();

                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .build();
                    MediaType mediaType = MediaType.parse("text/plain");
                    File tempFile =new File( path.trim());
                    String fileName = tempFile.getName();
                    RequestBody body = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("input_image",fileName,
                                    RequestBody.create(MediaType.parse("application/octet-stream"),
                                            new File(path)))
                            .build();
                    Request request = new Request.Builder()
                            .url("http://101.35.20.64:5000/upload_image?telephone="+telephone)
                            .method("POST", body)
                            .build();
                    try {
                        Response response = client.newCall(request).execute();
                        if (response.isSuccessful()) {
                            message.what = 1;
                            DataJsonResult<JudgeResult> dataJsonResult = new DataJsonResult<JudgeResult>();
                            dataJsonResult =new Gson().fromJson( response.body().string()
                                    , new TypeToken<DataJsonResult<JudgeResult>>() {
                                    }.getType());
                            Log.d(TAG, "UserLogin: "+dataJsonResult.getData().getJudge());
                            bundle.putString("Kind",dataJsonResult.getData().getJudge());
//                            Log.d("MainActivity", "run: "+response.body().string());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            }.start();
        }

        @Override
        public void onStopRecording() {
            frag = 3;
            activityJudgeBinding.activityJudgeTips.setText("检测结束");
            activityJudgeBinding.waveLineView.stopAnim();
        }
    };

    private void showAlertDialoDelete(String result) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("智慧诊断")
                .setMessage("您本次的诊断结果为："+result+"\n\n是否进行人脸心跳检测? 这会帮助我们提高准确性")
                .setNeutralButton("取消",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setPositiveButton("确认",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Bundle bundle_tojudge = new Bundle();
                        bundle_tojudge.putString("JudgeResult", result);
                        Intent intent = new Intent(JudgeActivity.this, ClassifierActivity.class);
                        intent.putExtras(bundle_tojudge);
                        startActivity(intent);
                        finish();
                    }
                }).show();
    }

    /**
     * 准备录音 录音之前 先判断是否有相关权限
     */
    private void readyRecord() {
        check();
        record();
    }

    private void check(){
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.RECORD_AUDIO) !=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(
                    Manifest.permission.RECORD_AUDIO);
        }
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }
    /**
     * 开始录音
     */
    private void record() {
        //如果需要保存录音文件  设置好保存路径就会自动保存  也可以通过onRecordData 回调自己保存  不设置 不会保存录音
        idealRecorder.setRecordFilePath(getSaveFilePath());
//        idealRecorder.setWavFormat(false);
        //设置录音配置 最长录音时长 以及音量回调的时间间隔
        idealRecorder.setRecordConfig(recordConfig).setMaxRecordTime(5000).setVolumeInterval(200);
        //设置录音时各种状态的监听
        idealRecorder.setStatusListener(statusListener);
        idealRecorder.start(); //开始录音

    }

    /**
     * 获取文件保存路径
     *
     * @return
     */
    private String getSaveFilePath() {
        File file = new File(getFilesDir().getPath(), "Audio");
        if (!file.exists()) {
            file.mkdirs();
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String today = format.format(System.currentTimeMillis());
        File wavFile = new File(file, "-"+today+".wav");
        return wavFile.getAbsolutePath();
    }


    /**
     * 停止录音
     */
    private void stopRecord() {
        //停止录音
        idealRecorder.stop();
    }

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            });
 }