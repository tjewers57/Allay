package org.allaymc.server.entity.component.common;

import lombok.Getter;
import org.allaymc.api.component.annotation.ComponentIdentifier;
import org.allaymc.api.component.annotation.ComponentedObject;
import org.allaymc.api.component.annotation.Dependency;
import org.allaymc.api.entity.Entity;
import org.allaymc.api.entity.component.common.EntityAttributeComponent;
import org.allaymc.api.entity.component.common.EntityBaseComponent;
import org.allaymc.api.entity.component.common.EntityDamageComponent;
import org.allaymc.api.entity.component.event.EntityFallEvent;
import org.allaymc.api.entity.component.player.EntityPlayerHungerComponent;
import org.allaymc.api.entity.damage.DamageContainer;
import org.allaymc.api.eventbus.EventHandler;
import org.allaymc.api.utils.Identifier;
import org.allaymc.api.world.gamerule.GameRule;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityEventType;
import org.cloudburstmc.protocol.bedrock.packet.AnimatePacket;

/**
 * Allay Project 2024/1/12
 *
 * @author daoge_cmd
 */
public class EntityDamageComponentImpl implements EntityDamageComponent {

    @ComponentIdentifier
    public static final Identifier IDENTIFIER = new Identifier("minecraft:entity_damage_component");

    @Dependency
    protected EntityBaseComponent baseComponent;
    @Dependency
    protected EntityAttributeComponent attributeComponent;

    @ComponentedObject
    protected Entity entity;

    @Getter
    protected DamageContainer lastDamage;
    @Getter
    protected long lastDamageTime = 0;

    @Override
    public boolean attack(DamageContainer damage) {
        if (!canAttack(damage)) return false;

        var currentTime = baseComponent.getWorld().getTick();
        if (lastDamage != null && currentTime - lastDamageTime <= lastDamage.getCoolDown()) return false;

        lastDamage = damage;
        lastDamageTime = currentTime;

        // Critical hit
        damage.updateFinalDamage(d -> d * (damage.isCritical() ? 1.5f : 1f));

        // Damage absorption
        var absorption = baseComponent.getAbsorption();
        if (absorption > 0) {
            baseComponent.setAbsorption(Math.max(0, absorption - damage.getFinalDamage()));
            damage.updateFinalDamage(d -> Math.max(0, d - absorption));
        }

        if (entity instanceof EntityPlayerHungerComponent hungerComponent)
            hungerComponent.exhaust(0.1f);

        attributeComponent.setHealth(attributeComponent.getHealth() - damage.getFinalDamage());
        baseComponent.applyEntityEvent(EntityEventType.HURT, 2);

        if (damage.isCritical()) baseComponent.applyAnimation(AnimatePacket.Action.CRITICAL_HIT);
        var attacker = damage.getAttacker();
        if (attacker != null) {
            if (attacker instanceof EntityPlayerHungerComponent hungerComponent)
                hungerComponent.exhaust(0.1f);

            if (damage.hasCustomKnockback()) {
                baseComponent.knockback(attacker.getLocation(), damage.getCustomKnockback());
            } else {
                baseComponent.knockback(attacker.getLocation());
            }
        }

        return true;
    }

    @Override
    public boolean canAttack(DamageContainer damage) {
        return true;
    }

    @Override
    public boolean hasFallDamage() {
        return baseComponent.hasGravity();
    }

    @EventHandler
    private void onEntityFall(EntityFallEvent event) {
        if (!hasFallDamage()) return;
        if (!((boolean) baseComponent.getWorld().getWorldData().getGameRule(GameRule.FALL_DAMAGE))) return;

        var damage = Math.round(event.getFallDistance() - 3);
        if (damage > 0) this.attack(new DamageContainer(entity, DamageContainer.DamageType.FALL, damage));
    }
}
