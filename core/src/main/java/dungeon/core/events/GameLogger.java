package dungeon.core.events;  // Define o pacote onde as classes de eventos estão localizadas

import dungeon.core.*;  // Importa as classes principais do jogo

// Implementação concreta de observador que faz log dos eventos para debug
public class GameLogger implements GameEventObserver {
    private boolean debugMode;  // Flag que controla se as mensagens de debug devem ser exibidas

    public GameLogger(boolean debugMode) {  // Construtor que define se o modo debug está ativo
        this.debugMode = debugMode;  // Armazena o estado do modo debug
    }

    @Override
    public void onItemCollected(Item item, Player player) {  // Implementação do callback de item coletado
        if (debugMode) {  // Verifica se o modo debug está ativo
            System.out.println("[LOG] Jogador coletou: " + item.getType().getName());  // Imprime informação sobre o item
        }
    }

    @Override
    public void onEnemyDefeated(Enemy enemy, Player player) {  // Implementação do callback de inimigo derrotado
        if (debugMode) {  // Só imprime se o debug estiver ligado
            System.out.println("[LOG] Inimigo derrotado. Jogador ganhou " + enemy.getGoldValue() + " moedas");  // Mostra informação sobre a vitória
        }
    }

    @Override
    public void onRoomCleared(Room room) {  // Implementação do callback de sala limpa
        if (debugMode) {  // Verifica o estado do modo debug
            System.out.println("[LOG] Sala " + room.getId() + " foi limpa de todos os inimigos");  // Informa que a sala foi limpa
        }
    }

    @Override
    public void onPlayerDamaged(Player player, int damage, Enemy attacker) {  // Implementação do callback de dano
        if (debugMode) {  // Só executa se o debug estiver ativo
            System.out.println("[LOG] Jogador recebeu " + damage + " de dano. Vida restante: " + player.getHealth());  // Mostra dano e vida
        }
    }

    @Override
    public void onDoorUnlocked(Door door, Player player) {  // Implementação do callback de porta desbloqueada
        if (debugMode) {  // Verifica se deve imprimir mensagens
            System.out.println("[LOG] Porta " + door.getPosition() + " foi desbloqueada");  // Informa qual porta foi aberta
        }
    }
}
