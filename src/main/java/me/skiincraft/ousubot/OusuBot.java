package me.skiincraft.ousubot;

import me.skiincraft.beans.Injector;
import me.skiincraft.beans.annotation.Inject;
import me.skiincraft.ousucore.OusuCore;
import me.skiincraft.ousucore.common.PresenceUpdater;
import me.skiincraft.ousucore.common.reactions.ReactionListeners;
import me.skiincraft.ousucore.common.reactions.Reactions;
import me.skiincraft.ousucore.language.Language;
import me.skiincraft.ousucore.plugin.OusuPlugin;
import me.skiincraft.ousubot.core.DiscordBotAPI;
import me.skiincraft.ousubot.core.OusuAPI;
import me.skiincraft.ousubot.repositories.ChannelTrackingRepository;
import me.skiincraft.ousubot.repositories.UserRepository;
import me.skiincraft.ousubot.view.emotes.GenericsEmotes;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.Activity;

import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class OusuBot extends OusuPlugin {

    private static PresenceUpdater presenceUpdater;
    @Inject
    private static ChannelTrackingRepository trackingRepository;
    @Inject
    private static UserRepository userRepository;
    @Inject
    private GenericsEmotes emotes;

    public static PresenceUpdater getPresenceUpdater() {
        return presenceUpdater;
    }

    public static ChannelTrackingRepository getTrackingRepository() {
        return trackingRepository;
    }

    public static UserRepository getUserRepository() {
        return userRepository;
    }

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
                .scheduleAtFixedRate(OusuCore::shutdown, 1, 1, TimeUnit.HOURS);
    }

    private void injectAPI(OusuAPI api, DiscordBotAPI discordBotAPI) {
        Injector injector = OusuCore.getInjector();
        injector.inject(api);
        injector.map(api);
        injector.map(discordBotAPI);
        api.setup();
    }
}
