package request;

public record ListGamesRequest(String authToken) implements AuthRequest {
    @Override
    public String getAuthToken () {
        return authToken;
    }
}
