package dungeon.core;  // Define o pacote onde esta classe está incluída

import com.badlogic.gdx.graphics.Texture;  // Importa a classe Texture da libGDX para carregar e gerir as imagens
import com.badlogic.gdx.graphics.g2d.SpriteBatch;  // Importa a classe SpriteBatch para desenhar as texturas no ecrã
import com.badlogic.gdx.math.Rectangle;  // Importa a classe Rectangle para criar hitboxes e detetar colisões

public class Door implements GameEntity {
    // Posição da porta no ecrã
    private float x, y;  // Coordenadas da porta no mundo do jogo
    private float width = 64f;  // Largura padrão da porta em pixels
    private float height = 128f;  // Altura padrão da porta em pixels
    private Rectangle hitbox;  // Área para detetar colisões com o jogador

    // Texturas da porta (normal e trancada)
    private Texture texture;  // Textura para a porta no estado normal (desbloqueada)
    private Texture lockedTexture;  // Textura para a porta no estado trancado

    // Características da porta
    private Room.DoorPosition position;  // Norte, Sul, Este, Oeste - posição/orientação da porta
    private int targetRoomId;  // Sala onde a porta dá acesso - ID da sala de destino
    private boolean isLocked;  // Indica se a porta está trancada ou não

    public Door(float x, float y, Room.DoorPosition position, int targetRoomId, boolean isLocked) {  // Construtor que configura a porta com as suas propriedades
        this.x = x;  // Guarda a coordenada X inicial
        this.y = y;  // Guarda a coordenada Y inicial
        this.position = position;  // Define a posição/orientação da porta
        this.targetRoomId = targetRoomId;  // Define a sala para onde a porta dá acesso
        this.isLocked = isLocked;  // Define se a porta está trancada inicialmente

        // Escolhe a textura com base na direção da porta
        String texturePath = "door_";  // Nome base do ficheiro de textura
        switch (position) {  // Verifica a orientação da porta para escolher a textura correta
            case NORTH:  // Se for uma porta na parede norte
                texturePath += "north.png";  // Usa a textura de porta virada para norte
                break;
            case SOUTH:  // Se for uma porta na parede sul
                texturePath += "south.png";  // Usa a textura de porta virada para sul
                break;
            case EAST:  // Se for uma porta na parede este
                texturePath += "east.png";  // Usa a textura de porta virada para este
                break;
            case WEST:  // Se for uma porta na parede oeste
                texturePath += "west.png";  // Usa a textura de porta virada para oeste
                break;
        }

        // Carrega as texturas
        this.texture = new Texture(texturePath);  // Carrega a textura padrão da porta
        this.lockedTexture = new Texture("door_locked.png");  // Carrega a textura da porta trancada

        // Ajusta a posição para que a porta fique bem alinhada com a parede
        switch (position) {  // Ajusta a posição final com base na orientação
            case NORTH:
                // Porta em cima - centrada horizontalmente e ligeiramente abaixo do topo
                this.x = x - width / 2;  // Centra horizontalmente
                this.y = y - height / 10;  // Posiciona um pouco abaixo do topo
                break;
            case SOUTH:
                // Porta em baixo - centrada horizontalmente e alinhada com a parte inferior
                this.x = x - width / 2;  // Centra horizontalmente
                this.y = y - height;  // Alinha com a parte inferior
                break;
            case EAST:
                /*
                Porta à direita - O valor das variáveis largura e altura é trocado (relativamente às anteriores)
                uma vez que a porta está de lado
                */
                float temp = width;  // Guarda o valor original da largura
                width = height;  // A largura passa a ser a altura original
                height = temp;  // A altura passa a ser a largura original
                this.x = x - width / 10;  // Posiciona um pouco à esquerda do limite direito
                this.y = y - height / 2;  // Centra verticalmente
                break;
            case WEST:
                // Porta à esquerda - também trocamos largura e altura como na anterior (EAST/direita)
                temp = width;  // Guarda o valor original da largura
                width = height;  // A largura passa a ser a altura original
                height = temp;  // A altura passa a ser a largura original
                this.x = x - width;  // Alinha com o limite esquerdo
                this.y = y - height / 2;  // Centra verticalmente
                break;
        }

        // Cria a hitbox para detetar colisões com a porta
        this.hitbox = new Rectangle(this.x, this.y, width, height);  // Define a área de colisão da porta
    }

    public void render(SpriteBatch batch) {  // Método chamado para desenhar a porta no ecrã
        if (isLocked) {  // Se a porta estiver trancada
            batch.draw(lockedTexture, x, y, width, height);  // Desenha a textura de porta trancada
        } else {  // Se a porta estiver desbloqueada
            batch.draw(texture, x, y, width, height);  // Desenha a textura normal da porta
        }
    }

    public boolean checkCollision(Player player) {  // Método que verifica se o jogador está a tocar na porta
        return hitbox.overlaps(player.getHitbox());  // Compara a hitbox da porta com a hitbox do jogador
    }

    public void unlock() {  // Método para desbloquear a porta quando o jogador usa uma chave
        isLocked = false;  // Define o estado da porta como desbloqueada
    }

    public boolean isLocked() {  // Método que verifica se a porta está trancada
        return isLocked;  // Devolve verdadeiro se a porta estiver trancada, falso caso contrário
    }

    public Room.DoorPosition getPosition() {  // Método que devolve a posição/orientação da porta
        return position;  // Devolve a orientação atual (NORTH, SOUTH, EAST ou WEST)
    }

    public int getTargetRoomId() {  // Método que devolve o ID da sala para onde a porta leva
        return targetRoomId;  // Devolve o número identificador da sala de destino
    }

    public void dispose() {  // Método para libertar os recursos gráficos usados pela porta
        texture.dispose();  // Liberta a memória usada pela textura normal
        lockedTexture.dispose();  // Liberta a memória usada pela textura de porta trancada
    }

    @Override
    public void update(float deltaTime) {
        // As portas são entidades estáticas que não requerem atualização de estado
    }

    public float getX() {  // Método que devolve a coordenada X da porta
        return x;  // Devolve a posição horizontal atual
    }

    public float getY() {  // Método que devolve a coordenada Y da porta
        return y;  // Devolve a posição vertical atual
    }
}
