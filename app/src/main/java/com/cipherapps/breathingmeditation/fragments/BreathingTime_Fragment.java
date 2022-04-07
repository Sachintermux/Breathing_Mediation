package com.cipherapps.breathingmeditation.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.cipherapps.breathingmeditation.R;
import com.cipherapps.breathingmeditation.activitys.SaveSession;
import com.cipherapps.breathingmeditation.activitys.SettingsActivity;
import com.cipherapps.breathingmeditation.savedata.SharePrefDataSave;
import com.cipherapps.breathingmeditation.viewmodels.BreathingTimeViewModel;
import com.cipherapps.breathingmeditation.viewmodels.TempBreathingTimeViewModel;

public class BreathingTime_Fragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final Handler handler = new Handler();
    private Runnable runnable = null;
    private TextView breathIn_txt, hold1_txt, breathOut_txt, hold2_txt, showTime_txt, breathIndicator_txt, changeRule_txt;
    private BreathingTimeViewModel viewModel;
    private TempBreathingTimeViewModel tempViewModel;
    private SharePrefDataSave sharePrefDataSave = new SharePrefDataSave();
    private Dialog dialog;
    private String mParam1;
    private String mParam2;
    private MediaPlayer mediaPlayer = null;
    private int soundSelectionPosition = 3;
    private String sessionDurationTAG = "sessionDuration";
    private boolean changeRuleFlag = false;

    public BreathingTime_Fragment() {

    }

    public static BreathingTime_Fragment newInstance( String param1, String param2 ) {
        BreathingTime_Fragment fragment = new BreathingTime_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        View view = inflater.inflate(R.layout.breathin_time__fragment, container, false);
        initializeView(view);

        return view;
    }

    private void initializeView( View view ) {
        breathIn_txt = view.findViewById(R.id.breathIn_txtFragment);
        breathOut_txt = view.findViewById(R.id.breathOut_txtFragment);
        hold1_txt = view.findViewById(R.id.breathHold1_txtFragment);
        hold2_txt = view.findViewById(R.id.breathHold2_txtFragment);
        showTime_txt = view.findViewById(R.id.showTime_txtFragment);
        breathIndicator_txt = view.findViewById(R.id.breathIndicator);
        changeRule_txt = view.findViewById(R.id.ruleChange_txtFragment);
        changeRuleClick();
        viewModel = new ViewModelProvider(requireActivity()).get(BreathingTimeViewModel.class);
        viewModel.setContext(requireActivity());
        tempViewModel = new ViewModelProvider(requireActivity()).get(TempBreathingTimeViewModel.class);
        tempViewModel.setContext(requireActivity());
        observersViewModel();
        viewModel.getAllSaveData(requireActivity());
        tempObserversViewModel();

    }

    private void indicatorTextBlinking() {
        if (viewModel.startTimer.getValue()) {
            Animation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setDuration(700);
            anim.setStartOffset(20);
            anim.setRepeatMode(Animation.REVERSE);
            anim.setRepeatCount(Animation.INFINITE);
            breathIndicator_txt.startAnimation(anim);
        } else breathIndicator_txt.clearAnimation();
    }

    private void tempObserversViewModel() {

        tempViewModel.isSessionComplete.observe(requireActivity(), value -> {
            if (value)
                sessionComplete();
        });


        tempViewModel.breathCyclePosition.observe(requireActivity(), value -> {
            breathCycleSoundPlay(value);
        });

        tempViewModel.isTimerRunning.observe(requireActivity(), value -> {
            if (value) {
                breathCycleSoundPlay(tempViewModel.breathCyclePosition.getValue().intValue());
                startBreathInSession();
            } else {
                handler.removeCallbacks(runnable);
            }
            enableSaveSessionBtn();
        });

        tempViewModel.breathInDuration.observe(requireActivity(), value -> {
            breathIn_txt.setText(String.valueOf(value));
        });

        tempViewModel.breathOutDuration.observe(requireActivity(), value -> {
            breathOut_txt.setText(String.valueOf(value));
        });

        tempViewModel.hold1Duration.observe(requireActivity(), value -> {
            hold1_txt.setText(String.valueOf(value));
        });

        tempViewModel.hold2Duration.observe(requireActivity(), value -> {
            hold2_txt.setText(String.valueOf(value));
        });

        tempViewModel.timerDuration.observe(requireActivity(), value -> {
            setTimeWithFormat(value);
            if (!tempViewModel.isTimerRunning.getValue().booleanValue()) enableSaveSessionBtn();
        });
    }

    private void sessionComplete() {
        @SuppressLint("ResourceType") Dialog dialog = new Dialog(requireActivity());
        dialog.setContentView(R.layout.session_complete_dialog);
        Button cancelBtn_dialog = dialog.findViewById(R.id.cancelComplete_BtnDialog),
                saveBtn_dialog = dialog.findViewById(R.id.saveComplete_BtnDialog);

        dialog.show();
        saveBtn_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                viewModel.showAds.setValue(true);
                Intent intent = new Intent(requireActivity(), SaveSession.class);
                int sessionDuration = enableSaveSessionBtn();
                intent.putExtra(sessionDurationTAG, sessionDuration);
                startActivity(intent);

            }
        });


        cancelBtn_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                dialog.cancel();
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel( DialogInterface dialogInterface ) {
                resetAll();
            }
        });
    }

    private void breathCycleSoundPlay( int position ) {
        if (tempViewModel.isTimerRunning.getValue().booleanValue()) {
            switch (position) {
                case 1:
                    breathIndicator_txt.setText("Breath In");
                    if (viewModel.soundSelection.getValue() == 3) playSound(R.raw.voice_breath_in);
                    if (viewModel.soundSelection.getValue() == 2) playSound(R.raw.bells_breath_in);
                    break;
                case 2:
                case 4:
                    breathIndicator_txt.setText("Hold");
                    if (viewModel.soundSelection.getValue() == 3) playSound(R.raw.voice_hold);
                    if (viewModel.soundSelection.getValue() == 2) playSound(R.raw.bells_hold);
                    break;
                case 3:
                    breathIndicator_txt.setText("Breath Out");
                    if (viewModel.soundSelection.getValue() == 3) playSound(R.raw.voice_breath_out);
                    if (viewModel.soundSelection.getValue() == 2) playSound(R.raw.bells_breath_in);
                    break;

            }
        }
    }


    private void playSound( int res ) {
        if (mediaPlayer != null)
            mediaPlayer.release();
        mediaPlayer = MediaPlayer.create(requireActivity(), res);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.start();

    }

    private void startBreathInSession() {
        runnable = new Runnable() {
            @Override
            public void run() {
                if (tempViewModel.isTimerRunning.getValue().booleanValue()) {
                    switch (tempViewModel.breathCyclePosition.getValue()) {

                        case 1:
                            breathIn_txt.setTextColor(Color.GREEN);
                            breathOut_txt.setTextColor(Color.WHITE);
                            hold1_txt.setTextColor(Color.WHITE);
                            hold2_txt.setTextColor(Color.WHITE);
                            setTextToBreathCycle(breathIn_txt);
                            break;

                        case 2:
                            hold1_txt.setTextColor(Color.GREEN);
                            breathIn_txt.setTextColor(Color.WHITE);
                            breathOut_txt.setTextColor(Color.WHITE);
                            hold2_txt.setTextColor(Color.WHITE);
                            setTextToBreathCycle(hold1_txt);
                            break;

                        case 3:
                            breathOut_txt.setTextColor(Color.GREEN);
                            breathIn_txt.setTextColor(Color.WHITE);
                            hold1_txt.setTextColor(Color.WHITE);
                            hold2_txt.setTextColor(Color.WHITE);
                            setTextToBreathCycle(breathOut_txt);
                            break;

                        case 4:
                            hold2_txt.setTextColor(Color.GREEN);
                            breathIn_txt.setTextColor(Color.WHITE);
                            breathOut_txt.setTextColor(Color.WHITE);
                            hold1_txt.setTextColor(Color.WHITE);
                            setTextToBreathCycle(hold2_txt);
                            break;
                    }
                    handler.postDelayed(this, 1000);
                }
            }
        };
        runnable.run();

    }

    @SuppressLint("SetTextI18n")
    private void setTextToBreathCycle( TextView textView ) {
        int text = Integer.parseInt(textView.getText().toString());
        text--;
        textView.setText(String.valueOf(text));
        if (text == 0) {
            int position = Integer.parseInt(tempViewModel.breathCyclePosition.getValue().toString());
            switch (position) {
                case 1:
                    textView.setText(tempViewModel.breathInDuration.getValue().toString());
                    position = 2;
                    break;
                case 2:
                    textView.setText(tempViewModel.hold1Duration.getValue().toString());
                    position = 3;
                    break;

                case 3:
                    textView.setText(tempViewModel.breathOutDuration.getValue().toString());
                    position = 4;
                    break;

                case 4:
                    textView.setText(tempViewModel.hold2Duration.getValue().toString());
                    position = 1;
                    break;
            }
            tempViewModel.breathCyclePosition.setValue(position);
        }

    }

    public void resetAll() {
        viewModel.setStartTimer(false);
        viewModel.getAllSaveData(requireActivity());
    }

    public void observersViewModel() {

        viewModel.saveDataBtnClick.observe(requireActivity(), value -> {
            if (value) saveDataBtnClick();
        });

        viewModel.startTimer.observe(requireActivity(), value -> {
            if (value) {
                tempViewModel.startTimer();
            } else {
                tempViewModel.stopTimer();
                handler.removeCallbacks(runnable);

            }
            indicatorTextBlinking();

        });

        viewModel.breathInDuration.observe(requireActivity(), value -> {
            tempViewModel.getAllSaveData(requireActivity());
        });

        viewModel.breathCyclePosition.observe(requireActivity(), value -> {
            tempViewModel.getAllSaveData(requireActivity());
        });

        viewModel.breathOutDuration.observe(requireActivity(), value -> {
            tempViewModel.getAllSaveData(requireActivity());
        });

        viewModel.hold1Duration.observe(requireActivity(), value -> {
            tempViewModel.getAllSaveData(requireActivity());
        });

        viewModel.hold2Duration.observe(requireActivity(), value -> {
            tempViewModel.getAllSaveData(requireActivity());
        });

        viewModel.timerDuration.observe(requireActivity(), value -> {
            tempViewModel.getAllSaveData(requireActivity());
        });

    }

    private void saveDataBtnClick() {
        Intent intent = new Intent(requireActivity(), SaveSession.class);
        int saveSessionDuration = enableSaveSessionBtn();
        intent.putExtra(sessionDurationTAG, saveSessionDuration);
        startActivity(intent);
        resetAll();
    }

    private int enableSaveSessionBtn() {
        int totalTime = timeFormatToSecond(viewModel.timerDuration.getValue().toString());
        int spendTime = timeFormatToSecond(showTime_txt.getText().toString());
        if (totalTime == spendTime) {
            viewModel.enableSaveDataBtn.setValue(false);
        } else {
            if (!viewModel.startTimer.getValue())
                viewModel.enableSaveDataBtn.setValue(true);
            else viewModel.enableSaveDataBtn.setValue(false);
        }
        return totalTime - spendTime;
    }

    private int timeFormatToSecond( String time ) {
        int ans;
        if (time.contains(":")) {
            String[] timeArr = time.split(":");
            ans = Integer.parseInt(timeArr[0]) * 60;
            ans += Integer.parseInt(timeArr[1]);
        } else
            ans = Integer.parseInt(time) * 60;
        return ans;
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.setStartTimer(false);
        tempViewModel.stopTimer();
        soundSelectionPosition = viewModel.soundSelection.getValue().intValue();
        if (changeRuleFlag) resetAll();
        changeRuleFlag = false;
    }

    @Override
    public void onPause() {
        viewModel.setStartTimer(false);
        tempViewModel.stopTimer();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setTimeWithFormat( Integer value ) {

        String timeString = String.format("%02d:%02d", value / 60, value % 60);
        showTime_txt.setText(timeString);

    }


    public void changeRuleClick() {
        changeRule_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                dialog = new Dialog(requireActivity());
                dialog.setContentView(R.layout.dialog_layout);
                dialog.show();
                TextView morning_dialog, evening_dialog, night_dialog, beginner_dialog, moderate_dialog, advance_dialog, custom_dialog;
                morning_dialog = dialog.findViewById(R.id.morning_dialog);
                evening_dialog = dialog.findViewById(R.id.evening_dialog);
                night_dialog = dialog.findViewById(R.id.night_dialog);
                beginner_dialog = dialog.findViewById(R.id.beginner_dialog);
                moderate_dialog = dialog.findViewById(R.id.moderate_dialog);
                advance_dialog = dialog.findViewById(R.id.advance_dialog);
                custom_dialog = dialog.findViewById(R.id.custom_dialog);

                morning_dialog.setOnClickListener(this::dialog_txtClick);
                evening_dialog.setOnClickListener(this::dialog_txtClick);
                night_dialog.setOnClickListener(this::dialog_txtClick);
                beginner_dialog.setOnClickListener(this::dialog_txtClick);
                moderate_dialog.setOnClickListener(this::dialog_txtClick);
                advance_dialog.setOnClickListener(this::dialog_txtClick);
                custom_dialog.setOnClickListener(this::dialog_txtClick);
            }

            private void dialog_txtClick( View view ) {
                switch (view.getId()) {
                    case R.id.morning_dialog:
                        morningClick(view);
                        break;

                    case R.id.evening_dialog:
                        eveningClick(view);
                        break;

                    case R.id.night_dialog:
                        nightClick(view);
                        break;

                    case R.id.beginner_dialog:
                        beginnerClick(view);
                        break;

                    case R.id.moderate_dialog:
                        moderateClick(view);
                        break;

                    case R.id.advance_dialog:
                        advanceClick(view);
                        break;

                    default:
                        startActivity(new Intent(requireActivity(), SettingsActivity.class));
                        changeRuleFlag = true;
                        break;
                }
                dialog.dismiss();
            }
        });

    }

    private void setDefaultData( int breathIn, int hold1, int breathOut, int hold2, int time ) {
        viewModel.setBreathInDuration(breathIn);
        viewModel.setBreathOutDuration(breathOut);
        viewModel.setHold1Duration(hold1);
        viewModel.setHold2Duration(hold2);
        viewModel.setTimerDuration(time);
        viewModel.setStartTimer(false);
        resetAll();

    }

    public void morningClick( View view ) {
        setDefaultData(
                getResources().getInteger(R.integer.morningBreathIn),
                getResources().getInteger(R.integer.morningHold1),
                getResources().getInteger(R.integer.morningBreathOut),
                getResources().getInteger(R.integer.morningHold2),
                getResources().getInteger(R.integer.morningTimeDuration)
        );
    }

    public void eveningClick( View view ) {
        setDefaultData(
                getResources().getInteger(R.integer.eveningBreathIn),
                getResources().getInteger(R.integer.eveningHold1),
                getResources().getInteger(R.integer.eveningBreathOut),
                getResources().getInteger(R.integer.eveningHold2),
                getResources().getInteger(R.integer.eveningTimeDuration)
        );
    }

    public void nightClick( View view ) {
        setDefaultData(
                getResources().getInteger(R.integer.nightBreathIn),
                getResources().getInteger(R.integer.nightHold1),
                getResources().getInteger(R.integer.nightBreathOut),
                getResources().getInteger(R.integer.nightHold2),
                getResources().getInteger(R.integer.nightTimeDuration)
        );
    }

    public void beginnerClick( View view ) {
        setDefaultData(
                getResources().getInteger(R.integer.beginnerBreathIn),
                getResources().getInteger(R.integer.beginnerHold1),
                getResources().getInteger(R.integer.beginnerBreathOut),
                getResources().getInteger(R.integer.beginnerHold2),
                getResources().getInteger(R.integer.beginnerTimeDuration)
        );

    }

    public void moderateClick( View view ) {
        setDefaultData(
                getResources().getInteger(R.integer.moderateBreathIn),
                getResources().getInteger(R.integer.moderateHold1),
                getResources().getInteger(R.integer.moderateBreathOut),
                getResources().getInteger(R.integer.moderateHold2),
                getResources().getInteger(R.integer.moderateTimeDuration)
        );
    }

    public void advanceClick( View view ) {
        setDefaultData(
                getResources().getInteger(R.integer.advanceBreathIn),
                getResources().getInteger(R.integer.advanceHold1),
                getResources().getInteger(R.integer.advanceBreathOut),
                getResources().getInteger(R.integer.advanceHold2),
                getResources().getInteger(R.integer.advanceTimeDuration)
        );
    }
}