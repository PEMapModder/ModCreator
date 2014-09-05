package pemapmodder.modcreator;

import java.io.File;

import pemapmodder.modcreator.objects.Project;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public class ManageProject extends ActionBarActivity{
	private Project project;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_project);
		Uri uri = getIntent().getData();
		File dir = new File(uri.getPath());
		project = new Project(this, dir);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.manage_project, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		int id = item.getItemId();
		if(id == R.id.action_settings){
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onStart(){
		super.onStart();
		project.lock();
		project.load();
	}
	@Override
	public void onStop(){
		super.onStop();
		project.save();
		project.unlock();
	}
	@Override
	public void onDestroy(){
		super.onDestroy();
		project.close();
	}
}
