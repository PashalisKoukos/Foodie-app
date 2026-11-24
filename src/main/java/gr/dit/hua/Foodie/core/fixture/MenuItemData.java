package gr.dit.hua.Foodie.core.fixture;

import java.math.BigDecimal;

public class MenuItemData {

    private String name;
    private String description;
    private BigDecimal price;

    public MenuItemData() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}