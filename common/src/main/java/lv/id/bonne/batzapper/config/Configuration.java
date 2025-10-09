//
// Created by BONNe
// Copyright - 2025
//


package lv.id.bonne.batzapper.config;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.*;

import lv.id.bonne.batzapper.config.annotations.JsonComment;


/**
 * The type Configuration.
 */
public class Configuration
{
    public static Configuration getDefaultConfig()
    {
        Configuration configuration = new Configuration();
        configuration.setDefaults(true);

        return configuration;
    }


    /**
     * Is invalid boolean.
     *
     * @return the boolean
     */
    public boolean isInvalid()
    {
        return this.lureToSummonChance == null ||
            this.lureOperationRange == null ||
            this.lureBatLimit == null ||
            this.lureSummonsPerTry == null ||
            this.zapperOperationRange == null ||
            this.playerSearchRange == null;
    }


    /**
     * Sets defaults.
     */
    public void setDefaults(boolean init)
    {
        if (this.lureToSummonChance == null || init)
        {
            this.lureToSummonChance = 0.33f;
        }

        if (this.lureOperationRange == null || init)
        {
            this.lureOperationRange = 32;
        }

        if (this.lureBatLimit == null || init)
        {
            this.lureBatLimit = 7;
        }

        if (this.lureSummonsPerTry == null || init)
        {
            this.lureSummonsPerTry = 1;
        }

        if (this.zapperOperationRange == null || init)
        {
            this.zapperOperationRange = 20;
        }

        if (this.playerSearchRange == null || init)
        {
            this.playerSearchRange = 12;
        }
    }


    /**
     * Gets lure to summon chance.
     *
     * @return the lure to summon chance
     */
    public float getLureToSummonChance()
    {
        return this.lureToSummonChance;
    }


    /**
     * Gets lure operation range.
     *
     * @return the lure operation range
     */
    public int getLureOperationRange()
    {
        return this.lureOperationRange;
    }


    /**
     * Gets lure bat limit.
     *
     * @return the lure bat limit
     */
    public int getLureBatLimit()
    {
        return this.lureBatLimit;
    }


    /**
     * Gets lure summons per try.
     *
     * @return the lure summons per try
     */
    public int getLureSummonsPerTry()
    {
        return this.lureSummonsPerTry;
    }


    /**
     * Gets zapper operation range.
     *
     * @return the zapper operation range
     */
    public int getZapperOperationRange()
    {
        return this.zapperOperationRange;
    }


    /**
     * Gets player search range.
     *
     * @return the player search range
     */
    public int getPlayerSearchRange()
    {
        return this.playerSearchRange;
    }


    @JsonComment("Allows to change a chance to trigger lure summoning bat(-s) when it is random-ticked.")
    @JsonComment("Values in range 0 - 1.")
    @JsonComment("Default = 0.33.")
    @Expose
    @SerializedName("lure_summon_chance")
    private Float lureToSummonChance = 0.33f;

    @JsonComment("Allows to change in how large area lure operates.")
    @JsonComment("It is used to detect bat limit and spawn position search for bats.")
    @JsonComment("Default = 32.")
    @Expose
    @SerializedName("lure_operation_range")
    private Integer lureOperationRange = 32;

    @JsonComment("Allows to change how many bats can be in range of bat lure for it to continue spawning.")
    @JsonComment("Default = 7.")
    @Expose
    @SerializedName("lure_bat_limit")
    private Integer lureBatLimit = 7;

    @JsonComment("Allows to change how many bats are spawned at once.")
    @JsonComment("Default = 1.")
    @Expose
    @SerializedName("lure_bat_summon_count")
    private Integer lureSummonsPerTry = 1;

    @JsonComment("Allows to change in how large area zapper operates.")
    @JsonComment("Default = 20.")
    @Expose
    @SerializedName("zapper_operation_range")
    private Integer zapperOperationRange = 20;

    @JsonComment("Allows to change in how large area bats searches for player with bat zapper in hand.")
    @JsonComment("Default = 12.")
    @Expose
    @SerializedName("player_search_range")
    private Integer playerSearchRange = 12;
}
