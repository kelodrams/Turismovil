package com.donoso.turismovil;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.donoso.bd.ConexionMysql;

public class RegistroActivity extends Activity {
	
	ConexionMysql con = new ConexionMysql();
	String ip_servidor = "10.31.117.28";
	String url = "http://www.delexltda.cl/TURISMOVIL/adduser.php";
	boolean resultado;
	private ProgressDialog pdialog;
	private static final String PATTERN_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registro);
		final TextView correo = (TextView)findViewById(R.id.txt_correo);
		final TextView pass = (TextView)findViewById(R.id.txt_reg_pass);
		final Button registrar = (Button)findViewById(R.id.btn_ingresar);
		final TextView passd = (TextView)findViewById(R.id.txt_reg_repetirPass);
		registrar.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				String us = correo.getText().toString();
				String pas = pass.getText().toString();
				String pasd = passd.getText().toString();
				if(validateEmail(us))
				{
					if(us.trim().length()>5)
					{
						if(contraseñaCumpleReglas(pas))
						{
							if(pas.trim().length()>5)
							{
						
								if(pas.equals(pasd))
								{
					
									if(checklogindata(us, pas, pasd))
									{
					
										new asynclogin().execute(us,pas);
					
									}
									else
									{
										err_login();
									}
				
			}//else de las contraseñas iguales
						else
						{
							Toast.makeText(getApplicationContext(), "Password no iguales", Toast.LENGTH_LONG).show();
							}
						}//else de la expression regular de la pass
						else{Toast.makeText(getApplicationContext(), "Contraseña debe contener a lo más 6 digitos", Toast.LENGTH_LONG).show();
						}
						}//else de pass >5
					else{Toast.makeText(getApplicationContext(), "Contraseña debe contener 1 digito, mayuscula, minuscula y a lo más 20 caracteres", Toast.LENGTH_LONG).show();}
		}else
		{
			Toast.makeText(getApplicationContext(), "Usuario debe tener más de 6 caracteres", Toast.LENGTH_LONG).show();
		}}else{Toast.makeText(getApplicationContext(), "Email invalido", Toast.LENGTH_LONG).show();}}});
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.registro, menu);
		return true;
	}
	
	 public boolean loginstatus(String username ,String password ) {
	    	int logstatus=-1;
	    	
	    	/*Creamos un ArrayList del tipo nombre valor para agregar los datos recibidos por los parametros anteriores
	    	 * y enviarlo mediante POST a nuestro sistema para relizar la validacion*/ 
	    	ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
	     		
			    		postparameters2send.add(new BasicNameValuePair("usuario",username));
			    		postparameters2send.add(new BasicNameValuePair("password",password));
			    		
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
	 public boolean checklogindata(String username ,String password, String passwordd ){
	    	
		    if 	(username.equals("") || password.equals("") && password!=passwordd){
		    	Log.e("Login ui", "checklogindata user or pass error");
		    return false;
		    
		    }else{
		    	
		    	return true;
		    }
		    
		}
	 public void err_login(){
	    	//Vibrator vibrator =(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		    //vibrator.vibrate(200);
		    Toast toast1 = Toast.makeText(getApplicationContext(),"Error:Usuario Existente", Toast.LENGTH_LONG);
	 	    toast1.show();    	
	    }
		    
		/*		CLASE ASYNCTASK
		 * 
		 * usaremos esta para poder mostrar el dialogo de progreso mientras enviamos y obtenemos los datos
		 * podria hacerse lo mismo sin usar esto pero si el tiempo de respuesta es demasiado lo que podria ocurrir    
		 * si la conexion es lenta o el servidor tarda en responder la aplicacion sera inestable.
		 * ademas observariamos el mensaje de que la app no responde.     
		 */
		    
		    class asynclogin extends AsyncTask< String, String, String > {
		    	 
		    	String user,pass;
		        protected void onPreExecute() {
		        	//para el progress dialog
		            pdialog = new ProgressDialog(RegistroActivity.this);
		            pdialog.setMessage("Registrando....");
		            pdialog.setIndeterminate(false);
		            pdialog.setCancelable(false);
		            pdialog.show();
		        }
		 
				protected String doInBackground(String... params) {
					//obtnemos usr y pass
					user=params[0];
					pass=params[1];
		            
					//enviamos y recibimos y analizamos los datos en segundo plano.
		    		if (loginstatus(user,pass)==true){    		    		
		    			return "ok"; //login valido
		    		}else{    		
		    			return "err"; //login invalido     	          	  
		    		}
		        	
				}
		       
				/*Una vez terminado doInBackground segun lo que halla ocurrido 
				pasamos a la sig. activity
				o mostramos error*/
		        protected void onPostExecute(String result) {

		           pdialog.dismiss();//ocultamos progess dialog.
		           Log.e("onPostExecute=",""+result);
		           
		           if (result.equals("ok")){

						Intent i=new Intent(RegistroActivity.this, LoginActivity.class);
						i.putExtra("user",user);
						Toast.makeText(getApplicationContext(), "Registrado", Toast.LENGTH_LONG).show();
						startActivity(i); 
						
		            }else{
		            	err_login();
		            }
		            
		                									}
		        
				
		        }
		    
		    public static boolean validateEmail(String email) {
		    	 
		        // Compiles the given regular expression into a pattern.
		        Pattern pattern = Pattern.compile(PATTERN_EMAIL);
		 
		        // Match the given input against this pattern
		        Matcher matcher = pattern.matcher(email);
		        return matcher.matches();
		 
		    }
		    public boolean contraseñaCumpleReglas(String pass) {
		        Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,20}$");
		        Matcher matcher = pattern.matcher(pass);
		         if (!matcher.find()) {
		             System.out.println("La contraseña tiene que tener al menos un dígito.");
		             return false;
		         }              
		         return true;
		 }


}
