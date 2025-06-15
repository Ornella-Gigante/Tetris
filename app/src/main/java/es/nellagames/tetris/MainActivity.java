package es.nellagames.tetris;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GameView gameView = findViewById(R.id.gameView);

        findViewById(R.id.btnLeft).setOnClickListener(v -> gameView.moveLeft());
        findViewById(R.id.btnRight).setOnClickListener(v -> gameView.moveRight());
        findViewById(R.id.btnRotate).setOnClickListener(v -> gameView.rotate());
    }

}
