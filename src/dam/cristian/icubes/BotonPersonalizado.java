package dam.cristian.icubes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.TextView;

public class BotonPersonalizado extends TextView {
	Paint pincelFondo;
	Paint pincelTexto;
	float escala;
	RectF rectanguloFondo;

	public BotonPersonalizado(Context context) {
		super(context);
		inicializar();
	}

	public BotonPersonalizado(Context context, AttributeSet attrs) {
		super(context, attrs);
		inicializar();
	}

	public BotonPersonalizado(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		inicializar();
	}

	private void inicializar() {
		pincelFondo = new Paint(Paint.ANTI_ALIAS_FLAG);
		pincelFondo.setARGB(255, 51, 181, 229);
		pincelFondo.setStyle(Style.FILL_AND_STROKE);

		pincelTexto = new Paint(Paint.ANTI_ALIAS_FLAG);
		pincelTexto.setColor(Color.WHITE);
		pincelTexto.setTextAlign(Align.CENTER);
		escala = getResources().getDisplayMetrics().density;
	}

	@SuppressLint("DrawAllocation")
	protected void onDraw(Canvas c) {
		super.onDraw(c);

		pincelTexto.setTextSize(getHeight() / 2);
		rectanguloFondo = new RectF(0, 0, this.getWidth(), this.getHeight());
		c.drawRoundRect(rectanguloFondo, 30, 30, pincelFondo);
		c.drawText(this.getTag().toString(), this.getWidth() / 2,
				(float) (this.getHeight() / 1.5), pincelTexto);
	}
}
