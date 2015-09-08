package xmlchangelog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.SimpleDateFormat;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.sling.commons.json.JSONException;

import xmlchangelog.bean.CustomerXMLList;

public class XMLComparator {
	
	private Unmarshaller customerUm;
	private JSONChangeLogWriter writer;
	private SimpleDateFormat sdf;
	
	public XMLComparator(JSONChangeLogWriter writer) {
		this.writer = writer;
		sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		try {
			customerUm = JAXBContext.newInstance(CustomerXMLList.class).createUnmarshaller();
		}
		catch (JAXBException e) {
			System.out.println("Error initializing unmarshaller for Customer XML in XMLComparator");
			e.printStackTrace();
		}
	}
	
	public void compareXML(String compareFrom, String compareTo) {
		try {
			File fromFile = new File(compareFrom);
			File toFile = new File(compareTo);
			CustomerXMLList fromList = (CustomerXMLList) customerUm.unmarshal(fromFile);
			CustomerXMLList toList = (CustomerXMLList) customerUm.unmarshal(toFile);
			
			if (fromList == null || toList == null) {
				return;
			}
			
			//Check for all id's in new file which changed from old file
			for (int i=0; i<fromList.getCustomerList().size(); i++) {
				boolean foundId = false;
				for (int j=0; j<toList.getCustomerList().size(); j++) {
					
					//Check for all id's which exist in both files but are changed in either name or address
					if (fromList.getCustomerList().get(i).getId() == toList.getCustomerList().get(j).getId()) {
						foundId = true;
						if (!fromList.getCustomerList().get(i).getName().equals(toList.getCustomerList().get(j).getName()) ||
							!fromList.getCustomerList().get(i).getAddress().equals(toList.getCustomerList().get(j).getAddress())) {
							writer.startChange();
							writer.setTimestamp(sdf.format(fromFile.lastModified()));
							
							if (!fromList.getCustomerList().get(i).getName().equals(toList.getCustomerList().get(j).getName())) {
								writer.setChange("customer.id["+fromList.getCustomerList().get(i).getId()+"].name", 
										         fromList.getCustomerList().get(i).getName(), 
										         toList.getCustomerList().get(j).getName());
								System.out.println("id:"+fromList.getCustomerList().get(i).getId());
							}
							if (!fromList.getCustomerList().get(i).getAddress().equals(toList.getCustomerList().get(j).getAddress())) {
								writer.setChange("customer.id["+fromList.getCustomerList().get(i).getId()+"].address", 
										         fromList.getCustomerList().get(i).getAddress(), 
										         toList.getCustomerList().get(j).getAddress());
								System.out.println("id:"+fromList.getCustomerList().get(i).getId());
							}
							writer.endChange();
						}
					}
				}
				//Check for all id's in new file but not found in old file
				if (!foundId) {
					writer.startChange();
					writer.setTimestamp(sdf.format(fromFile.lastModified()));
					
					writer.setChange("customer.id["+fromList.getCustomerList().get(i).getId()+"].name", 
								     fromList.getCustomerList().get(i).getName(), 
								     null);
					writer.setChange("customer.id["+fromList.getCustomerList().get(i).getId()+"].address", 
								     fromList.getCustomerList().get(i).getAddress(), 
								     null);
					writer.endChange();
					System.out.println("id:"+fromList.getCustomerList().get(i).getId());
				}
			}
			
			//Check for all id's in old file which were deleted from new file
			for (int j=0; j<toList.getCustomerList().size(); j++) {
				boolean foundId = false;
				for (int i=0; i<fromList.getCustomerList().size(); i++) {
					
					if (fromList.getCustomerList().get(i).getId() == toList.getCustomerList().get(j).getId()) {
						foundId = true;
						break;
					}
				}
				
				if (!foundId) {
					writer.startChange();
					writer.setTimestamp(sdf.format(fromFile.lastModified()));
					
					writer.setChange("customer.id["+toList.getCustomerList().get(j).getId()+"].name", 
							         null,
							         toList.getCustomerList().get(j).getName());
					writer.setChange("customer.id["+toList.getCustomerList().get(j).getId()+"].address", 
							         null,
								     toList.getCustomerList().get(j).getAddress());
					writer.endChange();
					System.out.println("id:"+toList.getCustomerList().get(j).getId());
				}
			}
			
		} catch (JAXBException e) {
			System.out.println("Could not parse XML in files " + compareFrom + ", " + compareTo);
			e.printStackTrace();
		} catch (JSONException e) {
			System.out.println("Could not write JSON log file");
			e.printStackTrace();
		}
		
	}
	
}
