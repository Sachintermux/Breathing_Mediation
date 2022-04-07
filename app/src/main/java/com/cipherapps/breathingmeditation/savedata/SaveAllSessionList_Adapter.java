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

public class SaveAllSessionList_Adapter extends ArrayAdapter<SaveAllSessionModels> {

    private clickPosition listener;
    private ArrayList<SaveAllSessionModels> arrayList = new ArrayList<>();
    public SaveAllSessionList_Adapter( @NonNull Context context, ArrayList<SaveAllSessionModels> saveAllSessionModels ) {
        super(context, R.layout.show_session_listview,saveAllSessionModels);
        arrayList = saveAllSessionModels;
        listener = (clickPosition) context;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Nullable
    @Override
    public SaveAllSessionModels getItem( int position ) {
        return arrayList.get(position);
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView( int position, @Nullable View convertView, @NonNull ViewGroup parent ) {
        SaveAllSessionModels models = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.show_session_listview, parent, false);
        }

        TextView time = convertView.findViewById(R.id.timeListView_showSession),
                length= convertView.findViewById(R.id.lengthListView_showSession),
                journal = convertView.findViewById(R.id.journalListView_showSession);

        ImageView delete = convertView.findViewById(R.id.deleteListView_showSession),
                edit = convertView.findViewById(R.id.editListView_showSession);


        time.setText("Date :    "+models.getTime());
        length.setText("Length :    "  + String.valueOf(models.getLength()));
        journal.setText("Journal entry :  " + models.getJournal());

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                System.out.println("edit Btn Click");
                listener.EditBtn_Click(position);

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
               listener.AdapterEdited(position);
            }
        });

        return convertView;
    }



    public interface clickPosition{
        void AdapterEdited(int position);
        void EditBtn_Click(int position);
    }
}
