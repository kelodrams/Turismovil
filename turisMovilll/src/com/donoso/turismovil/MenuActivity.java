package com.donoso.turismovil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MenuActivity extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		
		final ImageView img_1 = (ImageView)findViewById(R.id.imageView1);
		final ImageView img_2 = (ImageView)findViewById(R.id.imageView2);
		final ImageView img_3 = (ImageView)findViewById(R.id.imageView3);
		final ImageView img_4 = (ImageView)findViewById(R.id.imageView4);
		final ImageView img_5 = (ImageView)findViewById(R.id.imageView5);
	
		
		img_1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent inicio_menu = new Intent(MenuActivity.this, PerfilUsuarioActivity.class);
				startActivity(inicio_menu);
			}
		});
		
		
		
img_2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent inicio_menu = new Intent(MenuActivity.this, MapaActivity.class);
				startActivity(inicio_menu);
			}
		});

img_3.setOnClickListener(new View.OnClickListener() {
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Intent salida=new Intent( Intent.ACTION_MAIN); //Llamando a la activity principal
		finish();
		
	}
});

img_4.setOnClickListener(new View.OnClickListener() {
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Intent inicio_menu = new Intent(MenuActivity.this, CalendarioActivity.class);
		startActivity(inicio_menu);
	}
});


img_5.setOnClickListener(new View.OnClickListener() {
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Intent inicio_menu = new Intent(MenuActivity.this, TelefonoActivity.class);
		startActivity(inicio_menu);
	}
});





	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.perfil_usuario, menu);
		return true;
	}
	
	

}
