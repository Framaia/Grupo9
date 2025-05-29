package dungeon.core.factories;  // Define o pacote onde as factories estão localizadas

import dungeon.core.*;  // Importa as classes principais do jogo
import dungeon.core.ai.*;  // Importa as estratégias de IA
import java.util.Random;  // Para geração de elementos aleatórios

// Classe abstrata que define o padrão Factory para criação de salas
public abstract class RoomFactory {
    protected Random random;  // Gerador de números aleatórios para variação nas salas
    protected static final int SCREEN_WIDTH = 800;  // Largura padrão da tela
    protected static final int SCREEN_HEIGHT = 600;  // Altura padrão da tela

    public RoomFactory() {  // Construtor que inicializa o gerador aleatório
        this.random = new Random();  // Cria novo gerador de números aleatórios
    }

    // Método template que define o processo de criação de uma sala
    public final Room createRoom(int id) {  // Cria uma sala completa usando Template Method
        Room room = buildRoom(id);  // Cria a estrutura básica da sala
        populateWithEnemies(room);  // Adiciona inimigos específicos do tipo de sala
        populateWithItems(room);  // Adiciona itens específicos do tipo de sala
        addDoors(room);  // Adiciona portas de entrada e saída
        return room;  // Retorna a sala totalmente configurada
    }

    // Métodos abstratos que devem ser implementados pelas subclasses
    protected abstract Room buildRoom(int id);  // Cria a estrutura básica da sala
    protected abstract void populateWithEnemies(Room room);  // Define quais inimigos adicionar
    protected abstract void populateWithItems(Room room);  // Define quais itens adicionar
    protected abstract void addDoors(Room room);  // Define quais portas adicionar
}
