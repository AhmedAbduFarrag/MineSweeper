package com.example.minesweeper;

import android.app.Activity;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.RelativeSizeSpan;
import android.text.style.TypefaceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.fullmindgames.minesweeper.R;

public class LevelFragment extends Fragment {
	private View.OnClickListener listener;
	private Button btn_beginner;
	private Button btn_intermediate;
	private Button btn_expert;
	private Button btn_custom;
	private Typeface typeface;
	
	public LevelFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.level_fragment_layout, null);
		typeface = Typeface.createFromAsset(getActivity().getAssets(),
				"Reeler_PersonalUse.ttf");

		btn_beginner = (Button) view.findViewById(R.id.main_btn_beginner);
		btn_intermediate = (Button) view
				.findViewById(R.id.main_btn_intermediate);
		btn_expert = (Button) view.findViewById(R.id.main_btn_expert);
		btn_custom = (Button) view.findViewById(R.id.main_btn_custom);
		
		String s = "Beginner";
		String r = "9x9";

		Spannable beginnerSpan = new SpannableString(s + "\n" + r);
		beginnerSpan.setSpan(new CustomTypefaceSpan("", typeface), 0,
				s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		beginnerSpan.setSpan(new RelativeSizeSpan(0.8f), s.length(), s.length()
				+ r.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		s = "Intermediate";
		r = "16x16";
		Spannable intermediateSpan = new SpannableString(s + "\n" + r);
		intermediateSpan.setSpan(new CustomTypefaceSpan("", typeface), 0,
				s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		intermediateSpan
				.setSpan(new RelativeSizeSpan(0.8f), s.length(),
						s.length() + r.length() + 1,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		s = "Expert";
		r = "16x30";
		Spannable expertSpan = new SpannableString(s + "\n" + r);
		expertSpan.setSpan(new CustomTypefaceSpan("", typeface), 0, s.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		expertSpan.setSpan(new RelativeSizeSpan(0.8f), s.length(), s.length()
				+ r.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		s = "Custom";
		r = "?x?";
		Spannable customSpan = new SpannableString(s + "\n" + r);
		customSpan.setSpan(new CustomTypefaceSpan("", typeface), 0, s.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		customSpan.setSpan(new RelativeSizeSpan(0.8f), s.length(), s.length()
				+ r.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		btn_beginner.setText(beginnerSpan);
		btn_intermediate.setText(intermediateSpan);
		btn_expert.setText(expertSpan);
		btn_custom.setText(customSpan);


		btn_beginner.setOnClickListener(listener);
		btn_intermediate.setOnClickListener(listener);
		btn_expert.setOnClickListener(listener);
		btn_custom.setOnClickListener(listener);
		
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		listener = (OnClickListener) activity;
		super.onAttach(activity);
	}
	
	public class CustomTypefaceSpan extends TypefaceSpan {
		private final Typeface newType;

		public CustomTypefaceSpan(String family, Typeface type) {
			super(family);
			newType = type;
		}

		@Override
		public void updateDrawState(TextPaint ds) {
			applyCustomTypeFace(ds, newType);
		}

		@Override
		public void updateMeasureState(TextPaint paint) {
			applyCustomTypeFace(paint, newType);
		}

		private void applyCustomTypeFace(Paint paint, Typeface tf) {
			int oldStyle;
			Typeface old = paint.getTypeface();
			if (old == null) {
				oldStyle = 0;
			} else {
				oldStyle = old.getStyle();
			}

			int fake = oldStyle & ~tf.getStyle();
			if ((fake & Typeface.BOLD) != 0) {
				paint.setFakeBoldText(true);
			}

			if ((fake & Typeface.ITALIC) != 0) {
				paint.setTextSkewX(-0.25f);
			}

			paint.setTypeface(tf);
		}
	}
}
