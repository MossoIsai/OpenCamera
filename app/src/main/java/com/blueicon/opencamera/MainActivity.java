package com.blueicon.opencamera;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private Button buttonTomarFoto;
    private static final int REQUEST_OPEN_CAMERA = 0 ;
    private static final int REQUEST_OPEN_GALLERY = 1 ;
    private Bitmap bitmap;
    private Uri mURI;
    private String mPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }
    public void initViews(){
        buttonTomarFoto = (Button)findViewById(R.id.takePhoto);
        buttonTomarFoto.setOnClickListener(listener);
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            /*Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, 1);**/
            /*Intent pickPhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(pickPhoto.resolveActivity(getPackageManager())!= null){
                startActivityForResult(pickPhoto,0);
            }*/
            startDialog();

        }
    };


    private void startDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
        myAlertDialog.setTitle("Upload Pictures Option");
        myAlertDialog.setMessage("How do you want to set your picture?");

        myAlertDialog.setPositiveButton("Gallery",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent pictureActionIntent = null;

                        pictureActionIntent = new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(
                                pictureActionIntent,
                                REQUEST_OPEN_GALLERY);

                    }
                });

        myAlertDialog.setNegativeButton("Camera",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                        Intent intent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        File f = new File(android.os.Environment
                                .getExternalStorageDirectory(), "temp.jpg");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(f));

                        startActivityForResult(intent,
                                REQUEST_OPEN_GALLERY);

                    }
                });
        myAlertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("Estoy entrando al activity result");
        //Recuperando fotografia
        System.out.println("REQUEST CODE: "+requestCode);
        System.out.println("RESULT CODE: "+resultCode);
        if(requestCode == REQUEST_OPEN_GALLERY){
            if(data != null){
                System.out.println("Es direfenre de nullo");
            }else{
                System.out.println("Es nullo");
                Cursor cursor = getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]
                                {
                                        MediaStore.Images.Media.DATA,
                                        MediaStore.Images.Media.DATE_ADDED,
                                        MediaStore.Images.ImageColumns.ORIENTATION
                                },
                        MediaStore.Images.Media.DATE_ADDED,
                        null,
                        "date_added ASC"
                );
                if (cursor != null && cursor.moveToFirst())
                {
                    System.out.println("Cursor: "+cursor.getString(0));
                    do
                    {
                        mURI = Uri.parse(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                        System.out.println("DIRECCION URI: "+mURI);
                        mPhotoPath = mURI.toString();
                    }
                    while (cursor.moveToNext());
                    cursor.close();
                }

                // Resize full image to fit out in image view.
                ImageView mImageView = (ImageView) findViewById(R.id.mImageView);

                int width = 640;
                int height = 480;

                System.out.println("ANCHO : "+width);
                System.out.println("ALTO: "+height);

                BitmapFactory.Options factoryOptions = new BitmapFactory.Options();
                factoryOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(/*mURI.getPath()*/ mPhotoPath, factoryOptions);

                int imageWidth = factoryOptions.outWidth;
                int imageHeight = factoryOptions.outHeight;
                // Determine how much to scale down the image
                int scaleFactor = Math.min(
                        imageWidth/width,
                        imageHeight/height
                );

                // Decode the image file into a Bitmap sized to fill view
                factoryOptions.inJustDecodeBounds = false;
                factoryOptions.inSampleSize = scaleFactor;
                factoryOptions.inPurgeable = true;

                Bitmap bitmap = BitmapFactory.decodeFile(/*mURI.getPath()*/ mPhotoPath, factoryOptions);

                mImageView.setImageBitmap(bitmap);

            }
        }
    }
}
