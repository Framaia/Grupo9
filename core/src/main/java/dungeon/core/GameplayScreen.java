package dungeon.core;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Input;

public class GameplayScreen extends ScreenAdapter {
    private MainGame game;
    private SpriteBatch batch;
    private Texture backgroundTexture;
    private Texture playerTexture;
    private Texture enemyTexture;
    private Texture gameOverTexture;

    private float playerX, playerY;
    private float playerWidth = 128f;
    private float playerHeight = 128f;
    private float moveSpeed = 300f;

    private float enemyX, enemyY;
    private float enemySpeed = 100f;

    private int playerHealth = 100;
    private final int MAX_HEALTH = 100;

    private int enemyHealth = 50;
    private final int MAX_ENEMY_HEALTH = 50;

    private boolean isEnemyDead = false;

    private BitmapFont font;

    public GameplayScreen(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        backgroundTexture = new Texture("background.jpg");
        playerTexture = new Texture("player.png");
        enemyTexture = new Texture("enemy.png");
        gameOverTexture = new Texture("game_over.jpg");

        font = new BitmapFont();
        font.getData().setScale(2);

        playerX = (Gdx.graphics.getWidth() - playerWidth) / 2f;
        playerY = Gdx.graphics.getHeight() / 4f;

        enemyX = Gdx.graphics.getWidth();
        enemyY = Gdx.graphics.getHeight() / 2f;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        if (playerHealth <= 0) {
            gameOver();
            return;
        }

        handleInput();
        if (!isEnemyDead) moveEnemy();

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(playerTexture, playerX, playerY, playerWidth, playerHeight);
        if (!isEnemyDead) {
            batch.draw(enemyTexture, enemyX, enemyY, playerWidth, playerHeight);
        }

        drawHealthBar();
        if (!isEnemyDead) drawEnemyHealthBar();

        // Nomes
        font.setColor(Color.WHITE);
        font.draw(batch, "Herói", playerX + playerWidth / 2 - 30, playerY + playerHeight + 40);
        if (!isEnemyDead) {
            font.draw(batch, "Zumbi", enemyX + playerWidth / 2 - 30, enemyY + playerHeight + 40);
        }

        // Balões de fala
        font.setColor(Color.YELLOW);
        font.draw(batch, "Estou pronto!", playerX + playerWidth / 2 - 60, playerY + playerHeight + 80);
        if (!isEnemyDead) {
            font.draw(batch, "Cérebroooos...", enemyX + playerWidth / 2 - 70, enemyY + playerHeight + 80);
        }

        batch.end();

        checkCollision();
    }

    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            playerX -= moveSpeed * Gdx.graphics.getDeltaTime();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            playerX += moveSpeed * Gdx.graphics.getDeltaTime();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            playerY += moveSpeed * Gdx.graphics.getDeltaTime();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            playerY -= moveSpeed * Gdx.graphics.getDeltaTime();
        }

        playerX = Math.max(0, Math.min(playerX, Gdx.graphics.getWidth() - playerWidth));
        playerY = Math.max(0, Math.min(playerY, Gdx.graphics.getHeight() - playerHeight));
    }

    private void moveEnemy() {
        enemyX -= enemySpeed * Gdx.graphics.getDeltaTime();
        if (enemyX + playerWidth < 0) {
            enemyX = Gdx.graphics.getWidth();
        }
    }

    private void checkCollision() {
        if (playerX < enemyX + playerWidth && playerX + playerWidth > enemyX &&
            playerY < enemyY + playerHeight && playerY + playerHeight > enemyY) {
            if (!isEnemyDead) {
                playerHealth -= 1;
                if (playerHealth <= 0) playerHealth = 0;
            }

            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                enemyHealth -= 10;
                if (enemyHealth <= 0) {
                    isEnemyDead = true;
                }
            }
        }
    }

    private void drawHealthBar() {
        float healthBarWidth = 200f;
        float healthBarHeight = 20f;
        float healthPercentage = (float) playerHealth / MAX_HEALTH;

        batch.setColor(Color.GRAY);
        batch.draw(backgroundTexture, 10, Gdx.graphics.getHeight() - 30, healthBarWidth, healthBarHeight);

        batch.setColor(Color.GREEN);
        batch.draw(backgroundTexture, 10, Gdx.graphics.getHeight() - 30, healthBarWidth * healthPercentage, healthBarHeight);

        batch.setColor(Color.WHITE);
    }

    private void drawEnemyHealthBar() {
        float healthBarWidth = 200f;
        float healthBarHeight = 20f;
        float healthPercentage = (float) enemyHealth / MAX_ENEMY_HEALTH;

        batch.setColor(Color.GRAY);
        batch.draw(backgroundTexture, Gdx.graphics.getWidth() - 210, Gdx.graphics.getHeight() - 30, healthBarWidth, healthBarHeight);

        batch.setColor(Color.RED);
        batch.draw(backgroundTexture, Gdx.graphics.getWidth() - 210, Gdx.graphics.getHeight() - 30, healthBarWidth * healthPercentage, healthBarHeight);

        batch.setColor(Color.WHITE);
    }

    private void gameOver() {
        batch.begin();
        batch.draw(gameOverTexture, Gdx.graphics.getWidth() / 2f - gameOverTexture.getWidth() / 2f,
                Gdx.graphics.getHeight() / 2f - gameOverTexture.getHeight() / 2f);
        font.setColor(Color.RED);
        font.draw(batch, "GAME OVER", Gdx.graphics.getWidth() / 2f - 100,
                Gdx.graphics.getHeight() / 2f + gameOverTexture.getHeight() / 2f + 50);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        backgroundTexture.dispose();
        playerTexture.dispose();
        enemyTexture.dispose();
        gameOverTexture.dispose();
        font.dispose();
    }
}
