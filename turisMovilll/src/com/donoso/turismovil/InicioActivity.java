package com.donoso.turismovil;

import com.turismovil.reportes.FirstPdf;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class InicioActivity extends Activity {
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inicio);
		final Button btn_espanol = (Button)findViewById(R.id.btn_espanol);
		
		btn_espanol.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				FirstPdf gen = new FirstPdf();
				gen.generar();
				Intent inicio_login = new Intent(InicioActivity.this, LoginActivity.class);
				startActivity(inicio_login);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

}
