package com.cipherapps.breathingmeditation.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.cipherapps.breathingmeditation.R;
import com.cipherapps.breathingmeditation.savedata.SaveAllSessionList_Adapter;
import com.cipherapps.breathingmeditation.savedata.SaveAllSessionModels;
import com.cipherapps.breathingmeditation.savedata.SharePrefDataSave;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ShowAllSaveSesionActivity extends AppCompatActivity implements SaveAllSessionList_Adapter.clickPosition{
    private Button button;
    private ListView listView;
    private SaveAllSessionList_Adapter adapter;
    private SharePrefDataSave sharePrefDataSave = new SharePrefDataSave();
    private ArrayList<SaveAllSessionModels> arrayList;
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_save_sesion);
        intiViews();

    }
    private void intiViews(){
        button = findViewById(R.id.newSession_showAllSessionActivity);
        listView = findViewById(R.id.listView_showAllSessionActivity);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                startActivity(new Intent(ShowAllSaveSesionActivity.this,MainActivity.class));

                finish();
            }
        });

    }

    public void setListView( ArrayList<SaveAllSessionModels> models ){
        if (models.size() > 0) {
            this.arrayList = models;
            adapter = new SaveAllSessionList_Adapter(this, models);
            listView.setAdapter(adapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            String data = sharePrefDataSave.getData(this, getString(R.string.SaveAllSessionTAG), "");
            System.out.println("Before Test Data  =" + data);
            if (data != "" && data != "null") {
                System.out.println("After Test  Data  =" + data.length());
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<SaveAllSessionModels>>() {
                }.getType();
                ArrayList<SaveAllSessionModels> models = gson.fromJson(data, type);
                setListView(models);
            }
        }catch (NullPointerException e){

        }
    }

    private void showDialog( SaveAllSessionModels models , int position) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.edit_savesession_dialog);
        dialog.show();
        TextView time = dialog.findViewById(R.id.timeTxt_editDialog),
                length = dialog.findViewById(R.id.lengthTxt_editDialog);

        EditText journal = dialog.findViewById(R.id.journalEdit_editDialog);

        Button cancel = dialog.findViewById(R.id.cancelBtn_editDialog),
                save = dialog.findViewById(R.id.saveJournalBtn_editDialog);
        time.setText(models.getTime());
        length.setText(models.getLength());
        journal.setText(models.getJournal());

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                dialog.cancel();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                models.setJournal(journal.getText().toString());
                arrayList.set(position,models);
                dialog.cancel();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        Gson gson = new Gson();
        String data = gson.toJson(arrayList);
        sharePrefDataSave.savedData(this,getString(R.string.SaveAllSessionTAG),data);
    }

    @Override
    public void AdapterEdited(int position) {
            arrayList.remove(position);
            adapter.notifyDataSetChanged();
    }

    @Override
    public void EditBtn_Click( int position ) {
        showDialog(arrayList.get(position),position);
    }

}