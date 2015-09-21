package com.example.minesweeper;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.fullmindgames.minesweeper.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends FragmentActivity implements
		OnClickListener {

	private AdView mAdView;

	private GameManager gameManager;

	private int replacedId;
	int start_button_position;

	private ImageButton iBtn_back;

	Animation animation_hide;
	Animation animation_show;
	AnimationSet animSet;

	Fragment frg_starter;
	LevelFragment frg_level;

	private boolean firstStart;

	private final String KEY_BACK_BTN = "btn_visibility";
	private int btn_back_visibility;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		firstStart = savedInstanceState == null;
		if (firstStart)
			btn_back_visibility = View.GONE;
		else
			btn_back_visibility = savedInstanceState.getInt(KEY_BACK_BTN);

		gameManager = GameManager.initGameManager(getApplicationContext());
		FrameLayout fragmentsContainer = (FrameLayout) findViewById(R.id.fragments_container);
		replacedId = fragmentsContainer.getId();

		iBtn_back = (ImageButton) findViewById(R.id.iBtn_main_back);
		animation_hide = new TranslateAnimation(0, -100, 0, 0);
		animation_hide.setDuration(400);

		animation_show = new TranslateAnimation(-200, 0, 0, 0);
		animation_show.setDuration(400);
		animation_show.setFillAfter(true);

		Animation anim_fade = new AlphaAnimation(1, 0);
		anim_fade.setDuration(300);
		animSet = new AnimationSet(true);
		animSet.addAnimation(animation_hide);
		animSet.addAnimation(anim_fade);
		animSet.setFillAfter(true);

		iBtn_back.setVisibility(btn_back_visibility);
		iBtn_back.setOnClickListener(this);

		getSupportFragmentManager().addOnBackStackChangedListener(
				new FragmentManager.OnBackStackChangedListener() {

					@Override
					public void onBackStackChanged() {
						int i = getSupportFragmentManager()
								.getBackStackEntryCount();

						if (i < 1 && iBtn_back.getVisibility() == View.VISIBLE) {
							iBtn_back.startAnimation(animSet);
							iBtn_back.setVisibility(View.GONE);

						} else {
							iBtn_back.setVisibility(View.VISIBLE);
							iBtn_back.startAnimation(animation_show);
						}

					}
				});
		mAdView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(KEY_BACK_BTN, iBtn_back.getVisibility());
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (getSupportFragmentManager().findFragmentByTag("main_fragment") == null) {
			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			frg_starter = new StarterFragment();

			ft.replace(replacedId, frg_starter, "main_fragment");
			replacedId = frg_starter.getId();
			ft.commit();
		} else {
			frg_starter = getSupportFragmentManager().findFragmentByTag(
					"main_fragment");
			if (firstStart) {
				getSupportFragmentManager().popBackStack();
			}
		}

	}

	private boolean isLevelFragmentShown;

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
		case R.id.main_btn_start_game:
			isLevelFragmentShown = !isLevelFragmentShown;

			FragmentTransaction fts = getSupportFragmentManager()
					.beginTransaction();

			fts.setCustomAnimations(R.anim.slide_in_left,
					R.anim.slide_out_left, R.anim.slide_in_right,
					R.anim.slide_out_right);

			if (getSupportFragmentManager().findFragmentByTag("level_fragment") == null) {
				frg_level = new LevelFragment();
				fts.replace(replacedId, frg_level, "level_fragment");
				fts.addToBackStack(null);
			} else {
				fts.show(frg_level).commit();
			}

			replacedId = frg_level.getId();

			fts.commit();

			break;
		case R.id.iBtn_main_back:
			getSupportFragmentManager().popBackStack();
			replacedId = frg_starter.getId();
			break;
		case R.id.main_btn_beginner:
			gameManager.setLevel(GameManager.LEVEL_BEGINNER);
			overridePendingTransition(R.anim.slide_in_left,
					R.anim.slide_out_right);
			firstStart = true;
			break;

		case R.id.main_btn_intermediate:
			gameManager.setLevel(GameManager.LEVEL_INTERMEDIATE);
			overridePendingTransition(R.anim.slide_in_left,
					R.anim.slide_out_right);
			firstStart = true;
			break;

		case R.id.main_btn_expert:
			gameManager.setLevel(GameManager.LEVEL_EXPERT);
			overridePendingTransition(R.anim.slide_in_left,
					R.anim.slide_out_right);
			firstStart = true;
			break;
		case R.id.main_btn_custom:
			CustomLevelDialog dialog = CustomLevelDialog.newInstance();
			dialog.show(getSupportFragmentManager(), "custom_level");
			firstStart = true;
			break;
		case R.id.main_btn_rate:
			rate();
			break;
		case R.id.main_btn_bestTimes:
			BestTimesDialog bestTimesDialog = BestTimesDialog.newInstance();

			bestTimesDialog.show(getSupportFragmentManager(), "best_times");

			break;

		}

	}

	private void rate() {
		final Uri uri = Uri.parse("market://details?id=" + getPackageName());
		final Intent rateAppIntent = new Intent(Intent.ACTION_VIEW, uri);

		if (getPackageManager().queryIntentActivities(rateAppIntent, 0).size() > 0) {
			startActivity(rateAppIntent);
		} else {
			Toast.makeText(
					this,
					"Could not open Android market, please install the market app.",
					Toast.LENGTH_SHORT).show();
		}
	}

	AlertDialog settingsDialog;

}
