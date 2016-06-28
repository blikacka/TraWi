/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tracker;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

/**
 *
 * @author Notasek
 */
public class Images {

    Context ctx;
    Resources res;

    public Images(Context ctx, Resources res) {
        this.ctx = ctx;
        this.res = res;
    }

    public Bitmap scaleB(String item, int sizeX, int sizeY) {
        Matrix matrix = new Matrix();
        matrix.postRotate(0);
        int ide = ctx.getResources().getIdentifier(item, "drawable", "com.tracker");
        Bitmap imgB = BitmapFactory.decodeResource(res, ide);
        Bitmap img = Bitmap.createScaledBitmap(imgB, (int) sizeX, (int) sizeY, true);
        Bitmap rotatedBitmap = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        return rotatedBitmap;
    }

    public Bitmap recolorBitmap(Bitmap img, int color) {
        Bitmap bitmap = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setColorFilter(new LightingColorFilter(color, 0));
//        paint.setColorFilter(new ColorMatrixColorFilter(getColorMatrix()));
        canvas.drawBitmap(img, 0, 0, paint);
        return bitmap;
    }

    public Bitmap setMask(Bitmap img, PorterDuff.Mode mode) {
        Bitmap bitmap = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint maskPaint = new Paint();
        maskPaint.setXfermode(new PorterDuffXfermode(mode));
        canvas.drawBitmap(img, 0, 0, maskPaint);
        return bitmap;
    }

    private ColorMatrix getColorMatrix() {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        return colorMatrix;
    }

}
