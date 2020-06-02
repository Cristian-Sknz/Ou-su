package me.skiincraft.discord.ousu.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class ConfigSetup {
	
    private ConfigurationFile fileConfig;

	private void writeGson(File file , Object data) {
        FileWriter fr = null;
        try {
            fr = new FileWriter(file);
            fr.write(new Gson().toJson(data));
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
	public void makeConfig() {
		File file = new File("config.json");
		if (file.exists()) {
			return;
		}
		ConfigurationFile config = new ConfigurationFile();
		Map<String, String> configuration = new HashMap<>();
		for (ConfigOptions o :ConfigOptions.values()) {
			configuration.put(o.getString(), "none");
		}
		config.setTokenConfig(configuration);
		
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		writeGson(file, config);	
	}
	
	public enum ConfigOptions {
		Token("Token"), 
		DBL("DBL-Token1"), DBL1("DBL-Token2"), DBL2("DBL-Token3"),
		BotID("BotID"), GoogleToken("Google-Token"),
		OsuToken("OsuToken");
		
		private String string;
		ConfigOptions(String string) {
			this.string = string;
		}
		
		public String getString() {
			return string;
		}
	}
	
	public boolean verificarTokens() {
		if (getConfig(ConfigOptions.Token).contains("none")) return false;
		if (getConfig(ConfigOptions.GoogleToken).contains("none"))return false;
		if (getConfig(ConfigOptions.OsuToken).contains("none")) return false;
		return true;
	}
	
	public String getConfig(ConfigOptions option) {
		try {
			Gson gson = new Gson();
			InputStream in = new FileInputStream(new File("config.json"));
			Reader reader = new InputStreamReader(in);
			this.fileConfig = gson.fromJson(reader, ConfigurationFile.class);
			return fileConfig.getTokenConfig().get(option.getString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
