import java.util.ArrayList;

public class Library {
	int id;
	int signingTime;
	int noOfScanning;
	
	ArrayList<Book> booksForScanning= new ArrayList<Book>();
	ArrayList<Book> scannedBooks= new ArrayList<Book>();
	
	Book bookToScan;
	int noToScan;
	
	void scan() {
		noToScan= Math.min( noOfScanning, booksForScanning.size() );	//If there are enough books so that you can scan the maximum amount possible for this library do it. If not, scan as many as they are available.
		for(int i=0; i< noToScan; i++) {
			bookToScan=booksForScanning.get(0);	//I will always get the first element of the ArrayList
			scannedBooks.add(bookToScan);
			booksForScanning.remove(bookToScan);
			HashCode.numberOfRemBooks--;
			HashCode.books.remove(bookToScan);
			
			//Check every library(signed+unsigned) if it has this particular book in it's books for scanning(and thus if it is possible to scan it again) and if so remove it from the list (it is unnecessary to scan it again)(DANGER:DO NOT ADD IT TO IT'S SCANNED BOOKS LIST SINCE THIS WILLL BE REGISTERED AS A SCAN)
			for(Library lib: HashCode.unsignedLibraries ) {
				if(lib.booksForScanning.contains(bookToScan)) {
					lib.booksForScanning.remove(bookToScan);
				}
			}
			for(Library lib: HashCode.signedLibraries ) {
				if(lib.booksForScanning.contains(bookToScan)) {
					lib.booksForScanning.remove(bookToScan);
				}
			}
			
		
		}
	}
	
	
}

