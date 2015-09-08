package xmlchangelog;

import java.io.FileWriter;
import java.io.IOException;

import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;

public class JSONChangeLogWriter {
	
	/* Change log will be written in the following format:
	 * {
	 * 		timestamp: '<source file modification timestamp>',
	 * 		tagname1: {
	 * 			old_value: '<old value for tag>'
	 * 			new_value: '<new value for tag>'
	 * 		},
	 * 		tagname2: {
	 * 			old_value: '<old value for tag>'
	 * 			new_value: '<new value for tag>'
	 * 		},
	 * }
	 */
	
	private JSONObject json;
	private String logFilename;
	
	private static final String timestamp_variable = "timestamp";
	private static final String old_value = "old_value";
	private static final String new_value = "new_value";
	
	public JSONChangeLogWriter(String logFilename) {
		this.logFilename = logFilename;
	}
	
	public void startChange() {
		json = new JSONObject();
	}
	
	public void setTimestamp(String timestamp) throws JSONException {
		json.put(timestamp_variable, timestamp);
	}
	
	public void setChange(String tagName, String newValue, String oldValue) throws JSONException {
		JSONObject tagChanges = new JSONObject();
		
		tagChanges.put(old_value, oldValue);
		tagChanges.put(new_value, newValue);
		
		json.put(tagName, tagChanges);
	}
	
	public void endChange() throws JSONException {
		FileWriter writer = null;
		try {
			writer = new FileWriter(logFilename, true);
			System.out.println(json.toString(3));
			writer.write(json.toString(3));
			writer.flush();
			writer.close();
		}
		catch (IOException e) {
			System.out.println("Error encountered when writing to log file " + writer.toString());
			e.printStackTrace();
		}
	}
}
