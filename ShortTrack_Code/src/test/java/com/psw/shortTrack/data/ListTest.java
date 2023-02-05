package com.psw.shortTrack.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

public class ListTest {
	
	@Test
	void TestCheckNamePositive() {
		
		List list = new List("List 1",10);
		PersonalTask task = new PersonalTask("Task 1",10);
		list.addTask(task);
		
		assertEquals(true, list.checkName("Task 1"));
	}
	
	@Test
	void TestCheckNameNegative() {
		
		List list = new List("List 1",10);
		PersonalTask task = new PersonalTask("Task 1",10);
		list.addTask(task);
		
		assertEquals(false, list.checkName("Task 2"));
	}
	
	@Test
	void TestShortByNameWithNumbers() {
		
		List listToTest = new List("List to Test",10);
		PersonalTask task1 = new PersonalTask("Task 1",10);
		PersonalTask task2 = new PersonalTask("Task 2",10);
		PersonalTask task3 = new PersonalTask("Task 3",10);
		PersonalTask task4 = new PersonalTask("Task 4",10);
		listToTest.addTask(task3);
		listToTest.addTask(task1);
		listToTest.addTask(task2);
		listToTest.addTask(task4);
		
		listToTest.sort(SortBy.Name);
		
		List orderedList = new List("Ordered list",11);
		orderedList.addTask(task1);
		orderedList.addTask(task2);
		orderedList.addTask(task3);
		orderedList.addTask(task4);
		
		assertArrayEquals(orderedList.getTaskList().toArray(),listToTest.getTaskList().toArray());
	}
	
	@Test
	void TestShortByNameWithLetters() {
		
		List listToTest = new List("List to Test",10);
		PersonalTask task1 = new PersonalTask("First",10);
		PersonalTask task2 = new PersonalTask("Second",10);
		PersonalTask task3 = new PersonalTask("Third",10);
		PersonalTask task4 = new PersonalTask("Fourth",10);
		listToTest.addTask(task2);
		listToTest.addTask(task1);
		listToTest.addTask(task3);
		listToTest.addTask(task4);
		
		listToTest.sort(SortBy.Name);
		
		List orderedList = new List("Ordered list",11);
		orderedList.addTask(task1);
		orderedList.addTask(task4);
		orderedList.addTask(task2);
		orderedList.addTask(task3);
		
		assertArrayEquals(orderedList.getTaskList().toArray(),listToTest.getTaskList().toArray());
	}
	
	@Test
	void TestShortByDeadlineDateWithoutNullElements() {
		
		List listToTest = new List("List to Test",10);
		PersonalTask task1 = new PersonalTask("Task 1",10);
		task1.setDeadline(LocalDate.of(2022, 3, 11));
		PersonalTask task2 = new PersonalTask("Task 2",10);
		task2.setDeadline(LocalDate.of(2022, 6, 20));
		PersonalTask task3 = new PersonalTask("Task 3",10);
		task3.setDeadline(LocalDate.of(2022, 8, 1));
		PersonalTask task4 = new PersonalTask("Task 4",10);
		task4.setDeadline(LocalDate.of(2023, 3, 11));
		listToTest.addTask(task3);
		listToTest.addTask(task4);
		listToTest.addTask(task2);
		listToTest.addTask(task1);
		
		listToTest.sort(SortBy.DeadlineDate);
		
		List orderedList = new List("Ordered list",11);
		orderedList.addTask(task1);
		orderedList.addTask(task2);
		orderedList.addTask(task3);
		orderedList.addTask(task4);
		
		assertArrayEquals(orderedList.getTaskList().toArray(),listToTest.getTaskList().toArray());
	}
	
