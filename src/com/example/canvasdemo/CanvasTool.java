package com.example.canvasdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;

@SuppressLint("InflateParams") 
public class CanvasTool extends PopupWindow {

	private View toolView;
	private ImageView penView, backgroundView, rubberView, clearView;
	private ImageView color0, color1, color2, color3, color4, color5, color6, color7, color8, color9, colorA, colorB;
	private SeekBar penSeekBar, rubberSeekBar;
	
	public CanvasTool(Context context) {
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		toolView = inflater.inflate(R.layout.popup_window_selections, null);
		
		penView = (ImageView)toolView.findViewById(R.id.canvas_tool_pen);
		backgroundView = (ImageView)toolView.findViewById(R.id.canvas_tool_background);
		rubberView = (ImageView)toolView.findViewById(R.id.canvas_tool_eraser);
		clearView = (ImageView)toolView.findViewById(R.id.canvas_tool_clear);
		penSeekBar = (SeekBar)toolView.findViewById(R.id.canvas_tool_pen_seekbar);
		rubberSeekBar = (SeekBar)toolView.findViewById(R.id.canvas_tool_rubber_seekbar);
		
		this.setContentView(toolView);
	}

}
