package br.newvisionti.vendas;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements Runnable {

	ProgressDialog pgd;
	Cursor cursor;
	SQLiteDatabase db;
	String error;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		SQLiteDatabase db = openOrCreateDatabase("Vendas.db",
				Context.MODE_PRIVATE, null);

		StringBuilder sqlProduto = new StringBuilder();
		sqlProduto.append("CREATE TABLE IF NOT EXISTS [Produtos](");
		sqlProduto.append("[_id] INTEGER PRIMARY KEY AUTOINCREMENT,");
		sqlProduto.append("nome VARCHAR(100),");
		sqlProduto.append("preco DOUBLE[10,2]);");
		db.execSQL(sqlProduto.toString());

		//db.execSQL("INSERT INTO Produtos (nome,preco) VALUES ('Agua com gás', '2.50')");
		//db.execSQL("INSERT INTO Produtos (nome,preco) VALUES ('Coca Cola', '3,75')");

		StringBuilder sqlVendas = new StringBuilder();
		sqlVendas.append("CREATE TABLE IF NOT EXISTS [Vendas](");
		sqlVendas.append("[_id] INTEGER PRIMARY KEY AUTOINCREMENT,");
		sqlVendas.append("produto INTEGER,");
		sqlVendas.append("preco DOUBLE[10,2],");
		sqlVendas.append("la DOUBLE[10,9],");
		sqlVendas.append("lo DOUBLE[10,9]);");
		db.execSQL(sqlVendas.toString());

		db.close();
	}

	public void onResume() {
		super.onRestart();

		TextView txtStatusConexao = (TextView) findViewById(R.id.txtStatusConexao);

		ConnectivityManager conn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		if (conn.getNetworkInfo(0).isConnected()) {
			txtStatusConexao.setText("Status da conexão: 3G.");
		} else if (conn.getNetworkInfo(1).isConnected()) {
			txtStatusConexao.setText("Status da conexão: Wifi.");
		} else {
			((Button) findViewById(R.id.bttIniciarReplicar)).setEnabled(false);
			txtStatusConexao.setText("Status da conexão: desconectado.");
		}
	}

	public void onClickNovaVenda(View v) {
		startActivity(new Intent(getBaseContext(), NovaVendaActivity.class));

	}

	public void onClickListarVenda(View v) {
		startActivity(new Intent(getBaseContext(), ListarVendasActivity.class));

	}

	public void onClickIniciarReplicacao(View v) {
		// startService É usado para chamar um SERVICE
		//Toast.makeText(getBaseContext(), "Iniciar", Toast.LENGTH_LONG).show();
		//startService(new Intent("REPLICACAO"));
		db = openOrCreateDatabase("Vendas.db", Context.MODE_PRIVATE, null);

		cursor = db.rawQuery("SELECT * FROM vendas", null);

		pgd = new ProgressDialog(MainActivity.this);
		pgd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pgd.setCancelable(true);
		pgd.setTitle("Replicando dados");
		pgd.setMax(cursor.getCount());
		pgd.show();
		
		new Thread(MainActivity.this).start();
		//Toast.makeText(getBaseContext(), "Fim", Toast.LENGTH_LONG).show();

	}

	@Override
	public void run() {
		try {
			int totalDB = cursor.getCount();
			int totalReplicado = 0;


			//Toast.makeText(getBaseContext(), "Aqui também..", Toast.LENGTH_LONG)
			//		.show();
			
			while (cursor.moveToNext()) {
				StringBuilder strURL = new StringBuilder();
				strURL.append("http://127.0.0.1:8080/vendas/inserir.php?produto=");
				strURL.append(cursor.getInt(cursor.getColumnIndex("produto")));
				strURL.append("$preco=");
				strURL.append(cursor.getDouble(cursor.getColumnIndex("preco")));
				strURL.append("$latitude=");
				strURL.append(cursor.getDouble(cursor.getColumnIndex("la")));
				strURL.append("$longitude=");
				strURL.append(cursor.getDouble(cursor.getColumnIndex("lo")));

				URL url = new URL(strURL.toString());
				HttpURLConnection http = (HttpURLConnection) url
						.openConnection();

				InputStreamReader ips = new InputStreamReader(
						http.getInputStream());
				BufferedReader line = new BufferedReader(ips);

				String lineRetorno = line.readLine();

				if (lineRetorno.equals("Y")) {
					db.delete("vendas", "_id=?",
							new String[] { String.valueOf(cursor.getInt(0)) });
					totalReplicado++;
					h1.sendEmptyMessage(0);
				}

				//NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				//Notification nt = null;

				//nt.vibrate = new long[] { (int) Math.round(Math.random()), 100,
				//		2000, 1000, 2000 };
				//notificationManager.notify((int) Math.round(Math.random()), nt);

				db.close();

			}
			if (totalDB == totalReplicado) {
				h1.sendEmptyMessage(1);
			}else{
				error = "Ocorreu algum erro no sistema.";
				h1.sendEmptyMessage(2);
			}
			
		} catch (Exception e) {
			//Log.d("Replicação:", e.getMessage());
			//Toast.makeText(getBaseContext(), "Erro na replicação.", Toast.LENGTH_LONG).show();
			error = e.getMessage();
			h1.sendEmptyMessage(2);
		}
	}

	public Handler h1 = new Handler() {
		
		public void handleMessage(Message msg) {
			if (msg.what == 0){
				pgd.setProgress(pgd.getProgress() + 1);
			}else if (msg.what == 1){
				pgd.dismiss();
				Toast.makeText(MainActivity.this, "Sucesso na replicação!", Toast.LENGTH_LONG).show();
			}else if (msg.what == 2){
				pgd.dismiss();
				Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();
			}
			
		};
	};

}
