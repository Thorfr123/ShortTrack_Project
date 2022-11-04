package fileIO;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import data.List;

public class FileIO {
	private static String personalListFileName = "personalLists.dat";
	
	/**
	 * Writes all Personal Lists within the arrayList to a read-only local file
	 * 
	 * @param arrayList ArrayList with all Personal Lists to save
	 * @throws IOException If an error occurs while writing to the file
	 */
	public static void writePersonalListsToFile(ArrayList<List> arrayList) throws IOException{
		File file = new File(personalListFileName);
		file.createNewFile();
		file.setWritable(true);
		
		try (FileOutputStream fos = new FileOutputStream(personalListFileName);
			 ObjectOutputStream oos = new ObjectOutputStream(fos); ){

			for (List lst : arrayList) {
				oos.writeObject(lst);
			}
			
			System.out.println("List successfully written in local file");
			
		}
		
		file.setReadOnly();
	}
	
	/**
	 * Reads all saved Personal Lists from the local file
	 * @return All read Personal Lists
	 * @throws IOException If there is an error while reading the file
	 * @throws ClassNotFoundException If it is not possible to reassemble objects from the file
	 */
	public static ArrayList<List> readPersonalListsFromFile() throws IOException, ClassNotFoundException{
		ArrayList<List> arrayList = new ArrayList<List>();
		try (FileInputStream fis = new FileInputStream(personalListFileName);
			 ObjectInputStream ois = new ObjectInputStream(fis)){
			
			while(true) {
				List lst = (List)ois.readObject();
				arrayList.add(lst);
			}
			
		} catch (EOFException eof) {
			System.out.println("List successfully read from local file");
			return arrayList;
		}
	}
	
}
