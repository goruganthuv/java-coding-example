# Java code written by Venkata Lavanya Goruganthu

> This is an example Java program I have written to show my coding style. The intent is to share this with recruiters and potential employers in case they want to see code that I have written.

## Problem Statement

There are two files on the filesystem.
* Customers: which has customer ID, customer Name, customer Address. This is an XML file.
* Change Log: This is a JSON formatted. It has a list of changes to Customer Table and the time the data changed. 

A user can just edit the customer.xml file anytime they want and change it. This program detects the change and logs what changed to changelog.json.

### The following libraries have been used:
* Java NIO2 for polling filesystem for file change events
* JAXB: For converting XML file data into object list that can be compared between files
* Apache Sling JSON: For writing JSON log files

The design has been kept reasonably modular to allow changing parts of the program independently.

> Please share your feedback on my code by writing to goruganthuv@gmail.com
