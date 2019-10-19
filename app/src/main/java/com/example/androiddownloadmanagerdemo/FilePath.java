package com.example.androiddownloadmanagerdemo;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

//This Class We Used to Get File Path from Device Each Android Version had Different type of Methods for accessing files. this is just a helper class for accessing file path
public class FilePath {

    public static String getFilePath(Context context, Uri uri){
        boolean isKitKat= Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT;

        if(isKitKat && DocumentsContract.isDocumentUri(context,uri)){
            if (isExternalStorageDocument(uri)) {

                String docId=DocumentsContract.getDocumentId(uri);
                String[] split=docId.split(":");
                String type=split[0];

                if("primary".equalsIgnoreCase(type)){
                    return Environment.getExternalStorageDirectory()+"/"+split[1];
                }
             }
            else if(isDownloadsDocument(uri)){
                String id=DocumentsContract.getDocumentId(uri);
                Uri contenturi= ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(id));

                return getDataColumn(context,contenturi,null,null);
            }
            else if(isMediaDocument(uri)){
                String docId=DocumentsContract.getDocumentId(uri);
                String[] split=docId.split(":");
                String type=split[0];
                Uri contenturi=null;
                if("image".equals(type)){
                    contenturi= MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                }
                else if("video".equals(type)){
                    contenturi= MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                }
                else if("audio".equals(type)){
                    contenturi= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                String selection="_id=?";
                String[] selectionarg=new String[]{split[1]};

                return getDataColumn(context,contenturi,selection,selectionarg);
            }

        }
        else if("content".equalsIgnoreCase(uri.getScheme())){
            if(isGooglePhotosUri(uri))
            {
                return uri.getLastPathSegment();
            }
            return getDataColumn(context,uri,null,null);
        }
        else if("file".equalsIgnoreCase(uri.getScheme())){
            return uri.getPath();
        }
        return null;
    }


    //Lets create some methods before accessing path

    public static String getDataColumn(Context context,Uri uri,String selection,String[] selectionargs){
        Cursor cursor=null;
        final String column="_data";
        final String[] projection={column};
        try {
            cursor=context.getContentResolver().query(uri,projection,selection,selectionargs,null);
            if(cursor!=null && cursor.moveToFirst()){
                int index=cursor.getColumnIndexOrThrow(column);
                return  cursor.getString(index);
            }

        }
        finally {
            if(cursor!=null){
                cursor.close();
            }
        }

        return null;

    }

    public static boolean isExternalStorageDocument(Uri uri){
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri){
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri){
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri){
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
