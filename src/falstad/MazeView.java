package falstad;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;



public class MazeView extends DefaultViewer {

	Maze maze ; // need to know the maze model to check the state 
	// and to provide progress information in the generating state
	
	public MazeView(Maze m) {
		super() ;
		maze = m;
	}

	@Override
	public void redraw(MazePanel panel, int state, int px, int py, int view_dx,
			int view_dy, int walk_step, int view_offset, RangeSet rset, int ang) {
		//dbg("redraw") ;
		Graphics gc = panel.getBufferGraphics();
		
		switch (state) {
		case Constants.STATE_TITLE:
			redrawTitle(gc);
			maze.panel.update();
			break;
		case Constants.STATE_GENERATING:
			redrawGenerating(gc);
			maze.panel.update();
			break;
		case Constants.STATE_PLAY:
			// skip this one
			maze.panel.update();
			break;
		case Constants.STATE_FINISH:
			redrawFinish(gc);
			maze.panel.update();
			break;
		}
	}
	
	private void dbg(String str) {
		System.out.println("MazeView:" + str);
	}
	
	// 
	
	/**
	 * Helper method for redraw to draw the title screen, screen is hardcoded
	 * @param  gc graphics is the off screen image
	 */
	void redrawTitle(Graphics gc) {
		gc.setColor(Color.white);
		gc.fillRect(0, 0, Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT);
		gc.setFont(largeBannerFont);
		FontMetrics fm = gc.getFontMetrics();
		gc.setColor(Color.red);
		centerString(gc, fm, "MAZE", 100);
		gc.setColor(Color.blue);
		gc.setFont(smallBannerFont);
		fm = gc.getFontMetrics();
		centerString(gc, fm, "by Paul Falstad", 160);
		centerString(gc, fm, "www.falstad.com", 190);
		gc.setColor(Color.black);
		centerString(gc, fm, "To start, select a skill level.", 250);
		centerString(gc, fm, "(Press a number from 0 to 9,", 300);
		centerString(gc, fm, "or a letter from A to F)", 320);
		centerString(gc, fm, "v1.2", 350);
		
		
	}
	/**
	 * Helper method for redraw to draw final screen, screen is hard coded
	 * @param gc graphics is the off screen image
	 */
	void redrawFinish(Graphics gc) {
		gc.setColor(Color.blue);
		gc.fillRect(0, 0, Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT);
		gc.setFont(largeBannerFont);
		FontMetrics fm = gc.getFontMetrics();
		gc.setColor(Color.yellow);
		centerString(gc, fm, "You won!", 100);
		gc.setColor(Color.orange);
		gc.setFont(smallBannerFont);
		fm = gc.getFontMetrics();
		centerString(gc, fm, "Congratulations!", 160);
		gc.setColor(Color.white);
		centerString(gc, fm, "Hit any key to restart", 300);
	}

	/**
	 * Helper method for redraw to draw screen during phase of maze generation, screen is hard coded
	 * only attribute percentdone is dynamic
	 * @param gc graphics is the off screen image
	 */
	void redrawGenerating(Graphics gc) {
		gc.setColor(Color.yellow);
		gc.fillRect(0, 0, Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT);
		gc.setFont(largeBannerFont);
		FontMetrics fm = gc.getFontMetrics();
		gc.setColor(Color.red);
		centerString(gc, fm, "Building maze", 150);
		gc.setFont(smallBannerFont);
		fm = gc.getFontMetrics();
		gc.setColor(Color.black);
		centerString(gc, fm, maze.getPercentDone()+"% completed", 200);
		centerString(gc, fm, "Hit escape to stop", 300);
	}
	
	private void centerString(Graphics g, FontMetrics fm, String str, int ypos) {
		g.drawString(str, (Constants.VIEW_WIDTH-fm.stringWidth(str))/2, ypos);
	}

	Font largeBannerFont = new Font("TimesRoman", Font.BOLD, 48);
	Font smallBannerFont = new Font("TimesRoman", Font.BOLD, 16);

}
