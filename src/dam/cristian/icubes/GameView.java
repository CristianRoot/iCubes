package dam.cristian.icubes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
	private GameLoop thread;
	private Controlador controlador;
	private Paint pincel;
	private float anchoPantalla, altoPantalla;
	private float anchoTablero, altoTablero;
	private float xMover;
	private float yMover;
	private float xPulsacion;
	private float yPulsacion;
	private Object mObj = new Object();
	private long tiempoAccion = 0;
	private final float density = getResources().getDisplayMetrics().density;
	private float margenIzquierdo;
	private boolean inicializado;
	private boolean noMover;

	class GameLoop extends Thread {
		private SurfaceHolder sh;
		private GameView juego;
		private boolean run;
		private static final int FPS = 60;

		public GameLoop(SurfaceHolder sh, GameView juego) {
			this.sh = sh;
			this.juego = juego;
			run = false;
		}

		public void setRunning(boolean run) {
			synchronized (sh) {
				this.run = run;
			}
		}

		public void run() {
			long ticksPS = 100 / FPS;
			long startTime;
			long sleepTime;
			long tiempo = System.currentTimeMillis();
			Canvas canvas;

			while (run) {
				canvas = null;
				startTime = System.currentTimeMillis();
				try {
					canvas = sh.lockCanvas(null);
					synchronized (sh) {
						if (run) {
							juego.dibujar(canvas);

							if (controlador.moverLinea()) {
								controlador.borrarCuadradosMarcados();
							}
							synchronized (mObj) {
								controlador.marcarCuadrados();
								controlador.bajarCuadrados();
								controlador.desmarcarCuadradosPerdidos();
							}
							if (System.currentTimeMillis() - tiempo >= controlador
									.getVelocidad()) {
								tiempo = System.currentTimeMillis();

								if (!controlador.bajarCuadradoGrande()) {
									if (!controlador.comprobarGameOver()) {
										synchronized (mObj) {
											controlador.generarCuadradoGrande();
										}
									}
								}
							}
						}
					}
				} finally {
					if (canvas != null)
						sh.unlockCanvasAndPost(canvas);
				}
				sleepTime = ticksPS - (System.currentTimeMillis() - startTime);
				try {
					if (sleepTime > 0)
						sleep(sleepTime);
					else
						sleep(10);
				} catch (InterruptedException ie) {
				}
			}
		}
	}

	public GameView(Context context) {
		super(context);
		inicializado = false;
		getHolder().addCallback(this);
	}

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		inicializado = false;
		getHolder().addCallback(this);
	}

	public GameView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		inicializado = false;
		getHolder().addCallback(this);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		anchoPantalla = getWidth();
		altoPantalla = getHeight();
		noMover = false;
		pincel = new Paint(Paint.ANTI_ALIAS_FLAG);

		if (!inicializado) {
			controlador.inicializar();
			inicializado = true;
		} else
			controlador.reanudarTemporizador();

		anchoTablero = controlador.getAnchoTablero();
		altoTablero = controlador.getAltoTablero();
		margenIzquierdo = (anchoPantalla / 2) - (anchoTablero / 2);

		thread = new GameLoop(getHolder(), this);
		thread.setRunning(true);
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;

		controlador.apagarTemporizador();
		thread.setRunning(false);
		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException ie) {
			}
		}
	}

	public void setNoMover(boolean valor) {
		noMover = valor;
	}

	public float getAltoPantalla() {
		return altoPantalla;
	}

	public float getAnchoPantalla() {
		return anchoPantalla;
	}

	public float getDensidad() {
		return density;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!noMover) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_MOVE:
				synchronized (mObj) {
					if (controlador.moverCuadrado(event.getX(), event.getY(),
							xMover, yMover)) {
						xMover = event.getX();
						yMover = event.getY();
					}
				}
				break;
			case MotionEvent.ACTION_DOWN:
				xMover = event.getX();
				yMover = event.getY();
				xPulsacion = event.getX();
				yPulsacion = event.getY();
				tiempoAccion = System.currentTimeMillis();
			case MotionEvent.ACTION_UP:
				if (!controlador.comprobarSiCaidaRapida(event.getY(), yMover)) {
					float anchoCuadrado = controlador.getAnchoCuadrado();
					if (System.currentTimeMillis() - tiempoAccion > 5
							&& System.currentTimeMillis() - tiempoAccion < 100
							&& (event.getX() < xPulsacion + anchoCuadrado && event
									.getX() > xPulsacion - anchoCuadrado)
							&& (event.getY() < yPulsacion + anchoCuadrado && event
									.getY() > yPulsacion - anchoCuadrado)) {
						synchronized (mObj) {
							controlador.rotarCubo();
						}
					}
				}
				break;
			}
		}
		return true;
	}

	public float getMargenIzquierdo() {
		return margenIzquierdo;
	}

	public GameLoop getGameLoop() {
		return thread;
	}

	public void setControlador(Controlador controlador) {
		this.controlador = controlador;
	}

	public void dibujar(Canvas canvas) {
		// Fondo
		canvas.drawARGB(255, 251, 248, 239);

		// Bordes
		pincel.setStrokeWidth(1);
		pincel.setARGB(255, 180, 180, 180);
		canvas.drawLine(margenIzquierdo, altoTablero + 2, margenIzquierdo
				+ anchoTablero, altoTablero + 2, pincel);

		// Puntuacion
		pincel.setARGB(255, 51, 181, 229);
		pincel.setTextSize(36);
		canvas.drawText(getResources().getString(R.string.puntuacion) + ": "
				+ controlador.getPuntuacion(), margenIzquierdo,
				altoTablero + 50, pincel);
		canvas.drawText(getResources().getString(R.string.tiempo) + ": "
				+ controlador.getTiempoRestante(), margenIzquierdo + 510,
				altoTablero + 50, pincel);
		canvas.drawText("x" + String.valueOf(controlador.getMultiplicador()),
				anchoTablero, altoTablero + 50, pincel);

		controlador.pintarTablero(canvas);
		controlador.pintarCuadradoGrande(canvas);

		pincel.setARGB(255, 255, 0, 0);
		pincel.setStrokeWidth(2);
		canvas.drawLine(controlador.getPosLinea(),
				controlador.getAnchoCuadrado() * 2, controlador.getPosLinea(),
				altoTablero, pincel);
	}
}
