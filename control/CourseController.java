package myControllers;

import java.util.ArrayList;
import java.util.Scanner;

import myEntities.Course;
import myEntities.Index;
import myEntities.Lesson;
import myEntities.WaitList;

//controller to take care of course related functions

public class CourseController {
	private static Scanner sc;

//add course to STARS
	public static void addCourse() {
		Course newCourse = new Course();
		Course tempCourse = new Course();
		sc = new Scanner(System.in);
		int choices;

		System.out.println("Add Course");
		System.out.println("---------------------");
		System.out.print("Enter Course Code: ");
		String newCourseCode = sc.nextLine().toUpperCase();
		newCourseCode = newCourseCode.replaceAll("\\s", "");

		tempCourse = DBController.getCourseByCourseCode(newCourseCode);

		if (tempCourse != null) {
			System.out.println("Error. Course already exist.\n" + "Returning to main menu...\n");
			return;
		}

		newCourse.setCourseCode(newCourseCode);

		System.out.print("\nEnter Course Name: ");
		newCourse.setCourseName(sc.nextLine().toUpperCase());

		ArrayList<String> allSchoolCode = new ArrayList<String>();
		allSchoolCode = DBController.getAllSchoolCode();

		do {
			System.out.println("\nChoose School Code: ");

			for (int i = 0; i < allSchoolCode.size(); i++) {
				if (i != 0) {
					System.out.println(i + ". " + allSchoolCode.get(i));
				}
			}

			while (!sc.hasNextInt()) {
				sc.next();
				System.out.print("Please enter valid option:");
			}

			option = sc.nextInt();

			if (option < 1 || option > allSchoolCode.size() - 1)
				System.out.println("Please enter a valid choice");

		} while (option < 1 || option > allSchoolCode.size() - 1);

		newCourse.setSchool(DBController.getSchoolByInitial(allSchoolCode.get(option).toUpperCase()));

		do {
			System.out.println("\nChoose Lesson Type: ");
			System.out.println(
					"1. L1 = Lecture\n" + "2. L2 = Lecture + Tutorial\n" + "3. L3 = Lecture + Tutorial + Laboratory");
			while (!sc.hasNextInt()) {
				sc.next();
				System.out.print("Please enter valid option:");
			}
			option = sc.nextInt();

			switch (option) {
			case 1:
				newCourse.setLessonType("L1");
				break;
			case 2:
				newCourse.setLessonType("L2");
				break;
			case 3:
				newCourse.setLessonType("L3");
				break;
			default:
				System.out.println("Please enter a valid choice");
				break;
			}
		} while (option < 0 || option > 3);

		sc.nextLine();

		System.out.print("\nEnter Acadamic Credits: ");
		while (!sc.hasNextInt()) {
			sc.next();
			System.out.println("Please enter valid option:");
		}
		int au = sc.nextInt();
		newCourse.setAu(Integer.toString(au));

		DBController.addCourse(newCourse);

		option = 1;
		do {
			if (option != 1) {
				System.out.println("Do you want to add another index?");
				System.out.println("1. YES\n" + "2. NO");
				option = sc.nextInt();
			}
			switch (option) {
			case 1:
				CourseController.addCourseIndex(newCourse.getCourseCode());
				break;
			case 2:
				CourseController.printAllCourses();
				return;
			default:
				System.out.println("Please enter a valid choice");
				break;
			}
			option = 0;
		} while (option < 2 || option > 2);
	}

	//Add Index for a Course to MySTARS.
	
