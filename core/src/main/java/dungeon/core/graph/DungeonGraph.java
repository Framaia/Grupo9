package dungeon.core.graph;

import java.util.*;

public class DungeonGraph {
    private final Map<String, RoomNode> salas = new HashMap<>();

    public void adicionarSala(RoomNode sala) {
        salas.put(sala.getId(), sala);
    }

    public RoomNode obterSala(String id) {
        return salas.get(id);
    }

    public Collection<RoomNode> obterTodasAsSalas() {
        return salas.values();
    }
}
