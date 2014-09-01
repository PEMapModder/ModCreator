package pemapmodder.modcreator;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

public abstract class Utils{
	public final static String SHARED_PREFERENCES_NAME = "pemapmodder.modcreator.SharedPreferences";
	public final static String PREF_DIR = "app.dir";
	public final static String METHOD_DUMP_FILE = "method-dump.txt";
	public final static String DEBUG_SRC = "pemapmodder.modcreator";
	public static File getAppFile(Context ctx){
		File defaultFile = new File(Environment.getExternalStorageDirectory(), "pemapmodder.modcreator");
		SharedPreferences pref = ctx.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		File ret = new File(pref.getString(PREF_DIR, defaultFile.getAbsolutePath()));
		ret.mkdirs();
		return ret;
	}
	public static File getProjectsDir(Context ctx){
		return new File(getAppFile(ctx), "projects/");
	}
	public static File getMethodDumpFile(Context ctx){
		return new File(getAppFile(ctx), METHOD_DUMP_FILE);
	}
	public static void startImport(Context ctx){
		ctx.startService(new Intent(ctx, ImportMethodsService.class));
	}
	public static void e(Throwable t){
		Log.d(DEBUG_SRC, "Unexpected exception caught", t);
		t.printStackTrace();
	}
}
