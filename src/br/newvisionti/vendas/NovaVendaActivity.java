package br.newvisionti.vendas;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class NovaVendaActivity extends Activity implements LocationListener {

	private double lo;
	private double la;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nova_venda);
		
		
		Spinner spProdutos = (Spinner)findViewById(R.id.spProdutos);
		
		SQLiteDatabase db = openOrCreateDatabase("Vendas.db", Context.MODE_PRIVATE, null);
		
		Cursor cursor = db.rawQuery("SELECT * FROM Produtos ORDER BY nome ASC", null);
		
		String[] from = {"_id","nome", "preco"};
		int[] to = {R.id.txtvId, R.id.txtvNome, R.id.txtvPreco};
		
		SimpleCursorAdapter ad = new SimpleCursorAdapter(getBaseContext(), R.layout.spinner, cursor, from, to);
		spProdutos.setAdapter(ad);
		
		db.close();
		
	}

	public void onClickSalvar(View v){
		
		LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		//CRIA UM CRITERIO DA LOCALIZAÇÃO MAIS PROXIMA.
		Criteria criteria = new Criteria();
		String provider = locationManager.getBestProvider(criteria, false);
		//ULTIMA LOCALIZAÇÃO
		Location location = locationManager.getLastKnownLocation(provider);
		la = location.getLatitude();
		lo = location.getLongitude();
		
		SQLiteDatabase db = openOrCreateDatabase("Vendas.db", Context.MODE_PRIVATE, null);

		//VAI RETORNAR O OBJETO SELECIONADO NO SPINNER
		Spinner spProdutos = (Spinner)findViewById(R.id.spProdutos);
		SQLiteCursor dados = (SQLiteCursor)spProdutos.getAdapter().getItem(spProdutos.getSelectedItemPosition());
		
		ContentValues ctv = new ContentValues();
		ctv.put("produto", dados.getInt(0));
		ctv.put("preco", dados.getDouble(2));
		ctv.put("la", la);
		ctv.put("lo", lo);
		
		if (db.insert("vendas", "_id", ctv) > 0){
			Toast.makeText(getBaseContext(), "Sucesso em inserir a venda!", Toast.LENGTH_LONG).show();
			finish();
		}else{
			Toast.makeText(getBaseContext(), "Erro em inserir a venda!", Toast.LENGTH_LONG).show();
		}
	}
	
	//metodo que obtem a localização de tempos em tempos.
	@Override
	public void onLocationChanged(Location arg0) {
		//la = arg0.getLatitude();
		//lo = arg0.getLongitude();
		
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}

}
