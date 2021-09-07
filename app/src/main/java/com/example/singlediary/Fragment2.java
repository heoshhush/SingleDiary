package com.example.singlediary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.FileOutputStream;
import java.util.Date;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;


public class Fragment2 extends Fragment {
    private static final String TAG = "Fragment2";

    Context context;
    OnTabItemSelectedListener onTabItemSelectedListener;
    OnRequestListener onRequestListener;
    TextView frag2_date;
    TextView frag2_address;
    ImageView weatherIcon;
    SeekBar seekBar;

    EditText contentsInput;
    ImageView pictureImageView;

    boolean isPhotoCaptured;
    boolean isPhotoFileSaved;
    boolean isPhotoCanceled;

    int selectedPhotoMenu;

    ActivityResultLauncher<Intent> photoCaptureLauncher;
    ActivityResultLauncher<Intent> photoSelectionLauncher;

    Uri uri;
    File file;
    Bitmap resultPhotoBitmap;

    int mMode = AppConstants.MODE_INSERT;
    int _id = -1;
    int weatherIndex = 0;
    int moodIndex = 2;
    Note item;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.context = context;
        if(context instanceof OnTabItemSelectedListener){
            onTabItemSelectedListener = (OnTabItemSelectedListener) context;
        }

        if(context instanceof OnRequestListener){
            onRequestListener = (OnRequestListener) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_2, container, false);
        initUI(rootView);

