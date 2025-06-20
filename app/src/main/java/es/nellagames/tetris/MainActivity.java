package es.nellagames.tetris;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private Button btnPause;
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicialización después de setContentView
        btnPause = findViewById(R.id.btnPause);
        gameView = findViewById(R.id.gameView);

        findViewById(R.id.btnLeft).setOnClickListener(v -> gameView.moveLeft());
        findViewById(R.id.btnRight).setOnClickListener(v -> gameView.moveRight());
        findViewById(R.id.btnRotate).setOnClickListener(v -> gameView.rotate());

        btnPause.setOnClickListener(v -> {
            if (gameView.isPaused()) {
                gameView.resumeGame();
                btnPause.setText("⏸️");
            } else {
                gameView.pauseGame();
                btnPause.setText("▶️");
            }
        });

        gameView.setGameOverListener(() -> runOnUiThread(() -> {
            startActivity(new Intent(MainActivity.this, GameOverActivity.class));
            finish();
        }));

        mediaPlayer = MediaPlayer.create(this, R.raw.tetris);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}
