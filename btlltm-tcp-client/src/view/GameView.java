package view;

import java.util.concurrent.Callable;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import run.ClientRun;
import helper.*;
import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.SwingUtilities;

/**
 *
 * @author admin
 */
public class GameView extends javax.swing.JFrame {

    String competitor = "";
    CountDownTimer matchTimer;
    CountDownTimer waitingClientTimer;

    private String roomId;
    private int round = 1;
    private String productName;
    private String productImage;
    private double totalScore = 0;
    private String currentPlayer;
    private String opponent;
    boolean answer = false;
    /**
     * Creates new form GameView
     */
    public GameView() {
        initComponents();
        setTitle("Hãy chọn giá đúng");
        
        panelPlayAgain.setVisible(false);
        btnSubmit.setVisible(false);
        timerLabel.setVisible(false);
        imageLabel.setPreferredSize(new Dimension(200, 200)); // Điều chỉnh kích thước theo nhu cầu
        
        // close window event
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (JOptionPane.showConfirmDialog(GameView.this, "Bạn có chắc muốn rời phòng không? Bạn sẽ thua!", "LEAVE GAME", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_NO_OPTION){
                    ClientRun.socketHandler.leaveGame(competitor);
                    ClientRun.socketHandler.setRoomIdPresent(null);
                    dispose();
                } 
            }
        });
    }
    
    public void setWaitingRoom () {
        btnSubmit.setVisible(false);
        timerLabel.setVisible(false);
        btnStart.setVisible(false);
        lbWaiting.setText("đợi trận đấu...");
        waitingReplyClient();
    }
    
    public void showAskPlayAgain (String msg) {
        panelPlayAgain.setVisible(true);
        lbResult.setText(msg);
    }
    
    public void hideAskPlayAgain () {
        panelPlayAgain.setVisible(false);
    }
    
    public void setInfoPlayer(String opponentUsername) {
        this.currentPlayer = ClientRun.socketHandler.getLoginUser();
        this.opponent = opponentUsername;
        playerLabel.setText("Chơi game với: " + opponentUsername);
    }
    
    public void setStateHostRoom () {
        answer = false;
        btnStart.setVisible(true);
        lbWaiting.setVisible(false);
    }
    
    public void setStateUserInvited () {
        answer = false;
        btnStart.setVisible(false);
        lbWaiting.setVisible(true);
    }
    
    public void afterSubmit() {
        btnSubmit.setVisible(false);
        lbWaiting.setVisible(true);
        lbWaiting.setText("Đang chờ kết quả từ server...");
        timerLabel.setVisible(false);
    }
    
    public void setRoomId(String roomId) {
        this.roomId = roomId;
        roomIdLabel1.setText("Số phòng: " + roomId);
    }

    public void setRound(int round) {
        this.round = round;
        roundLabel.setText("Lượt chơi: " + round);
    }

    public void setProductInfo(String name, String imagePath) {
        this.productName = name;
        this.productImage = imagePath;
        productLabel.setText("Sản phẩm: " + name);
        // Cập nhật hình ảnh
        try {
            String resourcePath = "/resources/images/products/" + imagePath;
            java.net.URL imageURL = getClass().getResource(resourcePath);
            if (imageURL != null) {
                ImageIcon originalIcon = new ImageIcon(imageURL);
                Image img = originalIcon.getImage();
                Image scaledImg = img.getScaledInstance(imageLabel.getWidth(), imageLabel.getHeight(), Image.SCALE_SMOOTH);
                ImageIcon icon = new ImageIcon(scaledImg);
                imageLabel.setIcon(icon);
            } else {
                System.out.println("Không tìm thấy hình ảnh: " + resourcePath);
                imageLabel.setIcon(null);
                imageLabel.setText("Không tìm thấy hình ảnh");
            }
        } catch (Exception e) {
            e.printStackTrace();
            imageLabel.setIcon(null);
            imageLabel.setText("Lỗi khi tải hình ảnh");
        }
    }

    public String getGuessInput() {
        return guessInput.getText();
    }
    
    public void setStartGame(int matchTimeLimit) {
        answer = false;
        
        btnStart.setVisible(false);
        lbWaiting.setVisible(false);
        btnSubmit.setVisible(true);
        timerLabel.setVisible(true);

        matchTimer = new CountDownTimer(matchTimeLimit);
        matchTimer.setTimerCallBack(
            null,
            (Callable) () -> {
                int currentTick = matchTimer.getCurrentTick();
                timerLabel.setText("Thời gian: " + CustumDateTimeFormatter.secondsToMinutes(currentTick));
                if (currentTick == 0) {
                    afterSubmit();
                }
                return null;
            },
            1
        );
    }

    public void startNextRound(int timeLimit) {
        // Reset các trường nhập liệu và bắt đầu đếm ngược mới
        guessInput.setText("");
        guessInput.setEnabled(true);
        btnSubmit.setEnabled(true);
        setStartGame(timeLimit);
    }
    
    public void waitingReplyClient () {
        waitingClientTimer = new CountDownTimer(10);
        waitingClientTimer.setTimerCallBack(
                null,
                (Callable) () -> {
                    lbWaitingTimer.setText("" + CustumDateTimeFormatter.secondsToMinutes(waitingClientTimer.getCurrentTick()));
                    if (lbWaitingTimer.getText().equals("00:00") && answer == false) {
                        hideAskPlayAgain();
                    }
                    return null;
                },
                1
        );
    }
    
    public void showMessage(String msg){
        JOptionPane.showMessageDialog(this, msg);
    }
    
    public void showResultDialog(String winner, double actualPrice, float score) {
    SwingUtilities.invokeLater(() -> {
        String message = String.format("Kết quả lượt chơi:\n\n%s\n\nGiá thực: %.2f\nĐiểm: %.2f", 
                                       winner, actualPrice, score);
        JOptionPane.showMessageDialog(this, message, "Kết quả", JOptionPane.INFORMATION_MESSAGE);
    });
}
    public void showRoundResult(String winner, double actualPrice, 
                            double guessClient1, double guessClient2,
                            double roundScoreClient1, double roundScoreClient2,
                            double totalScoreClient1, double totalScoreClient2,
                            String nameClient1, String nameClient2) {
        SwingUtilities.invokeLater(() -> {
            String message;
            boolean isClient1 = currentPlayer.equals(ClientRun.socketHandler.getLoginUser());
            if (winner.equals(currentPlayer)) {
                message = "Bạn thắng lượt này!";
            } else if (winner.equals("DRAW")) {
                message = "Hòa!";
            } else {
                message = "Bạn thua lượt này.";
            }
            message += String.format("\nGiá thực: %,.0f", actualPrice);
            message += String.format("\n\n%s:\nDự đoán: %,.0f\nĐiểm lượt này: %.1f\nTổng điểm: %.1f",
                                    nameClient1, guessClient1, roundScoreClient1, totalScoreClient1);
            message += String.format("\n\n%s:\nDự đoán: %,.0f\nĐiểm lượt này: %.1f\nTổng điểm: %.1f",
                                    nameClient2, guessClient2, roundScoreClient2, totalScoreClient2);
            JOptionPane.showMessageDialog(this, message, "Kết quả lượt chơi", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    public void showGameOver(String winner, double scoreClient1, double scoreClient2, String nameClient1, String nameClient2) {
        SwingUtilities.invokeLater(() -> {
            String message;
            boolean isClient1 = currentPlayer.equals(ClientRun.socketHandler.getLoginUser());
            double playerScore = isClient1 ? scoreClient1 : scoreClient2;
            double opponentScore = isClient1 ? scoreClient2 : scoreClient1;
            String playerName = isClient1 ? nameClient1 : nameClient2;
            String opponentName = isClient1 ? nameClient2 : nameClient1;

            if (winner.equals(currentPlayer)) {
                message = "Bạn thắng trận đấu!";
            } else if (winner.equals("DRAW")) {
                message = "Kết quả hòa!";
            } else {
                message = "Bạn thua trận đấu.";
            }
            message += String.format("\n%s: %.1f\n%s: %.1f", 
                                    playerName, playerScore,
                                    opponentName, opponentScore);

            JOptionPane.showMessageDialog(this, message, "Kết quả trận đấu", JOptionPane.INFORMATION_MESSAGE);
            setWaitingRoom();
            showAskPlayAgain("Bạn có muốn chơi tiếp không?");
        });
    }
    
    public void pauseTime () {
        // pause timer
        matchTimer.pause();
    }
        
    public int getRemainingTime() {
        return matchTimer.getCurrentTick();
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        buttonGroup4 = new javax.swing.ButtonGroup();
        btnSubmit = new javax.swing.JButton();
        btnStart = new javax.swing.JButton();
        lbWaiting = new javax.swing.JLabel();
        panelPlayAgain = new javax.swing.JPanel();
        lbWaitingTimer = new javax.swing.JLabel();
        btnYes = new javax.swing.JButton();
        btnNo = new javax.swing.JButton();
        lbResult = new javax.swing.JLabel();
        timerLabel = new javax.swing.JLabel();
        productLabel = new javax.swing.JLabel();
        playerLabel = new javax.swing.JLabel();
        roundLabel = new javax.swing.JLabel();
        guessInput = new javax.swing.JTextField();
        imageLabel = new javax.swing.JLabel();
        roomIdLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        btnSubmit.setText("Xác nhận");
        btnSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubmitActionPerformed(evt);
            }
        });

        btnStart.setText("Bắt đầu");
        btnStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartActionPerformed(evt);
            }
        });

        lbWaiting.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lbWaiting.setText("Đợi chủ phòng bắt đầu game...");

        panelPlayAgain.setBorder(javax.swing.BorderFactory.createTitledBorder("Question?"));

        lbWaitingTimer.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lbWaitingTimer.setForeground(new java.awt.Color(204, 0, 0));
        lbWaitingTimer.setText("00:00");

        btnYes.setText("Tiếp tục");
        btnYes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnYesActionPerformed(evt);
            }
        });

        btnNo.setText("Dừng lại");
        btnNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNoActionPerformed(evt);
            }
        });

        lbResult.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        lbResult.setForeground(new java.awt.Color(204, 0, 51));
        lbResult.setText("Bạn có muốn chơi lại không?");

        javax.swing.GroupLayout panelPlayAgainLayout = new javax.swing.GroupLayout(panelPlayAgain);
        panelPlayAgain.setLayout(panelPlayAgainLayout);
        panelPlayAgainLayout.setHorizontalGroup(
            panelPlayAgainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPlayAgainLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbResult, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbWaitingTimer, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnYes, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnNo, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35))
        );
        panelPlayAgainLayout.setVerticalGroup(
            panelPlayAgainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPlayAgainLayout.createSequentialGroup()
                .addGroup(panelPlayAgainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelPlayAgainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lbResult, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lbWaitingTimer, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelPlayAgainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(btnNo, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnYes)))
                .addContainerGap())
        );

        timerLabel.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        timerLabel.setText("Thời gian: 30s");

        productLabel.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        productLabel.setText("Sản phẩm: [name]");

        playerLabel.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        playerLabel.setText("Người chơi: username");

        roundLabel.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        roundLabel.setText("Lượt chơi: 0");

        guessInput.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N

        imageLabel.setBackground(new java.awt.Color(204, 204, 255));
        imageLabel.setText("Hình ảnh");

        roomIdLabel1.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        roomIdLabel1.setText("Số phòng: 0");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(panelPlayAgain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnStart, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lbWaiting, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(56, 56, 56))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(roundLabel)
                            .addComponent(playerLabel)
                            .addComponent(guessInput, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(productLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(imageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(121, 121, 121))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(roomIdLabel1)
                            .addComponent(timerLabel))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(58, 58, 58)
                .addComponent(roomIdLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(playerLabel)
                        .addGap(18, 18, 18)
                        .addComponent(roundLabel)
                        .addGap(18, 18, 18)
                        .addComponent(productLabel)
                        .addGap(48, 48, 48)
                        .addComponent(guessInput, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(imageLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(57, 57, 57)
                .addComponent(timerLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnStart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbWaiting, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addComponent(panelPlayAgain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartActionPerformed
        ClientRun.socketHandler.startGame(competitor);
    }//GEN-LAST:event_btnStartActionPerformed

    private void btnSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmitActionPerformed
        String guess = getGuessInput();
        if (guess.isEmpty()) {
            showMessage("Vui lòng nhập giá dự đoán!");
        } else {
            ClientRun.socketHandler.submitResult(competitor);
        }
    }//GEN-LAST:event_btnSubmitActionPerformed

    private void btnNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNoActionPerformed
        ClientRun.socketHandler.notAcceptPlayAgain();
        answer = true;
        hideAskPlayAgain();
    }//GEN-LAST:event_btnNoActionPerformed

    private void btnYesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnYesActionPerformed
        ClientRun.socketHandler.acceptPlayAgain();
        answer = true;
        hideAskPlayAgain();
    }//GEN-LAST:event_btnYesActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GameView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GameView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GameView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GameView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GameView().setVisible(true);
            }
        });
    }

    public boolean isAnswer() {
        return answer;
    }

    public void setAnswer(boolean answer) {
        this.answer = answer;
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnNo;
    private javax.swing.JButton btnStart;
    private javax.swing.JButton btnSubmit;
    private javax.swing.JButton btnYes;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private javax.swing.JTextField guessInput;
    private javax.swing.JLabel imageLabel;
    private javax.swing.JLabel lbResult;
    private javax.swing.JLabel lbWaiting;
    private javax.swing.JLabel lbWaitingTimer;
    private javax.swing.JPanel panelPlayAgain;
    private javax.swing.JLabel playerLabel;
    private javax.swing.JLabel productLabel;
    private javax.swing.JLabel roomIdLabel1;
    private javax.swing.JLabel roundLabel;
    private javax.swing.JLabel timerLabel;
    // End of variables declaration//GEN-END:variables
}
