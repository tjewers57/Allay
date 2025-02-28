package org.allaymc.server.network.processor.login;

import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.eventbus.event.server.player.PlayerInitializedEvent;
import org.allaymc.api.i18n.TrKeys;
import org.allaymc.api.network.processor.ILoginPacketProcessor;
import org.allaymc.api.server.Server;
import org.allaymc.api.utils.TextFormat;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacketType;
import org.cloudburstmc.protocol.bedrock.packet.SetLocalPlayerAsInitializedPacket;

/**
 * Allay Project 2024/2/2
 *
 * @author daoge_cmd
 */
public class SetLocalPlayerAsInitializedPacketProcessor extends ILoginPacketProcessor<SetLocalPlayerAsInitializedPacket> {
    @Override
    public void handle(EntityPlayer player, SetLocalPlayerAsInitializedPacket packet) {
        var event = new PlayerInitializedEvent(player);
        Server.getInstance().getEventBus().callEvent(event);
        if (event.isCancelled()) player.disconnect(TrKeys.M_DISCONNECTIONSCREEN_NOREASON);
        // We only accept player's movement inputs, which are after SetLocalPlayerAsInitializedPacket,
        // So after player sent SetLocalPlayerAsInitializedPacket, we need to sync the pos with client
        // Otherwise the client will snap into the ground
        player.sendLocationToSelf();
        player.setInitialized();
        Server.getInstance().broadcastTr(TextFormat.YELLOW + "%" + TrKeys.M_MULTIPLAYER_PLAYER_JOINED, player.getOriginName());
    }

    @Override
    public BedrockPacketType getPacketType() {
        return BedrockPacketType.SET_LOCAL_PLAYER_AS_INITIALIZED;
    }
}
