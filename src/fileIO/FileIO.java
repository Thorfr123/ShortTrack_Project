package fileIO;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import data.Task;
import data.List;

public class FileIO {
	private static String personalListFileName = "personalLists.txt";
	
	/*
	 * Com esta implementação, a função escreve todo o objeto e não tem de lidar com typings para datas e coisas do genero
	 */
	public static void writePersonalListsToFile(ArrayList<List> l) {

		try (FileOutputStream fos = new FileOutputStream(personalListFileName);
			 ObjectOutputStream oos = new ObjectOutputStream(fos); ){

			for (List list : l) {
				oos.writeObject(list);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public static ArrayList<List> readPersonalListsFromFile() {
		ArrayList<List> arrayList = new ArrayList<List>();
		try (FileInputStream fis = new FileInputStream(personalListFileName);
			 ObjectInputStream ois = new ObjectInputStream(fis)){
			
			while(true) {
				List lst = (List)ois.readObject();
				arrayList.add(lst);
			}
			
		} catch (EOFException e) {
			System.out.println("No more objects");
			return arrayList;
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	/*public static void main(String args[]) {
		List l1 = new List("list1");
		List l2 = new List("list2");
		
		ArrayList<List> lists = new ArrayList<List>();
		lists.add(l1);
		lists.add(l2);
		
		writePersonalListsToFile(lists);
		
		
	}*/
}
