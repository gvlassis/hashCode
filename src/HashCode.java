import java.util.ArrayList;
import java.util.Collections;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class HashCode {
	static int noOfBooks;
	static int noOfLibraries;
	static int days;
	static int numberOfRemBooks=0;
	
	public static ArrayList<Book> books= new ArrayList<Book>();
	public static ArrayList<Library> unsignedLibraries= new ArrayList<Library>();
	public static ArrayList<Library> signedLibraries= new ArrayList<Library>();
	
	static Book myBook;
	static Library myLibrary;
	static int bookId;
	
	static String fileName="d";
	static int numberOfReorderings=2;
	static int noOfBooksForScanning;
			
	public static void main(String[] args) {
//----------------------------Input----------------------------------------		
		
		try {
			  BufferedReader reader = new BufferedReader(new InputStreamReader( HashCode.class.getResourceAsStream(fileName+".txt") )   );
		      
		      String line = reader.readLine();
		      String[] lineTokens = line.split(" ");
		      
		      noOfBooks=Integer.parseInt(lineTokens[0]);
		  	  noOfLibraries=Integer.parseInt(lineTokens[1]);
		  	  days=Integer.parseInt(lineTokens[2]);
		  	  
		  	  line= reader.readLine();
		  	  lineTokens = line.split(" ");
		  	
		  	  for(int i=0; i<noOfBooks; i++) {
		  		  myBook= new Book();
			  	  myBook.id=i;
			  	  myBook.score=(double) Integer.parseInt(lineTokens[i]);
			  	  books.add(myBook);
		  	  }
		  	  
		  	  
		  	  
		  	  for(int i=0; i<noOfLibraries; i++) {
		  		  line= reader.readLine();
			  	  lineTokens = line.split(" ");
			  	  
		  		  myLibrary=new Library();
		  		  myLibrary.id=i;
		  		  noOfBooksForScanning=Integer.parseInt(lineTokens[0]);
		  		  myLibrary.signingTime=Integer.parseInt(lineTokens[1]);
		  		  myLibrary.noOfScanning=Integer.parseInt(lineTokens[2]);
		  		  
		  		  line= reader.readLine();
			  	  lineTokens = line.split(" ");
			  	  for(int j=0; j<noOfBooksForScanning; j++) {
			  		  bookId= Integer.parseInt(lineTokens[j]);
			  		  myBook=books.get(bookId);
			  		  myLibrary.booksForScanning.add(myBook);
			  		  numberOfRemBooks++;
			  	  }
			  	  
			  	  unsignedLibraries.add(myLibrary);
			  	  
		  	  }
		  
		  	  
		  	  reader.close();
		  	       
		} catch(Exception ex) {
			System.out.println("Couldn't read the input");
			ex.printStackTrace();
		}	
		
//------------------------Algorithm--------------------------------------------------
		
		
//		Library libr;
//		for(int i=0; i<10; i++) {
//			libr=unsignedLibraries.get(i);
//			System.out.print("Library: "+libr.id);
//			System.out.println();
//			
//			for(Book b: libr.booksForScanning) {
//				System.out.print(b.id+"-"+b.heuristicScore+" ");
//			}
//			System.out.println();
//		}
//		
//		System.exit(0);
		
		orderBooks();
		
		Library libToSign=nextLibToSign(0);	//The initial day is 0
		int waitDays= libToSign.signingTime;
		
		for( int curDay=0; curDay<= days-1; curDay++) {
			System.out.println("Day number: "+curDay);
			
			if(waitDays==0) {
				signedLibraries.add(libToSign);
				unsignedLibraries.remove(libToSign);
				
				if(! unsignedLibraries.isEmpty() ) {	//Only do this if there is an unsigned library
					libToSign=nextLibToSign(curDay);
					if(libToSign==null) {	//This will occur if there are no remaining libraries that can contribute	
						waitDays=-1;
					}else {
						waitDays=libToSign.signingTime;
					}
					
				}
				//In case I have signed up all the libraries( before the ending of days) do nothing. This way, waitDays remains 0 and thus becomes negative after this iteration, which means that no new libraries will ever be signed up again

			}
			
			for(int i=0; i< signedLibraries.size(); i++) {
				signedLibraries.get(i).scan();
			}
			
			
			if( ( curDay %(days/numberOfReorderings)) == 0 ) {	
				orderBooks();	//It takes a significant amount of time and thus cannot run in each for loop iteration
			}
			waitDays--;
		}
		
		
//----------------------------------------Output-------------------------------
		try {
			PrintWriter writer = new PrintWriter(fileName+"_out"+".txt");
			
			int counter=0;
			for(Library lib :signedLibraries) {
				if(lib.scannedBooks.size()==0 ) {	//Counts the libraries that does't contribute and thus there is no need to sign them up in the first place
					counter++;
				}
			}
			writer.print(signedLibraries.size()-counter );	//We will sign up only the libraries that contribute
			writer.println();
			for(Library lib :signedLibraries) {
				if(lib.scannedBooks.size()!=0) {	//Orelse there are recordings of signed libraries without scannedBooks, which are not accepted in the output file
					writer.print(lib.id+" "+lib.scannedBooks.size() );
					writer.println();
					
					for(int i=0; i<lib.scannedBooks.size(); i++ ) {
						if (i==lib.scannedBooks.size()-1) {
							writer.print(lib.scannedBooks.get(i).id);
							writer.println();
						}else {
							writer.print(lib.scannedBooks.get(i).id+" ");
						}
					}
		
				}
	
				
			}
			
			writer.close();
			
		} catch(Exception ex) {
			System.out.println("Couldn't make the output");
			ex.printStackTrace();
		}
		
		System.out.println("Done!");
		
		
	}
	

	
	public static Library nextLibToSign(int curDay) {
		int productiveDays;
		int noOfBooksThatWillBeScanned;
		double libContribution;
		double curBestLibContribution=0;
		Library curBestLib=null;
		Library lib;
		
		for(int i=0; i<unsignedLibraries.size(); i++ ) {
			lib=unsignedLibraries.get(i);
			
			productiveDays=(days-curDay+1)-lib.signingTime;	//The expression on the left is: remDays-signTime=prodDays;
			noOfBooksThatWillBeScanned= Math.min( productiveDays*lib.noOfScanning, lib.booksForScanning.size() );	//An approximation to the number of books scanned from this particular library.
			
			libContribution=0;
			for(int j=0; j<noOfBooksThatWillBeScanned; j++) {
				libContribution+=lib.booksForScanning.get(j).heuristicScore;
			}
			if(libContribution>curBestLibContribution) {
				curBestLibContribution=libContribution;
				curBestLib=lib;	//In the end it will be the best lib
			}
			
		}
		return curBestLib;
		
	}
	
	public static void orderBooks() {
		int bookCopies;	
		double maxScore=0;
		double minScore=0;
		
		for(Book thisBook: books) {
			if(thisBook.score>maxScore) {
				maxScore=thisBook.score;
			}else if(thisBook.score<minScore) {
				minScore=thisBook.score;
			}
		}
		
		//Calculate books' heuristic score
		for(Book thisBook: books) {	//For each book search in each library(signed+unsigned)
			bookCopies=0;
			for(Library lib: unsignedLibraries) {
				if( lib.booksForScanning.contains(myBook) ) {
					bookCopies++;
				}
			}
			for(Library lib: signedLibraries) {
				if( lib.booksForScanning.contains(myBook) ) {
					bookCopies++;
				}
			}
			
			thisBook.heuristicScore= ( (thisBook.score-minScore)/(maxScore -minScore) )* java.lang.Math.exp(-bookCopies);
			
		}
		
		for(Library lib: unsignedLibraries) {		//For every library(signed+unsigned) I order in descending order its books(the sorting is based on the heuristic scores)(NOTE: The signed libraries are ordered for more efficient scanning and the unsigned for more efficient signing)
			Collections.sort(lib.booksForScanning, Collections.reverseOrder() );
		}
		for(Library lib: signedLibraries) {		
			Collections.sort(lib.booksForScanning, Collections.reverseOrder() );
		}
	}
}







