package dam.cristian.icubes;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;

public class JuegoPrincipal extends Activity {
	private MediaPlayer mp;
	Controlador controlador;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GameView vista = new GameView(this);
		controlador = new Controlador(vista, getSharedPreferences(
				"puntuaciones", Context.MODE_PRIVATE));
		setContentView(vista);

		mp = MediaPlayer.create(this, R.raw.fondo_loop);
		mp.setLooping(true);
		mp.setVolume(1, 1);
		mp.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_principal, menu);
		return true;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mp.pause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mp.start();
	}

	@Override
	public void onBackPressed() {
		controlador.apagarTemporizador();
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mp.stop();
		mp.release();
	}
}
