package com.donoso.turismovil;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.donoso.bd.ConexionMysql;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class inertphoto 
{
	
	static ConexionMysql con = new ConexionMysql();
	String ip_servidor = "10.31.117.28";
	String url = "http://www.delexltda.cl/TURISMOVIL/agregarFoto.php";
	boolean resultado;
	private ProgressDialog pdialog;

	public boolean addFoto(String url, String lat, String lng)
	{
		new asynclogin().execute(url,lat,lng);
		
		return true;
	}
	public static boolean loginstatus(String url ,String longitud, String latitude ) {
    	int logstatus=-1;
    	
    	/*Creamos un ArrayList del tipo nombre valor para agregar los datos recibidos por los parametros anteriores
    	 * y enviarlo mediante POST a nuestro sistema para relizar la validacion*/ 
    	ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
     		
		    		postparameters2send.add(new BasicNameValuePair("url",url));
		    		postparameters2send.add(new BasicNameValuePair("longitud",longitud));
		    		postparameters2send.add(new BasicNameValuePair("latitude",latitude));
		    		
		   //realizamos una peticion y como respuesta obtenes un array JSON
      		JSONArray jdata=con.getserverdata(postparameters2send, url);

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
	
	
	
	
	
	
	
	
	
	
	
}
