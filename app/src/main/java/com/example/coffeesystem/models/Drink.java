package com.example.coffeesystem.models;

public class Drink {
    private long id;
    private String name;
    private String description;
    private String image;
    private String category;
    private String ingredients;
    private boolean isFavorited;

    public Drink(String name, String description, String image,
                 String category, String ingredients, boolean isFavorited) {
        this.id = 0;
        this.name = name;
        this.description = description;
        this.image = image;
        this.category = category;
        this.ingredients = ingredients;
        this.isFavorited = isFavorited;
    }

    public Drink(long id, String name, String description, String image,
                 String category, String ingredients, boolean isFavorited) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.category = category;
        this.ingredients = ingredients;
        this.isFavorited = isFavorited;
    }

    public long getId() {
        return id;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public boolean isFavorited() {
        return isFavorited;
    }

    public void setFavorited(boolean isFavorited) {
        this.isFavorited = isFavorited;
    }
}
