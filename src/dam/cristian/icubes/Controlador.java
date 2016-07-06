package dam.cristian.icubes;

import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.CountDownTimer;
import android.util.Log;

public class Controlador {
	private SharedPreferences puntuaciones;
	private GameView vista;
	private CountDownTimer temporizador;
	private int tiempo, puntuacion, velocidad, columnaLinea;
	private int multiplicador, numBorrados;
	private Cuadrado[] cuadradoGrande;
	private Cuadrado[][] tableroDeJuego;
	private float anchoCuadrado, anchoTablero;
	private float altoTablero, velocidadLinea, xLinea;
	private Vector<Cuadrado> cuadradosParaBajar;
	private SoundPool soundPool;
	private int idGiro, idJuntarse;
	private boolean borrando;

	public Controlador(GameView vista, SharedPreferences puntuaciones) {
		this.puntuaciones = puntuaciones;
		this.vista = vista;
		this.vista.setControlador(this);
	}

	public void inicializar() {
		anchoCuadrado = vista.getAltoPantalla() / 15;
		int columnas = (int) ((vista.getAnchoPantalla() - (50 * vista
				.getDensidad())) / anchoCuadrado);
		int filas = (int) ((vista.getAltoPantalla() 
				- (40 * vista.getDensidad())) / anchoCuadrado);
		tableroDeJuego = new Cuadrado[filas][columnas];
		velocidad = 500;
		velocidadLinea = 1 * vista.getDensidad();
		altoTablero = tableroDeJuego.length * anchoCuadrado;
		anchoTablero = tableroDeJuego[0].length * anchoCuadrado;
		float margenIzquierdo = (vista.getAnchoPantalla() / 2)
				- (anchoTablero / 2);
		borrando = false;

		soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		idGiro = soundPool.load(vista.getContext(), R.raw.giro, 0);
		idJuntarse = soundPool.load(vista.getContext(), R.raw.juntarse, 0);
		multiplicador = 1;
		numBorrados = 0;
		cuadradosParaBajar = new Vector<Cuadrado>();
		xLinea = margenIzquierdo;
		generarCuadradoGrande();
		activarTemporizador();
	}

	public boolean moverCuadrado(float x, float y, Float xMover, Float yMover) {
		boolean seHaMovido = false;
		if (x + anchoCuadrado * 1.5 < xMover) {
			if (cuadradoGrande[0].getJ() > 0
					&& tableroDeJuego[cuadradoGrande[0].getI()][cuadradoGrande[0]
							.getJ() - 1] == null
					&& tableroDeJuego[cuadradoGrande[2].getI()][cuadradoGrande[2]
							.getJ() - 1] == null) {
				for (Cuadrado c : cuadradoGrande) {
					borrarCuadrado(c);
					c.moverIzquierda();
					colocarCuadrado(c);
				}
			}
			seHaMovido = true;
		} else if (x - anchoCuadrado * 1.5 > xMover) {
			if (cuadradoGrande[1].getJ() < tableroDeJuego[0].length - 1
					&& tableroDeJuego[cuadradoGrande[1].getI()][cuadradoGrande[1]
							.getJ() + 1] == null
					&& tableroDeJuego[cuadradoGrande[3].getI()][cuadradoGrande[3]
							.getJ() + 1] == null) {
				for (int i = cuadradoGrande.length - 1; i >= 0; i--) {
					Cuadrado c = cuadradoGrande[i];
					borrarCuadrado(c);
					c.moverDerecha();
					colocarCuadrado(c);
				}
			}
			seHaMovido = true;
		}
		return seHaMovido;
	}

	public boolean moverLinea() {
		int columAux;
		boolean seHaBorradoAlgo = false;
		boolean hayQueLimpiar = false;
		float margenIz = vista.getMargenIzquierdo();

		if (xLinea >= anchoTablero + margenIz) {
			xLinea = margenIz;
		} else {
			xLinea += velocidadLinea;
			columAux = (int) ((xLinea - margenIz) / anchoCuadrado);
			if (columAux != columnaLinea) {
				columnaLinea = columAux;
				if (columnaLinea < tableroDeJuego[0].length) {
					for (int i = 0; i < tableroDeJuego.length; i++) {
						if (tableroDeJuego[i][columnaLinea] != null
								&& tableroDeJuego[i][columnaLinea]
										.estaMarcado()) {
							borrando = true;
							seHaBorradoAlgo = true;
							tableroDeJuego[i][columnaLinea].borrar();
							aumentarPuntuacion(multiplicador);
							numBorrados++;
							if (numBorrados % 10 == 0)
								multiplicador++;
						}
					}
					if (borrando && !seHaBorradoAlgo) {
						borrando = false;
						hayQueLimpiar = true;
						numBorrados = 0;
						multiplicador = 1;
					}
				}
			}
		}
		return hayQueLimpiar;
	}