	@Test
	void TestShortByDeadlineDateWithNullElements() {
		
		List listToTest = new List("List to Test",10);
		PersonalTask task1 = new PersonalTask("Task 1",10);
		task1.setDeadline(LocalDate.of(2022, 3, 10));
		PersonalTask task2 = new PersonalTask("Task 2",10);
		task2.setDeadline(LocalDate.of(2022, 3, 15));
		PersonalTask task3 = new PersonalTask("Task 3",10);
		PersonalTask task4 = new PersonalTask("Task 4",10);
		listToTest.addTask(task3);
		listToTest.addTask(task1);
		listToTest.addTask(task2);
		listToTest.addTask(task4);
		
		listToTest.sort(SortBy.DeadlineDate);
		
		List orderedList = new List("Ordered list",11);
		orderedList.addTask(task1);
		orderedList.addTask(task2);
		orderedList.addTask(task3);
		orderedList.addTask(task4);
		
		assertArrayEquals(orderedList.getTaskList().toArray(),listToTest.getTaskList().toArray());
	}
	
	@Test
	void TestShortByCompleted() {
		
		List listToTest = new List("List to Test",10);
		PersonalTask task1 = new PersonalTask("Task 1",10);
		task1.setCompleted(false);
		PersonalTask task2 = new PersonalTask("Task 2",10);
		task2.setCompleted(false);
		PersonalTask task3 = new PersonalTask("Task 3",10);
		task3.setCompleted(true);
		PersonalTask task4 = new PersonalTask("Task 4",10);
		task4.setCompleted(true);
		listToTest.addTask(task3);
		listToTest.addTask(task1);
		listToTest.addTask(task4);
		listToTest.addTask(task2);
		
		listToTest.sort(SortBy.Completed);
		
		List orderedList = new List("Ordered list",11);
		orderedList.addTask(task1);
		orderedList.addTask(task2);
		orderedList.addTask(task3);
		orderedList.addTask(task4);
		
		assertArrayEquals(orderedList.getTaskList().toArray(),listToTest.getTaskList().toArray());
	}
	
	@Test
	void TestFindTaskByName1() {
		
		List listToTest = new List("List To Test",10);
		PersonalTask task1 = new PersonalTask("Task 1",10);
		PersonalTask task2 = new PersonalTask("Task 2",10);
		PersonalTask task3 = new PersonalTask("Task 3",10);
		PersonalTask task4 = new PersonalTask("Task 4",10);
		listToTest.addTask(task1);
		listToTest.addTask(task2);
		listToTest.addTask(task3);
		listToTest.addTask(task4);
		
		ArrayList<Task> answer = new ArrayList<Task>();	
		listToTest.findTaskByName("Task 3",answer);
		
		ArrayList<Task> solution = new ArrayList<Task>();
		solution.add(task3);
		
		assertTrue(solution.size() == answer.size() && solution.containsAll(answer) && answer.containsAll(solution));
	}
	
	@Test
	void TestFindTaskByName2() {
		
		List listToTest = new List("List To Test",10);
		PersonalTask task1 = new PersonalTask("Task 1",10);
		PersonalTask task2 = new PersonalTask("Second Task",10);
		PersonalTask task3 = new PersonalTask("Task 3",10);
		PersonalTask task4 = new PersonalTask("Fourth Task",10);
		listToTest.addTask(task1);
		listToTest.addTask(task2);
		listToTest.addTask(task3);
		listToTest.addTask(task4);
		
		ArrayList<Task> answer = new ArrayList<Task>();	
		listToTest.findTaskByName("Task",answer);
		
		ArrayList<Task> solution = new ArrayList<Task>();
		solution.add(task1);
		solution.add(task2);
		solution.add(task3);
		solution.add(task4);
		
		assertTrue(solution.size() == answer.size() && solution.containsAll(answer) && answer.containsAll(solution));
	}
	
