
public class Book implements Comparable<Book> {
	int id;
	Double score;
	Double heuristicScore;
	
	//Override
    public int compareTo(Book b) {
        return this.heuristicScore.compareTo(b.heuristicScore);
    }
}
