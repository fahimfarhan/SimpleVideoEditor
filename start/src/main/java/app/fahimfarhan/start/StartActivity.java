package app.fahimfarhan.start;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.fahimfarhan.simplevideoeditor.MainActivity;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        final Intent intent = new Intent(StartActivity.this, MainActivity.class);

        TextView openVideoEditor = findViewById(R.id.openVideoEditor);
        openVideoEditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });

        startActivity(intent);
    }
}
