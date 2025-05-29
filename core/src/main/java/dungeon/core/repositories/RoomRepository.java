package dungeon.core.repositories;  // Define o pacote onde os repositórios estão localizados

import dungeon.core.Room;  // Importa a classe Room
import java.util.List;  // Interface para listas

// Interface que define operações de repositório para salas
public interface RoomRepository {
    void saveRoom(Room room);  // Armazena uma sala no repositório
    Room findRoomById(int id);  // Encontra uma sala pelo seu ID
    List<Room> findAllRooms();  // Retorna todas as salas armazenadas
    void deleteRoom(int id);  // Remove uma sala do repositório
    boolean existsRoom(int id);  // Verifica se uma sala existe
    int getRoomCount();  // Retorna o número total de salas
    void clearAllRooms();  // Remove todas as salas do repositório
}
