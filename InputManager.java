import java.awt.Color;
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
			Writer.outputField.append(newQuery.word, Color.GRAY);
			synchronized (words) {
				words.add(newQuery.word);
				nb++;
				words.notifyAll();
			}
			if (!newQuery.word.equals(EventDispatcher.END_OF_GAME)) {
				// on ne cherche pas si end_of_game existe !
				EventDispatcher.queries.add(newQuery);
				Thread movieChecker = new MovieChecker(words.size()-1, newQuery);
				movieChecker.start();
			}
		}
	}
	
	public static void updateScore(int deltaScore) {
		int newScore = Integer.parseInt(Writer.score.getText()) + deltaScore;
		// on met à jour l'affichage du meilleur en score en live
		if (newScore > Writer.bestScoreInt) {
			synchronized (Writer.bestScore) {
				Writer.bestScore.setText("" + newScore);
			}
			Writer.message.setText("---- You are playing with the stars ----");
		} else if (Integer.parseInt(Writer.score.getText()) > Writer.bestScoreInt) { // on vient de repasser sous la barre du bestScore
			synchronized (Writer.bestScore) {
				Writer.bestScore.setText("" + Writer.bestScoreInt);
			}
		}
		synchronized (Writer.score) {
			Writer.score.setText("" +newScore);
		}
	}
}
