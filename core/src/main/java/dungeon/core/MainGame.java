package dungeon.core;

import com.badlogic.gdx.Game;

public class MainGame extends Game {
    @Override
    public void create() {
        setScreen(new InstructionScreen(this));
    }

    public void startGame() {
        setScreen(new GameplayScreen(this));
    }
}