	public void borrarCuadradosMarcados() {
		for (int i = 0; i < tableroDeJuego.length; i++)
			for (int j = 0; j < tableroDeJuego[i].length; j++)
				if (tableroDeJuego[i][j] != null
						&& tableroDeJuego[i][j].estaBorrado())
					tableroDeJuego[i][j] = null;
	}

	public void marcarCuadrados() {
		boolean seHaMarcado = false;

		for (int i = 0; i < tableroDeJuego.length; i++) {
			for (int j = 0; j < tableroDeJuego[i].length; j++) {
				if (tableroDeJuego[i][j] != null)
					if (!tableroDeJuego[i][j].estaMarcado()
							&& comprobarSiFormaCuadrado(tableroDeJuego[i][j]))
						seHaMarcado = true;
			}
		}
		if (seHaMarcado)
			soundPool.play(idJuntarse, 4, 4, 0, 0, 0);
	}

	public boolean bajarCuadradoGrande() {
		boolean haBajado = true;
		boolean haBajadoAlguno = false;
		Cuadrado c;

		for (int i = cuadradoGrande.length - 1; i >= 0; i--) {
			c = cuadradoGrande[i];
			if (c.getI() < tableroDeJuego.length - 1
					&& tableroDeJuego[c.getI() + 1][c.getJ()] == null) {
				borrarCuadrado(c);
				c.moverAbajo();
				colocarCuadrado(c);
				haBajadoAlguno = true;
			} else
				haBajado = false;
		}
		if (!haBajado) {
			activarCaidaRapida();
		}

		if (!haBajadoAlguno) {
			vista.setNoMover(false);
			velocidad = 500;
		}

		return haBajadoAlguno;
	}

	public boolean comprobarSiCaidaRapida(float y, float yMover) {
		if (y > yMover + anchoCuadrado * 2) {
			activarCaidaRapida();
			return true;
		} else
			return false;
	}

	private void activarCaidaRapida() {
		vista.setNoMover(true);
		velocidad = 50;
	}

	public void bajarCuadrados() {
		Cuadrado c;
		int auxI;
		int columnas = tableroDeJuego[0].length;
		int filas = tableroDeJuego.length;

		for (int j = columnas - 1; j >= 0; j--) {
			int count = 0;
			for (int i = filas - 2; i >= 1; i--) {
				c = tableroDeJuego[i][j];
				if (c != null && !c.estaBajando()
						&& !estaEnCuadradoGrande(i, j)) {
					auxI = i;
					while (auxI < filas - 1 && hayVaciosDebajo(auxI, j)) {
						auxI++;
					}
					if (auxI > i) {
						c.setIDestino(auxI - count);
						cuadradosParaBajar.add(c);
						count++;
					}
				}
			}
		}

		for (int i = 0; i < cuadradosParaBajar.size(); i++) {
			borrarCuadrado(cuadradosParaBajar.get(i));
			cuadradosParaBajar.get(i).setI(
					cuadradosParaBajar.get(i).getIDestino());
			colocarCuadrado(cuadradosParaBajar.get(i));
			cuadradosParaBajar.get(i).animarCaida(
					cuadradosParaBajar.get(i).getIDestino());
		}
		cuadradosParaBajar.clear();
	}

	public void desmarcarCuadradosPerdidos() {
		for (int i = 0; i < tableroDeJuego.length; i++)
			for (int j = 0; j < tableroDeJuego[i].length; j++)
				if (tableroDeJuego[i][j] != null
						&& tableroDeJuego[i][j].estaMarcado()
						&& !comprobarSiFormaCuadrado(tableroDeJuego[i][j]))
					tableroDeJuego[i][j].desmarcar();
	}

	private boolean hayVaciosDebajo(int fila, int col) {
		boolean result = false;

		for (int i = fila + 1; i < tableroDeJuego.length; i++) {
			if (tableroDeJuego[i][col] == null) {
				result = true;
			}
		}

		return result;
	}

