package pemapmodder.modcreator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ImportMethodsService extends Service {
	ImportMethodsTask task = null;
	@Override
	public void onCreate(){
		task = new ImportMethodsTask();
		task.execute(this);
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		return START_NOT_STICKY;
	}
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
