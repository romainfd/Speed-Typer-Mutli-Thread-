import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;


public class InputManager extends Thread {
	
	static LinkedBlockingQueue<Query> input = new LinkedBlockingQueue<Query>();
	private Query newQuery;
    static ArrayList<String> words = new ArrayList<String>();
    static volatile int nb = 0;
    
	public InputManager(){
	}
	
	public void run(){
		while(true){
			try {
				newQuery= input.take();
			} catch (InterruptedException e) {}
			EventDispatcher.queries.add(newQuery);
			synchronized (words) {
				words.add(newQuery.word);
				nb++;
				words.notifyAll();
			}
			Thread movieChecker = new MovieChecker(words.size()-1, newQuery);
			movieChecker.start();

		}
	}
	
}
