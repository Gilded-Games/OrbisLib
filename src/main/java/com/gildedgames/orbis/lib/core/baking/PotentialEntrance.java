package com.gildedgames.orbis.lib.core.baking;

import com.gildedgames.orbis.lib.data.blueprint.BlueprintData;
import com.gildedgames.orbis.lib.data.schedules.ScheduleEntranceHolder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PotentialEntrance
{
    private BlueprintData data;
    private ScheduleEntranceHolder holder;

    public PotentialEntrance(BlueprintData data, ScheduleEntranceHolder holder) {
        this.data = data;
        this.holder = holder;
    }

    public BlueprintData getData() {
        return this.data;
    }

    public ScheduleEntranceHolder getHolder() {
        return this.holder;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof PotentialEntrance) {
            PotentialEntrance obj2 = (PotentialEntrance)obj;
            return new EqualsBuilder().append(this.data, obj2.data).append(this.holder, obj2.holder).build();
        }

        return false;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.data).append(this.holder).build();
    }
}
