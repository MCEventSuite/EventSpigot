package dev.imabad.mceventsuite.spigot.modules.chat;

import com.google.gson.Gson;
import com.microsoft.azure.cognitiveservices.vision.contentmoderator.ContentModeratorClient;
import com.microsoft.azure.cognitiveservices.vision.contentmoderator.ContentModeratorManager;
import com.microsoft.azure.cognitiveservices.vision.contentmoderator.models.AzureRegionBaseUrl;
import com.microsoft.azure.cognitiveservices.vision.contentmoderator.models.DetectedTerms;
import com.microsoft.azure.cognitiveservices.vision.contentmoderator.models.ScreenTextOptionalParameter;
import dev.imabad.mceventsuite.core.api.IConfigProvider;
import dev.imabad.mceventsuite.core.api.modules.Module;
import dev.imabad.mceventsuite.spigot.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ChatFilterModule extends Module implements IConfigProvider<ChatFilterConfig> {

    private ChatFilterConfig chatFilterConfig;
    private ContentModeratorClient client;

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void onEnable() {

    }

    public CompletableFuture<Boolean> checkText(Player suspect, String string) {
        if(this.client == null) {
            this.client = ContentModeratorManager.authenticate(chatFilterConfig.getRegionUrl(),
                    chatFilterConfig.getApiKey());
            this.client = this.client.withBaseUrl(AzureRegionBaseUrl.fromString(chatFilterConfig.getRegionUrl()));
        }
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        this.client.textModerations().screenTextAsync("text/plain",
                string.getBytes(StandardCharsets.UTF_8),
                new ScreenTextOptionalParameter().withClassify(true).withPII(true)).subscribe((next) -> {
                    if(next.status().code() != 3000) {
                        future.complete(false);
                        return;
                    }
                    if(next.pII() != null) {
                        for(Player player : Bukkit.getAllOnlinePlayers()) {
                            boolean enabled = player.getPersistentData("cb_global") == null || player.getPersistentData("cb_global").equalsIgnoreCase("true");
                            if (!player.hasPermission("eventsuite.filter") || !enabled)
                                continue;
                            player.sendMessage(StringUtils.colorizeMessage("&e" + suspect.getName() + " &ctried to say &e" + string +
                                    " &cbut we detected PII!"));
                        }
                        future.complete(false);
                        return;
                    }
                    if(next.classification() == null) {
                        System.out.println("WARNING! Classification was null!");
                        System.out.println(new Gson().toJson(next));
                        future.complete(true);
                        return;
                    }
                    if(next.classification().reviewRecommended()) {
                        String debugString = "&e%s &ctried to say &e%s&c.\n&cReason: &e%s";
                        String terms;
                        if(next.terms() != null) {
                            terms = "Blacklist: " + next.terms().stream().map(DetectedTerms::term).collect(Collectors.joining(", "));
                        } else {
                            terms = "AI detected";
                            System.out.println(new Gson().toJson(next));
                        }
                        debugString = debugString.formatted(suspect.getName(), string, terms);
                        for(Player player : Bukkit.getAllOnlinePlayers()) {
                            boolean enabled = player.getPersistentData("cb_global") == null || player.getPersistentData("cb_global").equalsIgnoreCase("true");
                            if(!player.hasPermission("eventsuite.filter") || !enabled)
                                continue;
                            player.sendMessage(StringUtils.colorizeMessage(debugString));

                        }
                        future.complete(false);
                        return;
                    }
                    future.complete(true);
        });
        return future;
    }

    @Override
    public void onDisable() {

    }

    @Override
    public List<Class<? extends Module>> getDependencies() {
        return null;
    }

    @Override
    public Class<ChatFilterConfig> getConfigType() {
        return ChatFilterConfig.class;
    }

    @Override
    public ChatFilterConfig getConfig() {
        return this.chatFilterConfig;
    }

    @Override
    public String getFileName() {
        return "filter.json";
    }

    @Override
    public void loadConfig(ChatFilterConfig config) {
        this.chatFilterConfig = config;
    }

    @Override
    public void saveConfig() {

    }

    @Override
    public boolean saveOnQuit() {
        return false;
    }
}
