package falstad;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JToggleButton;

public class MazePanel extends Panel implements ActionListener {
	/* Panel operates a double buffer see
	 * http://www.codeproject.com/Articles/2136/Double-buffer-in-standard-Java-AWT
	 * for details
	 */
	Image bufferImage ;
	Graphics gc;
	// Maze object for mutating
	Maze maze;
	
	// All of the buttons and panels used
	JButton playButton;
	JButton stopButton;
	JButton quitButton;
	JButton menuButton;
	JButton saveMaze;
	JButton exitGame;
	JToggleButton showMap;
	JToggleButton showSol;
	
	JSlider slider;
	JRadioButton randButton;
	JRadioButton primsButton;
	JRadioButton ellersButton;
	JButton fileChoice;
	final JFileChooser fc = new JFileChooser();
	BorderLayout mazeLayout = new BorderLayout();
	FlowLayout flowLayout = new FlowLayout();
	GridLayout gridLayout = new GridLayout(0 , 1);
	
	final JPanel gamePanel = new JPanel();
	final JPanel selectPanel = new JPanel();
	final JPanel prepPanel = new JPanel();
	final JPanel sliderPanel = new JPanel();
	final JPanel generatorPanel = new JPanel();
	final JPanel filePanel = new JPanel();
	final JPanel endingPanel = new JPanel();
	JProgressBar progressBar;
	JComboBox<String> robotList;
	String robotDriverType;
	
	MazeFileReader mfr;
	MazeFileWriter mfw;
	
