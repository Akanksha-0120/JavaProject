/**import java.util.Scanner;

public class StudentGradeCalculator {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Welcome to the Student Grade Calculator");
        System.out.print("Enter Student Name: ");
        String studentName = sc.nextLine();
        System.out.print("Enter number of subjects: ");
        int subject = sc.nextInt();
        int[] marks = new int[subject];
        int totalMarks = 0;

        for (int i = 0; i < subject; i++) {
            System.out.print("Enter marks for Subject " + (i + 1) + " (out of 100): ");
            marks[i] = sc.nextInt();

            if (marks[i] < 0 || marks[i] > 100) {
                System.out.println("Invalid marks entered. Please enter between 0 and 100.");
                i--; 
            } else {
                totalMarks += marks[i];
            }
        }

        double average = (double) totalMarks / subject;
        String grade;
        if (average >= 90) {
            grade = "A+";
        } else if (average >= 80) {
            grade = "A";
        } else if (average >= 70) {
            grade = "B";
        } else if (average >= 60) {
            grade = "C";
        } else if (average >= 50) {
            grade = "D";
        } else {
            grade = "F (Fail)";
        }

        double cgpa;
        if (average >= 90) {
            cgpa = 10.0;
        } else if (average >= 80) {
            cgpa = 9.0;
        } else if (average >= 70) {
            cgpa = 8.0;
        } else if (average >= 60) {
            cgpa = 7.0;
        } else if (average >= 50) {
            cgpa = 6.0;
        } else if (average >= 40) {
            cgpa = 5.0;
        } else {
            cgpa = 0.0;
        } 

        System.out.println("\nStudent Report Card");
        System.out.println("------------------------");
        System.out.println("Name: " + studentName);
        System.out.println("Subjects: " + subject);
        System.out.println("Total Marks: " + totalMarks + " / " + (subject * 100));
        System.out.printf("Average: %.2f%%\n", average);
        System.out.printf("CGPA: %.1f / 10.0\n", cgpa);
        System.out.println("Grade: " + grade);
        System.out.println("------------------------");

        sc.close();
    }
}**/
/**import java.util.Scanner;

public class StudentGradeCalculator {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("🎓 Welcome to the Student Grade Calculator");

        System.out.print("Enter Student Name: ");
        String studentName = sc.nextLine();

        System.out.print("Enter number of subjects: ");
        int subjects = sc.nextInt();

        int totalTheoryMarks = 0;
        int totalPracticalMarks = 0;
        int maxTheoryMarks = 0;
        int maxPracticalMarks = 0;

        sc.nextLine(); // consume leftover newline

        for (int i = 0; i < subjects; i++) {
            System.out.println("\n📘 Subject " + (i + 1));

            // Theory marks input
            int theory;
            while (true) {
                System.out.print("Enter theory marks (out of 100): ");
                theory = sc.nextInt();
                if (theory >= 0 && theory <= 100) break;
                System.out.println("❌ Invalid! Enter between 0 and 100.");
            }
            totalTheoryMarks += theory;
            maxTheoryMarks += 100;

            // Ask if this subject has practical
            System.out.print("Does this subject have practical marks? (yes/no): ");
            sc.nextLine(); // consume newline
            String hasPractical = sc.nextLine().trim().toLowerCase();

            if (hasPractical.equals("yes")) {
                int practical;
                while (true) {
                    System.out.print("Enter practical marks (out of 50): ");
                    practical = sc.nextInt();
                    if (practical >= 0 && practical <= 50) break;
                    System.out.println("❌ Invalid! Enter between 0 and 50.");
                }
                totalPracticalMarks += practical;
                maxPracticalMarks += 50;
                sc.nextLine(); // clear newline for next loop
            }
        }

        int totalMarks = totalTheoryMarks + totalPracticalMarks;
        int maxTotalMarks = maxTheoryMarks + maxPracticalMarks;
        double percentage = (double) totalMarks / maxTotalMarks * 100;

        // Grade logic
        String grade;
        if (percentage >= 90) grade = "A+";
        else if (percentage >= 80) grade = "A";
        else if (percentage >= 70) grade = "B";
        else if (percentage >= 60) grade = "C";
        else if (percentage >= 50) grade = "D";
        else grade = "F (Fail)";

        // CGPA logic
        double currentCGPA;
        if (percentage >= 90) currentCGPA = 10.0;
        else if (percentage >= 80) currentCGPA = 9.0;
        else if (percentage >= 70) currentCGPA = 8.0;
        else if (percentage >= 60) currentCGPA = 7.0;
        else if (percentage >= 50) currentCGPA = 6.0;
        else if (percentage >= 40) currentCGPA = 5.0;
        else currentCGPA = 0.0;

        // Previous semester info
        System.out.print("\nEnter number of previous semesters: ");
        int previousSemesters = sc.nextInt();

        System.out.print("Enter your CGPA up to last semester: ");
        double previousCGPA = sc.nextDouble();

        int totalSemesters = previousSemesters + 1;
        double overallCGPA = ((previousCGPA * previousSemesters) + currentCGPA) / totalSemesters;

        // Report card output
        System.out.println("\n📘 Student Report Card");
        System.out.println("-------------------------------");
        System.out.println("👤 Name: " + studentName);
        System.out.println("📚 Subjects: " + subjects);
        System.out.println("🧠 Theory Marks: " + totalTheoryMarks + " / " + maxTheoryMarks);
        System.out.println("🧪 Practical Marks: " + totalPracticalMarks + " / " + maxPracticalMarks);
        System.out.println("📝 Total Marks: " + totalMarks + " / " + maxTotalMarks);
        System.out.printf("📊 Percentage: %.2f%%\n", percentage);
        System.out.println("🎖️ Grade: " + grade);
        System.out.printf("📘 Current CGPA: %.1f / 10.0\n", currentCGPA);
        System.out.printf("📈 Overall CGPA: %.2f / 10.0\n", overallCGPA);
        System.out.println("-------------------------------");

        sc.close();
    }
}**/

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
            int theoryMarks;
            double theoryCredit;
            while (true) {
                System.out.print("Enter theory marks (out of 100): ");
                theoryMarks = sc.nextInt();
                if (theoryMarks >= 0 && theoryMarks <= 100) break;
                System.out.println("Invalid! Enter between 0 and 100.");
            }

            System.out.print("Enter theory credit: ");
            theoryCredit = sc.nextDouble();

            int theoryGradePoint = getGradePoint(theoryMarks);
            totalWeightedScore += theoryGradePoint * theoryCredit;
            totalCredits += theoryCredit;
            grandTotalMarks += theoryMarks;
            grandMaxMarks += 100;
            sc.nextLine(); // clear newline

            // Ask about practical
            System.out.print("Does this subject have practical? (yes/no): ");
            String hasPractical = sc.nextLine().trim().toLowerCase();

            if (hasPractical.equals("yes")) {
                int practicalMarks;
                double practicalCredit;
                while (true) {
                    System.out.print("Enter practical marks (out of 50): ");
                    practicalMarks = sc.nextInt();
                    if (practicalMarks >= 0 && practicalMarks <= 50) break;
                    System.out.println("Invalid! Enter between 0 and 50.");
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