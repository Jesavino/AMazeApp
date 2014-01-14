package falstad;

// import java.awt.event.KeyListener;
// import java.awt.event.MouseListener;
import java.util.ArrayList;
// import java.util.Hashtable;
import java.util.Iterator;

/**
 * Class handles the user interaction for the maze. 
 * It implements a state-dependent behavior that controls the display and reacts to key board input from a user. 
 * After refactoring the original code from an applet into a panel, it is wrapped by a MazeApplication to be a java application 
 * and a MazeApp to be an applet for a web browser. At this point user keyboard input is first dealt with a key listener
 * and then handed over to a Maze object by way of the keyDown method.
 *
 * This code is refactored code from Maze.java by Paul Falstad, www.falstad.com, Copyright (C) 1998, all rights reserved
 * Paul Falstad granted permission to modify and use code for teaching purposes.
 * Refactored by Peter Kemper
 */
// MEMO: original code: public class Maze extends Applet {
//public class Maze extends Panel {
public class Maze {

	// Model View Controller pattern, the model needs to know the viewers
	// however, all viewers share the same graphics to draw on, such that the share graphics
	// are administered by the Maze object
	final private ArrayList<Viewer> views = new ArrayList<Viewer>() ; 
	MazePanel panel ; // graphics to draw on, shared by all views
		


	int state;			// keeps track of the current GUI state, one of STATE_TITLE,...,STATE_FINISH, mainly used in redraw()
	// possible values are defined in Constants
	// user can navigate 
	// title -> generating -(escape) -> title
	// title -> generation -> play -(escape)-> title
	// title -> generation -> play -> finish -> title
	// STATE_PLAY is the main state where the user can navigate through the maze in a first person view

	private int percentdone = 0; // describes progress during generation phase
	private boolean showMaze;		 	// toggle switch to show overall maze on screen
	boolean showSolution;		// toggle switch to show solution in overall maze on screen
	private boolean solving;			// toggle switch 
	boolean mapMode; // true: display map of maze, false: do not display map of maze
	// map_mode is toggled by user keyboard input, causes a call to draw_map during play mode

	//static final int viewz = 50;    
	int viewx, viewy, angle;
	int dx, dy;  // current direction
	int px, py ; // current position on maze grid (x,y)
	int walkStep;
	int viewdx, viewdy; // current view direction


	// debug stuff
	boolean deepdebug = false;
	boolean allVisible = false;
	boolean newGame = false;

	// properties of the current maze
	int mazew; // width of maze
	int mazeh; // height of maze
	Cells mazecells ; // maze as a matrix of cells which keep track of the location of walls
	Distance mazedists ; // a matrix with distance values for each cell towards the exit
	Cells seencells ; // a matrix with cells to memorize which cells are visible from the current point of view
	// the FirstPersonDrawer obtains this information and the MapDrawer uses it for highlighting currently visible walls on the map
	BSPNode rootnode ; // a binary tree type search data structure to quickly locate a subset of segments
	// a segment is a continuous sequence of walls in vertical or horizontal direction
	// a subset of segments need to be quickly identified for drawing
	// the BSP tree partitions the set of all segments and provides a binary search tree for the partitions
	

	// Mazebuilder is used to calculate a new maze together with a solution
	// The maze is computed in a separate thread. It is started in the local Build method.
	// The calculation communicates back by calling the local newMaze() method.
	MazeBuilder mazebuilder;

	
	// fixing a value matching the escape key
	final int ESCAPE = 27;

	// generation method used to compute a maze
	int method = 0 ; // 0 : default method, Falstad's original code
	// method == 1: Prim's algorithm
	// method == 2: Eller's algorithm

	int zscale = Constants.VIEW_HEIGHT/2;
	
	
	String robotDriverType;
	MazeFileReader mfr;
	MazeFileWriter mfw;
	
	private RangeSet rset;
	
	RobotDrivers robotDriver;
	BasicRobot robot;
	
