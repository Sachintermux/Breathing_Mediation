package com.cipherapps.breathingmeditation.activitys;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.cipherapps.breathingmeditation.R;
import com.cipherapps.breathingmeditation.savedata.RemindMeList_Adapter;
import com.cipherapps.breathingmeditation.savedata.RemindModels;
import com.cipherapps.breathingmeditation.savedata.SharePrefDataSave;
import com.cipherapps.breathingmeditation.savedata.Show_NotificationBroadcast;
import com.cipherapps.breathingmeditation.viewmodels.BreathingTimeViewModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, RemindMeList_Adapter.adapterInterface {
    private TextView breathIn_txt, hold1_txt, hold2_txt, breathOut_txt, timerDuration_txt;
    private SeekBar breathIn_slider, breathOut_slider, hold1_slider, hold2_slider, timerDuration_slider;
    private RadioButton noSound_rdBtn,voice_rdBtn,bells_rdBtn;
    private BreathingTimeViewModel viewModel;
    private SharePrefDataSave sharePrefDataSave = new SharePrefDataSave();
    private BottomNavigationView bottomNavigationView;
    private ListView remind_listView;
    private RemindMeList_Adapter adapter;
    private ImageView addBtn, deteleBtn;
    private ArrayList<RemindModels> remindModelsArrayList = new ArrayList<>();
    private int position = 0;
    private Dialog dialog;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initializeView();
    }


    private void initializeView() {

        //Sliders
        breathIn_slider = findViewById(R.id.breathIn_slider);
        breathOut_slider = findViewById(R.id.breathOut_slider);
        hold1_slider = findViewById(R.id.hold1_slider);
        hold2_slider = findViewById(R.id.hold2_slider);
        timerDuration_slider = findViewById(R.id.duration_slider);

        //TextViews
        breathIn_txt = findViewById(R.id.breathIn_txtView);
        breathOut_txt = findViewById(R.id.breathOut_txtView);
        hold1_txt = findViewById(R.id.hold1_txtView);
        hold2_txt = findViewById(R.id.hold2_txtView);
        timerDuration_txt = findViewById(R.id.duration_txtView);

        //Radio Button
        bells_rdBtn = findViewById(R.id.bells_radioBtn);
        noSound_rdBtn = findViewById(R.id.noSound_radioBtn);
        voice_rdBtn = findViewById(R.id.voice_radioBtn);
        rdBtn_ClickHandle();

        //Dialog
        dialog = new Dialog(this);

        //ListView
        remind_listView = findViewById(R.id.remind_listView);

        //ImageView
        addBtn = findViewById(R.id.add_listIc);
        deteleBtn = findViewById(R.id.delete_listIc);

        //Bottom Navigation
        bottomNavigationView = findViewById(R.id.bottom_navigationSetting);
        bottomNavigationView.setOnNavigationItemSelectedListener(SettingsActivity.this);

        //getSave Data in ArrayList
        remindModelsArrayList = getSaveArrayList();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            addBtn.setOnClickListener(this::addBtnClick);
        }
        deteleBtn.setOnClickListener(this::deleteBtn);


        //View Model
        viewModel = new ViewModelProvider(this).get(BreathingTimeViewModel.class);
        viewModel.setContext(this);
        observersViewModel();
        viewModel.getAllSaveData(this);
        seekBarClickHandle();

    }

    private void rdBtn_ClickHandle(){
        voice_rdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                viewModel.setSoundSelection(3);
            }
        });

        noSound_rdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                viewModel.setSoundSelection(1);
            }
        });

        bells_rdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                viewModel.setSoundSelection(2);
            }
        });
    }

    private void saveArrayList() {
        setAndCancelAlarm(remindModelsArrayList);
        Gson gson = new Gson();
        String data = gson.toJson(remindModelsArrayList);
        sharePrefDataSave.savedData(this, getString(R.string.RemindListTAG), data);
    }

    private ArrayList<RemindModels> getSaveArrayList() {
        String data = sharePrefDataSave.getData(this, getString(R.string.RemindListTAG), "");
        String isFirstTime = sharePrefDataSave.getData(this,"FirstTime","");
        if (!data.equals("")) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<RemindModels>>() {
            }.getType();
            ArrayList<RemindModels> remindModels = gson.fromJson(data, type);
            setAdapterInListView(remindModels);
            Alarm_cancel(remindModels);
            return remindModels;
        } else{

            sharePrefDataSave.savedData(this,"FirstTime","Hello");
            if(isFirstTime.equals("")){
                ArrayList<RemindModels> arrayList = new ArrayList<>();
                arrayList.add(new RemindModels("Morning","Time to take Morning Breathing Exercise",6,00));
                arrayList.add(new RemindModels("Evening","Time to take Evening Breathing Exercise",18,00));
                setAdapterInListView(arrayList);
                return arrayList;
            }
        }
        return new ArrayList<RemindModels>();
    }

    private void setAdapterInListView( ArrayList<RemindModels> arrayList ) {
        adapter = new RemindMeList_Adapter(this, arrayList);
        remind_listView.setAdapter(adapter);
    }

    private void sendNotification(Context context) {
        System.out.println("Hello Show Notification");
        NotificationCompat.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            builder = new NotificationCompat.Builder(this, "CHANNEL_ID")
                    .setSmallIcon(R.drawable.ic_add)
                    .setContentTitle("My notification")
                    .setContentText("Much longer text that cannot fit one line...")
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("Much longer text that cannot fit one line..."))
                    .setPriority(  NotificationManager.IMPORTANCE_HIGH);
        }
        // Set the intent that will fire when the user taps the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(120, builder.build());
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void addBtnClick( View view ) {
        sendNotification(this);
        dialog.setContentView(R.layout.remindset_time_dialog);
        dialog.show();

        EditText title_edt = dialog.findViewById(R.id.titleEdt_remindDialog),
                shortDesc_edt = dialog.findViewById(R.id.shortDescEdt_remindDialog);
        TimePicker timePicker;
        timePicker = dialog.findViewById(R.id.timePicker_remindDialog);
        Button okBtn = dialog.findViewById(R.id.okBtn_remindDialog),
                cancelBtn = dialog.findViewById(R.id.cancelBtn_remindDialog),
                delete = dialog.findViewById(R.id.deleteBtn_remindDialog);
        delete.setEnabled(false);

            delete.setBackgroundColor(getColor(R.color.gray));

        cancelBtn.setOnClickListener(this::cancelBtn_dialog);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                if(!title_edt.getText().toString().equals("")) {
                    RemindModels models = new RemindModels(title_edt.getText().toString(), shortDesc_edt.getText().toString(), timePicker.getCurrentHour(), timePicker.getCurrentMinute());
                    remindModelsArrayList.add(models);
                    setAdapterInListView(remindModelsArrayList);
                    dialog.cancel();
                } else{
                    title_edt.setHintTextColor(getColor(R.color.red));
                    Toast.makeText(SettingsActivity.this, "Please Enter the Notification Title", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void cancelBtn_dialog( View view ) {
        dialog.cancel();
    }


    private void deleteBtn( View view ) {
        if (remindModelsArrayList.size() > 0) {
            remindModelsArrayList.remove(0);
            setAdapterInListView(remindModelsArrayList);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.setting_tab);
    }

    @Override
    public boolean onNavigationItemSelected( @NonNull MenuItem item ) {
        switch (item.getItemId()) {
            case R.id.home_tab:
                finish();
                return true;

            case R.id.setting_tab:

                return true;

        }
        return false;
    }


    private void observersViewModel() {
        viewModel.soundSelection.observe(this, value ->{
            if(value == 1) noSound_rdBtn.setChecked(true);
            else if(value == 2) bells_rdBtn.setChecked(true);
            else if(value == 3) voice_rdBtn.setChecked(true);
        });

        viewModel.breathInDuration.observe(this, value -> {
            breathIn_txt.setText(String.valueOf(value));
            breathIn_slider.setProgress(value);
        });

        viewModel.breathOutDuration.observe(this, value -> {
            breathOut_txt.setText(String.valueOf(value));
            breathOut_slider.setProgress(value);
        });

        viewModel.hold1Duration.observe(this, value -> {
            hold1_txt.setText(String.valueOf(value));
            hold1_slider.setProgress(value);
        });

        viewModel.hold2Duration.observe(this, value -> {
            hold2_txt.setText(String.valueOf(value));
            hold2_slider.setProgress(value);
        });

        viewModel.timerDuration.observe(this, value -> {
            timerDuration_txt.setText(String.valueOf(value));
            timerDuration_slider.setProgress(value);
        });
    }

    private void seekBarClickHandle() {
        breathIn_slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged( SeekBar seekBar, int i, boolean b ) {
                if (b)
                    viewModel.setBreathInDuration(i);
            }

            @Override
            public void onStartTrackingTouch( SeekBar seekBar ) {

            }

            @Override
            public void onStopTrackingTouch( SeekBar seekBar ) {

            }
        });
        breathOut_slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged( SeekBar seekBar, int i, boolean b ) {
                if (b)
                    viewModel.setBreathOutDuration(i);
            }

            @Override
            public void onStartTrackingTouch( SeekBar seekBar ) {

            }


            @Override
            public void onStopTrackingTouch( SeekBar seekBar ) {

            }
        });
        hold1_slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged( SeekBar seekBar, int i, boolean b ) {
                if (b)
                    viewModel.setHold1Duration(i);
            }

            @Override
            public void onStartTrackingTouch( SeekBar seekBar ) {

            }

            @Override
            public void onStopTrackingTouch( SeekBar seekBar ) {

            }
        });
        hold2_slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged( SeekBar seekBar, int i, boolean b ) {
                if (b)
                    viewModel.setHold2Duration(i);
            }

            @Override
            public void onStartTrackingTouch( SeekBar seekBar ) {

            }


            @Override
            public void onStopTrackingTouch( SeekBar seekBar ) {

            }
        });
        timerDuration_slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged( SeekBar seekBar, int i, boolean b ) {
                if (b)
                    viewModel.setTimerDuration(i);
            }

            @Override
            public void onStartTrackingTouch( SeekBar seekBar ) {

            }

            @Override
            public void onStopTrackingTouch( SeekBar seekBar ) {

            }
        });

    }


    public void newSessionClick( View view ) {
        finish();
    }

    private void setDefaultData( int breathIn, int hold1, int breathOut, int hold2, int time ) {
        viewModel.setBreathInDuration(breathIn);
        viewModel.setBreathOutDuration(breathOut);
        viewModel.setHold1Duration(hold1);
        viewModel.setHold2Duration(hold2);
        viewModel.setTimerDuration(time);

    }

    public void morningClick( View view ) {
        setDefaultData(
                getResources().getInteger(R.integer.morningBreathIn),
                getResources().getInteger(R.integer.morningHold1),
                getResources().getInteger(R.integer.morningBreathOut),
                getResources().getInteger(R.integer.morningHold2),
                getResources().getInteger(R.integer.morningTimeDuration)
        );
    }

    public void eveningClick( View view ) {
        setDefaultData(
                getResources().getInteger(R.integer.eveningBreathIn),
                getResources().getInteger(R.integer.eveningHold1),
                getResources().getInteger(R.integer.eveningBreathOut),
                getResources().getInteger(R.integer.eveningHold2),
                getResources().getInteger(R.integer.eveningTimeDuration)
        );
    }

    public void nightClick( View view ) {
        setDefaultData(
                getResources().getInteger(R.integer.nightBreathIn),
                getResources().getInteger(R.integer.nightHold1),
                getResources().getInteger(R.integer.nightBreathOut),
                getResources().getInteger(R.integer.nightHold2),
                getResources().getInteger(R.integer.nightTimeDuration)
        );
    }

    public void beginnerClick( View view ) {
        setDefaultData(
                getResources().getInteger(R.integer.beginnerBreathIn),
                getResources().getInteger(R.integer.beginnerHold1),
                getResources().getInteger(R.integer.beginnerBreathOut),
                getResources().getInteger(R.integer.beginnerHold2),
                getResources().getInteger(R.integer.beginnerTimeDuration)
        );

    }

    public void moderateClick( View view ) {
        setDefaultData(
                getResources().getInteger(R.integer.moderateBreathIn),
                getResources().getInteger(R.integer.moderateHold1),
                getResources().getInteger(R.integer.moderateBreathOut),
                getResources().getInteger(R.integer.moderateHold2),
                getResources().getInteger(R.integer.moderateTimeDuration)
        );
    }

    public void advanceClick( View view ) {
        setDefaultData(
                getResources().getInteger(R.integer.advanceBreathIn),
                getResources().getInteger(R.integer.advanceHold1),
                getResources().getInteger(R.integer.advanceBreathOut),
                getResources().getInteger(R.integer.advanceHold2),
                getResources().getInteger(R.integer.advanceTimeDuration)
        );
    }

    @Override
    public void editNoteClick( int position ) {
        this.position = position;
        notificationBtnClick(position, remindModelsArrayList);
    }

    private void notificationBtnClick( int position, ArrayList<RemindModels> arrayList ) {
        dialog.setContentView(R.layout.remindset_time_dialog);
        Button okBtn = dialog.findViewById(R.id.okBtn_remindDialog),
                cancelBtn = dialog.findViewById(R.id.cancelBtn_remindDialog),
                deleteBtn = dialog.findViewById(R.id.deleteBtn_remindDialog);

        TextView title = dialog.findViewById(R.id.titleEdt_remindDialog),
                shortDesc = dialog.findViewById(R.id.shortDescEdt_remindDialog);

        TimePicker timePicker = dialog.findViewById(R.id.timePicker_remindDialog);

        title.setText(arrayList.get(position).getName());
        shortDesc.setText(arrayList.get(position).getShortDescription());

        timePicker.setCurrentHour(arrayList.get(position).getHour());
        timePicker.setCurrentMinute(arrayList.get(position).getMinute());

        deleteBtn.setEnabled(true);
        dialog.show();

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                if(!title.getText().toString().equals("")) {
                    remindModelsArrayList.get(position).setName(title.getText().toString());
                remindModelsArrayList.get(position).setShortDescription(shortDesc.getText().toString());
                remindModelsArrayList.get(position).setHour(timePicker.getCurrentHour());
                remindModelsArrayList.get(position).setMinute(timePicker.getCurrentMinute());
                setAdapterInListView(remindModelsArrayList);
                dialog.cancel();
                } else{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        title.setHintTextColor(getColor(R.color.red));
                    }
                    Toast.makeText(SettingsActivity.this, "Please Enter the Notification Title", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelBtn.setOnClickListener(this::cancelBtn_dialog);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                remindModelsArrayList.remove(position);
                setAdapterInListView(remindModelsArrayList);
                dialog.cancel();
            }
        });

    }

    private void setAndCancelAlarm(ArrayList<RemindModels> list){
        for(int i=0; i<list.size(); i++){
            setAlarm(list.get(i),i);
        }

    }


    private void setAlarm(RemindModels models, int i){
        AlarmManager alarmManager;
        Calendar calendar = Calendar.getInstance();
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        calendar.set(Calendar.HOUR_OF_DAY, models.getHour());
        calendar.set(Calendar.MINUTE, models.getMinute());

        Intent intent = new Intent(this, Show_NotificationBroadcast.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, i, intent, 0);

        long time = (calendar.getTimeInMillis() - (calendar.getTimeInMillis() % 60000));
        if (System.currentTimeMillis() > time) {
            if (calendar.AM_PM == 0)
                time = time + (1000 * 60 * 60 * 12);
            else
                time = time + (1000 * 60 * 60 * 24);
        }

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, AlarmManager.INTERVAL_DAY, pendingIntent);
    }


    private void Alarm_cancel(ArrayList<RemindModels> remindModels) {
        for(int i=0; i<remindModels.size(); i++) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, Show_NotificationBroadcast.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, i, intent, 0);
            alarmManager.cancel(pendingIntent);
        }
    }

    @Override
    protected void onPause() {
        saveArrayList();
        super.onPause();
    }
}

