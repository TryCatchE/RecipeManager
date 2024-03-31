package com.example.recipemanager.activities;

    import android.content.Intent;
    import android.os.Bundle;
    import android.view.View;
    import android.widget.AdapterView;
    import android.widget.ArrayAdapter;
    import android.widget.ListView;
    import androidx.appcompat.app.AppCompatActivity;
    import com.example.recipemanager.R;
    import com.example.recipemanager.database.DatabaseHelper;
    import com.example.recipemanager.models.Recipe;
    import com.example.recipemanager.utils.HeaderUtil;

    import java.util.List;

public class ViewRecipesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_activity);

        HeaderUtil.setupHeader(this, "All recipes", R.id.titleTextView, R.id.backButton, true);

        ListView recipesListView = findViewById(R.id.recipesListView);
        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        List<Recipe> recipes = databaseHelper.getAllRecipes();

        String[] recipeNames = new String[recipes.size()];
        for (int i = 0; i < recipes.size(); i++) {
            recipeNames[i] = recipes.get(i).getTitle();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, recipeNames);

        recipesListView.setAdapter(adapter);

        recipesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long recipeId = recipes.get(position).getId();

                Intent intent = new Intent(ViewRecipesActivity.this, SingleRecipeActivity.class);
                intent.putExtra("recipeId", recipeId);
                startActivity(intent);
            }
        });
    }
}