	/**
	 * Constructor
	 */
	public Maze() {
		super() ;
		panel = new MazePanel(this) ;
	}
	/**
	 * Constructor that also selects a particular generation method
	 */
	public Maze(int method)
	{
		super() ;
		// 0 is default, do not accept other settings but 0 and 1 or 2
		if (1 == method)
			this.method = 1 ;
		if (2 == method)
			this.method = 2;
		panel = new MazePanel(this) ;
	}
	/**
	 * Method to initialize internal attributes. Called separately from the constructor. 
	 */
	public void init() {
		state = Constants.STATE_TITLE;
		rset = new RangeSet();
		panel.initBufferImage();
		
		
		addView(new MazeView(this)) ;
		
		panel.setLayouts(this);
		panel.makeSelectPanel(this);
		panel.makeSliderPanel(this);
		panel.makeGeneratorPanel(this);
		panel.makeFilePanel(this);
		panel.addAllPanels(this);
		
		notifyViewerRedraw() ;
	}
	//@Override
	//public void actionPerformed(ActionEvent e) {
	//	this.actionPerformed(panel, e);
	//}
			
	
	/**
	 * Method obtains a new Mazebuilder and has it compute new maze, 
	 * it is only used in keyDown()
	 * @param skill level determines the width, height and number of rooms for the new maze
	 */
	protected void build(int skill) {
		// switch screen
		state = Constants.STATE_GENERATING;
		percentdone = 0;
		
		panel.makePrepPanel(this);
		// select generation method
		switch(method){
		case 2: mazebuilder = new MazeBuilderEller(); //generate with Eller's algorithm
		break;
		case 1 : mazebuilder = new MazeBuilderPrim(); // generate with Prim's algorithm
		break ;
		case 0: // generate with Falstad's original algorithm (0 and default), note the missing break statement
		default : mazebuilder = new MazeBuilder(); 
		break ;
		}
		// adjust settings and launch generation in a separate thread
		mazew = Constants.SKILL_X[skill];
		mazeh = Constants.SKILL_Y[skill];
		mazebuilder.build(this, mazew, mazeh, Constants.SKILL_ROOMS[skill], Constants.SKILL_PARTCT[skill]);
		// mazebuilder performs in a separate thread and calls back by calling newMaze() to return newly generated maze
	}
	/**
	 * Call back method for MazeBuilder to communicate newly generated maze as reaction to a call to build()
	 * @param root node for traversals, used for the first person perspective
	 * @param cells encodes the maze with its walls and border
	 * @param dists encodes the solution by providing distances to the exit for each position in the maze
	 * @param startx current position, x coordinate
	 * @param starty current position, y coordinate
	 */
	public void newMaze(BSPNode root, Cells c, Distance dists, int startx, int starty) {
			
		if (Cells.deepdebugWall)
		{   // for debugging: dump the sequence of all deleted walls to a log file
			// This reveals how the maze was generated
			c.saveLogFile(Cells.deepedebugWallFileName);
		}
		// adjust internal state of maze model
		showMaze = showSolution = solving = false;
		mazecells = c ;
		mazedists = dists;
		seencells = new Cells(mazew+1,mazeh+1) ;
		rootnode = root ;
		setCurrentDirection(1, 0) ;
		setCurrentPosition(startx,starty) ;
		walkStep = 0;
		viewdx = dx<<16; 
		viewdy = dy<<16;
		angle = 0;
		mapMode = false;
		// set the current state for the state-dependent behavior
		state = Constants.STATE_PLAY;
		
		// build the robot driver based on the main screen selection
		makeRobotType();
		
		// build the panel for display during the actual game
		panel.makeGamePanel(this);
		
		
		cleanViews() ;
		// register views for the new maze
		// mazew and mazeh have been set in build() method before mazebuider was called to generate a new maze.
		// reset map_scale in mapdrawer to a value of 10
		addView(new FirstPersonDrawer(Constants.VIEW_WIDTH,Constants.VIEW_HEIGHT,
				Constants.MAP_UNIT,Constants.STEP_SIZE, mazecells, seencells, 10, mazedists.getDists(), mazew, mazeh, root, this)) ;
		// order of registration matters, code executed in order of appearance!
		addView(new MapDrawer(Constants.VIEW_WIDTH,Constants.VIEW_HEIGHT,Constants.MAP_UNIT,Constants.STEP_SIZE, mazecells, seencells, 10, mazedists.getDists(), mazew, mazeh, this)) ;

		// notify viewers
		notifyViewerRedraw() ;
		
		if(robotDriverType != "Manual Driver") {
			try {
				
				if(robotDriver.drive2Exit()) {
					panel.makeFinishPanel(this);
					
				}
			} catch (Exception e) {
				System.out.printf("Robot has stopped due to having %f battery\n" , robot.getCurrentBatteryLevel());
				e.printStackTrace();
				state = Constants.STATE_TITLE;
				panel.playingToInitial();
				notifyViewerRedraw() ;
			}
		}
		
		
	}
	
