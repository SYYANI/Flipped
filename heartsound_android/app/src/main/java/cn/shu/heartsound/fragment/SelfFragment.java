package cn.shu.heartsound.fragment;

import static android.app.Activity.RESULT_OK;

import static cn.shu.heartsound.constant.DurationsKt.LARGE_COLLAPSE_DURATION;
import static cn.shu.heartsound.constant.DurationsKt.LARGE_EXPAND_DURATION;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.transition.Fade;
import cn.shu.heartsound.constant.RequestConstant;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import cn.shu.heartsound.HeartSoundApplication;
import cn.shu.heartsound.R;
import cn.shu.heartsound.activity.ClassifierActivity;
import cn.shu.heartsound.activity.JudgeActivity;
import cn.shu.heartsound.databinding.SelfFragmentBinding;
import cn.shu.heartsound.pojo.patient.PatientResult;
import cn.shu.heartsound.pojo.self.HospitalListResult;
import cn.shu.heartsound.pojo.self.HospitalResult;
import cn.shu.heartsound.presenter.SelfPresenter;
import cn.shu.heartsound.tools.BitmapUtils;
import cn.shu.heartsound.tools.CameraUtils;
import cn.shu.heartsound.tools.SPUtils;
import cn.shu.heartsound.tools.TokenUtils;
import cn.shu.heartsound.transition.InterpolatorsKt;
import cn.shu.heartsound.viewmodel.SelfViewModel;

public class SelfFragment extends Fragment {
    private Menu menu;
    private SelfViewModel selfViewModel;
    private SelfFragmentBinding selfFragmentBinding;
    private AutoCompleteTextView hospitalSelect;
    private ArrayList<String> hospitalList;
    private ArrayList<HospitalResult> hospitalResultArrayList;
    private ArrayAdapter<String> hospitalListAdapter;
    private SelfPresenter selfPresenter;
    private HeartSoundApplication heartSoundApplication;

    private SelfPresenterHandler selfPresenterHandler;
    //权限请求
    private RxPermissions rxPermissions;

    //是否拥有权限
    private boolean hasPermissions = false;

    //底部弹窗
    private BottomSheetDialog bottomSheetDialog;
    //弹窗视图
    private View bottomView;

    //存储拍完照后的图片
    private File outputImagePath;
    //启动相机标识
    public static final int TAKE_PHOTO = 1;
    //启动相册标识
    public static final int SELECT_PHOTO = 2;

    //图片控件
    private ShapeableImageView ivHead;
    //Base64
    private String base64Pic;
    //拍照和相册获取图片的Bitmap
    private Bitmap orc_bitmap;

    //Glide请求图片选项配置
    private RequestOptions requestOptions = RequestOptions.circleCropTransform()
            .diskCacheStrategy(DiskCacheStrategy.NONE)//不做磁盘缓存
            .skipMemoryCache(true);//不做内存缓存

    public static SelfFragment newInstance() {
        return new SelfFragment();
    }

    private static class SelfPresenterHandler extends Handler {
        private final WeakReference<SelfFragment> selfFragmentWeakReference;
        private SelfPresenterHandler(SelfFragment selfFragment) {
            this.selfFragmentWeakReference = new WeakReference<>(selfFragment);
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            SelfFragment selfFragment = selfFragmentWeakReference.get();
            if (selfFragment != null) {
                switch (msg.what) {
                    case RequestConstant.UPDATE_INFO_SUCCESS:
                        Toast.makeText(selfFragment.getContext(), "更新成功", Toast.LENGTH_SHORT).show();
                        NavController navController = Navigation.findNavController(selfFragment.selfFragmentBinding.getRoot());
                        NavigationUI.onNavDestinationSelected(selfFragment.menu.getItem(0),navController);
                        break;
                    case RequestConstant.UPDATE_INFO_FAILED:
                        Toast.makeText(selfFragment.getContext(), "更新失败", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        selfViewModel = new ViewModelProvider(getActivity()).get(SelfViewModel.class);
        selfFragmentBinding  = DataBindingUtil.inflate(inflater,R.layout.self_fragment,container,false);
        selfFragmentBinding.setSelfViewmodel(selfViewModel);
        selfFragmentBinding.setLifecycleOwner(getActivity());
        heartSoundApplication = (HeartSoundApplication) getActivity().getApplication();
        selfPresenter = new SelfPresenter(this);
        setHasOptionsMenu(true);//需要添加这行代码，来在fragment中获得menu
        ivHead = selfFragmentBinding.selfFragmentImageHeadview;
        //取出缓存
//        String imageUrl = SPUtils.getString("imageUrl",null,getActivity());
        String imageUrl = "https://syyan.site/gerenzhongxin.png";
        if(imageUrl != null){
            Glide.with(this).load(imageUrl).apply(requestOptions).into(ivHead);
        }
        initCardViewListener();
        setFadeDuration();
        return selfFragmentBinding.getRoot();
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu,menu);
        this.menu=menu;
    }
    private void initCardViewListener() {
        selfPresenter.getSelfInfo(heartSoundApplication.getTelephone());
        selfPresenter.getHospitalList();
        setHospitalChooseDropdownMenu();
        selfViewModel.getMutableLiveData_patient_result().observe(getActivity(), new Observer<PatientResult>() {
            @Override
            public void onChanged(PatientResult patientResult) {
                if(patientResult!=null){
                    selfFragmentBinding.selfFragmentTextUserId.setText(patientResult.getPatientId());
                    selfFragmentBinding.selfFragmentEditName.setText(patientResult.getPatientName());
                    selfFragmentBinding.selfFragmentEditFamilyName.setText(patientResult.getRelationName());
                    selfFragmentBinding.selfFragmentEditPhone.setText(patientResult.getRelationTel());
                }
            }
        });
        selfFragmentBinding.selfFragmentImageHeadview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //检查版本
                checkVersion();
                changeAvatar(view);
            }
        });
    }

