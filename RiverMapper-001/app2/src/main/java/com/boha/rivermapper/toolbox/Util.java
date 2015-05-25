package com.boha.rivermapper.toolbox;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.boha.rivermapper.R;

/**
 * Created by aubreyM on 15/05/25.
 */
public class Util {
    public static void setCustomActionBar( Context ctx,
                                           ActionBar actionBar, String text, Drawable image) {
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflator = (LayoutInflater)
                ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.action_bar_logo, null);
        TextView txt = (TextView) v.findViewById(R.id.ACTION_BAR_text);
        ImageView logo = (ImageView) v.findViewById(R.id.ACTION_BAR_logo);
        txt.setText(text);
        //
        logo.setImageDrawable(image);
        actionBar.setCustomView(v);
        actionBar.setTitle("");
    }
    public static void flashOnce(View view, long duration,  final UtilAnimationListener listener) {
        ObjectAnimator an = ObjectAnimator.ofFloat(view, "alpha", 0, 1);
        an.setRepeatMode(ObjectAnimator.REVERSE);
        an.setDuration(duration);
        an.setInterpolator(new AccelerateDecelerateInterpolator());
        an.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (listener != null)
                    listener.onAnimationEnded();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        an.start();

    }
    public static void rotate(View view, long duration) {
        final ObjectAnimator an = ObjectAnimator.ofFloat(view, "rotation", 0.0f, 360f);
        //an.setRepeatCount(ObjectAnimator.REVERSE);
        an.setDuration(duration);
        an.setInterpolator(new AccelerateDecelerateInterpolator());
        an.start();
    }

    public interface UtilAnimationListener {
        public void onAnimationEnded();
    }
}
