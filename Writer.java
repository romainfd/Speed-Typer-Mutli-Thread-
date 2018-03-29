import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import tc.TC;

@SuppressWarnings("serial")
public class Writer extends JPanel implements ActionListener {
    protected static JTextArea inputField;
    // On pourrait �ventuellement le remplacer par un documentListener pour permettre de modifier les modifications du texte d�j� taper et pas juste regarder le dernier mot tap� avant un espace
    protected static JColorTextPane outputField;
    protected static JTextField message;
    protected static JTextField score;
    protected static JTextField bestScore;
    protected static int bestScoreInt;    
    protected static ReentrantLock scoreLock = new ReentrantLock(); // pour modifier le score !
    protected static JTextField time;
    protected static JButton button;

    public Writer() {
        super(new GridBagLayout());
        this.setPreferredSize(new Dimension(500, 480));

        inputField = new JTextArea(11, 20);
        inputField.setLineWrap(true);
        inputField.setEditable(false);  // tant que le timer n'est pas lanc�, on ne peut rien faire
        JScrollPane scrollPaneInput = new JScrollPane(inputField);

        // On �coute l'appui sur la touche espace
        InputMap imap = inputField.getInputMap(JComponent.WHEN_FOCUSED);
        imap.put(KeyStroke.getKeyStroke("SPACE"), "spaceAction");
        // On ex�cute le code voulu lorsqu'un appui sur la touche expace est trouv�
        ActionMap amap = inputField.getActionMap();
        amap.put("spaceAction", new AbstractAction(){
            public void actionPerformed(ActionEvent e) {
            	// action � chaque appui sur espace : r�cup�rer le dernier mot
            	String text = inputField.getText();
            	// on devrait s'assurer qu'il n'y a rien eu de tap� depuis !
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
            	InputManager.input.add(new Query(word, i));
            	// bug si d�passe la capacit� de la blockingQueue
            }
        });
        
        // on empeche d'effacer les mots d�j� valid�s
        imap.put(KeyStroke.getKeyStroke("BACK_SPACE"), "removeLetter");
        amap.put("removeLetter", new AbstractAction(){
            public void actionPerformed(ActionEvent e) {
            	Document doc = inputField.getDocument();
            	int pos = doc.getLength() - 1;
            	if (pos < 0) return;
            	char lastChar = inputField.getText().charAt(pos);
            	if (lastChar == ' ') return; // on ne peut plus enlever un mot valid�
            	try {
					inputField.getDocument().remove(pos, 1);
				} catch (BadLocationException e1) {	}
            }
        });
        
        outputField = new JColorTextPane();
        outputField.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputField);

        message = new JTextField(20) {
            @Override public void setBorder(Border border) {
                // No!
            }
        };
        message.setText("---- Welcome in SpeedTyper ! ----");
        message.setEditable(false);
        message.setHorizontalAlignment(JTextField.CENTER);
        JLabel labelMessage = new JLabel("Message");
        labelMessage.setLabelFor(message);

        score = new JTextField(5) {
            @Override public void setBorder(Border border) {
                // No!
            }
        };
        score.setText("0");
        score.setEditable(false);
        JLabel labelScore = new JLabel("            Score      ");
        labelScore.setLabelFor(score);

        
    	TC.lectureDansFichier("src/bestScore.txt");
    	bestScoreInt = TC.lireInt();
        bestScore = new JTextField(5) {
            @Override public void setBorder(Border border) {
                // No!
            }
        };
        bestScore.setText(""+bestScoreInt);
        bestScore.setEditable(false);
        JLabel labelBestScore = new JLabel("      Best score      ");
        labelBestScore.setLabelFor(bestScore);

        time = new JTextField(5) {
            @Override public void setBorder(Border border) {
                // No!
            }
        };
        time.setText("0");
        time.setEditable(false);
        JLabel labelTime = new JLabel("      Time      ");
        labelTime.setLabelFor(time);       
        
        JLabel labelTimeVide = new JLabel("   ");
        JLabel labelTimeVide2 = new JLabel("   ");
        JLabel labelTimeVide3 = new JLabel("   ");
        
        button = new JButton("Go !");
        
        //Add Components to this panel.
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        add(labelScore, c);
        add(score, c);
        add(labelBestScore, c);
        add(bestScore, c);
        add(labelTime,c);
        add(time, c);        

        c.gridwidth = GridBagConstraints.REMAINDER;
        add(labelTimeVide,c);
        add(labelTimeVide2,c);
        add(labelMessage);
        add(message, c);
        add(scrollPaneInput, c); 
        add(labelTimeVide3,c);        

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        scrollPane.setAutoscrolls(true);
        add(scrollPane, c);
        
        // Ajout du bouton et �coute
        this.add(button);
        button.addActionListener(this);
    }

    public void actionPerformed(ActionEvent evt) {
    	if (evt.getSource() == button) { // Appui sur le bouton
    		EventDispatcher.clickGo();
    		// va lancer les Checker et le Timer
    		return;
    	}
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread
     */
    protected static Writer createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("SpeedTyper");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add contents to the window.
        Writer writer = new Writer();
        frame.add(writer);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
        
        return writer;
    }


    // Pour afficher l'output avec des couleurs
    public class JColorTextPane extends JTextPane {   
    	/**
    	 * Efface le mot et le r��cris de la bonne couleur
    	 * Thread-safe
    	 */
        public synchronized void write(Query query, Color c) {
        	// synchronized pour eviter les ecritures concurrentes
        	// Met le texte de la couleur voulue
            StyleContext sc = StyleContext.getDefaultStyleContext();
            AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
            //
			try {
				getStyledDocument().remove(query.pos, query.word.length());
				getStyledDocument().insertString(query.pos, query.word, aset);
			} catch (BadLocationException e) {}
        }
        
        /**
         * Ajoute simplement le mot � la fin
         * Thread-safe
         */
        public synchronized void append(String word, Color c) {
        	// synchronized pour eviter les ecritures concurrentes
        	// Met le texte de la couleur voulue
            StyleContext sc = StyleContext.getDefaultStyleContext();
            AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
            //
			try {
				getStyledDocument().insertString(getDocument().getLength(), word+" ", aset);
			} catch (BadLocationException e) {}
        }
         
    }
}
