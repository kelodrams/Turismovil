package com.donoso.turismovil;





import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.json.*;

import android.graphics.Color;
import android.location.*;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.SyncStateContract.Constants;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.donoso.bd.ConexionMysql;
import com.donoso.turismovil.GPSTracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapaActivity extends android.support.v4.app.FragmentActivity  implements  OnMapLongClickListener, LocationListener, OnMyLocationChangeListener{
  
	
	private static final LatLng AMSTERDAM = new LatLng(52.37518, 4.895439);
	private static final LatLng PARIS = new LatLng(48.856132, 2.352448);
	private static final LatLng FRANKFURT = new LatLng(50.111772, 8.682632);
	private GoogleMap map;
	private LatLng miPosicion;
	private SupportMapFragment fragment;
	private LatLngBounds latlngBounds;
	private Button bNavigation;
	private Polyline newPolyline;
	private boolean isTravelingToParis = false;
	private int width, height;
	private Marker markers;
	private LatLng pos;
	private LatLng posx;
	
	
	 private LocationManager locationManager;
	 GPSTracker gps;
	
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD) @SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_navigation);
		gps = new GPSTracker(MapaActivity.this);
		getSreenDimanstions();
	    fragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
	    
		map = fragment.getMap(); 	
		  
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

			StrictMode.setThreadPolicy(policy); 
		bNavigation = (Button) findViewById(R.id.bNavigation);
		
		if(gps.canGetLocation)
		{
			  map.addMarker(new MarkerOptions().position(new LatLng(gps.getLatitude(), gps.getLongitude())).title("Start"));
	            CameraUpdate center= CameraUpdateFactory.newLatLng(new LatLng(gps.getLatitude(), gps.getLongitude()));
	                CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);
	                Toast.makeText(getApplicationContext(),"posicion "+ new LatLng(gps.getLatitude(), gps.getLongitude()), Toast.LENGTH_LONG).show();
	                map.moveCamera(center);
	                map.animateCamera(zoom);
		}else
		{
			gps.showSettingsAlert();
		}
		
		
		 List<NameValuePair> lista=new ArrayList<NameValuePair>();
	        ConexionMysql jParse=new ConexionMysql();
	        String URL="http://www.delexltda.cl/TURISMOVIL/marca.php";
			JSONObject json=jParse.recibir(URL, "get", lista);
			//Toast.makeText(getApplicationContext(), ""+json, Toast.LENGTH_LONG).show();
			try{
				
				    
				
					JSONArray productos = null;
					productos=json.getJSONArray("marcador");
					String[] datos=new String[productos.length()];
					Toast.makeText(getApplicationContext(), productos.length() + " Productos existentes", Toast.LENGTH_LONG).show();
					
					for(int t=0;t<productos.length();t++){
						
						
						JSONObject c=productos.getJSONObject(t);
						String imagen = c.getString("id");
						String nombre=c.getString("titulo");
						String latitud=c.getString("latitud");
						String longitud = c.getString("longitud");
						double lat = Double.parseDouble(latitud);
						double lng = Double.parseDouble(longitud);
						String id = c.getString("tipo");
						map.addMarker(new MarkerOptions()
									  .position(new LatLng(lat,lng))
									  .title(nombre)
						);
						System.out.println(id+"   "+nombre);
						//Toast.makeText(getApplicationContext(), nombre + "el nombre es "+latitud+longitud, Toast.LENGTH_LONG).show();
					}
				
			}
			catch(JSONException error){
				Toast.makeText(getApplicationContext(), "error:"+error.getMessage(), Toast.LENGTH_LONG).show();
			}             
	       
			
			
		  map.setOnMarkerClickListener(new OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(Marker marker) {
					//findDirections( gps.latitude, gps.longitude,markers.getPosition().latitude,markers.getPosition().longitude, GMapV2Direction.MODE_DRIVING );
				Toast.makeText(getApplicationContext(), "Hice click en el marcador", Toast.LENGTH_LONG).show();
				pos = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
				posx = new LatLng(gps.latitude, gps.longitude);
				findDirections( gps.latitude, gps.longitude,marker.getPosition().latitude, marker.getPosition().longitude, GMapV2Direction.MODE_DRIVING );
				
				return false;
			}
		});
		 
	
		 
		
		
		
		
		
		                            
		
		
			
		bNavigation.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (!isTravelingToParis)
				{
					isTravelingToParis = true;
					findDirections( gps.latitude, gps.longitude,PARIS.latitude, PARIS.longitude, GMapV2Direction.MODE_DRIVING );
				}
				else
				{
					isTravelingToParis = false;
					findDirections( AMSTERDAM.latitude, AMSTERDAM.longitude, FRANKFURT.latitude, FRANKFURT.longitude, GMapV2Direction.MODE_DRIVING );  
				}
			}
		});
	}
	
 
 
	public void handleGetDirectionsResult(ArrayList<LatLng> directionPoints) {
		PolylineOptions rectLine = new PolylineOptions().width(5).color(Color.RED);

		for(int i = 0 ; i < directionPoints.size() ; i++) 
		{          
			rectLine.add(directionPoints.get(i));
		}
		if (newPolyline != null)
		{
			newPolyline.remove();
		}
		newPolyline = map.addPolyline(rectLine);
		 
			latlngBounds = createLatLngBoundsObject(pos, posx);
	        map.animateCamera(CameraUpdateFactory.newLatLngBounds(latlngBounds, width, height, 150));
		 
			 
		 
		
	}
	
	private void getSreenDimanstions()
	{
		Display display = getWindowManager().getDefaultDisplay();
		width = display.getWidth(); 
		height = display.getHeight(); 
	}
	
	private LatLngBounds createLatLngBoundsObject(LatLng firstLocation, LatLng secondLocation)
	{
		if (firstLocation != null && secondLocation != null)
		{
			LatLngBounds.Builder builder = new LatLngBounds.Builder();    
			builder.include(firstLocation).include(secondLocation);
			
			return builder.build();
		}
		return null;
	}
	
	public void findDirections(double fromPositionDoubleLat, double fromPositionDoubleLong, double toPositionDoubleLat, double toPositionDoubleLong, String mode)
	{
		Map<String, String> map = new HashMap<String, String>();
		map.put(GetDirectionsAsyncTask.USER_CURRENT_LAT, String.valueOf(fromPositionDoubleLat));
		map.put(GetDirectionsAsyncTask.USER_CURRENT_LONG, String.valueOf(fromPositionDoubleLong));
		map.put(GetDirectionsAsyncTask.DESTINATION_LAT, String.valueOf(toPositionDoubleLat));
		map.put(GetDirectionsAsyncTask.DESTINATION_LONG, String.valueOf(toPositionDoubleLong));
		map.put(GetDirectionsAsyncTask.DIRECTIONS_MODE, mode);
		
		GetDirectionsAsyncTask asyncTask = new GetDirectionsAsyncTask(this);
		asyncTask.execute(map);	
	}

	@Override
	public void onMapLongClick(LatLng point) {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "Mantuve apretado", Toast.LENGTH_LONG).show();
	}

	 
	public void onMapClick(LatLng point) {
		
		Toast.makeText(getApplicationContext(), "Hice click en el marcador", Toast.LENGTH_LONG).show();
		
	}
	
	
	 
