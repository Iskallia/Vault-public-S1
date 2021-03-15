package iskallia.vault.config;

public class VaultGearBaseConfig extends Config {

    @Override
    public String getName() {
        return null;
    }

    @Override
    protected void reset() {

    }

    /*
    @Override
    public String getName() {
        return "vault_gear_base";
    }

    @Goodie private List<GearBaseStats> SWORD;
    @Goodie private List<GearBaseStats> AXE;
    @Goodie private List<GearBaseStats> HELM;

    public List<GearBaseStats> getSWORD() {
        return SWORD;
    }

    public List<GearBaseStats> getAXE() {
        return AXE;
    }

    public List<GearBaseStats> getHELM() {
        return HELM;
    }

    @Override
    public GoodieSchema<GoodieObject> getRootSchema() {
        return ObjectSchema.of(
                getBaseStatSchema("SWORD"),
                getBaseStatSchema("AXE"),
                getBaseStatSchema("HELM")
        );
    }

    private GoodieSchema<GoodieArray> getBaseStatSchema(String field) {
        ObjectSchema baseStatSchema = ObjectSchema.of(
                new PrimitiveSchema("rarity", "common")
                        .withValidator(new StringValidator()),

                ObjectSchema.of(
                        "damage",
                        new PrimitiveSchema("min", 0)
                                .withValidator(new IntegerValidator()),
                        new PrimitiveSchema("max", 10)
                                .withValidator(new IntegerValidator())
                ),

                ObjectSchema.of(
                        "durability",
                        new PrimitiveSchema("min", 0)
                                .withValidator(new IntegerValidator()),
                        new PrimitiveSchema("max", 10)
                                .withValidator(new IntegerValidator())
                ),

                ObjectSchema.of(
                        "attackspeed",
                        new PrimitiveSchema("min", 0)
                                .withValidator(new IntegerValidator()),
                        new PrimitiveSchema("max", 10)
                                .withValidator(new IntegerValidator())
                )
        );

        return new ArraySchema(field, baseStatSchema)
                .withPreGeneratedValues(baseStatSchema.getDefaultValue());
    }*/

}
