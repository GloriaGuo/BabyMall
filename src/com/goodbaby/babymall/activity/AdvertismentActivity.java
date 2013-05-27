package com.goodbaby.babymall.activity;
    
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.goodbaby.babymall.BabyMallApplication;
import com.goodbaby.babymall.R;

public class AdvertismentActivity extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advertisment);
        
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(
                    BabyMallApplication.EXTERNAL_STORAGE_PATH + BabyMallApplication.ADVERTISEMENT_IMAGE);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(fis);
        
        ImageView mAdvertismentImage = (ImageView) findViewById(R.id.image_advertisment);
        mAdvertismentImage.setImageBitmap(bitmap);
        
        new Handler().postDelayed(new Runnable(){  
            @Override  
            public void run() {
                Intent mainIntent = new Intent(AdvertismentActivity.this, NavigationActivity.class);  
                AdvertismentActivity.this.startActivity(mainIntent);  
                AdvertismentActivity.this.finish();  
            }  
        }, 2000);
    }

}
