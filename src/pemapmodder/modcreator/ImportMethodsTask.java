package pemapmodder.modcreator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pemapmodder.modcreator.objects.Function;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.util.ArrayMap;
import android.widget.Toast;

public class ImportMethodsTask extends AsyncTask<Context, Object, Integer>{
	public final static int RESULT_NETWORK_UNACCESSIBLE = 1;
	public final static int RESULT_APP_FAULT = 500;
	public final static int RESULT_NO_RESPONSE = 2;
	public final static int RESULT_PAGE_NOT_FOUND = 404;
	public final static int RESULT_ACCESS_DENIED = 403;
	private Context ctx;
	private int cnt = 0;
	@Override
	protected Integer doInBackground(Context... args){
		ctx = args[0];
		ConnectivityManager mgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = mgr.getActiveNetworkInfo();
		if(info != null && info.isConnected()){
			try{
				URL url = new URL("https://raw.githubusercontent.com/zhuowei/MCPELauncher/master/src/net/zhuoweizhang/mcpelauncher/ScriptManager.java");
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setReadTimeout(5000);
				con.setRequestMethod("GET");
				con.setDoInput(true);
				con.connect();
				int response = con.getResponseCode();
				if(response == 404){
					return RESULT_PAGE_NOT_FOUND;
				}
				if(response == 403){
					return RESULT_ACCESS_DENIED;
				}
				InputStream is = con.getInputStream();
				StringBuilder builder = new StringBuilder();
				int c;
				while((c = is.read()) != -1){
					builder.append((char) c);
				}
				is.close();
				String src = builder.toString();
				String result = src
						.replaceAll("/\\*(?:.|[\\n\\r])*?\\*/", "")
//						.replaceAll("//.*$\n", "")
						;
				List<Function> functions = getTopNamespaceFunctions(result.substring(0));
				functions.addAll(parseClassMethods(result.substring(0)));
				cnt = functions.size();
				Writer writer = new OutputStreamWriter(new FileOutputStream(Utils.getMethodDumpFile(ctx)));
				for(Function fx: functions){
					writer.append(fx.getReturnType());
					writer.append(' ');
					writer.append(fx.getQualifiedName());
					writer.append(':');
					String buffer = "";
					for(Map.Entry<String, String> entry: fx.getArgs().entrySet()){
						buffer += entry.getValue();
						buffer += " ";
						buffer += entry.getKey();
						buffer += ",";
					}
					writer.append(buffer, 0, buffer.length() - 1);
					writer.append("\n");
					// ReturnType Namespace.fxName:Arg1Type arg1,Arg2Type arg2
				}
				writer.close();
			}
			catch(MalformedURLException e){
				e.printStackTrace();
				return RESULT_APP_FAULT;
			}
			catch(IOException e){
				e.printStackTrace();
				return RESULT_APP_FAULT;
			}
		}
		else{
			return RESULT_NETWORK_UNACCESSIBLE;
		}
		return null;
	}
	private List<Function> getTopNamespaceFunctions(String string){
		List<Function> fxs = new ArrayList<Function>();
		int index;
		while((index = string.indexOf("@JSFunction")) != -1){
			string = string.substring(index + "@JSFunction".length());
			string = string.substring(string.indexOf("public ") + "public ".length());
			int returnLength = string.indexOf(" ");
			String returnType = string.substring(0, returnLength);
			string = string.substring(returnLength + 1);
			int nameLength = string.indexOf("(");
			String name = string.substring(0, nameLength);
			string = string.substring(nameLength + 1);
			int argsRawLength = string.indexOf(")");
			String argsRaw = string.substring(0, argsRawLength);
			String[] argsArr = argsRaw.split(",");
			Map<String, String> args = new ArrayMap<String, String>(argsArr.length);
			for(String arg: argsArr){
				arg = arg.trim();
				String[] tokens = arg.split(" ", 2);
				args.put(tokens[1], tokens[0]);
			}
			Function fx = new Function("", name, args, returnType);
			fxs.add(fx);
		}
		return fxs;
	}
	private List<Function> parseClassMethods(String string){
		List<Function> result = new ArrayList<Function>();
		int classStartIndex;
		while((classStartIndex = string.indexOf("private static class Native")) != -1){
			string = string.substring(classStartIndex);
			String namespace = string.substring(0, string.indexOf(" "));
			if(!namespace.endsWith("Api")){
				continue;
			}
			string = string.substring(namespace.length());
			if(!string.startsWith(" extends ScriptableObject")){
				continue;
			}
			namespace = namespace.substring(0, namespace.length() - 3).concat(".");
			int nextClass = string.indexOf("private static class Native");
			int nextFx = string.indexOf("@JSStaticFunction");
			if(nextFx < nextClass){
				string = string.substring(string.indexOf("public static "));
				int returnLength = string.indexOf(" ");
				String returnType = string.substring(0, returnLength);
				int nameLength = string.indexOf("(");
				String name = string.substring(0, nameLength);
				string = string.substring(nameLength + 1);
				String[] argsRaw = string.substring(0, string.indexOf(")")).split(",");
				Map<String, String> args = new ArrayMap<String, String>();
				for(String arg: argsRaw){
					String[] tokens = arg.trim().split(" ");
					args.put(tokens[1], tokens[0]);
				}
				Function fx = new Function(namespace, name, args, returnType);
				result.add(fx);
			}
		}
		return result;
	}
	@Override
	protected void onProgressUpdate(Object... progress){
		if(progress[0] instanceof CharSequence){
			Toast.makeText(ctx, (CharSequence) progress[0], (Integer) progress[1]).show();
		}
		else{
			Toast.makeText(ctx, (Integer) progress[0], (Integer) progress[1]).show();
		}
	}
	@Override
	protected void onPostExecute(Integer result){
		String string = "Imported %d functions and saved.";
		int i = result == null ? 0:(int) result;
		switch(i){
			case RESULT_NETWORK_UNACCESSIBLE:
				string = "Cannot access network.";
				break;
			case RESULT_APP_FAULT:
				string = "An internal error occurred.";
				break;
			case RESULT_NO_RESPONSE:
				string = "No response from raw.githubusercontent.com in 5 seconds, aborted.";
				break;
			case RESULT_PAGE_NOT_FOUND:
				string = "The download page has been deleted or moved, failed.";
				break;
			case RESULT_ACCESS_DENIED:
				string = "Access to the repository is denied, aborted.";
				break;
			case 0:
				string = String.format(Locale.US, string, cnt);
				break;
		}
		Toast.makeText(ctx, "Downloading ModPE functions completed. Result: " + string, Toast.LENGTH_LONG).show();
	}
}
