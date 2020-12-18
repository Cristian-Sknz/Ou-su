package me.skiincraft.discord.ousu.adapter;

import com.google.gson.Gson;
import me.skiincraft.api.ousu.OusuAPI;
import me.skiincraft.api.ousu.exceptions.TokenException;
import me.skiincraft.discord.core.OusuCore;

import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class OsuAPIAdapter {

    private static OusuAPI apiV1;
    private static int integer;

    static {
        try {
            integer = 0;
            apiV1 = new OusuAPI(OsuAPIAdapter.class.getPackage().toString(), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static List<String> getTokens() throws IOException {
        Path path = Paths.get(OusuCore.getAssetsPath() + "/APIToken.json");
        if (!Files.exists(path))
            Files.copy(new ByteArrayInputStream("[]".getBytes()), path);
        return Arrays.asList(new Gson().fromJson(new FileReader(path.toFile()), String[].class));
    }

    public static boolean existsTokens(){
        try {
            return getTokens().size() != 0;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static OusuAPI getOusuAPI(){
        try {
            List<String> tokens = getTokens();
            tokens.remove(null);
            if (tokens.size() == 0) {
                throw new TokenException("Nenhum Token foi encontrado...", null);
            }

            apiV1.setToken(tokens.get(integer));
            integer++;
            if (integer == tokens.size())
                integer = 0;
            return apiV1;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (TokenException e){
            System.out.println("Não foi possivel localizar um token nas configurações. Desligando...");
            OusuCore.shutdown();
            return null;
        }
    }
}
