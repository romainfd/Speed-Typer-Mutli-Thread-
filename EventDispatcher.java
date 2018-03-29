import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import tc.TC;

public class EventDispatcher {
	private static final int nbCheckers = 3;
	static volatile boolean go = false; 
	static volatile boolean playing = false; 
	// est-ce que l'utilisateur a cliquï¿½ sur "Go !"
	// volatile car c'est dans Writer qu'on l'ï¿½crit true 
	// et on y accï¿½de depuis d'autre Thread pour savoir si lancï¿½ (depuis Timer dans le while du wait)
	private static ReentrantLock lock = new ReentrantLock();
	private static Condition goSignal = lock.newCondition();
	private static Condition overSignal = lock.newCondition();
	static LinkedBlockingQueue<Query> queries = new LinkedBlockingQueue<Query>();
	private static Timer timer;
	static ArrayList<String> movies = new ArrayList<String>();
	static String END_OF_GAME = "--- Game is over ---";
	
	public static void clickGo() {
		lock.lock();
		try {
			if (go) {
				// on souhaite interrompre le jeu avant la fin
	        	timer.interrupt();
			}
			setGo(!go);
		} finally {
				lock.unlock();
		}
	}
	
	public static void setGo(boolean newGo) {
		lock.lock();
		try {
			if (!newGo && go) {// on arrete le jeu pour recommencer
        		go = false;
        		playing = false;
        		overSignal.signalAll();
        		// on arrête les MovieChecker
        		InputManager.input.add(new Query(END_OF_GAME,0));
	        	Writer.message.setText("---- The game is over ----");
				Writer.button.setText("Go !");
		        Writer.button.setBackground(Color.GREEN);
			} else if (newGo && !go) { // lancer
				go = true;
				goSignal.signalAll();
				Writer.button.setText("Stop");
		        Writer.button.setBackground(Color.RED);
		        Writer.message.setText("---- Ready, set, ... ----");
				// on lance les Threads en attente (Timer et les Checker)
			}
		} finally {
				lock.unlock();
		}
	}
	
	
    public static void main(String[] args) throws InterruptedException {  	
        Writer.createAndShowGUI();
        // Gestion des mots tapé
        Thread inputManager = new InputManager();
        inputManager.start();
        // Enregistrement des films
        TC.lectureDansFichier("src/movies.txt");
        String movie = TC.lireLigne().substring(3); // remove the BOM
    	movies.add(movie);
        while (!TC.finEntree()) {
        	movie = TC.lireLigne();
        	movies.add(movie);
        }

        while (true) { 
	        timer = new Timer();
	        Writer.time.setText("0");
        	// vider la queue des queries
        	queries.clear();
        	
        	// Attente de l'appui sur go/rejouer
        	lock.lock();
        	try {
		        while (!go) {
		        	goSignal.awaitUninterruptibly();
		        }
        	} finally { lock.unlock(); }
        	// on lance le timer
	        timer.start();
        	
            // gï¿½nï¿½rer les checker
        	for (int i = 0; i < nbCheckers; i++) {
        		Checker checker = new Checker();
            	checker.start();
        	}

        	// redï¿½marrage d'une partie
        	Writer.score.setText("0");
        	Writer.inputField.setText("");
        	Writer.outputField.setText("");

	        // on attend la fin du jeu :
        	lock.lock();
        	try {
        		while (go) {
	        		overSignal.awaitUninterruptibly();
	        	}
	        } finally {
	        	lock.unlock();
	        }
        }
    }
}
