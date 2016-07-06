package dam.cristian.icubes;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Cuadrado {
	public static final int AZUL = 1;
	public static final int AMARILLO = 2;
	public static final int AZUL_BORRAR = 3;
	public static final int AMARILLO_BORRAR = 4;

	private int i, j;
	private int color;
	private float ancho;
	private Paint pincel;
	private boolean marcado;
	private boolean borrado;
	private float x, y;
	private int iDestino;
	private boolean estaBajando;
	private int alphaPincel;
	private float margenIzquierdo;

	public Cuadrado(float margenIzquierdo, int color, float ancho, int i, int j) {
		this.color = color;
		this.ancho = ancho;
		this.i = i;
		this.j = j;
		this.margenIzquierdo = margenIzquierdo;
		this.x = getCorX();
		this.y = getCorY();
		marcado = false;
		borrado = false;
		estaBajando = false;
		iDestino = 0;
		pincel = new Paint(Paint.ANTI_ALIAS_FLAG);
		alphaPincel = 255;
	}

	public void setIDestino(int i) {
		iDestino = i;
	}

	public int getIDestino() {
		return iDestino;
	}

	public float getCorX() {
		return margenIzquierdo + (j * ancho);
	}

	public float getCorY() {
		return i * ancho;
	}

	public float getCorXReal() {
		return margenIzquierdo + x;
	}

	public float getCorYReal() {
		return y;
	}

	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public int getJ() {
		return j;
	}

	public void setJ(int j) {
		this.j = j;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public float getAncho() {
		return ancho;
	}

	public void setAncho(float ancho) {
		this.ancho = ancho;
	}

	public Paint getPincel() {
		return pincel;
	}

	public void setPincel(Paint pincel) {
		this.pincel = pincel;
	}

	public boolean estaMarcado() {
		return marcado;
	}

	public void desmarcar() {
		borrado = false;
		marcado = false;
		switch (color) {
		case AZUL_BORRAR:
			color = AZUL;
			break;
		case AMARILLO_BORRAR:
			color = AMARILLO;
			break;
		}
	}

	public void borrar() {
		borrado = true;
	}

	public boolean estaBorrado() {
		return borrado;
	}

	public void moverIzquierda() {
		j--;
		x = getCorX();
	}

	public void moverDerecha() {
		j++;
		x = getCorX();
	}

	public void moverAbajo() {
		i++;
		y = getCorY();
	}

	public void marcarParaBorrar() {
		switch (color) {
		case AZUL:
			color = AZUL_BORRAR;
			break;
		case AMARILLO:
			color = AMARILLO_BORRAR;
			break;
		}
		marcado = true;
	}

	public boolean esIgual(Cuadrado c) {
		boolean result = false;

		switch (c.getColor()) {
		case AZUL:
			if (color == AZUL || color == AZUL_BORRAR)
				result = true;
			break;
		case AMARILLO:
			if (color == AMARILLO || color == AMARILLO_BORRAR)
				result = true;
			break;
		case AZUL_BORRAR:
			if (color == AZUL || color == AZUL_BORRAR)
				result = true;
			break;
		case AMARILLO_BORRAR:
			if (color == AMARILLO || color == AMARILLO_BORRAR)
				result = true;
			break;
		}

		return result;
	}

	public boolean estaBajando() {
		return estaBajando;
	}

	public void animarCaida(int filaDestino) {
		final float yFinal = filaDestino * ancho;
		estaBajando = true;

		new Thread() {
			@Override
			public void run() {
				while (y < yFinal) {
					y++;
					try {
						sleep(2);
					} catch (InterruptedException ie) {
					}
				}
				y = getCorY();
				estaBajando = false;
			}
		}.start();

	}

	public void doDraw(Canvas canvas) {
		switch (color) {
		case AZUL:
			pincel.setARGB(alphaPincel, 51, 181, 229);
			break;
		case AMARILLO:
			pincel.setARGB(alphaPincel, 255, 187, 51);
			break;
		case AZUL_BORRAR:
			pincel.setARGB(alphaPincel, 0, 153, 204);
			break;
		case AMARILLO_BORRAR:
			pincel.setARGB(alphaPincel, 255, 136, 0);
			break;
		}

		if (borrado) {
			if (alphaPincel > 90) {
				alphaPincel -= 5;
				pincel.setAlpha(alphaPincel);
			}
		}

		canvas.drawRect(x, y, x + ancho, y + ancho, pincel);
	}
}
