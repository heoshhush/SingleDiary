package com.example.singlediary;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;


public class Fragment1 extends Fragment {

    RecyclerView recyclerView;
    RadioGroup radioGroup;
    Context context;

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


        NoteAdapter noteAdapter = new NoteAdapter();
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

        return view;
    }


}