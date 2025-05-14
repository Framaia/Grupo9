package dungeon.core;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/*
  Classe Door - Portas que ligam as salas do jogo.
  Permitem ao jogador navegar entre salas diferentes e podem estar trancadas.
 */
public class Door {
    // Posição da porta no ecrã
    private float x, y;
    private float width = 64f;
    private float height = 128f;
    private Rectangle hitbox;  // Área para detetar colisões com o jogador

    // Texturas da porta (normal e trancada)
    private Texture texture;
    private Texture lockedTexture;

    // Características da porta
    private Room.DoorPosition position;  // Norte, Sul, Este, Oeste
    private int targetRoomId;            // Sala aonde a porta dá acesso
    private boolean isLocked;            // Indica se a porta está trancada ou não

    /*
      Construtor da classe Door - Apresenta uma porta com todos os atributos necessários, carrega as texturas
      apropriadas baseadas na posição e ajusta o posicionamento e dimensões da porta de acordo com a sua orientação
      (portas norte/sul ou este/oeste).
      Recebe como parâmetros as coordenadas iniciais (x,y), a posição/orientação da porta, o ID da sala de destino
      e uma flag que valida se a porta está inicialmente trancada ou não.
     */
    public Door(float x, float y, Room.DoorPosition position, int targetRoomId, boolean isLocked) {
        this.x = x;
        this.y = y;
        this.position = position;
        this.targetRoomId = targetRoomId;
        this.isLocked = isLocked;

        // Escolhe a textura com base na direção da porta
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

        // Carrega as texturas
        this.texture = new Texture(texturePath);
        this.lockedTexture = new Texture("door_locked.png");

        // Ajusta a posição para que a porta fique bem alinhada com a parede
        switch (position) {
            case NORTH:
                // Porta em cima
                this.x = x - width / 2;
                this.y = y - height / 10;
                break;
            case SOUTH:
                // Porta em baixo
                this.x = x - width / 2;
                this.y = y - height;
                break;
            case EAST:
                /*
                Porta à direita - O valor das variáveis largura e altura é trocado (relativamente às anteriores)
                uma vez que a porta está de lado
                */
                float temp = width;
                width = height;
                height = temp;
                this.x = x - width / 10;
                this.y = y - height / 2;
                break;
            case WEST:
                // Porta à esquerda - também trocamos largura e altura
                temp = width;
                width = height;
                height = temp;
                this.x = x - width;
                this.y = y - height / 2;
                break;
        }

        // Cria a hitbox para detetar colisões com a porta
        this.hitbox = new Rectangle(this.x, this.y, width, height);
    }

    /*
      Desenha a porta no ecrã.
      Usa a textura normal ou de porta trancada dependendo do estado.
     */
    public void render(SpriteBatch batch) {
        if (isLocked) {
            batch.draw(lockedTexture, x, y, width, height);
        } else {
            batch.draw(texture, x, y, width, height);
        }
    }

    /*
      Verifica se o jogador está a tocar na porta.
      Isto permite saber quando o jogador pode interagir com a porta.
     */
    public boolean checkCollision(Player player) {
        return hitbox.overlaps(player.getHitbox());
    }

    /*
      Desbloqueia a porta.
      É "chamado" quando o jogador tem em sua posse uma chave.
     */
    public void unlock() {
        isLocked = false;
    }

    /*
      Verifica se a porta está trancada.
     */
    public boolean isLocked() {
        return isLocked;
    }

    /*
      Devolve a posição da porta (Norte, Sul, Este, Oeste).
     */
    public Room.DoorPosition getPosition() {
        return position;
    }

    /*
      Devolve o número identificador (ID) da sala de destino desta porta.
      É usado para determinar o destino do jogador quando utiliza esta porta.
     */
    public int getTargetRoomId() {
        return targetRoomId;
    }

    /*
      Gestão dos recursos gráficos usados pela porta.
      Importante chamar quando já não precisamos da porta, para evitar memory leaks.
     */
    public void dispose() {
        texture.dispose();
        lockedTexture.dispose();
    }
}
