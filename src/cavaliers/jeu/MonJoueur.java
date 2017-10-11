package cavaliers.jeu;

import cavaliers.IJoueur;
import iia.jeux.alg.NegEchecAlphaBeta;


public class MonJoueur implements IJoueur {
	public final static String BLANC = "BLANC";
	public final static String NOIR = "NOIR";
	
	private final String name = "BUTET LEGUET";
	
	private String couleurAmi;
	private String couleurEnnemi;
	
	private PlateauCavaliers plateau;
	private NegEchecAlphaBeta algo;
	
	public MonJoueur() {
		this.plateau = new PlateauCavaliers();
	}

	@Override
	public void initJoueur(int couleurAmi) {
		if(couleurAmi == IJoueur.BLANC) {
			this.couleurAmi = MonJoueur.BLANC;
			this.couleurEnnemi = MonJoueur.NOIR;
		} else {
			this.couleurAmi = MonJoueur.NOIR;
			this.couleurEnnemi = MonJoueur.BLANC;
		}
		this.algo = new NegEchecAlphaBeta(HeuristiquesCavaliers.hblanc);
	}

	@Override
	public int getNumJoueur() {
		if(this.couleurAmi == MonJoueur.BLANC) {
			return IJoueur.BLANC;
		}
		return IJoueur.NOIR;
	}

	@Override
	public String choixMouvement() {
		System.out.println("Ah, c'est a moi, le joueur " + this.couleurAmi + " de jouer... Je réfléchis...");
	    System.out.println("Voici mon plateau de jeu avant de choisir mon coup :");
	    System.out.println(this.plateau.toString());
	    
	    String coup = "xxxxx";
	    if(!this.plateau.finDePartie()) {
		    String[] coupspossibles = this.plateau.mouvementsPossibles(this.couleurAmi);
		    if(coupspossibles.length > 0) {
		    	System.out.println("Mes coups " + coupspossibles.length + ": " + String.join(", ", coupspossibles));
		    	if(coupspossibles.length == 1) {
		    		coup = coupspossibles[0];
		    	} else {
		    		coup = this.algo.meilleurCoup(this.plateau).parse();
		    	}
		    	this.plateau.play(coup, this.couleurAmi);
		    }
	    }
	    System.out.println("Mon coups : " + coup);
	    return coup;
	}

	@Override
	public void declareLeVainqueur(int colour) {
		int macouleur;
		if(this.couleurAmi == MonJoueur.BLANC) {
			macouleur = IJoueur.BLANC;
		} else {
			macouleur = IJoueur.NOIR;
		}
		
		if(macouleur == colour) {
			System.out.println(this.name + " FTW !");
		}
	}

	@Override
	public void mouvementEnnemi(String coup) {
		this.plateau.play(coup, this.couleurEnnemi);
	}

	@Override
	public String binoName() {
		return this.name;
	}
}
