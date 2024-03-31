package com.example.recipemanager.models;

    import android.os.Parcel;
    import android.os.Parcelable;
    import java.util.ArrayList;
    import java.util.List;

public class Recipe implements Parcelable {
    private long id;
    private String title;
    private String description;
    private byte[] imageData;
    private List<Ingredient> ingredients;

    public Recipe(long id, String name, String description, List<Ingredient> ingredients) {
        this.id = id;
        this.title = name;
        this.description = description;
        this.ingredients = ingredients;
    }

    public Recipe(long id, String name, String description) {
        this.id = id;
        this.title = name;
        this.description = description;
    }

    public Recipe() {

    }
    public Recipe(long id, String name, String description, byte[] imageData) {
        this.id = id;
        this.title = name;
        this.description = description;
        this.imageData = imageData;
    }

    protected Recipe(Parcel in) {
        id = in.readLong();
        title = in.readString();
        description = in.readString();
        ingredients = new ArrayList<>();
        in.readList(ingredients, Ingredient.class.getClassLoader());
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeList(ingredients);
    }
    public String getMainIngredients() {
        StringBuilder mainIngredientsBuilder = new StringBuilder();
        for (Ingredient ingredient : ingredients) {
            mainIngredientsBuilder.append("• ").append(ingredient.getName()).append("\n");
        }
        if (mainIngredientsBuilder.length() > 1) {
            mainIngredientsBuilder.setLength(mainIngredientsBuilder.length() - 1);
        }
        return mainIngredientsBuilder.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void addIngredient(String ingredientName) {
        ingredients.add(new Ingredient(ingredientName));
    }

    public byte[] getImageData() {
        return imageData;
    }
}