	private void makeRobotType() {
		switch(robotDriverType) {
		case "Manual Driver":
			robot = new BasicRobot(this);
			robotDriver = new ManualDriver();
			break;
		case "Gambler":
			robot = new BasicRobot(this, true, true, false, false);
			robotDriver = new Gambler();
			break;
		case "Curious Gambler":
			robot = new BasicRobot(this, true, true, false, false);
			robotDriver = new CuriousGambler();
			break;
		case "Wall Follower":
			robot = new BasicRobot(this, true, true, false, false);
			robotDriver = new WallFollower();
			break;
		case "Wizard":
			robot = new BasicRobot(this);
			robotDriver = new Wizard(this.mazedists);
			break;
		default:
			System.out.println("Took default");
			robot = new BasicRobot(this);
			break;
		}
		try{
			robotDriver.setRobot(robot);
		} catch (UnsuitableRobotException s) {System.out.println("Your robot has no sensors!");}
	}

	/////////////////////////////// Methods for the Model-View-Controller Pattern /////////////////////////////
	/**
	 * Register a view
	 */
	public void addView(Viewer view) {
		views.add(view) ;
	}
	/**
	 * Unregister a view
	 */
	public void removeView(Viewer view) {
		views.remove(view) ;
	}
	/**
	 * Remove obsolete FirstPersonDrawer and MapDrawer
	 */
	private void cleanViews() {
		// go through views and notify each one
		Iterator<Viewer> it = views.iterator() ;
		while (it.hasNext())
		{
			Viewer v = it.next() ;
			if ((v instanceof FirstPersonDrawer)||(v instanceof MapDrawer))
			{
				//System.out.println("Removing " + v);
				it.remove() ;
			}
		}

	}
	/**
	 * Notify all registered viewers to redraw their graphics
	 */
	protected void notifyViewerRedraw() {
		// go through views and notify each one
		Iterator<Viewer> it = views.iterator() ;
		while (it.hasNext())
		{
			Viewer v = it.next() ;
			// viewers draw on the buffer graphics
			v.redraw(panel, state, px, py, viewdx, viewdy, walkStep, Constants.VIEW_OFFSET, rset, angle) ;
		}
		// update the screen with the buffer graphics
		panel.update() ;
	}
	/** 
	 * Notify all registered viewers to increment the map scale
	 */
	private void notifyViewerIncrementMapScale() {
		// go through views and notify each one
		Iterator<Viewer> it = views.iterator() ;
		while (it.hasNext())
		{
			Viewer v = it.next() ;
			v.incrementMapScale() ;
		}
		// update the screen with the buffer graphics
		panel.update() ;
	}
	/** 
	 * Notify all registered viewers to decrement the map scale
	 */
	private void notifyViewerDecrementMapScale() {
		// go through views and notify each one
		Iterator<Viewer> it = views.iterator() ;
		while (it.hasNext())
		{
			Viewer v = it.next() ;
			v.decrementMapScale() ;
		}
		// update the screen with the buffer graphics
		panel.update() ;
	}
	////////////////////////////// get methods ///////////////////////////////////////////////////////////////
	boolean isInMapMode() { 
		return mapMode ; 
	} 
	boolean isInShowMazeMode() { 
		return showMaze ; 
	} 
	boolean isInShowSolutionMode() { 
		return showSolution ; 
	} 
	public String getPercentDone(){
		return String.valueOf(percentdone) ;
	}
	public MazePanel getPanel() {
		return panel ;
	}
	////////////////////////////// set methods ///////////////////////////////////////////////////////////////
	////////////////////////////// Actions that can be performed on the maze model ///////////////////////////
	private void setCurrentPosition(int x, int y)
	{
		px = x ;
		py = y ;
	}
	private void setCurrentDirection(int x, int y)
	{
		dx = x ;
		dy = y ;
	}
	
	
	void buildInterrupted() {
		state = Constants.STATE_TITLE;
		notifyViewerRedraw() ;
		mazebuilder = null;
	}

	final double radify(int x) {
		return x*Math.PI/180;
	}


