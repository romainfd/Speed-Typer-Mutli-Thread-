import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class EventDispatcher {
	private static volatile boolean go = false; 
	// est-ce que l'utilisateur a cliqué sur "Go !"
	// volatile car c'est dans Writer qu'on l'écrit true 
	// et on y accède depuis d'autre Thread pour savoir si lancé (depuis Timer dans le while du wait)
	private static ReentrantLock lock = new ReentrantLock();
	private static Condition goSignal = lock.newCondition();
	
	public static void setGo(boolean newGo) {
		lock.lock();
		try {
			if (newGo) {
					go = true;
					goSignal.signalAll();
					// on lance les Threads en attente (Timer et les Checker)
			} else {
				go = false;
			}
		} finally {
				lock.unlock();
		}
	}
	
	
    public static void main(String[] args) throws InterruptedException {
        Writer.createAndShowGUI();
        // générer les checker
        
        // jouer: while (true) {
	        // On attend que l'utilisateur lance le jeu
        	lock.lock();
        	try {
		        while (!go) {
		        	goSignal.awaitUninterruptibly();
		        }
        	} finally { lock.unlock(); }
        	// on lance le timer
	        Timer timer = new Timer();
	        timer.start();
	        // gérer la répétition
    }
}
