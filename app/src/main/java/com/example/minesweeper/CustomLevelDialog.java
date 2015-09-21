package com.example.minesweeper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fullmindgames.minesweeper.R;

public class CustomLevelDialog extends DialogFragment implements
		View.OnClickListener {

	

	private GameManager gameManager;
	private Typeface typeface;

	EditText eTxt_cols;
	EditText eTxt_rows;
	EditText eTxt_mines;

	ImageButton iBtn_cols_up;
	ImageButton iBtn_cols_down;
	ImageButton iBtn_rows_up;
	ImageButton iBtn_rows_down;
	ImageButton iBtn_mines_up;
	ImageButton iBtn_mines_down;
	Button btn_start;

	private int cols;
	private int rows;
	private int mines;

	public static CustomLevelDialog newInstance() {
		CustomLevelDialog f = new CustomLevelDialog();
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
		gameManager = GameManager.getGameManager();
		typeface = Typeface.createFromAsset(getActivity().getAssets(),
				"Reeler_PersonalUse.ttf");

	}

	private View getContentView(LayoutInflater inflater) {

		cols = gameManager
				.getSavedCustomLevel(GameManager.KEY_CUSTOM_COLS_INDEX);
		rows = gameManager
				.getSavedCustomLevel(GameManager.KEY_CUSTOM_ROWS_INDEX);
		mines = gameManager
				.getSavedCustomLevel(GameManager.KEY_CUSTOM_MINES_INDEX);

		View view = inflater.inflate(R.layout.custom_level_layout, null);
		eTxt_cols = (EditText) view.findViewById(R.id.custom_level_eTxt_cols);
		eTxt_rows = (EditText) view.findViewById(R.id.custom_level_eTxt_rows);
		eTxt_mines = (EditText) view.findViewById(R.id.custom_level_eTxt_mines);

		eTxt_cols.setText(String.valueOf(cols));
		eTxt_rows.setText(String.valueOf(rows));
		eTxt_mines.setText(String.valueOf(mines));

		eTxt_cols.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable text) {
				if (eTxt_cols.getText().length() > 0)

					cols = Integer.valueOf(text.toString());

			}
		});

		eTxt_rows.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable text) {
				if (eTxt_rows.getText().length() > 0)
					rows = Integer.valueOf(text.toString());
			}
		});
		eTxt_mines.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable text) {
				if (eTxt_mines.getText().length() > 0)
					mines = Integer.valueOf(text.toString());
			}
		});

		iBtn_cols_up = (ImageButton) view
				.findViewById(R.id.custom_level_lbtn_cols_up);
		iBtn_rows_up = (ImageButton) view
				.findViewById(R.id.custom_level_lbtn_rows_up);
		iBtn_mines_up = (ImageButton) view
				.findViewById(R.id.custom_level_lbtn_mines_up);
		iBtn_cols_down = (ImageButton) view
				.findViewById(R.id.custom_level_lbtn_cols_down);
		iBtn_rows_down = (ImageButton) view
				.findViewById(R.id.custom_level_lbtn_rows_down);
		iBtn_mines_down = (ImageButton) view
				.findViewById(R.id.custom_level_lbtn_mines_down);

		btn_start = (Button) view.findViewById(R.id.custom_level_btn_start);
		btn_start.setTypeface(typeface);

		iBtn_cols_up.setOnClickListener(this);
		iBtn_cols_down.setOnClickListener(this);
		iBtn_rows_up.setOnClickListener(this);
		iBtn_rows_down.setOnClickListener(this);
		iBtn_mines_up.setOnClickListener(this);
		iBtn_mines_down.setOnClickListener(this);
		btn_start.setOnClickListener(this);

		((TextView) view.findViewById(R.id.custom_level_lbl_cols))
				.setTypeface(typeface);
		((TextView) view.findViewById(R.id.custom_level_lbl_rows))
				.setTypeface(typeface);
		((TextView) view.findViewById(R.id.custom_level_lbl_mines))
				.setTypeface(typeface);

		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.custom_level_lbtn_cols_up:
			if (cols >= 50)
				return;
			cols++;

			break;
		case R.id.custom_level_lbtn_rows_up:
			if (rows >= 30)
				return;
			rows++;

			break;
		case R.id.custom_level_lbtn_cols_down:
			if (cols <= 9)
				return;
			cols--;

			break;
		case R.id.custom_level_lbtn_rows_down:
			if (rows <= 9)
				return;
			rows--;

			break;
		case R.id.custom_level_lbtn_mines_up:
			if (mines >= (cols * rows - 15))
				return;
			mines++;

			break;
		case R.id.custom_level_lbtn_mines_down:
			if (mines <= 10 || mines < (cols * rows / 8))
				return;
			mines--;
			break;
		case R.id.custom_level_btn_start:

			if (cols < 0 || rows < 0)
				return;
			else
				gameManager.setCustomLevel(cols, rows, mines);
			dismiss();
			break;
		}
		eTxt_cols.setText(String.valueOf(cols));
		eTxt_rows.setText(String.valueOf(rows));
		eTxt_mines.setText(String.valueOf(mines));
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
		txt_title.setText("Custom Level");
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
		AlertDialog d = new AlertDialog.Builder(getActivity())
				.setCustomTitle(custom_title).setView(getContentView(inflater))
				.create();
		return d;

	}

}
