package iskallia.vault.vending;

import com.google.gson.annotations.Expose;
import iskallia.vault.util.nbt.INBTSerializable;
import iskallia.vault.util.nbt.NBTSerialize;

public class Trade implements INBTSerializable {

    @Expose
    @NBTSerialize
    protected Product buy;
    @Expose
    @NBTSerialize
    protected Product extra;
    @Expose
    @NBTSerialize
    protected Product sell;
    @Expose
    @NBTSerialize
    protected int max_trades;
    @Expose
    @NBTSerialize
    protected int times_traded;

    private int hashCode;

    public Trade() {
        // Serialization.
        this.max_trades = -1;
    }

    public Trade(Product buy, Product extra, Product sell) {
        this.buy = buy;
        this.extra = extra;
        this.sell = sell;
    }

    public Trade(Product buy, Product extra, Product sell, int max_trades, int times_traded) {
        this(buy, extra, sell);
        this.max_trades = max_trades;
        this.times_traded = times_traded;
    }

    public Product getBuy() {
        return this.buy;
    }

    public Product getExtra() {
        return this.extra;
    }

    public Product getSell() {
        return this.sell;
    }

    public int getMaxTrades() {
        return max_trades;
    }

    public int getTimesTraded() {
        return times_traded;
    }

    public void setTimesTraded(int amount) { this.times_traded = amount; }

    public int getTradesLeft() {
        if (max_trades == -1) return -1;
        return Math.max(0, max_trades - times_traded);
    }

    public void onTraded() {
        this.times_traded++;
    }

    public boolean isValid() {
        if (this.buy == null || !this.buy.isValid())
            return false;
        if (this.sell == null || !this.sell.isValid())
            return false;
        if (this.extra != null && !this.extra.isValid())
            return false;
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        else if (obj == this)
            return true;
        else if (this.getClass() != obj.getClass())
            return false;

        Trade trade = (Trade) obj;
        return trade.sell.equals(this.sell) && trade.buy.equals(this.buy);
    }

    public void setMaxTrades(int amount) {
        this.max_trades = amount;
    }

    public Trade copy() {
        return new Trade(this.getBuy(), this.getExtra(), this.getSell(), this.max_trades, this.times_traded);
    }
}
