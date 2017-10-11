package cavaliers.jeu;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cavaliers.Partie1;

public class PlateauCavaliers implements Partie1 {

	/* *********** constantes *********** */

	/** Taille de la grille */
	public final static int TAILLE = 9;
	public final static int TAILLE_POSITION_TABLE = 32;
	
	private final static char CASE_VIDE = '+';
	private final static char CASE_BLANC = 'b';
	private final static char CASE_NOIR = 'n';
	
	private final static int LIGNE_BLANC = 8;
	private final static int LIGNE_NOIR = 0;
	
	final static int MIN_CAVALIER = 3;
	
	final static boolean JOUEUR_BLANC = false;
	
	/* *********** Attributs  *********** */

	/** le damier */
	private char[][] plateau;
	private boolean joueur;
	private byte mouvement;
	
	private final static Map<Boolean, Map<Byte, Boolean[]>> Table = new HashMap<Boolean, Map<Byte, Boolean[]>>();
	private boolean[] position;
	
	/* ************ Constructeurs *************** */ 

	public PlateauCavaliers(){
		this.plateau = new char[TAILLE][TAILLE];
		
		for(int x = 0; x < TAILLE; x++) {
			for(int y = 0; y < TAILLE; y++) {
				if(x == LIGNE_BLANC && (y > 0 && y < 8 && y != 4)) {
					this.plateau[x][y] = CASE_BLANC;
				} else if(x == LIGNE_NOIR && (y > 0 && y < 8 && y != 4)) {
					this.plateau[x][y] = CASE_NOIR;
				} else {
					this.plateau[x][y] = CASE_VIDE;
				}
			}
		}
		
		initPosition();
		initTable();
		this.joueur = JOUEUR_BLANC;
		this.mouvement = 0;
	}
	
	public PlateauCavaliers(char depuis[][], boolean[] position, boolean joueur, byte mouvement){
		this();
		for(int i=0; i < TAILLE; i++)
			for (int j=0; j < TAILLE; j++)
				this.plateau[i][j] = depuis[i][j];
		
		for(int bit = 0; bit < TAILLE_POSITION_TABLE; bit++) {
			this.position[bit] = position[bit];
		}
		this.joueur = joueur;
		this.mouvement = mouvement;
	}
	
	
	/* ************ Table *************** */ 
	
	public void initPosition() {
		Random random = new Random();
		this.position = new boolean[TAILLE_POSITION_TABLE];
		for(int bit = 0; bit < TAILLE_POSITION_TABLE; bit++) {
			this.position[bit] = random.nextBoolean();
		}
	}
	
	public void initTable() {
		if(PlateauCavaliers.Table.isEmpty()) {			
			initTableJoueur(false);
			initTableJoueur(true);
		}
	}
	
	public void initTableJoueur(boolean joueur) {
		Map<Byte, Boolean[]> positions = new HashMap<Byte, Boolean[]>();
		Random random = new Random();		
		for(byte cpt = 0; cpt < TAILLE * TAILLE; cpt++) {
			Boolean[] hashValue = new Boolean[TAILLE_POSITION_TABLE];
			for(int bit = 0; bit < TAILLE_POSITION_TABLE; bit++) {
				hashValue[bit] = random.nextBoolean();
			}
			positions.put(cpt, hashValue);
		}
		PlateauCavaliers.Table.put(joueur, positions);
		
		/*String retstr = new String("");
		for(Entry<Byte, Boolean[]> pos : Table.get(joueur).entrySet()) {
			retstr +=  joueur + "\t : \t" + pos.getKey() + "\t : \t";
			for(Boolean bit : pos.getValue()) {
				if(bit) {
					retstr += "1";
				} else {
					retstr += "0";
				}
			}
			retstr += "\n";
		}
		System.out.println(retstr);*/
	}
	
	public boolean[] getPosition() {
		return this.position;
	}
	
	public int getMouvement() {
		return this.mouvement;
	}
	
	public byte getNumJoueur() {
		if(this.joueur == PlateauCavaliers.JOUEUR_BLANC) {
			return 1;
		} else {
			return -1;
		}
	}
	
	public void positionJoue(CoupCavaliers coup) {
		Boolean[] initiale = Table.get(this.joueur).get(coup.getInitiale());
		Boolean[] finale = Table.get(this.joueur).get(coup.getFinale());
		for(int bit = 0; bit < TAILLE_POSITION_TABLE; bit++) {
			//System.out.println("position : " + this.position[bit] + "; initiale = " + initiale[bit] + "; initiale = " + finale[bit] + "; egale = " + ((this.position[bit] != initiale[bit]) != finale[bit]));
			this.position[bit] = (this.position[bit] != initiale[bit]) != finale[bit];
		}
	}
	