	public static void addCourseIndex(String courseCode) {
		Index newIndex = new Index();
		Lesson newLesson = new Lesson();
		WaitList newWaitList = new WaitList();

		ArrayList<String> temp = new ArrayList<String>();

		sc = new Scanner(System.in);

		System.out.println("\nAdd Course Index");
		System.out.println("---------------------");

		boolean exist = true;
		String newIndex;

		do {
			System.out.print("Enter Course Index: ");
			newIndex = sc.nextLine().toUpperCase();

			temp = DBController.getAllIndexes();

			if (temp.contains(newIndex)) {
				System.out.println("Error. Course already exist.\n" + "Please re-enter index.\n");
			} else
				exist = false;

		} while (exist != false);

		
		newWaitList.setCourseIndex(newIndex);
		newWaitList.setNumberOfStudentWaitList("0");
		DBController.addWaitList(newWaitList);

		
		newIndex.setCourseIndex(newIndex);
		newIndex.setCourseCode(courseCode);

		int size = 0;
		do {
			System.out.print("\nEnter Size: ");
			while (!sc.hasNextInt()) {
				sc.next();
				System.out.print("Please enter valid option:");
			}
			size = sc.nextInt();
		} while (size <= 0);

		newIndex.setSize(Integer.toString(size));
		newIndex.setVacancy(Integer.toString(size));
		newIndex.setNumberOfStudent("0");
		DBController.addIndex(newIndex);

		
		newLesson.setCourseIndex(newIndex);

		String tempLessonType = DBController.getLessonTypeByCourseCode(courseCode);
		int times = 0;

		if (tempLessonType.equals("L1"))
			times = 1;
		else if (tempLessonType.equals("L2"))
			times = 2;
		else if (tempLessonType.equals("L3"))
			times = 3;

		boolean cont = true;
		int optionClass = 1;

		while (cont) {
			if (optionClass <= times) {
				// Choose Class Type
				switch (optionClass) {
				case 1:
					System.out.println("\nClass: LEC/STUDIO");
					newLesson.setClassType("LEC/STUDIO");
					break;
				case 2:
					System.out.println("\nClass: TUT");
					newLesson.setClassType("TUT");
					break;
				case 3:
					System.out.println("\nClass: LAB");
					newLesson.setClassType("LAB");
					break;
				default:
					break;
				}
				optionClass++;
			} else {
				int option = 0;
				do {
					System.out.println("\nDo you want to add another LEC/STUDIO?");
					System.out.println("1. YES\n" + "2. NO");
					while (!sc.hasNextInt()) {
						sc.next();
						System.out.print("Please enter valid option:");
					}
					option = sc.nextInt();
					if (option == 1) {
						newLesson.setClassType("LEC/STUDIO");
					} else if (option == 2) {
						cont = false;
						return;
					} else {
						System.out.println("Please enter a valid choice");
					}
				} while (option < 1 || option > 2);
			}

			newLesson = CourseController.chooseDay(newLesson);

			newLesson.setStarttime(CourseController.chooseTimeSlot(true, ""));
			newLesson.setEndtime(CourseController.chooseTimeSlot(false, newIndexDetails.getStarttime()));

			sc.nextLine();
			System.out.print("\nEnter Venue: ");
			String venue = sc.nextLine().toUpperCase();
			venue = venue.replaceAll("\\s", "");
			newLesson.setVenue(venue);

			newLesson = CourseController.chooseWeek(newLesson);

			sc.nextLine();
			System.out.print("\nEnter Group: ");
			String group = sc.nextLine().toUpperCase();
			group = group.replaceAll("\\s", "");
			newLesson.setGroup(group);

			DBController.addLesson(newLesson);
		}
	}

	// Lesson day
	public static Lesson chooseDay(Lesson newLesson) {
		int dayOption;
		sc = new Scanner(System.in);
		do {
			System.out.println("\nChoose Day: ");
			System.out.println("1. Monday\n" + "2. Tuesday\n" + "3. Wednesday\n" + "4. Thursday\n" + "5. Friday");
			while (!sc.hasNextInt()) {
				sc.next();
				System.out.print("Please enter valid option:");
			}
			dayOption = sc.nextInt();

			switch (dayOption) {
			case 1:
				newLesson.setDay("MON");
				break;
			case 2:
				newLesson.setDay("TUE");
				break;
			case 3:
				newLesson.setDay("WED");
				break;
			case 4:
				newLesson.setDay("THU");
				break;
			case 5:
				newLesson.setDay("FRI");
				break;
			default:
				System.out.println("Please enter a valid choice");
				break;
			}
		} while (dayOption < 0 || dayOption > 5);

		return newLesson;
	}

