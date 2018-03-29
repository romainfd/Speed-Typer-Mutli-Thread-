import tc.TC;

public class Timer extends Thread {
	static long delayStart = 3000;
	static long duration = 180000;
	

	public String msToText(long timeMs) {
		float remainingSec = timeMs/1000f;
    	long remainingMin = (long) remainingSec / 60;
    	remainingSec -= remainingMin * 60;
    	String time = "";
    	if (remainingMin != 0) {
    		time = remainingMin+":";
    	   	if (remainingSec <= 9.99f) {
    	   		time += "0";
    	   	}
    	}
    	time += Math.round((remainingSec*1000)/100)/10.0;
    	return time;
	}
	
    public void run() {
        long t0 = System.currentTimeMillis();
        long t = t0;
        while (t - t0 < delayStart) {
        	if (Thread.interrupted()) return;
            Writer.time.setText(msToText(delayStart - (t - t0)));
            if (t - t0 > 300) {
            	try {
					Thread.sleep(50);
				} catch (InterruptedException e) {return;}
            }
            t = System.currentTimeMillis();
        }       
        EventDispatcher.playing = true;
        Writer.inputField.setEditable(true);
        Writer.message.setText("---- The game is on ! Try to type as fast as you can ----");
        
        // on relance le compte � rebours mais depuis duration ms
        t0 = System.currentTimeMillis();
        t = t0;
        while (t - t0 < duration) {
        	if (Thread.interrupted()) return;
            Writer.time.setText(msToText(duration - (t - t0)));
            if (t - t0 > 300) {
            	try {
					Thread.sleep(50);
				} catch (InterruptedException e) { 
			        Writer.inputField.setEditable(false);
					return; 
				}
            }
            t = System.currentTimeMillis();
        }    
        Writer.inputField.setEditable(false);
        if (Thread.interrupted()) return;
        
        // on annonce � l'Event Dispatcher que le jeu est termin�
        EventDispatcher.setGo(false);
        
        // Gestion du meilleur score
        int newScore = Integer.parseInt(Writer.score.getText());
        if (newScore > Writer.bestScoreInt) {
        	// on met � jour l'affichage
        	Writer.bestScore.setText(""+newScore);
        	Writer.message.setText("---- Best score beaten ! ----");
        	// on enregistre le meilleur score
        	// en dur
        	TC.ecritureDansNouveauFichier("src/bestScore.txt");
        	TC.println(newScore);
        	TC.ecritureSortieStandard();
        	// en local dans le jeu courant
        	Writer.bestScoreInt = newScore;
        }
    }
}
