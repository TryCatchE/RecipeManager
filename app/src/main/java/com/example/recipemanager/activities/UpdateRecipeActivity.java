package com.example.recipemanager.activities;

    import android.content.Intent;
    import android.graphics.Bitmap;
    import android.graphics.BitmapFactory;
    import android.os.Bundle;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.ImageView;
    import android.widget.LinearLayout;
    import android.widget.TextView;
    import androidx.appcompat.app.AppCompatActivity;
    import com.example.recipemanager.R;
    import com.example.recipemanager.database.DatabaseHelper;
    import com.example.recipemanager.models.Ingredient;
    import com.example.recipemanager.models.Recipe;
    import com.example.recipemanager.utils.HeaderUtil;
    import java.util.ArrayList;
    import java.util.List;

public class UpdateRecipeActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private Recipe recipe;
    private LinearLayout ingredientsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_activity);

        databaseHelper = new DatabaseHelper(this);
        ingredientsLayout = findViewById(R.id.ingredientsLayout);

        long recipeId = getIntent().getLongExtra("recipeId", -1);
        recipe = databaseHelper.getRecipeById(recipeId);

        if (recipe != null) {
            HeaderUtil.setupHeader(this, "Update recipe", R.id.titleTextView, R.id.backButton, true);

            if (recipe.getImageData() != null) {
                ImageView recipeImageView = findViewById(R.id.imagePlaceholder);
                Bitmap bitmap = BitmapFactory.decodeByteArray(recipe.getImageData(), 0, recipe.getImageData().length);
                recipeImageView.setImageBitmap(bitmap);
            }

            TextView recipeTitle = findViewById(R.id.editTextTitle);
            recipeTitle.setText(recipe.getTitle());

            TextView recipeDescriptionTextView = findViewById(R.id.editTextDescription);
            recipeDescriptionTextView.setText(recipe.getDescription());

            List<Ingredient> ingredients = recipe.getIngredients();
            for (Ingredient ingredient : ingredients) {
                addIngredientField(ingredient.getName());
            }
        }

        Button buttonAddIngredient = findViewById(R.id.buttonAddIngredient);
        buttonAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addIngredientField("");
            }
        });

        Button saveBtn = findViewById(R.id.buttonSave);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updatedTitle = ((TextView) findViewById(R.id.editTextTitle)).getText().toString();
                String updatedDescription = ((TextView) findViewById(R.id.editTextDescription)).getText().toString();
                ImageView img = findViewById(R.id.imagePlaceholder);

                List<String> updatedIngredients = new ArrayList<>();
                for (int i = 0; i < ingredientsLayout.getChildCount(); i++) {
                    View view = ingredientsLayout.getChildAt(i);
                    if (view instanceof EditText) {
                        String ingredientName = ((EditText) view).getText().toString().trim();
                        if (!ingredientName.isEmpty()) {
                            updatedIngredients.add(ingredientName);
                        }
                    }
                }

                databaseHelper.updateRecipe(recipe.getId(), updatedTitle, updatedDescription, updatedIngredients);

                Intent intent = new Intent(UpdateRecipeActivity.this, SingleRecipeActivity.class);
                intent.putExtra("recipeId", recipeId);
                startActivity(intent);
            }
        });
    }

    private void addIngredientField(String ingredientName) {
        EditText editText = new EditText(this);
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        editText.setHint("Enter ingredient");
        editText.setText(ingredientName);

        ingredientsLayout.addView(editText);
    }
}