private void obtenerItems(GoogleMap map) {
        
        
        List<NameValuePair> lista=new ArrayList<NameValuePair>();
        ConexionMysql jParse=new ConexionMysql();
        String URL="http://www.delexltda.cl/TURISMOVIL/marca.php";
		JSONObject json=jParse.recibir(URL, "get", lista);
		try{
			
			    
				
				JSONArray productos=null;
				productos=json.getJSONArray("marcador");
				String[] datos=new String[productos.length()];
				Toast.makeText(getApplicationContext(), productos.length() + " Productos existentes", Toast.LENGTH_LONG).show();
				
				for(int t=0;t<productos.length();t++){
					
					
					JSONObject c=productos.getJSONObject(t);
					String imagen = c.getString("id");
					String nombre=c.getString("titulo");
					String latitud=c.getString("latitud");
					String longitud = c.getString("longitud");
					double lat = Double.parseDouble(latitud);
					double lng = Double.parseDouble(longitud);
					String id = c.getString("tipo");
					map.addMarker(new MarkerOptions()
								  .position(new LatLng(lat,lng))
								  .title(nombre)
					);
					System.out.println(id+"   "+nombre);
					//Toast.makeText(getApplicationContext(), nombre + "el nombre es "+latitud+longitud, Toast.LENGTH_LONG).show();
				}
			
		}
		catch(JSONException error){
			Toast.makeText(getApplicationContext(), "error:"+error.getMessage(), Toast.LENGTH_LONG).show();
		}             
      }




@Override
public void onLocationChanged(Location location) {
	  LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
	    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
	    map.animateCamera(cameraUpdate);
	    locationManager.removeUpdates(this);
	
}



@Override
public void onStatusChanged(String provider, int status, Bundle extras) {
	// TODO Auto-generated method stub
	
}



@Override
public void onProviderEnabled(String provider) {
	// TODO Auto-generated method stub
	
}



@Override
public void onProviderDisabled(String provider) {
	// TODO Auto-generated method stub
	
}

/*
private void centerMapOnMyLocation() {

    map.setMyLocationEnabled(true);

    location = map.getMyLocation();

    if (location != null) {
        miPosicion = new LatLng(location.getLatitude(),
                location.getLongitude());
    }
    map.animateCamera(CameraUpdateFactory.newLatLngZoom(miPosicion,
            Const));
}*/

private void myposititon()
{
	 LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
     Criteria criteria = new Criteria();

     Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
     if (location != null)
     {
         map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                 new LatLng(location.getLatitude(), location.getLongitude()), 13));

         CameraPosition cameraPosition = new CameraPosition.Builder()
         .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
         .zoom(17)                   // Sets the zoom
         .bearing(90)                // Sets the orientation of the camera to east
         .tilt(40)                   // Sets the tilt of the camera to 30 degrees
         .build();                   // Creates a CameraPosition from the builder
     map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

     }	

	}



	
private void turnGPSOn() {

    @SuppressWarnings("deprecation")
	String provider = android.provider.Settings.Secure.getString(
            getContentResolver(),
            android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
    if (!provider.contains("gps")) { // if gps is disabled
        final Intent poke = new Intent();
        poke.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
        poke.setData(Uri.parse("3"));
        sendBroadcast(poke);
    }
	
}



@Override
public void onMyLocationChange(Location location) {
	 LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
	    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
	    map.animateCamera(cameraUpdate);
	    locationManager.removeUpdates(this);
		
}




}

