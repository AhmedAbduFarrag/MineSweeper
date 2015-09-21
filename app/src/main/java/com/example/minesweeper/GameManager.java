package com.example.minesweeper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.WindowManager;

import com.fullmindgames.minesweeper.R;

public class GameManager {
	private static GameManager manager;
	private SharedPreferences pref;
	private Context context;
	public static final int LEVEL_BEGINNER = 0;
	public static final int LEVEL_INTERMEDIATE = 1;
	public static final int LEVEL_EXPERT = 2;
	public static final int LEVEL_CUSTOM = 3;

	public static final String KEY_LEVEL = "level";

	public static final String KEY_COLS = "cols";
	public static final String KEY_ROWS = "rows";
	public static final String KEY_MINES = "mines";
	public static final String KEY_BEST_TIME = "best_time";
	private static final String PREF_NAME = "game_preferences";
	private static final String PREF_KEY_BEST_BEGINNER = "best_beginner";
	private static final String PREF_KEY_BEST_INTERMEDIATE = "best_intermediate";
	private static final String PREF_KEY_BEST_EXPERT = "best_expert";

	private static final String PREF_KEY_CELL_SIZE = "cell_size";

	private static final String PREF_KEY_SOUND = "sound";
	private static final String PREF_KEY_VIBRATION = "vibration";

	public static final int SETTINGS_SOUND_INDEX = 0;
	public static final int SETTINGS_VIBRATION_INDEX = 1;

	protected static final String PREF_KEY_CUSTOM_ROWS = "custom_rows";
	protected static final String PREF_KEY_CUSTOM_COLS = "custom_cols";
	private static final String PREF_KEY_CUSTOM_MINES = "custom_mines";

	public static final int KEY_CUSTOM_COLS_INDEX = 0;
	public static final int KEY_CUSTOM_ROWS_INDEX = 1;
	public static final int KEY_CUSTOM_MINES_INDEX = 2;

	public final static int KEY_SOUND_HIGH_SCORE = 0;
	public final static int KEY_SOUND_WIN = 1;
	public final static int KEY_SOUND_EXPLODE = 2;
	private static final long DEFUALT_TIME = 3600000;

	private SoundPool soundPool;

	private SparseIntArray sounds_ids;
	private int level;
	private long bestTime;

	private int cols;
	private int rows;
	private int mines;
	private int cellSize;

	private SparseBooleanArray settings_;

	private GameManager(Context context) {
		this.context = context;
		pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

		settings_ = new SparseBooleanArray();

		cellSize = pref.getInt(PREF_KEY_CELL_SIZE, (int) context.getResources()
				.getDimension(R.dimen.cell_width));
		settings_.put(SETTINGS_SOUND_INDEX,
				pref.getBoolean(PREF_KEY_SOUND, true));
		settings_.put(SETTINGS_VIBRATION_INDEX,
				pref.getBoolean(PREF_KEY_VIBRATION, true));

		sounds_ids = new SparseIntArray();
		soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
		setGameSounds();

	}

	public int getCellSize() {

		return cellSize;
	}

	public void saveCellSize(int newSize) {
		cellSize = newSize;
		pref.edit().putInt(PREF_KEY_CELL_SIZE, cellSize).commit();
	}

	// public Point getDisplaySize() {
	// if (display_width <= 0)
	// setDisplayInfo();
	// return new Point(display_width, display_height);
	// }

	public int getCellSizeMin() {
		return (int) context.getResources()
				.getDimension(R.dimen.cell_width_min);
	}

	public int getCellSizeMax() {
		return (int) context.getResources()
				.getDimension(R.dimen.cell_width_max);
	}

	public void setGameSounds() {

		sounds_ids.put(KEY_SOUND_HIGH_SCORE,
				soundPool.load(context, R.raw.high_score, 1));
		sounds_ids.put(KEY_SOUND_WIN, soundPool.load(context, R.raw.win, 1));
		sounds_ids.put(KEY_SOUND_EXPLODE,
				soundPool.load(context, R.raw.explode2, 1));

	}

	public void playSound(int soundId) {
		soundPool.play(sounds_ids.get(soundId), 1, 1, 1, 0, 1.0f);
	}

	public static GameManager initGameManager(Context context) {
		if (manager == null)
			manager = new GameManager(context);
		return manager;
	}

	public static GameManager getGameManager() {
		return manager;
	}

	public boolean getSetting(int settings_index) {
		return settings_.get(settings_index);
	}

