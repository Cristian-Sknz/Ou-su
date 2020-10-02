package me.skiincraft.discord.ousu.object;

public class Participante {
	
	private String name;
	private long id;
	private Personagem personagem;
	private long guildId;
	private long channelId;
	private long starttime;
	
	private int tentativa;
	
	public Participante(String name, long id, Personagem personagem, long guildId, long channelId) {
		this.name = name;
		this.id = id;
		this.personagem = personagem;
		this.guildId = guildId;
		this.channelId = channelId;
		this.tentativa = 0;
		this.starttime = System.currentTimeMillis();
	}
	
	public String getName() {
		return name;
	}
	
	public boolean hasTime() {
		return System.currentTimeMillis() - starttime <= 15000L;
	}
	
	public long getChannelId() {
		return channelId;
	}
	
	public void setTentativa(int tentativa) {
		this.tentativa = tentativa;
	}
	
	public long getGuildId() {
		return guildId;
	}
	public long getId() {
		return id;
	}
	public Personagem getPersonagem() {
		return personagem;
	}
	public int getTentativa() {
		return tentativa;
	}
	

}
