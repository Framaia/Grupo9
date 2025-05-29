package dungeon.core.ai;  // Define o pacote onde as estratégias de IA estão localizadas

import com.badlogic.gdx.math.Vector2;  // Importa Vector2 para cálculos de distância
import dungeon.core.Enemy;  // Importa a classe Enemy
import dungeon.core.Player;  // Importa a classe Player

// Estratégia que faz o inimigo seguir directamente o jogador
public class BasicFollowerStrategy implements EnemyStrategy {
    private static final float FOLLOW_RANGE = 300f;  // Distância máxima para começar a seguir o jogador

    @Override
    public void init(Enemy enemy) {  // Método chamado quando a estratégia é atribuída ao inimigo
        // Esta estratégia não necessita de inicialização especial
    }

    @Override
    public void update(Enemy enemy, float deltaTime, Player player) {  // Actualiza o comportamento a cada frame
        // Calcula a distância entre o inimigo e o jogador
        float distance = Vector2.dst(enemy.getX(), enemy.getY(), player.getX(), player.getY());

        if (distance <= FOLLOW_RANGE) {  // Se o jogador estiver dentro do alcance de detecção
            moveTowardsPlayer(enemy, player, deltaTime);  // Move o inimigo em direcção ao jogador
        }
    }

    private void moveTowardsPlayer(Enemy enemy, Player player, float deltaTime) {  // Move o inimigo na direcção do jogador
        // Calcula o vector direccional do inimigo para o jogador
        float directionX = player.getX() - enemy.getX();  // Diferença nas coordenadas X
        float directionY = player.getY() - enemy.getY();  // Diferença nas coordenadas Y
        float length = (float) Math.sqrt(directionX * directionX + directionY * directionY);  // Comprimento do vector

        if (length > 0) {  // Evita divisão por zero se as posições forem idênticas
            directionX /= length;  // Normaliza a componente X do vector (valor entre -1 e 1)
            directionY /= length;  // Normaliza a componente Y do vector (valor entre -1 e 1)
        }

        // Calcula a nova posição do inimigo baseada na velocidade e tempo decorrido
        float newX = enemy.getX() + directionX * enemy.getMoveSpeed() * deltaTime;
        float newY = enemy.getY() + directionY * enemy.getMoveSpeed() * deltaTime;

        // Aplica limites da tela para manter o inimigo visível
        newX = Math.max(0, Math.min(newX, 800 - enemy.getWidth()));  // Limita movimento horizontal
        newY = Math.max(0, Math.min(newY, 600 - enemy.getHeight()));  // Limita movimento vertical

        // Define a nova posição do inimigo
        enemy.setPosition(newX, newY);  // Actualiza as coordenadas do inimigo
    }
}
