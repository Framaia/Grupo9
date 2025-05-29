
package dungeon.core;  // Define o pacote onde esta interface está incluída

import com.badlogic.gdx.graphics.g2d.SpriteBatch;  // Importa SpriteBatch para desenhar no ecrã

// Interface que define comportamentos comuns para todas as entidades do jogo
// Implementa o princípio do Polimorfismo através de uma interface comum
public interface GameEntity {

    void update(float deltaTime);  // Atualiza a lógica da entidade com base no tempo decorrido

    void render(SpriteBatch batch);  // Renderiza a entidade na sua posição atual

    float getX();  // Coordenada horizontal da entidade

    float getY();  // Coordenada vertical da entidade

    void dispose();// Liberta recursos gráficos e de memória

}