	private boolean comprobarSiFormaCuadrado(Cuadrado c) {
		int i, j;
		boolean haFormadoCuadrado = false;
		int columnas = tableroDeJuego[0].length;
		int filas = tableroDeJuego.length;

		i = c.getI();
		j = c.getJ();
		if (j > 0 && i > 0 && tableroDeJuego[i][j - 1] != null
				&& tableroDeJuego[i][j - 1].esIgual(c)
				&& !estaEnCuadradoGrande(i, j - 1)
				&& tableroDeJuego[i - 1][j - 1] != null
				&& tableroDeJuego[i - 1][j - 1].esIgual(c)
				&& !estaEnCuadradoGrande(i - 1, j - 1)
				&& tableroDeJuego[i - 1][j] != null
				&& tableroDeJuego[i - 1][j].esIgual(c)
				&& !estaEnCuadradoGrande(i - 1, j)) {
			tableroDeJuego[i][j - 1].marcarParaBorrar();
			tableroDeJuego[i - 1][j - 1].marcarParaBorrar();
			tableroDeJuego[i - 1][j].marcarParaBorrar();
			tableroDeJuego[i][j].marcarParaBorrar();
			haFormadoCuadrado = true;
		}
		if (j < columnas - 1 && i > 0 && tableroDeJuego[i][j + 1] != null
				&& tableroDeJuego[i][j + 1].esIgual(c)
				&& !estaEnCuadradoGrande(i, j + 1)
				&& tableroDeJuego[i - 1][j + 1] != null
				&& tableroDeJuego[i - 1][j + 1].esIgual(c)
				&& !estaEnCuadradoGrande(i - 1, j + 1)
				&& tableroDeJuego[i - 1][j] != null
				&& tableroDeJuego[i - 1][j].esIgual(c)
				&& !estaEnCuadradoGrande(i - 1, j)) {
			tableroDeJuego[i][j + 1].marcarParaBorrar();
			tableroDeJuego[i - 1][j + 1].marcarParaBorrar();
			tableroDeJuego[i - 1][j].marcarParaBorrar();
			tableroDeJuego[i][j].marcarParaBorrar();
			haFormadoCuadrado = true;
		}
		if (j > 0 && i < filas - 1 && tableroDeJuego[i][j - 1] != null
				&& tableroDeJuego[i][j - 1].esIgual(c)
				&& !estaEnCuadradoGrande(i, j - 1)
				&& tableroDeJuego[i + 1][j - 1] != null
				&& tableroDeJuego[i + 1][j - 1].esIgual(c)
				&& !estaEnCuadradoGrande(i + 1, j - 1)
				&& tableroDeJuego[i + 1][j] != null
				&& tableroDeJuego[i + 1][j].esIgual(c)
				&& !estaEnCuadradoGrande(i + 1, j)) {
			tableroDeJuego[i][j - 1].marcarParaBorrar();
			tableroDeJuego[i + 1][j - 1].marcarParaBorrar();
			tableroDeJuego[i + 1][j].marcarParaBorrar();
			tableroDeJuego[i][j].marcarParaBorrar();
			haFormadoCuadrado = true;
		}
		if (j < columnas - 1 && i < filas - 1
				&& tableroDeJuego[i][j + 1] != null
				&& tableroDeJuego[i][j + 1].esIgual(c)
				&& !estaEnCuadradoGrande(i, j + 1)
				&& tableroDeJuego[i + 1][j + 1] != null
				&& tableroDeJuego[i + 1][j + 1].esIgual(c)
				&& !estaEnCuadradoGrande(i + 1, j + 1)
				&& tableroDeJuego[i + 1][j] != null
				&& tableroDeJuego[i + 1][j].esIgual(c)
				&& !estaEnCuadradoGrande(i + 1, j)) {
			tableroDeJuego[i][j + 1].marcarParaBorrar();
			tableroDeJuego[i + 1][j + 1].marcarParaBorrar();
			tableroDeJuego[i + 1][j].marcarParaBorrar();
			tableroDeJuego[i][j].marcarParaBorrar();
			haFormadoCuadrado = true;
		}
		return haFormadoCuadrado;
	}

	private boolean estaEnCuadradoGrande(int i, int j) {
		boolean encontrado = false;
		int k = 0;

		while (!encontrado && k < cuadradoGrande.length) {
			if (cuadradoGrande[k].getI() == i && cuadradoGrande[k].getJ() == j)
				encontrado = true;
			else
				k++;
		}

		return encontrado;
	}

