package dam.cristian.icubes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MenuPrincipal extends Activity {
	private static final int REQUEST_PRINCIPAL = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu_principal);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_principal, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuSalir:
			finish();
			break;
		case R.id.menuAcerca:
			Toast.makeText(this, "Designed by Cristian Gonzalez Morante",
					Toast.LENGTH_SHORT).show();
		default:
			break;
		}
		return false;
	}

	public void jugar(View v) {
		Intent i = new Intent(this, JuegoPrincipal.class);
		startActivityForResult(i, REQUEST_PRINCIPAL);
		overridePendingTransition(R.anim.left_to_center, R.anim.center_to_right);
	}

	public void mostrarResultados(int puntuacion) {
		Intent i = new Intent(this, Resultados.class);
		i.putExtra("puntuacion", puntuacion);
		startActivity(i);
		overridePendingTransition(R.anim.top_to_center, R.anim.center_to_bot);
	}

	public void mostrarInstrucciones(View v) {
		Intent i = new Intent(this, Instrucciones.class);
		startActivity(i);
	}

	public void mostrarPuntuaciones(View v) {
		Intent i = new Intent(this, Puntuaciones.class);
		startActivity(i);
	}

	public void salir(View v) {
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_PRINCIPAL:
				mostrarResultados(data.getIntExtra("puntuacion", 0));
			}
		}
	}
}