	//timeslot of specific lesson
	public static String chooseTimeSlot(boolean startend, String time) {
		int timeOption;
		sc = new Scanner(System.in);

		String[] timeSlot = { "0830", "0930", "1030", "1130", "1230", "1330", "1430", "1530", "1630", "1730", "1830",
				"1930", "2030" };

		int temp = -1;

		for (int i = 0; i < timeSlot.length; i++) {
			if (timeSlot[i].equals(time))
				temp = i;
		}

		temp++;
		int k = 1;
		do {
			if (startend)
				System.out.println("\nChoose Start Time: ");
			else {
				System.out.println("\nChoose End Time: ");
			}
			k = 1;
			for (int j = temp; j < timeSlot.length; j++) {
				System.out.println((k) + ". " + timeSlot[j]);
				k++;
			}

			while (!sc.hasNextInt()) {
				sc.next();
				System.out.print("Please enter valid option:");
			}
			optionTime = sc.nextInt();

			if (timeOption < 0 || timeOption > k - 1)
				System.out.println("Please enter a valid choice");
		} while (timeOption < 0 || timeOption > k - 1);

		return timeSlot[timeOption - 1 + temp];
	}

	// week of lessons
	public static Lesson chooseWeek(Lesson newLesson) {
		int weekOption;
		sc = new Scanner(System.in);

		do {
			System.out.println("\nChoose Week: ");
			System.out.println("1. EVERY\n" + "2. ODD\n" + "3. EVEN\n" + "4. Wk2-13");
			while (!sc.hasNextInt()) {
				sc.next();
				System.out.print("Please enter valid option:");
			}
			optionWeek = sc.nextInt();

			switch (weekOption) {
			case 1:
				newLesson.setWeek("EVERY");
				break;
			case 2:
				newLesson.setWeek("ODD");
				break;
			case 3:
				newLesson.setWeek("EVEN");
				break;
			case 4:
				newLesson.setWeek("Wk2-13");
				break;
			default:
				System.out.println("Please enter a valid choice");
				break;
			}
		} while (weekOption < 0 || weekOption > 4);

		return newLesson;
	}

	//deleting course from STARS
	
	public static void removeCourse() {
		Course tempCourse = new Course();
		sc = new Scanner(System.in);
		int option;

		System.out.println("Delete Course");
		System.out.println("---------------------");
		System.out.print("Enter course code of the course you wish to delete: ");
		String courseCode = sc.nextLine().toUpperCase();

		tempCourse = DBController.getCourseByCourseCode(courseCode);

		if (tempCourse != null) {
			CourseController.printCourseByCourseCode(courseCode);
		} else {
			System.out.println("Error. Course (" + courseCode + ") does not exist.\n" + "Returning to main menu...\n");
			return;
		}

		do {
			System.out.println("\nAre you sure you want to delete?");
			System.out.println("1. YES\n" + "2. NO");
			while (!sc.hasNextInt()) {
				sc.next();
				System.out.print("Please enter valid option:");
			}
			option = sc.nextInt();

			switch (option) {
			case 1:
				ArrayList<String> indexes = new ArrayList<String>();
				indexes = DBController.getIndexesByCourseCode(courseCode);

				boolean gotStudent = false;

				for (int i = 0; i < indexes.size(); i++) {
					String noofstud = DBController.getNoOfStudetbyIndex(indexes.get(i));
					if (!noofstud.equals("0"))
						gotStudent = true;
				}

				if (gotStudent == false) {
					for (int i = 0; i < indexes.size(); i++) {
						DBController.deleteWaitlist(indexes.get(i));
						DBController.deleteLesson(indexes.get(i));
						DBController.deleteIndexcap(indexes.get(i));

						System.out.println("Course index (" + indexes.get(i) + ") under course (" + courseCode
								+ ") has been deleted.");
					}
					if (DBController.removeCourse(courseCode))
						System.out.println("Course (" + courseCode + ") has been deleted.");
					else
						System.out.println("Error. Course (" + courseCode + ") cannot be deleted.\n"
								+ "Returning to main menu...\n");
				} else
					System.out.println("Error. Course indexes under course (" + courseCode
							+ ") cannot be deleted as there is still students in the course.\n" + "Returning to main menu...\n");
				break;
			case 2:
				return;
			default:
				System.out.println("Please enter a valid choice");
				break;
			}
		} while (option < 1 || option > 2);
	}

