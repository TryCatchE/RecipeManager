package com.example.recipemanager.utils;

    import android.app.Activity;
    import android.content.Intent;
    import android.view.View;
    import android.widget.Button;
    import android.widget.TextView;
    import com.example.recipemanager.MainActivity;

public class HeaderUtil {
    public static void setupHeader(Activity activity, String title, int viewId, int btnId, boolean isButtonVisible) {
        TextView titleTextView = activity.findViewById(viewId);
        titleTextView.setText(title);

        Button backButton = activity.findViewById(btnId);
        titleTextView.setText(title);

        if (isButtonVisible && btnId != 0) {
            backButton.setVisibility(View.VISIBLE);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, MainActivity.class);
                    activity.startActivity(intent);
                    activity.finish();
                }
            });
        } else {
            backButton.setVisibility(View.GONE);
        }
    }
}
