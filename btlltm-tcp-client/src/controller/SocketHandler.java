package controller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import run.ClientRun;

public class SocketHandler {
     
    Socket s;
    DataInputStream dis;
    DataOutputStream dos;

    String loginUser = null; // lưu tài khoản đăng nhập hiện tại
    String roomIdPresent = null; // lưu room hiện tại
    float score = 0;
    
    Thread listener = null;

    public String connect(String addr, int port) {
        try {
            // getting ip 
            InetAddress ip = InetAddress.getByName(addr);

            // establish the connection with server port 
            s = new Socket();
            s.connect(new InetSocketAddress(ip, port), 4000);
            System.out.println("Connected to " + ip + ":" + port + ", localport:" + s.getLocalPort());

            // obtaining input and output streams
            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());

            // close old listener
            if (listener != null && listener.isAlive()) {
                listener.interrupt();
            }

            // listen to server
            listener = new Thread(this::listen);
            listener.start();

            // connect success
            return "success";

        } catch (IOException e) {

            // connect failed
            return "failed;" + e.getMessage();
        }
    }

    private void listen() {
        boolean running = true;

        while (running) {
            try {
                // receive the data from server
                String received = dis.readUTF();

                System.out.println("RECEIVED: " + received);

                String type = received.split(";")[0];

                switch (type) {
                    case "LOGIN":
                        onReceiveLogin(received);
                        break;
                    case "REGISTER":
                        onReceiveRegister(received);
                        break;
                    case "GET_LIST_ONLINE":
                        onReceiveGetListOnline(received);
                        break;
                    case "LOGOUT":
                        onReceiveLogout(received);
                        break;
                    case "INVITE_TO_CHAT":
                        onReceiveInviteToChat(received);
                        break;
                    case "GET_INFO_USER":
                        onReceiveGetInfoUser(received);
                        break;
                    case "ACCEPT_MESSAGE":
                        onReceiveAcceptMessage(received);
                        break;
                    case "NOT_ACCEPT_MESSAGE":
                        onReceiveNotAcceptMessage(received);
                        break;
                    case "LEAVE_TO_CHAT":
                        onReceiveLeaveChat(received);
                        break;
                    case "CHAT_MESSAGE":
                        onReceiveChatMessage(received);
                        break;
                    case "INVITE_TO_PLAY":
                        onReceiveInviteToPlay(received);
                        break;
                    case "ACCEPT_PLAY":
                        onReceiveAcceptPlay(received);
                        break;
                    case "NOT_ACCEPT_PLAY":
                        onReceiveNotAcceptPlay(received);
                        break;
                    case "LEAVE_TO_GAME":
                        onReceiveLeaveGame(received);
                        break;
                    case "CHECK_STATUS_USER":
                        onReceiveCheckStatusUser(received);
                        break;
                    case "START_GAME":
                        onReceiveStartGame(received);
                        break; 
                    case "NEXT_ROUND":
                        onReceiveNextRound(received);
                        break;
                    case "ROUND_RESULT":
                        onReceiveRoundResult(received);
                        break;
                    case "GAME_OVER":
                        onReceiveGameOver(received);
                        break;
                    case "RESULT_GAME":
                        onReceiveResultGame(received);
                        break;
                    case "ASK_PLAY_AGAIN":
                        onReceiveAskPlayAgain(received);
                        break;
                        
                    case "EXIT":
                        running = false;
                }

            } catch (IOException ex) {
                Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
                running = false;
            }
        }

        try {
            // closing resources
            s.close();
            dis.close();
            dos.close();
        } catch (IOException ex) {
            Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        // alert if connect interup
        JOptionPane.showMessageDialog(null, "Mất kết nối tới server", "Lỗi", JOptionPane.ERROR_MESSAGE);
        ClientRun.closeAllScene();
        ClientRun.openScene(ClientRun.SceneName.CONNECTSERVER);
    }
    
    /***
     * Handle from client
     */
    public void login(String email, String password) {
        // prepare data
        String data = "LOGIN" + ";" + email + ";" + password;
        // send data
        sendData(data);
    }
    
    public void register(String email, String password) {
        // prepare data
        String data = "REGISTER" + ";" + email + ";" + password;
        // send data
        sendData(data);
    }
    
    public void logout () {
        this.loginUser = null;
        sendData("LOGOUT");
    }
    
    public void close () {
        sendData("CLOSE");
    }
    
    public void getListOnline() {
        sendData("GET_LIST_ONLINE");
    }
    
    public void getInfoUser(String username) {
        sendData("GET_INFO_USER;" + username);
    }
    
    public void checkStatusUser(String username) {
        sendData("CHECK_STATUS_USER;" + username);
    }
    
    // Chat
    public void inviteToChat (String userInvited) {
        sendData("INVITE_TO_CHAT;" + loginUser + ";" + userInvited);
    }
    
    public void leaveChat (String userInvited) {
        sendData("LEAVE_TO_CHAT;" + loginUser + ";" + userInvited);
    }
    
    public void sendMessage (String userInvited, String message) {
        String chat = "[" + loginUser + "] : " + message + "\n";
        ClientRun.messageView.setContentChat(chat);
            
        sendData("CHAT_MESSAGE;" + loginUser + ";" + userInvited  + ";" + message);
    }
    
    // Play game
    public void inviteToPlay (String userInvited) {
        sendData("INVITE_TO_PLAY;" + loginUser + ";" + userInvited);
    }
    
    public void leaveGame (String userInvited) { 
        sendData("LEAVE_TO_GAME;" + loginUser + ";" + userInvited + ";" + roomIdPresent);
    }
    
    public void startGame (String userInvited) { 
        sendData("START_GAME;" + loginUser + ";" + userInvited + ";" + roomIdPresent);
    }
        
    public void submitResult(String competitor) { 
        String guess = ClientRun.gameView.getGuessInput();
        if (guess.isEmpty()) {
            ClientRun.gameView.showMessage("Vui lòng nhập giá dự đoán!");
        } else {
            ClientRun.gameView.pauseTime();
            int remainingTime = ClientRun.gameView.getRemainingTime();
            int elapsedTime = 30 - remainingTime; // Giả sử thời gian ban đầu là 30 giây

            sendData("SUBMIT_RESULT;" + loginUser + ";" + competitor + ";" + roomIdPresent + ";" + guess + ";" + elapsedTime);
            ClientRun.gameView.afterSubmit();
        }
    }
    
    public void acceptPlayAgain() {
        sendData("ASK_PLAY_AGAIN;YES;" + loginUser);
    }
    
    public void notAcceptPlayAgain() {
        sendData("ASK_PLAY_AGAIN;NO;" + loginUser);
    }
    
    /***
     * Handle send data to server
     */
    public void sendData(String data) {
        try {
            dos.writeUTF(data);
        } catch (IOException ex) {
            Logger.getLogger(SocketHandler.class
                .getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /***
     * Handle receive data from server
     */
    private void onReceiveLogin(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("failed")) {
            // hiển thị lỗi
            String failedMsg = splitted[2];
            JOptionPane.showMessageDialog(ClientRun.loginView, failedMsg, "Lỗi", JOptionPane.ERROR_MESSAGE);

        } else if (status.equals("success")) {
            // lưu user login
            this.loginUser = splitted[2];
            this.score = Float.parseFloat(splitted[3]) ;
            // chuyển scene
            ClientRun.closeScene(ClientRun.SceneName.LOGIN);
            ClientRun.openScene(ClientRun.SceneName.HOMEVIEW);

            // auto set info user
            ClientRun.homeView.setUsername(loginUser);
            ClientRun.homeView.setUserScore(score);
        }
    }
    
    private void onReceiveRegister(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("failed")) {
            // hiển thị lỗi
            String failedMsg = splitted[2];
            JOptionPane.showMessageDialog(ClientRun.registerView, failedMsg, "Lỗi", JOptionPane.ERROR_MESSAGE);

        } else if (status.equals("success")) {
            JOptionPane.showMessageDialog(ClientRun.registerView, "Đăng ký thành công! Hãy đăng nhập!");
            // chuyển scene
            ClientRun.closeScene(ClientRun.SceneName.REGISTER);
            ClientRun.openScene(ClientRun.SceneName.LOGIN);
        }
    }
    
    private void onReceiveGetListOnline(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("success")) {
            int userCount = Integer.parseInt(splitted[2]);

            // https://niithanoi.edu.vn/huong-dan-thao-tac-voi-jtable-lap-trinh-java-swing.html
            Vector vheader = new Vector();
            vheader.add("User");

            Vector vdata = new Vector();
            if (userCount > 1) {
                for (int i = 3; i < userCount + 3; i++) {
                    String user = splitted[i];
                    if (!user.equals(loginUser) && !user.equals("null")) {
                        Vector vrow = new Vector();
                        vrow.add(user);
                        vdata.add(vrow);
                    }
                }

                ClientRun.homeView.setListUser(vdata, vheader);
            } else {
                ClientRun.homeView.resetTblUser();
            }
            
        } else {
            JOptionPane.showMessageDialog(ClientRun.loginView, "Have some error!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void onReceiveGetInfoUser(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("success")) {
            String userName = splitted[2];
            String userScore =  splitted[3];
            String userWin =  splitted[4];
            String userDraw =  splitted[5];
            String userLose =  splitted[6];
            String userAvgCompetitor =  splitted[7];
            String userAvgTime =  splitted[8];
            String userStatus = splitted[9];
            
            ClientRun.openScene(ClientRun.SceneName.INFOPLAYER);
            ClientRun.infoPlayerView.setInfoUser(userName, userScore, userWin, userDraw, userLose, userAvgCompetitor, userAvgTime, userStatus);
        }
    }
    
    private void onReceiveLogout(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("success")) {
            ClientRun.closeScene(ClientRun.SceneName.HOMEVIEW);
            ClientRun.openScene(ClientRun.SceneName.LOGIN);
        }
    }
    
    // chat
    private void onReceiveInviteToChat(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("success")) {
            String userHost = splitted[2];
            String userInvited = splitted[3];
            if (JOptionPane.showConfirmDialog(ClientRun.homeView, userHost + " muốn chat với bạn?", "Chat?", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_NO_OPTION){
                ClientRun.openScene(ClientRun.SceneName.MESSAGEVIEW);
                ClientRun.messageView.setInfoUserChat(userHost);
                sendData("ACCEPT_MESSAGE;" + userHost + ";" + userInvited);
            } else {
                sendData("NOT_ACCEPT_MESSAGE;" + userHost + ";" + userInvited);
            }
        }
    }
    
    private void onReceiveAcceptMessage(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("success")) {
            String userHost = splitted[2];
            String userInvited = splitted[3];
                
            ClientRun.openScene(ClientRun.SceneName.MESSAGEVIEW);
            ClientRun.messageView.setInfoUserChat(userInvited);
        }
    }
    
    private void onReceiveNotAcceptMessage(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("success")) {
            String userHost = splitted[2];
            String userInvited = splitted[3];
                
            JOptionPane.showMessageDialog(ClientRun.homeView, userInvited + " don't want to chat with you!");
        }
    }
    
    private void onReceiveLeaveChat(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("success")) {
            String userHost = splitted[2];
            String userInvited = splitted[3];
            
            ClientRun.closeScene(ClientRun.SceneName.MESSAGEVIEW);   
            JOptionPane.showMessageDialog(ClientRun.homeView, userHost + " leave to chat!");
        }
    }
    
    private void onReceiveChatMessage(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("success")) {
            String userHost = splitted[2];
            String userInvited = splitted[3];
            String message = splitted[4];
            
            String chat = "[" + userHost + "] : " + message + "\n";
            ClientRun.messageView.setContentChat(chat);
        }
    }
    
    // play game
    private void onReceiveInviteToPlay(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("success")) {
            String userHost = splitted[2];
            String userInvited = splitted[3];
            String roomId = splitted[4];
            if (JOptionPane.showConfirmDialog(ClientRun.homeView, userHost + " muốn chơi game cùng bạn?", "Game?", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_NO_OPTION){
                ClientRun.openScene(ClientRun.SceneName.GAMEVIEW);
                ClientRun.gameView.setInfoPlayer(userHost);
                roomIdPresent = roomId;
                ClientRun.gameView.setStateUserInvited();
                sendData("ACCEPT_PLAY;" + userHost + ";" + userInvited + ";" + roomId);
            } else {
                sendData("NOT_ACCEPT_PLAY;" + userHost + ";" + userInvited + ";" + roomId);
            }
        }
    }
    
    private void onReceiveAcceptPlay(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("success")) {
            String userHost = splitted[2];
            String userInvited = splitted[3];
            roomIdPresent = splitted[4];
            ClientRun.openScene(ClientRun.SceneName.GAMEVIEW);
            ClientRun.gameView.setInfoPlayer(userInvited);
            ClientRun.gameView.setStateHostRoom();
        }
    }
    
    private void onReceiveNotAcceptPlay(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("success")) {
            String userHost = splitted[2];
            String userInvited = splitted[3];
                
            JOptionPane.showMessageDialog(ClientRun.homeView, userInvited + " không muốn chơi game cùng bạn!");
        }
    }
    
    private void onReceiveLeaveGame(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("success")) {
            String user1 = splitted[2];
            String user2 = splitted[3];

            roomIdPresent = null;
            ClientRun.closeScene(ClientRun.SceneName.GAMEVIEW);   
            JOptionPane.showMessageDialog(ClientRun.homeView, user1 + " leave to game!");
        }
    }
     
    private void onReceiveCheckStatusUser(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[2];
        ClientRun.homeView.setStatusCompetitor(status);
    }
    
    private void onReceiveStartGame(String received) {
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("success")) {
            String roomId = splitted[2];
            String productName = splitted[3];
            String imagePath = splitted[4];
//            int roundGame = Integer.parseInt(splitted[5]);
            
            SwingUtilities.invokeLater(() -> {
                ClientRun.gameView.setRoomId(roomId);
                ClientRun.gameView.setProductInfo(productName, imagePath);
//                ClientRun.gameView.setStartGame(30, productName, imagePath);
                ClientRun.gameView.setStartGame(30);
            });
        }
    }
    
    private void onReceiveNextRound(String received) {
        String[] splitted = received.split(";");
        if (splitted[1].equals("success")) {
            String roomId = splitted[2];
            String productName = splitted[3];
            String imagePath = splitted[4];
            int currentRound = Integer.parseInt(splitted[5]);

            System.out.println("currentRound: " + currentRound);
            SwingUtilities.invokeLater(() -> {
                ClientRun.gameView.setRoomId(roomId);
                ClientRun.gameView.setProductInfo(productName, imagePath);
                ClientRun.gameView.setRound(currentRound);
                ClientRun.gameView.startNextRound(30); // 30 giây cho mỗi vòng
            });
        }
    }
    
    private void onReceiveRoundResult(String received) {
        String[] splitted = received.split(";");
        if (splitted[1].equals("success")) {
            String winner = splitted[2];
            double actualPrice = Double.parseDouble(splitted[3]);
            double guessClient1 = Double.parseDouble(splitted[4]);
            double guessClient2 = Double.parseDouble(splitted[5]);
            double roundScoreClient1 = Double.parseDouble(splitted[6]);
            double roundScoreClient2 = Double.parseDouble(splitted[7]);
            double totalScoreClient1 = Double.parseDouble(splitted[8]);
            double totalScoreClient2 = Double.parseDouble(splitted[9]);
            String nameClient1 = splitted[10];
            String nameClient2 = splitted[11];

            System.out.println("roundScoreClient1: " + roundScoreClient1);
            System.out.println("roundScoreClient2: " + roundScoreClient2);
            SwingUtilities.invokeLater(() -> {
                ClientRun.gameView.showRoundResult(winner, actualPrice, guessClient1, guessClient2, 
                                                   roundScoreClient1, roundScoreClient2, 
                                                   totalScoreClient1, totalScoreClient2,
                                                   nameClient1, nameClient2);
            });
        }
    }

    private void onReceiveGameOver(String received) {
        String[] splitted = received.split(";");
        if (splitted[1].equals("success")) {
            String winner = splitted[2];
            String nameClient1 = splitted[3];
            String nameClient2 = splitted[4];
            String roomId = splitted[5];
            double scoreClient1 = Double.parseDouble(splitted[6]);
            double scoreClient2 = Double.parseDouble(splitted[7]);
            SwingUtilities.invokeLater(() -> {
                ClientRun.gameView.showGameOver(winner, scoreClient1, scoreClient2, nameClient1, nameClient2);
            });
        }
    }
    
    private void onReceiveResultGame(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];
        String result = splitted[2];
        String user1 = splitted[3];
        String user2 = splitted[4];
        String roomId = splitted[5];
        double actualPrice = Double.parseDouble(splitted[6]);

        if (status.equals("success")) {
            ClientRun.gameView.setWaitingRoom();
            String winner;
            if (result.equals("DRAW")) {
                winner = "Kết quả hòa.";
            } else if (result.equals(loginUser)) {
                winner = "Bạn thắng!";
            } else {
                winner = "Bạn thua.";
            }
            SwingUtilities.invokeLater(() -> {
            ClientRun.gameView.showResultDialog(winner, actualPrice, score);
            ClientRun.gameView.setWaitingRoom();
            String message = "Bạn có muốn chơi tiếp không?";
            ClientRun.gameView.showAskPlayAgain(message);
        });
        }
    }
    
    private void onReceiveAskPlayAgain(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];
        
        if (status.equals("NO")) {
            ClientRun.closeScene(ClientRun.SceneName.GAMEVIEW);
            JOptionPane.showMessageDialog(ClientRun.homeView, "End Game!");
        } else {
            if (loginUser.equals(splitted[2])) {
                ClientRun.gameView.setStateHostRoom();
            } else {
                ClientRun.gameView.setStateUserInvited();
            }
        }
    }  
    
    // get set
    public String getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(String loginUser) {
        this.loginUser = loginUser;
    }

    public Socket getS() {
        return s;
    }

    public void setS(Socket s) {
        this.s = s;
    }

    public String getRoomIdPresent() {
        return roomIdPresent;
    }

    public void setRoomIdPresent(String roomIdPresent) {
        this.roomIdPresent = roomIdPresent;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }
    
}
