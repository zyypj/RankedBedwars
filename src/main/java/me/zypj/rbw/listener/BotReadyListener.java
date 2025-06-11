package me.zypj.rbw.listener;

import me.zypj.rbw.RBWPlugin;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class BotReadyListener extends ListenerAdapter {
    @Override
    public void onReady(@NotNull ReadyEvent event) {
        RBWPlugin.getInstance().onJdaReady();
    }
}
