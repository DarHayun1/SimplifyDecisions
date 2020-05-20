package dar.life.helpers.simplifydecisions.ui.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import dar.life.helpers.simplifydecisions.R;

public class TransparentSpotlight extends androidx.appcompat.widget.AppCompatImageView {
    private final Paint mPaint;
    private final PorterDuffXfermode mPorterDuffMode;
    private RectF circleRect;
    private int radius;

    public TransparentSpotlight(Context context, AttributeSet attrs) {
        super(context, attrs);
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPorterDuffMode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    }

    public void setCircle(RectF rect, int radius) {
        this.circleRect = rect;
        this.radius = radius;
        //Redraw after defining circle
        postInvalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(circleRect != null) {
            mPaint.setColor(getResources().getColor(android.R.color.black));
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawPaint(mPaint);

            mPaint.setXfermode(mPorterDuffMode);
            canvas.drawRoundRect(circleRect, radius, radius, mPaint);
        }
    }
}
