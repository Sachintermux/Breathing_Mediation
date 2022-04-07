package com.cipherapps.breathingmeditation.savedata;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cipherapps.breathingmeditation.R;

import java.util.ArrayList;

public class RemindMeList_Adapter extends ArrayAdapter<RemindModels> {
    private ArrayList<RemindModels> list = new ArrayList<>();
    private Context context;
    private adapterInterface listener;

    public RemindMeList_Adapter( @NonNull Context context, ArrayList<RemindModels> remindModels ) {
        super(context, R.layout.remind_list_layout, remindModels);
        this.context = context;
        this.list = remindModels;
        listener = (adapterInterface) context;
    }


    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView( int position, @Nullable View convertView, @NonNull ViewGroup parent ) {
        RemindModels remindModels = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.remind_list_layout, parent, false);
        }

        TextView serialNo, title, time;
        ImageView editNotification;

        serialNo = convertView.findViewById(R.id.serialNo_remindList);
        title = convertView.findViewById(R.id.titleTxt_remindList);
        time = convertView.findViewById(R.id.timeTxt_remindList);

        editNotification = convertView.findViewById(R.id.editNote_remindList);

        serialNo.setText(String.valueOf(position + 1) + ".");
        title.setText(remindModels.getName());

        timeSetter(time, remindModels);


        editNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                listener.editNoteClick(position);
            }
        });

        return convertView;
    }

    private void timeSetter( TextView time, RemindModels models ) {

        String isAm = " AM";
        if(models.getHour() / 12 > 0){
            isAm = " PM";
        }

        String timeString = String.format("%02d:%02d", models.getHour()%12, models.getMinute());
        time.setText(timeString + isAm);

    }

    public interface adapterInterface {
        void editNoteClick( int position );
    }
}
