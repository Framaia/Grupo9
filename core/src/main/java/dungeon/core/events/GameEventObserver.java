package dungeon.core.events;  // Define o pacote onde as classes de eventos estão localizadas

import dungeon.core.*;  // Importa todas as classes principais do jogo

// Interface que define os métodos que os observadores devem implementar para receber notificações
public interface GameEventObserver {
    void onItemCollected(Item item, Player player);  // Método chamado quando um item é coletado
    void onEnemyDefeated(Enemy enemy, Player player);  // Método chamado quando um inimigo é derrotado
    void onRoomCleared(Room room);  // Método chamado quando todos os inimigos de uma sala são eliminados
    void onPlayerDamaged(Player player, int damage, Enemy attacker);  // Método chamado quando o jogador recebe dano
    void onDoorUnlocked(Door door, Player player);  // Método chamado quando uma porta é desbloqueada
}
