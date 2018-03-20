
public class Timer {
	static Writer writer;
	static long delayStart = 3000;
	static long duration = 5000;
	static boolean finished = false;
	
    public static void main(String[] args) throws InterruptedException {
        writer = Writer.createAndShowGUI();
        long t0 = System.currentTimeMillis();
        long t = t0;
        while (t - t0 < delayStart) {
            writer.time.setText(""+Math.round((delayStart - (t - t0))/100)/10.0);
            if (t - t0 > 300) {
            	Thread.sleep(50);
            }
            t = System.currentTimeMillis();
        }       
        writer.inputField.setEditable(true);
        writer.outputWrite("---- Le jeu est lancé ----");
        
        // on relance le compte à rebours mais depuis duration ms
        t0 = System.currentTimeMillis();
        t = t0;
        while (t - t0 < duration) {
            writer.time.setText(""+Math.round((duration - (t - t0))/100)/10.0);
            if (t - t0 > 300) {
            	Thread.sleep(50);
            }
            t = System.currentTimeMillis();
        }    
        writer.inputField.setEditable(false);
        finished = true; // pour que les appuis sur "Space" n'ait plus d'effet
        writer.outputWrite("---- Le jeu est terminé ----");
    }
}
