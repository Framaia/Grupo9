package dungeon.core.repositories;  // Define o pacote onde os repositórios estão localizados

import dungeon.core.Room;  // Importa a classe Room
import java.util.List;  // Interface para listas
import java.util.Map;  // Interface para mapas
import java.util.HashMap;  // Implementação de mapa
import java.util.ArrayList;  // Implementação de lista

// Implementação em memória do repositório de salas
public class InMemoryRoomRepository implements RoomRepository {
    private Map<Integer, Room> rooms;  // Mapa que associa IDs às salas

    public InMemoryRoomRepository() {  // Construtor que inicializa o armazenamento
        this.rooms = new HashMap<>();  // Cria mapa vazio para armazenar salas
    }

    @Override
    public void saveRoom(Room room) {  // Armazena uma sala no mapa
        if (room != null) {  // Verifica se a sala não é nula
            rooms.put(room.getId(), room);  // Adiciona sala usando ID como chave
        }
    }

    @Override
    public Room findRoomById(int id) {  // Procura uma sala pelo ID
        return rooms.get(id);  // Retorna sala ou null se não existir
    }

    @Override
    public List<Room> findAllRooms() {  // Retorna lista com todas as salas
        return new ArrayList<>(rooms.values());  // Nova lista com todas as salas
    }

    @Override
    public void deleteRoom(int id) {  // Remove uma sala do repositório
        rooms.remove(id);  // Remove entrada do mapa
    }

    @Override
    public boolean existsRoom(int id) {  // Verifica se uma sala existe
        return rooms.containsKey(id);  // Verifica se ID existe no mapa
    }

    @Override
    public int getRoomCount() {  // Conta o número de salas armazenadas
        return rooms.size();  // Retorna tamanho do mapa
    }

    @Override
    public void clearAllRooms() {  // Remove todas as salas
        rooms.clear();  // Limpa completamente o mapa
    }
}
