package com.example.recipemanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.recipemanager.activities.CreateRecipeActivity;
import com.example.recipemanager.activities.QrActivity;
import com.example.recipemanager.activities.ViewRecipesActivity;
import com.example.recipemanager.utils.HeaderUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HeaderUtil.setupHeader(this, "Main Activity", R.id.titleTextView, R.id.backButton, false);

        int[] buttonIds = {R.id.button1, R.id.button2, R.id.button3};

        for (int buttonId : buttonIds) {
            Button button = findViewById(buttonId);
            button.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        int[] buttonIds = {R.id.button1, R.id.button2, R.id.button3};
        Class<?>[] activities = {CreateRecipeActivity.class, ViewRecipesActivity.class, QrActivity.class};

        for (int i = 0; i < buttonIds.length; i++) {
            if (v.getId() == buttonIds[i]) {
                Intent intent = new Intent(MainActivity.this, activities[i]);
                startActivity(intent);
                break;
            }
        }
    }
}
