package com.donoso.turismovil;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.donoso.bd.ConexionMysql;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class ImagenesActivity extends Activity {
    
    ListView list;
    LazyAdapter adapter;
    String[] mStrings;
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
        List<NameValuePair> lista=new ArrayList<NameValuePair>();
        ConexionMysql jParse=new ConexionMysql();
        String URL="http://delexltda.cl/TURISMOVIL/showPic.php";
		JSONObject json=jParse.recibir(URL, "get", lista);
		Toast.makeText(getApplicationContext(), ""+json, Toast.LENGTH_LONG).show();
		Toast.makeText(getApplicationContext(), ""+json, Toast.LENGTH_LONG).show();
		String[] aux = null;
		    
		try
		{
		    
			JSONArray productos = null;
			productos=json.getJSONArray("fotos");
			String[] datos=new String[productos.length()];
			Toast.makeText(getApplicationContext(), productos.length() + " Productos existentes", Toast.LENGTH_LONG).show();
			 mStrings = new String[productos.length()];
			aux = new String[productos.length()];
			for(int t=0;t<productos.length();t++){
				
				
				JSONObject c=productos.getJSONObject(t);
				String url = c.getString("url");
				Toast.makeText(getApplicationContext(), url, Toast.LENGTH_LONG).show();
				aux[t] = url;
				
				//Toast.makeText(getApplicationContext(), nombre + "el nombre es "+latitud+longitud, Toast.LENGTH_LONG).show();
			
			}   
		}catch(JSONException e)
		{
			Log.e("Error en : ", e.getMessage());
		}
        mStrings = aux;
        list=(ListView)findViewById(R.id.list);
        adapter=new LazyAdapter(this, mStrings);
        list.setAdapter(adapter);
        
        Button b=(Button)findViewById(R.id.button1);
        b.setOnClickListener(listener);
    }
    
    @Override
    public void onDestroy()
    {
        list.setAdapter(null);
        super.onDestroy();
    }
    
    public OnClickListener listener=new OnClickListener(){
        @Override
        public void onClick(View arg0) {
            adapter.imageLoader.clearCache();
            adapter.notifyDataSetChanged();
        }
    };
    
    
}