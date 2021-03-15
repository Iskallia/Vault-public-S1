package iskallia.vault.util;

import net.minecraft.util.ResourceLocation;

public class ResourceBoundary {

    ResourceLocation resource;
    int u, v;
    int w, h;

    public ResourceBoundary(ResourceLocation resource, int u, int v, int w, int h) {
        this.resource = resource;
        this.u = u;
        this.v = v;
        this.w = w;
        this.h = h;
    }

    public ResourceLocation getResource() {
        return resource;
    }

    public int getU() {
        return u;
    }

    public int getV() {
        return v;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

}
