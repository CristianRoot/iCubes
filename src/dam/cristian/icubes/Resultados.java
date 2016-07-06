package dam.cristian.icubes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MotionEvent;
import android.widget.TextView;

public class Resultados extends Activity {
	private TextView tvPuntos, tvContinuar;
	private int puntos;
	private long tiempo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_resultados);

		tiempo = System.currentTimeMillis();
		tvPuntos = (TextView) findViewById(R.id.tvPuntos);
		tvContinuar = (TextView) findViewById(R.id.tvContinuar);
		puntos = getIntent().getIntExtra("puntuacion", 0);
		tvPuntos.setText(String.valueOf(puntos));

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				tvContinuar.setVisibility(TextView.VISIBLE);
			}
		}, 2000);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_principal, menu);
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (System.currentTimeMillis() - tiempo > 2000) {
				finish();
				overridePendingTransition(R.anim.right_to_center,
						R.anim.center_to_left);
			}
		}

		return true;
	}
}
