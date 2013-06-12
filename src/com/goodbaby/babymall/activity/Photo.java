package com.goodbaby.babymall.activity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.goodbaby.babymall.BabyMallApplication;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Gallery.LayoutParams;

public class Photo extends Activity
{
    private static final String TAG = "Photo";

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        int position = getIntent().getIntExtra("position", 0);
        
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(
                    BabyMallApplication.EXTERNAL_STORAGE_PATH + position + ".png");
            final Bitmap bitmap = BitmapFactory.decodeStream(fis);
            fis.close();
            if (bitmap != null) {
                Bitmap newB = CreateBitmap(bitmap);
                MyImageView i = new MyImageView(this, newB);
                i.setAdjustViewBounds(true);
                i.setLayoutParams(new Gallery.LayoutParams(LayoutParams.FILL_PARENT,
                        LayoutParams.FILL_PARENT));
                i.setBackgroundColor(Color.WHITE);
                setContentView(i);
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Read the image file failed: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IO failed: " + e.getMessage());
        }
    }
    
    private Bitmap CreateBitmap(Bitmap img)
    {
        Matrix matrix = new Matrix();
        int width = img.getWidth();
        int height = img.getHeight();
        int newwidth = 1000;
        int newheight = 1000;
        float scaleWidth = ((float) newwidth) / width;
        float scaleHeight = ((float) newheight) / height;
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newimg = Bitmap.createBitmap(img, 0, 0, width, height,
                matrix, true);
        return newimg;
    }
    
    public class MyImageView extends ImageView 
    {
        private GestureDetector mGestureDetector;
        private MyGuestureImp mMyGuestureImp;
        private Context mContext;
        private Bitmap mBitMap;
        
        private int mWidth;
        private int mHeight;
        
        public MyImageView(Context context, Bitmap bm)
        {
            super(context);
            mContext = context;
            this.mBitMap = bm;
            this.mWidth = bm.getWidth();
            this.mHeight = bm.getHeight();
            mMyGuestureImp = new MyGuestureImp();
            mGestureDetector = new GestureDetector(mContext, mMyGuestureImp);
            x = 0;
            y = 0;
            setLongClickable(true);
        }

        public boolean onTouchEvent(MotionEvent event)
        {
            return mGestureDetector.onTouchEvent(event);
        }

        public class MyGuestureImp extends
                GestureDetector.SimpleOnGestureListener
        {

            public boolean onDown(MotionEvent e)
            {
                return true;
            }

            public boolean onSingleTapConfirmed(MotionEvent e)
            {
                return true;
            }

            public boolean onScroll(MotionEvent e1, MotionEvent e2,
                    float distanceX, float distanceY)
            {
                x -= distanceX;
                y -= distanceY;
                if (x > 0)
                    x = 0;
                if (y > 0)
                    y = 0;
                invalidate();
                return true;
            }

            public boolean onDoubleTap(MotionEvent e)
            {
                ((Photo) mContext).finish();
                return true;
            }
        }

        public float x;
        public float y;

        protected void onDraw(Canvas canvas)
        {
            if (x < -(this.mWidth - 600)) {
                x = -(this.mWidth - 600);
            }
            if (y < -(this.mHeight - 800)) {
                y = -(this.mHeight - 800);
            }
            canvas.translate(x, y);
            canvas.drawBitmap(this.mBitMap, 0, 0, new Paint(Paint.ANTI_ALIAS_FLAG));
        }
    }

}

