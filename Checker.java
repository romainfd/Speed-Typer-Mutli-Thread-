//add dependencies to your class
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

// This class checks if its word exists
public class Checker extends Thread {
    final String app_id = "918d8af1";
    final String app_key = "c1a5df723a97384087fa26e3d24a0a6e";
	String word;
	
	public Checker(String word) {
		this.word = word;
	}

	@Override
	public void run() {
		if (Timer.finished) return;
		try {
            URL url = new URL(query());
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Accept","application/json");
            urlConnection.setRequestProperty("app_id",app_id);
            urlConnection.setRequestProperty("app_key",app_key);
            
            try {
	            // read the output from the server
	            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
	            StringBuilder stringBuilder = new StringBuilder();
	
	            String line = null;
	            while ((line = reader.readLine()) != null) {
	                stringBuilder.append(line + "\n");
	            }
	            //System.out.println(stringBuilder.toString());
	            Writer.outputWrite(this.word + " existe");
	            try {
	            	Writer.scoreLock.lock();
	            	Writer.score.setText("" +(Integer.parseInt(Writer.score.getText()) + this.word.length()));
	            } finally {
	            	Writer.scoreLock.unlock();
	            }
            } catch (java.io.FileNotFoundException e) {
            	Writer.outputWrite(this.word+ " n'existe pas");
	            try {
	            	Writer.scoreLock.lock();
	            	Writer.score.setText("" +(Integer.parseInt(Writer.score.getText()) - this.word.length()));
	            } finally {
	            	Writer.scoreLock.unlock();
	            }
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.toString());
        }
	}

    private String query() {
        final String word_id = this.word.toLowerCase(); //word id is case sensitive and lowercase is required
        return "https://od-api.oxforddictionaries.com:443/api/v1/inflections/en/" + word_id;
    }
}
