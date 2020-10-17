package server;

import java.util.ArrayList;
import java.util.List;

public class SimpleAuthService implements AuthService {
    private class UserDate {
        String login;
        String password;
        String nickname;

        public UserDate(String login, String password, String nickname) {
            this.login = login;
            this.password = password;
            this.nickname = nickname;
        }
    }

    List<UserDate> users;

    public SimpleAuthService() {
        users = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
           users.add(new UserDate("login" + i, "pass" + i, "nick" + i));
        }

        users.add(new UserDate("qwe" , "qwe" , "qwe" ));
        users.add(new UserDate("asd" , "asd" , "asd" ));
        users.add(new UserDate("zxc" , "zxc" , "zxc" ));
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        for (UserDate user : users) {
            if (user.login.equals(login) && user.password.equals(password)) {
                return user.nickname;
            }
        }
        return null;
    }
}
