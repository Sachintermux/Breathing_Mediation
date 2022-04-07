package com.cipherapps.breathingmeditation.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.cipherapps.breathingmeditation.R;
import com.cipherapps.breathingmeditation.savedata.SharePrefDataSave;
import com.cipherapps.breathingmeditation.viewmodels.BreathingTimeViewModel;


public class ControlsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ImageView play_ic, close_ic, save_ic;
    private String mParam1;
    private String mParam2;
    private BreathingTimeViewModel viewModel;
    private SharePrefDataSave sharePrefDataSave = new SharePrefDataSave();

    public ControlsFragment() {
        // Required empty public constructor
    }

    public static ControlsFragment newInstance( String param1, String param2 ) {
        ControlsFragment fragment = new ControlsFragment();
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

        View view = inflater.inflate(R.layout.fragment_controls, container, false);
        initializeView(view);

        return view;

    }

    private void initializeView( View view ) {
        play_ic = view.findViewById(R.id.play_ic_ConFragment);
        save_ic = view.findViewById(R.id.save_ic_ConFragment);
        close_ic = view.findViewById(R.id.close_ic_ConFragment);
        viewModel = new ViewModelProvider(requireActivity()).get(BreathingTimeViewModel.class);
        viewModel.setContext(requireActivity());
        viewClicks();

        //observers
        observers();
    }

    private void observers() {
        viewModel.startTimer.observe(requireActivity(), value -> {
            if (value) {
                play_ic.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
            } else {
                play_ic.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
            }
        });

        viewModel.enableSaveDataBtn.observe(requireActivity(), value -> {
            if (value) {
                save_ic.setEnabled(true);
                save_ic.setColorFilter(getResources().getColor(R.color.check_bottom_nav_color));
            } else {
                save_ic.setEnabled(false);
                save_ic.setColorFilter(getResources().getColor(R.color.white));
            }
        });
    }

    private void viewClicks() {

        play_ic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                playClick();
            }
        });

        close_ic.setOnClickListener(this::closeClick);
        close_ic.setColorFilter(getResources().getColor(R.color.check_bottom_nav_color));
        save_ic.setOnClickListener(this::saveBtnClick);
    }

    private void saveBtnClick( View view ) {
        viewModel.showAds.setValue(true);
        viewModel.saveDataBtnClick.setValue(true);
    }

    private void closeClick( View view ) {
        viewModel.showAds.setValue(true);
        viewModel.getAllSaveData(requireActivity());
        viewModel.setStartTimer(false);
    }

    private void playClick() {
        Drawable drawable = getResources().getDrawable(R.drawable.ic_play);
        if (drawable.getConstantState().equals(play_ic.getDrawable().getConstantState())) {
            viewModel.setStartTimer(true);
            play_ic.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
        } else {
            viewModel.setStartTimer(false);
            play_ic.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
        }
    }

    @Override
    public void onPause() {
        play_ic.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
        super.onPause();
    }


}


