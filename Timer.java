
public class Timer extends Thread {
	static long delayStart = 3000;
	static long duration = 10000;
	static boolean finished = false;
	
    public void run() {
        long t0 = System.currentTimeMillis();
        long t = t0;
        while (t - t0 < delayStart) {
            Writer.time.setText(""+Math.round((delayStart - (t - t0))/100)/10.0);
            if (t - t0 > 300) {
            	try {
					Thread.sleep(50);
				} catch (InterruptedException e) {;}
            }
            t = System.currentTimeMillis();
        }       
        Writer.inputField.setEditable(true);
        Writer.outputWrite("---- Le jeu est lancé ----");
        
        // on relance le compte à rebours mais depuis duration ms
        t0 = System.currentTimeMillis();
        t = t0;
        while (t - t0 < duration) {
            Writer.time.setText(""+Math.round((duration - (t - t0))/100)/10.0);
            if (t - t0 > 300) {
            	try {
					Thread.sleep(50);
				} catch (InterruptedException e) {;}
            }
            t = System.currentTimeMillis();
        }    
        Writer.inputField.setEditable(false);
        finished = true; // pour que les appuis sur "Space" n'ait plus d'effet
        Writer.outputWrite("---- Le jeu est terminé ----");
    }
}
