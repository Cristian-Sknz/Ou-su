package me.skiincraft.ousubot.listeners;

import me.skiincraft.beans.annotation.Inject;
import me.skiincraft.beans.stereotypes.EventMap;
import me.skiincraft.ousucore.common.EventListener;
import me.skiincraft.ousubot.api.DiscordBotAPI;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

@EventMap
public class DBListener implements EventListener {

    @Inject
    private DiscordBotAPI api;

    @SubscribeEvent
    public void onJoinServer(GuildJoinEvent event) {
        System.out.printf("Foi adicionado uma nova Guild(%s)%n", event.getGuild().getName());
        api.updateStats();
    }

}
