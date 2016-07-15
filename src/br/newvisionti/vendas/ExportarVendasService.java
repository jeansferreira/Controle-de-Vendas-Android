package br.newvisionti.vendas;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class ExportarVendasService extends Service implements Runnable {

	public void oncreate() {
		Toast.makeText(getBaseContext(), "Aqui tb..", Toast.LENGTH_LONG).show();
		new Thread(ExportarVendasService.this).start();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void run() {

		try {
			SQLiteDatabase db = openOrCreateDatabase("Vendas.db",
					Context.MODE_PRIVATE, null);

			Cursor cursor = db.rawQuery("SELECT * FROM vendas", null);
			int totalDB = cursor.getCount();
			int totalReplicado = 0;

			Toast.makeText(getBaseContext(), "Aqui também..", Toast.LENGTH_LONG)
					.show();

			while (cursor.moveToNext()) {
				
				//http://127.0.0.1:8080/inserir.php?produto=1&preco=2.50&latitude=13131&longitude=435211
				
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
				}

				NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				Notification nt = null;

				if (totalDB == totalReplicado) {
					nt = new Notification(R.drawable.ic_launcher,
							"Status Replicação", System.currentTimeMillis());
					nt.flags |= Notification.FLAG_AUTO_CANCEL;
					PendingIntent pi = PendingIntent.getActivity(this, 0,
							new Intent(this.getApplicationContext(),
									MainActivity.class), 0);
					nt.setLatestEventInfo(this, "Status Replícação",
							"A replicação foi feita com sucesso, total:"
									+ totalReplicado, pi);
				} else {
					nt = new Notification(R.drawable.ic_launcher,
							"Status Replicação", System.currentTimeMillis());
					nt.flags |= Notification.FLAG_AUTO_CANCEL;
					PendingIntent pi = PendingIntent.getActivity(this, 0,
							new Intent(this.getApplicationContext(),
									MainActivity.class), 0);
					nt.setLatestEventInfo(this, "Status Replícação",
							"A replicação não foi feita com sucesso, total:"
									+ totalReplicado + " de " + totalDB, pi);
				}

				nt.vibrate = new long[] { (int) Math.round(Math.random()), 100,
						2000, 1000, 2000 };
				notificationManager.notify((int) Math.round(Math.random()), nt);

				db.close();

			}

		} catch (Exception e) {
			Log.d("Replicação:", e.getMessage());
			Toast.makeText(getBaseContext(), "Erro na replicação.",
					Toast.LENGTH_LONG).show();
		}

	}
}