        onRequestListener.onRequest("getCurrentLocation");
        return rootView;
    }

    public void initUI(ViewGroup rootView){
        Button saveBtn = rootView.findViewById(R.id.frag2_btn1);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMode == AppConstants.MODE_INSERT){
                    saveNote();
                } else if(mMode == AppConstants.MODE_MODIFY){
                    modifyNote();
                }

                if(onTabItemSelectedListener != null){
                    onTabItemSelectedListener.onTabSelected(0);
                }
            }
        });

        Button deleteBtn = rootView.findViewById(R.id.frag2_btn2);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNote();

                onTabItemSelectedListener.onTabSelected(0);
            }
        });

        Button cancelBtn = rootView.findViewById(R.id.frag2_btn3);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTabItemSelectedListener.onTabSelected(0);
            }
        });

        SeekBar seekBar = rootView.findViewById(R.id.frag2_seekBar);
        seekBar.setProgress(2);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d("Mood", Integer.toString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        frag2_date = rootView.findViewById(R.id.frag2_date);
        frag2_address = rootView.findViewById(R.id.frag2_address);
        weatherIcon = rootView.findViewById(R.id.frag2_weatherIcon);
        pictureImageView = rootView.findViewById(R.id.frag2_iv);
        contentsInput = rootView.findViewById(R.id.frag2_et);

        pictureImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPhotoCaptured || isPhotoFileSaved){
                    showDialog(AppConstants.CONTENT_PHOTO_EX);
                } else {
                    showDialog(AppConstants.CONTENT_PHOTO);
                }
            }
        });

        photoCaptureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Log.d(TAG, "onActivityResult() for PHOTO_CAPTURE");
                Log.d(TAG, "resultCode :" + result.getResultCode());

                resultPhotoBitmap = decodeSampleBitmapFromResource(file, pictureImageView.getWidth(), pictureImageView.getHeight());
                pictureImageView.setImageBitmap(resultPhotoBitmap);

            }
        });

        photoSelectionLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Log.d(TAG, "onActivityResult() for PHOTO_SELECTION");

                        Uri selectedImage;
                        if(result.getData() != null){
                            selectedImage = result.getData().getData();

                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String filePath = cursor.getString(columnIndex);
                        cursor.close();

                        resultPhotoBitmap = decodeSampleBitmapFromResource(new File(filePath),

                                pictureImageView.getWidth(), pictureImageView.getHeight());
                        pictureImageView.setImageBitmap(resultPhotoBitmap);
                        isPhotoCaptured = true;
                        }
                    }
                });


    }

    public static Bitmap decodeSampleBitmapFromResource(File res, int reqWidth, int reqHeight){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(res.getAbsolutePath(), options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(res.getAbsolutePath(), options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight){
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if(height > reqHeight || width > reqWidth){
            final int halfHeight = height;
            final int halfWidth = width;

            if((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth){
                inSampleSize = 2;
            }
        }

        return inSampleSize;

    }

    private String createFilename(){
        Date curDate = new Date();
        String curDateStr = String.valueOf(curDate.getTime());

        return curDateStr;
    }

    public void setWeather(String data) {
        if (data != null) {
            if (data.equals("맑음")) {
                weatherIcon.setImageResource(R.drawable.weather_icon_1);
            } else if (data.equals("구름 조금")) {
                weatherIcon.setImageResource(R.drawable.weather_icon_2);
            } else if (data.equals("구름 많음")) {
                weatherIcon.setImageResource(R.drawable.weather_icon_3);
            } else if (data.equals("흐림")) {
                weatherIcon.setImageResource(R.drawable.weather_icon_4);
            } else if (data.equals("비")) {
                weatherIcon.setImageResource(R.drawable.weather_icon_5);
            } else if (data.equals("눈/비")) {
                weatherIcon.setImageResource(R.drawable.weather_icon_6);
            } else if (data.equals("눈")) {
                weatherIcon.setImageResource(R.drawable.weather_icon_7);
            } else {
                Log.d("Fragment2", "Unknown weather string : " + data);
            }
        }
    }

    public void showDialog(int id){
        AlertDialog.Builder builder = null;
        switch(id){
            case AppConstants.CONTENT_PHOTO:
                builder = new AlertDialog.Builder(context);

                builder.setTitle("사진 메뉴 선택");
                builder.setSingleChoiceItems(R.array.array_photo, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedPhotoMenu = which;
                    }
                });
                builder.setPositiveButton("선택", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(selectedPhotoMenu == 0){
                            showPhotoCaptureActivity();
                        } else if(selectedPhotoMenu == 1){
                            showPhotoSelectionActivity();
                        }
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                break;

            case AppConstants.CONTENT_PHOTO_EX:
                builder = new AlertDialog.Builder(context);

                builder.setTitle("사진 메뉴 선택");
                builder.setSingleChoiceItems(R.array.array_photo_ex, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedPhotoMenu = which;
                    }
                });
                builder.setPositiveButton("선택", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(selectedPhotoMenu == 0){
                            showPhotoCaptureActivity();
                        } else if(selectedPhotoMenu == 1){
                            showPhotoSelectionActivity();
                        } else if(selectedPhotoMenu == 2){
                            isPhotoCanceled = true;
                            isPhotoCaptured = false;

                            pictureImageView.setImageResource(R.drawable.picture1);
                        }
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                break;

            default:
                break;
        }

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showPhotoCaptureActivity(){
        if(file == null){
            file = createFile();
        }

        Uri fileUri = FileProvider.getUriForFile(context, "com.example.singlediary.fileprovider", file);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        if(intent.resolveActivity(context.getPackageManager()) != null){
            photoCaptureLauncher.launch(intent);
        }
    }

    private File createFile(){
        String filename = "capture.jpg";
        File storageDir = Environment.getExternalStorageDirectory();
        File outFile = new File(storageDir, filename);

        return outFile;
    }

    public void showPhotoSelectionActivity(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        photoSelectionLauncher.launch(intent);
    }

    public void setDateString(String dateString){
        frag2_date.setText(dateString);
    }


    public void setAddress(String data){
        frag2_address.setText(data);
    }

    private void saveNote(){
        String address = frag2_address.getText().toString();
        String contents = contentsInput.getText().toString();

        String picturePath = savePicture();

        String sql = "insert into " + NoteDatabase.TABLE_NOTE +
                "(weather, address, location_x, location_y, contents, mood, picture) values(" +
                "'" + weatherIndex + "', " +
                "'" + address + "', " +
                "'" + "" + "', " +
                "'" + "" + "', " +
                "'" + contents + "', " +
                "'" + moodIndex + "', " +
                "'" + picturePath + "')";

        NoteDatabase database = NoteDatabase.getInstance(context);
        database.execSQL(sql);
    }


    private void modifyNote() {
        if (item != null) {
            String address = frag2_address.getText().toString();
            String contents = contentsInput.getText().toString();

            String picturePath = savePicture();

            // update note
            String sql = "update " + NoteDatabase.TABLE_NOTE +
                    " set " +
                    "   WEATHER = '" + weatherIndex + "'" +
                    "   ,ADDRESS = '" + address + "'" +
                    "   ,LOCATION_X = '" + "" + "'" +
                    "   ,LOCATION_Y = '" + "" + "'" +
                    "   ,CONTENTS = '" + contents + "'" +
                    "   ,MOOD = '" + moodIndex + "'" +
                    "   ,PICTURE = '" + picturePath + "'" +
                    " where " +
                    "   _id = " + item.id;

            Log.d(TAG, "sql : " + sql);
            NoteDatabase database = NoteDatabase.getInstance(context);
            database.execSQL(sql);
        }
    }


    private void deleteNote() {
        AppConstants.println("deleteNote called.");

        if (item != null) {
            // delete note
            String sql = "delete from " + NoteDatabase.TABLE_NOTE +
                    " where " +
                    "   _id = " + item.id;

            Log.d(TAG, "sql : " + sql);
            NoteDatabase database = NoteDatabase.getInstance(context);
            database.execSQL(sql);
        }
    }




    private String savePicture() {
        if (resultPhotoBitmap == null) {
            AppConstants.println("No picture to be saved.");
            return "";
        }

        File photoFolder = new File(AppConstants.FOLDER_PHOTO);

        if(!photoFolder.isDirectory()) {
            Log.d(TAG, "creating photo folder : " + photoFolder);
            photoFolder.mkdirs();
        }

        String photoFilename = createFilename();
        String picturePath = photoFolder + File.separator + photoFilename;

        try {
            FileOutputStream outstream = new FileOutputStream(picturePath);
            resultPhotoBitmap.compress(Bitmap.CompressFormat.PNG, 100, outstream);
            outstream.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return picturePath;
    }

}