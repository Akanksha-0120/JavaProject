import java.util.Scanner;
import java.util.Random;

public class GuessingGame {
    public static void main(String[] args) {
        // Create Random object to generate number
        Random random = new Random();
        int numberToGuess = random.nextInt(100) + 1; // lb= 1 to ub=100
        Scanner sc = new Scanner(System.in);
        int guess = 0;
        int maxGuess=5;
        System.out.println("Welcome to the Number Guessing Game!");
        System.out.println("I have selected a number between 1 and 100.");
        System.out.println("Can you guess it?");

        while (guess != maxGuess) {
            System.out.print("Enter your guess: ");
            int userGuess = sc.nextInt();
            guess++;

            if (userGuess==numberToGuess) {
                 System.out.println("Correct! You guessed the number in " + guess + " tries.");
            } else if (userGuess > numberToGuess) {
                 System.out.println("Too High! Try again.");
            }else if (userGuess < numberToGuess && numberToGuess - userGuess <= 5) {
                 System.out.println("You are very close, just a little below!");
           }else if (userGuess > numberToGuess && userGuess - numberToGuess <= 5) {
                 System.out.println("You are very close, just a little above!");
           } else {
                 System.out.println("Too Low! Try again.");
            }
        }
        if(guess==maxGuess)
        {
            System.out.println("Sorry, you have reached the maximum number of guess.The correct number was:"+numberToGuess);
        }
        System.out.println("Thanks for playing");
        sc.close();
    }
}