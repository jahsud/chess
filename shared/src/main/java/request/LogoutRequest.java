package request;

public record LogoutRequest(String authToken) implements AuthRequest {
    @Override
    public String getAuthToken () {
        return authToken;
    }
}
