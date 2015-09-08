package xmlchangelog.bean;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="customers")
public class CustomerXMLList {
	private List<CustomerXML> customerList = new ArrayList<CustomerXML>();
	
	public CustomerXMLList() { }
	public CustomerXMLList(List<CustomerXML> list) {
		this.customerList = list;
	}

	public List<CustomerXML> getCustomerList() {
		return customerList;
	}
	
	@XmlElement(name="customer")
	public void setCustomerList(List<CustomerXML> customerList) {
		this.customerList = customerList;
	}
}
