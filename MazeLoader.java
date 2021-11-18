package mazesol;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;



public class MazeLoader {
    
    private JFrame window;
    private Scanner fileToRead;
    private JPanel[][] grid;
    private static final Color WALL_COLOR = Color.BLUE.darker();
    private static final Color PATH_COLOR = Color.MAGENTA.brighter();
    private static final Color OPEN_COLOR = Color.WHITE;
    private static final Color BAD_PATH_COLOR  = Color.RED;
    private static int ROW;
    private static int COL;
    private String data;
    private Point start;
    private boolean allowMazeUpdate;
    private JMenuBar menuBar;
    private JMenu menu;
    private JMenuItem[] loadMaze;
    private Timer timer;
    private JFileChooser mazeFile;
    private String lastDirectory = null;
    
    /** Default constructor - initializes all private values
     * 
     */
    public MazeLoader() {
        // Intialize other "stuff"
        start = new Point();
        allowMazeUpdate = true;
        timer = new Timer(100, new TimerListener());
        
        // Create the maze window
        window = new JFrame("Maze Program");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Need to define the layout - as a grid depending on the number
        // of grid squares to use. Open the file and read in the size.
        try {
            
            fileToRead = new Scanner(new File("Maze.txt"));
            ROW = fileToRead.nextInt();
            COL = fileToRead.nextInt();
        }
        catch(FileNotFoundException e) {
            JOptionPane.showMessageDialog(window,"Default maze not found. " +
                    "\nSelect a maze to solve from the menu," +
                    "\nor rename maze to maze.txt", "Error", JOptionPane.ERROR_MESSAGE);
            allowMazeUpdate = false;
        }

        if(allowMazeUpdate) {
            // Now establish the Layout - appropriate to the grid size
            window.setLayout(new GridLayout(ROW, COL));
            grid= new JPanel[ROW][COL];
            data = fileToRead.nextLine();
            for(int i=0; i<ROW; i++) {
                data = fileToRead.nextLine();
                for(int j=0; j<COL; j++) {
                    grid[i][j] = new JPanel();
                    grid[i][j].setName("" + i + ":" + j);
                    if(data.charAt(j) == '*') 
                        grid[i][j].setBackground(WALL_COLOR);
                    else {
                        grid[i][j].setBackground(OPEN_COLOR);
                             grid[i][j].addMouseListener(new MazeListener());
                    }
                    window.add(grid[i][j]);
                }
            }
            fileToRead.close();
            window.pack();
        }

        // Add the menu to the window
        menuBar = new JMenuBar();
        menu = new JMenu("Load Maze...");
        loadMaze = new JMenuItem[2];
        loadMaze[0] = new JMenuItem("Load New Maze from another file...");
        loadMaze[0].addActionListener(new LoadMazeFromFile());
        loadMaze[1] = new JMenuItem("Load New Maze from current maze...");
        loadMaze[1].addActionListener(new ReloadCurrentMaze());
        menu.add(loadMaze[0]);
        menu.add(loadMaze[1]);
        menuBar.add(menu);
        window.setJMenuBar(menuBar);
        
        if(!allowMazeUpdate)
            window.setSize(100,50);
       
        // Finally, show the maze
        window.setResizable(false);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
    
    /** MazeListener class reacts to mouse presses - only when the current
     *  block that is clicked is a valid starting point within the maze.
     */
    private class MazeListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        /** mousePressed method defines the (x,y) coordinate of the starting
         *  square within the maze. Note: the start Point object does NOT
         *  reference the pixel location, rather the matrix location.
         * @param e - the MouseEvent created upon mouse click.
         */
        @Override
        public void mousePressed(MouseEvent e) {
            if(((JPanel)e.getSource()).getBackground().equals(OPEN_COLOR) &&
                    !timer.isRunning()) {
                data = ((JPanel)e.getSource()).getName();
                start.x = Integer.parseInt(data.substring(0,data.indexOf(":")));
                start.y = Integer.parseInt(data.substring(data.indexOf(":")+1));
              
                // Find the maze solution
                if(!findPath(start))
                    JOptionPane.showMessageDialog(window,"Cannot exit maze.");
                else
                    JOptionPane.showMessageDialog(window, "Maze Exited!");
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
        
    }
    
    /** findPath is the recursive routine to find the solution through the maze
     * 
     * @param p - the current Point in the maze
     * @return whether or not a solution has been found.
     */
    public boolean findPath(Point p)  {
        boolean foundSolution = false;
        
        grid[p.x][p.y].setBackground(PATH_COLOR);
        
        //base cases
        if(p.x == 0 || p.y == 0 || p.x == ROW-1 || p.y == COL-1)
            foundSolution = true;
        else{
                //down
                  if(grid[p.x+1][p.y].getBackground().equals(OPEN_COLOR) && !foundSolution
                          )
                {          
                  p.x++;
                  grid[p.x][p.y].setBackground(PATH_COLOR);
                  return findPath(p);
                }
                  
                //up
                if(grid[p.x-1][p.y].getBackground().equals(OPEN_COLOR) &&!foundSolution)
                {
                   p.x--;
                  grid[p.x][p.y].setBackground(PATH_COLOR);
                  return findPath(p);
                }
                
                //right
                if(grid[p.x][p.y+1].getBackground().equals(OPEN_COLOR) && !foundSolution)
                {
                  p.y++;
                  System.out.println(p.x);
                  System.out.println(p.y);
                  grid[p.x][p.y].setBackground(PATH_COLOR);
                  return findPath(p);
                }
                
                //left
                if(grid[p.x][p.y-1].getBackground().equals(OPEN_COLOR) && 
                        !foundSolution)
                {
                  p.y--;  
                  grid[p.x][p.y].setBackground(PATH_COLOR);
                  return findPath(p);
                }
                grid[p.x][p.y].setBackground(BAD_PATH_COLOR);
                //backtracks
                if(grid[p.x][p.y].getBackground().equals(BAD_PATH_COLOR) && 
                        grid[p.x+1][p.y].getBackground().equals(PATH_COLOR)&&!foundSolution)
                {
                    p.x++;
                    return findPath(p);
                }
                if(grid[p.x][p.y].getBackground().equals(BAD_PATH_COLOR) && 
                        grid[p.x-1][p.y].getBackground().equals(PATH_COLOR)&&!foundSolution)
                {
                    p.x--;
                    return findPath(p);
                }
                if(grid[p.x][p.y].getBackground().equals(BAD_PATH_COLOR) && 
                        grid[p.x][p.y+1].getBackground().equals(PATH_COLOR)&&!foundSolution)
                {
                    p.y++;
                    return findPath(p);
                }
                if(grid[p.x][p.y].getBackground().equals(BAD_PATH_COLOR) && 
                        grid[p.x][p.y-1].getBackground().equals(PATH_COLOR)&&!foundSolution)
                {
                    p.y--;
                    return findPath(p);
                }
        }
                
        return foundSolution;
    }
    
    /** ReloadCurrentMaze class listens to menu clicks - simply
     *  wipes the current state of the maze.
     */
    private class ReloadCurrentMaze implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            for(int i=0; i<ROW; i++)
                for(int j=0; j<COL; j++)
                    if(grid[i][j].getBackground().equals(PATH_COLOR) ||
                       grid[i][j].getBackground().equals(BAD_PATH_COLOR))
                         grid[i][j].setBackground(OPEN_COLOR);
        }
    }
    
    /** 
     * Loads maze from file
     */
    private class LoadMazeFromFile implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(window, "Feature not yet implemented",
                    "Extra Credit #2", JOptionPane.WARNING_MESSAGE);
        }
    } // end of LoadMazeFromFile class
    
    /** 
     * Not implemented yet
     */
    private class TimerListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }
    public static void main(String[] args){
       MazeLoader theMaze = new MazeLoader();
       
    }
}
