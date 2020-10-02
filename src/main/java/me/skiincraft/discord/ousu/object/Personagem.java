package me.skiincraft.discord.ousu.object;

public class Personagem {

	private String name, link, image, haircolor;
	private Gender gender;
	
	public Personagem(String name, Gender gender, String link, String haircolor,String image) {
		this.name = name;
		this.haircolor = haircolor;
		this.gender = gender;
		this.image = image;
		this.link = link;
	}
	
	public String getImage() {
		return image;
	}
	
	public String getLink() {
		return link;
	}
	
	public String getName() {
		return name;
	}
	
	public Gender getGender() {
		return gender;
	}
	
	public String getHaircolor() {
		return haircolor;
	}

	public String toString() {
		return "Personagem [name=" + name + ", link=" + link + ", image=" + image + ", haircolor=" + haircolor
				+ ", gender=" + gender + "]";
	}
	
	

}
