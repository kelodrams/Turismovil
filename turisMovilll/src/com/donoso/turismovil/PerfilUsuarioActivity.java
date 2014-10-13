package com.donoso.turismovil;

import java.io.File;

import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.donoso.bd.ConexionMysql;
import com.donoso.turismovil.inertphoto.asynclogin;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.turismovil.clima.Location;
import com.turismovil.clima.Weather;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class PerfilUsuarioActivity extends Activity {
	
	
	Button btnCamara;
	private String foto;
	private static int TAKE_PICTURE = 1;
	double aleatorio = 0;
	String url = "http://www.delexltda.cl/TURISMOVIL/up.php";
	public double lat;
	public double lng;
	String nombreFoto;
	ConexionMysql con = new ConexionMysql();
	//clima
	private TextView cityText;
	private TextView condDescr;
	private TextView temp;
	private TextView press;
	private TextView windSpeed;
	private TextView windDeg;
	 public GPSTracker gps;
	private TextView hum;
	private ImageView imgView;
	/////////

	@Override   
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_perfil_usuario);
		gps = new GPSTracker(PerfilUsuarioActivity.this);
		if(gps.canGetLocation)
		{
			 lat = gps.latitude;
			 lng = gps.longitude;
		}else
		{
			gps.showSettingsAlert();
		}
		try
		{
			Geocoder geocoder = new Geocoder(getApplicationContext());
			List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
			Address address = addresses.get(0);
			String x = address.getCountryName();
			String y =address.getAddressLine(0);
			String z =address.getAddressLine(1);
			String aa =address.getAddressLine(2);
			Toast.makeText(getApplicationContext(), y+x+aa, Toast.LENGTH_LONG).show();
			Toast.makeText(getApplicationContext(), foto, Toast.LENGTH_LONG).show();
			Toast.makeText(getApplicationContext(), lat+"", Toast.LENGTH_LONG).show();
			
			
		}catch(Exception e)
		{
			Toast.makeText(getApplicationContext(), "Error en : "+e.getMessage(), Toast.LENGTH_LONG).show();
		}
		
        String city = "Santiago, Chile";
        
		cityText = (TextView) findViewById(R.id.cityText);
		condDescr = (TextView) findViewById(R.id.condDescr);
		temp = (TextView) findViewById(R.id.temp);
		hum = (TextView) findViewById(R.id.hum);
		press = (TextView) findViewById(R.id.press);
		windSpeed = (TextView) findViewById(R.id.windSpeed);
		windDeg = (TextView) findViewById(R.id.windDeg);
		imgView = (ImageView) findViewById(R.id.condIcon);
		
		JSONWeatherTask task = new JSONWeatherTask();
		task.execute(new String[]{city});
		
		
		final Button btn_miUbicacion = (Button)findViewById(R.id.btn_miUbicacion);
		final Button btn_tomarFoto = (Button)findViewById(R.id.btn_tomarFoto);
		final Button btn_x = (Button)findViewById(R.id.btn_lugares);
		//Para crear un nombre diferente para la foto
		aleatorio = new Double(Math.random() * 100).intValue();
		foto = Environment.getExternalStorageDirectory() + "/turisMobile"+ aleatorio +".jpg";
		nombreFoto = foto.replace("/storage/emulated/0", "http://www.delexltda.cl/fotos");
		
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
	public boolean addFoto(String urll, String lat, String lng)
	{
		
		new asynclogin().execute(urll,lat,lng);
		
		return true;
	}
	public boolean loginstatus(String url ,String longitud, String latitude ) {
    	int logstatus=-1;
    	
    	/*Creamos un ArrayList del tipo nombre valor para agregar los datos recibidos por los parametros anteriores
    	 * y enviarlo mediante POST a nuestro sistema para relizar la validacion*/ 
    	ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
     		String urll = "http://www.delexltda.cl/TURISMOVIL/agregarFoto.php";
		    		postparameters2send.add(new BasicNameValuePair("url",url));
		    		postparameters2send.add(new BasicNameValuePair("lat",longitud));
		    		postparameters2send.add(new BasicNameValuePair("lng",latitude));
		    		
		   //realizamos una peticion y como respuesta obtenes un array JSON
      		JSONArray jdata=con.getserverdata(postparameters2send, urll);
      		
      		/*como estamos trabajando de manera local el ida y vuelta sera casi inmediato
      		 * para darle un poco realismo decimos que el proceso se pare por unos segundos para poder
      		 * observar el progressdialog
      		 * la podemos eliminar si queremos
      		 */
		    
		    		
		    //si lo que obtuvimos no es null
		    	if (jdata!=null && jdata.length() > 0){

		    		JSONObject json_data; //creamos un objeto JSON
					try {
						json_data = jdata.getJSONObject(0); //leemos el primer segmento en nuestro caso el unico
						System.out.println(json_data);
						 logstatus=json_data.getInt("logstatus");//accedemos al valor 
						 Log.e("loginstatus","logstatus= "+logstatus);//muestro por log que obtuvimos
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}		            
		             
					//validamos el valor obtenido
		    		 if (logstatus==0){// [{"logstatus":"0"}] 
		    			 Log.e("loginstatus ", "invalido");
		    			 return false;
		    		 }
		    		 else{// [{"logstatus":"1"}]
		    			 Log.e("loginstatus ", "valido");
		    			 return true;
		    		 }
		    		 
			  }else{	//json obtenido invalido verificar parte WEB.
		    			 Log.e("JSON  ", "ERROR");
			    		return false;
			  }
    	
    }


	    
	/*		CLASE ASYNCTASK
	 * 
	 * usaremos esta para poder mostrar el dialogo de progreso mientras enviamos y obtenemos los datos
	 * podria hacerse lo mismo sin usar esto pero si el tiempo de respuesta es demasiado lo que podria ocurrir    
	 * si la conexion es lenta o el servidor tarda en responder la aplicacion sera inestable.
	 * ademas observariamos el mensaje de que la app no responde.     
	 */
	    
	     class asynclogin extends AsyncTask< String, String, String > {
	    	 
	    	String url,latitude,longitud;
	        protected void onPreExecute() {
	        	//para el progress dialog
	         
	        }
	 
			protected String doInBackground(String... params) {
				//obtnemos usr y pass
				url=params[0];
				latitude=params[1];
				longitud = params[2];
	            
				//enviamos y recibimos y analizamos los datos en segundo plano.
	    		if (loginstatus(url,latitude,longitud)==true){    		    		
	    			return "ok"; //login valido
	    		}else{    		
	    			return "err"; //login invalido     	          	  
	    		}
	        	
			}
	       
			/*Una vez terminado doInBackground segun lo que halla ocurrido 
			pasamos a la sig. activity
			o mostramos error*/
	        protected void onPostExecute(String result) {

	           //pdialog.dismiss();//ocultamos progess dialog.
	           Log.e("onPostExecute=",""+result);
	           
	           if (result.equals("ok")){

					//Intent i=new Intent(RegistroActivity.this, LoginActivity.class);
					//i.putExtra("user",user);
					//startActivity(i); 
					
	            }else{
	            	//err_login();
	            }
	            
	                									}
	        
			
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
				String latt = lat+"";
				String lngg = lng+"";
				new asynclogin().execute(nombreFoto,latt,lngg);
				
				
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
	
private class JSONWeatherTask extends AsyncTask<String, Void, Weather> {
		
		@Override
		protected Weather doInBackground(String... params) {
			Weather weather = new Weather();
			String data = ( (new WeatherHttpClient()).getWeatherData(params[0]));

			try {
				weather = JSONWeatherParser.getWeather(data);
				
				// Let's retrieve the icon
				weather.iconData = ( (new WeatherHttpClient()).getImage(weather.currentCondition.getIcon()));
				
			} catch (JSONException e) {				
				e.printStackTrace();
			}
			return weather;
		
	}
		
		
		
		
	@Override
		protected void onPostExecute(Weather weather) {			
			super.onPostExecute(weather);
			
			if (weather.iconData != null && weather.iconData.length > 0) {
				Bitmap img = BitmapFactory.decodeByteArray(weather.iconData, 0, weather.iconData.length); 
				imgView.setImageBitmap(img);
			}
			
			cityText.setText(weather.location.getCity() + "," + weather.location.getCountry());
			condDescr.setText(weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescr() + ")");
			temp.setText("" + Math.round((weather.temperature.getTemp() - 273.15)) + "°C");
			hum.setText("" + weather.currentCondition.getHumidity() + "%");
			press.setText("" + weather.currentCondition.getPressure() + " hPa");
			windSpeed.setText("" + weather.wind.getSpeed() + " mps");
			windDeg.setText("" + weather.wind.getDeg() + "ï¿½");
				
		}
	





	
  }

}
