package cn.ihealthbaby.weitaixin.ui.home;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * Created by chenweihua on 2015/10/15.
 */
public class AnimUntils {


    public static  void startAnim(final RelativeLayout rl2,final TextView view, String tvText, final RelativeLayout rl3,final TextView view3, final String tvText3){
        view.setText(tvText);
        AnimationSet animationSet = new AnimationSet(true);
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, -0.4f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f);
        animationSet.setFillEnabled(true);
        animationSet.setFillAfter(true);
        animationSet.setDuration(700);
        animationSet.addAnimation(translateAnimation);
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setText("");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                AnimationSet animationSet2 = new AnimationSet(true);
                TranslateAnimation translateAnimation2 = new TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, -0.4f,
                        Animation.RELATIVE_TO_SELF, -0.2f,
                        Animation.RELATIVE_TO_SELF, 0f,
                        Animation.RELATIVE_TO_SELF, 0f);
                animationSet2.setFillEnabled(true);
                animationSet2.setFillAfter(true);
                animationSet2.setDuration(600);
                animationSet2.addAnimation(translateAnimation2);
                translateAnimation2.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        AnimationSet animationSet3 = new AnimationSet(true);
                        TranslateAnimation translateAnimation3 = new TranslateAnimation(
                                Animation.RELATIVE_TO_SELF, -0.2f,
                                Animation.RELATIVE_TO_SELF, -0.3f,
                                Animation.RELATIVE_TO_SELF, 0f,
                                Animation.RELATIVE_TO_SELF, 0f);
                        animationSet3.setFillEnabled(true);
                        animationSet3.setFillAfter(true);
                        animationSet3.setDuration(600);
                        animationSet3.addAnimation(translateAnimation3);
                        animationSet3.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                startAnim3(rl3, view3, tvText3);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        rl2.startAnimation(animationSet3);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                rl2.startAnimation(animationSet2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        rl2.startAnimation(animationSet);
    }




    public static  void startAnim3(final RelativeLayout rl3, final TextView view, String tvText){
        view.setText(tvText);
        AnimationSet animationSet = new AnimationSet(true);
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, -0.4f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f);
        animationSet.setFillEnabled(true);
        animationSet.setFillAfter(true);
        animationSet.setDuration(700);
        animationSet.addAnimation(translateAnimation);
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setText("");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                AnimationSet animationSet2 = new AnimationSet(true);
                TranslateAnimation translateAnimation2 = new TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, -0.4f,
                        Animation.RELATIVE_TO_SELF, -0.2f,
                        Animation.RELATIVE_TO_SELF, 0f,
                        Animation.RELATIVE_TO_SELF, 0f);
                animationSet2.setFillEnabled(true);
                animationSet2.setFillAfter(true);
                animationSet2.setDuration(600);
                animationSet2.addAnimation(translateAnimation2);
                translateAnimation2.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        AnimationSet animationSet3 = new AnimationSet(true);
                        TranslateAnimation translateAnimation3 = new TranslateAnimation(
                                Animation.RELATIVE_TO_SELF, -0.2f,
                                Animation.RELATIVE_TO_SELF, -0.3f,
                                Animation.RELATIVE_TO_SELF, 0f,
                                Animation.RELATIVE_TO_SELF, 0f);
                        animationSet3.setFillEnabled(true);
                        animationSet3.setFillAfter(true);
                        animationSet3.setDuration(600);
                        animationSet3.addAnimation(translateAnimation3);
                        animationSet3.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        rl3.startAnimation(animationSet3);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                rl3.startAnimation(animationSet2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        rl3.startAnimation(animationSet);
    }


}
