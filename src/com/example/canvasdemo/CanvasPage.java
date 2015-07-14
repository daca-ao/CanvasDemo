package com.example.canvasdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
/**
 * 
 * @author aohuijun
 *
 */
public class CanvasPage extends Activity {

	private Bitmap baseBitmap;
	private Canvas canvas;
	private Paint paint;
	private Paint rubber;
	
	private ImageView drawBoard;
	private ImageView moreButton, selectedDoneButton;
	private LinearLayout moreFunctionsLayout;
	private ImageView penView, backgroundView, rubberView, clearView;
	private ImageView[] colorViews = new ImageView[12];
	private int[] colorButtons = {R.id.change_color0, R.id.change_color1, R.id.change_color2, R.id.change_color3, 
								R.id.change_color4, R.id.change_color5, R.id.change_color6, R.id.change_color7, 
								R.id.change_color8, R.id.change_color9, R.id.change_colora, R.id.change_colorb};
	private SeekBar penSeekBar, rubberSeekBar;
	
	private String[] colors;
	private int selectedMode = 0;
	private boolean isSelected = false;
	
	private RotateAnimation rotateAnimation;
	
	public final static int CHOOSE_PEN = 0;
	public final static int CHOOSE_BACK = 1;
	public final static int CHOOSE_RUBBER = 2;
	public final static int CHOOSE_CLEAR = 3;
	
	public final static int INIT_PEN_SIZE = 5;
	public final static int INIT_RUBBER_SIZE = 30;
	