	@Test
	void TestFindTaskByName3() {
		
		List listToTest = new List("List To Test",10);
		PersonalTask task1 = new PersonalTask("First element",10);
		PersonalTask task2 = new PersonalTask("Elements",10);
		PersonalTask task3 = new PersonalTask("Element to remove",10);
		PersonalTask task4 = new PersonalTask("Delete this element",10);
		listToTest.addTask(task1);
		listToTest.addTask(task2);
		listToTest.addTask(task3);
		listToTest.addTask(task4);
		
		ArrayList<Task> answer = new ArrayList<Task>();	
		listToTest.findTaskByName("eLeMeNt",answer);
		
		ArrayList<Task> solution = new ArrayList<Task>();
		solution.add(task1);
		solution.add(task2);
		solution.add(task3);
		solution.add(task4);
		
		assertTrue(solution.size() == answer.size() && solution.containsAll(answer) && answer.containsAll(solution));
	}
	
	@Test
	void TestFindTaskByDeadlineDateWithoutNullElements1() {
		
		List listToTest = new List("List To Test",10);
		PersonalTask task1 = new PersonalTask("Task 1",10);
		task1.setDeadline(LocalDate.of(2022, 3, 11));
		PersonalTask task2 = new PersonalTask("Task 2",10);
		task2.setDeadline(LocalDate.of(2022, 6, 20));
		PersonalTask task3 = new PersonalTask("Task 3",10);
		task3.setDeadline(LocalDate.of(2022, 8, 1));
		PersonalTask task4 = new PersonalTask("Task 4",10);
		task4.setDeadline(LocalDate.of(2023, 3, 11));
		listToTest.addTask(task1);
		listToTest.addTask(task2);
		listToTest.addTask(task3);
		listToTest.addTask(task4);
		
		ArrayList<Task> answer = new ArrayList<Task>();
		listToTest.findTaskByDeadline(Task.checkValidDate("2022-03-11"),answer);
		
		ArrayList<Task> solution = new ArrayList<Task>();
		solution.add(task1);
		
		assertTrue(solution.size() == answer.size() && solution.containsAll(answer) && answer.containsAll(solution));
	}
	
	@Test
	void TestFindTaskByDeadlineDateWithoutNullElements2() {
		
		List listToTest = new List("List To Test",10);
		PersonalTask task1 = new PersonalTask("Task 1",10);
		task1.setDeadline(LocalDate.of(2022, 3, 11));
		PersonalTask task2 = new PersonalTask("Task 2",10);
		task2.setDeadline(LocalDate.of(2022, 6, 20));
		PersonalTask task3 = new PersonalTask("Task 3",10);
		task3.setDeadline(LocalDate.of(2022, 8, 1));
		PersonalTask task4 = new PersonalTask("Task 4",10);
		task4.setDeadline(LocalDate.of(2023, 3, 11));
		listToTest.addTask(task1);
		listToTest.addTask(task2);
		listToTest.addTask(task3);
		listToTest.addTask(task4);
		
		ArrayList<Task> answer = new ArrayList<Task>();
		listToTest.findTaskByDeadline(Task.checkValidDate("2022-8-1"),answer);
		
		ArrayList<Task> solution = new ArrayList<Task>();
		solution.add(task3);
		
		assertTrue(solution.size() == answer.size() && solution.containsAll(answer) && answer.containsAll(solution));
	}
	
	@Test
	void TestFindTaskByDeadlineDateWithNullElements() {
		
		List listToTest = new List("List To Test",10);
		PersonalTask task1 = new PersonalTask("Task 1",10);
		PersonalTask task2 = new PersonalTask("Task 2",10);
		task2.setDeadline(LocalDate.of(2022, 3, 11));
		PersonalTask task3 = new PersonalTask("Task 3",10);
		PersonalTask task4 = new PersonalTask("Task 4",10);
		task4.setDeadline(LocalDate.of(2023, 3, 11));
		listToTest.addTask(task1);
		listToTest.addTask(task2);
		listToTest.addTask(task3);
		listToTest.addTask(task4);
		
		ArrayList<Task> answer = new ArrayList<Task>();
		listToTest.findTaskByDeadline(Task.checkValidDate("2023-03-11"),answer);
		
		ArrayList<Task> solution = new ArrayList<Task>();
		solution.add(task4);
		
		assertTrue(solution.size() == answer.size() && solution.containsAll(answer) && answer.containsAll(solution));
	}
}
