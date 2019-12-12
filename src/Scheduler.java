/**
* The Scheduler program reads in a .csv file of names and hours
* and allows the user to perform a number of functions on the
* schedule. This includes showing the schedule in a readable format,
* calculating the hours for a given day or person, or changing the
* hours for an employee. Valid changes are saved back to the .csv.
*
* Note: Hours must be on the hour or on the half-hour for accurate
* calculation of hours worked.
*
* @author  Alissa Cielecki, Janelle Rohrbach
* @version 1.0
* @since   2019-12-06
*/
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.HashMap;
import java.io.IOException;
import java.io.FileWriter;
public class Scheduler {
						  //name          //day   //hours
	public static HashMap<String, HashMap<String, String>> hm = new HashMap<>();
	private static File hours = new File("hours.csv");
	public static void main(String[] args) throws FileNotFoundException, IOException {
		readSchedule();
		mainMenu();
		saveChanges();
	}
	//reads the csv file of employee hours into the hashmap of hashmaps
	public static void readSchedule() throws FileNotFoundException {
		Scanner in = new Scanner(hours);
		if(in.hasNextLine()) {
			in.nextLine();
		}
		while(in.hasNextLine()) {
			String line[] = in.nextLine().split(",");
			String name = line[0];
				    //day, hours
			HashMap<String, String> hoursPerDay = new HashMap<>();
			hoursPerDay.put("Monday", line[1]);
			hoursPerDay.put("Tuesday", line[2]);
			hoursPerDay.put("Wednesday", line[3]);
			hoursPerDay.put("Thursday", line[4]);
			hoursPerDay.put("Friday", line[5]);
			hm.put(name, hoursPerDay);
		}

	}
	//prints out full schedule in a readable format
	public static void getFullSchedule() {
		String schedule = "";
		for(String name: hm.keySet()) {
			schedule += name + "\n";
			schedule += "Monday" + ": " + hm.get(name).get("Monday") + "\n";
			schedule += "Tuesday" + ": " + hm.get(name).get("Tuesday") + "\n";
			schedule += "Wednesday" + ": " + hm.get(name).get("Wednesday") + "\n";
			schedule += "Thursday" + ": " + hm.get(name).get("Thursday") + "\n";
			schedule += "Friday" + ": " + hm.get(name).get("Friday") + "\n";
			schedule += "\n";
		}
		System.out.println(schedule);
	}
	//Asks user which day of the week they would like to see the schedule for and calls getDayOfWeek
	public static void chooseDayOfWeek() {
		Scanner in = new Scanner(System.in);
		System.out.println("What day of the week would you like to see the schedule for?");
		boolean validOption = true;
		do {
			validOption = true;
			switch(in.nextLine().toLowerCase()) {
				case "monday":
					getDayOfWeek("Monday");
					break;
				case "tuesday":
					getDayOfWeek("Tuesday");
					break;
				case "wednesday":
					getDayOfWeek("Wednesday");
					break;
				case "thursday":
					getDayOfWeek("Thursday");
					break;
				case "friday":
					getDayOfWeek("Friday");
					break;
				default:
					validOption = false;
					System.out.println("Invalid day. Try again.");
			}
		} while(!validOption);
	}
	//takes a day of the week and prints out who is working and what times they are working on this day
	public static void getDayOfWeek(String dayOfWeek) {
		System.out.println(dayOfWeek);
		for(String name: hm.keySet()) {
			System.out.println(name + " - " + hm.get(name).get(dayOfWeek));
		}
		System.out.println("Total hours worked: " + getHoursOnDay(dayOfWeek) + "\n");
	}
	//takes an employee and will print out the schedule for that person
	public static void getSingleSchedule(String name) {
			System.out.println(name);
			System.out.println("Monday" + ": " + hm.get(name).get("Monday"));
			System.out.println("Tuesday" + ": " + hm.get(name).get("Tuesday"));
			System.out.println("Wednesday" + ": " + hm.get(name).get("Wednesday"));
			System.out.println("Thursday" + ": " + hm.get(name).get("Thursday"));
			System.out.println("Friday" + ": " + hm.get(name).get("Friday"));
			System.out.println("Total hours worked: " + getHoursForPerson(name) + "\n");
	}
	//takes day of week and returns how many hours are being worked that day
	//only accounts for full and half hour scheduling times
	public static String getHoursOnDay(String dayOfWeek) {
		String hours = "";
		double totalHours = 0;
		for(String name: hm.keySet()) {
			hours = hm.get(name).get(dayOfWeek);
			if(!(hours.equals("off"))) { //split the String and convert to a double after changing the format of the times
				String[] split = hours.split("-");
				double start = Double.parseDouble(split[0].replaceAll(":3", ".5").replaceAll(":", "."));
				double end = Double.parseDouble(split[1].replaceAll(":3", ".5").replaceAll(":", "."));
				totalHours += (end-start);
			}
		}
		return totalHours + "";
	}
	//takes a person and returns how many hours they work per week
	public static String getHoursForPerson(String name) {
		double totalHours = 0;
		for(String dayOfWeek: hm.get(name).keySet()) {
			if(!(hm.get(name).get(dayOfWeek).equals("off"))) {
				String[] split = hm.get(name).get(dayOfWeek).split("-");
				double var1 = Double.parseDouble(split[0].replaceAll(":3", ".5").replaceAll(":", "."));
				double var2 = Double.parseDouble(split[1].replaceAll(":3", ".5").replaceAll(":", "."));
				totalHours += (var2-var1);
			}
		}
		return totalHours + "";
	}
	//takes a person and changes the hours they are working
	public static void updateHours(String name) throws IOException {
		Scanner in = new Scanner(System.in);
		String day = "";
		String oldHours = "";
		while(!(day.toLowerCase().equals("done"))) {
			try {
				getSingleSchedule(name);
				System.out.println("For which day would you like to change " + name + "'s hours? (Or enter \"done\" to quit).");
				day = in.nextLine();
				if(!(day.toLowerCase().equals("done")) && hm.get(name).containsKey(day)) {
					oldHours = hm.get(name).get(day);
					System.out.println("Please enter " + name + "'s new hours in the format \"00:00-00:00\" or \"off.\"");
					String hours = in.nextLine();
					hm.get(name).put(day, hours.replaceAll("\\s", ""));
				}
			} catch(Exception e) {
				System.out.println("Incorrect format. Changes not saved.\n");
				hm.get(name).put(day, oldHours);
			}
		}
		saveChanges();
	}
	//prints options menu until the user decides to quit
	public static void mainMenu() throws IOException {
		Scanner in = new Scanner(System.in);
		boolean keepGoing = true;
		String name = "";
		do {
			System.out.println("Main Menu - Enter the number next to the option you would like to choose!\n1 - Show full schedule.\n2 - Show schedule for given day.\n3 - Show employee information.\n4 - Update an employee's hours.\n5 - Quit");
			switch(in.nextLine()) {
				case "1":
					getFullSchedule();
					break;
				case "2":
					chooseDayOfWeek();
					break;
				case "3":
					System.out.println("Enter the full name of an employee.");
					name = in.nextLine();
					if(hm.containsKey(name)) {
						getSingleSchedule(name);
					} else {
						System.out.println("Couldn't find " + name + ".");
					}
					break;
				case "4":
					System.out.println("Enter the full name of an employee.");
					name = in.nextLine();
					if(hm.containsKey(name)) {
						updateHours(name);
					} else {
						System.out.println("Couldn't find " + name + ".");
					}
					break;
				case "5":
					keepGoing = false;
					System.out.println("Have a good day!");
					break;
				default:
					System.out.println("Invalid option.");
			}
		} while(keepGoing);
	}
	//writes back to text file
	public static void saveChanges() throws IOException {
		FileWriter fileWriter = new FileWriter(hours);
		String schedule = "Name,Monday,Tuesday,Wednesday,Thursday,Friday\n";
		for(String name: hm.keySet()) {
			schedule += name + "," + hm.get(name).get("Monday") + ",";
			schedule += hm.get(name).get("Tuesday") + ",";
			schedule += hm.get(name).get("Wednesday") + ",";
			schedule += hm.get(name).get("Thursday") + ",";
			schedule += hm.get(name).get("Friday") + "\n";
		}
		fileWriter.write(schedule);
		fileWriter.close();
	}
}
