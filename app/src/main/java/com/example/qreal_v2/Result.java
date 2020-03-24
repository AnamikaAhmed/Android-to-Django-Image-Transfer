package com.example.qreal_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

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

public class Result extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        // Add your code below!
        Intent intent = getIntent();
        File pictureFile = (File)intent.getExtras().get("picture");
        Log.e("Result page message", pictureFile.length()+"");
        Toast.makeText(this, pictureFile.length()+"", Toast.LENGTH_SHORT).show();
        uploadPicture(pictureFile);
        
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
