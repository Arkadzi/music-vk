package me.gumenniy.arkadiy.vkmusic.app.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import me.gumenniy.arkadiy.vkmusic.R;

/**
 * Created by Arkadiy on 04.04.2016.
 */
public class SquareImageView extends ImageView {
    private boolean maxWidth;

    public SquareImageView(Context context) {
        super(context);
        setDirection(context, null, 0);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDirection(context, attrs, 0);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setDirection(context, attrs, defStyleAttr);
    }

    private void setDirection(Context context, AttributeSet attrs, int defStyleAttr) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(
                    attrs,
                    R.styleable.SquareImageView,
                    defStyleAttr, 0);

            try {
                maxWidth = (a.getInteger(R.styleable.SquareImageView_direction, 0) == 0);
            } finally {
                a.recycle();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (maxWidth) {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        }
        else {
            super.onMeasure(heightMeasureSpec, heightMeasureSpec);
        }
    }
}
