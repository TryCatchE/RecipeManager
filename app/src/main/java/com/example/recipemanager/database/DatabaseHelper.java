package com.example.recipemanager.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.recipemanager.models.Ingredient;
import com.example.recipemanager.models.Recipe;
import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "recipe.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE recipes(ID INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, description TEXT, image BLOB)");
        db.execSQL("CREATE TABLE ingredients(ID INTEGER PRIMARY KEY AUTOINCREMENT, recipe_id INTEGER, name TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS recipes");
        db.execSQL("DROP TABLE IF EXISTS ingredients");
        onCreate(db);
    }

    public long insertRecipe(String title, String description, byte[] imageData) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("description", description);
        contentValues.put("image", imageData); // Add the image data to the ContentValues
        return db.insert("recipes", null, contentValues);
    }

    public void insertIngredient(int recipeId, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("recipe_id", recipeId);
        contentValues.put("name", name);
        db.insert("ingredients", null, contentValues);
    }
    public List<Recipe> getAllRecipes() {
        List<Recipe> recipeList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM  recipes";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex("ID");
            int titleIndex = cursor.getColumnIndex("title");
            int descriptionIndex = cursor.getColumnIndex("description");
            do {
                Recipe recipe = new Recipe();
                int recipeId = cursor.getInt(idIndex);
                recipe.setId(recipeId);
                recipe.setTitle(cursor.getString(titleIndex));
                recipe.setDescription(cursor.getString(descriptionIndex));

                // Retrieve ingredients for the current recipe
//                List<String> ingredients = getIngredientsForRecipe(db, recipeId);
//                recipe.setIngredients(ingredients);

                recipeList.add(recipe);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return recipeList;
    }
    public boolean deleteRecipe(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("recipes", "ID=?", new String[]{String.valueOf(id)}) > 0;
    }

    public void updateRecipe(long recipeId, String title, String description, List<String> ingredients) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Update recipe details
        ContentValues recipeValues = new ContentValues();
        recipeValues.put("title", title);
        recipeValues.put("description", description);
//        recipeValues.put("image",imageData);
        db.update("recipes", recipeValues, "ID=?", new String[]{String.valueOf(recipeId)});

        // Update ingredients
        // First, delete existing ingredients for the recipe
        db.delete("ingredients", "recipe_id=?", new String[]{String.valueOf(recipeId)});

        // Then, insert new ingredients
        for (String ingredient : ingredients) {
            ContentValues ingredientValues = new ContentValues();
            ingredientValues.put("recipe_id", recipeId);
            ingredientValues.put("name", ingredient);
            db.insert("ingredients", null, ingredientValues);
        }
    }

    @SuppressLint("Range")
    public Recipe getRecipeById(long recipeId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Define the columns you want to retrieve for the recipe
        String[] recipeProjection = {"title", "description","image"};

        // Define the selection criteria for the recipe
        String recipeSelection = "ID=?";
        String[] recipeSelectionArgs = {String.valueOf(recipeId)};

        // Execute the query to retrieve the recipe details
        Cursor recipeCursor = db.query("recipes", recipeProjection, recipeSelection, recipeSelectionArgs, null, null, null);

        // Initialize the Recipe object to store the retrieved recipe
        Recipe recipe = null;

        // Check if a recipe with the given ID exists
        if (recipeCursor != null && recipeCursor.moveToFirst()) {
            // Retrieve the recipe details
            @SuppressLint("Range") String title = recipeCursor.getString(recipeCursor.getColumnIndex("title"));
            @SuppressLint("Range") String description = recipeCursor.getString(recipeCursor.getColumnIndex("description"));
//            @SuppressLint("Range") byte[] imageData ;
            byte[] imageData = recipeCursor.getBlob(recipeCursor.getColumnIndex("image"));
            recipe = new Recipe(recipeId, title, description,imageData);

            // Close the cursor for recipe details
            recipeCursor.close();

            // Fetch ingredients associated with the recipe
            List<Ingredient> ingredients = getIngredientsForRecipe(recipeId, db);
            recipe.setIngredients(ingredients);
        }

        // Return the retrieved recipe with its ingredients
        return recipe;
    }

    private List<Ingredient> getIngredientsForRecipe(long recipeId, SQLiteDatabase db) {
        List<Ingredient> ingredients = new ArrayList<>();

        // Define the columns you want to retrieve for ingredients
        String[] ingredientProjection = {"name"};

        // Define the selection criteria for ingredients
        String ingredientSelection = "recipe_id=?";
        String[] ingredientSelectionArgs = {String.valueOf(recipeId)};

        // Execute the query to retrieve the ingredients
        Cursor ingredientCursor = db.query("ingredients", ingredientProjection, ingredientSelection, ingredientSelectionArgs, null, null, null);

        // Check if ingredients exist for the recipe
        if (ingredientCursor != null && ingredientCursor.moveToFirst()) {
            // Loop through the cursor to extract ingredients
            do {
                @SuppressLint("Range") String ingredientName = ingredientCursor.getString(ingredientCursor.getColumnIndex("name"));
                ingredients.add(new Ingredient(ingredientName));
            } while (ingredientCursor.moveToNext());

            // Close the cursor for ingredients
            ingredientCursor.close();
        }

        return ingredients;
    }

}


