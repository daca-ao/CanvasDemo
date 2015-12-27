package com.example.canvasdemo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
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

	public final static int EDIT_PEN = 0;
	public final static int EDIT_BGD = 1;
	public final static int EDIT_RUBBER = 2;
	
	public final static int INIT_PEN_SIZE = 5;
	public final static int INIT_RUBBER_SIZE = 30;
	public final static int NUM_COLOR = 12;
	
	public final static int WHITE = 0;
	public final static int BLACK = 6;
	
	private PaintBoardView mPaintBoardView;
	private ImageView moreButton, selectedDoneButton;
	private View mFunctionsView;
	private LinearLayout mFunctionsLayout;
	private ImageView mPenButton, mBgdButton, mRubberButton;
	
	private String[] colors;
	private ImageView[] colorViews = new ImageView[NUM_COLOR];
	private int[] colorButtons = {R.id.change_color0, R.id.change_color1, R.id.change_color2, R.id.change_color3, 
								R.id.change_color4, R.id.change_color5, R.id.change_color6, R.id.change_color7, 
								R.id.change_color8, R.id.change_color9, R.id.change_colora, R.id.change_colorb};
	private SeekBar penSeekBar, rubberSeekBar;
	
	private int mEditMode = 0;
	private int penPrevState = -1;
	private int backPrevState = -1;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas_page);
        initTools();
        initColors();        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.menu_canvas, menu);
    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	int id = item.getItemId();
    	switch (id) {
			case R.id.canvas_undo:
				mPaintBoardView.undo();
				break;
			case R.id.canvas_redo:
				mPaintBoardView.redo();
				break;
			case R.id.canvas_clear:
				mPaintBoardView.clear();
				break;
			default:
				break;
		}
    	return super.onOptionsItemSelected(item);
    }
    
	private void initTools() {
		
		mPaintBoardView = (PaintBoardView) findViewById(R.id.canvas_draw);
		moreButton = (ImageView)findViewById(R.id.canvas_more_button);
		
		mFunctionsView = View.inflate(getApplicationContext(), R.layout.canvas_functions, null);
		mFunctionsLayout = (LinearLayout) mFunctionsView.findViewById(R.id.canvas_functions);
		mPenButton = (ImageView) mFunctionsView.findViewById(R.id.canvas_pen);
		mBgdButton = (ImageView) mFunctionsView.findViewById(R.id.canvas_background);
		mRubberButton = (ImageView) mFunctionsView.findViewById(R.id.canvas_rubber);
		
		penSeekBar = (SeekBar) mFunctionsView.findViewById(R.id.canvas_pen_seekbar);
		rubberSeekBar = (SeekBar) mFunctionsView.findViewById(R.id.canvas_rubber_seekbar);
		selectedDoneButton = (ImageView) mFunctionsView.findViewById(R.id.canvas_done);
		
		mPenButton.setImageDrawable(getResources().getDrawable(R.drawable.pentool_selected));

		mPenButton.setOnClickListener(new OnEditModeSelectedListener(EDIT_PEN));
		mBgdButton.setOnClickListener(new OnEditModeSelectedListener(EDIT_BGD));
		mRubberButton.setOnClickListener(new OnEditModeSelectedListener(EDIT_RUBBER));
		
		penSeekBar.setOnSeekBarChangeListener(new OnWidthChangeListener(EDIT_PEN));
		rubberSeekBar.setOnSeekBarChangeListener(new OnWidthChangeListener(EDIT_RUBBER));
		moreButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		selectedDoneButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		mEditMode = EDIT_PEN;
	}

	private void initColors() {
		colors = getResources().getStringArray(R.array.color_array);
		for (int i = 0; i < colors.length; i++) {
			colorViews[i] = (ImageView)findViewById(colorButtons[i]);
			colorViews[i].setOnClickListener(new OnColorChangeListener(i));
		}
		setPresentColor(BLACK);
	}
	
	private void setPresentColor(int index) {
		for (int i = 0; i < colors.length; i++) {
			colorViews[i].setBackgroundResource(0);
		}
		if (index != -1) {
			colorViews[index].setBackgroundResource(R.drawable.color_selection_background);
		}
	}

	private void setFunctionsSelection() {
		mPenButton.setImageResource(mEditMode == EDIT_PEN ? R.drawable.pentool_selected : R.drawable.pentool_normal);
		mBgdButton.setImageResource(mEditMode == EDIT_BGD ? R.drawable.change_background_color_selected : R.drawable.change_background_color_nomal);
		mRubberButton.setImageResource(mEditMode == EDIT_RUBBER ? R.drawable.rubbertool_selected : R.drawable.rubbertool_normal);
	}
	
	public class OnEditModeSelectedListener implements OnClickListener {
		private int editMode;
		public OnEditModeSelectedListener(int mode) {
			this.editMode = mode;
		}

		@Override
		public void onClick(View v) {
			mEditMode = editMode;
			setFunctionsSelection();
			switch (mEditMode) {
				case EDIT_PEN:
					mPaintBoardView.enableRubber(false);
					setPresentColor(penPrevState);
					if (penPrevState == -1) {
						colorViews[BLACK].setBackgroundResource(R.drawable.color_selection_background);
					}
					break;
				case EDIT_BGD:
					mPaintBoardView.enableRubber(false);
					setPresentColor(backPrevState);
					if (backPrevState == -1) {
						colorViews[WHITE].setBackgroundResource(R.drawable.color_selection_background);
					}
					break;
				case EDIT_RUBBER:
					mPaintBoardView.enableRubber(true);
					for (int i = 0; i < colors.length; i++) {
						colorViews[i].setBackgroundResource(0);
					}
					break;
				default:
					break;
			}
		}
		
	}
	
	public class OnColorChangeListener implements OnClickListener {
		private int colorIndex;
		public OnColorChangeListener(int index) {
			this.colorIndex = index;
		}

		@Override
		public void onClick(View v) {
			setPresentColor(colorIndex);
			switch (mEditMode) {
				case EDIT_PEN:
					mPaintBoardView.setPaintColor(Color.parseColor(colors[colorIndex]));
					penPrevState = colorIndex;
					break;
				case EDIT_BGD:
					mPaintBoardView.setBackgroundColor(Color.parseColor(colors[colorIndex]));
					backPrevState = colorIndex;
					break;
				default:
					break;
			}
		}
	}
	
	public class OnWidthChangeListener implements OnSeekBarChangeListener {
		private int editMode;
		public OnWidthChangeListener(int mode) {
			this.editMode = mode;
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			switch (editMode) {
				case EDIT_PEN:
					mPaintBoardView.setPaintWidth(progress);
					break;
				case EDIT_BGD:
					mPaintBoardView.setRubberWidth(progress);
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
}
