package dungeon.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.Color;

public class InstructionScreen implements Screen {

    private MainGame game;
    private Texture instructionImage;
    private SpriteBatch batch;
    private BitmapFont font;
    private GlyphLayout layout;

    public InstructionScreen(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        instructionImage = new Texture(Gdx.files.internal("instrucoes.jpg")); // Certifica-te que o nome está certo
        batch = new SpriteBatch();
        font = new BitmapFont(); // Usa a fonte padrão, ou carrega a tua .fnt
        font.getData().setScale(2f); // Aumenta o tamanho do texto
        font.setColor(Color.WHITE); // Cor do texto
        layout = new GlyphLayout();
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(new GameplayScreen(game));
        }

        batch.begin();
        batch.draw(instructionImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Prepara e desenha o texto no centro
        String message = "Press ENTER to start the game";
        layout.setText(font, message);
        float x = (Gdx.graphics.getWidth() - layout.width) / 2;
        float y = (Gdx.graphics.getHeight() - layout.height) / 2;
        font.draw(batch, layout, x, y);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {}
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
    @Override
    public void dispose() {
        batch.dispose();
        instructionImage.dispose();
        font.dispose();
    }
}
