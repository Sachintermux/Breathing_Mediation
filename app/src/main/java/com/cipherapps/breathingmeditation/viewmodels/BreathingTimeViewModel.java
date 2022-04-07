package com.cipherapps.breathingmeditation.viewmodels;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cipherapps.breathingmeditation.R;
import com.cipherapps.breathingmeditation.savedata.SharePrefDataSave;

public class BreathingTimeViewModel extends ViewModel {

    public final MutableLiveData<Boolean> startTimer = new MutableLiveData<>(false);
    public MutableLiveData<Integer> timerDuration = new MutableLiveData<>(5);
    public MutableLiveData<Integer> breathInDuration = new MutableLiveData<>(4);
    public MutableLiveData<Integer> hold1Duration = new MutableLiveData<>(7);
    public MutableLiveData<Integer> breathOutDuration = new MutableLiveData<>(8);
    public MutableLiveData<Integer> hold2Duration = new MutableLiveData<>(2);
    public MutableLiveData<Integer> soundSelection = new MutableLiveData<>(3);
    public MutableLiveData<Boolean> enableSaveDataBtn = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> saveDataBtnClick = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> showAds = new MutableLiveData<>(false);



    private Context context;
    public MutableLiveData<Integer> breathCyclePosition = new MutableLiveData<>(1);
    private SharePrefDataSave sharePrefDataSave = new SharePrefDataSave();

    public void setSoundSelection(int value){
        soundSelection.setValue(value);
        sharePrefDataSave.savedData(context,context.getString(R.string.soundSelectionTAG),value);

    }
    public void setContext( Context context ) {
        this.context = context;
    }

    public void setStartTimer( boolean value ) {
        this.startTimer.setValue(value);

    }

    public void setTimerDuration( int timerDuration ) {
        this.timerDuration.setValue(timerDuration);
        sharePrefDataSave.savedData(context, context.getString(R.string.durationTAG), timerDuration);
    }

    public void setBreathInDuration( int breathInDuration ) {
        this.breathInDuration.setValue(breathInDuration);
        sharePrefDataSave.savedData(context, context.getString(R.string.breathInDurationTAG), breathInDuration);
    }

    public void setHold1Duration( int hold1Duration ) {
        this.hold1Duration.setValue(hold1Duration);
        sharePrefDataSave.savedData(context, context.getString(R.string.hold1DurationTAG), hold1Duration);
    }

    public void setBreathOutDuration( int breathOutDuration ) {
        this.breathOutDuration.setValue(breathOutDuration);
        sharePrefDataSave.savedData(context, context.getString(R.string.breathOutDurationTAG), breathOutDuration);
    }

    public void setHold2Duration( int hold2Duration ) {
        this.hold2Duration.setValue(hold2Duration);
        sharePrefDataSave.savedData(context, context.getString(R.string.hold2DurationTAG), hold2Duration);
    }

    public void getAllSaveData(Context context){
        timerDuration.setValue(sharePrefDataSave.getData(context, context.getString(R.string.durationTAG), 5));
        breathInDuration.setValue(sharePrefDataSave.getData(context, context.getString(R.string.breathInDurationTAG), 4));
        breathOutDuration.setValue(sharePrefDataSave.getData(context,context.getString(R.string.breathOutDurationTAG),8));
        hold1Duration.setValue(sharePrefDataSave.getData(context, context.getString(R.string.hold1DurationTAG), 7));
        hold2Duration.setValue(sharePrefDataSave.getData(context, context.getString(R.string.hold2DurationTAG), 2));
        soundSelection.setValue(sharePrefDataSave.getData(context,context.getString(R.string.soundSelectionTAG),3));
        breathCyclePosition.setValue(1);
    }


}