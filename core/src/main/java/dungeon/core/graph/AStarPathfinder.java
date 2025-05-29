package dungeon.core.graph;

import java.util.*;

public class AStarPathfinder {
    public static List<RoomNode> encontrarCaminho(RoomNode inicio, RoomNode objetivo) {
        Set<RoomNode> fechado = new HashSet<>();
        Set<RoomNode> aberto = new HashSet<>();
        Map<RoomNode, RoomNode> cameFrom = new HashMap<>();
        Map<RoomNode, Integer> gScore = new HashMap<>();
        Map<RoomNode, Integer> fScore = new HashMap<>();

        aberto.add(inicio);
        gScore.put(inicio, 0);
        fScore.put(inicio, heuristica(inicio, objetivo));

        while (!aberto.isEmpty()) {
            RoomNode atual = obterMenorFScore(aberto, fScore);
            if (atual.equals(objetivo)) {
                return reconstruirCaminho(cameFrom, atual);
            }

            aberto.remove(atual);
            fechado.add(atual);

            for (RoomNode vizinho : atual.getVizinhos()) {
                if (fechado.contains(vizinho)) continue;

                int tentativeGScore = gScore.getOrDefault(atual, Integer.MAX_VALUE) + 1;
                if (!aberto.contains(vizinho)) {
                    aberto.add(vizinho);
                } else if (tentativeGScore >= gScore.getOrDefault(vizinho, Integer.MAX_VALUE)) {
                    continue;
                }

                cameFrom.put(vizinho, atual);
                gScore.put(vizinho, tentativeGScore);
                fScore.put(vizinho, tentativeGScore + heuristica(vizinho, objetivo));
            }
        }

        return Collections.emptyList();
    }

    private static int heuristica(RoomNode a, RoomNode b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    }

    private static RoomNode obterMenorFScore(Set<RoomNode> aberto, Map<RoomNode, Integer> fScore) {
        RoomNode menor = null;
        int menorScore = Integer.MAX_VALUE;
        for (RoomNode node : aberto) {
            int score = fScore.getOrDefault(node, Integer.MAX_VALUE);
            if (score < menorScore) {
                menorScore = score;
                menor = node;
            }
        }
        return menor;
    }

    private static List<RoomNode> reconstruirCaminho(Map<RoomNode, RoomNode> cameFrom, RoomNode atual) {
        List<RoomNode> caminho = new ArrayList<>();
        caminho.add(atual);
        while (cameFrom.containsKey(atual)) {
            atual = cameFrom.get(atual);
            caminho.add(0, atual);
        }
        return caminho;
    }
}
