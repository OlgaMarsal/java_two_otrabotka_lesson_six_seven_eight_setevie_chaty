package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private String nickname;
    private String login;

    public ClientHandler(Server server, Socket socket) {
        try{
            this.server = server;
            this.socket = socket;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                    try {
                        //цикл аутентификации
                        while (true) {
                            String str = in.readUTF();

                            if (str.startsWith("/auth ")) {
                                String[] token = str.split("\\s");
                                if (token.length < 3) {
                                    continue;
                                }
                                String newNick = server
                                        .getAuthService()
                                        .getNicknameByLoginAndPassword(token[1], token[2]);
                                login = token[1];

                                if (newNick != null) {
                                    if(!server.isLoginAuthenticated(login)) {
                                        nickname = newNick;
                                        sendMsg("/authok " + nickname);
                                        server.subscribe(this);
                                        System.out.println("Клиент " + nickname + " подключился");
                                        break;
                                    }else {
                                        sendMsg("С данной учетной записью уже прошли аутентификацию");
                                    }
                                } else {
                                    sendMsg("Неверный логин / пароль");
                                }
                            }
                        }

                        //цикл работы
                        while (true) {
                            String str = in.readUTF();
// / косая черта считается за служебное сообщение и начинается на w, s+ это если повторяются пробелы считать за 1

                            if (str.startsWith("/")) {
                                System.out.println(str);
                                if (str.equals("/end")) {
                                    out.writeUTF("/end");
                                    break;
                                }
                                if (str.startsWith("/w")) {
                                    String[] token = str.split("\\s+", 3);
                                    if (token.length < 3) {
                                        continue;
                                    }
                                    server.privateMsg(this, token[1], token[2]);
                                }
                            } else {
                                server.broadcastMsg(this, str);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        System.out.println("Клиет отключился");
                        server.unsubscribe(this);
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNickname() {
        return nickname;
    }

    //метод получения логина
    public String getLogin() {
        return login;
    }
}
