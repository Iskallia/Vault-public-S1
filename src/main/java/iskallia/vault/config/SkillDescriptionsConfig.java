package iskallia.vault.config;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.HashMap;

public class SkillDescriptionsConfig extends Config {

    @Expose private HashMap<String, JsonElement> descriptions;

    @Override
    public String getName() {
        return "skill_descriptions";
    }

    public IFormattableTextComponent getDescriptionFor(String skillName) {
        JsonElement element = descriptions.get(skillName);
        if (element == null) {
            return StringTextComponent.Serializer.fromJsonLenient(
                    "[" +
                            "{text:'No description for ', color:'#192022'}," +
                            "{text: '" + skillName + "', color: '#fcf5c5'}," +
                            "{text: ', yet', color: '#192022'}" +
                            "]"
            );
        }
        return StringTextComponent.Serializer.fromJson(element);
    }

    @Override
    protected void reset() {
        this.descriptions = new HashMap<>();
    }

}
