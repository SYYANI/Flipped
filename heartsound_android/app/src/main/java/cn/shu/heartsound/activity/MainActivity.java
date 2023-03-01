package cn.shu.heartsound.activity;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;

import cn.shu.heartsound.R;
import cn.shu.heartsound.databinding.ActivityMainBinding;
import cn.shu.heartsound.pojo.patient.PatientResult;
import cn.shu.heartsound.tools.SPUtils;
import cn.shu.heartsound.tools.TokenUtils;
import cn.shu.heartsound.viewmodel.PatientViewModel;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActivityMainBinding binding;
    private String user_kind_string;
    Handler handler_main_activity;
    //    private String accessid;
    private String type;
    //    private String telephone;
    private long exitTime = 0;
    private PatientViewModel patientViewModel;
    NavController navController;
    Menu menu;
    String nowFragment;
    //Glide请求图片选项配置
    private RequestOptions requestOptions_main = RequestOptions.circleCropTransform()
            .diskCacheStrategy(DiskCacheStrategy.NONE)//不做磁盘缓存
            .skipMemoryCache(true);//不做内存缓存
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        patientViewModel = new ViewModelProvider(this).get(PatientViewModel.class);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

//        accessid = bundle.getString("Accessid");
        type = bundle.getString("Type");
//        telephone = bundle.getString("Telephone");
        Log.d(TAG, "onCreate: type" + type);
//        Log.d(TAG, "onCreate: "+"accessid: "+accessid+" type: "+type+" telephone "+telephone);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);
        menu = navigationView.getMenu();
//        if(user_kind_string.equals("student")){
//            Log.d(TAG, "onCreate: "+user_kind_string);
//            menu.removeGroup(R.id.teacher_menu_group);
//            menu.removeGroup(R.id.admin_menu_group);
//        } else if(user_kind_string.equals("teacher")){
//            StudentManagerApplication application =(StudentManagerApplication) getApplication();
//            if(application.getName().equals("admin")){
//                menu.removeGroup(R.id.student_menu_group);
//                menu.removeGroup(R.id.teacher_menu_group);
//            } else{
//                menu.removeGroup(R.id.student_menu_group);
//                menu.removeGroup(R.id.admin_menu_group);
//            }
//        }
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(navigationView, navController);
        Log.d(TAG, "onCreate: " + menu.getItem(0));
        NavigationUI.onNavDestinationSelected(menu.getItem(0), navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
                                                          @Override
                                                          public void onDestinationChanged(
                                                                  @NonNull NavController controller,
                                                                  @NonNull NavDestination destination,
                                                                  @Nullable Bundle arguments
                                                          ) {
                                                              toolbar.setTitle(destination.getLabel());
                                                              nowFragment = destination.getLabel().toString();
//                                                               if(!navController.popBackStack(destination.getId(),false)){
//                                                                   toolbar.setTitle(destination.getLabel());
//                                                               }
//                        Toast.makeText(getApplicationContext(), destination.getLabel(), Toast.LENGTH_SHORT).show();
                                                          }
                                                      }
        );

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.openNavDrawer,
                R.string.closeNavDrawer
        );

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        initUi();
//        handler_main_activity = new Handler(){
//            @Override
//            public void handleMessage(Message msg) {
//                if(msg.what == RequestConstant.REQUEST_SUCCESS){
//                    Snackbar snackbar = Snackbar.make(findViewById(R.id.move_linear_openclass), "操作成功", Snackbar.LENGTH_SHORT);
//                    snackbar.show();
//                } else if(msg.what == RequestConstant.REQUEST_FAILURE){
//                    Snackbar snackbar = Snackbar.make(findViewById(R.id.move_linear_openclass), "操作失败，请联系管理员", Snackbar.LENGTH_SHORT);
//                    snackbar.show();
//                }
//            }
//        };
    }

    private void initUi() {
        View header = binding.navView.getHeaderView(0);
        TextView text_user_name = header.findViewById(R.id.user_name_text);
        TextView text_user_id = header.findViewById(R.id.user_id_text);
        TextView text_current_type = header.findViewById(R.id.current_user_type);
        ImageView image_head_view = header.findViewById(R.id.nav_head_head_view);

        Context context = getApplicationContext();
        if (type.equals("0")) {
            patientViewModel.getMutableLiveData_patient_result().observe(this, new Observer<PatientResult>() {
                @Override
                public void onChanged(PatientResult patientResult) {
                    text_user_name.setText(patientResult.getPatientName());
                    text_user_id.setText("ID：" + patientResult.getPatientId());
                    text_current_type.setText("患者");
                    String imageUrl = patientResult.getPhoto();
                    if (imageUrl != null) {
                        Glide.with(context).load(imageUrl).apply(requestOptions_main).into(image_head_view);
                    }
                }
            });
        }else if(type.equals("-1")){
            NavigationUI.onNavDestinationSelected(menu.getItem(2), navController);
            ShowToast("请更新个人信息！");
            text_current_type.setText("游客");
        } else if (type.equals("1")) {
//            text_current_type.setText("医生");
        }
        //取出缓存
//        String imageUrl = SPUtils.getString("imageUrl",null,this);
//        if(imageUrl != null){
//            Glide.with(this).load(imageUrl).apply(requestOptions_main).into(image_head_view);
//        }
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationUI.onNavDestinationSelected(menu.getItem(2), navController);
                drawerLayout .closeDrawer(Gravity.LEFT);
            }
        });
        MaterialButton button_logout = findViewById(R.id.main_activity_footer_button_exit);
        button_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialoDelete();
            }
        });
    }

    private void showAlertDialoDelete() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("退出登录")
                .setMessage("确认退出登录吗？")
                .setNeutralButton("取消",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("确认",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TokenUtils.ClearUserToken(MainActivity.this);
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finishAffinity();
                    }
                }).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

    public static void setStatusColor(Activity activity, boolean isTranslate, boolean isDarkText) {
        //如果系统为6.0及以上，就可以使用Android自带的方式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = activity.getWindow();
            View decorView = window.getDecorView();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS); //可有可无
            decorView.setSystemUiVisibility((isTranslate ? View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN : 0) | (isDarkText ? View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR : 0));
            window.setStatusBarColor(activity.getResources().getColor(R.color.white));
        }
    }

    /**
     * 处理返回操作
     *
     * @param keyCode
     * @param event
     * @return
     */
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
//            if ((System.currentTimeMillis() - exitTime) > 2000) {
//                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
//                exitTime = System.currentTimeMillis();
//            } else {
//                finishAffinity();
//            }
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (!nowFragment.equals("主页")) {
                NavigationUI.onNavDestinationSelected(menu.getItem(0), navController);
            } else {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                } else {
                    finishAffinity();
                }
                return true;
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    public void ShowToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}