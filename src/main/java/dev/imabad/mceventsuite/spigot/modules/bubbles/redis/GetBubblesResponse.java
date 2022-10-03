package dev.imabad.mceventsuite.spigot.modules.bubbles.redis;

import dev.imabad.mceventsuite.spigot.modules.bubbles.ChatBubble;
import dev.imabad.mceventsuite.core.modules.redis.RedisBaseMessage;

import java.util.List;

public class GetBubblesResponse extends RedisBaseMessage {
    private List<ChatBubble> bubbleList;

    public GetBubblesResponse(List<ChatBubble> chatBubbleList) {
        this.bubbleList = chatBubbleList;
        this.bubbleList.removeIf((bubble) -> bubble.getName().equalsIgnoreCase("Main Stage")
                || bubble.getName().equalsIgnoreCase("Community Stage"));
    }

    public List<ChatBubble> getBubbleList() {
        return this.bubbleList;
    }
}
