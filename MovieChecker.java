import java.awt.Color;


public class MovieChecker extends Thread{
	int j=0,n;
	String wordTest;
	Query query;
	
	
	public MovieChecker(int n, Query query){
		this.n =n;
		this.query = query;
		this.wordTest = query.word;
	}
	
	public void run(){
		while(true){
			String s = EventDispatcher.movies.get(0);
			System.out.println(compareString(wordTest, s));
			while(compareString(wordTest, EventDispatcher.movies.get(j))>=0){
				System.out.println(EventDispatcher.movies.get(j)+ " : "+wordTest+ " : "+j);
				j++;
				if(EventDispatcher.movies.size()==j) return;
			}
			if(EventDispatcher.movies.get(j).equals(wordTest)) {
					System.out.println("Bingo");
	            	// Writer.outputField.write(this.query, Color.BLUE);
			}
			if(wordTest.indexOf(EventDispatcher.movies.get(j))!=0) return;
			
			// dormir
			wordTest = wordTest + " " + InputManager.words.get(n);
		}
	}
	
	public static int compareString(String a, String anotherString) {
		int len1 = a.length();
		int len2 = anotherString.length();
		int lim = Math.min(len1, len2);
		char v1[] = a.toCharArray();
		char v2[] = anotherString.toCharArray();
		int k = 0;
		while (k < lim) {
			char c1 = v1[k];
			char c2 = v2[k];
			if (c1 != c2) {
				System.out.println(c1);
				System.out.println(c2);
				return c1 - c2;
			}
			k++;
		}
		return len1 - len2;
	}
}
