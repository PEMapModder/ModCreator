package pemapmodder.modcreator.objects;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import pemapmodder.modcreator.Utils;
import android.content.Context;

public class Project{
	protected File dir;
	protected Context ctx;
	protected File LOCK;
	public Project(Context ctx, File f){
		this.ctx = ctx;
		dir = f;
	}
	public boolean lock(){
		if(LOCK != null){
			return false;
		}
		LOCK = new File(dir, "LOCK");
		try{
			LOCK.createNewFile();
		}
		catch(IOException e){
			Utils.e(e);
		}
		return true;
	}
	public void load(){
		try{
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().
					parse(new File(dir, "workspace"));
			Element main = doc.getDocumentElement();
		}
		catch(Exception e){
			Utils.e(e);
		}
	}
	public void save(){
		// TODO
	}
	public boolean unlock(){
		if(LOCK == null){
			return false;
		}
		LOCK.delete();
		return true;
	}
	public void close(){
		// TODO
	}
}
