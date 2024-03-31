package com.example.recipemanager.models;
import android.os.Parcel;
        import android.os.Parcelable;

public class Ingredient implements Parcelable {
    private String name;

    public Ingredient(String name) {
        this.name = name;
    }

    protected Ingredient(Parcel in) {
        name = in.readString();
    }

    public static final Creator<Ingredient> CREATOR = new Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };

    public String getName() {
        return name;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
