package ui;

import com.google.gson.Gson;
import model.*;
import request.*;
import result.*;

import java.io.*;
import java.net.*;

import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade (String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public RegisterResult register (String username, String password, String email) throws ResponseException {
        var path = "/user";
        return makeRequest("POST", path, new RegisterRequest(username, password, email), RegisterResult.class);
    }

    public LoginResult login (String username, String password) throws ResponseException {
        var path = "/session";
        return makeRequest("POST", path, new LoginRequest(username, password), LoginResult.class);
    }

    public LogoutResult logout (String authToken) throws ResponseException {
        var path = "/session";
        return makeRequest("DELETE", path, new LogoutRequest(authToken), null);
    }

    public JoinGameResult observe (String authToken, Integer gameID) throws ResponseException {
        var path = "/game";
        return makeRequest("PUT", path, new JoinGameRequest(authToken, null, gameID), JoinGameResult.class);
    }

    public JoinGameResult joinGame (String authToken, String playerColor, Integer gameID) throws ResponseException {
        var path = "/game";
        return makeRequest("PUT", path, new JoinGameRequest(authToken, playerColor, gameID), JoinGameResult.class);
    }

    public ListGamesResult listGames (String authToken) throws ResponseException {
        var path = "/game";
        return makeRequest("GET", path, new ListGamesRequest(authToken), ListGamesResult.class);
    }

    public CreateGameResult createGame (String authToken) throws ResponseException {
        var path = "/game";
        return makeRequest("POST", path, new CreateGameRequest(authToken, gameName), CreateGameResult.class);
    }

    private <T> T makeRequest (String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private static void writeBody (Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful (HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, SET_TEXT_COLOR_RED + "Failure: " + status);
        }
    }

    private static <T> T readBody (HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful (int status) {
        return status / 100 == 2;
    }

}
