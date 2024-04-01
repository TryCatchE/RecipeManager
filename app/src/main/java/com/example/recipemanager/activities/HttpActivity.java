package com.example.recipemanager.activities;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.recipemanager.R;
import com.example.recipemanager.database.DatabaseHelper;
import com.example.recipemanager.models.Ingredient;
import com.example.recipemanager.models.Recipe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpActivity extends AppCompatActivity {

    private TextView titleTextView;
    private TextView recipeDescriptionTextView;
    private TextView mainIngredientsListView;
    private ImageView recipeImageView;
    private Button saveButton;

    private String title;
    private String description;
    private String imageData;
    private String[] ingredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.http_activity);

        titleTextView = findViewById(R.id.titleTextView);
        recipeDescriptionTextView = findViewById(R.id.recipeDescriptionTextView);
        mainIngredientsListView = findViewById(R.id.mainIngredientsListView);
        recipeImageView = findViewById(R.id.recipeImageView);
        saveButton = findViewById(R.id.saveButton);


        OkHttpClient client = new OkHttpClient();
        String url = getIntent().getStringExtra("url");

        if (url == null || url.isEmpty()) {
            url = "http://10.0.2.2:8080/api/recipe/1";
        }

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }

                    String responseData = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseData);

                    // Parse JSON response
                    title = jsonObject.getString("title");
                    description = jsonObject.getString("description");
                    JSONArray ingredientsArray = jsonObject.getJSONArray("ingredients");
                    ingredients = new String[ingredientsArray.length()];
                    for (int i = 0; i < ingredientsArray.length(); i++) {
                        JSONObject ingredientObject = ingredientsArray.getJSONObject(i);
                        ingredients[i] = ingredientObject.getString("name");
                    }

                    // Decode Base64 image data
                    imageData = jsonObject.getString("imageDataBase64");
                    byte[] decodedImage = android.util.Base64.decode(imageData, android.util.Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);

                    // Update UI on the main thread
                    runOnUiThread(() -> {
                        titleTextView.setText(title);
                        recipeDescriptionTextView.setText(description);
                        StringBuilder ingredientsText = new StringBuilder();
                        for (String ingredient : ingredients) {
                            ingredientsText.append(ingredient).append("\n");
                        }
                        mainIngredientsListView.setText(ingredientsText.toString());
                        // Set the Bitmap to the ImageView
                        recipeImageView.setImageBitmap(bitmap);
                    });

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRecipeToDatabase();
            }
        });
    }

    private void saveRecipeToDatabase() {
        // Convert Bitmap to byte array
        byte[] imageByteArray = convertBitmapToByteArray();

        // Insert recipe data into database
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        long recipeId = databaseHelper.insertRecipe(title, description, imageByteArray);

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

    private byte[] convertBitmapToByteArray() {
        // Convert Bitmap to byte array
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bitmap = ((BitmapDrawable) recipeImageView.getDrawable()).getBitmap();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }
}
