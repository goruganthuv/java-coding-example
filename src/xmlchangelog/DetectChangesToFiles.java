package xmlchangelog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

public class DetectChangesToFiles implements Runnable {
	
/*   1.Read customer.xml file and Create a backup file
 *   2.Set Watch on File Directory and listen for file change events
 *   3.When the event is generated Do the following:
 *   4.Compare old file and New file and write the changes to the JsonLog file.
 *   5.Delete the old backup file and copy customer.xml to backup folder.
 */
	
	private String directory;
	private String fileToWatch;
	private String fileToCompare;
	private String logFile;
	private XMLComparator comparator;
	
	private WatchService watcher;
	private WatchKey key;
	private Thread watcherThread;
	private volatile boolean threadRunning;
	
	private JSONChangeLogWriter jsonWriter;
	
	public DetectChangesToFiles(String directory, String logFile) {
		this.directory = directory;
		this.logFile = logFile;
		this.jsonWriter = new JSONChangeLogWriter(logFile);
		this.comparator = new XMLComparator(this.jsonWriter);
		
		try {
			watcher = FileSystems.getDefault().newWatchService();
			Path dir = FileSystems.getDefault().getPath(directory);
			
			key = dir.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
			
			watcherThread = null;
			threadRunning = false;
		}
		catch (IOException e) {
			System.out.println("Unable to create WatchService in DetectChanges on directory " + directory);
			e.printStackTrace();
		}
	}
	
	public void run() {
		System.out.println("Watcher thread started for directory " + directory);
		while (threadRunning) {
			for (WatchEvent<?> event : key.pollEvents()) {
				
				WatchEvent.Kind<?> kindOfEvent = event.kind();
				if (kindOfEvent == StandardWatchEventKinds.OVERFLOW) {
					System.out.println("Overflow event");
					continue;
				}
				
				@SuppressWarnings("unchecked")
				Path filename = ((WatchEvent<Path>)event).context();
				
				if (fileToWatch.endsWith(filename.toString())) {
					System.out.println("Change detected in file " + fileToWatch);
					comparator.compareXML(fileToWatch, fileToCompare);
					createBackupFile(fileToWatch, fileToCompare);
				}
			}
		}
	}
	
	public void startDetectingChanges(String fileToWatch, String fileToCompare) {
		if (watcherThread == null) {
			this.fileToWatch = fileToWatch;
			this.fileToCompare = fileToCompare;
			
			createBackupFile(this.fileToWatch, this.fileToCompare);
			
			watcherThread = new Thread(this);
			threadRunning = true;
			watcherThread.start();
		}
		else
		{
			System.out.println("Already detecting changes on directory " + directory);
		}
	}
	
	public void stopDetectingChanges() {
		threadRunning = false;
		try {
			watcherThread.join();
			System.out.println("Watcher thread stopped for directory " + directory);
		}
		catch (InterruptedException e) {
			System.out.println("Interruption in thread for detecting changes on directory " + directory);
		}
	}
	
	private void createBackupFile(String source,String destination) {
		
		File inputFile = new File(source);
		File outputFile = new File (destination);
		
		if(!inputFile.exists()){
			System.out.println("Input file " + source + " not found");
			return;
		}
		if(outputFile.exists()){
			deleteOldBackupFile(destination);
		}
		
		FileChannel inputChannel = null;
		FileChannel outputChannel = null;
		
		try {
			inputChannel = new FileInputStream(source).getChannel();
			outputChannel = new FileOutputStream(destination).getChannel();
			outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
		}
		catch(IOException e) {
			System.out.println("Error Creating backup file in DetectChanges.createBackupFile()");
			e.printStackTrace();
		}
		finally {
			try {
				inputChannel.close();
				outputChannel.close();
			}
			catch (IOException e) {
				System.out.println("Error closing input and output files in DetectChanges.createBackupFile()");
				e.printStackTrace();
			}
		}
	}
	
	private boolean deleteOldBackupFile(String fileName) {
		File oldBackup = new File(fileName);
		return oldBackup.delete();
	}
	
}