	public void positionSupprime(byte pos) {
		Boolean[] finale = Table.get(this.joueur).get(pos);
		for(int bit = 0; bit < TAILLE_POSITION_TABLE; bit++) {
			this.position[bit] = this.position[bit] != finale[bit];
		}
	}

	/************* Méthodes de l'interface PlateauJeu ****************/ 
	
	@Override
	public void setFromFile(String fileName) {
		int cpt = 0;
		try{
			BufferedReader fichier = new BufferedReader(new FileReader(fileName));
			String ligne;
			while ((ligne = fichier.readLine()) != null && cpt < TAILLE){
				if(!ligne.startsWith("%")) {
					String[] split =  ligne.split(" ");
					if(split.length > 2 && split[1].length() == TAILLE) {
						this.plateau[cpt] = split[1].toCharArray();
						cpt++;
					}
				}
			}
			fichier.close(); 
		} catch (Exception e){
			System.err.println("Erreur chargement du fichier : " + e.toString());
		}
		if(cpt < TAILLE) {
			System.err.println("Erreur lors du chargement du plateau !");
		}
	}


	@Override
	public void saveToFile(String fileName) {
		File fichier = new File(fileName);
		try {
			if(!fichier.exists()) {
				fichier.createNewFile();
				System.out.println("Création du fichier : " + fichier.getName());
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(fichier));
			writer.write("% ABCDEFGHI");
			writer.newLine();
			for(int x = 0; x < TAILLE; x++) {
				writer.write((x+1) + " ");
				for(int y = 0; y < TAILLE; y++) {
					writer.write(this.plateau[x][y]);
				}
				writer.write(" " + (x+1));
				writer.newLine();
			}
			writer.write("% ABCDEFGHI");
			writer.close();
		} catch (IOException e) {
			System.err.println("Impossible de créer le fichier : " + fichier.getName());
		}
	}


	@Override
	public boolean estValide(String move, String player) {
		return this.coupValide(player, new CoupCavaliers(move));
	}

	public String[] mouvementsPossibles() {
		if(this.joueur == PlateauCavaliers.JOUEUR_BLANC) {
			System.out.println("Coups du blanc : " + String.join(", ", mouvementsPossibles(MonJoueur.BLANC)));
			return mouvementsPossibles(MonJoueur.BLANC);
		} else {
			System.out.println("Coups du noir : " + String.join(", ", mouvementsPossibles(MonJoueur.NOIR)));
			return mouvementsPossibles(MonJoueur.NOIR);
		}
	}

	@Override
	public String[] mouvementsPossibles(String player) {
		List<CoupCavaliers> coups = this.coupsPossibles(player);
		String[] resultat = new String[coups.size()];
		for(int cpt = 0; cpt < coups.size(); cpt++) {
			resultat[cpt] = coups.get(cpt).parse();
		}
		return resultat;
	}


	@Override
	public void play(String move, String player) {
		this.joue(player, new CoupCavaliers(move));
	}
	
	/* ************ Méthodes de l'interface PlateauJeu *************** */
	
	public List<CoupCavaliers> coupsPossibles() {
		if(this.joueur == PlateauCavaliers.JOUEUR_BLANC) {
			//System.out.println("Coups du blanc : " + String.join(", ", mouvementsPossibles(MonJoueur.BLANC)));
			return coupsPossibles(MonJoueur.BLANC);
		} else {
			//System.out.println("Coups du noir : " + String.join(", ", mouvementsPossibles(MonJoueur.NOIR)));
			return coupsPossibles(MonJoueur.NOIR);
		}
	}
	
	public ArrayList<CoupCavaliers> coupsPossibles(String joueur) {
		ArrayList<CoupCavaliers> coups = new ArrayList<CoupCavaliers>();
		
		if(joueur == MonJoueur.BLANC) {
			for(int l = 1; l < TAILLE; l++){
				for(int c = 0; c < TAILLE; c++){
					if(this.plateau[l][c] == CASE_BLANC) {
						coups.addAll(this.coupsPossiblesBlanc(l, c));
					}
				}
			}
		} else {
			for(int l = 0; l < TAILLE - 1; l++){
				for(int c = 0; c < TAILLE; c++){
					if(this.plateau[l][c] == CASE_NOIR) {
						coups.addAll(this.coupsPossiblesNoir(l, c));
					}
				}
			}
		}
		
		if(coups.isEmpty()) {
			coups.add(new CoupCavaliers());
		}
		return coups;
	}
	