	/**
	 * Allows external increase to percentage in generating mode with subsequence graphics update
	 * @param pc gives the new percentage on a range [0,100]
	 * @return true if percentage was updated, false otherwise
	 */
	public boolean increasePercentage(int pc) {
		if (percentdone < pc && pc < 100) {
			percentdone = pc;
			if (state == Constants.STATE_GENERATING)
			{
				panel.progressBar.setValue(percentdone);
				notifyViewerRedraw() ;
			}
			else
				dbg("Warning: Receiving update request for increasePercentage while not in generating state, skip redraw.") ;
			return true ;
		}
		return false ;
	}

	



	/////////////////////// Methods for debugging ////////////////////////////////
	private void dbg(String str) {
		//System.out.println(str);
	}

	private void logPosition() {
		if (!deepdebug)
			return;
		dbg("x="+viewx/Constants.MAP_UNIT+" ("+
				viewx+") y="+viewy/Constants.MAP_UNIT+" ("+viewy+") ang="+
				angle+" dx="+dx+" dy="+dy+" "+viewdx+" "+viewdy);
	}
	///////////////////////////////////////////////////////////////////////////////

	/**
	 * Helper method for walk()
	 * @param dir
	 * @return true if there is no wall in this direction
	 */
	public boolean checkMove(int dir) {
		// obtain appropriate index for direction (CW_BOT, CW_TOP ...) 
		// for given direction parameter
		int a = angle/90;
		if (dir == -1)
			a = (a+2) & 3; // TODO: check why this works
		// check if cell has walls in this direction
		// returns true if there are no walls in this direction
		
		return mazecells.hasMaskedBitsFalse(px, py, Constants.MASKS[a]) ;
	}



	private void rotateStep() {
		angle = (angle+1800) % 360;
		viewdx = (int) (Math.cos(radify(angle))*(1<<16));
		viewdy = (int) (Math.sin(radify(angle))*(1<<16));
		moveStep();
	}

	private void moveStep() {
		notifyViewerRedraw() ;
		try {
			Thread.currentThread().sleep(25);
		} catch (Exception e) { }
	}

	private void rotateFinish() {
		setCurrentDirection((int) Math.cos(radify(angle)), (int) Math.sin(radify(angle))) ;
		logPosition();
	}

	private void walkFinish(int dir) {
		setCurrentPosition(px + dir*dx, py + dir*dy) ;
		
		
		if (isEndPosition(px,py)) {
			panel.makeFinishPanel(this);
		}
		walkStep = 0;
		logPosition();
	}

	/**
	 * checks if the given position is outside the maze
	 * @param x
	 * @param y
	 * @return true if position is outside, false otherwise
	 */
	private boolean isEndPosition(int x, int y) {
		return x < 0 || y < 0 || x >= mazew || y >= mazeh;
	}



	synchronized public void walk(int dir) {
		if (!checkMove(dir))
			return;
		for (int step = 0; step != 4; step++) {
			walkStep += dir;
			moveStep();
		}
		walkFinish(dir);
	}

	synchronized public void rotate(int dir) {
		final int originalAngle = angle;
		final int steps = 4;

		for (int i = 0; i != steps; i++) {
			angle = originalAngle + dir*(90*(i+1))/steps;
			rotateStep();
		}
		rotateFinish();
	}



