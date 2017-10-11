/**
 * 
 */

package iia.jeux.alg;

import java.util.HashSet;
import java.util.List;
import java.util.Random;

import cavaliers.jeu.CoupCavaliers;
import cavaliers.jeu.PlateauCavaliers;

public class NegEchecAlphaBeta {

	/**
	 * La profondeur de recherche par défaut
	 */
	private final static int PROF_MOUVEMENT_DEFAUT = 6;
	private final static int PROF_MOUVEMENT_NORMAL = 20;
	
	private final static int PROF_DEFAUT = 3;
	private final static int PROF_NORMAL = 5;
	private final static int PROF_MAX = 6;
	
	private final static int PROF_MAX_DEFAUT = 96;
	private final static int MAX_TIME = 360000;
	private final static int MAX_COUP = 48;
	// -------------------------------------------
	// Attributs
	// -------------------------------------------

	/**
	 * La profondeur de recherche utilisée pour l'algorithme
	 */
	private int profMax;

	/**
	 * L'heuristique utilisée par l'algorithme
	 */
	private Heuristique h;

	/**
	 * Le nombre de noeuds développé par l'algorithme (intéressant pour se faire
	 * une idée du nombre de noeuds développés)
	 */
	private int nbnoeuds;

	/**
	 * Le nombre de feuilles évaluées par l'algorithme
	 */
	private int nbfeuilles;
	
	/**
	 * Le temps total
	 */
	private long time_total;

	// -------------------------------------------
	// Constructeurs
	// -------------------------------------------
	public NegEchecAlphaBeta(Heuristique h) {
		this(h, PROF_MAX_DEFAUT);
	}

	public NegEchecAlphaBeta(Heuristique h, int profMaxi) {
		this.h = h;
		profMax = profMaxi;
		System.out.println("Initialisation d'un NegEchecAlphaBeta");
		
		this.table = new HashSet<EntreeT>();
		this.time_total = 0;
	}

	// -------------------------------------------
	// Méthodes de l'interface AlgoJeu
	// -------------------------------------------
	/**
	 * Nombre de mouvement déjà effectués
	 */
	private byte mouvement;
	
	/**
	 * Le temps utilisé pour effectuer le dernier calcule
	 */
	private long time;
	
	/**
	 * La table de transpositions
	 */
	private final HashSet<EntreeT> table;
	
	/**
	 * Le meilleurCoup
	 */
	private CoupCavaliers meilleurCoup;
	
	/**
	 * Variable
	 */
	private Short g = null;
	
	public CoupCavaliers meilleurCoup(PlateauCavaliers plateau) {
		this.time = System.currentTimeMillis();
		this.nbfeuilles = 0;
		this.nbnoeuds = 0;
		
		this.mouvement = (byte) plateau.getMouvement();
		this.meilleurCoup = null;
		
		// Pour les 2 premiers coups on ne va pas en profondeur
		this.profMax = this.mouvement + PROF_DEFAUT;
		
		// Si la variable n'a jamais était initialisé
		if(this.g == null) {
			this.g = (short)(plateau.getNumJoueur() * this.h.eval(plateau));
		}
		
		short beta;
		do {
			short min = -Short.MAX_VALUE;
			short max = Short.MAX_VALUE;
			while (min < max) {
				if(this.g == min) {
					beta = (short) (this.g + 50);
				} else {
					beta = this.g;
				}
				//System.err.println("g = " + g + ", min=" + min + ", max = " + max + ", prof = " + this.profMax + ",alpha =" + (short)(beta-50) + ",beta =" + beta);
				this.g = negaEchecAlphaBetaMem(plateau, (byte)this.mouvement, (short)(beta-50), beta, plateau.getNumJoueur());
				//System.err.println("heuristique = " + this.g);
				if(this.g < beta) {
					max = this.g;
				} else {
					min = this.g;
				}
				this.table.clear();
			}
			this.profMax ++;
			//System.err.println("Time =" + (System.currentTimeMillis() - this.time) + ", reste=" + ((MAX_TIME - this.time_total) / Math.max(1, (MAX_COUP - (this.mouvement / 2)))));
		} while(this.g != Short.MAX_VALUE && testTime() && this.mouvement > PROF_MOUVEMENT_DEFAUT &&
				((this.mouvement < PROF_MOUVEMENT_NORMAL && this.profMax <= this.mouvement + PROF_NORMAL) || (this.mouvement >= PROF_MOUVEMENT_NORMAL && this.profMax <= this.mouvement + PROF_MAX)));
		
		this.table.clear();
		
		// Calcule du temps
		this.time = System.currentTimeMillis() - this.time;
		this.time_total += this.time;
		System.err.println(this);
		
		// Retourne le résultat
		if(this.meilleurCoup == null) {
			List<CoupCavaliers> coups = plateau.coupsPossibles();
			return  coups.get((new Random()).nextInt(coups.size()));
		}
		return this.meilleurCoup;
	}

