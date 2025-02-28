package org.allaymc.api.entity.effect.type;

import org.allaymc.api.entity.effect.AbstractEffectType;
import org.allaymc.api.utils.Identifier;

import java.awt.*;

/**
 * Allay Project 2023/10/27
 *
 * @author daoge_cmd
 */
public class EffectWaterBreathingType extends AbstractEffectType {
    public static final EffectWaterBreathingType WATER_BREATHING_TYPE = new EffectWaterBreathingType();

    private EffectWaterBreathingType() {
        super(13, new Identifier("minecraft:water_breathing"), new Color(152, 218, 192));
    }
}
