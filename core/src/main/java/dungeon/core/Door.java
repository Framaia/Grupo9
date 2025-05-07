package dungeon.core;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Door {
    // Posicionamento
    private float x, y;
    private float width = 64f;
    private float height = 128f;
    private Rectangle hitbox;

    // Visual
    private Texture texture;
    private Texture lockedTexture;

    // Propriedades
    private Room.DoorPosition position;
    private int targetRoomId; // ID da sala que esta porta leva
    private boolean isLocked;

    public Door(float x, float y, Room.DoorPosition position, int targetRoomId, boolean isLocked) {
        this.x = x;
        this.y = y;
        this.position = position;
        this.targetRoomId = targetRoomId;
        this.isLocked = isLocked;

        // Configurar textura com base na posição
        String texturePath = "door_";
        switch (position) {
            case NORTH:
                texturePath += "north.png";
                break;
            case SOUTH:
                texturePath += "south.png";
                break;
            case EAST:
                texturePath += "east.png";
                break;
            case WEST:
                texturePath += "west.png";
                break;
        }

        this.texture = new Texture(texturePath);
        this.lockedTexture = new Texture("door_locked.png");

        // Ajustar posicionamento com base na posição da porta
        switch (position) {
            case NORTH:
                this.x = x - width / 2;
                this.y = y - height / 10;
                break;
            case SOUTH:
                this.x = x - width / 2;
                this.y = y - height;
                break;
            case EAST:
                // Rotação de 90 graus, então largura e altura são trocadas
                float temp = width;
                width = height;
                height = temp;
                this.x = x - width / 10;
                this.y = y - height / 2;
                break;
            case WEST:
                // Rotação de 90 graus, então largura e altura são trocadas
                temp = width;
                width = height;
                height = temp;
                this.x = x - width;
                this.y = y - height / 2;
                break;
        }

        this.hitbox = new Rectangle(this.x, this.y, width, height);
    }

    public void render(SpriteBatch batch) {
        if (isLocked) {
            batch.draw(lockedTexture, x, y, width, height);
        } else {
            batch.draw(texture, x, y, width, height);
        }
    }

    public boolean checkCollision(Player player) {
        return hitbox.overlaps(player.getHitbox());
    }

    public void unlock() {
        isLocked = false;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public Room.DoorPosition getPosition() {
        return position;
    }

    public int getTargetRoomId() {
        return targetRoomId;
    }

    public void dispose() {
        texture.dispose();
        lockedTexture.dispose();
    }
}
