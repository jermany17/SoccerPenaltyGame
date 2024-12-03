package soccer.game;

import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

class Player {
    private int score;
    private int chancesLeft;
    private boolean[] shotResults;

    public Player(int chances) {
        this.score = 0;
        this.chancesLeft = chances;
        this.shotResults = new boolean[chances];
    }

    public int getScore() {
        return score;
    }

    public int getChancesLeft() {
        return chancesLeft;
    }

    public boolean[] getShotResults() {
        return shotResults;
    }

    public void increaseScore() {
        score++;
    }

    public void decreaseChances() {
        chancesLeft--;
    }

    public void setShotResult(int index, boolean isGoal) {
        shotResults[index] = isGoal;
    }

    public void reset(int chances) {
        this.score = 0;
        this.chancesLeft = chances;
        this.shotResults = new boolean[chances];
    }
}


public class SoccerPenaltyGame extends JFrame {
    private Player player1;
    private Player player2;
    private int currentPlayerIndex = 1; // 1: Player1, 2: Player2

    private JLabel player1ScoreLabel, player2ScoreLabel, chancesLeftLabel1, chancesLeftLabel2;
    private JPanel scorePanel, scorePanel2;

    public SoccerPenaltyGame() {
        player1 = new Player(5);
        player2 = new Player(5);

        setTitle("Soccer Penalty Shootout Game"); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, 750, 470);

        ImageIcon icon = new ImageIcon("images\\GoalPost.png");
        Image scaledImage = icon.getImage().getScaledInstance(750, 470, Image.SCALE_SMOOTH);
        icon = new ImageIcon(scaledImage);
        JLabel backgroundLabel = new JLabel(icon);
        backgroundLabel.setBounds(0, 0, 750, 470);

        JButton leftButton = new JButton("Left");
        leftButton.setBounds(220, 150, 73, 50); 
        JButton centerButton = new JButton("Center");
        centerButton.setBounds(345, 150, 73, 50);
        JButton rightButton = new JButton("Right");
        rightButton.setBounds(465, 150, 73, 50);
        
        JButton gameRecordButton = new JButton("Game Record");
        gameRecordButton.setBounds(600, 400, 120, 30);

        leftButton.addActionListener(e -> shoot(0));
        centerButton.addActionListener(e -> shoot(1));
        rightButton.addActionListener(e -> shoot(2));
        
        gameRecordButton.addActionListener(e -> showGameRecords());

        player1ScoreLabel = new JLabel("Player1 Score: 0");
        player1ScoreLabel.setBounds(50, 20, 150, 30);
        player2ScoreLabel = new JLabel("Player2 Score: 0");
        player2ScoreLabel.setBounds(600, 20, 150, 30);
        chancesLeftLabel1 = new JLabel("Chances Left: 5");
        chancesLeftLabel1.setBounds(50, 50, 150, 30);
        chancesLeftLabel2 = new JLabel("Chances Left: 5");
        chancesLeftLabel2.setBounds(600, 50, 150, 30);

        scorePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawScores(g);
            }
        };
        scorePanel.setOpaque(false); 
        scorePanel.setBounds(180, 45, 400, 50); 

        scorePanel2 = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 25));
                g.drawString(player1.getScore() + "          :          " + player2.getScore(), 10, 30);
            }
        };
        scorePanel2.setOpaque(false); 
        scorePanel2.setBounds(280, 10, 200, 50); 

        
        layeredPane.add(backgroundLabel, Integer.valueOf(0));
        layeredPane.add(leftButton, Integer.valueOf(1));
        layeredPane.add(centerButton, Integer.valueOf(1));
        layeredPane.add(rightButton, Integer.valueOf(1));
        layeredPane.add(gameRecordButton, Integer.valueOf(1));
        layeredPane.add(player1ScoreLabel, Integer.valueOf(1));
        layeredPane.add(player2ScoreLabel, Integer.valueOf(1));
        layeredPane.add(chancesLeftLabel1, Integer.valueOf(1));
        layeredPane.add(chancesLeftLabel2, Integer.valueOf(1));
        layeredPane.add(scorePanel, Integer.valueOf(1));
        layeredPane.add(scorePanel2, Integer.valueOf(2));

        add(layeredPane);

        setSize(750, 470); 
        setResizable(false);
        setVisible(true);
    }


    private void shoot(int direction) {
        Random random = new Random();
        int defenderDirection = random.nextInt(3);

        Player currentPlayer = (currentPlayerIndex == 1) ? player1 : player2;
        JLabel currentScoreLabel = (currentPlayerIndex == 1) ? player1ScoreLabel : player2ScoreLabel;
        JLabel currentChancesLabel = (currentPlayerIndex == 1) ? chancesLeftLabel1 : chancesLeftLabel2;

        boolean isGoal = direction != defenderDirection;
        int shotIndex = 5 - currentPlayer.getChancesLeft();

        if (isGoal) {
            currentPlayer.increaseScore();
            if (currentPlayer.getChancesLeft() > 0) currentPlayer.setShotResult(shotIndex, true);
            showGoalScreen(); 
        } 
        else {
        	if (currentPlayer.getChancesLeft() > 0) currentPlayer.setShotResult(shotIndex, false);
            showSaveScreen(); 
        }

        if (currentPlayer.getChancesLeft() > 0) currentPlayer.decreaseChances();
        currentScoreLabel.setText("Player" + currentPlayerIndex + " Score: " + currentPlayer.getScore());
        currentChancesLabel.setText("Chances Left: " + currentPlayer.getChancesLeft());

        scorePanel.repaint();
        scorePanel2.repaint();

        if ((player1.getChancesLeft() == 0 && player2.getChancesLeft() == 0) || 
            (player1.getScore() + player1.getChancesLeft() < player2.getScore()) || 
            (player2.getScore() + player2.getChancesLeft() < player1.getScore())) {

            if (player1.getChancesLeft() == 0 && player2.getChancesLeft() == 0) { 
            	
                if (player1.getScore() == player2.getScore()) { 
                    currentPlayerIndex = (currentPlayerIndex == 1) ? 2 : 1; 
                } 
                else {
                    int findNextPlayer = (currentPlayerIndex == 1) ? 2 : 1;
                    
                    if (findNextPlayer == 2) {
                        currentPlayerIndex = findNextPlayer; 
                    } 
                    else {
                        endGame();
                    }
                }
            } 
            else {
                endGame();
            }
        } 
        else {
            currentPlayerIndex = (currentPlayerIndex == 1) ? 2 : 1;
        }
    }

    private void drawScores(Graphics g) {
        int diameter = 17;
        int xOffsetPlayer1 = 5;
        int xOffsetPlayer2 = 275; 
        int yOffset = 15; 

        // Player 1
        for (int i = 0; i < 5; i++) {
            if (i < 5 - player1.getChancesLeft()) { 
                g.setColor(player1.getShotResults()[i] ? Color.GREEN : Color.RED);
            } else {
                g.setColor(Color.GRAY);
            }
            g.fillOval(xOffsetPlayer1 + 25 * i, yOffset, diameter, diameter);
        }

        // Player 2
        for (int i = 0; i < 5; i++) {
            if (i < 5 - player2.getChancesLeft()) { 
                g.setColor(player2.getShotResults()[i] ? Color.GREEN : Color.RED);
            } else { 
                g.setColor(Color.GRAY);
            }
            g.fillOval(xOffsetPlayer2 + 25 * i, yOffset, diameter, diameter);
        }
    }

    private void showGoalScreen() {
        JOptionPane.showMessageDialog(this, "GOAL!!!");
    }

    private void showSaveScreen() {
        JOptionPane.showMessageDialog(this, "SAVE!!!");
    }

    private void endGame() {
        String winner;
        if (player1.getScore() > player2.getScore()) {
            winner = "Player1";
        } else if (player2.getScore() > player1.getScore()) {
            winner = "Player2";
        } else {
            winner = "Draw";
        }
        JOptionPane.showMessageDialog(this, "Game Over! " + winner + " Won!");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("files/game_results.txt", true))) {
            writer.write("  --------------------------------------------------------\n");
            writer.write("  Player1 Score: " + player1.getScore() + "\n");
            writer.write("  Player2 Score: " + player2.getScore() + "\n");
            writer.write("  Winner: " + winner + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        resetGame();
    }

    private void resetGame() {
        player1.reset(5);
        player2.reset(5);
        currentPlayerIndex = 1;

        player1ScoreLabel.setText("Player1 Score: 0");
        player2ScoreLabel.setText("Player2 Score: 0");
        chancesLeftLabel1.setText("Chances Left: 5");
        chancesLeftLabel2.setText("Chances Left: 5");
        scorePanel.repaint();
        scorePanel2.repaint();
    }

    private void showGameRecords() {
        JFrame recordFrame = new JFrame("Game Records");
        recordFrame.setSize(270, 400);
        recordFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextArea recordArea = new JTextArea();
        recordArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(recordArea);
        recordFrame.add(scrollPane);

        try (BufferedReader reader = new BufferedReader(new FileReader("files/game_results.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                recordArea.append(line + "\n");
            }
        } catch (IOException ex) {
            recordArea.setText("Error reading game_results.txt");
        }

        recordFrame.setVisible(true);
    }
    
    public static void main(String[] args) {
        new SoccerPenaltyGame();
    }
}