    public SelfViewModel getSelfViewModel() {
        return selfViewModel;
    }
    private void setFadeDuration() {
        Fade fade = new Fade(Fade.OUT);
        fade.setDuration(LARGE_EXPAND_DURATION / 2);
        fade.setInterpolator(InterpolatorsKt.getFAST_OUT_LINEAR_IN());
        this.setExitTransition(fade);

        Fade fade_in = new Fade(Fade.IN);
        fade_in.setDuration(LARGE_COLLAPSE_DURATION);
        fade_in.setInterpolator(InterpolatorsKt.getLINEAR_OUT_SLOW_IN());
        fade_in.setStartDelay(LARGE_COLLAPSE_DURATION / 2);
        this.setReenterTransition(fade_in);
    }
    /**
     * 检查版本
     */
    private void checkVersion() {
        //Android6.0及以上版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //如果你是在Fragment中，则把this换成getActivity()
            rxPermissions = new RxPermissions(getActivity());
            //权限请求
            rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(granted -> {
                        if (granted) {//申请成功
                            showMsg("已获取权限");
                            hasPermissions = true;
                        } else {//申请失败
                            showMsg("权限未开启");
                            hasPermissions = false;
                        }
                    });
        } else {
            //Android6.0以下
            showMsg("无需请求动态权限");
        }
    }

    /**
     * 更换头像
     *
     * @param view
     */
    public void changeAvatar(View view) {
        bottomSheetDialog = new BottomSheetDialog(getActivity());
        bottomView = getLayoutInflater().inflate(R.layout.dialog_bottom, null);
        bottomSheetDialog.setContentView(bottomView);
//        bottomSheetDialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundColor(Color.TRANSPARENT);
        TextView tvTakePictures = bottomView.findViewById(R.id.tv_take_pictures);
        TextView tvOpenAlbum = bottomView.findViewById(R.id.tv_open_album);
        TextView tvCancel = bottomView.findViewById(R.id.tv_cancel);

        //拍照
        tvTakePictures.setOnClickListener(v -> {
            takePhoto();
            showMsg("拍照");
            bottomSheetDialog.cancel();
        });
        //打开相册
        tvOpenAlbum.setOnClickListener(v -> {
            openAlbum();
            showMsg("打开相册");
            bottomSheetDialog.cancel();
        });
        //取消
        tvCancel.setOnClickListener(v -> {
            bottomSheetDialog.cancel();
        });
        //底部弹窗显示
        bottomSheetDialog.show();
    }

    /**
     * 拍照
     */
    private void takePhoto() {
        if (!hasPermissions) {
            showMsg("未获取到权限");
            checkVersion();
            return;
        }
        SimpleDateFormat timeStampFormat = new SimpleDateFormat(
                "yyyy_MM_dd_HH_mm_ss");
        String filename = timeStampFormat.format(new Date());
        outputImagePath = new File(getActivity().getExternalCacheDir(),
                filename + ".jpg");
        Intent takePhotoIntent = CameraUtils.getTakePhotoIntent(getActivity(), outputImagePath);
        // 开启一个带有返回值的Activity，请求码为TAKE_PHOTO
        startActivityForResult(takePhotoIntent, TAKE_PHOTO);
    }

    /**
     * 打开相册
     */
    private void openAlbum() {
        if (!hasPermissions) {
            showMsg("未获取到权限");
            checkVersion();
            return;
        }
        startActivityForResult(CameraUtils.getSelectPhotoIntent(), SELECT_PHOTO);
    }
    /**
     * 返回到Activity
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //拍照后返回
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //显示图片
                    displayImage(outputImagePath.getAbsolutePath());
                }
                break;
            //打开相册后返回
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    String imagePath = null;
                    //判断手机系统版本号
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                        //4.4及以上系统使用这个方法处理图片
                        imagePath = CameraUtils.getImageOnKitKatPath(data, getActivity());
                    } else {
                        imagePath = CameraUtils.getImageBeforeKitKatPath(data, getActivity());
                    }
                    //显示图片
                    displayImage(imagePath);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 通过图片路径显示图片
     */
    private void displayImage(String imagePath) {
        if (!TextUtils.isEmpty(imagePath)) {

            //放入缓存
            SPUtils.putString("imageUrl",imagePath,getActivity());
            //上传图片
//            uploadImage(imagePath);
            //显示图片
            Glide.with(this).load(imagePath).apply(requestOptions).into(ivHead);

            //压缩图片
            orc_bitmap = CameraUtils.compression(BitmapFactory.decodeFile(imagePath));
            //转Base64
            base64Pic = BitmapUtils.bitmapToBase64(orc_bitmap);

        } else {
            showMsg("图片获取失败");
        }
    }

    /**
     * 上传图片
     */
