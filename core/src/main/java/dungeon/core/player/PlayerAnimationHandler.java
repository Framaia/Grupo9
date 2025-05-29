package dungeon.core.player;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import dungeon.core.Player;

public class PlayerAnimationHandler {

    private final Player player;
    private final Texture normalTexture;
    private final Texture combatTexture;

    public PlayerAnimationHandler(Player player, Texture normalTexture, Texture combatTexture) {
        this.player = player;
        this.normalTexture = normalTexture;
        this.combatTexture = combatTexture;
    }

    public void render(SpriteBatch batch) {
        if (player.getCombat().isAttacking()) {
            batch.draw(combatTexture, player.getX(), player.getY());
        } else {
            batch.draw(normalTexture, player.getX(), player.getY());
        }
    }
}
