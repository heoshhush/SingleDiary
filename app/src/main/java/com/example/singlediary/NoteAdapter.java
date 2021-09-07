package com.example.singlediary;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.Holder> {
    ArrayList<Note> itemList = new ArrayList<Note>();

    OnNoteItemClickListener onNoteItemClickListener = null;

    int layoutType = 0;

    class Holder extends RecyclerView.ViewHolder implements OnNoteItemClickListener{

        LinearLayout layout1;
        LinearLayout layout2;
        ImageView card1_moodImage;
        ImageView card2_realImage;
        ImageView card1_weatherImage;
        ImageView card2_weatherImage;
        ImageView card1_isImage;
        ImageView card2_isImage;
        TextView card1_title;
        TextView card2_title;
        TextView card1_location;
        TextView card2_location;
        TextView card1_date;
        TextView card2_date;

        public Holder(@NonNull View itemView) {

            super(itemView);
            layout1 = itemView.findViewById(R.id.cardLinearLayout1);
            layout2 = itemView.findViewById(R.id.cardLinearLayout2);
            card1_moodImage = itemView.findViewById(R.id.card_iv);
            card2_realImage = itemView.findViewById(R.id.card2_iv);
            card1_weatherImage = itemView.findViewById(R.id.card_weatherImage);
            card2_weatherImage = itemView.findViewById(R.id.card2_weatherImage);
            card1_isImage = itemView.findViewById(R.id.card_isImage);
            card2_isImage = itemView.findViewById(R.id.card2_isImage);
            card1_title = itemView.findViewById(R.id.card_tv_title);
            card2_title = itemView.findViewById(R.id.card2_tv_title);
            card1_location = itemView.findViewById(R.id.card_tv_location);
            card2_location = itemView.findViewById(R.id.card2_tv2_location);
            card1_date = itemView.findViewById(R.id.card_tv_date);
            card2_date = itemView.findViewById(R.id.card2_tv2_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int pos = getAdapterPosition();
                    Log.d("Main", "hi");
                    if(onNoteItemClickListener != null){
                        onNoteItemClickListener.onNoteItemClick(Holder.this, v, pos);
                        Log.d("Main", "hi2");
                    }
                }
            });
            setCardLayout(layoutType);

        }

        @Override
        public void onNoteItemClick(Holder holder, View view, int position) {
            if(onNoteItemClickListener != null){
                onNoteItemClickListener.onNoteItemClick(holder, view, position);
            }
        }

        public void setCardLayout(int position){
            if(position == 0){
                layout1.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.GONE);
            } else if(position == 1){
                layout2.setVisibility(View.VISIBLE);
                layout1.setVisibility(View.GONE);
            }
        }

        public void setItem(Note item){
            String mood = item.getMood();
            int moodIndex = Integer.parseInt(mood);
            setMood(moodIndex);

            String pictures = item.getPictures();
            if(pictures != null && pictures.equals("")){
                card2_realImage.setImageURI(Uri.parse("file://" + pictures));
                card1_isImage.setVisibility(View.VISIBLE);
            } else {
                card2_realImage.setImageResource(R.drawable.ic_launcher_foreground);
                card2_isImage.setVisibility(View.GONE);
            }

            String weather = item.getWeather();
            int weatherIndex = Integer.parseInt(weather);
            setWeather(weatherIndex);

            card1_title.setText(item.getContents());
            card2_title.setText(item.getContents());
            card1_date.setText(item.getCreateDateStr());
            card2_date.setText(item.getCreateDateStr());
            card1_location.setText(item.getAddress());
            card2_location.setText(item.getAddress());


        }

        public void setMood(int moodIndex){
            switch(moodIndex){
                case 0 :
                    card1_moodImage.setImageResource(R.drawable.smile1_48);
                    break;
                case 1:
                    card1_moodImage.setImageResource(R.drawable.smile2_48);
                    break;
                case 2:
                    card1_moodImage.setImageResource(R.drawable.smile3_48);
                    break;
                case 3:
                    card1_moodImage.setImageResource(R.drawable.smile4_48);
                    break;
                case 4:
                    card1_moodImage.setImageResource(R.drawable.smile5_48);
                    break;
                default:
                    card1_moodImage.setImageResource(R.drawable.smile3_48);
                    break;
            }
        }

        public void setWeather(int weatherIndex){
            switch (weatherIndex){
                case 0 :
                    card1_weatherImage.setImageResource(R.drawable.weather_icon_1);
                    card2_weatherImage.setImageResource(R.drawable.weather_icon_1);
                    break;
                case 1:
                    card1_weatherImage.setImageResource(R.drawable.weather_icon_2);
                    card2_weatherImage.setImageResource(R.drawable.weather_icon_2);
                    break;
                case 2:
                    card1_weatherImage.setImageResource(R.drawable.weather_icon_3);
                    card2_weatherImage.setImageResource(R.drawable.weather_icon_3);
                    break;
                case 3:
                    card1_weatherImage.setImageResource(R.drawable.weather_icon_4);
                    card2_weatherImage.setImageResource(R.drawable.weather_icon_4);
                    break;
                case 4:
                    card1_weatherImage.setImageResource(R.drawable.weather_icon_5);
                    card2_weatherImage.setImageResource(R.drawable.weather_icon_5);
                    break;
                case 5:
                    card1_weatherImage.setImageResource(R.drawable.weather_icon_6);
                    card2_weatherImage.setImageResource(R.drawable.weather_icon_6);
                    break;
                case 6:
                    card1_weatherImage.setImageResource(R.drawable.weather_icon_7);
                    card2_weatherImage.setImageResource(R.drawable.weather_icon_7);
                    break;
                default:
                    card1_weatherImage.setImageResource(R.drawable.weather_icon_1);
                    card2_weatherImage.setImageResource(R.drawable.weather_icon_1);
            }
        }
    }

    public void addItem(Note item){
        itemList.add(item);
    }


    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.frag1_recycler_item, parent, false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Note item = itemList.get(position);
        holder.setItem(item);
        holder.setCardLayout(layoutType);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void switchLayoutType(int position){
        layoutType = position;
    }

    public void setOnItemClickListener(OnNoteItemClickListener onNoteItemClickListener){
        this.onNoteItemClickListener = onNoteItemClickListener;
    }

    public Note getItem(int position){
        return itemList.get(position);
    }

    public void setItems(ArrayList<Note> items){
        itemList = items;
    }


}
