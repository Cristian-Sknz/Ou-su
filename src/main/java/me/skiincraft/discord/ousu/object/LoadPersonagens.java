package me.skiincraft.discord.ousu.object;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.htmlpage.CharacterGetter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class LoadPersonagens {
	
	private static final List<Personagem> personagems = new ArrayList<>();

	private final static File file = new File(OusuBot.getMain().getPlugin().getAssetsPath() + "/personagens.json");
	public static List<Personagem> getPersonagems() {
		return personagems;
	}
	
	public static synchronized void load() {
		personagems.clear();
		try {
			create();
			FileReader reader = new FileReader(file);
			JsonArray array = new JsonParser().parse(reader).getAsJsonArray();
			if (array.size() == 0) {
				return;
			}
			
			try {
				for (JsonElement ele : array) {
					JsonObject obj = ele.getAsJsonObject();
					personagems.add(new Personagem(obj.get("nome").getAsString(),
							Gender.valueOf(obj.get("gender").getAsString()), obj.get("link").getAsString(),
							obj.get("haircolor").getAsString(), obj.get("image").getAsString()));
				}
			} catch (Exception e) {
				reader.close();
				refreshPersonagens();
			}
			
		} catch (JsonIOException | JsonSyntaxException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public static synchronized void refreshPersonagens() {
		try {
			personagems.clear();
			FileReader reader = new FileReader(file);
			JsonArray array = new JsonParser().parse(reader).getAsJsonArray();
			for (JsonElement ele : array) {
				JsonObject object = ele.getAsJsonObject();
				Personagem character = CharacterGetter.refreshPersonagem(object.get("link").getAsString());
				
				Field field = character.getClass().getDeclaredField("name");
				field.setAccessible(true);
				field.set(character, object.get("nome").getAsString());
				
				personagems.add(character);
			}
			
			file.delete();
			JsonArray array2 = new JsonArray();
			for (Personagem personagem : personagems) {
				JsonObject object = new JsonObject();
				object.addProperty("nome", personagem.getName());
				object.addProperty("image", personagem.getImage());
				object.addProperty("gender", personagem.getGender().name());
				object.addProperty("link", personagem.getLink());
				object.addProperty("haircolor", personagem.getHaircolor());
				array2.add(object);
			}
			save(array2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static synchronized void editPersonagem(Personagem personagem, String newname) {
		try {
			JsonArray array = new JsonParser().parse(new FileReader(file)).getAsJsonArray();
			for (JsonElement element : array) {
				JsonObject object = element.getAsJsonObject();
				if (object.get("link").getAsString().equalsIgnoreCase(personagem.getLink())) {
					object.addProperty("name", newname);
					changeField(personagem, "name", newname);
					save(array);
					return;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	public static synchronized void removePersonagem(Personagem personagem) {
		try {
			JsonArray array = new JsonParser().parse(new FileReader(file)).getAsJsonArray();
			for (JsonElement element : array) {
				JsonObject object = element.getAsJsonObject();
				if (object.get("link").getAsString().equalsIgnoreCase(personagem.getLink())) {
					array.remove(object);
					personagems.remove(personagem);
					save(array);
					return;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	public static synchronized void editImagePersonagem(Personagem personagem, String image) {
		try {
			JsonArray array = new JsonParser().parse(new FileReader(file)).getAsJsonArray();
			for (JsonElement element : array) {
				JsonObject object = element.getAsJsonObject();
				if (object.get("link").getAsString().equalsIgnoreCase(personagem.getLink())) {
					object.addProperty("image", image);
					changeField(personagem, "image", image);
					save(array);
					return;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	private static void changeField(Personagem p, String fieldname, Object item) {
		try {
			Field f = p.getClass().getDeclaredField(fieldname);
			f.setAccessible(true);
			f.set(p, item);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void save(JsonArray array) throws IOException {
		FileWriter writer = new FileWriter(file);
		writer.write(array.toString());
		writer.close();
	}
	
	public static void savePersonagem(Personagem personagem) {
		try {
			create();
			JsonArray array = new JsonParser().parse(new FileReader(file)).getAsJsonArray();
			JsonObject object = new JsonObject();
			if (array.size() != 0) {
				for (JsonElement ele : array) {
					JsonObject ob = ele.getAsJsonObject();
					if (ob.get("link").getAsString().equalsIgnoreCase(personagem.getLink())) {
						return;
					}
				}
			}
			
			object.addProperty("nome", personagem.getName());
			object.addProperty("image", personagem.getImage());
			object.addProperty("gender", personagem.getGender().name());
			object.addProperty("link", personagem.getLink());
			object.addProperty("haircolor", personagem.getHaircolor());
			
			personagems.add(personagem);
			
			array.add(object);
			save(array);
			System.out.println(personagem.getName() + " Um personagem foi adicionado.");
		} catch (JsonIOException | JsonSyntaxException | IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void create() throws IOException {
		if (file.exists()) {
			return;
		}
		file.createNewFile();
		FileWriter writer = new FileWriter(file);
		writer.write("[]");
		writer.close();
	}

}
