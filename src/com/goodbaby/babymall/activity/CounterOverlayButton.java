/**
 * StreamWIDE Visual Voicemail Client
 *
 * $Rev: 843 $
 * $LastChangedDate: 2011-03-01 18:01:08 +0800 (周二, 01 三月 2011) $
 * $LastChangedBy: mvinel $
 *
 * @category   Streamwide
 * @copyright  Copyright (c) 2010 StreamWIDE SA
 * @author     VVMClient team <VVMClient@streamwide.com>
 * @version    $Id: CounterOverlayButton.java 843 2011-03-01 10:01:08Z mvinel $
 *
 *  © Copyright 2010 StreamWIDE. StreamWIDE is the copyright holder 
 *  of all code contained in this file. Do not redistribute or
 *  re-use without permission.
 *
 */

package com.goodbaby.babymall.activity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

import com.goodbaby.babymall.R;


/**
 * The CounterOverlayButton class is an extension of android's ImageButton
 * class, able to overlay a counter centered in the bottom right corner of the
 * button. The value of the counter is set dynamically by the client through a 
 * public accesser.
 */
public class CounterOverlayButton extends ImageView {
    private static final int MAX_COUNT = 99;

    private int      mCount    = 0;
    private Drawable mDrawable = null;
    private Paint    mPaint    = null;
    private final Rect     mBounds   = new Rect();
    
    /**
     * Creates a new CounterOverlayButton
     * @param context application context or current activity
     */
    public CounterOverlayButton(final Context context) {
        super(context);
        setup(context);
    }

    /**
     * Creates a new CounterOverlayButton
     * @param context application context or current activity
     * @param attrs android widget attributes
     */
    public CounterOverlayButton(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        setup(context);
    }

    /**
     * Creates a new CounterOverlayButton
     * @param context application context or current activity
     * @param attrs android widget attributes
     * @param defStyle android style and theme
     */
    public CounterOverlayButton(final Context context, final AttributeSet attrs,
            final int defStyle) {
        super(context, attrs, defStyle);
        setup(context);
    }
    
    /**
     * do the real constructor work for CounterOverlayButton
     * @param context the application context or current activity
     */
    private void setup(final Context context) {
        this.mDrawable = context.getResources().getDrawable(
            R.drawable.badge).mutate();
        
        this.mDrawable.setCallback(this);
        
        this.mPaint = new Paint();
//        this.mPaint.setColor(context.getResources().getColor(
//            R.color.counter_indicator_text));
        this.mPaint.setDither(true);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setTextAlign(Align.CENTER);
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setStrokeJoin(Paint.Join.ROUND);
        this.mPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mPaint.setTextSize(TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, 
            12, 
            context.getResources().getDisplayMetrics()));
        this.mPaint.setFilterBitmap(true);
        this.mPaint.setSubpixelText(true);
    }
    
    /**
     * Sets the counter value to be displayed by the CounterOverlayButton
     * @param count to display
     */
    public void setCount(final int count) {
        this.mCount = Math.min(count, MAX_COUNT);
        invalidate();
    }
    
    /* (non-Javadoc)
     * @see android.widget.ImageView#onDraw(android.graphics.Canvas)
     */
    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        if (0 >= this.mCount) {
            return;
        }
        // Get Bounds information
        this.getDrawingRect(this.mBounds);
        this.mBounds.set(
                Math.round(this.mBounds.exactCenterX()),
                Math.round(this.mBounds.exactCenterY()),
                this.mBounds.centerX(), 
                (this.mBounds.centerY()) / 3);
        this.mBounds.inset(Math.round((this.mBounds.width()
            - this.mDrawable.getIntrinsicWidth())/2.0f), 
            Math.round((this.mBounds.height() 
                - this.mDrawable.getIntrinsicHeight())/2.0f));
        // Translate text
        final String text = Integer.toString(this.mCount);
        // Draw background
        this.mDrawable.setBounds(this.mBounds);
        this.mDrawable.draw(canvas);
        // Draw text
        canvas.drawText(text, 
            this.mBounds.exactCenterX(), 
            this.mBounds.exactCenterY() + this.mPaint.descent() + 2,
            this.mPaint);
    }

}