	public List<CoupCavaliers> coupsPossiblesBlanc(int l, int c) {
		ArrayList<CoupCavaliers> coups = new ArrayList<CoupCavaliers>();
		
		CoupCavaliers coup = new CoupCavaliers(l, c, l-1, c+2);
		if(this.coupValide(MonJoueur.BLANC, coup)) {
			coups.add(coup);
		}
		
		coup = new CoupCavaliers(l, c, l-1, c-2);
		if(this.coupValide(MonJoueur.BLANC, coup)) {
			coups.add(coup);
		}
		
		coup = new CoupCavaliers(l, c, l-2, c+1);
		if(this.coupValide(MonJoueur.BLANC, coup)) {
			coups.add(coup);
		}
		
		coup = new CoupCavaliers(l, c, l-2, c-1);
		if(this.coupValide(MonJoueur.BLANC, coup)) {
			coups.add(coup);
		}
		
		return coups;
	}
	
	public List<CoupCavaliers> coupsPossiblesNoir(int l, int c) {
		ArrayList<CoupCavaliers> coups = new ArrayList<CoupCavaliers>();

		CoupCavaliers coup = new CoupCavaliers(l, c, l+1, c+2);
		if(this.coupValide(MonJoueur.NOIR, coup)) {
			coups.add(coup);
		}
		
		coup = new CoupCavaliers(l, c, l+1, c-2);
		if(this.coupValide(MonJoueur.NOIR, coup)) {
			coups.add(coup);
		}
		
		coup = new CoupCavaliers(l, c, l+2, c+1);
		if(this.coupValide(MonJoueur.NOIR, coup)) {
			coups.add(coup);
		}
		
		coup = new CoupCavaliers(l, c, l+2, c-1);
		if(this.coupValide(MonJoueur.NOIR, coup)) {
			coups.add(coup);
		}
		
		return coups;
	}
	
	public void joue(CoupCavaliers coup) {
		if(this.joueur == PlateauCavaliers.JOUEUR_BLANC) {
			joue(MonJoueur.BLANC, coup);
		} else {
			joue(MonJoueur.NOIR, coup);
		}
	}

	public void joue(String j, CoupCavaliers coup) {
		//System.out.println("Initiale :" + this);
		if(!coup.isPasse()) {
			if(this.plateau[coup.getFinaleLigne()][coup.getFinaleColonne()] != CASE_VIDE) {
				this.positionSupprime(coup.getFinale());
			}
			this.positionJoue(coup);
			
			if(j == MonJoueur.BLANC) {
				this.plateau[coup.getFinaleLigne()][coup.getFinaleColonne()] = CASE_BLANC;
			} else {
				this.plateau[coup.getFinaleLigne()][coup.getFinaleColonne()] = CASE_NOIR;
			}
			this.plateau[coup.getInitialeLigne()][coup.getInitialeColonne()] = CASE_VIDE;
		}
		
		this.joueur = !this.joueur;
		this.mouvement++;
		
		//System.out.println("Finale :" + this);
	}

	public PlateauCavaliers copy() {
		return new PlateauCavaliers(this.plateau, this.position, this.joueur, this.mouvement);
	}

