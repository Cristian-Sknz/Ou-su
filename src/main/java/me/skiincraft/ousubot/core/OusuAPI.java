package me.skiincraft.ousubot.core;

import com.google.common.collect.Iterables;
import me.skiincraft.api.osu.OsuAPI;
import me.skiincraft.api.osu.exceptions.TokenException;
import me.skiincraft.api.osu.impl.v1.OsuAPIV1;
import me.skiincraft.api.osu.object.OAuthApplication;
import me.skiincraft.api.osu.requests.Token;
import me.skiincraft.beans.annotation.Inject;
import me.skiincraft.ousubot.OusuBot;
import me.skiincraft.ousubot.models.APIKey;
import me.skiincraft.ousubot.repositories.APIKeyRepository;
import me.skiincraft.ousucore.OusuCore;
import org.jetbrains.annotations.NotNull;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;

public class OusuAPI {

    private final OsuAPI api;
    private final OsuAPIV1 apiv1;
    @Inject
    private APIKeyRepository apiKeyRepository;
    private Iterator<Token> availableTokens;
    private int activeTokens;

    public OusuAPI() {
        this.api = OsuAPI.newAPIV2(PropertiesOAuthApllication.newOAuthApplication());
        this.apiv1 = (OsuAPIV1) OsuAPI.newAPIV1();
        api.setClient(OusuCore.getShardManager().getShards().get(0).getHttpClient());
    }

    public void createToken(String token) {
        apiKeyRepository.save(new APIKey(api.createToken(token).get()));
    }

    public void createTokenV1(String token) {
        apiKeyRepository.save(new APIKey(apiv1.createToken(token).get()));
    }

    public Token getToken(APIKey apiKey) {
        Token token = api.getTokens().stream()
                .filter(tkn -> tkn.getToken().equalsIgnoreCase(apiKey.getToken()))
                .findFirst().orElse(apiv1.getTokens().stream().filter(tkn -> tkn.getToken().equalsIgnoreCase(apiKey.getToken())).findFirst().orElse(null));

        if (Objects.isNull(token)) {
            return null;
        }
        return token;
    }

    public void refreshToken(String refreshToken) {
        apiKeyRepository.getAll()
                .stream().filter(tkn -> !tkn.isV1())
                .filter(apiKey -> apiKey.getRefreshToken().equalsIgnoreCase(refreshToken))
                .forEach(apiKey -> apiKeyRepository.removeObject(apiKey));

        apiKeyRepository.save(new APIKey(api.refreshToken(refreshToken).get()));
    }

    public void remove(String identification) {
        APIKey key = apiKeyRepository.getById(identification).orElse(null);
        if (Objects.isNull(key)) {
            return;
        }
        apiKeyRepository.removeObject(key);
        OsuAPI api = this.api;
        Token token = api.getTokens().stream()
                .filter(tkn -> tkn.getToken().equalsIgnoreCase(key.getToken()))
                .findFirst().orElse(null);

        if (Objects.isNull(token))
            return;

        api.getTokens().remove(token);
    }

    public OsuAPI getAPI() {
        return api;
    }

    public OsuAPIV1 getAPIV1() {
        return apiv1;
    }

    public Token getAvailableTokens() {
        try {
            if (availableTokens == null || activeTokens < getAPI().getTokens().size()) {
                availableTokens = Iterables.cycle(getAPI().getTokens().toArray(new Token[0])).iterator();
                activeTokens = getAPI().getTokens().size();
            }

            return checkIfExpired(availableTokens.next());
        } catch (NoSuchElementException e){
            throw new TokenException(e);
        }
    }

    private Token checkIfExpired(Token token) {
        if (Objects.isNull(token)) {
            return null;
        }
        // Checar se existe
        Optional<APIKey> apiToken = apiKeyRepository.getById(token.getToken().substring(0, 12));
        if (!apiToken.isPresent()) {
            throw new TokenException("Um token não registrado foi identificado e não pode ser registrado.\n" + token.getToken());
        }

        APIKey key = apiToken.get();
        if (key.getExpiresIn() != null && key.getExpiresIn().isBefore(LocalDateTime.now(Clock.systemUTC()))) {
            api.getTokens().remove(token);
            Token refreshToken = api.refreshToken(key.getRefreshToken()).get();
            availableTokens = Iterables.cycle(getAPI().getTokens().toArray(new Token[0])).iterator();
            return refreshToken;
        }
        return token;
    }

    private void generateTokens() throws TokenException {
        List<APIKey> tokens = apiKeyRepository.getAll();
        if (tokens.size() == 0) {
            throw new TokenException("Não existe nenhum token no repositório.");
        }

        for (APIKey apiKey : tokens) {
            if (apiKey.isV1()) {
                apiv1.createToken(apiKey.getToken()).get();
                continue;
            }
            if (apiKey.getExpiresIn() != null && apiKey.getExpiresIn().isBefore(LocalDateTime.now(Clock.systemUTC()))) {
                apiKeyRepository.removeObject(apiKey);
                apiKeyRepository.save(new APIKey(api.refreshToken(apiKey.getRefreshToken()).get()));
                continue;
            }
            api.resumeToken(apiKey.getToken()).get();
        }
    }


    public void setup() {
        try {
            generateTokens();
        } catch (TokenException e) {
            e.printStackTrace();
        }
    }

    public static class PropertiesOAuthApllication extends OAuthApplication {

        private PropertiesOAuthApllication(long clientId, @NotNull String clientSecret, @NotNull String redirectUri) {
            super(clientId, clientSecret, redirectUri);
        }

        public static PropertiesOAuthApllication newOAuthApplication() {
            try {
                Properties properties = new Properties();
                properties.load(OusuBot.class.getResourceAsStream("/OAuthCredentials.properties"));
                return new PropertiesOAuthApllication(Long.parseLong(properties.getProperty("clientId")), properties.getProperty("client_secret"), properties.getProperty("redirect_uri"));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
