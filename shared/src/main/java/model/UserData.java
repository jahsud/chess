package model;

import com.google.gson.Gson;

public record UserData(String username, String password, String email) {
    public UserData setUsername (String username) {
        return new UserData(username, this.password, this.email);
    }

    public UserData setPassword (String password) {
        return new UserData(this.username, password, this.email);
    }

    public UserData setEmail (String email) {
        return new UserData(this.username, this.password, email);
    }

    public String toString () {
        return new Gson().toJson(this);
    }
}
