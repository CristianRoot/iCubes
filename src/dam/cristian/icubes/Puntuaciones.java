package dam.cristian.icubes;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Puntuaciones extends Activity {
	private SharedPreferences puntuaciones;
	private LinearLayout lyIzquierda;
	private LinearLayout lyDerecha;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_puntuaciones);
		lyIzquierda = (LinearLayout) findViewById(R.id.izquierda);
		lyDerecha = (LinearLayout) findViewById(R.id.derecha);
		puntuaciones = getSharedPreferences("puntuaciones", CONTEXT_RESTRICTED);

		cargarPuntuaciones();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_principal, menu);
		return true;
	}

	private void cargarPuntuaciones() {
		for (int i = 0; i < 5; i++) {
			((TextView) lyIzquierda.getChildAt(i)).setText((i + 1) + ". "
					+ puntuaciones.getInt("punt" + i, 0));
		}
		for (int i = 5; i < 10; i++) {
			((TextView) lyDerecha.getChildAt(i - 5)).setText((i + 1) + ". "
					+ puntuaciones.getInt("punt" + i, 0));
		}
	}
}
