package com.example.androiddownloadmanagerdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private long downloadId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //catching download complete events from android download manager which broadcast message
        registerReceiver(onDownloadComplete,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        Button download=findViewById(R.id.download);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginDownload("http://www.tutorialspoint.com/java/java_tutorial.pdf");
            }
        });
    }

    private void beginDownload(String file_link){
        File file=new File(getExternalFilesDir(null),"Dummy");

        //checking if android version is equal and greater than noughat

        //now if download complete file not visible now lets show it
        DownloadManager.Request request=null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
           request=new DownloadManager.Request(Uri.parse(file_link))
                    .setTitle("Dummy")
                    .setDescription("Downloading")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationUri(Uri.fromFile(file))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true);
        }
        else{
             request=new DownloadManager.Request(Uri.parse(file_link))
                    .setTitle("Dummy")
                    .setDescription("Downloading")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationUri(Uri.fromFile(file))
                    .setAllowedOverRoaming(true);
        }

        DownloadManager downloadManager=(DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        downloadId=downloadManager.enqueue(request);

    }

    //now checking if download complete

    private BroadcastReceiver onDownloadComplete=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id=intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1);
            if(downloadId==id){
                Toast.makeText(MainActivity.this, "Download Completed", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onDownloadComplete);
    }
}
