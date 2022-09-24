package dev.imabad.mceventsuite.spigot.modules.map;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.spigot.modules.map.objects.Kevin;
import dev.imabad.mceventsuite.spigot.modules.npc.NPC;
import dev.imabad.mceventsuite.spigot.modules.npc.NPCManager;
import dev.imabad.mceventsuite.spigot.modules.npc.NPCModule;
import dev.imabad.mceventsuite.spigot.utils.StringUtils;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class KevinManager {

    private final NPCManager npcManager;

    public KevinManager(World world, MapConfig mapConfig){
        this.npcManager = EventCore.getInstance().getModuleRegistry()
                .getModule(NPCModule.class).getNpcManager();
        addKevins(world, mapConfig);
    }

    private List<NPC> villagerNPCList = new ArrayList<>();

    public void addKevins(World world, MapConfig mapConfig){
        mapConfig.getKevins().forEach(kevin -> {
            addKevin(world, kevin);
//            addKevin(, kevin.getName(), kevin.getVoiceLines());
        });
//        addKevin(new Location(world, 905.5, 71, 551.5), "&9&lMerch Kevin", "&rPick up some sweet merch at https://pass.cubedcon.com");
//        addKevin(new Location(world, 991.5, 71, 523.5), "&9&lArcade Kevin", "&rPew Pew, play some games!");
//        addKevin(new Location(world, 986.5, 71, 523.5), "&9&lArcade Kevin", "&rI'm so gonna beat you at spleef.");
//        addKevin(new Location(world, 919.5, 71, 493.5), "&9&lSnack Kevin", "&r&o&lCRUNCH CRUNCH &rOh, you want some?");
//        addKevin(new Location(world, 919.5, 71, 455.5), "&9&lSnack Kevin", "&r&o&lSLURRRRRRRP");
//        addKevin(new Location(world, 978.5, 86, 525.5), "&9&lConcierge Kevin", "&rMay I take your coat?");
//        addKevin(new Location(world, 971.5, 86, 543.5), "&9&lBartender Kevin", "&rI've got all the drinks you can think of, yes all of them.");
//        addKevin(new Location(world, 975.5, 86, 555.5), "&9&lPotwash Kevin", "&rWash wash wash... just washing the pots...");
//        addKevin(new Location(world, 968.5, 86, 553.5), "&9&lChef Kevin", "&rWhat are you doing back here?! Get out!");
//        addKevin(new Location(world, 958.5, 87, 534.5), "&9&lDJ Kevin", "&rOh you want C418? I'll put it in the queue.");
//        addKevin(new Location(world, 593.5, 66, 530.5), "&9&lHelp Kevin", "&rHowdy! Join our discord for assistance: https://cubedcon.com/discord");
//        addKevin(new Location(world, 509.5, 71, 527.5), "&9&lTicket Kevin", "&rTickets please!");
//        addKevin(new Location(world, 509.5, 71, 533.5), "&9&lTicket Kevin", "&rOh my, you're gonna have an amazing time!");
//        addKevin(new Location(world, 509.5, 71, 534.5), "&9&lTicket Kevin", "&rMake sure to stop by the Stage to see some of the panels, enjoy your time.");
//        addKevin(new Location(world, 509.5, 71, 540.5), "&9&lTicket Kevin", "&rQR codes facing up please.");
//        addKevin(new Location(world, 509.5, 71, 541.5), "&9&lTicket Kevin", "&rWelcome to Cubed! 2020, please explore the convention center!");
//        addKevin(new Location(world, 509.5, 71, 547.5), "&9&lTicket Kevin", "&rEnsure you have your identification ready to display....");
    }

    public void byeKevins(){
        villagerNPCList.clear();
    }

    private void addKevin(World world, Kevin kevin) {
        NPC npc;
        Location location = new Location(world, kevin.getPosX(), kevin.getPosY(), kevin.getPosZ(), kevin.getFacing(), 0);
        EntityType model;
        switch (kevin.getModel()) {
            case "player" -> model = EntityType.PLAYER;
            case "iron_golem" -> model = EntityType.IRON_GOLEM;
            default -> model = EntityType.VILLAGER;
        }
        if(kevin.getVoiceLines().size() != 0 && !kevin.getVoiceLines().get(0).isBlank())
            npc = npcManager.createNpc(StringUtils.colorizeMessage(kevin.getName()), model,
                    new KevinInteraction(kevin), location, Character.toChars(0x0316)[0]);
        else
            npc = npcManager.createNpc(StringUtils.colorizeMessage(kevin.getName()), model,
                    new KevinInteraction(kevin), location, null);
        if (npc != null) {
            villagerNPCList.add(npc);
            if(npc.getEntity() instanceof ServerPlayer)
                npc.setSkin(kevin.getSkin().getValue(), kevin.getSkin().getSignature());
        }
    }

}
