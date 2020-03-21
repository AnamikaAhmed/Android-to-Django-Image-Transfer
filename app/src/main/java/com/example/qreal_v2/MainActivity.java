package com.example.qreal_v2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.qreal_v2.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    Button button;
    ImageView imageView;
    String path;
    File image;
 //   File imageFile1;
    private static final int RESULT_CANCELED = 0;
    private static final int REQUEST_PICTURE_CAPTURE = 1;

    // your authority, must be the same as in your manifest file
    private static final String CAPTURE_IMAGE_FILE_PROVIDER = "com.example.qreal_v2.fileprovider";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageView);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                captureIntent(); // returns the photofile

            }
        });
    }


    public void captureIntent(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, REQUEST_PICTURE_CAPTURE);

            File pictureFile = getPictureFile();

            if (pictureFile != null) {
                Log.e("image", "photofile is not null and is created blank");
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.qreal_v2.fileprovider",
                        pictureFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, REQUEST_PICTURE_CAPTURE);
            }
        }
    }

    public File getPictureFile(){
        //String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String pictureFile = "Post_Image";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        try {
            image = File.createTempFile(pictureFile, ".jpg", storageDir);

        }
        catch(Exception e){
            Log.e("EXCEPTION", "THIS IS AN EXCEPTION");
        }

        return image;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_PICTURE_CAPTURE && resultCode == RESULT_OK) {
            File imageFile1 = new  File(image.getAbsolutePath());
            if(imageFile1.exists()){
                //image.setImageURI(Uri.fromFile(imgFile));
                Toast.makeText(this, "Your image is created", Toast.LENGTH_SHORT).show();;
              //  image.setImageURI(Uri.fromFile(imgFile));
               Log.e("IMAGE EXISTS", "IMAGE HAS BEEN CREATED IN ON RESULT ACTIVITY WITH LENGTH "+imageFile1.length());
               uploadPicture(imageFile1);
            }
        }

    }

    public void uploadPicture(File imageFile){
        System.out.println("The imagefile is stored in "+imageFile);
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        clientBuilder.addInterceptor(loggingInterceptor);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DjangoApi.DJANGO_SITE)
                .client(clientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Log.d("Django post API", retrofit+"");


        DjangoApi postApi= retrofit.create(DjangoApi.class);

        Log.d("message", postApi+"");
        Log.d(imageFile.getName(),"IMAGE FILE NAME");
        Log.d(imageFile.canRead()+"","CAN READ IMAGEFILE?");
        Log.d(imageFile.length()+"","LENGTH OF IMAGEFILE");


        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);

        try {
            if (requestBody != null) {
                Log.d("debug", "REQUEST BODY LENGTH: " + requestBody.contentLength());
                Log.d("debug", "REQUEST BODY FILE TYPE: " + requestBody.contentType());
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        MultipartBody.Part multiPartBody = MultipartBody.Part
                .createFormData("model_pic", imageFile.getName(), requestBody);




        Call<RequestBody> call = postApi.uploadFile(multiPartBody);

        call.enqueue(new Callback<RequestBody>() {
            @Override
            public void onResponse(Call<RequestBody> call, Response<RequestBody> response) {
                Log.d("good", "good");

            }
            @Override
            public void onFailure(Call<RequestBody> call, Throwable t) {

                Log.d("fail", t.getMessage());
            }
        });


    }




}
