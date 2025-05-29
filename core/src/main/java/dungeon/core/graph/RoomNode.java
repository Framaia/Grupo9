package dungeon.core.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Classe que representa uma sala do dungeon como um nó do grafo.
 */
public class RoomNode {

    private final String id;
    private final int x;
    private final int y;
    private final List<RoomNode> vizinhos;

    /**
     * Construtor do nó da sala
     * @param id identificador único da sala
     * @param x coordenada X
     * @param y coordenada Y
     */
    public RoomNode(String id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.vizinhos = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public List<RoomNode> getVizinhos() {
        return vizinhos;
    }

    /**
     * Adiciona uma ligação entre esta sala e outra.
     */
    public void adicionarVizinho(RoomNode outro) {
        if (!vizinhos.contains(outro)) {
            vizinhos.add(outro);
            outro.vizinhos.add(this); // ligação bidirecional
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomNode roomNode = (RoomNode) o;
        return Objects.equals(id, roomNode.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Sala(" + id + ", x=" + x + ", y=" + y + ")";
    }
}
