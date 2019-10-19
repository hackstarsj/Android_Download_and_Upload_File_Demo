package com.example.androiddownloadmanagerdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AdvanceFileUpload extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int ALL_FILE_REQUEST = 102;
    private static final int CAMERA_REQUEST = 103;
    private static final int GALLERY_REQUEST = 104;
    Button select_all_file;
    Button gallery_file;
    Button camera_file;

    ImageView camera_preview;
    ImageView gallery_preview;

    TextView all_file_name;
    TextView gallery_file_name;
    TextView camera_file_name;

    Button submit;
    ProgressBar progressBar;
    int method = 0;
    String gallery_file_path, all_file_path, camer_file_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advance_file_upload);


        select_all_file = findViewById(R.id.select_from_all_files);
        gallery_file = findViewById(R.id.select_from_gallery);
        camera_file = findViewById(R.id.select_from_camera);


        camera_preview = findViewById(R.id.camera_preview);
        gallery_preview = findViewById(R.id.gallery_preview);

        all_file_name = findViewById(R.id.all_file_name);
        gallery_file_name = findViewById(R.id.gallery_file_name);
        camera_file_name = findViewById(R.id.camera_file_name);

        submit = findViewById(R.id.upload);
        progressBar = findViewById(R.id.progressbar);


        select_all_file.setOnClickListener(AdvanceFileUpload.this);
        gallery_file.setOnClickListener(AdvanceFileUpload.this);
        camera_file.setOnClickListener(AdvanceFileUpload.this);
        submit.setOnClickListener(AdvanceFileUpload.this);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gallery_file_path == null) {
                    Toast.makeText(AdvanceFileUpload.this, "Gallery File Empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (all_file_path == null) {
                    Toast.makeText(AdvanceFileUpload.this, "ALl File File Empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (camer_file_path == null) {
                    Toast.makeText(AdvanceFileUpload.this, "CAmera File Empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                UploadTask uploadTask = new UploadTask();
                uploadTask.execute(new String[]{gallery_file_path, camer_file_path, all_file_path});
            }
        });

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.select_from_all_files) {
            method = 0;
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    filePicker(0);
                } else {
                    requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            } else {
                filePicker(0);
            }

        } else if (v.getId() == R.id.select_from_camera) {
            method = 1;
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkPermission(Manifest.permission.CAMERA)) {
                    filePicker(1);
                } else {
                    requestPermission(Manifest.permission.CAMERA);
                }
            } else {
                filePicker(1);
            }

        } else if (v.getId() == R.id.select_from_gallery) {
            method = 2;
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    filePicker(2);
                } else {
                    requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            } else {
                filePicker(2);
            }
        }

    }

    private void filePicker(int i) {
        if (i == 0) {
            Intent intent = new Intent();
            intent.setType("*/*");
            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(Intent.createChooser(intent, "Choose File to Upload"), ALL_FILE_REQUEST);
        }

        if (i == 1) {
            Intent intent = new Intent();
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_REQUEST);
        }
        if (i == 2) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(intent, GALLERY_REQUEST);
        }

    }

    private boolean checkPermission(String permission) {
        int result = ContextCompat.checkSelfPermission(AdvanceFileUpload.this, permission);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission(String permission) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(AdvanceFileUpload.this, permission)) {
            Toast.makeText(AdvanceFileUpload.this, "Please Allow Permission", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(AdvanceFileUpload.this, new String[]{permission}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(AdvanceFileUpload.this, "Permission Successfull", Toast.LENGTH_SHORT).show();
                    filePicker(method);
                } else {
                    Toast.makeText(AdvanceFileUpload.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY_REQUEST) {
                if (data == null) {
                    return;
                }

                Uri uri = data.getData();
                String selectedPath = FilePath.getFilePath(AdvanceFileUpload.this, uri);
                Log.d("File Path ", " " + selectedPath);
                if (selectedPath != null) {
                    gallery_file_name.setText("" + new File(selectedPath).getName());
                }
                Bitmap bitmap = BitmapFactory.decodeFile(selectedPath);
                gallery_preview.setImageBitmap(bitmap);
                gallery_file_path = selectedPath;
            }
            if (requestCode == CAMERA_REQUEST) {
                if (data == null) {
                    return;
                }

                //in camera request i will save my file to temp location

                Bitmap thumb = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumb.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                File destination = new File(Environment.getExternalStorageDirectory(), "temp.jpg");

                if (destination.exists()) {
                    destination.delete();
                }

                FileOutputStream out;

                try {
                    out = new FileOutputStream(destination);
                    out.write(bytes.toByteArray());
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("File Path ", " " + destination.getPath());
                if (destination != null) {
                    camera_file_name.setText("" + destination.getName());
                }
                camera_preview.setImageBitmap(thumb);
                camer_file_path = destination.getPath();

            }

            if (requestCode == ALL_FILE_REQUEST) {
                if (data == null) {
                    return;
                }

                Uri uri = data.getData();
                String paths = FilePath.getFilePath(AdvanceFileUpload.this, uri);
                Log.d("File Path : ", "" + paths);
                if (paths != null) {
                    all_file_name.setText("" + new File(paths).getName());
                }
                all_file_path = paths;
            }
        }
    }

//    Now Lets Upload it First Create a Server Code for hadnling file

    //Now uploading it

    public class UploadTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s!=null){
                Toast.makeText(AdvanceFileUpload.this, "File Uploaded", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(AdvanceFileUpload.this, "File Upload Failed", Toast.LENGTH_SHORT).show();
            }
            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... strings) {

            File file1 = new File(strings[0]);
            File file2 = new File(strings[1]);
            File file3 = new File(strings[2]);

            try {
                RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("files1", file1.getName(), RequestBody.create(MediaType.parse("*/*"), file1))
                        .addFormDataPart("files2", file2.getName(), RequestBody.create(MediaType.parse("*/*"), file2))
                        .addFormDataPart("files3", file3.getName(), RequestBody.create(MediaType.parse("*/*"), file3))
                        .addFormDataPart("some_key", "some_value")
                        .addFormDataPart("submit", "submit")
                        .build();
                Request request = new Request.Builder()
                        .url("http://192.168.0.2/project/upload2.php")
                        .post(requestBody)
                        .build();

                OkHttpClient okHttpClient = new OkHttpClient();
                //now progressbar not showing properly let's fixed it
                Response response = okHttpClient.newCall(request).execute();
                if (response != null && response.isSuccessful()) {
                    return response.body().string();
                } else {
                    return null;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
