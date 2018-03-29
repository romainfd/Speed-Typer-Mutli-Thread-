import java.awt.Color;


public class MovieChecker extends Thread{
	int j=0,n, nbMots;
	String wordTest;
	Query query;
	
	
	public MovieChecker(int n, Query query){
		this.n = n;
		this.nbMots = 1;
		this.query = query;
		this.wordTest = query.word;
	}
	
	public void run(){
		while(true){
			// on parcourt la liste croissante jusqu'au bon endroit
			while(compareString(wordTest, EventDispatcher.movies.get(j))>0){
				j++;
				// il a parcouru tous les titres dispos
				if(EventDispatcher.movies.size()==j) {
					return;
				}
			}

			// on est arrivï¿½ sur le bon titre
			if(compareString(EventDispatcher.movies.get(j), wordTest) == 0) {
	            	Writer.outputField.writeMovie(new Query(wordTest, query.pos));
	            	InputManager.updateScore(+wordTest.length());
	            	return;
			}
			
			// on n'est pas dans la liste des titres
			if(EventDispatcher.movies.get(j).toLowerCase().indexOf(wordTest.toLowerCase())!=0) return;

			// on attend qu'un nouveau mot soit ajoutï¿½ car on a un dï¿½but de titre mais pas complet
			synchronized (InputManager.words) {
				try {
					while(InputManager.nb <= n + nbMots) {
						InputManager.words.wait();
					}
				} catch (InterruptedException e) {}
			}
			// le jeu a été interrompu
			if (InputManager.words.get(n + nbMots).equals(EventDispatcher.END_OF_GAME)) {
				return;
			}
			// rajouter le nouveau mot tapé
			wordTest = wordTest + " " + InputManager.words.get(n + nbMots);
			nbMots++;
		}
	}
	
	public static int compareString(String a, String anotherString) {
		a = a.toLowerCase();
		anotherString = anotherString.toLowerCase();
		int len1 = a.length();
		int len2 = anotherString.length();
		int lim = Math.min(len1, len2);
		char v1[] = a.toCharArray();
		char v2[] = anotherString.toCharArray();
		int k = 0;
		while (k < lim) {
			char c1 = v1[k];
			char c2 = v2[k];
			if (c1 != c2) {
				return c1 - c2;
			}
			k++;
		}
		return len1 - len2;
	}
}
