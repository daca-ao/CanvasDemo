package com.example.canvasdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SeekBar;
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
	private ImageView penView, backgroundView, rubberView, clearView;
	private ImageView color0, color1, color2, color3, color4, color5, color6, color7, color8, color9, colorA, colorB;
	private SeekBar penSeekBar, rubberSeekBar;
	
	private int selectedMode = 0;
	private String[] colors;
	
	private final static int CHOOSE_PEN = 0;
	private final static int CHOOSE_BACK = 1;
	private final static int CHOOSE_RUBBER = 2;
	private final static int CHOOSE_CLEAR = 3;
	
	private final static int WHITE = 0;
	private final static int DARK_RED = 1;
	private final static int BRIGHT_RED = 2;
	private final static int PINK = 3;
	private final static int YELLOW = 4;
	private final static int ORANGE = 5;
	private final static int BLACK = 6;
	private final static int CYAN = 7;
	private final static int MINT = 8;
	private final static int PURPLE = 9;
	private final static int HORIZON = 10;
	private final static int GREEN = 11;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas_page);
        initTools();
        initColors();
        initCanvas();
        
    }

	private void initTools() {
		penView = (ImageView)findViewById(R.id.canvas_pen);
		backgroundView = (ImageView)findViewById(R.id.canvas_background);
		rubberView = (ImageView)findViewById(R.id.canvas_eraser);
		clearView = (ImageView)findViewById(R.id.canvas_clear);
		penSeekBar = (SeekBar)findViewById(R.id.canvas_pen_seekbar);
		rubberSeekBar = (SeekBar)findViewById(R.id.canvas_rubber_seekbar);
		
		penView.setImageDrawable(getResources().getDrawable(R.drawable.pentool_selected));

		penView.setOnClickListener(new ModeSelectedListener(CHOOSE_PEN));
		backgroundView.setOnClickListener(new ModeSelectedListener(CHOOSE_BACK));
		rubberView.setOnClickListener(new ModeSelectedListener(CHOOSE_RUBBER));
		clearView.setOnClickListener(new ModeSelectedListener(CHOOSE_CLEAR));

	}

	private void initColors() {
		colors = getResources().getStringArray(R.array.color_array);
		
		color0 = (ImageView)findViewById(R.id.change_color0);
		color1 = (ImageView)findViewById(R.id.change_color1);
		color2 = (ImageView)findViewById(R.id.change_color2);
		color3 = (ImageView)findViewById(R.id.change_color3);
		color4 = (ImageView)findViewById(R.id.change_color4);
		color5 = (ImageView)findViewById(R.id.change_color5);
		color6 = (ImageView)findViewById(R.id.change_color6);
		color7 = (ImageView)findViewById(R.id.change_color7);
		color8 = (ImageView)findViewById(R.id.change_color8);
		color9 = (ImageView)findViewById(R.id.change_color9);
		colorA = (ImageView)findViewById(R.id.change_colora);
		colorB = (ImageView)findViewById(R.id.change_colorb);
		
		color0.setOnClickListener(new ColorChangeListener(WHITE));
		color1.setOnClickListener(new ColorChangeListener(DARK_RED));
		color2.setOnClickListener(new ColorChangeListener(BRIGHT_RED));
		color3.setOnClickListener(new ColorChangeListener(PINK));
		color4.setOnClickListener(new ColorChangeListener(YELLOW));
		color5.setOnClickListener(new ColorChangeListener(ORANGE));
		color6.setOnClickListener(new ColorChangeListener(BLACK));
		color7.setOnClickListener(new ColorChangeListener(CYAN));
		color8.setOnClickListener(new ColorChangeListener(MINT));
		color9.setOnClickListener(new ColorChangeListener(PURPLE));
		colorA.setOnClickListener(new ColorChangeListener(HORIZON));
		colorB.setOnClickListener(new ColorChangeListener(GREEN));
		
	}
	

	private void initCanvas() {
    	drawBoard = (ImageView)findViewById(R.id.canvas_draw);
		baseBitmap = Bitmap.createBitmap(1080, 867, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(baseBitmap);
		canvas.drawColor(Color.TRANSPARENT);

		paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(5);
		paint.setAntiAlias(true);
		paint.setDither(true);
		
		rubber = new Paint();
		rubber.setAlpha(0);
		rubber.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		rubber.setStrokeWidth(30);
		rubber.setAntiAlias(true);
		rubber.setDither(true);
		
		canvas.drawBitmap(baseBitmap, new Matrix(), paint);
		drawBoard.setImageBitmap(baseBitmap);
		drawBoard.setOnTouchListener(new View.OnTouchListener() {
			
			float startX;
			float startY;
			
			@SuppressLint("ClickableViewAccessibility") 
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
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
}