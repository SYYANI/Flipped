package cn.shu.heartsound.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import cn.shu.heartsound.R;
import cn.shu.heartsound.pojo.medicine.MedicineResult;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.ViewHolder> {
    Context context;
    ArrayList<MedicineResult> medicineResultArrayList;
    public MedicineAdapter() {
        this.medicineResultArrayList = new ArrayList<MedicineResult>();
    }
    public MedicineAdapter(Context context, ArrayList<MedicineResult> medicineResultArrayList) {
        this.context = context;
        this.medicineResultArrayList = medicineResultArrayList;
    }
    public MedicineAdapter(Context context) {
        this.context = context;
        this.medicineResultArrayList = new ArrayList<MedicineResult>();
    }
    @NonNull
    @Override
    public MedicineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicine,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineAdapter.ViewHolder holder, int position) {
        if(medicineResultArrayList!=null && medicineResultArrayList.size()>0){
            MedicineResult medicineResultEntity = medicineResultArrayList.get(position);
            holder.medicineNameTextView.setText(medicineResultEntity.getMedicineName());
            holder.medicineInstructionTextView.setText(medicineResultEntity.getInstruction());
        }
    }

    public int getRecordId(){
        if(medicineResultArrayList!=null && medicineResultArrayList.size()>0){
            MedicineResult medicineResultEntity = medicineResultArrayList.get(0);
            return medicineResultEntity.getRecordId();
        }
        return 0;
    }
    @Override
    public int getItemCount() {
        if (medicineResultArrayList != null && medicineResultArrayList.size() > 0) {
            return medicineResultArrayList.size();
        }else  {
            return 0;
        }
    }
    public void updateMedicineList(ArrayList<MedicineResult> medicineResultArrayList){
        this.medicineResultArrayList.clear();
        this.medicineResultArrayList.addAll(medicineResultArrayList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView medicineNameTextView;
        TextView medicineInstructionTextView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            medicineNameTextView = itemView.findViewById(R.id.medicine_item_medicine_name);
            medicineInstructionTextView = itemView.findViewById(R.id.medicine_item_record_instruction);
        }
    }

}
