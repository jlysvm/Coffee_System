package com.example.coffeesystem;

public class Drink {
    private String name;
    private String category;
    private String image;
    private boolean icedAvailable;
    private boolean hotAvailable;


    public Drink() {}

    public Drink(String name, String category, String image, boolean icedAvailable, boolean hotAvailable) {
        this.name = name;
        this.category = category;
        this.image = image;
        this.icedAvailable = icedAvailable;
        this.hotAvailable = hotAvailable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isIcedAvailable() {
        return icedAvailable;
    }

    public void setIcedAvailable(boolean icedAvailable) {
        this.icedAvailable = icedAvailable;
    }

    public boolean isHotAvailable() {
        return hotAvailable;
    }

    public void setHotAvailable(boolean hotAvailable) {
        this.hotAvailable = hotAvailable;
    }
}