	public void pintarTablero(Canvas c) {
		for (int i = 0; i < tableroDeJuego.length; i++) {
			for (int j = 0; j < tableroDeJuego[i].length; j++) {
				if (tableroDeJuego[i][j] != null)
					tableroDeJuego[i][j].doDraw(c);
			}
		}
	}

	public void pintarCuadradoGrande(Canvas c) {
		for (Cuadrado cu : cuadradoGrande)
			cu.doDraw(c);
	}

	public void generarCuadradoGrande() {
		float margenIz = (vista.getAnchoPantalla() / 2) - (anchoTablero / 2);
		int col = tableroDeJuego[0].length;
		cuadradoGrande = new Cuadrado[4];
		cuadradoGrande[0] = new Cuadrado(margenIz,
				(int) (Math.random() * 2) + 1, anchoCuadrado, 0, col / 2);
		cuadradoGrande[1] = new Cuadrado(margenIz,
				(int) (Math.random() * 2) + 1, anchoCuadrado, 0, col / 2 + 1);
		cuadradoGrande[2] = new Cuadrado(margenIz,
				(int) (Math.random() * 2) + 1, anchoCuadrado, 1, col / 2);
		cuadradoGrande[3] = new Cuadrado(margenIz,
				(int) (Math.random() * 2) + 1, anchoCuadrado, 1, col / 2 + 1);
	}

	public void rotarCubo() {
		int auxC = cuadradoGrande[0].getColor();
		cuadradoGrande[0].setColor(cuadradoGrande[1].getColor());
		cuadradoGrande[1].setColor(cuadradoGrande[3].getColor());
		cuadradoGrande[3].setColor(cuadradoGrande[2].getColor());
		cuadradoGrande[2].setColor(auxC);
		soundPool.play(idGiro, 1, 1, 1, 0, 1);
	}

	private void borrarCuadrado(Cuadrado c) {
		tableroDeJuego[c.getI()][c.getJ()] = null;
	}

	private void colocarCuadrado(Cuadrado c) {
		if (c.getI() > 1)
			tableroDeJuego[c.getI()][c.getJ()] = c;
	}

	public float getAnchoCuadrado() {
		return anchoCuadrado;
	}

	public float getPosLinea() {
		return xLinea;
	}

	public float getAltoTablero() {
		return altoTablero;
	}

	public float getAnchoTablero() {
		return anchoTablero;
	}

	public int getVelocidad() {
		return velocidad;
	}

	public int getMultiplicador() {
		return multiplicador;
	}

	public void activarTemporizador() {
		tiempo = 61;
		temporizador = new CountDownTimer((tiempo + 1) * 1000, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				tiempo--;
				if (tiempo == 0)
					gameOver();
			}

			@Override
			public void onFinish() {

			}
		}.start();
	}

	public void apagarTemporizador() {
		temporizador.cancel();
	}

	public void reanudarTemporizador() {
		temporizador = new CountDownTimer((tiempo + 1) * 1000, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				tiempo--;
				Log.d("tiempo", String.valueOf(tiempo));
				if (tiempo == 0)
					gameOver();
			}

			@Override
			public void onFinish() {

			}
		}.start();
	}

	public int getTiempoRestante() {
		return tiempo;
	}

	public boolean comprobarGameOver() {
		if (cuadradoGrande[0].getI() == 0 || cuadradoGrande[1].getI() == 0
				|| cuadradoGrande[2].getI() == 1
				|| cuadradoGrande[3].getI() == 1) {
			gameOver();
			return true;
		} else
			return false;
	}

	private void gameOver() {
		int posRanking;

		vista.getGameLoop().setRunning(false);
		temporizador.cancel();
		posRanking = guardarPuntuacion();
		Activity padre = ((Activity) vista.getContext());
		Intent datos = new Intent();
		datos.putExtra("puntuacion", puntuacion);
		datos.putExtra("posRanking", posRanking);
		padre.setResult(Activity.RESULT_OK, datos);
		padre.finish();
	}

	private void aumentarPuntuacion(int num) {
		puntuacion += num;
	}

	private int guardarPuntuacion() {
		int i = 0, pos = 0;
		boolean colocado = false;
		SharedPreferences.Editor editor = puntuaciones.edit();

		while (!colocado && i < 10) {
			if (puntuacion > puntuaciones.getInt("punt" + i, 0)) {
				editor.putInt("punt" + i, puntuacion);
				editor.commit();
				colocado = true;
				pos = i + 1;
			} else
				i++;
		}
		return pos;
	}

	public int getPuntuacion() {
		return puntuacion;
	}
}
