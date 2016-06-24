package de.dreier.mytargets.utils;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import de.dreier.mytargets.R;
import de.dreier.mytargets.databinding.LayoutFabDescriptionBinding;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

//TODO Make this a view or better: Use a library
public class FABMenu {

    private final LayoutFabDescriptionBinding binding;
    private final View overlayView;
    private boolean isFabOpen = false;
    private final Animation fabOpen;
    private final Animation fabClose;
    private final Animation rotateForward;
    private final Animation rotateBackward;
    private final Animation fabShowAnimation;
    private final Animation fabHideAnimation;
    private Listener listener;

    public interface Listener {
        boolean isFABExpandable();

        void onFabClicked(int index);
    }

    public FABMenu(Context context, LayoutFabDescriptionBinding binding, View overlay) {
        this.binding = binding;
        this.overlayView = overlay;
        fabOpen = AnimationUtils.loadAnimation(context, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(context, R.anim.fab_close);
        rotateForward = AnimationUtils.loadAnimation(context, R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(context, R.anim.rotate_backward);
        fabShowAnimation = AnimationUtils.loadAnimation(context, R.anim.fab_label_show);
        fabHideAnimation = AnimationUtils.loadAnimation(context, R.anim.fab_label_hide);
        binding.fab.setOnClickListener(this::onClick);
        binding.fab1.setOnClickListener(this::onClick);
        binding.fab2.setOnClickListener(this::onClick);
        binding.fab1Label.setOnClickListener(this::onClick);
        binding.fab2Label.setOnClickListener(this::onClick);
        overlayView.setOnClickListener((view) -> dismissFabMenu());

        fabOpen.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                applyFabState(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        fabClose.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                applyFabState(false);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void reset() {
        if (!isFabOpen) {
            binding.fab.setAnimation(null);
            binding.fab1.setAnimation(null);
            binding.fab2.setAnimation(null);
            binding.fab1Label.setAnimation(null);
            binding.fab2Label.setAnimation(null);
        }
        applyFabState(isFabOpen);
    }

    public void onClick(View v) {
        if (listener == null)
            return;
        switch (v.getId()) {
            case R.id.fab:
                if (listener.isFABExpandable()) {
                    animateFAB();
                } else {
                    listener.onFabClicked(0);
                }
                break;
            case R.id.fab1Label:
            case R.id.fab1:
                listener.onFabClicked(1);
                isFabOpen = false;
                break;
            case R.id.fab2Label:
            case R.id.fab2:
                listener.onFabClicked(2);
                isFabOpen = false;
                break;
        }
    }

    private void dismissFabMenu() {
        animateFAB();
    }

    private void animateFAB() {
        if (isFabOpen) {
            binding.fab.startAnimation(rotateBackward);
            binding.fab1.startAnimation(fabClose);
            binding.fab2.startAnimation(fabClose);
            binding.fab1Label.startAnimation(fabHideAnimation);
            binding.fab2Label.startAnimation(fabHideAnimation);
            overlayView.setAlpha(1);
            overlayView.animate().alpha(0).start();
        } else {
            binding.fab.startAnimation(rotateForward);
            binding.fab1.startAnimation(fabOpen);
            binding.fab2.startAnimation(fabOpen);
            binding.fab1Label.startAnimation(fabShowAnimation);
            binding.fab2Label.startAnimation(fabShowAnimation);
            overlayView.setAlpha(0);
            overlayView.setVisibility(VISIBLE);
            overlayView.animate().alpha(1).start();
        }
    }

    private void applyFabState(boolean state) {
        isFabOpen = state;
        final int visibility = isFabOpen ? VISIBLE : INVISIBLE;
        binding.fab1Label.setVisibility(visibility);
        binding.fab2Label.setVisibility(visibility);
        binding.fab1.setClickable(isFabOpen);
        binding.fab2.setClickable(isFabOpen);
        binding.fab1Label.setClickable(isFabOpen);
        binding.fab2Label.setClickable(isFabOpen);
        overlayView.setVisibility(visibility);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        isFabOpen = savedInstanceState.getBoolean("fab_open");
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("fab_open", isFabOpen);
    }

    public void notifyContentChanged() {
        if (isFabOpen && !listener.isFABExpandable()) {
            animateFAB();
        }
    }

    public void setFABHelperTitle(int stringRes) {
        if (stringRes == 0) {
            binding.newLayout.setVisibility(View.GONE);
        } else {
            binding.newLayout.setVisibility(VISIBLE);
            binding.newText.setText(stringRes);
        }
    }

    public void setFABItem(int index, @DrawableRes int icon, @StringRes int text) {
        switch (index) {
            case 1:
                binding.fab1.setImageResource(icon);
                binding.fab1Label.setText(text);
                break;
            case 2:
                binding.fab2.setImageResource(icon);
                binding.fab2Label.setText(text);
                break;
        }
    }
}
