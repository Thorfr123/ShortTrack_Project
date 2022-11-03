package fileIO;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import data.Task;
import data.List;

public class FileIO {
	private static String fileName = "output.txt";	
	
	/*
	 * Com esta implementação, a função escreve todo o objeto e não tem de lidar com typings para datas e coisas do genero
	 */
	public static void writeObjectToFile(ArrayList <Task> l) {
		try {
			FileOutputStream fos = new FileOutputStream(fileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			for (Task t : l) {
				oos.writeObject(t);
			}

			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeObjectToFile(List list) {
		try {
			FileOutputStream fos = new FileOutputStream(fileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			oos.writeObject(list);

			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	public static void readObjectFromFile() {
		try {
			FileInputStream fis = new FileInputStream(fileName);
			try (ObjectInputStream ois = new ObjectInputStream(fis)) {
				while(true) {
					Task t = (Task)ois.readObject();
					
					System.out.println(t.getID());
					System.out.println(t.getName());
					System.out.println(t.getDescription());
					System.out.println(t.getCreatedDateTime());
					System.out.println(t.getDeadlineDateTime());
				}
			}
		} catch (EOFException e) {
			System.out.println("No more objects");
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	public static List readListObjectFromFile() {
		try {
			FileInputStream fis = new FileInputStream(fileName);
			try (ObjectInputStream ois = new ObjectInputStream(fis)) {
				List lst = (List)ois.readObject();
				
				System.out.println(lst.getName());
				System.out.println(lst.getTaskList());
				
				return lst;
			}
		} catch (EOFException e) {
			System.out.println("No more objects");
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static ArrayList<List> readAllListObjectFromFile() {
		ArrayList<List> arrayList = new ArrayList<List>();
		try {
			FileInputStream fis = new FileInputStream(fileName);
			try (ObjectInputStream ois = new ObjectInputStream(fis)) {
				while(true) {
					List lst = (List)ois.readObject();
					arrayList.add(lst);
				}
			}
		} catch (EOFException e) {
			System.out.println("No more objects");
			return arrayList;
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void main(String args[]) {
		
		Task t = new Task("Task 1");
		t.setDescription("This is the first task");
		t.setID(0);
		Task t1 = new Task("Task 2");
		t1.setDescription("This is the second task");
		t1.setID(1);
		
		ArrayList<Task> list = new ArrayList<Task>();
		list.add(t);
		list.add(t1);
		
		List lst = new List("List 1");
		lst.addTask(t);
		lst.addTask(t1);
		
		//writeObjectToFile(list);
		writeObjectToFile(lst);
		//writeObjectToFile(t1);
		readListObjectFromFile();
	}
}
