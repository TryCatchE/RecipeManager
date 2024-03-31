package com.example.recipemanager.activities;

        import android.os.Bundle;
        import androidx.appcompat.app.AppCompatActivity;
        import com.example.recipemanager.R;
        import com.example.recipemanager.utils.HeaderUtil;

public class GetRandomRecipeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_activity);
        HeaderUtil.setupHeader(this, "Get a random recipe", R.id.titleTextView, R.id.backButton, true);

    }
}

