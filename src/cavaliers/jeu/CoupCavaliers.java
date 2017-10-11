package cavaliers.jeu;


import iia.jeux.modele.CoupJeu;

public class CoupCavaliers implements CoupJeu {
	
	private final static String PASSE = "PASSE";
	
	private final static int PARSE_INT = '1';
	private final static int PARSE_CHAR = 'A';
	
	/****** Attributs *******/ 

	private int initiale_ligne;
	private int initiale_colonne;
	
	private int finale_ligne;
	private int finale_colonne;
	
	private boolean passe;


	/****** Clonstructeur *******/ 
	
	public CoupCavaliers() {
		this.passe = true;
	}

	public CoupCavaliers(int initiale_ligne, int initiale_colonne, int finale_ligne, int finale_colonne) {
		this.passe = false;
		
		this.initiale_ligne = initiale_ligne;
		this.initiale_colonne = initiale_colonne;
		this.finale_ligne = finale_ligne;
		this.finale_colonne = finale_colonne;
	}
	
	public CoupCavaliers(String move) {
		this.passe = true;
		
		move = move.toUpperCase();
		if(!move.equals(PASSE)) {
			String[] pos = move.split("-");
			if(pos.length == 2 && pos[0].length() == 2 && pos[1].length() == 2) {
				this.initiale_colonne = pos[0].toCharArray()[0] - PARSE_CHAR;
				this.initiale_ligne = pos[0].toCharArray()[1] - PARSE_INT;
				
				this.finale_colonne = pos[1].toCharArray()[0] - PARSE_CHAR;
				this.finale_ligne = pos[1].toCharArray()[1] - PARSE_INT;
				
				this.passe = false;
			} else {
				System.err.println("Format position invalide : " + move);
			}
		}
	}
	
	public CoupCavaliers(byte initiale, byte finale) {
		this.passe = false;
		
		this.initiale_colonne = initiale % PlateauCavaliers.TAILLE;
		this.initiale_ligne = (initiale - this.initiale_colonne) / PlateauCavaliers.TAILLE;
		
		this.finale_colonne = finale % PlateauCavaliers.TAILLE;
		this.finale_ligne = (finale - this.finale_colonne) / PlateauCavaliers.TAILLE;
	}

	/****** Accesseurs *******/ 

	public byte getInitiale() {
		return  ((Integer) ((this.initiale_ligne * PlateauCavaliers.TAILLE) + this.initiale_colonne)).byteValue();
	}
	
	public byte getFinale() {
		return ((Integer) ((this.finale_ligne * PlateauCavaliers.TAILLE) + this.finale_colonne)).byteValue();
	}
	
	public int getInitialeLigne() {
		return this.initiale_ligne;
	}

	public int getInitialeColonne() {
		return this.initiale_colonne;
	}
	
	public int getFinaleLigne() {
		return this.finale_ligne;
	}

	public int getFinaleColonne() {
		return this.finale_colonne;
	}

	public boolean isPasse() {
		return this.passe;
	}
	
	/****** Accesseurs *******/ 
	
	public String parse() {
		if(isPasse()) {
			return PASSE;
		} else {
			String retstr = new String("");
			retstr += (char) (this.initiale_colonne + PARSE_CHAR);
			retstr += (char) (this.initiale_ligne + PARSE_INT);
			retstr += "-";
			retstr += (char) (this.finale_colonne + PARSE_CHAR);
			retstr += (char) (this.finale_ligne + PARSE_INT);
			return retstr;
		}
	}
	
	public String toString() {
		return parse();
	}

	public String toString1() {
		if(isPasse()) {
			return PASSE;
		} else {
			return "Initiale(Ligne=" + this.initiale_ligne + ";Colonne=" + this.initiale_colonne + ");Finale(Ligne=" + this.finale_ligne + ";Colonne=" + this.finale_colonne + ")";
		}
	} 
}
