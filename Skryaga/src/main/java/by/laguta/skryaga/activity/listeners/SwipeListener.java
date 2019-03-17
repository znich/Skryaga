package by.laguta.skryaga.activity.listeners;

import android.app.Activity;
import android.transition.TransitionManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import by.laguta.skryaga.service.util.Settings;

import java.util.Timer;
import java.util.TimerTask;

public class SwipeListener implements View.OnTouchListener {

    private int mXDelta;
    private View swipableView;
    private Timer timer;
    private TimerTask task;
    private Activity activity;
    private Double totalAmount;

    public SwipeListener(View swipableView, Activity activity) {
        this.swipableView = swipableView;
        this.activity = activity;
        timer = new Timer();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final int x = (int) event.getRawX();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) swipableView.getLayoutParams();
                mXDelta = x - lParams.leftMargin;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                int left = swipableView.getLeft();
                if (left < 10) {
                    moveToLeft(x - mXDelta);
                }
                break;
        }
        normalizeAfterSwap();
        return true;
    }

    private void moveToLeft(int leftMargin) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) swipableView.getLayoutParams();
        layoutParams.leftMargin = leftMargin;
        layoutParams.rightMargin = -300;
        swipableView.setLayoutParams(layoutParams);
    }

    private void normalizeAfterSwap() {
        if (task != null) {
            task.cancel();
            timer.purge();
        }
        task = new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(new TimerTask() {
                    @Override
                    public void run() {
                        int swipeLeftPosition = swipableView.getLeft();
                        if (swipeLeftPosition >= 0) {
                            moveLeftWithAnimation(0);
                        } else {
                            setPosition();
                        }
                    }
                });
            }
        };
        timer.schedule(task, 300);
    }

    private void moveLeftWithAnimation(int leftMargin) {
        ViewGroup rootView = (ViewGroup) swipableView.getParent();
        TransitionManager.beginDelayedTransition(rootView);
        moveToLeft(leftMargin);
    }

    public void initializePosition(Double totalAmount) {
        this.totalAmount = totalAmount;
        setPosition();
    }

    private void setPosition() {
        if (shouldHide(totalAmount)) {
            int swipeMargin = totalAmount > 9999 ? -48 : -32;
            moveLeftWithAnimation(swipeMargin);
        } else {
            moveLeftWithAnimation(0);
        }
    }

    private boolean shouldHide(Double totalAmount) {
        Settings settings = Settings.getInstance();
        boolean secureModeEnabled = settings.getModel().isSecureModeEnabled();
        return secureModeEnabled && totalAmount != null && totalAmount > 1000;
    }
}