	// print all courses in STARS
	
	public static void printAllCourses() {
		ArrayList<Course> courseList = new ArrayList<Course>();
		courseList = DBController.getAllCourses();

		System.out.println(
				"+-----------------------------------------------------------------------------------------------+");
		System.out.format("|%-12s|%-40s|%15s|%11s|%13s|\n", "Course Code", "Course Name", "School Initial",
				"Lesson Type", "Academic Unit");
		System.out.println(
				"+-----------------------------------------------------------------------------------------------+");

		if (courseList.size() != 0) {
			for (int i = 0; i < courseList.size(); i++) {
				if (i != 0) {
					Course temp = new Course();
					temp = courseList.get(i);
					System.out.format("|%-12s|%-40s|%15s|%11s|%13s|\n", temp.getCourseCode(), temp.getCourseName(),
							temp.getSchool().getSchoolInitial(), temp.getLessonType(), temp.getAu());
				}
			}
			System.out.println(
					"+-----------------------------------------------------------------------------------------------+");
			System.out.println(
					"\nLesson Type\nL1 = Lecture\nL2 = Lecture + Tutorial\nL3 = Lecture + Tutorial + Laboratory\n");
		} else {
			System.out.println("Error. Courses does not exist.\n" + "Returning to main menu...\n");
			return;
		}
	}

	// Print course details of a certain course code
	public static void printCourseByCourseCode(String courseCode) {
		Course course = new Course();
		course = DBController.getCourseByCourseCode(courseCode);

		System.out.println(
				"\nCourse Code\t" + "Course Name\t\t\t\t" + "School Code\t" + "Lesson Type\t" + "Academic Unit");
		System.out.println(
				"-------------------------------------------------------------------------------------------------------------");

		if (course != null) {
			System.out.format("%-16s%-40s%11s\t%11s\t%13s\n", course.getCourseCode(), course.getCourseName(),
					course.getSchool().getSchoolInitial(), course.getLessonType(), course.getAu());
		} else {
			System.out.println("Error. Course (" + courseCode + ") does not exist.\n" + "Returning to main menu...\n");
			return;
		}
	}

	//Update academic units of a course
	public static void updateAcademicUnit(String courseCode) {
		sc = new Scanner(System.in);

		System.out.print("\nEnter Acadamic Credits: ");
		while (!sc.hasNextInt()) {
			sc.next();
			System.out.println("Please enter valid option:");
		}
		int au = sc.nextInt();

		if (DBController.updateAUInCourse(courseCode, Integer.toString(au))) {
			System.out.println("Academic Unit updated.\n");
		} else {
			System.out.println("Error. Unable to update academic unit.\n");
		}
	}

	// Updating main menu of details of a course
	public static void updateCourse() {
		Course tempCourse = new Course();
		sc = new Scanner(System.in);
		int choice;

		System.out.println("Update Course");
		System.out.println("---------------------");
		System.out.print("Enter course code of the course you wish to update: ");
		String courseCode = sc.nextLine().toUpperCase();

		tempCourse = DBController.getCourseByCourseCode(courseCode);

		if (tempCourse != null) {
			CourseController.printCourseByCourseCode(courseCode);

			do {
				System.out.println("\nWhat would you like do update:");
				System.out.println("1. Course Details\n" + "2. Course Indexes\n" + "3. Cancel and return to main menu");
				while (!sc.hasNextInt()) {
					sc.next();
					System.out.print("Please enter valid option:");
				}
				choice = sc.nextInt();

				switch (choice) {
				case 1:
					courseCode = CourseController.updateCourseDetails(courseCode);
					break;
				case 2:
					CourseController.updateCourseIndex(courseCode);
					break;
				case 3:
					return;
				default:
					System.out.println("Please enter a valid choice");
					break;
				}
			} while (choice < 3 || choice > 3);
		} else {
			System.out.println("Error. Course (" + courseCode + ") does not exist.\n" + "Returning to main menu...\n");
			return;
		}
	}

