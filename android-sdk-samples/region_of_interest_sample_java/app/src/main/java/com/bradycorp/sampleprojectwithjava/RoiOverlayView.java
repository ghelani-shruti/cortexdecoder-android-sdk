package com.bradycorp.sampleprojectwithjava;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import androidx.annotation.NonNull;

/**
 * Transparent overlay view that draws a centered ROI box
 * over the camera preview.
 */
public class RoiOverlayView extends View {

    private final Paint borderPaint;
    private final float cornerRadius;
    private RectF roiRectF;

    public RoiOverlayView(Context context) {
        super(context);

        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setColor(Color.parseColor("#014991"));
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(6f);

        cornerRadius = 16f * context.getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        computeRoiRect(w, h);
    }

    private void computeRoiRect(int w, int h) {
        float boxWidth  = w * 0.75f;
        float boxHeight = h * 0.30f;
        float left = (w - boxWidth)  / 2f;
        float top  = (h - boxHeight) / 2f;
        roiRectF = new RectF(left, top, left + boxWidth, top + boxHeight);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (roiRectF != null) {
            canvas.drawRoundRect(roiRectF, cornerRadius, cornerRadius, borderPaint);
        }
    }

    /**
     * Returns the ROI as an integer Rect (screen coordinates) for passing to
     * CDDecoder.shared.setRegionOfInterest(..., true, true).
     */
    public Rect getRoiRect() {
        if (roiRectF == null) return new Rect(0, 0, 0, 0);
        return new Rect(
                (int) roiRectF.left,
                (int) roiRectF.top,
                (int) roiRectF.right,
                (int) roiRectF.bottom
        );
    }
}








