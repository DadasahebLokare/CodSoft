package com.app.Task1;
import java.util.Scanner;
import java.lang.Math;
public class NumberGuessingGame {

	public static void main(String arg[])
	{
		System.out.println("Welcome to Guess Number Game ");
		System.out.println("You Will be Asked to guess a number to win the game");
		System.out.println("You have Maximum 5 Attemp Limit");
		int num = 5;
		int ran = 1 + (int) (100 * Math.random());//using random method
		for (int i = 0; i < num; i++)//loop moving 5 times 
		{
			System.out.println("Enter a guess the number between 1 to 100 ");
			Scanner sc = new Scanner(System.in);
			int input = sc.nextInt();
			if (ran == input) {
				System.out.println("OOhhOO! Your Number is Correct You Win the Game.");
				break;
			} else if (ran > input && i != num - 1) 
				System.out.println("Your Guess Number is Smaller");
			 else if (ran < input && i != num - 1)
				System.out.println("Your Guess Number is Greater");
		}//for loop close
		System.out.println( "Randome Mathod Was Generated Number:" + ran);
	}//main method close
}//class close
