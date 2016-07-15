package br.newvisionti.vendas;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ExemploBroadCast extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Toast.makeText(context, "Executou o broad cast.", Toast.LENGTH_LONG).show();
		
		//if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
			
		//}
		
	}

}
