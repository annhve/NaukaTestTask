package com.annhve.naukatesttask.util;

import com.annhve.naukatesttask.model.User;

public class UserRepository {
    private User loggedUser;
    private final DbConnector dbConnector;

    public UserRepository(DbConnector dbConnector) {
        this.dbConnector = dbConnector;
    }

    public User signIn(String login, String password) throws Exception {
        this.loggedUser = dbConnector.getUser(login, password);
        return this.loggedUser;
    }

    public void signOut() {
        this.loggedUser = null;
    }

    public User getLoggedUser() {
        return loggedUser;
    }
}
