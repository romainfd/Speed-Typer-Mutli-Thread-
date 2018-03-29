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
	// est-ce que l'utilisateur a cliqu� sur "Go !"
	// volatile car c'est dans Writer qu'on l'�crit true 
	// et on y acc�de depuis d'autre Thread pour savoir si lanc� (depuis Timer dans le while du wait)
	private static ReentrantLock lock = new ReentrantLock();
	private static Condition goSignal = lock.newCondition();
	private static Condition overSignal = lock.newCondition();
	static LinkedBlockingQueue<Query> queries = new LinkedBlockingQueue<Query>();
	private static Timer timer;
	static ArrayList<String> movies = new ArrayList<String>();;
	
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
    	System.out.println(MovieChecker.compareString("inception","A Clockwork Orange"));
        Writer.createAndShowGUI();
        // Gestion des mots tap�s
        Thread inputManager = new InputManager();
        inputManager.start();
        // Enregistrement des films
        TC.lectureDansFichier("src/movies.txt");
        String movie;
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
        	
            // g�n�rer les checker
        	for (int i = 0; i < nbCheckers; i++) {
        		Checker checker = new Checker();
            	checker.start();
        	}

        	// red�marrage d'une partie
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
