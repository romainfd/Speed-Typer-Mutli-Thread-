import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.*;

@SuppressWarnings("serial")
public class Writer extends JPanel implements ActionListener {
    protected JTextField inputField;
    // On pourrait éventuellement le remplacer par un documentListener pour permettre de modifier les modifications du texte déjà taper et pas juste regarder le dernier mot tapé avant un espace
    protected JTextArea outputField;
    protected JTextField score;
    protected ReentrantLock scoreLock = new ReentrantLock(); // pour modifier le score !
    protected JTextField time;
    private final static String newline = "\n";

    public Writer() {
        super(new GridBagLayout());

        inputField = new JTextField(20);
        inputField.setEditable(false);  // tant que le timer n'est pas lancé, on ne peut rien faire
        Writer writer = this; // pour le passer au Checker
        // On écoute l'appui sur la touche espace
        InputMap imap = inputField.getInputMap(JComponent.WHEN_FOCUSED);
        imap.put(KeyStroke.getKeyStroke("SPACE"), "spaceAction");
        // On exécute le code voulu lorsqu'un appui sur la touche expace est trouvé
        ActionMap amap = inputField.getActionMap();
        amap.put("spaceAction", new AbstractAction(){
            public void actionPerformed(ActionEvent e) {
            	// action à chaque appui sur espace : récupérer le dernier mot
            	String text = inputField.getText();
            	// on devrait s'assurer qu'il n'y a rien eu de tapé depuis !
            	int i = text.length();
            	int iFin = i;
            	while (i > 0) {
            		if (text.charAt(i - 1) != ' ') {
            			i--;
            		} else {
            			break;
            		}
            	}
            	String word = text.substring(i, iFin);
                // System.out.println("Space Pressed: " + word);
                Checker checker = new Checker(word, writer);
                checker.start();
            }
        });

        outputField = new JTextArea(5, 20);
        outputField.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputField);

        score = new JTextField(20);
        score.setText("0");
        score.setEditable(false);
        
        time = new JTextField(20);
        time.setText("0");
        time.setEditable(false);
        
        //Add Components to this panel.
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;

        c.fill = GridBagConstraints.HORIZONTAL;
        add(inputField, c);        
        add(score, c);
        add(time, c);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        add(scrollPane, c);
    }

    public void actionPerformed(ActionEvent evt) {
        String text = inputField.getText();
        outputField.append(text + newline);
        inputField.selectAll();

        //Make sure the new text is visible, even if there
        //was a selection in the text area.
        outputField.setCaretPosition(outputField.getDocument().getLength());
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    protected static Writer createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("TextDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add contents to the window.
        Writer writer = new Writer();
        frame.add(writer);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
        
        return writer;
    }

    /**
     * Affiche dans la fenetre d'output la chaine str
     * @param str
     */
    public void outputWrite(String str) {
    	this.outputField.append(str+newline);
    }
}
