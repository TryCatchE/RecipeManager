package com.example.recipemanager.activities;

    import android.content.Intent;
    import android.content.pm.PackageManager;
    import android.database.Cursor;
    import android.graphics.Bitmap;
    import android.graphics.BitmapFactory;
    import android.net.Uri;
    import android.os.Bundle;
    import android.os.Environment;
    import android.provider.MediaStore;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.ImageView;
    import android.widget.LinearLayout;
    import android.widget.Toast;

    import androidx.annotation.Nullable;
    import androidx.appcompat.app.AppCompatActivity;

    import com.example.recipemanager.R;
    import com.example.recipemanager.database.DatabaseHelper;
    import com.example.recipemanager.utils.HeaderUtil;

    import java.io.ByteArrayOutputStream;
    import java.io.File;
    import java.io.IOException;
    import java.util.ArrayList;
    import java.util.List;

public class CreateRecipeActivity extends AppCompatActivity {

    private LinearLayout ingredientsLayout;
    private EditText editTextTitle;
    private EditText editTextDescription;
    private Bitmap capturedImage;

    private static final int REQUEST_CODE = 22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_activity);
        HeaderUtil.setupHeader(this, "Create a new Recipe", R.id.titleTextView, R.id.backButton, true);

        ingredientsLayout = findViewById(R.id.ingredientsLayout);
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);

        Button buttonAddIngredient = findViewById(R.id.buttonAddIngredient);
        buttonAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addIngredientField();
            }
        });

        Button buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRecipeToDatabase();
            }
        });

        Button imgUpl = findViewById(R.id.imgBtn);
        imgUpl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera,REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        ImageView imgPlaceholder = findViewById(R.id.imagePlaceholder);

        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            capturedImage = (Bitmap) data.getExtras().get("data");
            imgPlaceholder.setImageBitmap(capturedImage);
        }else{
            Toast.makeText(this,"Something went wrong", Toast.LENGTH_SHORT).show();
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private String getPathFromURI(Uri contentUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(columnIndex);
            cursor.close();
            return path;
        }
        return null;
    }

    private void addIngredientField() {
        EditText editText = new EditText(this);
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        editText.setHint("Enter ingredient");
        ingredientsLayout.addView(editText);
    }

    private void saveRecipeToDatabase() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        List<String> ingredients = getIngredientsFromLayout();

        if (title.isEmpty() || description.isEmpty() || ingredients.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert Bitmap to byte array
        byte[] imageData = null;
        if (capturedImage != null) {
            try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
                capturedImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                imageData = stream.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        long recipeId = databaseHelper.insertRecipe(title, description,imageData);

        if (recipeId != -1) {
            for (String ingredient : ingredients) {
                databaseHelper.insertIngredient((int) recipeId, ingredient);
            }

            Toast.makeText(this, "Recipe saved successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save recipe", Toast.LENGTH_SHORT).show();
        }
    }

    private List<String> getIngredientsFromLayout() {
        List<String> ingredients = new ArrayList<>();
        for (int i = 0; i < ingredientsLayout.getChildCount(); i++) {
            View childView = ingredientsLayout.getChildAt(i);
            if (childView instanceof EditText) {
                EditText editText = (EditText) childView;
                String ingredient = editText.getText().toString().trim();
                if (!ingredient.isEmpty()) {
                    ingredients.add(ingredient);
                }
            }
        }
        return ingredients;
    }
}
