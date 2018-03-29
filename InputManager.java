import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;


public class InputManager extends Thread {
	
	static LinkedBlockingQueue<Query> input = new LinkedBlockingQueue<Query>();
	private Query newQuery;
    static ArrayList<String> words = new ArrayList<String>();

	
	public InputManager(){
	}
	
	public void run(){
		while(true){
			try {
				newQuery= input.take();
			} catch (InterruptedException e) {}
			EventDispatcher.queries.add(newQuery);
			words.add(newQuery.word);
			Thread movieChecker = new MovieChecker(words.size()-1, newQuery);
			movieChecker.start();

		}
	}
	
}
