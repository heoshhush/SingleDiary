package com.example.singlediary;

import android.view.View;

public interface OnNoteItemClickListener {
    public void onNoteItemClick(NoteAdapter.Holder holder, View view, int position);
}
