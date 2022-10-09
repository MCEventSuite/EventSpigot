package dev.imabad.mceventsuite.spigot.modules.bossbar.ui;

import it.unimi.dsi.fastutil.ints.Int2CharMap;
import it.unimi.dsi.fastutil.ints.Int2CharOpenHashMap;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import org.bukkit.entity.Player;

public class BossBarUI {

    private static final Int2CharMap BG_CHARS = new Int2CharOpenHashMap();
    private static final Int2CharMap OFFSET_CHARS = new Int2CharOpenHashMap();
    private static final int[] REVERSED_ORDER = {128, 64, 32, 16, 8, 4, 2, 1};
    private static final Key BOSS_BAR_FONT = Key.key("cubed", "boss_bar");

    static {
        BG_CHARS.put(1, (char) 0x0325);
        BG_CHARS.put(2, (char) 0x0326);
        BG_CHARS.put(4, (char) 0x0327);
        BG_CHARS.put(8, (char) 0x0328);
        BG_CHARS.put(16, (char) 0x0329);
        BG_CHARS.put(32, (char) 0x0330);
        BG_CHARS.put(64, (char) 0x0331);
        BG_CHARS.put(128, (char) 0x0332);
        OFFSET_CHARS.put(1, (char) 0xF801);
        OFFSET_CHARS.put(2, (char) 0xF802);
        OFFSET_CHARS.put(4, (char) 0xF803);
        OFFSET_CHARS.put(8, (char) 0xF804);
        OFFSET_CHARS.put(16, (char) 0xF805);
        OFFSET_CHARS.put(32, (char) 0xF806);
        OFFSET_CHARS.put(64, (char) 0xF807);
        OFFSET_CHARS.put(128, (char) 0xF808);
    }

    private Component text;
    private String rawText;
    private int padding = 4;
    private BossBar bossBar;

    public BossBarUI(Component text){
        this.text = text;
        this.rawText = PlainTextComponentSerializer.plainText().serialize(text);
        this.bossBar = BossBar.bossBar(generateTitle(), 0, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS);
    }

    public void show(Audience target){
        target.showBossBar(bossBar);
    }

    public void hide(Audience target){
        target.hideBossBar(bossBar);
    }

    public void setText(Component text){
        this.text = text;
        this.rawText = PlainTextComponentSerializer.plainText().serialize(text);
        update();
    }

    public void setPadding(int padding){
        this.padding = padding;
        update();
    }

    public void update(){
        bossBar.name(generateTitle());
    }

    private Component generateTitle(){
        int totalWidth = calculateSize();
        String bg = getBG(totalWidth);
        String negativeOffset = getOffset(totalWidth - padding);
        Component finalOutput = Component.text(bg).font(BOSS_BAR_FONT).color(TextColor.color(78, 92, 36))
                .append(Component.text(negativeOffset).font(BOSS_BAR_FONT))
                .append(text);
        return finalOutput;
    }

    private int calculateSize(){
        int totalSize = 0;
        for(int i = 0; i < rawText.length(); i++){
            char c = rawText.charAt(i);
            int size = FontInfo.getFontInfo(c).getLength();
            totalSize += size;
        }
        totalSize += padding * 2;
        return totalSize;
    }

    private String getOffset(int length){
        StringBuilder outputString = new StringBuilder();
        for (int section : REVERSED_ORDER) {
            if (length / section > 0) {
                for (int x = 0; x < length / section; x++) {
                    outputString.append(OFFSET_CHARS.get(section));
                    length = length - section;
                }
            }
        }
        return outputString.toString();
    }

    private String getBG(int length){
        StringBuilder outputString = new StringBuilder();
        for (int section : REVERSED_ORDER) {
            if (length / section > 0) {
                for (int x = 0; x < length / section; x++) {
                    outputString.append(BG_CHARS.get(section));
                    outputString.append(OFFSET_CHARS.get(1));
                    length = length - section;
                }
            }
        }
        return outputString.toString();
    }

}