	public boolean coupValide(String j, CoupCavaliers coup) {
		// Si c'est dans le plateau
		if(	coup.getInitialeLigne() >= 0 && coup.getInitialeLigne() < TAILLE &&
			coup.getInitialeColonne() >= 0 && coup.getInitialeColonne() < TAILLE &&
			coup.getFinaleLigne() >= 0 && coup.getFinaleLigne() < TAILLE &&
			coup.getFinaleColonne() >= 0 && coup.getFinaleColonne() < TAILLE) {
			
			// Si la position de initial est valide
			if(	(j == MonJoueur.BLANC && this.plateau[coup.getInitialeLigne()][coup.getInitialeColonne()] == CASE_BLANC) || 
				(j == MonJoueur.NOIR && this.plateau[coup.getInitialeLigne()][coup.getInitialeColonne()] == CASE_NOIR)) {
				
				// Si la position final est valie
				if(	(j == MonJoueur.BLANC && this.plateau[coup.getFinaleLigne()][coup.getFinaleColonne()] != CASE_BLANC) || 
					(j == MonJoueur.NOIR && this.plateau[coup.getFinaleLigne()][coup.getFinaleColonne()] != CASE_NOIR)) {
					
					int l = coup.getFinaleLigne() - coup.getInitialeLigne();
					int c = coup.getFinaleColonne() - coup.getInitialeColonne();
					
					// Si le coup est dans le bon sens
					if( (j == MonJoueur.BLANC && ((l == -2 && c == -1) || (l == -2 && c == 1) || (l == -1 && c == -2) || (l == -1 && c == 2)) || 
						(j == MonJoueur.NOIR && ((l == 2 && c == -1) || (l == 2 && c == 1) || (l == 1 && c == -2) || (l == 1 && c == 2))))) {
						
						if(l < 0) {
							l++;
						} else {
							l--;
						}
						
						if(c < 0) {
							c++;
						} else {
							c--;
						}
					
						// Si il n'y a pas de cavalier qui obstrue le chemin
						return this.plateau[coup.getInitialeLigne() + l][coup.getInitialeColonne() + c] == CASE_VIDE;
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean finDePartie() {
		ArrayList<CoupCavaliers> coupsBlanc = this.coupsPossibles(MonJoueur.BLANC);
		ArrayList<CoupCavaliers> coupsNoir = this.coupsPossibles(MonJoueur.NOIR);	
		
		// Aucun coup
		if(coupsBlanc.size() == 0 || coupsNoir.size() == 0) {
			return true;
		// Les Coups sont des "PASSE"
		} else if(coupsBlanc.get(0).isPasse() && coupsNoir.get(0).isPasse()) {
			return true;
		// Si il y a déjà assez de cavaliers chez l'ennemi
		} else {
			int nbBlanc = 0;
			int nbNoir = 0;
			int cpt = 0;
			while(cpt < TAILLE && nbBlanc < MIN_CAVALIER && nbNoir < MIN_CAVALIER) {
				if(this.plateau[LIGNE_NOIR][cpt] == CASE_BLANC) {
					nbBlanc++;
				}
				if(this.plateau[LIGNE_BLANC][cpt] == CASE_NOIR) {
					nbNoir++;
				}
				cpt++;
			}
			return nbBlanc >= MIN_CAVALIER || nbNoir >= MIN_CAVALIER || (nbBlanc == nbBlanc() && nbBlanc >= nbNoir()) || (nbNoir == nbNoir() && nbNoir >= nbBlanc());
		}
	}
	
	/* ********************* Autres méthodes ***************** */
	
	public int nbCavalierFiniBlanc() {
		int nb = 0;
		int cpt = 0;
		while(cpt < TAILLE) {
			if(this.plateau[LIGNE_NOIR][cpt] == CASE_BLANC) {
				nb++;
			}
			cpt++;
		}
		return nb;
	}
	
	public int nbCavalierFiniNoir() {
		int nb = 0;
		int cpt = 0;
		while(cpt < TAILLE) {
			if(this.plateau[LIGNE_BLANC][cpt] == CASE_NOIR) {
				nb++;
			}
			cpt++;
		}
		return nb;
	}
	
	public short nbBlanc() {
		short nb = 0;
		for(int l = 0; l < TAILLE; l++){
			for(int c = 0; c < TAILLE; c++){
				if(this.plateau[l][c] == CASE_BLANC) {
					nb++;
				}
			}
		}
		return nb;
	}
	
	public short nbNoir() {
		short nb = 0;
		for(int l = 0; l < TAILLE; l++){
			for(int c = 0; c < TAILLE; c++){
				if(this.plateau[l][c] == CASE_NOIR) {
					nb++;
				}
			}
		}
		return nb;
	}
	
	public short nbPointBlanc() {
		short nb = 0;
		for(int l = 0; l < TAILLE; l++){
			for(int c = 0; c < TAILLE; c++){
				if(this.plateau[l][c] == CASE_BLANC) {
					nb += Math.exp((TAILLE - l -1));
				}
			}
		}
		return nb;
	}
	
	public short nbPointNoir() {
		short nb = 0;
		for(int l = 0; l < TAILLE; l++){
			for(int c = 0; c < TAILLE; c++){
				if(this.plateau[l][c] == CASE_NOIR) {
					nb += Math.exp(l);
				}
			}
		}
		return nb;
	}
	
	public String toString() {
		String retstr = new String("");
		retstr += "% ABCDEFGHI\n";
		
		for(int x = 0; x < TAILLE; x++) {
			retstr += (x+1) + " ";
			for(int y = 0; y < TAILLE; y++) {
				retstr += this.plateau[x][y];
			}
			retstr += " " + (x+1) + "\n";
		}
		
		retstr += "% ABCDEFGHI\n";
		retstr += "Mouvement = " + this.mouvement + "\n";
		retstr += "Point Blanc = " + this.nbPointBlanc() + "\n";
		retstr += "Point Noir = " + this.nbPointNoir() + "\n";
		retstr += "Position = " ;
		for(Boolean bit : this.position) {
			if(bit) {
				retstr += "1";
			} else {
				retstr += "0";
			}
		}
		retstr += "\n";
		return retstr;
	}
	
	public String positionToString() {
		String retstr = new String("");
		retstr += "Position = " ;
		for(Boolean bit : this.position) {
			if(bit) {
				retstr += "1";
			} else {
				retstr += "0";
			}
		}
		retstr += "\n";
		return retstr;
	}
}
