package org.allaymc.api.block.component.common;

import lombok.Getter;

/**
 * @author tjewers57
 */

@Getter
public final class Coordinate {
    private final int xValue;
    private final int yValue;
    private final int zValue;

    public Coordinate(int xValue, int yValue, int zValue) {
        this.xValue = xValue;
        this.yValue = yValue;
        this.zValue = zValue;
    }
}