	// -------------------------------------------
	// Méthodes internes
	// -------------------------------------------
	
	private boolean testTime() {
		return (System.currentTimeMillis() - this.time) < ((MAX_TIME - this.time_total) / Math.max(1, (MAX_COUP - (this.mouvement / 2))));
	}
	
	private short negaEchecAlphaBetaMem(PlateauCavaliers plateau, byte prof, short alpha, short beta, byte p) {
		short alphaInit = alpha;
		
		// Recherche dans la table de transpositions
		EntreeT entreeT =  rechercheTT(plateau.getPosition());
		if(entreeT != null && entreeT.getProf() >= this.profMax) {
			if(entreeT.isExact()) {
				return entreeT.getVal();
			} else {
				if(entreeT.isInf()) {
					alpha = (short) Math.max(alpha, entreeT.getVal());
				} else if(entreeT.isSup()) {
					beta =  (short) Math.min(beta, entreeT.getVal());
				}
				
				if(alpha >= beta) {
					return entreeT.getVal();
				}
			}
		}
		
		// Cherche le meilleur coup
		CoupCavaliers meilleurCoup = null;
		short max;
		if(plateau.finDePartie() || prof == this.profMax) {
			this.nbfeuilles++;
			return (short) (plateau.getNumJoueur() * this.h.eval(plateau));
		} else {
			this.nbnoeuds++;
			max = -Short.MAX_VALUE;
			if(entreeT != null) {
				meilleurCoup = entreeT.getMeilleurCoup();
			}
			for (CoupCavaliers coups : plateau.coupsPossibles()) {
				// Jouer le coup sur un plateau
				PlateauCavaliers newPlateau = plateau.copy();
				newPlateau.joue(coups);
				// Récuperer la valeur max
				max =  (short) Math.max(max, -negaEchecAlphaBetaMem(newPlateau, (byte)(prof + 1), (short)-beta, (short)-alpha, (byte)-p));
				if(max > alpha) {
					alpha = max;
					meilleurCoup = coups;
				}
				if(alpha >= beta) {
					break;
				}
			}
		
			// Mise à jour de la table de transpositions
			if(meilleurCoup != null) {
				boolean borne = false;
				boolean exact = !EntreeT.ExactVal;
				if(max <= alphaInit) {
					borne = EntreeT.BSup;
				} else if(max >= beta) {
					borne = EntreeT.BInf;
				} else {
					exact = EntreeT.ExactVal;
				}
				this.table.remove(plateau.getPosition());
				this.table.add(new EntreeT(plateau.getPosition(), prof, max, meilleurCoup, exact, borne));
			}
		}
		
		if(prof == this.mouvement && meilleurCoup != null) {
			//System.err.println("Update meilleur coup: " + meilleurCoup);
			this.meilleurCoup = meilleurCoup;
		}
		
		return max;
	}
	
	private EntreeT rechercheTT(boolean[] position) {
		for(EntreeT entreeT : this.table) {
			if(entreeT.equals(position)) {
				return entreeT;
			}
		}
		return null;
	}
	
	// -------------------------------------------
	// Méthodes publiques
	// -------------------------------------------
	@Override
	public String toString() {
		return "NegEchecAlphaBeta [nbnoeuds=" + this.nbnoeuds + ", nbfeuilles=" + this.nbfeuilles + ", time=" + this.time + "ms, time_total=" + this.time_total + "ms]";
	}
}
