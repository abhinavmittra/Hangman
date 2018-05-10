

import java.util.*;
import java.io.*;

class FileToWord{
	String file;
	String[] words;

	FileToWord(String file){
		this.file = file;
	}



	String readFile() throws IOException {
	    BufferedReader reader = new BufferedReader(new FileReader (file));
	    String         line = null;
	    StringBuilder  stringBuilder = new StringBuilder();
	    String         ls = System.getProperty("line.separator");    // \n for linux and \n\r for windows depends on the OS (Line Seperator)

	    try {
	        while((line = reader.readLine()) != null) {
	            stringBuilder.append(line);
	            stringBuilder.append(ls);
	        }

	        return stringBuilder.toString();
	    } 
	    finally {
	        reader.close();
	    }
	}

	String getSecretWord() {
		try{
			words = this.readFile().split(" ");
		}
			
		catch (IOException e){
			System.out.println("\t\tIOException");
		}

		Random rand = new Random();  
		int n = rand.nextInt(words.length);   //Index of word 

		return words[n];
	}

}


class Hangman{
	String secret, guess;
	boolean isWin;
	ArrayList<Character> guessedLetters;  // Using Array List because Size is Dynamic
	char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();

	Hangman(){
		this.isWin = false;
		this.secret = "";
		this.guess="";
		this.guessedLetters = new ArrayList<Character>();
		
	}

	boolean isWin(){
		if (isWin)
			return true;
		else
			return false;
	}

	boolean contains(char someChar, ArrayList<Character> someArray){
		for(int i=0;i<someArray.size();i++)
			if(someChar == someArray.get(i))  // get() keyword used like charAt() keyword for ArrayList
				return true;
		return false;	
	}

	boolean isWordGuessed(String secretWord, ArrayList<Character> lettersGuessed){  // If word guessed is correct then returns true
		int num =0;
		for (int i=0;i<secretWord.length(); i++){
			if (contains(secretWord.charAt(i),lettersGuessed))
				num+=1;
		}	
		if (num==secretWord.length())
			return true;
		else
			return false;	

			
	}
	
	String getGuessedWord(String secretWord, ArrayList<Character> lettersGuessed){   //Returns Guessed Word if a letter hasnt been guessed then it is replaced with _
		String guess="";
		for(int i=0; i<secretWord.length(); i++){

			if (contains(secretWord.charAt(i),lettersGuessed)){
				guess += secretWord.charAt(i);
				guess += ' ';
			}
			else{
				guess+= '_';
				guess+=' ';
			}
		}	
		return guess;	

	}

	String getAvailableLetters(ArrayList<Character> lettersGuessed){    // Return Available Letters
		ArrayList<Character> alpha = new ArrayList<Character>();
		for(char c : alphabet){alpha.add(c);}

		

		for(int i=0; i<lettersGuessed.size(); i++){
			alpha.remove(lettersGuessed.get(i));
		}
		

		String availableLetters="";
		for(int i=0; i<alpha.size();i++) {
			availableLetters+=alpha.get(i);
		}

		return availableLetters;
	}

