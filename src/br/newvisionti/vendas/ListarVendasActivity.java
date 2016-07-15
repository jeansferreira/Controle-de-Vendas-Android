package br.newvisionti.vendas;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ListarVendasActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listar_vendas);
		
		SQLiteDatabase db = openOrCreateDatabase("Vendas.db", Context.MODE_PRIVATE, null);
		ListView lw = (ListView)findViewById(R.id.ltwVendas);
		
		Cursor cursor = db.rawQuery("SELECT vendas._id, vendas.preco, produtos.nome, vendas.la, vendas.lo"+
				" FROM vendas INNER JOIN produtos on produtos._id = vendas.produto", null);
		
		String[] from = {"_id","nome","preco","la","lo"};
		int[] to = {R.id.txtId, R.id.txtNome, R.id.txtPreco, R.id.txtLa,R.id.txtLo};
		
		SimpleCursorAdapter ad = new SimpleCursorAdapter(getBaseContext(), R.layout.model_listar_vendas, cursor, from, to);
		lw.setAdapter(ad);

		//
		/*
		 *TODO Recuperando o click na lista 
		 */
		lw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView ad, View v, int posicao,long id) {
				SQLiteCursor c = (SQLiteCursor) ad.getAdapter().getItem(posicao);
				Intent it = new Intent(getBaseContext(), MapasActivity.class);
				it.putExtra("latitude", c.getDouble(c.getColumnIndex("la")));
				it.putExtra("longitude", c.getDouble(c.getColumnIndex("lo")));
				
				startActivity(it);
			}
		});
		
		
		
		db.close();
		
	}

}
