package me.skiincraft.discord.ousu.abstractcore;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import me.skiincraft.discord.ousu.exception.PosCommandException;
import me.skiincraft.discord.ousu.utils.InputStreamFile;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public abstract class ChannelUtils extends ListenerAdapter {
	
	public abstract TextChannel getTextChannel();
	
	/**
	 * 
	 * @param message mensagem ao ser enviada
	 * @param consumer o que acontecerá depois da mensagem.
	 * @param tolerancia tolerancia de espera maxima em segundos
	 * 
	 */
	public void replyQueue(String message, Consumer<Message> consumer, InputStreamFile file, int tolerancia) {
		List<Message> messagelist = new ArrayList<Message>();
		String name = getClass().getName()
				.replace("Command", "")
				.replace("Commands", "");
		if (consumer == null) {
			if (file != null) {
				getTextChannel().sendMessage(message).addFile(file.getInputStream(), file.getFullname()).queue();
				return;
			}
			getTextChannel().sendMessage(message).queue();
			return;
		}
		if (file != null) {
			getTextChannel().sendMessage(message).addFile(file.getInputStream(), file.getFullname()).queue(con -> {
				messagelist.add(con);
			});
		} else {
			getTextChannel().sendMessage(message).queue(con -> {
				messagelist.add(con);
			});
		}

		
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				int i = 0;
				int tol = tolerancia;
				while (messagelist.size() == 0) {
					try {
						Thread.sleep(200);	i++;
						
						if (tolerancia == 0) {
							tol = 2;
						}
						if (i >= tol*5) {
							throw new PosCommandException("Houve um erro ao completar um comando: " + name + "Command");
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				consumer.accept(messagelist.get(0));
			}
		}, name + "_posCommand");
		thread.start();
	}
	
	public void replyQueue(String message, Consumer<Message> consumer) {
		this.replyQueue((String)message, consumer, null, 12);
	}
	
	public void replyQueue(String message, InputStreamFile file) {
		this.replyQueue((String)message, null, file, 12);
	}
	
	public void reply(String message) {
		getTextChannel().sendMessage(message).queue();
	}

	/**
	 * 
	 * @param message mensagem ao ser enviada
	 * @param consumer o que acontecerá depois da mensagem.
	 * @param tolerancia tolerancia de espera maxima em segundos
	 * 
	 */
	public void replyQueue(CharSequence message, Consumer<Message> consumer, InputStreamFile file, int tolerancia) {
		List<Message> messagelist = new ArrayList<Message>();
		String name = getClass().getName()
				.replace("Command", "")
				.replace("Commands", "");
		if (consumer == null) {
			if (file != null) {
				getTextChannel().sendMessage(message).addFile(file.getInputStream(),
						file.getFullname()).queue();
				return;
			}
			getTextChannel().sendMessage(message).queue();
			return;
		}
		if (file != null) {
			getTextChannel().sendMessage(message).addFile(file.getInputStream(), file.getFullname()).queue(con -> {
				messagelist.add(con);
			});
		} else {
			getTextChannel().sendMessage(message).queue(con -> {
				messagelist.add(con);
			});
		}
		
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				int i = 0;
				int tol = tolerancia;
				while (messagelist.size() == 0) {
					try {
						Thread.sleep(200);	i++;
						
						if (tolerancia == 0) {
							tol = 2;
						}
						if (i >= tol*5) {
							throw new PosCommandException("Houve um erro ao completar um comando: " + name + "Command");
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				consumer.accept(messagelist.get(0));
			}
		}, name + "_posCommand");
		thread.start();
	}
	
	public void replyQueue(CharSequence message, Consumer<Message> consumer) {
		this.replyQueue(message, consumer, null, 12);
	}
	
	public void replyQueue(CharSequence message, InputStreamFile file) {
		this.replyQueue(message, null, file, 12);
	}
	
	public void reply(CharSequence message) {
		getTextChannel().sendMessage(message).queue();
	}
	

	public void replyQueue(MessageEmbed message, Consumer<Message> consumer, InputStreamFile file, int tolerancia) {
		List<Message> messagelist = new ArrayList<Message>();
		String name = getClass().getName()
				.replace("Command", "")
				.replace("Commands", "");
		if (consumer == null) {
			if (file != null) {
				EmbedBuilder embed = new EmbedBuilder(message);
				embed.setImage("attachment://" + file.getFullname());
				getTextChannel().sendFile(file.getInputStream(), file.getFullname()).embed(embed.build()).queue();
				return;
			}
			getTextChannel().sendMessage(message).queue();
			return;
		}
		if (file != null) {
			EmbedBuilder embed = new EmbedBuilder(message);
			embed.setImage("attachment://" + file.getFullname());
			getTextChannel().sendFile(file.getInputStream(), file.getFullname()).embed(embed.build()).queue(con -> {
				messagelist.add(con);
			});
		} else {
			getTextChannel().sendMessage(message).queue(con -> {
				messagelist.add(con);
			});
		}
		
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				int i = 0;
				int tol = tolerancia;
				while (messagelist.size() == 0) {
					try {
						Thread.sleep(200);	i++;
						
						if (tolerancia == 0) {
							tol = 2;
						}
						if (i >= tol*5) {
							throw new PosCommandException("Houve um erro ao completar um comando: " + name + "Command");
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				consumer.accept(messagelist.get(0));
			}
		}, name + "_posCommand");
		thread.start();
	}
	
	public void replyQueue(MessageEmbed message, Consumer<Message> consumer) {
		this.replyQueue(message, consumer, null, 12);
	}
	
	public void replyQueue(MessageEmbed message, InputStreamFile file) {
		this.replyQueue(message, null, file, 12);
	}
	
	public void reply(MessageEmbed message) {
		getTextChannel().sendMessage(message).queue();
	}
	
	/**
	 * 
	 * @param message mensagem ao ser enviada
	 * @param consumer o que acontecerá depois da mensagem.
	 * @param tolerancia tolerancia de espera maxima em segundos
	 * 
	 */
	public void replyQueue(Message message, Consumer<Message> consumer, InputStreamFile file, int tolerancia) {
		List<Message> messagelist = new ArrayList<Message>();
		String name = getClass().getName()
				.replace("Command", "")
				.replace("Commands", "");
		if (consumer == null) {
			if (file != null) {
				getTextChannel().sendMessage(message).addFile(file.getInputStream(), file.getFullname()).queue();
				return;
			}
			getTextChannel().sendMessage(message).queue();
			return;
		}
		if (file != null) {
			getTextChannel().sendMessage(message).addFile(file.getInputStream(), file.getFullname()).queue(con -> {
				messagelist.add(con);
			});
		} else {
			getTextChannel().sendMessage(message).queue(con -> {
				messagelist.add(con);
			});
		}

		
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				int i = 0;
				int tol = tolerancia;
				while (messagelist.size() == 0) {
					try {
						Thread.sleep(200);	i++;
						
						if (tolerancia == 0) {
							tol = 2;
						}
						if (i >= tol*5) {
							throw new PosCommandException("Houve um erro ao completar um comando: " + name + "Command");
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				consumer.accept(messagelist.get(0));
			}
		}, name + "_posCommand");
		thread.start();
	}
	
	public void replyQueue(Message message, Consumer<Message> consumer) {
		this.replyQueue(message, consumer, null, 12);
	}
	
	public void reply(Message message, InputStreamFile file) {
		this.replyQueue(message, null, file, 12);
	}
	
	public void reply(MessageEmbed message, InputStreamFile file) {
		this.replyQueue(message, null, file, 12);
	}
	
	public void reply(Message message) {
		getTextChannel().sendMessage(message).queue();
	}
	
	public void reply(InputStreamFile file) {
		getTextChannel().sendFile(file.getInputStream(), file.getFullname()).queue();
	}
	
	public void reply(InputStream file, String name) {
		getTextChannel().sendFile(file, name).queue();
	}
}