	//update course code of a course
	public static String updateCourseCode(String courseCode) {
		sc = new Scanner(System.in);

		System.out.print("Enter Course Code: ");
		String newCourseCode = sc.nextLine().toUpperCase();
		newCourseCode = newCourseCode.replaceAll("\\s", "");

		if (courseCode.equals(newCourseCode)) {
			System.out.println("Same course code entered.\n" + "Returning to main menu...\n");
			return courseCode;
		}

		Course tempCourse = DBController.getCourseByCourseCode(newCourseCode);

		if (tempCourse != null) {
			System.out.println("Error. Course code already exist in another course.\n" + "Returning to main menu...\n");
			return courseCode;
		}

		if (DBController.updateCourseCodeInCourse(courseCode, newCourseCode)) {
			if (DBController.updateCourseCodeInIndexCapacity(courseCode, newCourseCode)) {
				System.out.println("Coure code updated.\n");
			} else {
				System.out.println("Error. Unable to update course code under index capacity.\n");
			}
		} else {
			System.out.println("Error. Unable to update course code under course.\n");
		}
		return newCourseCode;
	}

	// update info of course
	public static String updateCourseDetails(String courseCode) {
		sc = new Scanner(System.in);
		int option;

		CourseController.printCourseByCourseCode(courseCode);

		do {
			System.out.println("\nWhat would you like do update:");
			System.out.println("1. Course Code\n" + "2. Course Name\n" + "3. School Code\n" + "4. Lesson Type\n"
					+ "5. Academic Unit\n" + "6. Cancel and return to main menu");
			while (!sc.hasNextInt()) {
				sc.next();
				System.out.print("Please enter valid option:");
			}
			option = sc.nextInt();
			switch (option) {
			case 1:
				courseCode = CourseController.updateCourseCode(courseCode);
				break;
			case 2:
				CourseController.updateCourseName(courseCode);
				break;
			case 3:
				CourseController.updateSchoolCode(courseCode);
				break;
			case 4:
				CourseController.updateLessonType(courseCode);
				break;
			case 5:
				CourseController.updateAcademicUnit(courseCode);
				break;
			case 6:
				return courseCode;
			default:
				System.out.println("Please enter a valid choice");
				break;
			}
		} while (option < 6 || option > 6);

		return courseCode;
	}

