package me.skiincraft.ousubot;

import me.skiincraft.beans.Injector;
import me.skiincraft.beans.annotation.Inject;
import me.skiincraft.discord.core.OusuCore;
import me.skiincraft.discord.core.common.PresenceUpdater;
import me.skiincraft.discord.core.common.reactions.ReactionListeners;
import me.skiincraft.discord.core.common.reactions.Reactions;
import me.skiincraft.discord.core.language.Language;
import me.skiincraft.discord.core.plugin.OusuPlugin;
import me.skiincraft.ousubot.api.DiscordBotAPI;
import me.skiincraft.ousubot.api.OusuAPI;
import me.skiincraft.ousubot.view.emotes.GenericsEmotes;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.Activity;

import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class OusuBot extends OusuPlugin {

    @Inject
    private GenericsEmotes emotes;
    private static PresenceUpdater presenceUpdater;

    @Override
    public void onEnable() {
        OusuCore.addLanguage(new Language(new Locale("pt", "BR"), Region.BRAZIL));
        OusuCore.addLanguage(new Language(new Locale("en", "US")));
        injectAPI(new OusuAPI(), new DiscordBotAPI());
        emotes.loadEmotes(OusuCore.getAssetsPath().toAbsolutePath() + "/emotes/");

        ReactionListeners reactionListeners = new ReactionListeners();
        Reactions reactions = new Reactions(reactionListeners);
        OusuCore.registerListener(reactionListeners);

        presenceUpdater = new PresenceUpdater(Arrays.asList(Activity.listening("ou!help for help."),
                Activity.watching("{guildsize} Servidores online."),
                Activity.listening("Ousubot has been improved and is better!")));

        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(OusuCore::shutdown, 1,1, TimeUnit.HOURS);
    }

    public static PresenceUpdater getPresenceUpdater() {
        return presenceUpdater;
    }

    private void injectAPI(OusuAPI api, DiscordBotAPI discordBotAPI){
        Injector injector = OusuCore.getInjector();
        injector.inject(api);
        injector.map(api);
        injector.map(discordBotAPI);
        api.setup();
    }
}
