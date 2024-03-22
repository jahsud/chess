package request;

public record CreateGameRequest(String authToken, String gameName) implements AuthRequest {
    @Override
    public String getAuthToken () {
        return authToken;
    }
}