	public MazePanel(Maze maze) {
		super() ;
		this.maze = maze;
		this.setFocusable(false) ;
		this.setLayout(mazeLayout);
	}
	@Override
	public void update(Graphics g) {
		
		paint(g) ;
	}
	@Override
	public void paint(Graphics g) {
		g.drawImage(bufferImage,0,0,null) ;
		
		super.paint(g);
	}
	/*
	public void setBufferImage(Image buffer) {
		bufferImage = buffer ;
	}
	*/
	public void initBufferImage() {
		bufferImage = createImage(Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT);
		if (null == bufferImage)
		{
			System.out.println("Error: creation of buffered image failed, presumedly container not displayable");
		}
		
	}
	public Graphics getBufferGraphics() {
		if (null == bufferImage)
			initBufferImage() ;
		gc = bufferImage.getGraphics();
		return bufferImage.getGraphics() ;
	}
	public void update() {
		paint(getGraphics()) ;
	}
	/**
	 * makes the panel for selecting robot type and playing the maze
	 * @param maze TODO
	 */
	void makeSelectPanel(Maze maze) {
		playButton = new JButton("Play");
		selectPanel.add(playButton);
		playButton.addActionListener(this);
		
		String[] robotDrivers =  {"Manual Driver" ,"Gambler" , "Curious Gambler" , "Wall Follower" , "Wizard"};
		robotList = new JComboBox(robotDrivers);
		robotList.addActionListener(this);
		robotList.setSelectedIndex(0);
		selectPanel.add(robotList);
	}
	/**
	 * makes the panel for selection of maze difficulty
	 * @param maze TODO
	 */
	void makeSliderPanel(Maze maze) {
		slider = new JSlider( 1 , 15);
		slider.setMajorTickSpacing(1);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		sliderPanel.add(slider);
	}
	/**
	 * makes the panel which contains file manipulation choices
	 * @param maze TODO
	 */
	void makeFilePanel(Maze maze) {
		saveMaze = new JButton("Generate and Save Maze");
		saveMaze.addActionListener(this);
		filePanel.add(saveMaze);
		
		fileChoice = new JButton("Load a maze");
		fileChoice.addActionListener(this);
		filePanel.add(fileChoice);
	}
	/**
	 * makes the panel for selection of maze generation algorithms
	 * @param maze TODO
	 */
	void makeGeneratorPanel(Maze maze) {
		randButton = new JRadioButton("Random");
		primsButton = new JRadioButton("Prims");
		ellersButton = new JRadioButton("Ellers");
		
		ButtonGroup group = new ButtonGroup();
		group.add(randButton);
		group.add(primsButton);
		group.add(ellersButton);
		
		randButton.addActionListener(this);
		primsButton.addActionListener(this);
		ellersButton.addActionListener(this);
		
		generatorPanel.add(randButton);
		generatorPanel.add(primsButton);
		generatorPanel.add(ellersButton);
	}
	void setLayouts(Maze maze) {
		sliderPanel.setLayout(flowLayout);
		generatorPanel.setLayout(flowLayout);
		filePanel.setLayout(flowLayout);
		selectPanel.setLayout(gridLayout);
	}
	void addAllPanels(Maze maze) {
		selectPanel.add(sliderPanel);
		selectPanel.add(generatorPanel);
		selectPanel.add(filePanel);
		
		this.add(selectPanel, BorderLayout.SOUTH);
		setVisible(true);
	}
	/**
	 * creates the panel for the loading screen
	 * @param maze TODO
	 */
	void makePrepPanel(Maze maze) {
		stopButton = new JButton("Stop");
		stopButton.addActionListener(this);
		prepPanel.add(stopButton);
		
		progressBar = new JProgressBar(0 , 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		
		prepPanel.add(progressBar);
		
		add(prepPanel, BorderLayout.SOUTH);
		validate();
		
		maze.notifyViewerRedraw() ;
	}
	void makeFinishPanel(Maze maze) {
		maze.state = Constants.STATE_FINISH;
		quitButton = new JButton("Quit Game");
		quitButton.addActionListener(this);
		endingPanel.add(quitButton);
	
		menuButton = new JButton("Return to the Menu");
		menuButton.addActionListener(this);
		endingPanel.add(menuButton);
		
		remove(gamePanel);
		add(endingPanel, BorderLayout.SOUTH);
		update();
		
		maze.notifyViewerRedraw() ;
	}
	void makeGamePanel(Maze maze) {
		remove(prepPanel);
		update();
		showMap = new JToggleButton("Show the Map");
		showMap.addActionListener(this);
		gamePanel.add(showMap);
		
		showSol = new JToggleButton("Show the Solution");
		showSol.addActionListener(this);
		gamePanel.add(showSol);
		
		exitGame = new JButton("Exit to Menu");
		exitGame.addActionListener(this);
		gamePanel.add(exitGame);
		
		add(gamePanel, BorderLayout.SOUTH);
		update();
		maze.notifyViewerRedraw();
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(playButton)) {
			remove(selectPanel);
			 maze.build( slider.getValue()-1);			
		}
		if(e.getSource().equals( randButton)) {
			maze.method = 0;
		}
		if(e.getSource().equals( primsButton)) {
			maze.method = 1;
		}
		if(e.getSource().equals( ellersButton)) {
			maze.method = 2;
		}
		if(e.getSource().equals( fileChoice)) {
			int returnVal =  fc.showDialog(this,"Load");
			File f =  fc.getSelectedFile();
			String parameter = f.getPath();
			
			
			if (f.exists() && f.canRead())
			{
				System.out.println("MazeApplication: loading maze from file: " + parameter);
				// TODO: adjust this to mazeview
				
				maze.mfr = new MazeFileReader(parameter) ;
				maze.mazeh = maze.mfr.getHeight() ;
				maze.mazew = maze.mfr.getWidth() ;
				Distance d = new Distance(maze.mfr.getDistances()) ;
				maze.state = Constants.STATE_GENERATING;
				remove(selectPanel);
				maze.notifyViewerRedraw();
				maze.newMaze(maze.mfr.getRootNode(),maze.mfr.getCells(),d,maze.mfr.getStartX(), maze.mfr.getStartY()) ;
				
			}
			else
			{
				System.out.println("MazeApplication: unknown parameter value: " + parameter + " ignored, please try again.");
			}
		}
		if(e.getSource().equals(stopButton)) {
			maze.state = Constants.STATE_TITLE;
			remove(prepPanel);
			add( selectPanel, BorderLayout.SOUTH);
			validate();
			maze.notifyViewerRedraw() ;
			
		}
		if(e.getSource().equals( menuButton)) {
			maze.state = Constants.STATE_TITLE;
			remove( endingPanel);
			remove(selectPanel);
			add( selectPanel, BorderLayout.SOUTH);
			update();
			maze.notifyViewerRedraw() ;
		}
		if(e.getSource().equals( quitButton)) {
			System.exit(0);
		}
		if(e.getSource().equals( showMap))	{
			remove( gamePanel);
			maze.mapMode = !maze.mapMode;
			add( gamePanel);
			maze.notifyViewerRedraw() ; 
		}
		if(e.getSource().equals( showSol))	{
			if(maze.mapMode) {
				remove( gamePanel);
				maze.showSolution = !maze.showSolution;
				add( gamePanel);
				maze.notifyViewerRedraw() ;
			}
			else{
				remove( gamePanel);
				add( gamePanel);
				maze.notifyViewerRedraw();
			}
		}
		if(e.getSource().equals( exitGame)) {
			maze.state = Constants.STATE_TITLE;
			remove( gamePanel);
			remove(selectPanel);
			add( selectPanel, BorderLayout.SOUTH);
			update();
			maze.notifyViewerRedraw() ;	
		}
		if(e.getSource().equals( saveMaze)) {
			remove( selectPanel);
			maze.build( slider.getValue()-1);	
			
			int returnVal =  fc.showSaveDialog(this);
			String filename = fc.getSelectedFile().getAbsolutePath() + ".xml";
			
			MazeFileWriter.store( filename , maze.mazew, maze.mazeh, maze.mazebuilder.getNumRooms() , 
					maze.mazebuilder.expectedPartiters , maze.rootnode , maze.mazecells, maze.mazedists.dists , maze.px, maze.py);	
		}
		if(e.getSource().equals( robotList)){
			JComboBox cb = (JComboBox)e.getSource();
			 maze.robotDriverType = (String)cb.getSelectedItem();
		}
			
	}
	void playingToInitial() {
		remove(gamePanel);
		remove(selectPanel);
		add(selectPanel, BorderLayout.SOUTH);
		update();
	}
	void setGraphicsWhite() {
		gc.setColor(Color.white);
		
	}
	void drawLine(int nx1 , int ny1 , int nx2 , int ny2) {
		
		gc.drawLine(nx1 , ny1 , nx2 , ny2);
	}
	void setGraphicsGray() {
		
		gc.setColor(Color.gray);
	}
	void setGraphicsYellow() {
		
		gc.setColor(Color.yellow);
	}
	void setGraphicsRed() {
		
		gc.setColor(Color.red);
	}
	void fillOval(int first , int second, int third, int fourth) {
		
		gc.fillOval(first , second, third, fourth);
	}
	void setGraphicsBlack() {
		
		gc.setColor(Color.black);
	}
	void setGraphicsBlue() {
		
		gc.setColor(Color.blue);
	}
	void setGraphicsDarkGray() {
		
		gc.setColor(Color.darkGray);
	}
	void fillRect(int x , int y, int width , int height) {
		
		gc.fillRect( x , y , width , height);
	}
	void setColor(Seg seg) {
		Color colo = new Color(seg.col[0] , seg.col[1] , seg.col[2]);
		gc.setColor(colo);
	}
	void fillPolygon( int[] xps , int[] yps , int nPoints) {
		
		gc.fillPolygon(xps, yps, nPoints);
	}
	/**
	 * For getting rid of awt in Seg. 
	 * @param col
	 * @return
	 */
	public static int getRGB(int[] col) {
		Color colo = new Color(col[0] , col[1] , col[2]);

		return colo.getRGB();
	}
	public static int[] returnColorArray(int col) {
		int[] color = new int[3];
		Color colo = new Color(col);
		color[0] = colo.getRed();
		color[1] = colo.getGreen();
		color[2] = colo.getBlue();
		return color;
	}
}
