import java.awt.Color;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import tc.TC;

public class EventDispatcher {
	private static final int nbCheckers = 3;
	static volatile boolean go = false; 
	static volatile boolean playing = false; 
	// est-ce que l'utilisateur a cliqué sur "Go !"
	// volatile car c'est dans Writer qu'on l'écrit true 
	// et on y accède depuis d'autre Thread pour savoir si lancé (depuis Timer dans le while du wait)
	private static ReentrantLock lock = new ReentrantLock();
	private static Condition goSignal = lock.newCondition();
	private static Condition overSignal = lock.newCondition();
	static LinkedBlockingQueue<Query> queries = new LinkedBlockingQueue<Query>();
	private static Timer timer;
	
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
			} else if (newGo && !go) { // lancer
				go = true;
				goSignal.signalAll();
				Writer.button.setText("Stop");
		        Writer.message.setText("---- Ready, set, ... ----");
				// on lance les Threads en attente (Timer et les Checker)
			}
		} finally {
				lock.unlock();
		}
	}
	
	
    public static void main(String[] args) throws InterruptedException {  	
    	System.out.println("the".compareTo("the a"));
        Writer.createAndShowGUI();
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
        	
            // générer les checker
        	for (int i = 0; i < nbCheckers; i++) {
        		Checker checker = new Checker();
            	checker.start();
        	}

        	// redémarrage d'une partie
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
