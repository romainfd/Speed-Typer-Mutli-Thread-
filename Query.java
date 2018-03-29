public class Query { // pour gérer un mot et sa position dans le texte
	public String word;
	public int pos;
	
	public Query(String word, int pos) {
		this.word = word;
		this.pos = pos;
	}
}