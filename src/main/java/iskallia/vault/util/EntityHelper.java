package iskallia.vault.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.lang.reflect.Field;

public class EntityHelper {

    public static <T extends Entity> T changeSize(T entity, float size, Runnable callback) {
        changeSize(entity, size);
        callback.run();
        return entity;
    }

    public static <T extends Entity> T changeSize(T entity, float size) {
        Field sizeField = Entity.class.getDeclaredFields()[79]; //Entity.size
        sizeField.setAccessible(true);

        try {
            sizeField.set(entity, entity.getDimensions(Pose.STANDING).scale(size));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        entity.refreshDimensions();

        return entity;
    }

    public static void giveItem(PlayerEntity player, ItemStack itemStack) {
        boolean added = player.inventory.add(itemStack);
        if (!added) player.drop(itemStack, false, false);
    }

}
