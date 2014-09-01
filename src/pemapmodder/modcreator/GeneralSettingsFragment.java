package pemapmodder.modcreator;

import java.io.File;
import java.io.IOException;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class GeneralSettingsFragment extends PreferenceFragment
		implements OnPreferenceChangeListener{
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		Preference p = findPreference(getString(R.string.settings_key_folder_title));
		p.setOnPreferenceChangeListener(this);
		try{
			((EditTextPreference) p).setText(Utils.getAppFile(getActivity())
					.getCanonicalPath());
		}
		catch(IOException e){
			Utils.e(e);
		}
	}
	@Override
	public boolean onPreferenceChange(Preference pref, Object str){
		if(pref.getKey() == getString(R.string.settings_key_folder_title)){
			File file = new File(((EditTextPreference) pref).getText());
			file.mkdirs();
			if(!file.isDirectory()){
				Toast.makeText(getActivity(), R.string.settings_invalid_folder,
						Toast.LENGTH_SHORT).show();
				return false;
			}
			File testFile = new File(file, Utils.METHOD_DUMP_FILE);
			try{
				testFile.createNewFile();
				if(!testFile.isFile()){
					Toast.makeText(getActivity(),
							R.string.settings_folder_cannot_make_file,
							Toast.LENGTH_SHORT).show();
				}
			}
			catch(IOException e){
				Utils.e(e);
				Toast.makeText(getActivity(), String.format("An unexpected error " +
						"(%s) occurred: %s", e.getClass().getSimpleName(),
						e.getMessage()), Toast.LENGTH_LONG).show();
				return false;
			}
			try{
				getActivity().getSharedPreferences(Utils.SHARED_PREFERENCES_NAME,
						Context.MODE_PRIVATE).edit()
						.putString(Utils.PREF_DIR, file.getCanonicalPath())
						.apply();
			}
			catch(IOException e){
				Utils.e(e);
			}
			return true;
		}
		Toast.makeText(getActivity(), String.format("[INTERNAL ERROR] Unexpected " +
				"preference %s not handled by GeneralSettingsFragment", 
				pref.toString()), Toast.LENGTH_LONG).show();
		return false;
	}
}
