package dungeon.core;  // Define o pacote onde esta interface está incluída

// Interface que define como os itens aplicam efeitos aos jogadores
// Implementa o princípio da Abstração separando lógica de efeitos da lógica de itens
public interface ItemEffect {

    void applyEffect(Player player);  // Aplica o efeito específico do item ao jogador

    String getEffectDescription();  // Devolve descrição textual do efeito para mostrar ao utilizador
}




