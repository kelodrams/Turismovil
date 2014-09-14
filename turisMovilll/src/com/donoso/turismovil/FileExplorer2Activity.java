package com.donoso.turismovil;

import java.io.File;
import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;

public class FileExplorer2Activity extends Activity {

	private Button open_file_explorer;
	private Button upload;
	private TextView archivo;	
	private String archivo_seleccionado;
	private String archivo_nombre;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.foto_file);
		
		archivo = (TextView)findViewById(R.id.txtNombreArchivo);
		
		open_file_explorer = (Button)findViewById(R.id.btnBuscar);
		open_file_explorer.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				Intent file_explorer = new Intent(FileExplorer2Activity.this,FileExplorerActivity.class);
				startActivityForResult(file_explorer, 555);
			}
		});
		upload = (Button)findViewById(R.id.btnSubir);
		upload.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {				
				if(archivo_seleccionado==null || archivo_seleccionado.equals("")){
					Toast.makeText(FileExplorer2Activity.this, "Selecciona un archivo",
					        Toast.LENGTH_SHORT).show();
				}else{					
					try{	
						new UploadFileTask(FileExplorer2Activity.this).execute(archivo_seleccionado);
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}					
				}				
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  if (resultCode == RESULT_OK && requestCode == 555) {
	    if (data.hasExtra("archivo_seleccionado")) {
	      archivo_seleccionado = data.getExtras().getString("archivo_seleccionado");
	      archivo_nombre = data.getExtras().getString("archivo_nombre");
	      archivo.setText(archivo_nombre);
	    }
	  }
	} 
	
	
}

//http://stackoverflow.com/questions/2017414/post-multipart-request-with-android-sdk
class UploadFileTask extends AsyncTask<String,Void,String> {

	private Activity activity;
	
	public UploadFileTask(Activity activity){
		this.activity = activity;
	}
	
    protected String doInBackground(String... archivo) {
    	String result = null;
    	try {

        	HttpClient httpclient = new DefaultHttpClient();
            httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

            HttpPost httppost = new HttpPost("http://192.168.1.10/nanoupload/upload.php");
            File file = new File(archivo[0]);
            
            MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);  
            multipartEntity.addPart("archivo", new FileBody(file));

            httppost.setEntity(multipartEntity);
            result = httpclient.execute(httppost, new FileUploadResponseHandler());
        	
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		return result;
    }

    protected void onPostExecute(String feed) {
    	Toast.makeText(activity, feed,
		        Toast.LENGTH_SHORT).show();
    }
 }

 class FileUploadResponseHandler implements ResponseHandler {

    @Override
    public Object handleResponse(HttpResponse response)
            throws ClientProtocolException, IOException {

        HttpEntity r_entity = response.getEntity();
        String responseString = EntityUtils.toString(r_entity);
        Log.d("UPLOAD", responseString);

        return responseString;
    }

}

