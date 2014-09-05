package pemapmodder.modcreator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pemapmodder.modcreator.objects.Function;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;

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
	public static List<Function> parseMethods(Context ctx){
		File dumpFile = getMethodDumpFile(ctx);
		if(!dumpFile.isFile()){
			Toast.makeText(ctx, R.string.dump_file_absent, Toast.LENGTH_SHORT).show();
			Toast.makeText(ctx, R.string.dump_file_absent_suggestion, Toast.LENGTH_SHORT).show();
		}
		BufferedReader is = null;
		try{
			List<Function> fxs = new ArrayList<Function>();
			is = new BufferedReader(new InputStreamReader(new FileInputStream(dumpFile)));
			String line;
			while((line = is.readLine()) != null){
				String returnType = line.substring(0, line.indexOf(" "));
				line = line.substring(returnType.length() + 1);
				String fullName = line.substring(0, line.indexOf(":"));
				line = line.substring(fullName.length() + 1);
				String[] argsRaw = line.split(",");
				String namespace = fullName.substring(0, fullName.lastIndexOf(".") + 1); // safely returns "" if none
				String name = fullName.substring(namespace.length()); // safely returns the full string if no namespace
				Map<String, String> args = new ArrayMap<String, String>(argsRaw.length);
				for(String arg: argsRaw){
					String[] tokens = arg.split(" ");
					args.put(tokens[1], tokens[0]);
				}
				fxs.add(new Function(namespace, name, args, returnType));
			}
			is.close();
			return fxs;
		}
		catch(IOException e){
			e(e);
			if(is != null){
				try{
					is.close();
				}
				catch(Exception ex){
					e(ex);
				}
			}
			return null;
		}
	}
	public static void e(Throwable t){
		Log.d(DEBUG_SRC, "Unexpected exception caught", t);
		t.printStackTrace();
	}
}
