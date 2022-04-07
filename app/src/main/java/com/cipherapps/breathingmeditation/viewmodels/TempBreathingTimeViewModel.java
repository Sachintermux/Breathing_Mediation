package com.cipherapps.breathingmeditation.viewmodels;

import android.content.Context;
import android.os.Handler;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cipherapps.breathingmeditation.R;
import com.cipherapps.breathingmeditation.savedata.SharePrefDataSave;

public class TempBreathingTimeViewModel extends ViewModel {

    private static Runnable runnable = null;
    private final Handler handler = new Handler();
    public MutableLiveData<Integer> timerDuration = new MutableLiveData<>(1);
    public MutableLiveData<Integer> breathInDuration = new MutableLiveData<>(7);
    public MutableLiveData<Integer> hold1Duration = new MutableLiveData<>(4);
    public MutableLiveData<Integer> breathOutDuration = new MutableLiveData<>(8);
    public MutableLiveData<Integer> hold2Duration = new MutableLiveData<>(2);
    public MutableLiveData<Integer> breathCyclePosition = new MutableLiveData<>(1);
    public MutableLiveData<Boolean> isTimerRunning = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> isSessionComplete = new MutableLiveData<>(false);
    private SharePrefDataSave sharePrefDataSave = new SharePrefDataSave();
    private Context context;
    private boolean flag = false;

    public void setContext( Context context ) {
        this.context = context;
        getAllSaveData(context);
    }

    public void setBreathInDuration( int value ) {
        this.breathInDuration.setValue(value);
    }

    public void setTimerDuration( int value ) {
        this.timerDuration.setValue(value);
    }

    public void setBreathOutDuration( int value ) {
        this.breathOutDuration.setValue(value);
    }

    public void setHold1Duration( int value ) {
        this.hold1Duration.setValue(value);
    }

    public void setHold2Duration( int value ) {
        this.hold2Duration.setValue(value);
    }

    public void startTimer() {
        isTimerRunning.setValue(true);
        if (runnable == null) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    if (timerDuration.getValue() > 0) {
                        timerDuration.setValue(timerDuration.getValue() - 1);
                        handler.postDelayed(this, 1000);
                    } else {
                        isTimerRunning.setValue(false);
                        handler.removeCallbacksAndMessages(this);
                        isSessionComplete.setValue(true);
                    }
                }
            };
            runnable.run();
        }

    }

    public void stopTimer() {
        isTimerRunning.setValue(false);
        handler.removeCallbacksAndMessages(runnable);
        handler.removeCallbacks(runnable);
        runnable = null;
    }


    public void getAllSaveData( Context context ) {
        timerDuration.setValue(sharePrefDataSave.getData(context, context.getString(R.string.durationTAG), 5) * 60);
        breathInDuration.setValue(sharePrefDataSave.getData(context, context.getString(R.string.breathInDurationTAG), 4));
        breathOutDuration.setValue(sharePrefDataSave.getData(context, context.getString(R.string.breathOutDurationTAG), 8));
        hold1Duration.setValue(sharePrefDataSave.getData(context, context.getString(R.string.hold1DurationTAG), 7));
        hold2Duration.setValue(sharePrefDataSave.getData(context, context.getString(R.string.hold2DurationTAG), 2));
        breathCyclePosition.setValue(1);
    }

}
