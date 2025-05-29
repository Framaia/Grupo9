package dungeon.core.player;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Gdx;
import dungeon.core.Player;

public class PlayerController {

    private final Player player;

    public PlayerController(Player player) {
        this.player = player;
    }

    public void processInput(float deltaTime) {
        float moveSpeed = player.getSpeed();

        float dx = 0;
        float dy = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) dy += moveSpeed * deltaTime;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) dy -= moveSpeed * deltaTime;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) dx -= moveSpeed * deltaTime;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) dx += moveSpeed * deltaTime;

        player.setY(player.getY() + dy);
        player.setX(player.getX() + dx);
    }
}
