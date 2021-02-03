package me.skiincraft.ousubot.listeners;

import me.skiincraft.beans.stereotypes.EventMap;
import me.skiincraft.ousucore.OusuCore;
import me.skiincraft.ousucore.common.EventListener;
import me.skiincraft.ousucore.events.CommandExecuteEvent;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.apache.logging.log4j.Level;

import java.util.Objects;

@EventMap
public class CommandListener implements EventListener {

    @SubscribeEvent
    public void onCommandExecute(CommandExecuteEvent event){
        User member = Objects.requireNonNull(event.getCommand().getMessage().getMember(), "user").getUser();
        OusuCore.getLogger().log(Level.getLevel("Command"), String.format("%s digitou o comando: %s. [%s %s]",
                member.getName(), event.getCommand().getName(),
                event.getCommand().getName(),
                String.join(" ", event.getCommand().getArgs())));
    }

}
