package dungeon.core.events;  // Define o pacote onde as classes de eventos estão localizadas

import dungeon.core.*;  // Importa as classes principais do jogo
import java.util.ArrayList;  // Importa ArrayList para armazenar observadores
import java.util.List;  // Importa interface List

// Classe que mantém uma lista de observadores e os notifica quando eventos acontecem
public class GameEventManager {
    private static GameEventManager instance;  // Instância única (padrão Singleton)
    private List<GameEventObserver> observers;  // Lista que armazena todos os observadores registados

    private GameEventManager() {  // Construtor privado para implementar Singleton
        this.observers = new ArrayList<>();  // Inicializa lista de observadores vazia
    }

    public static GameEventManager getInstance() {  // Método que garante apenas uma instância
        if (instance == null) {  // Primeira vez que é chamado
            instance = new GameEventManager();  // Cria a instância única
        }
        return instance;  // Retorna a instância única
    }

    public void addObserver(GameEventObserver observer) {  // Adiciona observador à lista
        if (!observers.contains(observer)) {  // Evita duplicados
            observers.add(observer);  // Adiciona à lista
        }
    }

    public void removeObserver(GameEventObserver observer) {  // Remove observador da lista
        observers.remove(observer);  // Remove da lista
    }

    public void notifyItemCollected(Item item, Player player) {  // Notifica todos quando item é coletado
        for (GameEventObserver observer : observers) {  // Percorre todos os observadores
            observer.onItemCollected(item, player);  // Chama método do observador
        }
    }

    public void notifyEnemyDefeated(Enemy enemy, Player player) {  // Notifica quando inimigo é derrotado
        for (GameEventObserver observer : observers) {  // Percorre observadores
            observer.onEnemyDefeated(enemy, player);  // Executa callback
        }
    }

    public void notifyRoomCleared(Room room) {  // Notifica quando sala é limpa
        for (GameEventObserver observer : observers) {  // Percorre observadores
            observer.onRoomCleared(room);  // Invoca método de sala limpa
        }
    }

    public void notifyPlayerDamaged(Player player, int damage, Enemy attacker) {  // Notifica dano ao jogador
        for (GameEventObserver observer : observers) {  // Itera observadores
            observer.onPlayerDamaged(player, damage, attacker);  // Chama método de dano
        }
    }

    public void notifyDoorUnlocked(Door door, Player player) {  // Notifica porta desbloqueada
        for (GameEventObserver observer : observers) {  // Percorre observadores
            observer.onDoorUnlocked(door, player);  // Executa callback de porta
        }
    }
}
