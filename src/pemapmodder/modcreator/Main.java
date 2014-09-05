package pemapmodder.modcreator;

import java.io.File;
import java.io.FileFilter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

public class Main extends ActionBarActivity implements OnClickListener{
	public final static int PROJECT_BUTTON_ID = 0x80000000;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Utils.getMethodDumpFile(this);
		listProjects();
	}
	private void listProjects(){
		File[] files = Utils.getProjectsDir(this).listFiles(new ProjectFileFilter());
		String[] names = new String[files.length];
		int i = 0;
		for(File f: files){
			names[i++] = f.getName();
		}
		@SuppressWarnings("unchecked")
		AdapterView<ArrayAdapter<String>> lv = (AdapterView<ArrayAdapter<String>>) findViewById(R.id.main_projects_lv);
		Button tv = new Button(this);
		tv.setOnClickListener(this);
		tv.setId(PROJECT_BUTTON_ID);
		lv.setAdapter(new ArrayAdapter<String>(this, PROJECT_BUTTON_ID, names));
	}
	private class ProjectFileFilter implements FileFilter{
		@Override
		public boolean accept(File file){
			return file.isDirectory(); // TODO more
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		int id = item.getItemId();
		if(id == R.id.action_settings){
			startActivity(new Intent(this, Settings.class));
			return true;
		}
		if(id == R.id.action_import){
			startService(new Intent(this, ImportMethodsService.class));
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onClick(View v){
		File file = new File(Utils.getProjectsDir(this), ((Button) v).getText().toString());
		Uri uri = Uri.fromFile(file);
		Intent intent = new Intent(this, ManageProject.class);
		intent.setData(uri);
		startActivity(intent);
	}
}
