package me.skiincraft.ousubot.view.utils;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import me.skiincraft.ousubot.OusuBot;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class CountryCodes {

    private static CountryCode[] COUNTRY_CODES;

    private static void inicialize() {
        if (COUNTRY_CODES != null)
            return;
        COUNTRY_CODES = new Gson().fromJson(new InputStreamReader(OusuBot.class.getResourceAsStream("/country_codes.json"), StandardCharsets.UTF_8), CountryCode[].class);
    }

    public static boolean isCountryCode(String countryCode) {
        inicialize();
        if (countryCode.length() <= 1) {
            return false;
        }
        return Arrays.stream(COUNTRY_CODES)
                .anyMatch(cc -> cc.getCode().equalsIgnoreCase(countryCode.substring(0, 2)));
    }

    public static CountryCode getCountryCode(String countryCode) {
        inicialize();
        return Arrays.stream(COUNTRY_CODES)
                .filter(cc -> cc.getCode().equalsIgnoreCase(countryCode.substring(0, 2)))
                .findFirst()
                .orElse(null);
    }


    public static class CountryCode {

        @SerializedName("Code")
        private final String code;
        @SerializedName("Name")
        private final String name;

        public CountryCode(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public String getCode() {
            return code;
        }

        public char[] getCodeChars() {
            return code.toCharArray();
        }
    }

}
