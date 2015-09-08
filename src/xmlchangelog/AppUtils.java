package xmlchangelog;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppUtils {
	
	private static final AppUtils singleton = new AppUtils();
	private AppUtils() {
		if (AppUtils.singleton != null) {
			throw new IllegalStateException ("Already instantiated AppUtils singleton");
		} else {
			prop_location = System.getProperty("user.dir") + "/" + property_file_name;
			initProperties();
		}
	}
	public static AppUtils getInstance() {
		return AppUtils.singleton;
	}
	
	private static final String property_file_name = "project.properties";
	private static final String prop_app_root = "APP_ROOT";
	private static final String prop_xml_location = "APP_XML_LOCATION";
	private static final String prop_tmp_location = "APP_TMP_LOCATION";
	private static final String prop_log_location = "APP_LOG_LOCATION";
	private static final String prop_cust_filename = "CUSTOMER_XML_FILENAME";
	private static final String prop_cust_old_filename = "CUSTOMER_XML_OLD_FILENAME";
	private static final String prop_cust_changelog_filename = "CUSTOMER_XML_CHANGELOG_FILENAME";
	
	private String prop_location;
	
	private String app_root;
	private String xml_location;
	private String tmp_location;
	private String log_location;
	
	private String customer_xml_filename;
	private String customer_old_xml_filename;
	private String customer_changelog_filename;
	
	private void initProperties() {
		Properties prop = new Properties();
		InputStream stream = null;
		
		try {
			stream = new FileInputStream(prop_location);
			prop.load(stream);
			
			app_root = prop.getProperty(prop_app_root);
			xml_location = prop.getProperty(prop_xml_location);
			tmp_location = prop.getProperty(prop_tmp_location);
			log_location = prop.getProperty(prop_log_location);
			
			customer_xml_filename = prop.getProperty(prop_cust_filename);
			customer_old_xml_filename = prop.getProperty(prop_cust_old_filename);
			customer_changelog_filename = prop.getProperty(prop_cust_changelog_filename);
			
			System.out.println("Property file location: " + prop_location);
			System.out.println(prop_app_root + ": " + app_root);
			System.out.println(prop_xml_location + ": " + xml_location);
			System.out.println(prop_tmp_location + ": " + tmp_location);
			System.out.println(prop_log_location + ": " + log_location);
			System.out.println(prop_cust_filename + ": " + customer_xml_filename);
			System.out.println(prop_cust_old_filename + ": " + customer_old_xml_filename);
			System.out.println(prop_cust_changelog_filename + ": " + customer_changelog_filename);
		}
		catch (FileNotFoundException e) {
			System.out.println("Unable to find property file " + prop_location);
			e.printStackTrace();
		}
		catch (IOException e) {
			System.out.println("Error encountered when trying to read project.properties file");
			e.printStackTrace();
		}
		finally {
			if (stream != null) {
				try {
					stream.close();
				}
				catch (IOException e) {
					System.out.println("Error encountered when trying to close project.properties file");
					e.printStackTrace();
				}
			}
		}
	}
	
	public String getAppRoot() {
		return app_root;
	}
	
	public String getXmlLocation() {
		return app_root+'/'+xml_location;
	}
	
	public String getTmpLocation() {
		return app_root+'/'+tmp_location;
	}
	
	public String getLogLocation() {
		return app_root+'/'+log_location;
	}
	
	public String getCustomerXmlFilename() {
		return customer_xml_filename;
	}
	
	public String getCustomerOldXmlFilename() {
		return customer_old_xml_filename;
	}
	
	public String getCustomerChangelogFilename() {
		return customer_changelog_filename;
	}
	
	public static void main (String[] args) {
		AppUtils util = AppUtils.getInstance();
		DetectChangesToFiles changeObj = new DetectChangesToFiles(util.getXmlLocation(), 
				                                                  util.getLogLocation()+"/"+util.getCustomerChangelogFilename());
		
		changeObj.startDetectingChanges(
				util.getXmlLocation()+"/"+util.getCustomerXmlFilename(), 
				util.getTmpLocation()+"/"+util.getCustomerOldXmlFilename());
		
		//changeObj.stopDetectingChanges();
	}
}
