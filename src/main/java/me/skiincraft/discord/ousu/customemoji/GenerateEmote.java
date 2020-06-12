package me.skiincraft.discord.ousu.customemoji;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.managers.EmoteManager;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;

public class GenerateEmote {

	public static void writeGson(File file , Object data) {
        FileWriter fr = null;
        try {
            fr = new FileWriter(file);
            fr.write(new GsonBuilder().setPrettyPrinting().create().toJson(data));
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
    
	public void makeConfig(Guild guild) {
		File file = new File("emotes.json");
		if (file.exists()) {
			setEmotes();
			return;
		}
		EmotesFile config = new EmotesFile();
		Map<String, Map<String, String>> configuration = new HashMap<>();
			guild.getEmotes().forEach(emote -> {
				Map<String, String> emoteinfo = new HashMap<>();
				emoteinfo.put("EmoteID", emote.getId());
				emoteinfo.put("Mention", emote.getAsMention());
				emoteinfo.put("Date", emote.getTimeCreated().toString());
				configuration.put(emote.getName(), emoteinfo);	
		});
		config.setEmotes(configuration);
		
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		writeGson(file, config);
		setEmotes();
	}
	
	
	public static String setEmotes() {
		try {
			Gson gson = new Gson();
			InputStream in = new FileInputStream(new File("emotes.json"));
			Reader reader = new InputStreamReader(in);
			EmotesFile e = gson.fromJson(reader, EmotesFile.class);
			;
			List<String> lista = e.getEmotes().keySet().stream().collect(Collectors.toList());
			for (String string:lista) {
				Emote emote = new Emote() {
					
					@Override
					public boolean isFake() {
						return true;
					}
					
					@Override
					public long getIdLong() {
						return new Long(e.getEmotes().get(string).get("EmoteID"));
					}
					
					@Override
					public boolean isManaged() {
						return false;
					}
					
					@Override
					public boolean isAnimated() {
						return false;
					}
					
					@Override
					public List<Role> getRoles() {
						return null;
					}
					
					@Override
					public String getName() {
						return string;
					}
					
					@Override
					public EmoteManager getManager() {
						return null;
					}
					
					@Override
					public JDA getJDA() {
						return null;
					}
					
					@Override
					public Guild getGuild() {
						return null;
					}
					
					@Override
					public AuditableRestAction<Void> delete() {
						return null;
					}
					
					@Override
					public boolean canProvideRoles() {
						return false;
					}
				};
				OusuEmojis.emotes.add(emote);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