	void start(String secret){
		this.secret = secret;
		char guess;
		Scanner in = new Scanner(System.in);
		ArrayList<Character>secretList = new ArrayList<Character>();  // A list has been used to store words for Multiplayer Mode
		for(char c : secret.toCharArray()){secretList.add(c);}


		
		System.out.println("I am thinking of a word that is "+ secret.length() + " letters long.");
		System.out.println("\t\t------------------------------------------------");

		int nguess=8;
		while (nguess>0){
			System.out.println("\t\tYou have "+ nguess + " guesses left.");
			System.out.println("\t\tAvailable letters: "+getAvailableLetters(guessedLetters));
			System.out.print("\t\tYour guess: ");
			
			while(true){
				try{
					guess = (in.nextLine()).charAt(0);
					break;
				}catch(StringIndexOutOfBoundsException e){
					System.out.print("\t\tYour guess: ");
				}
			}

			String guessString = Character.toString(guess);   // Converting a Character to a String
			
			if(isWordGuessed(guessString, guessedLetters)){   // If word is in guessed letters
				System.out.println("\t\tOops! You've already guessed that letter: " + getGuessedWord(secret, guessedLetters));
				System.out.println("\t\t------------------------------------------------");
			}

			else if (isWordGuessed(guessString, secretList)){  // If word is in Secret List
				guessedLetters.add(guess);
				System.out.println("\t\tGood guess: "+ getGuessedWord(secret,guessedLetters));
				System.out.println("\t\t------------------------------------------------");
				if(isWordGuessed(secret,guessedLetters))
					break;

			}else{
				guessedLetters.add(guess);
				nguess--;
				System.out.println("\t\tOops! That letter is not in my word: "+ getGuessedWord(secret,guessedLetters));
				System.out.println("\t\t------------------------------------------------");	

			}

		}

		if(isWordGuessed(secret,guessedLetters)){
			isWin=true;
			System.out.print("\033[H\033[2J");
			System.out.flush();  // Clear Buffer
			System.out.println("\t\tCongratulations, you won!");
		}
		else if (nguess==0) {
			
			System.out.print("\033[H\033[2J");
			System.out.flush();
			System.out.println("\t\tSorry, you ran out of guesses.");			
		}
		

	}


}

class SinglePlayer {
	Hangman h;
	String secret;
  
	SinglePlayer(String s){
		this.secret = s;		
		this.h = new Hangman();  // Default Constructor of Hangman Class is called which initializes all the vars
	}

	void start(){
		System.out.println("\n\n\t\t\t       Single Player Mode \n");
		System.out.print("\t\t");
		h.start(secret);
		System.out.println("\n\t\tThe word was "+ secret +"\n");
	}

}

class MultiPlayer extends SinglePlayer{
	int players;
	boolean [] arr;

	MultiPlayer(String s, int n){
		super(s);	
		this.players = n;		
		arr = new boolean[players];
	}

	void start(){
		System.out.println("\n\n\t\t\t     MultiPlayer Player Mode ");
		System.out.println("\t\t\t            "+ players+ " players \n");

		for(int i =0; i<players; i++){
			System.out.print("\t\tPlayer "+ (i+1)+ ", ");
			h = new Hangman();
			h.start(secret);
			arr[i] = h.isWin();			
		}
		System.out.println();
		for (int i=0; i<players; i++){
			
			if(arr[i])
				System.out.println("\t\tPlayer "+ (i+1)+ " won.");
			else
				System.out.println("\t\tPlayer "+ (i+1)+ " lost.");

		}
		System.out.println("\n\t\tThe word was "+ secret+"\n");

	}


}

class HangmanGame{
	public static void main(String[] args){
		Scanner in = new Scanner(System.in);
		FileToWord fw = new FileToWord("");
		int u=0;
		int x; // no. of players

		try{
			fw = new  FileToWord(args[0]);
		}catch (ArrayIndexOutOfBoundsException e){
			System.out.println("\t\tGive file address - java HangmanGame /path/to/words.txt");
			return ;
		}
		
		System.out.println("\n\n\t\t\t        Welcome to Hangman\n");
		do{
			System.out.println("\t\tFor Single Player, enter 1");
			System.out.println("\t\tFor MultiPlayer, enter 2");
			System.out.print("\t\tYour input: ");
			x = in.nextInt();

			if(x==1)
				playSingle(fw.getSecretWord());
			else if (x==2)
				playMulti(fw.getSecretWord());
			else{
				System.out.println("\t\tInput 1 or 2 only");
				return;
			}

			System.out.println("\t\tTo play again enter 0");
			System.out.print("\t\tYour input: ");
			try{
				u=in.nextInt();
			}
			catch (InputMismatchException e) {
				u=1;
			}
		}while(u==0);	

	}

	static void playSingle(String secret){
		SinglePlayer s = new SinglePlayer(secret);
		s.start();
	}

	static void playMulti(String secret){
		Scanner in = new Scanner(System.in);
		
		int players;
		System.out.print("\t\tEnter number of players: ");
		players = in.nextInt();
		
		MultiPlayer m = new MultiPlayer(secret, players);
		m.start();

	}
}