	// update index of course
	public static void updateCourseIndex(String courseCode) {
		sc = new Scanner(System.in);
		int option;
		String newIndex;
		String oldIndex;
		ArrayList<String> temp = new ArrayList<String>();

		do {
			System.out.println("\nWhat would you like do update:");
			System.out.println("1. Course Index\n" + "2. Size\n" + "3. Cancel and return to main menu");
			while (!sc.hasNextInt()) {
				sc.next();
				System.out.print("Please enter valid option:");
			}
			option = sc.nextInt();
			sc.nextLine();
			switch (option) {
			case 1:
				System.out.print("\nEnter Current Course Index: ");
				oldIndex = sc.nextLine().toUpperCase();

				temp = DBController.getAllIndexes();

				if (temp.contains(oldIndex)) {
					System.out.print("\nEnter New Course Index: ");
					newIndex = sc.nextLine().toUpperCase();

					if (newIndex.equals(oldIndex)) {
						System.out.println("Same course code entered.\n" + "Returning to main menu...\n");
						return;
					} else {
						if (DBController.updateIndexInIndexCapacity(oldIndex, newIndex)
								&& DBController.updateIndexInIndexDetails(oldIndex, newIndex)
								&& DBController.updateIndexInWaitList(oldIndex, newIndex)
								&& DBController.updateIndexInCourseRegistration(oldIndex, newIndex))
							System.out.println("Course Index Updated.");
						else
							System.out.println("Error. Unable to update course index.\n");
					}
				} else {
					System.out.println("Error. Course index does not exist.");
				}
				return;
			case 2:
				System.out.print("\nEnter Current Course Index: ");
				oldIndex = sc.nextLine().toUpperCase();

				temp = DBController.getIndexesByCourseCode(courseCode);

				if (temp.contains(oldIndex)) {
					int noOfStudent = Integer.parseInt(DBController.getNoOfStudetbyIndex(oldIndex));

					int size = 0;
					do {
						System.out.print("\nEnter New Course Size: ");
						while (!sc.hasNextInt()) {
							sc.next();
							System.out.print("Please enter valid option:");
						}
						size = sc.nextInt();
					} while (size <= noOfStudent);

					String newSize = Integer.toString(size);
					String newVacancy = Integer.toString(size - noOfStudent);

					if (DBController.updateSizeInIndexCapacity(oldIndex, newSize, newVacancy)) {
						System.out.println("Course Size Updated.");
					} else {
						System.out.println("Error. Unable to update course size.\n");
					}
				} else {
					System.out.println("Error. Course index does not exist.");
				}
				return;
			case 3:
				return;
			default:
				System.out.println("Please enter a valid choice");
				break;
			}
		} while (option < 3 || option > 3);
	}

	// updating name of course
	public static void updateCourseName(String courseCode) {
		sc = new Scanner(System.in);

		System.out.print("\nEnter new course name: ");
		String newCourseName = sc.nextLine().toUpperCase();

		if (DBController.updateCourseNameInCourse(courseCode, newCourseName)) {
			System.out.println("Course name updated.\n");
		} else {
			System.out.println("Error. Unable to update course name.\n");
		}
	}

	// updating lesson type of course
	public static void updateLessonType(String courseCode) {
		sc = new Scanner(System.in);
		int option;
		String newLessonType = "";

		do {
			System.out.println("\nChoose Lesson Type: ");
			System.out.println(
					"1. L1 = Lecture\n" + "2. L2 = Lecture + Tutorial\n" + "3. L3 = Lecture + Tutorial + Laboratory");
			while (!sc.hasNextInt()) {
				sc.next();
				System.out.print("Please enter valid option:");
			}
			option = sc.nextInt();

			switch (option) {
			case 1:
				newLessonType = "L1";
				break;
			case 2:
				newLessonType = "L2";
				break;
			case 3:
				newLessonType = "L3";
				break;
			default:
				System.out.println("Please enter a valid choice");
				break;
			}
		} while (option < 0 || option > 3);

		if (DBController.updateLessonTypeInCourse(courseCode, newLessonType)) {
			System.out.println("Lesson Type updated.\n");
		} else {
			System.out.println("Error. Unable to update lesson type.\n");
		}
	}

	// updating school code of course
	public static void updateSchoolCode(String courseCode) {
		sc = new Scanner(System.in);
		int option;

		ArrayList<String> allSchoolCode = new ArrayList<String>();
		allSchoolCode = DBController.getAllSchoolCode();

		do {
			System.out.println("\nChoose School Code: ");

			for (int i = 0; i < allSchoolCode.size(); i++) {
				if (i != 0) {
					System.out.println(i + ". " + allSchoolCode.get(i));
				}
			}

			while (!sc.hasNextInt()) {
				sc.next();
				System.out.print("Please enter valid option:");
			}

			option = sc.nextInt();

			if (option < 1 || option > allSchoolCode.size() - 1)
				System.out.println("Please enter a valid choice");

		} while (option < 1 || option > allSchoolCode.size() - 1);

		String newSchoolCode = allSchoolCode.get(option).toUpperCase();

		if (DBController.updateSchoolCodeInCourse(courseCode, newSchoolCode)) {
			System.out.println("School code updated.\n");
		} else {
			System.out.println("Error. Unable to update school code.\n");
		}
	}
