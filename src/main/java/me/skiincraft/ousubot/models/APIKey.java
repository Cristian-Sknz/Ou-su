package me.skiincraft.ousubot.models;

import me.skiincraft.api.osu.requests.Token;
import me.skiincraft.sql.annotation.Id;
import me.skiincraft.sql.annotation.Lob;
import me.skiincraft.sql.annotation.Table;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.LocalDateTime;

@Table("tb_tokens")
public class APIKey {

    @Id
    private String identification;
    @Lob
    private String token;
    @Lob
    private String refreshToken;
    private LocalDateTime expiresIn;

    public APIKey() {
    }

    public APIKey(Token token) {
        this.identification = token.getToken().substring(0, 12);
        this.token = token.getToken();
        if (token.isV1()) {
            this.refreshToken = null;
            this.expiresIn = null;
            return;
        }
        this.refreshToken = token.getRefreshToken();
        this.expiresIn = LocalDateTime.now(Clock.systemUTC()).plusSeconds(token.getExpiresIn());
    }

    public String getIdentification() {
        return identification;
    }

    public String getToken() {
        return token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public LocalDateTime getExpiresIn() {
        return expiresIn;
    }

    public String getExpireInString() {
        if (expiresIn == null) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Timestamp timestamp = Timestamp.valueOf(expiresIn);
        return timestamp.toString();
    }

    public boolean isV1() {
        return expiresIn == null;
    }
}
