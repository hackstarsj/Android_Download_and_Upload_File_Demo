# Android_Download_and_Upload_File_Demo
Android Download and Upload File Demo

Added  New Feature

1. Upload File From File Manager<br>
2. Upload File From Gallery<br>
3. Upload File from Camera


<h2>Added Fixed for File Uploading :</h2>
1. Added android:requestLegacyExternalStorage="true" in AndroidManifest.xml in application Tag<br>
2. Added Projection in getRealPathFromUri() Method String[] proj = { MediaStore.Images.Media.DATA }; in MainActivity.java
