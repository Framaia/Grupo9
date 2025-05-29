package dungeon.core.events;  // Define o pacote onde as classes de eventos estão localizadas

import dungeon.core.*;  // Importa as classes principais do jogo

// Observador que gere o sistema de pontuação e estatísticas do jogo
public class ScoreManager implements GameEventObserver {
    private int totalEnemiesKilled;  // Contador total de inimigos eliminados
    private int totalGoldCollected;  // Quantidade total de ouro coletado
    private int totalItemsCollected;  // Número total de itens apanhados
    private int roomsCleared;  // Número de salas totalmente limpas

    public ScoreManager() {  // Construtor que inicializa todas as estatísticas
        this.totalEnemiesKilled = 0;  // Inicia contador de inimigos em zero
        this.totalGoldCollected = 0;  // Inicia contador de ouro em zero
        this.totalItemsCollected = 0;  // Inicia contador de itens em zero
        this.roomsCleared = 0;  // Inicia contador de salas em zero
    }

    @Override
    public void onItemCollected(Item item, Player player) {  // Atualiza estatísticas quando item é coletado
        totalItemsCollected++;  // Incrementa contador de itens
        if (item.getType() == Item.ItemType.GOLD_COIN) {  // Verifica se é moeda de ouro
            totalGoldCollected += item.getType().getValue();  // Adiciona valor ao total de ouro
        }
    }

    @Override
    public void onEnemyDefeated(Enemy enemy, Player player) {  // Atualiza estatísticas quando inimigo é derrotado
        totalEnemiesKilled++;  // Incrementa contador de inimigos eliminados
        totalGoldCollected += enemy.getGoldValue();  // Adiciona ouro que o inimigo deixa cair
    }

    @Override
    public void onRoomCleared(Room room) {  // Atualiza estatísticas quando sala é limpa
        roomsCleared++;  // Incrementa contador de salas limpas
    }

    @Override
    public void onPlayerDamaged(Player player, int damage, Enemy attacker) {  // Método vazio pois não afeta pontuação
        // Este evento não afeta a pontuação diretamente
    }

    @Override
    public void onDoorUnlocked(Door door, Player player) {  // Método vazio pois não afeta pontuação
        // Este evento não afeta a pontuação diretamente
    }

    // Métodos getter para aceder às estatísticas atuais
    public int getTotalEnemiesKilled() { return totalEnemiesKilled; }  // Retorna inimigos eliminados
    public int getTotalGoldCollected() { return totalGoldCollected; }  // Retorna ouro total
    public int getTotalItemsCollected() { return totalItemsCollected; }  // Retorna itens coletados
    public int getRoomsCleared() { return roomsCleared; }  // Retorna salas limpas
}