	public void saveSettings(int settings_index, boolean value) {
		settings_.delete(settings_index);
		settings_.append(settings_index, value);
		String key = "";
		switch (settings_index) {
		case SETTINGS_SOUND_INDEX:
			key = PREF_KEY_SOUND;
			break;
		case SETTINGS_VIBRATION_INDEX:
			key = PREF_KEY_VIBRATION;
			break;
		}
		pref.edit().putBoolean(key, value).commit();
	}

	public void saveBestTime(long time) {
		bestTime = time;
		String key = "";
		switch (level) {
		case LEVEL_BEGINNER:
			key = PREF_KEY_BEST_BEGINNER;
			break;
		case LEVEL_INTERMEDIATE:
			key = PREF_KEY_BEST_INTERMEDIATE;
			break;
		case LEVEL_EXPERT:
			key = PREF_KEY_BEST_EXPERT;
			break;
		}
		pref.edit().putLong(key, time).commit();
	}

	public long getBestTime(int level) {
		switch (level) {
		case LEVEL_BEGINNER:
			return pref.getLong(PREF_KEY_BEST_BEGINNER, DEFUALT_TIME);
		case LEVEL_INTERMEDIATE:
			return pref.getLong(PREF_KEY_BEST_INTERMEDIATE, DEFUALT_TIME);
		case LEVEL_EXPERT:
			return pref.getLong(PREF_KEY_BEST_EXPERT, DEFUALT_TIME);
		}
		return 0;
	}

	public void setLevel(int lvl) {
		this.level = lvl;
		switch (level) {
		case LEVEL_BEGINNER:
			cols = 9;
			rows = 9;
			mines = 10;
			break;
		case LEVEL_INTERMEDIATE:
			cols = 16;
			rows = 16;
			mines = 44;
			break;
		case LEVEL_EXPERT:
			cols = 30;
			rows = 16;
			mines = 99;
			break;

		}
		startGameActivity();
	}

	public void setCustomLevel(int cols, int rows, int mines) {
		level = LEVEL_CUSTOM;
		if (rows < 9)
			this.rows = 9;
		else if (rows > 30)
			this.rows = 30;
		else
			this.rows = rows;

		if (cols < 9)
			this.cols = 9;
		else if (cols > 50)
			this.cols = 50;
		else
			this.cols = cols;

		if (cols == 9 && rows == 9)
			this.mines = 10;
		else if (mines < (this.cols * this.rows) / 8)
			this.mines = this.cols * this.rows / 8;
		else if (mines > (this.cols * this.rows) - 15)
			this.mines = this.cols * this.rows - 15;
		else
			this.mines = mines;
		pref.edit().putInt(PREF_KEY_CUSTOM_COLS, this.cols)
				.putInt(PREF_KEY_CUSTOM_ROWS, this.rows)
				.putInt(PREF_KEY_CUSTOM_MINES, this.mines).commit();
		startGameActivity();
	}

	public int getSavedCustomLevel(int key) {
		switch (key) {
		case KEY_CUSTOM_COLS_INDEX:
			return pref.getInt(PREF_KEY_CUSTOM_COLS, 9);
		case KEY_CUSTOM_ROWS_INDEX:
			return pref.getInt(PREF_KEY_CUSTOM_ROWS, 9);
		case KEY_CUSTOM_MINES_INDEX:
			return pref.getInt(PREF_KEY_CUSTOM_MINES, 10);
		}
		return 0;
	}

	public void startGameActivity() {
		Intent intent = new Intent(context, GameActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Bundle bundle = new Bundle();
		bundle.putInt(KEY_COLS, cols);
		bundle.putInt(KEY_ROWS, rows);
		bundle.putInt(KEY_MINES, mines);
		bundle.putLong(KEY_BEST_TIME, bestTime);
		intent.putExtras(bundle);
		context.startActivity(intent);

	}

	public int getLevel() {
		return level;
	}

	public boolean isBestTime(long time) {
		if (level == LEVEL_CUSTOM)
			return false;

		return time < getBestTime(level);
	}

	public String getLevelString(int level) {
		switch (level) {
		case LEVEL_BEGINNER:
			return "Beginner";
		case LEVEL_INTERMEDIATE:
			return "Intermediate";
		case LEVEL_EXPERT:
			return "Expert";
		case LEVEL_CUSTOM:
			return "Custom";
		}
		return "";
	}

	public void resetBestTime(int level) {
		String key = "";
		switch (level) {
		case LEVEL_BEGINNER:
			key = PREF_KEY_BEST_BEGINNER;
			break;
		case LEVEL_INTERMEDIATE:
			key = PREF_KEY_BEST_INTERMEDIATE;
			break;
		case LEVEL_EXPERT:
			key = PREF_KEY_BEST_EXPERT;
			break;
		}
		pref.edit().putLong(key, DEFUALT_TIME).commit();
	}

}
