package com.example.recipemanager.activities;

    import android.content.Intent;
    import android.graphics.Bitmap;
    import android.graphics.BitmapFactory;
    import android.os.Bundle;
    import android.view.View;
    import android.widget.Button;
    import android.widget.ImageView;
    import android.widget.TextView;
    import android.widget.Toast;
    import androidx.appcompat.app.AppCompatActivity;
    import com.example.recipemanager.MainActivity;
    import com.example.recipemanager.R;
    import com.example.recipemanager.database.DatabaseHelper;
    import com.example.recipemanager.models.Recipe;
    import com.example.recipemanager.utils.HeaderUtil;

public class SingleRecipeActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private Recipe recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_activity);

        databaseHelper = new DatabaseHelper(this);

        long recipeId = getIntent().getLongExtra("recipeId", -1);

        recipe = databaseHelper.getRecipeById(recipeId);

        if (recipe != null) {
            // Set recipe image (if available)
            // ImageView recipeImageView = findViewById(R.id.recipeImageView);

            if (recipe.getImageData() != null) {
                ImageView recipeImageView = findViewById(R.id.recipeImageView);
                Bitmap bitmap = BitmapFactory.decodeByteArray(recipe.getImageData(), 0, recipe.getImageData().length);
                recipeImageView.setImageBitmap(bitmap);
            }

            HeaderUtil.setupHeader(this, recipe.getTitle(), R.id.titleTextView, R.id.backButton, true);

            TextView mainIngredientsTextView = findViewById(R.id.mainIngredientsTextView);
            mainIngredientsTextView.setText("Main Ingredients: \n" + recipe.getMainIngredients());

            TextView recipeDescriptionTextView = findViewById(R.id.recipeDescriptionTextView);
            recipeDescriptionTextView.setText("Recipe Description: \n" + recipe.getDescription());
        }

        // Set up buttons
        Button deleteButton = findViewById(R.id.deleteButton);
        Button updateButton = findViewById(R.id.updateButton);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean deleted = databaseHelper.deleteRecipe(recipeId);

                if (deleted) {
                    Toast.makeText(SingleRecipeActivity.this, "Recipe deleted successfully", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SingleRecipeActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {

                    Toast.makeText(SingleRecipeActivity.this, "Failed to delete recipe", Toast.LENGTH_SHORT).show();
                }
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SingleRecipeActivity.this, UpdateRecipeActivity.class);
                intent.putExtra("recipeId", recipeId);
                startActivity(intent);
            }
        });

    }
}
