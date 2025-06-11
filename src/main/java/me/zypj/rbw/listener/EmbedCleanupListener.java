package me.zypj.rbw.listener;

import me.zypj.rbw.instance.Embed;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * Removes stored embed pages when the original message is deleted.
 */
public class EmbedCleanupListener extends ListenerAdapter {
    @Override
    public void onMessageDelete(@NotNull MessageDeleteEvent event) {
        Embed.embedPages.remove(event.getMessageId());
    }
}
