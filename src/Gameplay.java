import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.Timer;

import javax.swing.JPanel;

public class Gameplay extends JPanel implements KeyListener, ActionListener {

	private boolean play = false;
	private int score = 0;
	
	private int totalBricks = 21;
	
	private Timer timer;
	private int delay = 8;
	
	private int playerX = 310;
	
	// ball starting position
	private int ballposX = (int) (Math.random()*600); // width
	private int ballposY = (int) (Math.random()*200) + 250; // height 

	private int ballXdir = -2;
	private int ballYdir = -3;
	
	private MapGenerator map;
	
	// constructor; starting time
	public Gameplay() {
		map = new MapGenerator(3,7);
		addKeyListener(this);
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);
		timer = new Timer(delay, this);
		timer.start();
	}
	
	// function; drawing graphics
	public void paint(Graphics g) {
		
		// background
		g.setColor(Color.black);
		g.fillRect(1, 1, 692, 592);
		
		// drawing map
		map.draw((Graphics2D)g);
		
		// borders; fillRect(int x, int y, int width, int height)
		g.setColor(Color.blue);
		g.fillRect(0, 0, 3, 590); // left border
		g.fillRect(0, 0, 690, 3); // top border
		g.fillRect(680, 0, 3, 590); // right border
		
		// scoreboard
		g.setColor(Color.white);
		g.setFont(new Font("serif", Font.BOLD, 25));
		g.drawString("" + score, 590, 30);
		
		// the paddle
		g.setColor(Color.cyan);
		g.fillRect(playerX, 500, 100, 8);
		
		// ball
		g.setColor(Color.red);
		g.fillOval(ballposX, ballposY, 20, 20);
		
		// win!!
		if (totalBricks == 0) {
			play = false;
			ballXdir = 0;
			ballYdir = 0;
			// bg
			g.setColor(Color.GREEN);
			g.fillRect(1, 1, 692, 592);
			// text
			g.setColor(Color.black);
			g.setFont(new Font("serif", Font.BOLD, 75));
			g.drawString("CONGRATS", 130, 150);
			g.drawString("YOU WON!", 145, 230);
			// score display
			g.setFont(new Font("serif", Font.BOLD, 40));
			g.drawString("Your Score: "+ score, 225, 350);
			// play again
			g.setFont(new Font("serif", Font.BOLD, 45));
			g.drawString("Press enter to play again", 118, 450);
		}
		
		// booo! You Lose!
		if (ballposY > 600) {
			play = false;
			ballXdir = 0;
			ballYdir = 0;
			// bg
			g.setColor(Color.DARK_GRAY);
			g.fillRect(1, 1, 692, 592);
			// text
			g.setColor(Color.red);
			g.setFont(new Font("serif", Font.BOLD, 75));
			g.drawString("BOOOO", 205, 150);
			g.drawString("YOU LOSE!", 145, 230);
			// score display
			g.setFont(new Font("serif", Font.BOLD, 40));
			g.drawString("Your Score: "+ score, 225, 350);
			// play again
			g.setFont(new Font("serif", Font.BOLD, 45));
			g.drawString("Press enter to play again", 118, 450);
		}

		g.dispose();
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		timer.start();
		
		// ball bouncing off walls
		if(play) {
			// paddle
			if(new Rectangle(ballposX, ballposY, 20, 20).intersects(new Rectangle(playerX, 500, 100, 8))) {
				ballYdir = -ballYdir;
			}
			
			// Ball interaction with bricks
			A: for(int i = 0; i<map.map.length; i++) { // first map is the variable in this class, 2nd is the variable in MapGenerator.java
				for (int j = 0; j < map.map[0].length; j++) {
					if (map.map[i][j] > 0) {
						int brickX = j * map.brickWidth + 80;
						int brickY = i * map.brickHeight + 50;
						int brickWidth = map.brickWidth;
						int brickHeight = map.brickHeight;
						
						Rectangle rect = new Rectangle(brickX, brickY, brickWidth, brickHeight);
						Rectangle ballRect = new Rectangle(ballposX, ballposY, 20, 20);
						Rectangle brickRect = rect;
						
						if (ballRect.intersects(brickRect)) {
							map.setBrickValue(0, i, j);
							totalBricks--;
							score += 5;
							
							if ((ballposX + 19 <= brickRect.x) || (ballposX + 1 >= brickRect.x + brickRect.width)) {
								ballXdir = -ballXdir;
							} else {
								ballYdir = -ballYdir;
							}
							
							break A;
						}
					}
				}
			}
			
			
			ballposX += ballXdir;
			ballposY += ballYdir;
			// left border
			if (ballposX < 5) {
				ballXdir = -ballXdir;
			}
			// top border
			if (ballposY < 0) {
				ballYdir = -ballYdir;
			}
			// right border
			if (ballposX > 660) {
				ballXdir = -ballXdir;
			}
		}
		
		repaint();
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
		// pressing right arrow key functionality
		if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
			if (playerX >= 580) {
				playerX = 580;
			} else {
				moveRight();
			}
		}
		
		// pressing left arrow key functionality
		if(e.getKeyCode() == KeyEvent.VK_LEFT) {
			if (playerX <= 5) {
				playerX = 5;
			} else {
				moveLeft();
			}
		}
		
		// play again
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			if (!play) {
				play = true;
				ballposX = (int) (Math.random()*600); 
				ballposY = (int) (Math.random()*200) + 250; 
				ballXdir = -2;
				ballYdir = -3;
				playerX = 310;
				score = 0;
				totalBricks = 21;
				map = new MapGenerator(3,7);
				
				repaint();
			}
		}
	}	

	// function; paddle moving right
	public void moveRight() {
		play = true;
		playerX += 20;
	}
	
	// function; paddle moving left
	public void moveLeft() {
		play = true;
		playerX -= 20;
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	
}
