package com.example.minesweeper;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.fullmindgames.minesweeper.R;

public class StarterFragment extends Fragment {
	private Button btn_startGame;
	private Button btn_rate;
	private Button btn_bestTimes;

	private Typeface typeface;

	public StarterFragment() {
	}

	private View.OnClickListener listener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.main_fragment_layout, null);
		typeface = Typeface.createFromAsset(getActivity().getAssets(),
				"Reeler_PersonalUse.ttf");

		btn_startGame = (Button) view.findViewById(R.id.main_btn_start_game);
		btn_bestTimes = (Button) view.findViewById(R.id.main_btn_bestTimes);
		btn_rate = (Button) view.findViewById(R.id.main_btn_rate);

		btn_startGame.setTypeface(typeface);
		btn_rate.setTypeface(typeface);
		btn_bestTimes.setTypeface(typeface);

		btn_startGame.setOnClickListener(listener);
		btn_rate.setOnClickListener(listener);
		btn_bestTimes.setOnClickListener(listener);

		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		listener = (OnClickListener) activity;
		super.onAttach(activity);
	}

}
