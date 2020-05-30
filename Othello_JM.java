
import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class Othello_JM extends JFrame implements ActionListener{

	private PicPanel[][] allPanels;

	private JButton skipButton;

	public  final int[] HORZDISP = {1,1,1,0,-1,-1,-1,0};
	public  final int[] VERTDISP = {-1,0,1,1,1,0,-1,-1};
	private BufferedImage whitePiece;
	private BufferedImage blackPiece;

	private JLabel blackCountLabel;
	private int blackCount = 2;

	private JLabel whiteCountLabel;
	private int whiteCount = 2;

	private JLabel turnLabel;
	private boolean blackTurn = true;

	public Othello_JM(){

		setSize(1200,950);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		setTitle("Othello");
		getContentPane().setBackground(Color.white);

		allPanels = new PicPanel[8][8];

		JPanel gridPanel = new JPanel();
		gridPanel.setLayout(new GridLayout(8,8,2,2));
		gridPanel.setBackground(Color.black);
		gridPanel.setBounds(95,50,800,814);
		gridPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		for(int row = 0; row < 8; row++){
			for(int col = 0; col < 8; col++){
				allPanels[row][col] = new PicPanel(row,col,76,100);
				gridPanel.add(allPanels[row][col]);
			}
		}

		try {
			whitePiece = ImageIO.read(new File("white.jpg"));
			blackPiece = ImageIO.read(new File("black.jpg"));

		} catch (IOException ioe) {
			JOptionPane.showMessageDialog(null, "Could not read in the pic");
			System.exit(0);
		}	

		skipButton = new JButton("Skip Turn");
		skipButton.addActionListener(this);
		skipButton.setBounds(925,475,150,50);

		blackCountLabel = new JLabel("Black: 2 ");
		blackCountLabel.setFont(new Font("Calibri",Font.PLAIN,35));
		blackCountLabel.setBounds(925,150,275,50);

		whiteCountLabel = new JLabel("White: 2 ");
		whiteCountLabel.setFont(new Font("Calibri",Font.PLAIN,35));
		whiteCountLabel.setBounds(925,225,275,50);

		turnLabel = new JLabel("Turn: Black ");
		turnLabel.setFont(new Font("Calibri",Font.PLAIN,35));
		turnLabel.setBounds(925,375,275,75);

		allPanels[allPanels.length/2][allPanels[0].length/2].myColor = Color.white;
		allPanels[allPanels.length/2][(allPanels[0].length/2) - 1].myColor = Color.black;
		allPanels[(allPanels.length/2) - 1][(allPanels[0].length/2)].myColor = Color.black;
		allPanels[(allPanels.length/2) - 1][(allPanels[0].length/2) - 1].myColor = Color.white;

		add(gridPanel);
		add(skipButton);
		add(blackCountLabel);
		add(whiteCountLabel);
		add(turnLabel);


		setVisible(true);
	}

	private void updateLabels(){
		whiteCountLabel.setText("White: "+whiteCount);
		blackCountLabel.setText("Black: "+blackCount);

		String turn = "Black";

		if(!blackTurn)
			turn = "White";

		turnLabel.setText("Turn: "+turn);
	}


	public void actionPerformed(ActionEvent ae){

		blackTurn = !blackTurn;
		updateLabels();
	}


	class PicPanel extends JPanel implements MouseListener{
		private int row;
		private int col;

		private Color myColor;

		public PicPanel(int r, int c, int w, int h){
			row = r;
			col = c;

			this.addMouseListener(this);

		}	

		//flips the color of the piece (if found)
		public void flip() {
			if(myColor == null)
				return;
			if(myColor == Color.black)
				myColor = Color.white;
			else
				myColor = Color.black;

			repaint();
		}	


		//this will draw the image (piece or green background).  You will never call this method
		public void paintComponent(Graphics g){
			super.paintComponent(g);

			if(myColor == null){
				setBackground(new Color(0,108,89));
			}
			else if(myColor == Color.white){
				g.drawImage(whitePiece,0,0,this);
			}
			else{
				g.drawImage(blackPiece,0,0,this);
			}

		}

		//add code here to react to the user clicking on the panel
		public void mouseClicked(MouseEvent arg0) {

			ArrayList<PicPanel> enemies = new ArrayList<PicPanel>();

			Color enemyColor = Color.black;
			Color ourColor = Color.white;

			//finds and adds enemies
			for(int i = 0; i < VERTDISP.length; i++) {
				int displaceRow = row + VERTDISP[i];
				int displaceCol = col + HORZDISP[i];

				if(displaceRow >= 0 && (displaceRow < allPanels.length)) {
					if(displaceCol >= 0 && displaceCol < allPanels[0].length) {
						if(allPanels[displaceRow][displaceCol].myColor != null) {
							if(blackTurn && allPanels[displaceRow][displaceCol].myColor.equals(Color.white) ) {
								enemies.add(allPanels[displaceRow][displaceCol]);
								enemyColor = Color.white;
								ourColor = Color.black;

							}

							else if(!blackTurn && allPanels[displaceRow][displaceCol].myColor.equals(Color.black)) {
								enemies.add(allPanels[displaceRow][displaceCol]);
								enemyColor = Color.black;
								ourColor = Color.white;
							}
						}
					}
				}
			}

			int count = 0;

			for (int i = 0; i < enemies.size(); i++) {
				int factor = 1;
				enemyColor = enemies.get(i).myColor;
				int dispRow = enemies.get(i).row - row;
				int dispCol = enemies.get(i).col - col;

				while(enemies.get(i).myColor.equals(enemyColor)) {

					if((row+dispRow*factor) >= 0 && (row+dispRow*factor) < allPanels.length) {
						if(col+dispCol*factor >= 0 && (col+dispCol*factor) < allPanels[0].length) {
							enemyColor = allPanels[row+dispRow*factor][col+dispCol*factor].myColor;
							factor++;
						}
					}
				}
				factor--;

				Color current = allPanels[row+dispRow*factor][col+dispCol*factor].myColor;

				if (allPanels[row][col].myColor==null && current != null && current.equals(ourColor)) {	
					
					allPanels[row][col].repaint();

					for (int j = 1; j < factor; j++) {
						allPanels[row+dispRow*j][col+dispCol*j].flip();

						if (blackTurn) {
							blackCount++;
							whiteCount--;
						}
						else {
							whiteCount++;
							blackCount--;
						}
					}

					count++;
				}
			}

			if (enemies.size()==0) {
				JOptionPane.showMessageDialog(null, "No adjacent enemies");
			}

			else if (count==0){
				JOptionPane.showMessageDialog(null, "Doesn't end with own piece");
			}

			else {
				if(blackTurn) {
					allPanels[row][col].myColor = Color.black;
					blackCount++;
				}
				else {
					allPanels[row][col].myColor = Color.white;
					whiteCount++;
				}

				blackTurn = !blackTurn;
				updateLabels();

			}

		}

		public void mouseEntered(MouseEvent arg0) {
			//DO NOT IMPLEMENT

		}

		public void mouseExited(MouseEvent arg0) {
			//DO NOT IMPLEMENT

		}

		public void mousePressed(MouseEvent arg0) {
			//DO NOT IMPLEMENT

		}

		public void mouseReleased(MouseEvent arg0) {
			//DO NOT IMPLEMENT

		}
	}

	public static void main(String[] args){
		new Othello_JM();
	}
}
