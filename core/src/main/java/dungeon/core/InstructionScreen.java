package dungeon.core;  // Define o pacote onde esta classe está incluída

import com.badlogic.gdx.Gdx; // Importa a classe Gdx, que fornece acesso a funcionalidades centrais da LibGDX
import com.badlogic.gdx.Input; // Importa a classe Input para tratar as entradas do teclado
import com.badlogic.gdx.Screen; // Importa a interface Screen que define os métodos utilizados nos vários ecrãs do jogo
import com.badlogic.gdx.graphics.Texture; // Importa a classe Texture para trabalhar com imagens
import com.badlogic.gdx.graphics.g2d.BitmapFont; // Importa a classe BitmapFont para desenhar texto no ecrã
import com.badlogic.gdx.graphics.g2d.SpriteBatch; // Importa SpriteBatch, usado para desenhar texturas de forma eficiente
import com.badlogic.gdx.graphics.g2d.GlyphLayout; // Importa GlyphLayout, usado para medir e posicionar o texto
import com.badlogic.gdx.graphics.Color; // Importa a classe Color, usada para definir cores (ex. do texto)

public class InstructionScreen implements Screen { // Define a classe InstructionScreen que implementa a interface Screen (representa um ecrã do jogo)

    private MainGame game; // Referência ao objeto principal do jogo (MainGame), para poder mudar de ecrã
    private Texture instructionImage; // Imagem de fundo com as instruções
    private SpriteBatch batch; // Objeto usado para desenhar (renderizar) as imagens no ecrã
    private BitmapFont font; // Fonte usada para mostrar o texto
    private GlyphLayout layout; // Objeto usado para medir o tamanho do texto e posicioná-lo corretamente

    public InstructionScreen(MainGame game) { // Construtor da classe que recebe o jogo principal como argumento
        this.game = game; // Guarda a referência ao jogo para ser usada mais tarde
    }

    @Override
    public void show() { // Método de chamamento automatico quando este ecrã é apresentado pela primeira vez
        instructionImage = new Texture(Gdx.files.internal("instrucoes.jpg")); // Carrega a imagem das instruções (certificação de que o ficheiro existe na pasta correta)
        batch = new SpriteBatch(); // Cria um novo SpriteBatch para desenhar imagens
        font = new BitmapFont(); // Cria uma nova fonte bitmap (por defeito)
        font.getData().setScale(2f);  // Aumenta o tamanho da fonte para que o texto seja mais visível
        font.setColor(Color.WHITE); // Define a cor do texto como branco
        layout = new GlyphLayout(); // Inicializa o objeto que calcula o tamanho do texto para poder centralizá-lo
    }

    @Override
    public void render(float delta) { // Método chamado repetidamente para desenhar o ecrã (a cada frame)
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) { // Metodo que verifica se a tecla ENTER foi pressionada
            game.setScreen(new GameplayScreen(game)); // Muda para o ecrã principal do jogo (GameplayScreen)
        }

        batch.begin(); // Metodo de inicialização do processo de desenho no ecrã
        batch.draw(instructionImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // Desenha a imagem das instruções que ocupa todo o ecrã

        // Prepara e desenha o texto no centro
        String message = "Press ENTER to start the game"; // Define a mensagem a mostrar no ecrã
        layout.setText(font, message); // Mede o tamanho da mensagem com a fonte atual
        float x = (Gdx.graphics.getWidth() - layout.width) / 2; // Calcula a posição X para centrar o texto horizontalmente
        float y = (Gdx.graphics.getHeight() - layout.height) / 2; // Calcula a posição Y para centrar o texto verticalmente
        font.draw(batch, layout, x, y); // Desenha o texto no ecrã, centrado

        batch.end(); // Termina o processo de desenho
    }

    @Override
    public void resize(int width, int height) {} // Método chamado se a janela mudar de tamanho (não é usado aqui)
    @Override
    public void pause() {} // Método chamado quando faz pausa no jogo (não é usado aqui)
    @Override
    public void resume() {} // Método chamado quando se retoma o jogo (não é usado aqui)
    @Override
    public void hide() {} // Método chamado quando o ecrã deixa de estar visível (não é usado aqui)
    @Override
    public void dispose() { // Método chamado para limpar/libertar os recursos quando o ecrã já não é necessário
        batch.dispose(); // Limpa ou liberta os recursos usados para desenhar
        instructionImage.dispose(); // Limpa a imagem das instruções da memória
        font.dispose();  // Limpa/liberta a fonte da memória
    }
}
