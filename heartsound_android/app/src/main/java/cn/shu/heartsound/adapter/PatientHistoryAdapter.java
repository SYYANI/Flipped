package cn.shu.heartsound.adapter;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import cn.shu.heartsound.R;
import cn.shu.heartsound.pojo.history.LastResult;

public class PatientHistoryAdapter extends RecyclerView.Adapter<PatientHistoryAdapter.ViewHolder> {
    Context context;
    ArrayList<LastResult> judgeResultArrayList;

    MediaPlayer mediaPlayer;
    Handler handler = new Handler();
    SeekBar seekBar;
    ImageButton imageButton;

    public PatientHistoryAdapter(){
        this.judgeResultArrayList = new ArrayList<LastResult>();
    }
    public PatientHistoryAdapter(Context context, ArrayList<LastResult> judgeResultArrayList){
        this.context = context;
        this.judgeResultArrayList = judgeResultArrayList;
    }

    public PatientHistoryAdapter(Context context){
        this.context = context;
        this.judgeResultArrayList = new ArrayList<LastResult>();
    }

    @NonNull
    @Override
    public PatientHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_judge_file,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientHistoryAdapter.ViewHolder holder, int position) {
        if(judgeResultArrayList != null && judgeResultArrayList.size() > 0){
            LastResult judgeResultEntity = judgeResultArrayList.get(position);
            holder.ai_judge_date.setText(judgeResultEntity.getDate().toString());
            holder.ai_judge_result.setText(judgeResultEntity.getFresult());
            if(judgeResultEntity.getDid()!=null){
                holder.no_doctor_judge.setVisibility(View.GONE);
                holder.have_doctor_judged.setVisibility(View.VISIBLE);
                holder.doctor_judge_name.setText(judgeResultEntity.getDid());
                holder.doctor_judge_result.setText(judgeResultEntity.getDresult());
            }else{
                holder.no_doctor_judge.setVisibility(View.VISIBLE);
                holder.have_doctor_judged.setVisibility(View.GONE);
            }
            holder.play_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    seekBar = holder.seekBar;
                    imageButton = holder.play_btn;
                    initAudioPlayer(judgeResultEntity);
                }
            });
        }
    }

    private void initAudioPlayer(LastResult judgeResultEntity){
        seekBar.setMax(100);
        seekBar.setClickable(false);
        seekBar.setEnabled(false);
        seekBar.setFocusable(false);
        mediaPlayer = new MediaPlayer();
        prepareMediaPlayer(judgeResultEntity.getAudio());
        if(mediaPlayer.isPlaying()){
            handler.removeCallbacks(updater);
            mediaPlayer.pause();
            imageButton.setImageResource(R.drawable.play_circle);
        }else{
            mediaPlayer.start();
            imageButton.setImageResource(R.drawable.pause_circle);
            updateSeekbar();
        }
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mediaPlayer = new MediaPlayer();
                if(mediaPlayer.isPlaying()){
                    handler.removeCallbacks(updater);
                    mediaPlayer.pause();
                    imageButton.setImageResource(R.drawable.play_circle);
                }else{
                    mediaPlayer.start();
                    imageButton.setImageResource(R.drawable.pause_circle);
                    updateSeekbar();
                }
            }
        });
    }
    private void prepareMediaPlayer(String uri){
        try {
            seekBar.setProgress(0);
            mediaPlayer.reset();
            mediaPlayer.setDataSource(uri);
            mediaPlayer.prepare();
        } catch (Exception exception){
            Log.d(TAG, "prepareMediaPlayer: "+exception.getMessage());
        }
    }
    private Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateSeekbar();
        }
    };
    private void updateSeekbar(){
        if(mediaPlayer.isPlaying()){
            seekBar.setProgress((int)(((float)mediaPlayer.getCurrentPosition()/mediaPlayer.getDuration())*100));
            handler.postDelayed(updater,1000);
        }else{
            imageButton.setImageResource(R.drawable.play_circle);
            seekBar.setProgress(0);
//            mediaPlayer.release();
//            mediaPlayer=null;
        }
    }
    @Override
    public int getItemCount() {
        if(judgeResultArrayList!=null){
            return judgeResultArrayList.size();
        }else {
            return 0;
        }
    }

    public void updateHistoryList(ArrayList<LastResult> judgeResultArrayList){
        this.judgeResultArrayList.clear();
        this.judgeResultArrayList.addAll(judgeResultArrayList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView ai_judge_date, ai_judge_result;
        LinearLayout have_doctor_judged;
        TextView doctor_judge_name,doctor_judge_result;
        TextView no_doctor_judge;
        ImageButton play_btn;
        SeekBar seekBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ai_judge_date = itemView.findViewById(R.id.history_item_ai_judge_date);
            ai_judge_result = itemView.findViewById(R.id.history_item_ai_judge_result);
            have_doctor_judged = itemView.findViewById(R.id.history_item_have_doctor_judged);
            doctor_judge_name = itemView.findViewById(R.id.history_item_doctor_judge_name);
            doctor_judge_result = itemView.findViewById(R.id.history_item_doctor_judge_result);
            no_doctor_judge = itemView.findViewById(R.id.history_item_no_doctor_judge);
            play_btn = itemView.findViewById(R.id.history_item_play_btn);
            seekBar = itemView.findViewById(R.id.history_item_seekbar);
        }
    }
}
