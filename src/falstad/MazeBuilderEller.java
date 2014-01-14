/**
 * 
 */
package falstad;

/**
 * @author Jesavino and Cmedginton
 *
 */
public class MazeBuilderEller extends MazeBuilder implements Runnable {
	
	/**
	 * 
	 */
	public MazeBuilderEller() {
		super();
		System.out.println("MazeBuilderEller uses Eller's algorithm to generate maze.");
	}
	
	/**
	 * This method to generate pathways. The algorithm goes row by row, starting at the top. Walls are deleted horizonatlly using a random
	 * number stream. Cells are added to sets based on the walls being deleted one by one. These sets are used to determine if a wall should 
	 * be deleted. After the horizontal rows are done, the vertical walls are deleted, finally ending with shifting the sets used down. 
	 * Finishes the completed cells object. 
	 */
	@Override
	protected void generatePathways() {
		int[][] cellSet = new int[width][height];
		int i;
		int newSet = width; // used for creating new sets when shifting row down
		int rowPtr = 0;
		int colPtr = 0;
		int rand = 0; // for use w/ random
		
		while(rowPtr != height) {
			
			// either initialize or copy sets from previous row
			if(rowPtr == 0) {
				// add each cell in the first row to its own set
				for(i=0; i< width ; i++)
					cellSet[i][0] = i;
			}
			// copy top row. If a cell has a wall above it, put it in its own class
			else {		
				for(i = 0 ; i < width ; i++) {
					if(cells.hasWallOnTop(i, rowPtr)) 
						cellSet[i][rowPtr] = newSet++;
					else
						cellSet[i][rowPtr] = cellSet[i][rowPtr - 1];
				}
			}
			
			// horizontal pathing
			while(colPtr != width) {
				
				// check for the last row
				if(rowPtr == (height - 1)) {
					for(i = 0 ; i < (width - 1) ; i++) {
						if(cellSet[i][rowPtr] != cellSet[i+1][rowPtr])
							cells.deleteWall(i, rowPtr, 1, 0);
					}
					break;
				}				
				
				if(colPtr != (width - 1)) {
					
					if(cellSet[colPtr][rowPtr] != cellSet[colPtr + 1][rowPtr]) {
						// get a random number between 0,1
						
						rand =  random.nextIntWithinInterval(0, 1);
					}
					else {
						colPtr++;
						continue;
					}
				}
				else{
					colPtr++;
					continue;
				}
				
				// knock down wall
				if(rand == 0) {
					cells.deleteWall(colPtr, rowPtr, 1, 0);
					cellSet[colPtr+1][rowPtr] = cellSet[colPtr][rowPtr];
				}
				else colPtr++;
			}
			
			colPtr = 0;
			// vertical pathing
			while(colPtr != width)  {
				if(rowPtr == (height - 1))
					break;
				
				int startCol = colPtr;
				
				if(colPtr == (width - 1)) { // only gets here is last col is not in set w/ 2nd to last
					cells.deleteWall(colPtr, rowPtr, 0, 1);
					break;
				}
				
				while(true) {
					
					if(colPtr == (width - 1))
						break;
					if(cellSet[colPtr][rowPtr] == cellSet[colPtr+1][rowPtr]) {
						colPtr++;
						continue;
					}
					else break;
		
				}
				// delete one wall randomly from the set of walls on the bottom
				rand = random.nextIntWithinInterval(startCol, colPtr);
				cells.deleteWall(rand, rowPtr, 0, 1);
				colPtr++;			
				
			}	
			// increment the row, and do the whole thing again. 
			rowPtr++;
			colPtr = 0;
		}
	}
	
	/**
	 * Fill the given maze object with a newly computed maze according to parameter settings
	 * @param mz maze to be filled
	 * @param w width of requested maze
	 * @param h height of requested maze
	 * @param roomct number of rooms // always 0 no matter what is passed in
	 * @param pc number of expected partiters
	 */
	@Override
	public void build(Maze mz, int w, int h, int roomct, int pc) {
		init(mz, w, h, 0, pc);
		buildThread = new Thread(this);
		buildThread.start();
	}
	/**
	 * Initialize internal attributes, method is called by build() when input parameters are provided
	 * @param mz maze to be filled
	 * @param w width of requested maze
	 * @param h height of requested maze
	 * @param roomct number of rooms
	 * @param pc number of expected partiters
	 */
	private void init(Maze mz, int w, int h, int roomct, int pc) {
		// store parameters
		maze = mz;
		width = w;
		height = h;
		
		expectedPartiters = pc;
		// initialize data structures
		cells = new Cells(w,h) ;
		dists = new Distance(w,h) ;
		//colchange = random.nextIntWithinInterval(0, 255); // used in the constructor for Segments  class Seg
	}
		
	/**
	 * Main method to run construction of a new maze with a MazeBuilder in a thread of its own.
	 * This method is called internally by the build method when it sets up and starts a new thread for this object.
	 * Changed so that no rooms are added compared to the MazeBuilder method
	 */
	public void run() {
		// try-catch block to recognize if thread is interrupted
		try {
			// create an initial invalid maze where all walls and borders are up
			cells.initialize();
						
			Thread.sleep(SLEEP_INTERVAL) ; // test if thread has been interrupted, i.e. notified to stop

			// put pathways into the maze, determine its starting and end position and calculate distances
			generate();

			Thread.sleep(SLEEP_INTERVAL) ; // test if thread has been interrupted, i.e. notified to stop

			final int colchange = random.nextIntWithinInterval(0, 255); // used in the constructor for Segments  class Seg
			final BSPBuilder b = new BSPBuilder(maze, dists, cells, width, height, colchange, expectedPartiters) ;
			BSPNode root = b.generateBSPNodes();

			Thread.sleep(SLEEP_INTERVAL) ; // test if thread has been interrupted, i.e. notified to stop

			// dbg("partiters = "+partiters);
			// communicate results back to maze object
			maze.newMaze(root, cells, dists, startx, starty);
		}
		catch (InterruptedException ex) {
			// necessary to catch exception to avoid escalation
			// exception mechanism basically used to exit method in a controlled way
			// no need to clean up internal data structures
			// dbg("Catching signal to stop") ;
		}
	}
}
