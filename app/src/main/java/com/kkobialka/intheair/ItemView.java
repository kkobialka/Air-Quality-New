package com.kkobialka.intheair;

public class ItemView {
    private String itemCityGetText;
    private String itemCityName;


    public ItemView(String itemCityGetText, String itemCityName) {
        this.itemCityGetText = itemCityGetText;
        this.itemCityName = itemCityName;
    }

    public String getItemCityGetText() {
        return itemCityGetText;
    }

    public void setItemCityGetText(String itemCityGetText) {
        this.itemCityGetText = itemCityGetText;
    }

    public String getItemCityName() {
        return itemCityName;
    }

    public void setItemCityName(String itemCityName) {
        this.itemCityName = itemCityName;
    }
}
