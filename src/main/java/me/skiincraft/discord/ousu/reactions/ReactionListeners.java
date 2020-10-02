package me.skiincraft.discord.ousu.reactions;

import java.util.concurrent.TimeUnit;
import me.skiincraft.discord.core.event.EventTarget;
import me.skiincraft.discord.core.event.Listener;
import me.skiincraft.discord.ousu.common.CreatedReactionEvent;

public class ReactionListeners implements Listener {
	
	@EventTarget
	public void reactionReset(CreatedReactionEvent event) {
		Thread thread = new Thread(() ->{
			try {
				Thread.sleep(TimeUnit.MINUTES.toMillis(2));
				event.delete();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		thread.start();
	}
}