package dungeon.core.ai;  // Define o pacote onde as estratégias de IA estão localizadas

import dungeon.core.Enemy;  // Importa a classe Enemy
import dungeon.core.Player;  // Importa a classe Player

// Interface que define como os inimigos se devem comportar
public interface EnemyStrategy {
    void update(Enemy enemy, float deltaTime, Player player);  // Actualiza o comportamento do inimigo
    void init(Enemy enemy);  // Inicializa a estratégia quando atribuída ao inimigo
}
