package me.skiincraft.ousubot;

import me.skiincraft.beans.Injector;
import me.skiincraft.beans.annotation.Inject;
import me.skiincraft.discord.core.OusuCore;
import me.skiincraft.discord.core.common.reactions.ReactionListeners;
import me.skiincraft.discord.core.common.reactions.Reactions;
import me.skiincraft.discord.core.language.Language;
import me.skiincraft.discord.core.plugin.OusuPlugin;
import me.skiincraft.ousubot.api.DiscordBotAPI;
import me.skiincraft.ousubot.api.OusuAPI;
import me.skiincraft.ousubot.view.emotes.GenericsEmotes;
import net.dv8tion.jda.api.Region;

import java.util.Locale;

public class OusuBot extends OusuPlugin {

    @Inject
    private GenericsEmotes emotes;

    @Override
    public void onEnable() {
        OusuCore.addLanguage(new Language(new Locale("pt", "BR"), Region.BRAZIL));
        OusuCore.addLanguage(new Language(new Locale("en", "US")));
        injectAPI(new OusuAPI(), null); //new DiscordBotAPI());
        emotes.loadEmotes(OusuCore.getAssetsPath().toAbsolutePath() + "/emotes/");

        ReactionListeners reactionListeners = new ReactionListeners();
        Reactions reactions = new Reactions(reactionListeners);
        OusuCore.registerListener(reactionListeners);

    }

    private void injectAPI(OusuAPI api, DiscordBotAPI discordBotAPI){
        Injector injector = OusuCore.getInjector();
        injector.inject(api);
        //injector.inject(discordBotAPI);
        injector.map(api);
        //injector.map(discordBotAPI);
        api.setup();
    }
}
