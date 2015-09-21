package com.example.minesweeper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.GridLayout.LayoutParams;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.Gravity;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fullmindgames.minesweeper.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class GameActivity extends FragmentActivity implements OnClickListener
		{

	protected final int ZOOM_IN = 1;
	protected final int ZOOM_OUT = -1;
	protected final int ZOOM_RATE = 3;

	private final int BG_EMPTY = 0;
	private final int BG_COVER = 9;
	private final int BG_FLAG = 10;
	private final int BG_MINE = 11;
	private final int BG_EXPLODE = 12;
	private final int BG_MARK = 13;
	private final int BG_WRONG = 14;
	private final int BG_MINE_END = 15;

	private GameManager gameManager;

	private TextView txt_mineLeft;
	private TextView txt_gameName;
	private ImageButton iBtn_flag;
	private ImageButton iBtn_newGame;
	private Chronometer timer;
	private RelativeLayout main_layout;

	private SparseIntArray drawableID;
	private SparseArray<Drawable> drawablesCache;
	private SparseIntArray colorsCache;

	private GridLayout gridLayout;
	private int cols;
	private int rows;
	private int mines;
	private int fields;
	private int uncoveredCells;
	private int explodedMines;

	private List<Cell> cells;
	private List<Integer> mineList;
	private List<Integer> flagedList;
	private boolean mark_mode;

	private boolean isRunning;
	private boolean firstClick;

	boolean vibrationEnabled;
	Vibrator v;
	private boolean soundsEnabled;

	boolean gameIsPaused = false;
	long pausedTime = 0;

	AdView adView_game;

	private ImageButton iBtn_back;
	private ImageButton iBtn_sound;
	private ImageButton iBtn_vibration;
	private ImageButton iBtn_zoom_in;
	private ImageButton iBtn_zoom_out;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.mine_sweeper_layout);

		gameManager = GameManager.getGameManager();
		main_layout = (RelativeLayout) findViewById(R.id.game_rl_main);

		iBtn_back = (ImageButton) findViewById(R.id.sittings_iBtn_back);
		iBtn_sound = (ImageButton) findViewById(R.id.sittings_iBtn_sound);
		iBtn_vibration = (ImageButton) findViewById(R.id.sittings_iBtn_vibration);
		iBtn_zoom_in = (ImageButton) findViewById(R.id.sittings_iBtn_zoom_in);
		iBtn_zoom_out = (ImageButton) findViewById(R.id.sittings_iBtn_zoom_out);

		txt_gameName = (TextView) findViewById(R.id.sittings_txt_title);
		txt_gameName.setTypeface(Typeface.createFromAsset(getAssets(),
				"dialogFont.otf"));
		txt_gameName.setText(R.string.app_name);

		iBtn_back.setOnClickListener(this);
		iBtn_sound.setOnClickListener(this);
		iBtn_vibration.setOnClickListener(this);
		iBtn_zoom_in.setOnClickListener(this);
		iBtn_zoom_out.setOnClickListener(this);

		iBtn_newGame = (ImageButton) findViewById(R.id.main_iBtn_newGame);
		iBtn_newGame.setOnClickListener(this);

		txt_mineLeft = (TextView) findViewById(R.id.main_txt_mineCount);
		iBtn_flag = (ImageButton) findViewById(R.id.main_iBtn_flag);
		timer = (Chronometer) findViewById(R.id.main_time);
		v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
		iBtn_flag.setOnClickListener(this);

		gridLayout = (GridLayout) findViewById(R.id.grid_layout);

		drawableID = new SparseIntArray();
		drawablesCache = new SparseArray<Drawable>();
		colorsCache = new SparseIntArray();
		setDrawables();
		cells = new ArrayList<Cell>();
		Bundle bundle = getIntent().getExtras();
		cols = bundle.getInt(GameManager.KEY_COLS);
		rows = bundle.getInt(GameManager.KEY_ROWS);
		mines = bundle.getInt(GameManager.KEY_MINES);
		flagedList = new ArrayList<Integer>();
		vibrationEnabled = gameManager
				.getSetting(GameManager.SETTINGS_VIBRATION_INDEX);
		setSittingsBg(GameManager.SETTINGS_VIBRATION_INDEX, iBtn_vibration);
		soundsEnabled = gameManager
				.getSetting(GameManager.SETTINGS_SOUND_INDEX);
		setSittingsBg(GameManager.SETTINGS_SOUND_INDEX, iBtn_sound);
		newGame();
		adView_game = (AdView) findViewById(R.id.adView_game);

		setAdView();
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		display = wm.getDefaultDisplay();
		setDisplayInfo();
	}

	private int display_width;
	private int display_height;
	Display display;

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public void setDisplayInfo() {
		Point size;
		int sdk = android.os.Build.VERSION.SDK_INT;
		if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			size = new Point(display.getWidth(), display.getHeight());
		} else {
			size = new Point();
			display.getSize(size);
		}
		display_width = size.x;
		display_height = size.y;
	}

	@Override
	protected void onPause() {
		if (isRunning) {
			gameIsPaused = true;
			pausedTime = timer.getBase() - SystemClock.elapsedRealtime();
			timer.stop();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		if (gameIsPaused) {
			timer.setBase(SystemClock.elapsedRealtime() + pausedTime);
			timer.start();
			gameIsPaused = false;
		}
		super.onResume();
	}

	private void setAdView() {
		main_layout.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {

					@SuppressWarnings("deprecation")
					@SuppressLint("NewApi")
					@Override
					public void onGlobalLayout() {
						int orientation = display.getRotation();
						int i = 0;
						if (orientation == Surface.ROTATION_90
								|| orientation == Surface.ROTATION_270)
							i = display_width;
						else
							i = display_height;
						if (main_layout.getHeight() <= i - 60) {
							adView_game.setVisibility(View.VISIBLE);
							AdRequest adRequest = new AdRequest.Builder()
									.build();
							adView_game.loadAd(adRequest);

						} else {
							adView_game.setVisibility(View.GONE);

						}

						if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
							main_layout.getViewTreeObserver()
									.removeOnGlobalLayoutListener(this);
						else
							main_layout.getViewTreeObserver()
									.removeGlobalOnLayoutListener(this);
					}
				});
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setAdView();
		Log.e("Game", "onConfigurationChanged");
	}

	private void setCellSize(int zoomRate) {
		int width = 0;
		for (Cell cell : cells) {
			ImageButton cellButton = cell.getImageButton();
			GridLayout.LayoutParams param = (LayoutParams) cellButton
					.getLayoutParams();
			if (zoomRate > 0)
				width = param.width + ZOOM_RATE;
			else
				width = param.width - ZOOM_RATE;
			if (width < gameManager.getCellSizeMin()
					|| width > gameManager.getCellSizeMax())
				return;
			param.width = width;
			param.height = width;
			cellButton.setLayoutParams(param);
		}
		setAdView();
		gameManager.saveCellSize(width);
	}

	public void markCell(Cell cell) {
		if (cell.checkState(FLAG_FLAGED)) {

			cell.removeState(FLAG_FLAGED);
			cell.addState(FLAG_MARKED);
			flagedList.remove(new Integer(cell.getLocation()));
		} else {
			if (cell.checkState(FLAG_MARKED)) {
				cell.removeState(FLAG_MARKED);

			} else {
				cell.addState(FLAG_FLAGED);
				flagedList.add(cell.getLocation());
			}
		}
		txt_mineLeft.setText("" + (mines - flagedList.size()));
		cell.drawCell();
	}

	public void openCell(Cell cell) {

		if (cell.checkState(FLAG_COVERED) && !cell.checkState(FLAG_FLAGED)) {
			if (cell.checkState(FLAG_MINE)) {

				cell.addState(FLAG_EXPLODED);
				explodedMines++;
				if (vibrationEnabled)
					v.vibrate(100);
			} else {
				cell.removeState(FLAG_COVERED);
				if (cell.checkState(FLAG_EMPTY)) {
					checkForEmptyNieghbor(cell);
				}
				uncoveredCells++;
			}

			cell.getImageButton().startAnimation(getBubbleAnim(0, 200));

			cell.drawCell();
			checkGameState();
		}
	}

	public Animation getBubbleAnim(long startOffset, long time) {

		ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1,
				ScaleAnimation.RELATIVE_TO_SELF, .5f,
				ScaleAnimation.RELATIVE_TO_SELF, .5f);
		scale.setInterpolator(new OvershootInterpolator());

		scale.setStartOffset(startOffset);
		scale.setDuration(time);
		return scale;
	}

	private void checkGameState() {
		if (uncoveredCells == fields - mines) {
			winGame();
		}
		if (explodedMines > 0) {
			loseGame();
		}
	}

	private void loseGame() {

		endGame();
		if (soundsEnabled)
			gameManager.playSound(GameManager.KEY_SOUND_EXPLODE);
		iBtn_newGame.setImageDrawable(getResources().getDrawable(
				R.drawable.face1));
		for (Integer rInt : mineList) {
			Cell cell = cells.get(rInt);
			if (!cell.checkState(FLAG_FLAGED)) {
				cell.removeState(FLAG_COVERED);

				cell.getImageButton().setAnimation(
						getBubbleAnim(2 * cell.getRow(), 200));
			}
			cell.drawCell();
		}
		for (Integer fInt : flagedList) {
			Cell cell = cells.get(fInt);
			if (!cell.checkState(FLAG_MINE))
				cell.addState(FLAG_WRONG);
			cell.drawCell();
		}
	}

	private void winGame() {
		long time = SystemClock.elapsedRealtime() - timer.getBase();
		endGame();
		int soundID = GameManager.KEY_SOUND_WIN;
		if (gameManager.isBestTime(time)) {
			soundID = GameManager.KEY_SOUND_HIGH_SCORE;
			gameManager.saveBestTime(time);
			//

			SimpleDateFormat df = new SimpleDateFormat("mm:ss");
			String timeStr = df.format(new Date(time));
			timer.setText(timeStr);
			showBestTimes();

		}
		if (soundsEnabled)
			gameManager.playSound(soundID);
		for (Cell cell : cells) {
			if (!cell.checkState(FLAG_FLAGED) && cell.checkState(FLAG_COVERED)) {
				if (cell.checkState(FLAG_MINE)) {
					if (cell.checkState(FLAG_MARKED))
						cell.removeState(FLAG_MARKED);
					cell.addState(FLAG_FLAGED);
				} else
					cell.removeState(FLAG_COVERED);

				cell.drawCell();
			}
		}
	}

	public void showBestTimes() {

		BestTimesDialog dialog = BestTimesDialog.newInstance();
		dialog.show(getSupportFragmentManager(), "best_times");
	}

	public void newGame() {

		fields = cols * rows;

		firstClick = true;
		uncoveredCells = 0;
		explodedMines = 0;
		flagedList.clear();
		txt_mineLeft.setText("" + mines);
		timer.stop();
		timer.setText("00:00");
		if (mark_mode)
			setMarkMode(false);
		iBtn_newGame.setImageDrawable(getResources().getDrawable(
				R.drawable.face0));

		isRunning = false;
		if (cells.size() > 0) {
			for (int i = 0; i < cells.size(); i++) {
				Cell cell = cells.get(i);
				cell.clearCell();
				cell.drawCell();
			}
		} else
			setFields();
	}

	private void endGame() {
		timer.stop();
		if (isRunning) {
			isRunning = false;
			if (mark_mode)
				setMarkMode(false);

		}
	}

	public void setMarkMode(boolean mode) {
		mark_mode = mode;
		if (mark_mode) {
			iBtn_flag.setBackgroundResource(R.drawable.flag_btn_bg_enable);
		} else {

			iBtn_flag.setBackgroundResource(R.drawable.flag_btn_bg);
		}
	}

	public void setDrawables() {
		for (int i = 1; i <= 8; i++) {
			String str = "bg" + i;
			int id = getResources().getIdentifier(str, "drawable",
					this.getPackageName());

			drawableID.append(i, id);
		}
		TypedArray ta = getResources().obtainTypedArray(R.array.color_array);
		for (int i = 0; i < ta.length(); i++)
			colorsCache.put(i + 1, ta.getColor(i, 0));

		ta.recycle();

		drawableID.append(BG_EMPTY, R.drawable.new_empty);

		drawableID.append(BG_COVER, R.drawable.new_cover);

		drawableID.append(BG_MINE, R.drawable.bg11);

		drawableID.append(BG_FLAG, R.drawable.bg10);

		drawableID.append(BG_EXPLODE, R.drawable.new_explode);

		drawableID.append(BG_MARK, R.drawable.mark);

		drawableID.append(BG_WRONG, R.drawable.newxml);

		drawableID.append(BG_MINE_END, R.drawable.new_mine_end);
	}

	public Drawable getMyDrawable(int key) {
		Drawable d = drawablesCache.get(key);

		if (d == null) {
			d = getResources().getDrawable(drawableID.get(key));
			drawablesCache.put(key, d);
		}

		return d;
	}

	public int getColor(int key) {

		return colorsCache.get(key);
	}

	private void setFields() {
		for (int position = 0, col = 0, row = 0; position < fields; position++, col++) {
			if (col == cols) {
				col = 0;
				row++;
			}
			Cell cell = new Cell(position, col, row);
			cells.add(cell);
			CellImageButton oImageView = cell.getImageView(this);
			oImageView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View view) {

					Cell cell = (Cell) view.getTag();
					if (firstClick) {
						isRunning = true;
						setRandomMinesPosition(cell.getLocation());
						firstClick = false;
						timer.setBase(SystemClock.elapsedRealtime());
						timer.start();
					}
					if (isRunning) {
						if (mark_mode && cell.checkState(FLAG_COVERED)) {
							markCell(cell);
						} else {
							openCell(cell);
						}
					}

				}
			});
			oImageView.setOnLongClickListener(new View.OnLongClickListener() {

				@Override
				public boolean onLongClick(View view) {
					if (isRunning && !firstClick) {
						Cell cell = (Cell) view.getTag();
						if (cell.checkState(FLAG_COVERED))
							markCell(cell);
						return true;
					} else
						return false;
				}
			});
			GridLayout.LayoutParams param = new GridLayout.LayoutParams();
			param.width = gameManager.getCellSize();
			param.height = gameManager.getCellSize();
			int padding = (int) getResources().getDimension(
					R.dimen.cell_padding);
			param.rightMargin = padding;
			param.topMargin = padding;
			param.bottomMargin = padding;
			param.leftMargin = padding;

			param.setGravity(Gravity.CENTER);
			param.columnSpec = GridLayout.spec(col);
			param.rowSpec = GridLayout.spec(row);
			oImageView.setLayoutParams(param);
			oImageView.setScaleType(ScaleType.FIT_CENTER);

			cell.drawCell();
			gridLayout.addView(oImageView);
		}

	}

	public void setRandomMinesPosition(int firstCell) {
		mineList = new ArrayList<Integer>();
		Random r = new Random();

		for (int i = 0; i < mines; i++) {
			int rInt = r.nextInt(fields);
			if (mineList.contains(rInt) || rInt == firstCell) {
				i--;
			} else {
				mineList.add(rInt);
				Cell cell = cells.get(rInt);
				cell.removeState(FLAG_EMPTY);
				cell.addState(FLAG_MINE);
				cell.destriputeMineCount();
			}
		}
	}

	
	public void checkForEmptyNieghbor(Cell cell) {

		for (int i = 0; i < 8; i++) {
			int cell_location = cell.getNaoughbor(i);
			if (cell_location >= 0) {
				final Cell c = cells.get(cell_location);
				if (!c.checkState(FLAG_MINE)
						&& c.getLocation() != cell.getLocation()
						&& c.checkState(FLAG_COVERED)
						&& !c.checkState(FLAG_FLAGED)) {
					c.removeState(FLAG_COVERED);

					c.getImageButton().setAnimation(
							getBubbleAnim(2 * cell.getRow(), 200));

					c.drawCell();
					uncoveredCells++;

					if (c.checkState(FLAG_EMPTY))
						checkForEmptyNieghbor(c);
				}
			}
		}
	}

	public static final int FLAG_COVERED = 0x01;
	public static final int FLAG_MINE = 0x02;
	public static final int FLAG_EMPTY = 0x04;
	public static final int FLAG_FLAGED = 0x08;
	public static final int FLAG_EXPLODED = 0x10;
	public static final int FLAG_MARKED = 0x20;
	public static final int FLAG_WRONG = 0x40;

	@Override
	protected void onDestroy() {

		if (cells.size() > 0) {
			for (int i = 0; i < cells.size(); i++)
				cells.get(i).disposeCell();
		}
		cells.clear();

		gridLayout.removeAllViews();
		super.onStop();
	}

	public class Cell {
		private int location;
		private int col_position;
		private int row_position;

		private int flagState;
		private int intState;

		public void addState(int state) {
			flagState |= state;
		}

		public void removeState(int state) {
			flagState &= ~state;
		}

		public boolean checkState(int state) {
			return (flagState & state) == state;
		}

		private CellImageButton iBtn_cell;

		public Cell(int location, int col_position, int row_position) {
			this.col_position = col_position;
			this.row_position = row_position;
			this.location = location;
			flagState = FLAG_COVERED | FLAG_EMPTY;

		}

		public CellImageButton getImageButton() {
			return iBtn_cell;
		}

		public CellImageButton getImageView(Context context) {
			if (iBtn_cell == null) {
				iBtn_cell = new CellImageButton(context);

			}

			iBtn_cell.setTag(this);
			return iBtn_cell;
		}

		@SuppressLint("NewApi")
		@SuppressWarnings("deprecation")
		public void drawCell() {
			List<Drawable> ds = new ArrayList<Drawable>();

			if (checkState(FLAG_COVERED)) {
				ds.add(getMyDrawable(BG_COVER));
				if (checkState(FLAG_FLAGED)) {
					ds.add(getMyDrawable(BG_FLAG));

					if (checkState(FLAG_WRONG))
						ds.add(getMyDrawable(BG_WRONG));
				}
				if (checkState(FLAG_MARKED))
					ds.add(getMyDrawable(BG_MARK));
			} else {
				if (checkState(FLAG_MINE)) {
					if (checkState(FLAG_EXPLODED)) {
						ds.add(getMyDrawable(BG_EXPLODE));
					} else
						ds.add(getMyDrawable(BG_MINE_END));

					ds.add(getMyDrawable(BG_MINE));
				} else {
					ds.add(getMyDrawable(BG_EMPTY));
					if (!checkState(FLAG_EMPTY))
						iBtn_cell.setText(String.valueOf(intState),
								getColor(intState));

				}

			}
			int sdk = android.os.Build.VERSION.SDK_INT;
			if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
				iBtn_cell.setBackgroundDrawable(ds.remove(0));
			} else {
				iBtn_cell.setBackground(ds.remove(0));
			}
			iBtn_cell.setImageDrawable(new LayerDrawable(ds
					.toArray(new Drawable[0])));

		}

		public void disposeCell() {
			iBtn_cell = null;

		}

		public void clearCell() {
			flagState = 0;
			intState = 0;
			iBtn_cell.disableText();
			flagState = FLAG_COVERED | FLAG_EMPTY;
		}

		public int getCol() {
			return col_position;
		}

		public int getRow() {
			return row_position;
		}

		private int getNaoughbor(int i) {

			switch (i) {
			case 0:
				return (row_position - 1) >= 0 && col_position - 1 >= 0 ? location
						- cols - 1
						: -1;
			case 1:
				return (row_position - 1) >= 0 ? location - cols : -1;
			case 2:
				return (row_position - 1) >= 0 && col_position + 1 < cols ? location
						- cols + 1
						: -1;
			case 3:
				return col_position - 1 >= 0 ? location - 1 : -1;
			case 4:
				return col_position + 1 < cols ? location + 1 : -1;
			case 5:
				return (row_position + 1) < rows && col_position - 1 >= 0 ? location
						+ cols - 1
						: -1;
			case 6:
				return (row_position + 1) < rows ? location + cols : -1;
			case 7:
				return (row_position + 1) < rows && col_position + 1 < cols ? location
						+ cols + 1
						: -1;
			}
			return -1;
		}

		public void destriputeMineCount() {
			for (int i = 0; i < 8; i++) {
				int cell_location = getNaoughbor(i);
				if (cell_location >= 0) {
					Cell cell = cells.get(cell_location);
					if (!cell.checkState(FLAG_MINE)) {
						cell.addIntState();
						cell.removeState(FLAG_EMPTY);
					}
				}
			}
		}

		public void addIntState() {
			intState++;
		}

		public int getIntState() {
			return intState;
		}

		public int getLocation() {
			return this.location;
		}

	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public void setSittingsBg(int who, View v) {
		boolean keyEnabled = false;
		switch (who) {
		case GameManager.SETTINGS_SOUND_INDEX:
			v = iBtn_sound;
			keyEnabled = soundsEnabled;
			break;
		case GameManager.SETTINGS_VIBRATION_INDEX:
			v = iBtn_vibration;
			keyEnabled = vibrationEnabled;
			break;

		}
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			v.setBackgroundDrawable((keyEnabled ? getResources().getDrawable(
					R.drawable.ring_enable) : getResources().getDrawable(
					R.drawable.ring_disable)));
		} else {

			v.setBackground(keyEnabled ? getResources().getDrawable(
					R.drawable.ring_enable) : getResources().getDrawable(
					R.drawable.ring_disable));
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.main_iBtn_flag:
			setMarkMode(!mark_mode);
			break;
		case R.id.main_iBtn_newGame:
			newGame();
			break;
		case R.id.sittings_iBtn_back:
			finish();
			break;
		case R.id.sittings_iBtn_sound:
			soundsEnabled = !soundsEnabled;
			gameManager.saveSettings(GameManager.SETTINGS_SOUND_INDEX,
					soundsEnabled);

			setSittingsBg(GameManager.SETTINGS_SOUND_INDEX, v);
			break;
		case R.id.sittings_iBtn_vibration:
			vibrationEnabled = !vibrationEnabled;
			gameManager.saveSettings(GameManager.SETTINGS_VIBRATION_INDEX,
					vibrationEnabled);
			setSittingsBg(GameManager.SETTINGS_VIBRATION_INDEX, v);

			break;
		case R.id.sittings_iBtn_zoom_in:
			setCellSize(ZOOM_IN);
			break;
		case R.id.sittings_iBtn_zoom_out:
			setCellSize(ZOOM_OUT);
			break;

		}

	}

}
