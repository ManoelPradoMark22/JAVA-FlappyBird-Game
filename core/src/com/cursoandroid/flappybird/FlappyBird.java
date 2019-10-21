package com.cursoandroid.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

import javax.naming.Context;

public class
FlappyBird extends ApplicationAdapter{

	//private int contador=0;

	//usada para criar animações
	private  SpriteBatch batch;
	private Texture[] passaros;
	private Texture fundo;
	private Texture canoBaixo;
	private Texture canoTopo;
	private Texture gameOver;
	private Random numeroRandomico;
	private BitmapFont fonte;
	private BitmapFont mensagem;
	private Circle passaroCirculo;  //circle da BadLogic.gdx!!
    private Rectangle retangulocanoTopo;
    private Rectangle retangulocanoBaixo;
    //private ShapeRenderer shape; //funciona da mesma forma que o batch (para desenhar na tela as formas) nao precisamos mostrar na tela

	//Atributos de Configuração
	//private int movimento=0;
	private float larguraDoDispositivo;
	private float alturaDoDispositivo;
	private int estadoJogo =0; //0-> jogo nao iniciado. 1-> jogo iniciado 2-> game over
	private int pontuacao=0;

	private float variacao=0;
	private float velocidadeQueda=0;
	private float posicaoInicialVertical;
	private float posicaoMovimentoCanoHorizontal;
	private float espacoEntreCanos;
	private float deltaTime;
	private float alturaEntreCanosRandomica;
	private boolean marcouPonto=false; //por padrao já é false

	//câmera
	private OrthographicCamera camera;
	private Viewport viewport; //devemos fazer um ajste no viewport com um valor fixo para aumentarmos proporcionalmente
	private final float VIRTUAL_WIDTH = 768 ; //pode usar tb 600
	private final float VIRTUAL_HEIGHT = 1024; // pode uar tb 800   (as que usamos sao mais adequadas para os tamanhos das imagens do jogo)

	@Override
	public void create () {
		//Gdx.app.log("Create", "Inicializado o jogo");
		marcouPonto = false;
		pontuacao = 0;
		//agora que instanciamos a classe SpriteBatch, ela é usada para manipular nossas texturas (imagens)
		batch = new SpriteBatch();
		numeroRandomico = new Random();
		passaroCirculo = new Circle();
		//retangulocanoBaixo = new Rectangle();
		//retangulocanoTopo = new Rectangle();  já instanciamos os shapes dos retangulos la embaixo
		//shape = new ShapeRenderer(); nao precisaremos mostrar o shape na tela
		fonte = new BitmapFont();
		fonte.setColor(Color.WHITE);
		fonte.getData().setScale(6);

        mensagem = new BitmapFont();
        mensagem.setColor(Color.WHITE);
        mensagem.getData().setScale(3);

		passaros = new Texture[3];
		passaros[0] = new Texture("passaro1.png");
		passaros[1] = new Texture("passaro2.png");
		passaros[2] = new Texture("passaro3.png");
		fundo = new Texture("fundo.png");
		canoBaixo = new Texture("cano_baixo.png");
		canoTopo = new Texture("cano_topo.png");
		gameOver = new Texture("game_over.png");

		//configurações da câmera
		camera = new OrthographicCamera();
		camera.position.set(VIRTUAL_WIDTH/2, VIRTUAL_HEIGHT/2, 0); //posicionar a camera //tres valores para posicionamento da camera x,y,z (em 2D so o x e y)
        //viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera); //largura, altura e a camera usada
        //ou
        viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera); //largura, altura e a camera usada



		//largura real do aparelho
		/*larguraDoDispositivo=Gdx.graphics.getWidth();
		alturaDoDispositivo=Gdx.graphics.getHeight();*/

		//largura virtual (se ajusta a todos os aparelhos
		larguraDoDispositivo=VIRTUAL_WIDTH;
		alturaDoDispositivo=VIRTUAL_HEIGHT;

		posicaoInicialVertical = alturaDoDispositivo/2;
		posicaoMovimentoCanoHorizontal = larguraDoDispositivo;
		espacoEntreCanos = 300;
	}

	//chamado de tempos em tempos para alterar a exibicao do  jogo
	//posso criar as animacoes
	@Override
	public void render () {
	    camera.update();
	    //limpar os frames anteriores para usar menos memória
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		deltaTime = Gdx.graphics.getDeltaTime();
		//contador++;
		//Gdx.app.log("Render", "Renderizando o jogo: " + contador );
		//movimento ++;
		//variacao recebe a propria variacao + 0.1 (para reduzir a velocidade)
		//variacao += variacao + 0.1;
		//getDeltaTime calcula a diferenca entre uma execucao do render e outra. (valor mto pequeno)
		//variacao+= variacao+0.2;
		//Gdx.app.log("Variacao", "Variacao: " + Gdx.graphics.getDeltaTime());
		//OU
		variacao += (deltaTime) * 10; //vezes 10 pra nao ficar mto lento
		//troca de imagens dos passaros
		if (variacao > 2) {
			variacao = 0;
		}

		if (estadoJogo==0){ //nao iniciado
			if(Gdx.input.justTouched()){
				estadoJogo=1;
			}
		}else {

			velocidadeQueda++;
			//queda do passaro
			if (posicaoInicialVertical > 0 || velocidadeQueda < 0) {
				posicaoInicialVertical = posicaoInicialVertical - velocidadeQueda;
			}

			if (estadoJogo==1){//jogo iniciado

				posicaoMovimentoCanoHorizontal -= deltaTime * 200;

				//verificar se a tela foi tocada
				if (Gdx.input.justTouched()) {
					//Gdx.app.log("Toque", "Toque na tela");
					velocidadeQueda = -15;
				}

				//loop dos canos
				if (posicaoMovimentoCanoHorizontal < -canoTopo.getWidth()
						|| posicaoMovimentoCanoHorizontal < -canoBaixo.getWidth()) {
					posicaoMovimentoCanoHorizontal = larguraDoDispositivo;
					//o -200 é para gerar numeros NEGATIVOS também!! para tanto subir quanto descer!
					alturaEntreCanosRandomica = numeroRandomico.nextInt(400) - 200;
					marcouPonto = false;
				}
			}else{//tela de game over

				if (Gdx.input.justTouched()){

					//colocar a preferencia RECORDE
					record(pontuacao);

					estadoJogo=0;
					pontuacao=0;
					velocidadeQueda=0;
					posicaoInicialVertical = alturaDoDispositivo/2;
					posicaoMovimentoCanoHorizontal = larguraDoDispositivo;
					marcouPonto = false;
				}

			}

			//verifica pontuacao
			if (posicaoMovimentoCanoHorizontal<=120){

				if (!marcouPonto){
				pontuacao++;
				marcouPonto=true;
				}
			}

		}

		//configurar dados de projeção da camera
		batch.setProjectionMatrix(camera.combined); //recuperamos os dados de projeção

		batch.begin();

		//A ORDEM TEM IMPORTANCIA!!!
		//os parametros 4 e 5 sao referentes a largura e altura da imagem
		batch.draw(fundo, 0,0, larguraDoDispositivo, alturaDoDispositivo);
		batch.draw(canoTopo,posicaoMovimentoCanoHorizontal,alturaDoDispositivo/2 + espacoEntreCanos/2 + alturaEntreCanosRandomica);
		batch.draw(canoBaixo, posicaoMovimentoCanoHorizontal, alturaDoDispositivo/2 - canoBaixo.getHeight() - espacoEntreCanos/2 +alturaEntreCanosRandomica);
		//esse (int) antes de variacao é pra converter o numero float para inteiro!
		batch.draw(passaros[(int)variacao], 120, posicaoInicialVertical);
		//converte a pontuacao para uma string
		fonte.draw(batch, String.valueOf(pontuacao), larguraDoDispositivo/2, alturaDoDispositivo - 50 );
		//game over
		if (estadoJogo==2){
			batch.draw(gameOver, larguraDoDispositivo/2 - gameOver.getWidth()/2, alturaDoDispositivo/2);
			mensagem.draw(batch, "Toque para Reiniciar", larguraDoDispositivo/2 - 200, alturaDoDispositivo/2 - gameOver.getHeight()/2);
		}

		batch.end();

		passaroCirculo.set(120 + passaros[0].getWidth()/2, posicaoInicialVertical + passaros[0].getHeight()/2, passaros[0].getWidth()/2);
		retangulocanoBaixo = new Rectangle(
				posicaoMovimentoCanoHorizontal,
				alturaDoDispositivo/2 - canoBaixo.getHeight() - espacoEntreCanos/2 +alturaEntreCanosRandomica,
				canoBaixo.getWidth(),
				canoBaixo.getHeight()
		);//x,y,largura,altura

        retangulocanoTopo = new Rectangle(
                posicaoMovimentoCanoHorizontal,
                alturaDoDispositivo/2 + espacoEntreCanos/2 + alturaEntreCanosRandomica,
                canoTopo.getWidth(),
                canoTopo.getHeight()
        );

		//desenhar as formas  (aqui é apenas para vizualizarmos na tela, mas para a colisao nao precisa mostrar na tela essas formas)
       /* shape.begin(ShapeRenderer.ShapeType.Filled); //forma preenchida

        shape.circle(passaroCirculo.x, passaroCirculo.y, passaroCirculo.radius);
        shape.rect(retangulocanoBaixo.x,retangulocanoBaixo.y, retangulocanoBaixo.width, retangulocanoBaixo.height);
        shape.rect(retangulocanoTopo.x,retangulocanoTopo.y,retangulocanoTopo.width,retangulocanoTopo.height);
        shape.setColor(Color.RED);

        shape.end();*/

        //Teste de colisao
		if(Intersector.overlaps(passaroCirculo,retangulocanoBaixo) || Intersector.overlaps(passaroCirculo,retangulocanoTopo)
                || posicaoInicialVertical<=0 || posicaoInicialVertical>alturaDoDispositivo){
            //Gdx.app.log("Colisao", "Houve Colisao");
			estadoJogo = 2; //game over
		}

	}

	@Override //chamado sempre q a largura e altura do dispositivo for alterada //primeiro o create depois o resize
	public void resize(int width, int height) {
		viewport.update(width,height);
	}

	public void record (int record){

	}

	/*@Override
	public void dispose () {

	}*/

}
