package dungeon.core;  // Define o pacote onde esta classe está incluída

import com.badlogic.gdx.graphics.Texture;  // Importa a classe Texture para carregar as imagens dos itens
import com.badlogic.gdx.graphics.g2d.SpriteBatch;  // Importa a classe SpriteBatch para desenhar os itens no ecrã
import com.badlogic.gdx.math.Rectangle;  // Importa a classe Rectangle para criar hitboxes e detetar colisões

public class Item implements GameEntity {
    // Tipos de itens disponíveis no jogo
    public enum ItemType {  // Enumeração que define os diferentes tipos de itens e as suas características
        HEALTH_POTION("health_potion.png", "Poção de Vida", 20, "Restaura 20 pontos de vida"),  // Poção que recupera pontos de vida ao jogador
        DAMAGE_BOOST("damage_boost.png", "Amuleto de Força", 5, "Aumenta o teu dano em 5 pontos"),  // Item que aumenta o dano do jogador
        KEY("key.png", "Chave", 0, "Abre portas trancadas"),  // Chave para abrir portas bloqueadas
        GOLD_COIN("gold_coin.png", "Moeda de Ouro", 10, "10 moedas de ouro");  // Moeda que aumenta a riqueza do jogador

        private final String texturePath;  // Caminho para o ficheiro de imagem do item
        private final String name;  // Nome do item que aparece para o jogador
        private final int value;  // Valor numérico do item (pontos de vida, dano, etc.)
        private final String description;  // Descrição do efeito do item

        ItemType(String texturePath, String name, int value, String description) {  // Construtor do enumerador para definir as propriedades de cada tipo
            this.texturePath = texturePath;  // Define o caminho da imagem
            this.name = name;  // Define o nome do item
            this.value = value;  // Define o valor do item
            this.description = description;  // Define a descrição do item
        }

        public String getTexturePath() {  // Devolve o caminho do ficheiro de imagem
            return texturePath;  // O caminho para o ficheiro de imagem
        }

        public String getName() {  // Devolve o nome do item
            return name;  // O nome do item como string
        }

        public int getValue() {  // Devolve o valor numérico do item
            return value;  // O valor numérico associado ao item
        }

        public String getDescription() {  // Devolve a descrição do item
            return description;  // A descrição textual do item
        }
    }

    // Componente de posição - localização do item no mundo do jogo
    private float x, y;  // Coordenadas X e Y do item
    private float width = 32f;  // Largura do item em pixels
    private float height = 32f;  // Altura do item em pixels
    private Rectangle hitbox;  // Área para detetar colisões com o jogador

    // Componente visual - aparência do item
    private Texture texture;  // Imagem que representa o item no ecrã

    // Componente de propriedades do item
    private ItemType type;  // Tipo do item (poção, chave, etc.)
    private boolean isCollected;  // Indica se o item já foi apanhado pelo jogador

    // Componente de animação - efeitos visuais do item
    private float bobHeight = 10f;  // Altura máxima do movimento de flutuação
    private float bobSpeed = 2f;  // Velocidade da animação de flutuação
    private float bobTimer = 0f;  // Contador de tempo para a animação
    private float originalY;  // Posição Y original para calcular a flutuação

    public Item(float x, float y, ItemType type) {  // Construtor que inicializa um item com um tipo específico
        this.x = x;  // Define a posição X inicial
        this.y = y;  // Define a posição Y inicial
        this.originalY = y;  // Guarda a posição Y original para a animação
        this.type = type;  // Define o tipo do item
        this.texture = new Texture(type.getTexturePath());  // Carrega a textura baseada no tipo
        this.isCollected = false;  // O valor 'false' indica que o item ainda está disponível no jogo
        this.hitbox = new Rectangle(x, y, width, height);  // Cria a área de colisão
    }

    public void update(float deltaTime) {  // Método chamado a cada frame para atualizar o estado do item
        if (!isCollected) {  // Só atualiza se o item ainda não foi apanhado
            // Animação simples de flutuação
            bobTimer += deltaTime;  // Incrementa o temporizador da animação
            y = originalY + (float) Math.sin(bobTimer * bobSpeed) * bobHeight;  // Calcula a nova posição Y com efeito de onda

            // Atualiza a hitbox para coincidir com a nova posição
            hitbox.set(x, y, width, height);  // Sincroniza a hitbox com a posição atual
        }
    }

    public void render(SpriteBatch batch) {  // Método que desenha o item no ecrã
        if (!isCollected) {  // Só desenha se o item ainda não foi apanhado
            batch.draw(texture, x, y, width, height);  // Desenha a textura do item
        }
    }

    public boolean checkCollision(Player player) {  // Verifica se o jogador está a tocar no item
        if (isCollected) {  // Caso o item já tenha sido apanhado, não há colisão
            return false;  // Não há colisão possível com itens já apanhados
        }

        return hitbox.overlaps(player.getHitbox());  // Resultado da verificação de sobreposição das áreas de colisão
    }

    public void collect() {  // Marca o item como apanhado
        isCollected = true;  // Define o estado do item como apanhado
    }

    public boolean isCollected() {  // Verifica se o item já foi apanhado pelo jogador
        return isCollected;  // Estado atual do item (true = apanhado, false = disponível)
    }

    public ItemType getType() {  // Devolve o tipo do item (poção, chave, etc.)
        return type;  // O tipo específico deste item
    }

    public float getX() {  // Devolve a coordenada X do item
        return x;  // Valor atual da coordenada X
    }

    public float getY() {  // Devolve a coordenada Y do item
        return y;  // Valor atual da coordenada Y
    }

    public float getWidth() {  // Devolve a largura do item
        return width;  // Valor da largura em pixels
    }

    public float getHeight() {  // Devolve a altura do item
        return height;  // Valor da altura em pixels
    }

    public void dispose() {  // Liberta os recursos gráficos usados pelo item
        texture.dispose();  // Liberta a memória usada pela textura
    }
}
