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

        // JLayeredPane 생성
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, 750, 470);

        // 배경 이미지 설정
        ImageIcon icon = new ImageIcon("images\\GoalPost.png");
        Image scaledImage = icon.getImage().getScaledInstance(750, 470, Image.SCALE_SMOOTH);
        icon = new ImageIcon(scaledImage);
        JLabel backgroundLabel = new JLabel(icon);
        backgroundLabel.setBounds(0, 0, 750, 470); // 이미지 위치와 크기 설정

        // 버튼 설정
        JButton leftButton = new JButton("Left");
        leftButton.setBounds(220, 150, 73, 50); 
        JButton centerButton = new JButton("Center");
        centerButton.setBounds(345, 150, 73, 50);
        JButton rightButton = new JButton("Right");
        rightButton.setBounds(465, 150, 73, 50);
        JButton gameRecordButton = new JButton("Game Record");
        gameRecordButton.setBounds(600, 400, 120, 30);

        // 버튼에 ActionListener 추가
        leftButton.addActionListener(e -> shoot(0));
        centerButton.addActionListener(e -> shoot(1));
        rightButton.addActionListener(e -> shoot(2));
        gameRecordButton.addActionListener(e -> showGameRecords());

        // 점수 및 기회 표시
        player1ScoreLabel = new JLabel("Player1 Score: 0");
        player1ScoreLabel.setBounds(50, 20, 150, 30);
        player2ScoreLabel = new JLabel("Player2 Score: 0");
        player2ScoreLabel.setBounds(600, 20, 150, 30);
        chancesLeftLabel1 = new JLabel("Chances Left: 5");
        chancesLeftLabel1.setBounds(50, 50, 150, 30);
        chancesLeftLabel2 = new JLabel("Chances Left: 5");
        chancesLeftLabel2.setBounds(600, 50, 150, 30);

        // 점수판 패널
        scorePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawScores(g);
            }
        };
        scorePanel.setOpaque(false); // 배경을 투명하게 설정
        scorePanel.setBounds(180, 45, 400, 50); // 중앙에 위치하도록 설정

        // 점수 표시를 위한 패널
        scorePanel2 = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 25));
                g.drawString(player1.getScore() + "          :          " + player2.getScore(), 10, 30);
            }
        };
        scorePanel2.setOpaque(false); // 배경을 투명하게 설정
        scorePanel2.setBounds(280, 10, 200, 50); // 중앙에 위치하도록 설정

        
        // 레이어에 컴포넌트 추가
        layeredPane.add(backgroundLabel, Integer.valueOf(0)); // 배경 이미지 레이어 0
        layeredPane.add(leftButton, Integer.valueOf(1)); // 버튼을 이미지 위 레이어에 추가
        layeredPane.add(centerButton, Integer.valueOf(1));
        layeredPane.add(rightButton, Integer.valueOf(1));
        layeredPane.add(gameRecordButton, Integer.valueOf(1));
        layeredPane.add(player1ScoreLabel, Integer.valueOf(1));
        layeredPane.add(player2ScoreLabel, Integer.valueOf(1));
        layeredPane.add(chancesLeftLabel1, Integer.valueOf(1));
        layeredPane.add(chancesLeftLabel2, Integer.valueOf(1));
        layeredPane.add(scorePanel, Integer.valueOf(1));
        layeredPane.add(scorePanel2, Integer.valueOf(2));

        // 프레임에 레이어 추가
        add(layeredPane);

        setSize(750, 470); // 창 크기 설정
        setResizable(false); // 창 크기 고정
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
            showGoalScreen(); // 골 화면
        } 
        else {
        	if (currentPlayer.getChancesLeft() > 0) currentPlayer.setShotResult(shotIndex, false);
            showSaveScreen(); // 세이브 화면
        }

        if (currentPlayer.getChancesLeft() > 0) currentPlayer.decreaseChances();
        currentScoreLabel.setText("Player" + currentPlayerIndex + " Score: " + currentPlayer.getScore());
        currentChancesLabel.setText("Chances Left: " + currentPlayer.getChancesLeft());

        scorePanel.repaint();
        scorePanel2.repaint();

        // 다음 플레이어로 변경 또는 게임 종료
        if ((player1.getChancesLeft() == 0 && player2.getChancesLeft() == 0) || 
            (player1.getScore() + player1.getChancesLeft() < player2.getScore()) || 
            (player2.getScore() + player2.getChancesLeft() < player1.getScore())) {

            if (player1.getChancesLeft() == 0 && player2.getChancesLeft() == 0) { // 기회를 모두 소진한 경우
            	
                if (player1.getScore() == player2.getScore()) { // 동점인 경우 골든골로 진행
                    currentPlayerIndex = (currentPlayerIndex == 1) ? 2 : 1; // 다음 플레이어로 전환
                } 
                else {
                    // 동점이 아닌 경우, 다음 플레이어 확인 후 종료 결정
                    int findNextPlayer = (currentPlayerIndex == 1) ? 2 : 1;
                    
                    if (findNextPlayer == 2) {
                        currentPlayerIndex = findNextPlayer; // Player 2에게 기회 제공 (골든골)
                    } 
                    else {
                        endGame();
                    }
                }
            } 
            else {
                // 더 이상 이길 수 없는 상황인 경우 게임 종료
                endGame();
            }
        } 
        else {
            currentPlayerIndex = (currentPlayerIndex == 1) ? 2 : 1; // 게임 계속 진행
        }
    }

    private void drawScores(Graphics g) {
        int diameter = 17;
        int xOffsetPlayer1 = 5; // Player 1의 시작 x 위치
        int xOffsetPlayer2 = 275; // Player 2의 시작 x 위치
        int yOffset = 15; // y 위치 (점수 표시 높이 조정)

        // Player 1 점수 표시
        for (int i = 0; i < 5; i++) {
            if (i < 5 - player1.getChancesLeft()) { // 이미 시도한 경우
                g.setColor(player1.getShotResults()[i] ? Color.GREEN : Color.RED);
            } else { // 아직 시도하지 않은 경우
                g.setColor(Color.GRAY);
            }
            g.fillOval(xOffsetPlayer1 + 25 * i, yOffset, diameter, diameter);
        }

        // Player 2 점수 표시
        for (int i = 0; i < 5; i++) {
            if (i < 5 - player2.getChancesLeft()) { // 이미 시도한 경우
                g.setColor(player2.getShotResults()[i] ? Color.GREEN : Color.RED);
            } else { // 아직 시도하지 않은 경우
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