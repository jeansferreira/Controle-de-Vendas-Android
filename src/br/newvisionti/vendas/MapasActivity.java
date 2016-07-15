package br.newvisionti.vendas;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class MapasActivity extends MapActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mapa);
		
		MapView map = (MapView)findViewById(R.id.mapview);
		
		map.setBuiltInZoomControls(true);
		map.displayZoomControls(true);
		
		Intent it = getIntent();

		int latitude = (int)(it.getDoubleExtra("latitude",0)*1E6);
		int longitude = (int)(it.getDoubleExtra("longitude",0)*1E6);
		
		MapController mc = map.getController();
		mc.animateTo(new GeoPoint(latitude, longitude));
		
		//Valor padrão para dar zoom na tela
		mc.setZoom(30);
		
		map.invalidate();
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
