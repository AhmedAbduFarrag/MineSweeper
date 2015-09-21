package com.example.minesweeper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ImageButton;

public class CellImageButton extends ImageButton {

	private String text;
	private boolean hasText;
	private Paint fillPaint;
	private Paint strokePaint;

	private int textColor;

	public void setText(String text, int color) {
		hasText = true;
		this.text = text;
		this.textColor = color;
		if (fillPaint == null)
			initTextPaint();
	}

	private void initTextPaint() {

		fillPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG
				| Paint.FAKE_BOLD_TEXT_FLAG);
		fillPaint.setColor(textColor);
		fillPaint.setStyle(Paint.Style.FILL);
		fillPaint.setTypeface(Typeface.MONOSPACE);
		fillPaint.setTextAlign(Paint.Align.LEFT);

		strokePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG
				| Paint.FAKE_BOLD_TEXT_FLAG);
		strokePaint.setStyle(Paint.Style.STROKE);
		strokePaint.setColor(Color.BLACK);
		strokePaint.setTypeface(Typeface.MONOSPACE);
		strokePaint.setTextAlign(Paint.Align.LEFT);
		strokePaint.setStrokeWidth(0.8f);
	}

	public void disableText() {
		hasText = false;
		fillPaint = null;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (hasText) {
			float textSize = getMeasuredHeight() - 4;
			fillPaint.setTextSize(textSize);
			strokePaint.setTextSize(textSize);

			float x = (getMeasuredWidth() / 2 - fillPaint.measureText(text) / 2);

			float y = ((getMeasuredHeight() / 2) - ((fillPaint.descent() + fillPaint
					.ascent()) / 2));
			canvas.drawText(text, x, y, strokePaint);
			canvas.drawText(text, x, y, fillPaint);
		}
	}

	public float convertDpToPixel(float dp) {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		return dp * (metrics.densityDpi / 160f);
	}

	public float convertPixelsToDp(float px) {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		return px / (metrics.densityDpi / 160f);
	}

	public CellImageButton(Context context) {
		super(context);

	}

	public CellImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CellImageButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(getMeasuredHeight(), getMeasuredHeight());
	}

}
