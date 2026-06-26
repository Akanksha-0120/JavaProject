//REPORT CARD FOR A COLLEGE STUDENT

import java.util.Scanner;

public class StudentGradeCalculator {
        public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Welcome to the Credit-Based Student Grade Calculator");

        System.out.print("Enter Student Name: ");
        String studentName = sc.nextLine();

        System.out.print("Enter number of subjects: ");
        int subjects = sc.nextInt();

        double totalWeightedScore = 0;
        float totalCredits = 0;
        int grandTotalMarks = 0;
        int grandMaxMarks = 0;

        sc.nextLine(); // Consume newline

        for (int i = 0; i < subjects; i++) {
            System.out.println("\nSubject " + (i + 1));

            // Theory marks and credit
            System.out.print("Does this subject have theory marks? (yes/no): ");
            String hastheorymarks = sc.nextLine().trim().toLowerCase();
            if (hastheorymarks.equals("yes")) {
              int theoryMarks;
              double theoryCredit;
              while (true) {
                System.out.print("Enter theory marks (out of 100): ");
                theoryMarks = sc.nextInt();
                if (theoryMarks > 0 && theoryMarks <= 100) break;
                System.out.println("Invalid! Enter valid number.");
              }

               System.out.print("Enter theory credit: ");
               theoryCredit = sc.nextDouble();

               int theoryGradePoint = getGradePoint(theoryMarks);
               totalWeightedScore += theoryGradePoint * theoryCredit;
               totalCredits += theoryCredit;
               grandTotalMarks += theoryMarks;
               grandMaxMarks += 100;
               sc.nextLine(); // clear newline
            }
            // Ask about practical
            System.out.print("Does this subject have practical? (yes/no): ");
            String hasPractical = sc.nextLine().trim().toLowerCase();

            if (hasPractical.equals("yes")) {
                int practicalMarks;
                double practicalCredit;
                while (true) {
                    System.out.print("Enter practical marks (out of 50): ");
                    practicalMarks = sc.nextInt();
                    if (practicalMarks > 0 && practicalMarks <= 50) break;
                    System.out.println("Invalid! Enter valid number");
                }

                System.out.print("Enter practical credit: ");
                practicalCredit = sc.nextDouble();

                // Convert practical marks to percentage out of 100
                int practicalPercent = (int)((practicalMarks / 50.0) * 100);
                int practicalGradePoint = getGradePoint(practicalPercent);

                totalWeightedScore += practicalGradePoint * practicalCredit;
                totalCredits += practicalCredit;
                grandTotalMarks += practicalMarks;
                grandMaxMarks += 50;
                sc.nextLine(); // clear newline
            }
        }

        double percentage = (double) grandTotalMarks / grandMaxMarks * 100;
        double SGPA = totalWeightedScore / totalCredits;

        // Get previous CGPA info
        System.out.print("\nEnter number of previous semesters: ");
        int prevSem = sc.nextInt();

        System.out.print("Enter CGPA up to last semester: ");
        double prevCGPA = sc.nextDouble();

        double CGPA = ((prevCGPA * prevSem) + SGPA) / (prevSem + 1);
        String overallGrade = getGradeLetter((int) percentage);

        // Report
        System.out.println("\nStudent Report Card");
        System.out.println("-------------------------------");
        System.out.println("Name: " + studentName);
        System.out.printf("Total Marks: %d / %d\n", grandTotalMarks, grandMaxMarks);
        System.out.printf("Percentage: %.2f%%\n", percentage);
        System.out.println("Grade: " + overallGrade);
        System.out.printf("Current Semester CGPA: %.2f / 10.0\n", SGPA);
        System.out.printf("Overall CGPA: %.2f / 10.0\n", CGPA);
        System.out.println("-------------------------------");

        sc.close();
    }

    // Utility: get grade point based on percentage
    public static int getGradePoint(int percent) {
        if (percent >= 90) return 10;
        else if (percent >= 80) return 9;
        else if (percent >= 70) return 8;
        else if (percent >= 60) return 7;
        else if (percent >= 50) return 6;
        else if (percent >= 40) return 5;
        else return 0; // Fail
    }
    // Utility: Grade letter based on %
    public static String getGradeLetter(int percent) {
        if (percent >= 90) return "A+";
        else if (percent >= 80) return "A";
        else if (percent >= 70) return "B";
        else if (percent >= 60) return "C";
        else if (percent >= 50) return "D";
        else if (percent >= 40) return "E";
        else return "F";
    }
}