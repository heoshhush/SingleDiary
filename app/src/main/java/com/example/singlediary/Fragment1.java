package com.example.singlediary;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;

import java.text.ParseException;
import java.util.Date;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;


public class Fragment1 extends Fragment {
    private static final String TAG = "Fragment1";

    RecyclerView recyclerView;
    RadioGroup radioGroup;
    Context context;
    NoteAdapter noteAdapter;
    OnTabItemSelectedListener onTabItemSelectedListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.context = context;
        if(context instanceof OnTabItemSelectedListener){
            onTabItemSelectedListener = (OnTabItemSelectedListener) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_1, container, false);


        noteAdapter = new NoteAdapter();
        noteAdapter.addItem(new Note(1, "0", "관악구 청룡동", "", "", "오늘은 일기장을 만들었다", "1", null, "8월 31일"));
        noteAdapter.addItem(new Note(2, "2", "관악구 청룡동", "", "", "머라노", "3", null, "2월 12일"));
        noteAdapter.addItem(new Note(3, "3", "관악구 청룡동", "", "", "ㅎㅇㅎㅇ", "4", null, "3월 20일"));
        recyclerView = view.findViewById(R.id.frag1_recyclerView);

        recyclerView.setAdapter(noteAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        radioGroup = view.findViewById(R.id.frag1_radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.frag1_radioBtn1:
                        noteAdapter.switchLayoutType(0);
                        break;
                    case R.id.frag1_radioBtn2:
                        noteAdapter.switchLayoutType(1);
                        break;
                }
                noteAdapter.notifyDataSetChanged();
            }
        });

        Button editDiaryBtn = view.findViewById(R.id.frag1_editDiaryBtn);
        editDiaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTabItemSelectedListener.onTabSelected(1);
            }
        });

        noteAdapter.setOnItemClickListener(new OnNoteItemClickListener() {
            @Override
            public void onNoteItemClick(NoteAdapter.Holder holder, View view, int position) {
                Note item = noteAdapter.getItem(position);
                Toast.makeText(context, "선택됨 : ", Toast.LENGTH_LONG).show();
                Log.d("Main", item.getContents());
            }
        });

        loadNoteListData();

        return view;
    }


    public int loadNoteListData(){
        String sql = "select _id, weather, address, location_x, " +
                "location_y, contents, mood, picture, create_date, " +
                "modify_date from " + NoteDatabase.TABLE_NOTE +
                " order by create_date desc";

        int recordCount = -1;
        NoteDatabase database = NoteDatabase.getInstance(context);
        if(database != null){
            Cursor outCursor = database.rawQuery(sql);

            recordCount = outCursor.getCount();
            ArrayList<Note> items = new ArrayList<Note>();
            while(outCursor.moveToNext()){
                int _id = outCursor.getInt(0);
                String weather = outCursor.getString(1);
                String address = outCursor.getString(2);
                String locationX = outCursor.getString(3);
                String locationY = outCursor.getString(4);
                String contents = outCursor.getString(5);
                String mood = outCursor.getString(6);
                String picture = outCursor.getString(7);
                String dateStr = outCursor.getString(8);
                String createDateStr = null;
                if(dateStr != null && dateStr.length() > 10){
                    try {
                        Date inDate = AppConstants.dateFormat4.parse(dateStr);
                        createDateStr = AppConstants.dateFormat3.format(inDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    createDateStr = "";
                }
                items.add(new Note(_id, weather, address, locationX, locationY,
                        contents, mood, picture, createDateStr));
            }

            outCursor.close();
            noteAdapter.setItems(items);
            noteAdapter.notifyDataSetChanged();
        }

        return recordCount;
    }

}