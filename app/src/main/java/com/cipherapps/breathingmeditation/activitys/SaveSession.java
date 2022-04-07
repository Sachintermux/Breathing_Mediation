package com.cipherapps.breathingmeditation.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.cipherapps.breathingmeditation.R;
import com.cipherapps.breathingmeditation.savedata.SaveAllSessionModels;
import com.cipherapps.breathingmeditation.savedata.SharePrefDataSave;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SaveSession extends AppCompatActivity {
    private TextView time_txt, length_txt;
    private EditText journal_edt;
    private Button viewAll_btn, save_btn;
    private SharePrefDataSave sharePrefDataSave = new SharePrefDataSave();
    private ArrayList<SaveAllSessionModels> arrayList = new ArrayList<>();
    private SaveAllSessionModels models;
    private int duration = 0;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        duration = getIntent().getIntExtra("sessionDuration", 0);
        if (duration == 0) {
            startActivity(new Intent(this, ShowAllSaveSesionActivity.class));
        } else {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_save_session);
            intiView();
            setLength_txt();
            setTime_txt();
        }
    }

    private void intiView() {
        time_txt = findViewById(R.id.timeTxt_saveSessionActivity);
        length_txt = findViewById(R.id.lengthTxt_saveSessionActivity);
        journal_edt = findViewById(R.id.journalEdit_saveSessionActivity);
        viewAll_btn = findViewById(R.id.viewAllJournalBtn_saveSessionActivity);
        save_btn = findViewById(R.id.saveJournalBtn_saveSessionActivity);

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                save_btnClick();
                finish();
            }
        });
        viewAll_btn.setOnClickListener(this::viewAll_btnClick);
    }

    private void save_btnClick() {
        @SuppressLint("DefaultLocale") String timeString = String.format("%02d:%02d", duration / 60, duration % 60);
        models = new SaveAllSessionModels(time_txt.getText().toString(),
              timeString, journal_edt.getText().toString());
       if (arrayList==null || arrayList.size() ==0) {
          arrayList = new ArrayList<>();
       }
        arrayList.add(models);
        saveData(arrayList);
        startActivity(new Intent(SaveSession.this, ShowAllSaveSesionActivity.class));
    }


    private void viewAll_btnClick( View view ) {
        startActivity(new Intent(SaveSession.this, ShowAllSaveSesionActivity.class));
    }

    @SuppressLint("SetTextI18n")
    private void setTime_txt() {
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("hh:mm  dd-MM-yyyy a");
        String dateToStr = format.format(today);
        time_txt.setText(time_txt.getText().toString() + dateToStr);
    }

    public void setLength_txt() {
            String timeString = String.format("%02d:%02d", duration / 60, duration % 60);
        length_txt.setText(length_txt.getText().toString() + timeString);
    }

    private void saveData( ArrayList<SaveAllSessionModels> arrayList ) {
        Gson gson = new Gson();
        String data = gson.toJson(arrayList);
        sharePrefDataSave.savedData(this, getString(R.string.SaveAllSessionTAG), data);
    }

    private ArrayList<SaveAllSessionModels> getData() {
        ArrayList<SaveAllSessionModels> arrayList = new ArrayList<>();
        String data = sharePrefDataSave.getData(this, getString(R.string.SaveAllSessionTAG), "");
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<SaveAllSessionModels>>() {
            }.getType();
           return gson.fromJson(data, type);
    }

    @Override
    public void onPause() {
        saveData(arrayList);
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        arrayList = getData();
    }
}




