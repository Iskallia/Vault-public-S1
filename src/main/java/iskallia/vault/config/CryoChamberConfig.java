package iskallia.vault.config;

import com.google.gson.annotations.Expose;

import java.util.HashMap;

public class CryoChamberConfig extends Config {

    @Expose private int INFUSION_TIME;
    @Expose private int GROW_ETERNAL_TIME;
    @Expose private HashMap<String, Integer> STREAMER_CORE_REQ = new HashMap<>();

//	@Expose public int GENERATOR_FE_PER_TICK_MIN;
//	@Expose public int GENERATOR_FE_PER_TICK_MAX;
//	@Expose public int GENERATOR_FE_CAPACITY;
//	@Expose public WeightedList<Product> MINER_DROPS;
//	@Expose public int MINER_TICKS_DELAY;
//	@Expose public WeightedList<Product> LOOTER_DROPS;
//	@Expose public int LOOTER_TICKS_DELAY;

    @Override
    public String getName() {
        return "cryo_chamber";
    }

    public int getPlayerCoreCount(String name) {
        if (STREAMER_CORE_REQ.containsKey(name))
            return STREAMER_CORE_REQ.get(name);
        return 100;
    }

    public int getGrowEternalTime() { return this.GROW_ETERNAL_TIME * 20; }
    public int getInfusionTime() { return this.INFUSION_TIME * 20; }

    @Override
    protected void reset() {
        INFUSION_TIME = 4;
        GROW_ETERNAL_TIME = 10;

        STREAMER_CORE_REQ.put("iskall85", 100);
        STREAMER_CORE_REQ.put("Stressmonster101", 100);
        STREAMER_CORE_REQ.put("AntonioAsh", 100);

//		this.GENERATOR_FE_PER_TICK_MIN = 100;
//		this.GENERATOR_FE_PER_TICK_MAX = 1000;
//		this.GENERATOR_FE_CAPACITY = 100000;
//
//		this.MINER_DROPS = new WeightedList<Product>()
//				.add(new Product(Items.IRON_ORE, 2, new CompoundNBT()), 1)
//				.add(new Product(Items.GOLD_ORE, 2, new CompoundNBT()), 1)
//				.add(new Product(Items.DIAMOND_ORE, 1, new CompoundNBT()), 1).strip();
//
//		this.MINER_TICKS_DELAY = 100;
//
//		this.LOOTER_DROPS = new WeightedList<Product>()
//				.add(new Product(Items.EMERALD, 1, new CompoundNBT()), 1)
//				.add(new Product(Items.PAPER, 10, new CompoundNBT()), 1)
//				.add(new Product(Items.WHITE_WOOL, 3, new CompoundNBT()), 1).strip();
//
//		this.LOOTER_TICKS_DELAY = 100;
    }

}
