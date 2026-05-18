package gift.api.auth;

public final class AuthDto {

    private AuthDto() {
    }

    public record Response(String token) {
    }
}