	public final static int WHITE = 0;
	public final static int DARK_RED = 1;
	public final static int BRIGHT_RED = 2;
	public final static int PINK = 3;
	public final static int YELLOW = 4;
	public final static int ORANGE = 5;
	public final static int BLACK = 6;
	public final static int CYAN = 7;
	public final static int MINT = 8;
	public final static int PURPLE = 9;
	public final static int HORIZON = 10;
	public final static int GREEN = 11;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas_page);
        initTools();
        initColors();
        initCanvas();
        
    }

	private void initTools() {
		
		rotateAnimation = (RotateAnimation)AnimationUtils.loadAnimation(this, R.anim.reverse_anim);
		LinearInterpolator interpolator = new LinearInterpolator();
		rotateAnimation.setInterpolator(interpolator);
		
		moreButton = (ImageView)findViewById(R.id.canvas_more_button);
		
		moreFunctionsLayout = (LinearLayout)findViewById(R.id.canvas_more_functions);
		penView = (ImageView)findViewById(R.id.canvas_pen);
		backgroundView = (ImageView)findViewById(R.id.canvas_background);
		rubberView = (ImageView)findViewById(R.id.canvas_eraser);
		clearView = (ImageView)findViewById(R.id.canvas_clear);
		penSeekBar = (SeekBar)findViewById(R.id.canvas_pen_seekbar);
		rubberSeekBar = (SeekBar)findViewById(R.id.canvas_rubber_seekbar);
		selectedDoneButton = (ImageView)findViewById(R.id.canvas_done);
		penView.setImageDrawable(getResources().getDrawable(R.drawable.pentool_selected));

		penView.setOnClickListener(new ModeSelectedListener(CHOOSE_PEN));
		backgroundView.setOnClickListener(new ModeSelectedListener(CHOOSE_BACK));
		rubberView.setOnClickListener(new ModeSelectedListener(CHOOSE_RUBBER));
		clearView.setOnClickListener(new ModeSelectedListener(CHOOSE_CLEAR));
		penSeekBar.setOnSeekBarChangeListener(new StyleChangeListener(CHOOSE_PEN));
		rubberSeekBar.setOnSeekBarChangeListener(new StyleChangeListener(CHOOSE_RUBBER));
		
		moreButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setMoreFunctions(false);
			}
		});
		selectedDoneButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setMoreFunctions(true);
			}
		});
		
	}

	private void initColors() {
		colors = getResources().getStringArray(R.array.color_array);
		
		for (int i = 0; i < colors.length; i++) {
			colorViews[i] = (ImageView)findViewById(colorButtons[i]);
			colorViews[i].setOnClickListener(new ColorChangeListener(i));
		}
		
	}
	

	private void initCanvas() {
		paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(INIT_PEN_SIZE);
		roundPaintEdge(paint);
		
		rubber = new Paint();
		rubber.setAlpha(0);
		rubber.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		rubber.setStrokeWidth(INIT_RUBBER_SIZE);
		roundPaintEdge(rubber);
		
		drawBoard = (ImageView)findViewById(R.id.canvas_draw);
    	drawBoard.setOnTouchListener(new View.OnTouchListener() {
			
			float startX;
			float startY;
			
			@SuppressLint("ClickableViewAccessibility") 
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (baseBitmap == null) {
						baseBitmap = Bitmap.createBitmap(drawBoard.getWidth(), drawBoard.getHeight(), Bitmap.Config.ARGB_8888);
						canvas = new Canvas(baseBitmap);
						canvas.drawColor(Color.TRANSPARENT);
					}
					startX = event.getX();
					startY = event.getY();
					break;
				case MotionEvent.ACTION_MOVE:
					float stopX = event.getX();
					float stopY = event.getY();
					if (selectedMode == CHOOSE_PEN) {
						canvas.drawLine(startX, startY, stopX, stopY, paint);
					} else if (selectedMode == CHOOSE_RUBBER) {
						canvas.drawLine(startX, startY, stopX, stopY, rubber);
					}
					startX = event.getX();
					startY = event.getY();
					drawBoard.setImageBitmap(baseBitmap);
					break;
				default:
					break;
				}
				return true;
			}
		});

	}
	
	private void roundPaintEdge(Paint paint) {
		if(null != paint){
			paint.setAntiAlias(true);
			paint.setDither(true);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeJoin(Paint.Join.ROUND);
			paint.setStrokeCap(Paint.Cap.ROUND);
			paint.setPathEffect(new CornerPathEffect(paint.getStrokeWidth()));
		}
	}

	public class ModeSelectedListener implements OnClickListener {

		private int mode;
		public ModeSelectedListener(int mode) {
			this.mode = mode;
		}

		@Override
		public void onClick(View v) {
			switch (mode) {
			case CHOOSE_PEN:
				penView.setImageDrawable(getResources().getDrawable(R.drawable.pentool_selected));
				backgroundView.setImageDrawable(getResources().getDrawable(R.drawable.change_background_color_nomal));
				rubberView.setImageDrawable(getResources().getDrawable(R.drawable.rubbertool_normal));
				selectedMode = mode;
				break;
			case CHOOSE_BACK:
				penView.setImageDrawable(getResources().getDrawable(R.drawable.pentool_normal));
				backgroundView.setImageDrawable(getResources().getDrawable(R.drawable.change_background_color_selected));
				rubberView.setImageDrawable(getResources().getDrawable(R.drawable.rubbertool_normal));
				selectedMode = mode;
				break;
			case CHOOSE_RUBBER:
				penView.setImageDrawable(getResources().getDrawable(R.drawable.pentool_normal));
				backgroundView.setImageDrawable(getResources().getDrawable(R.drawable.change_background_color_nomal));
				rubberView.setImageDrawable(getResources().getDrawable(R.drawable.rubbertool_selected));
				selectedMode = mode;
				break;
			case CHOOSE_CLEAR:
				if (baseBitmap != null) {
					baseBitmap = Bitmap.createBitmap(drawBoard.getWidth(), drawBoard.getHeight(), Bitmap.Config.ARGB_8888);
					canvas = new Canvas(baseBitmap);
					canvas.drawColor(Color.TRANSPARENT);
					drawBoard.setImageBitmap(baseBitmap);
				}
				break;
			default:
				break;
			}
		}
		
	}
	
	public class ColorChangeListener implements OnClickListener {

		private int colorIndex;
		public ColorChangeListener(int index) {
			colorIndex = index;
		}

		@Override
		public void onClick(View v) {
			switch (selectedMode) {
			case CHOOSE_PEN:
				paint.setColor(Color.parseColor(colors[colorIndex]));
				break;
			case CHOOSE_BACK:
				drawBoard.setBackgroundColor(Color.parseColor(colors[colorIndex]));
				break;
			default:
				break;
			}
		}
		
	}
	
	public class StyleChangeListener implements OnSeekBarChangeListener {

		private int mode;
		public StyleChangeListener(int mode) {
			this.mode = mode;
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			switch (mode) {
			case CHOOSE_PEN:
				paint.setStrokeWidth(progress);
				break;
			case CHOOSE_RUBBER:
				rubber.setStrokeWidth(progress);
				break;
			default:
				break;
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private void setMoreFunctions(boolean b) {
		this.isSelected = b;
		if (!isSelected) {
			moreFunctionsLayout.setVisibility(View.VISIBLE);
			moreButton.startAnimation(rotateAnimation);
			moreButton.setVisibility(View.INVISIBLE);
			isSelected = true;
		} else if (isSelected) {
			moreFunctionsLayout.setVisibility(View.INVISIBLE);
			moreButton.clearAnimation();
			moreButton.setVisibility(View.VISIBLE);
			isSelected = false;
		}

	}
}
