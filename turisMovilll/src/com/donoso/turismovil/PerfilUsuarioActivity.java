package com.donoso.turismovil;

import java.io.File;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class PerfilUsuarioActivity extends Activity {
	
	
	Button btnCamara;
	private String foto;
	private static int TAKE_PICTURE = 1;
	double aleatorio = 0;

	@Override   
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_perfil_usuario);
		
		final Button btn_miUbicacion = (Button)findViewById(R.id.btn_miUbicacion);
		final Button btn_tomarFoto = (Button)findViewById(R.id.btn_tomarFoto);
		final Button btn_x = (Button)findViewById(R.id.btn_lugares);
		//Para crear un nombre diferente para la foto
		aleatorio = new Double(Math.random() * 100).intValue();
		foto = Environment.getExternalStorageDirectory() + "/imagen"+ aleatorio +".jpg";
		btn_x.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				  
			}
		});
		btn_tomarFoto.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				/*
				 * Creamos un fichero donde guardaremos la foto
				 */
				Uri output = Uri.fromFile(new File(foto));
				intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
				/*
				 * Lanzamos el intenta y recogemos el resultado en onActivityResult
				 */
				startActivityForResult(intent, TAKE_PICTURE); // 1 para la camara, 2 para la galeria
			}
		});
		btn_miUbicacion.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent perfil_mapa = new Intent(PerfilUsuarioActivity.this, MapaActivity.class);
				startActivity(perfil_mapa);
				
			}
		});
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		ImageView iv = (ImageView) findViewById(R.id.img_usuario);
		iv.setImageBitmap(BitmapFactory.decodeFile(foto));

		File file = new File(foto);
		if (file.exists()) {
			UploaderFoto nuevaTarea = new UploaderFoto();
			nuevaTarea.execute(foto);
		}
		else
			Toast.makeText(getApplicationContext(), "No se ha realizado la foto", Toast.LENGTH_SHORT).show();

	}
	
	/*
	 * Clase asincrona para subir la foto
	 */
	class UploaderFoto extends AsyncTask<String, Void, Void>{

		ProgressDialog pDialog;
		String miFoto = "";
		
		@Override
		protected Void doInBackground(String... params) {
			miFoto = params[0];
			try { 
				HttpClient httpclient = new DefaultHttpClient();
				httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
				HttpPost httppost = new HttpPost("http://www.delexltda.cl/up.php");
				File file = new File(miFoto);
				MultipartEntity mpEntity = new MultipartEntity();
				ContentBody foto = new FileBody(file, "image/jpeg");
				mpEntity.addPart("fotoUp", foto);
				httppost.setEntity(mpEntity);
				httpclient.execute(httppost);
				httpclient.getConnectionManager().shutdown();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(PerfilUsuarioActivity.this);
	        pDialog.setMessage("Subiendo la imagen, espere." );
	        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	        pDialog.setCancelable(true);
	        pDialog.show();
		}
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			pDialog.dismiss();
		}
	}
	
		 
		
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.perfil_usuario, menu);
		return true;
	}

}
