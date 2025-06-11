package me.zypj.rbw.instance;

import lombok.Getter;
import lombok.Setter;
import me.zypj.rbw.sample.EmbedType;
import me.zypj.rbw.RBWPlugin;
import me.zypj.rbw.config.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
public class Embed {

    public static HashMap<String, List<Embed>> embedPages = new HashMap<>();

    private int pages;
    private int currentPage = 0;
    private EmbedType type;
    private String title;
    private String description;
    private String thumbnailURL;
    private String imageURL;
    private String footer;
    private final List<MessageEmbed.Field> fields = new ArrayList<>();

    @Setter
    EmbedBuilder eb = new EmbedBuilder();

    public Embed(EmbedType type, String title, String description, int pages) {
        this.type = type;
        this.title = title;
        this.description = description;
        this.pages = pages;

        String[] d = Config.getValue("default").split(",");
        String[] s = Config.getValue("success").split(",");
        String[] e = Config.getValue("error").split(",");

        if (type == EmbedType.SUCCESS) {eb.setColor(new Color(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2])));}
        else if (type == EmbedType.ERROR) {eb.setColor(new Color(Integer.parseInt(e[0]), Integer.parseInt(e[1]), Integer.parseInt(e[2])));}
        else {eb.setColor(new Color(Integer.parseInt(d[0]), Integer.parseInt(d[1]), Integer.parseInt(d[2])));}

        this.footer = Config.getValue("footer").replaceAll("%name%", Config.getValue("server-name")).replaceAll("%version%", RBWPlugin.version);
    }

    public MessageEmbed build() {
        if (title != null && !title.isEmpty())
            if (pages != 1) {
                eb.setTitle(title + " `[Page: " + (currentPage + 1) + "/" + pages + "]`");
            }
            else {
                eb.setTitle(title);
            }
        if (description != null && !description.isEmpty())
            eb.setDescription(description);
        if (thumbnailURL != null && !thumbnailURL.isEmpty())
            eb.setThumbnail(thumbnailURL);
        if (imageURL != null && !imageURL.isEmpty())
            eb.setImage(imageURL);
        if (footer != null && !footer.isEmpty())
            eb.setFooter(footer).setTimestamp(OffsetDateTime.now());

        for (MessageEmbed.Field f : fields) {
            assert f.getName() != null;
            assert f.getValue() != null;
            eb.addField(f.getName(), f.getValue(), f.isInline());
        }

        fields.clear();

        return eb.build();
    }

    public void addField(String title, String content, boolean inline) {
        fields.add(new MessageEmbed.Field(title, content, inline));
    }

    public static Collection<? extends ItemComponent> createButtons(int currentPage) {
        List<Button> buttons = new ArrayList<>();
        buttons.add(Button.secondary("rankedbot-page-" + (currentPage - 1), "←"));
        buttons.add(Button.secondary("rankedbot-page-" + (currentPage + 1), "→"));
        return buttons;
    }

    public static Embed getPage(String msgID, int page) {
        return embedPages.get(msgID).get(page);
    }

    public static void addPage(String msgID, Embed embed) {
        List<Embed> pages;

        if (embedPages.get(msgID) != null) {
            pages = new ArrayList<>(embedPages.get(msgID));
        }
        else {
            pages = new ArrayList<>();
        }

        pages.add(embed);
        embedPages.put(msgID, pages);
    }
}
