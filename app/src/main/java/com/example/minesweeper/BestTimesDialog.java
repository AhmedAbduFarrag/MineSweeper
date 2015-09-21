package com.example.minesweeper;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.fullmindgames.minesweeper.R;

public class BestTimesDialog extends DialogFragment {
	private GameManager gameManager;
	private Typeface typeface;

	public static BestTimesDialog newInstance() {
		BestTimesDialog f = new BestTimesDialog();

		return f;
	}

	@Override
	public void onStart() {
		super.onStart();
		Dialog d = getDialog();

		if (d != null) {

			int width = (int) getResources().getDimension(R.dimen.dialog_width);
			int height = ViewGroup.LayoutParams.WRAP_CONTENT;
			d.getWindow().setLayout(width, height);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		typeface = Typeface.createFromAsset(getActivity().getAssets(),
				"dialogFont.otf");
	}

	private View getContentView(LayoutInflater inflater) {
		final LinearLayout layout = new LinearLayout(getActivity());
		layout.setOrientation(LinearLayout.VERTICAL);
		gameManager = GameManager.getGameManager();

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		params.topMargin = 18;

		layout.setLayoutParams(params);
		for (int i = 0; i < 3; i++) {
			View view = inflater.inflate(R.layout.best_times_layout, null);
			view.setPadding(8, 8, 8, 8);
			TextView txt_level = (TextView) view
					.findViewById(R.id.best_times_level);
			TextView txt_time = (TextView) view
					.findViewById(R.id.best_times_time);
			ImageButton iBtn_reset = (ImageButton) view
					.findViewById(R.id.best_times_reset);
			txt_time.setTag(i);
			iBtn_reset.setTag(txt_time);
			iBtn_reset.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					TextView txt_time = (TextView) v.getTag();
					int level = (Integer) txt_time.getTag();
					gameManager.resetBestTime(level);
					txt_time.setText("----- ");
				}
			});
			String levelStr = gameManager.getLevelString(i);
			long time = gameManager.getBestTime(i);
			String timeStr = "";
			String seconds = "";
			if (time < 3600000) {
				int minutes = (int) ((time / (1000 * 60)) % 60);

				if (minutes > 0)
					seconds = "minutes";
				else
					seconds = "seconds";
				SimpleDateFormat df = new SimpleDateFormat("mm:ss");
				timeStr = df.format(new Date(time));
			} else
				timeStr = "-----";
			txt_level.setTypeface(typeface);
			txt_level.setText(levelStr);
			txt_level.setTextSize(18);
			txt_time.setText(timeStr + " " + seconds);
			txt_time.setTextSize(15);
			txt_time.setTypeface(typeface);
			layout.addView(view);
		}

		return layout;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View custom_title = inflater.inflate(R.layout.custom_main_title, null);
		ImageButton iBtn_back = (ImageButton) custom_title
				.findViewById(R.id.sittings_iBtn_back);
		TextView txt_title = (TextView) custom_title
				.findViewById(R.id.sittings_txt_title);
		RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) iBtn_back
				.getLayoutParams();

		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) txt_title
				.getLayoutParams();
		params2.topMargin = params2.leftMargin = params.topMargin = 10;
		iBtn_back.setLayoutParams(params2);
		txt_title.setTypeface(typeface);
		txt_title.setText("Best Times");
		params.leftMargin = 25;
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		txt_title.setLayoutParams(params);
		txt_title.setTextSize(22);

		iBtn_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dismiss();
			}
		});
		AlertDialog dialog = new AlertDialog.Builder(getActivity())
				.setCustomTitle(custom_title).setView(getContentView(inflater))
				.create();
		return dialog;

	}

}
