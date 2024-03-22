package request;

public record JoinGameRequest(String authToken, String playerColor, Integer gameID) implements AuthRequest {
    @Override
    public String getAuthToken () {
        return authToken;
    }
}