//    private void uploadImage(String imagePath) {
//        //上传图片
//        File file = new File(imagePath);
//        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
//        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
//        //构建请求体
//        RequestBody body = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("file", file.getName(), requestBody)
//                .build();
//        //构建请求
//        Request request = new Request.Builder()
//                .url(Constants.UPLOAD_IMAGE)
//                .post(body)
//                .build();
//        //发送请求
//        OkHttpClient okHttpClient = new OkHttpClient();
//        okHttpClient.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                showMsg("图片上传失败");
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                String json = response.body().string();
//                Log.e("TAG", "onResponse: " + json);
//                try {
//                    JSONObject jsonObject = new JSONObject(json);
//                    String code = jsonObject.getString("code");
//                    if (code.equals("200")) {
//                        showMsg("图片上传成功");
//                    } else {
//                        showMsg("图片上传失败");
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }

    /**
     * Toast提示
     *
     * @param msg
     */
    private void showMsg(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onResume() {
        super.onResume();
        setHospitalChooseDropdownMenu();
        setButtonClick();
    }
    private void showAlertDialoDelete() {
        new MaterialAlertDialogBuilder(getContext())
                .setTitle("修改信息")
                .setMessage("确认提交修改信息吗？")
                .setNeutralButton("取消",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("确认",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        submitChange();
                    }
                }).show();
    }
    private void submitChange(){
        selfPresenterHandler = new SelfPresenterHandler(this);
        String name = selfFragmentBinding.selfFragmentEditName.getText().toString();
        String phone = TokenUtils.GetUserTelephone(getActivity());
        String relation = selfFragmentBinding.selfFragmentEditFamilyName.getText().toString();
        String relationPhone = selfFragmentBinding.selfFragmentEditPhone.getText().toString();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(relation) || TextUtils.isEmpty(relationPhone)) {
            showMsg("请检查信息是否填写完整！");
            return;
        }
        Map<String,String> map = new HashMap<>();
        map.put("patientname",name);
        map.put("telephone",phone);
        map.put("relationname",relation);
        map.put("relationtelephone",relationPhone);
        selfPresenter.submitChange(map,selfPresenterHandler);
    }
    private void setButtonClick(){
        selfFragmentBinding.patientSelfFragmentTouchToSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialoDelete();
            }
        });
    }
    private void setHospitalChooseDropdownMenu() {
        Log.d("TAG" ,"setHospitalChooseDropdownMenu: ");
       //设置下拉菜单的内容适配器
        hospitalResultArrayList = new ArrayList<>();
        hospitalList = new ArrayList<String>();
        selfViewModel.getMutableLiveData_hospital_list_result().observe(getActivity(), new Observer<HospitalListResult>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onChanged(HospitalListResult hospitalListResult) {
                if (hospitalListResult != null) {
                    hospitalResultArrayList = hospitalListResult.getResultArrayList();
                    if(hospitalResultArrayList!=null){
                        hospitalResultArrayList.forEach(hospitalResult -> {
                            if(!hospitalList.contains(hospitalResult.getHospitalName())){
                                hospitalList.add(hospitalResult.getHospitalName());
                            }
                        });
                    }}
            }
        });
        hospitalSelect = selfFragmentBinding.patientSelfFragmentHospitalSelect;
        hospitalListAdapter = new ArrayAdapter<>(getActivity(),R.layout.item_menu,hospitalList);
        hospitalSelect.setAdapter(hospitalListAdapter);
        hospitalSelect.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String hospitalName = hospitalList.get(position);
                String hospitalId = hospitalResultArrayList.get(position).getHospitalId();
                Log.d("TAG", "onItemSelected: "+hospitalName+" "+hospitalId);
            }
        });
    }

}