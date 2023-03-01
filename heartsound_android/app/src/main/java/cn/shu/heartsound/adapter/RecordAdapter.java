package cn.shu.heartsound.adapter;

import static cn.shu.heartsound.constant.DurationsKt.MEDIUM_EXPAND_DURATION;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import cn.shu.heartsound.R;
import cn.shu.heartsound.pojo.medicine.MedicineListResult;
import cn.shu.heartsound.pojo.medicine.MedicineResult;
import cn.shu.heartsound.pojo.record.RecordListResult;
import cn.shu.heartsound.pojo.record.RecordResult;
import cn.shu.heartsound.tools.TokenUtils;
import cn.shu.heartsound.transition.TransitionsKt;
import cn.shu.heartsound.viewmodel.MedicineViewModel;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {
    Context context;
    ArrayList<RecordResult> recordResultArrayList;
    private MedicineAdapter medicineAdapter;
    private RecyclerView medicineRecyclerView;
    private MedicineViewModel medicineViewModel;
    public RecordAdapter(){
        this.recordResultArrayList = new ArrayList<RecordResult>();
    }
    public RecordAdapter(Context context){
        this.context = context;
        this.recordResultArrayList = new ArrayList<RecordResult>();
    }
    public RecordAdapter(Context context, ArrayList<RecordResult> recordResultArrayList){
        this.context = context;
        this.recordResultArrayList = recordResultArrayList;
    }
    @NonNull
    @Override
    public RecordAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        medicineViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(MedicineViewModel.class);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(recordResultArrayList != null && recordResultArrayList.size() > 0){
            RecordResult recordResultEntity = recordResultArrayList.get(position);
            holder.updateTimeTextView.setText(formatDate(recordResultEntity.getUpdateDate()));
            holder.createTimeTextView.setText(formatDate(recordResultEntity.getCreateDate()));
            holder.recordIdTextView.setText(recordResultEntity.getRecordId().toString());
            holder.diseaseNameTextView.setText(recordResultEntity.getDiseaseName());
            holder.patientIdTextView.setText(recordResultEntity.getPatientId().toString());
            holder.patientNameTextView.setText(recordResultEntity.getPatientName());
            holder.hospitalNameTextView.setText(recordResultEntity.getHospitalName());
            holder.doctorNameTextView.setText(recordResultEntity.getDoctorName());
            holder.diseaseDetailTextView.setText(recordResultEntity.getDetail());
            try {
                initMedicineList(recordResultEntity.getRecordId().toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            holder.touchToMoreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Transition transition = TransitionsKt.fadeThrough().setDuration(MEDIUM_EXPAND_DURATION);
                    TransitionManager.beginDelayedTransition(holder.transParentLinearLayoutCompat,transition);
                    holder.recordItemNotDetailLinearLayout.setVisibility(View.GONE);
                    holder.recordItemDetailLinearLayout.setVisibility(View.VISIBLE);
                }
            });
            holder.touchToCloseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Transition transition = TransitionsKt.fadeThrough().setDuration(MEDIUM_EXPAND_DURATION);
                    TransitionManager.beginDelayedTransition(holder.transParentLinearLayoutCompat,transition);
                    holder.recordItemNotDetailLinearLayout.setVisibility(View.VISIBLE);
                    holder.recordItemDetailLinearLayout.setVisibility(View.GONE);
                }
            });
            medicineViewModel.getMedicineMapLiveData().observe((LifecycleOwner) context, new androidx.lifecycle.Observer<HashMap<Integer,ArrayList<MedicineResult>>>() {
                @Override
                public void onChanged(HashMap<Integer,ArrayList<MedicineResult>> medicineMapLiveData) {
                    if(medicineMapLiveData != null && medicineMapLiveData.size() > 0){
                        setMedicineRecyclerView(holder, medicineMapLiveData.get(recordResultEntity.getRecordId()));
                    }
                }
            });
        }
    }

    private void initMedicineList(String recordId) throws IOException {
        new Thread(){
            @Override
            public void run(){
                super.run();
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                RequestBody body = new FormBody.Builder()
                        .add("recordid",recordId)
                        .build();
                Log.d("TAG", "run88: "+ TokenUtils.GetUserTelephone(context));
                Request request = new Request.Builder()
                        .url("http://192.168.31.123:8080/medicine/listbyrecord")
                        .method("POST", body)
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .addHeader("telephone", TokenUtils.GetUserTelephone(context))
                        .addHeader("accessid", TokenUtils.GetUserAccessId(context))
                        .build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    if(response.isSuccessful()){
                        MedicineListResult dataJsonResult = new MedicineListResult();
                        dataJsonResult = new Gson().fromJson(response.body().string()
                                , new TypeToken<MedicineListResult>(){
                                }.getType());
                        medicineViewModel.setMedicineList(Integer.valueOf(recordId),dataJsonResult.getResultArrayList());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    private void setMedicineRecyclerView(ViewHolder holder, ArrayList<MedicineResult> medicineResults){
        medicineRecyclerView = holder.medicineListRecyclerView;
        medicineAdapter = new MedicineAdapter(context);
        medicineRecyclerView.setHasFixedSize(true);
        medicineRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        medicineRecyclerView.setAdapter(medicineAdapter);
        if(medicineResults != null && medicineResults.size() > 0){
            medicineAdapter.updateMedicineList(medicineResults);
        }
    }

    private String formatDate(Timestamp timestamp){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = format.format(timestamp);
        return dateString;
    }

    @Override
    public int getItemCount() {
        if(recordResultArrayList != null && recordResultArrayList.size() > 0){
            return recordResultArrayList.size();
        }else {
            return 0;
        }
    }

    public void updateRecordList(ArrayList<RecordResult> recordResultArrayListNew){
        this.recordResultArrayList.clear();
        this.recordResultArrayList.addAll(recordResultArrayListNew);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout recordItemNotDetailLinearLayout;
        LinearLayout recordItemDetailLinearLayout;
        TextView updateTimeTextView;
        TextView createTimeTextView;
        TextView recordIdTextView;
        TextView diseaseNameTextView;
        TextView patientIdTextView;
        TextView patientNameTextView;
        TextView doctorNameTextView;
        TextView hospitalNameTextView;
        TextView diseaseDetailTextView;
        MaterialButton touchToMoreButton;
        MaterialButton touchToCloseButton;
        RecyclerView medicineListRecyclerView;
        LinearLayout recordItemDrugListContainerLinearLayout;
        LinearLayoutCompat transParentLinearLayoutCompat;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recordItemDrugListContainerLinearLayout = itemView.findViewById(R.id.record_item_drug_list_container);
            medicineListRecyclerView = itemView.findViewById(R.id.record_item_drug_list);
            transParentLinearLayoutCompat = itemView.findViewById(R.id.record_item_trans_parent);
            recordItemNotDetailLinearLayout = itemView.findViewById(R.id.record_item_not_detail);
            recordItemDetailLinearLayout = itemView.findViewById(R.id.record_item_detail);
            diseaseDetailTextView = itemView.findViewById(R.id.record_item_disease_detail);
            createTimeTextView = itemView.findViewById(R.id.record_item_create_time);
            updateTimeTextView = itemView.findViewById(R.id.record_item_update_date);
            recordIdTextView = itemView.findViewById(R.id.record_item_record_id);
            diseaseNameTextView = itemView.findViewById(R.id.record_item_disease_name);
            patientIdTextView = itemView.findViewById(R.id.record_item_patient_id);
            patientNameTextView = itemView.findViewById(R.id.record_item_patient_name);
            doctorNameTextView = itemView.findViewById(R.id.record_item_doctor_name);
            hospitalNameTextView = itemView.findViewById(R.id.record_item_hospital_name);
            touchToMoreButton = itemView.findViewById(R.id.record_item_touch_to_more);
            touchToCloseButton = itemView.findViewById(R.id.record_item_touch_to_close);
        }
    }
 }
