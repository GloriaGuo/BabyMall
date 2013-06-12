package com.goodbaby.babymall.activity;
    
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.goodbaby.babymall.BabyMallApplication;
import com.goodbaby.babymall.R;

public class AdvertisementActivity extends Activity {
    
    private static final String TAG = "AdvertisementActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advertisment);
        
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(
                    BabyMallApplication.EXTERNAL_STORAGE_PATH + BabyMallApplication.ADVERTISEMENT_IMAGE);
            final Bitmap bitmap = BitmapFactory.decodeStream(fis);
            fis.close();
            
            ImageView mAdvertismentImage = (ImageView) findViewById(R.id.image_advertisment);
            mAdvertismentImage.setImageBitmap(bitmap);
            
            new Handler().postDelayed(new Runnable(){  
                @Override  
                public void run() {
                    Intent mainIntent = new Intent(AdvertisementActivity.this, NavigationActivity.class);  
                    AdvertisementActivity.this.startActivity(mainIntent);
                    if (null != bitmap) {
                        bitmap.recycle();
                    }
                    BabyMallApplication.revomeFile(BabyMallApplication.ADVERTISEMENT_IMAGE);
                    AdvertisementActivity.this.finish();  
                }  
            }, 2000);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Read the advertisement file failed: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "Read the advertisement file failed: " + e.getMessage());
        }
        
        
    }

}
