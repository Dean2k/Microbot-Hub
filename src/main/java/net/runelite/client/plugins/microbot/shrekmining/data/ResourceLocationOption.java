package net.runelite.client.plugins.microbot.shrekmining.data;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Quest;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

import java.util.Map;

@Getter
@Slf4j
public class ResourceLocationOption extends LocationOption {

    private final int numberOfResources;

    public ResourceLocationOption(WorldPoint worldPoint, String name, boolean membersOnly, int numberOfResources) {
        super(worldPoint, name, membersOnly);
        this.numberOfResources = numberOfResources;
    }

    public ResourceLocationOption(WorldPoint worldPoint, String name,
                                  boolean membersOnly,
                                  int numberOfResources,
                                  Map<Quest, QuestState> requiredQuests,
                                  Map<Skill, Integer> requiredSkills,
                                  Map<Integer, Integer> requiredVarbits,
                                  Map<Integer, Integer> requiredVarplayer,
                                  Map<Integer, Integer> requiredItems) {
        super(worldPoint, name, membersOnly, requiredQuests, requiredSkills,
                requiredVarbits, requiredVarplayer, requiredItems);
        this.numberOfResources = numberOfResources;
    }

    public boolean hasMinimumResources(int minResources) {
        return numberOfResources >= minResources;
    }

    public double calculateResourceEfficiencyScore(WorldPoint referencePoint) {
        if (referencePoint == null) {
            return numberOfResources;
        }

        double distance = Math.sqrt(
                Math.pow(getWorldPoint().getX() - referencePoint.getX(), 2) +
                        Math.pow(getWorldPoint().getY() - referencePoint.getY(), 2)
        );

        return numberOfResources * (100.0 / (distance + 1));
    }

    public boolean isBetterThan(ResourceLocationOption other, WorldPoint referencePoint) {
        if (other == null) return true;

        boolean thisAccessible = this.hasRequirements();
        boolean otherAccessible = other.hasRequirements();

        if (thisAccessible && !otherAccessible) return true;
        if (!thisAccessible && otherAccessible) return false;

        if (this.numberOfResources != other.numberOfResources) {
            return this.numberOfResources > other.numberOfResources;
        }

        if (referencePoint != null) {
            return this.calculateResourceEfficiencyScore(referencePoint) >
                    other.calculateResourceEfficiencyScore(referencePoint);
        }

        return true;
    }

    @Override
    public String toString() {
        return getName() + " (" + getWorldPoint().getX() + ", " + getWorldPoint().getY() +
                ", " + getWorldPoint().getPlane() + ") - Resources: " + numberOfResources;
    }
}
