package com.example.android.utility;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.android.R;

public class ShimmerView extends FrameLayout {
    public static final String TAG = ShimmerView.class.getName();

    private Context mContext;
    private Animation mAnimation;
    private int mAnimDrawable;
    private boolean mAutoStart;
    private float mAnimScale;
    private boolean mIsAnimated;
    private ImageView mShimmer;

    public ShimmerView(Context context) {
        this(context, null);
    }

    public ShimmerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShimmerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShimmerView);
        if (typedArray.hasValue(R.styleable.ShimmerView_animDrawable)) {
            mAnimDrawable = typedArray.getResourceId(R.styleable.ShimmerView_animDrawable, 0);
        } else {
            throw new IllegalArgumentException("Attribute animDrawable is mandatory");
        }
        mAutoStart = typedArray.getBoolean(R.styleable.ShimmerView_autoStart, false);
        mAnimScale = typedArray.getFloat(R.styleable.ShimmerView_animScale, 1);
        typedArray.recycle();
    }

    @Override
    protected void onFinishInflate() {
        initShimmer();
        initAnimation();
        super.onFinishInflate();
    }

    private void initShimmer() {
        if (mShimmer == null) {
            mShimmer = new ImageView(mContext);
            mShimmer.setImageResource(mAnimDrawable);
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            addView(mShimmer, getChildCount(), params);
        }
    }

    private void initAnimation() {
        if (mAnimation == null) {
            mAnimation = AnimationUtils.loadAnimation(mContext, R.anim.shimmer_effect);
            long duration = mAnimation.getDuration();
            mAnimation.setDuration((long) (mAnimScale * duration));
            mAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    mShimmer.setImageAlpha(255);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mShimmer.setImageAlpha(0);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
    }

    @Override
    protected void onAttachedToWindow() {
        if (mAutoStart || mIsAnimated) startAnimation();
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        stopAnimation();
        super.onDetachedFromWindow();
    }

    private void startAnimation() {
        if (mShimmer == null || mIsAnimated) return;
        mIsAnimated = true;
        mShimmer.clearAnimation();
        mShimmer.startAnimation(mAnimation);
    }

    private void stopAnimation() {
        if (mShimmer == null || !mIsAnimated) return;
        mIsAnimated = false;
        mShimmer.clearAnimation();
    }

    public void setAnimated(boolean animated) {
        if (animated) startAnimation();
        else stopAnimation();
    }
}