	/**
	 * Method incorporates all reactions to keyboard input in original code, 
	 * after refactoring, Java Applet and Java Application wrapper call this method to communicate input.
	 */
	public boolean keyDown( int key) {
		switch (state) {
		// if screen shows title page, keys describe level of expertise
		// create a maze according to the user's selected level
		case Constants.STATE_TITLE:
			if (key >= '0' && key <= '9') {
				build(key - '0');
				break;
			}
			if (key >= 'a' && key <= 'f') {
				build(key - 'a' + 10);
				break;
			}
			break;
		// if we are currently generating a maze, recognize interrupt signal (ESCAPE key)
		// to stop generation of current maze
		case Constants.STATE_GENERATING:
			if (key == ESCAPE) {
				mazebuilder.interrupt();
				buildInterrupted();
			}
			break;
		// if user explores maze, 
		// react to input for directions and interrupt signal (ESCAPE key)	
		// react to input for displaying a map of the current path or of the overall maze (on/off toggle switch)
		// react to input to display solution (on/off toggle switch)
		// react to input to increase/reduce map scale
		case Constants.STATE_PLAY:
			switch (key) {
			case 'k': case '8': // handles UP events
				try{ 
					if(robotDriver.drive2Exit()) {
						panel.makeFinishPanel(this);
						break;
					}	
				} catch (Exception e) {
					System.out.println("Oh no your robot died");
					state = Constants.STATE_TITLE;
					panel.playingToInitial();
					notifyViewerRedraw() ;
				}
				try {
					if(robot.hasStopped()) {
						System.out.println("Oh no your robot died");
						state = Constants.STATE_TITLE;
						panel.playingToInitial();
						notifyViewerRedraw() ;
					}
					robotDriver.stepForward();
				} catch (HitObstacleException h) {
					System.out.println("You hit a wall!");
					state = Constants.STATE_TITLE;
					panel.playingToInitial();
					notifyViewerRedraw() ;
				}
				break;
			case 'h': case '4': // handles LEFT events
				try{ 
					if(robotDriver.drive2Exit()) {
						panel.makeFinishPanel(this);
						break;
					}	
				} catch (Exception e) {
					System.out.println("Oh no your robot died");
					state = Constants.STATE_TITLE;
					panel.playingToInitial();
					notifyViewerRedraw() ;
				}
				if(robot.hasStopped()) {
					System.out.println("Oh no your robot died");
					state = Constants.STATE_TITLE;
					panel.playingToInitial();
					notifyViewerRedraw() ;
				}
				robotDriver.turnLeft();
				break;
			case 'l': case '6': // handles RIGHT events
				try{ 
					if(robotDriver.drive2Exit()) {
						panel.makeFinishPanel(this);
						break;
					}	
				} catch (Exception e) {
					System.out.println("Oh no your robot died");
					state = Constants.STATE_TITLE;
					panel.playingToInitial();
					notifyViewerRedraw() ;
				}
				if(robot.hasStopped()) {
					System.out.println("Oh no your robot died");
					state = Constants.STATE_TITLE;
					panel.playingToInitial();
					notifyViewerRedraw() ;
				}
				robotDriver.turnRight();
				break;
			case 'j': case '2': // handles DOWN events
				try{ 
					if(robotDriver.drive2Exit()) {
						panel.makeFinishPanel(this);
						break;
					}	
				} catch (Exception e) {
					System.out.println("Oh no your robot died");
					state = Constants.STATE_TITLE;
					panel.playingToInitial();
					notifyViewerRedraw() ;
				}
				if(robot.hasStopped()) {
					System.out.println("Oh no your robot died");
					state = Constants.STATE_TITLE;
					panel.playingToInitial();
					notifyViewerRedraw() ;
				}
				try {
					robotDriver.stepBackward();
					} catch (HitObstacleException h) {
						System.out.println("You hit a wall behind you!");
						state = Constants.STATE_TITLE;
						panel.playingToInitial();
						notifyViewerRedraw() ;
					}
				break;
			case ESCAPE: case 65385:
				if (solving)
					solving = false;
				else
					state = Constants.STATE_TITLE;
				notifyViewerRedraw() ;
				break;
			case ('w' & 0x1f): 
			{ 
				setCurrentPosition(px + dx, py + dy) ;
				notifyViewerRedraw() ;
				break;
			}
			case '\t': case 'm':
				mapMode = !mapMode; 		
				notifyViewerRedraw() ; 
				break;
			case 'z':
				showMaze = !showMaze; 		
				notifyViewerRedraw() ; 
				break;
			case 's':
				showSolution = !showSolution; 		
				notifyViewerRedraw() ;
				break;
			case ('s' & 0x1f):
				if (solving)
					solving = false;
				else {
					solving = true;
				}
			break;
			case '+': case '=':
			{
				notifyViewerIncrementMapScale() ;
				notifyViewerRedraw() ; // seems useless but it is necessary to make the screen update
				break ;
			}
			case '-':
				notifyViewerDecrementMapScale() ;
				notifyViewerRedraw() ; // seems useless but it is necessary to make the screen update
				break ;
			}
			break;
		// if we are finished, return to initial state with title screen	
		case Constants.STATE_FINISH:
			panel.makeFinishPanel(this);
			break;
	} 
			
		return true;
	}
	


}
