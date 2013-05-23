package com.goodbaby.babymall.activity;

import com.goodbaby.babymall.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class LoadActivity extends Activity {
    
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load);  
        new Handler().postDelayed(new Runnable(){  
            @Override  
            public void run() {
                // TODO download the advertisment icon
                
                if (true) {
                    Intent mainIntent = new Intent(LoadActivity.this, AdvertismentActivity.class);  
                    LoadActivity.this.startActivity(mainIntent);  
                    LoadActivity.this.finish();  
                }
            }  
        }, 2000);
    }

}
