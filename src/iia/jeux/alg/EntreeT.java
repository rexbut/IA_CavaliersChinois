package iia.jeux.alg;

import cavaliers.jeu.CoupCavaliers;

public class EntreeT {
	public static boolean ExactVal = true;
	public static boolean BInf = false;
	public static boolean BSup = true;
	
	/**
	 * Le plateau
	 */
	private final boolean[] position;
	
	/**
	 * La profondeur
	 */
	private final byte prof;
	
	/**
	 * La valeur
	 */
	private final short val;
	
	/**
	 * Les coordonnées initiales de la pièce (0 à 80)
	 */
	private final byte coord_init;
	
	/**
	 * Les coordonnées finales de la pièce (0 à 80)
	 */
	private final byte coord_final;
	
	/**
	 * Le flag : exact
	 */
	private final boolean exact;
	
	/**
	 * Le flag  : borne
	 */
	private final boolean borne;

	public EntreeT(boolean[] position, byte prof, short val, CoupCavaliers meilleurCoups, boolean exact, boolean borne) {
		this.position = position;
		
		this.prof = prof; 
		this.val = val;
		if(meilleurCoups.isPasse()) {
			this.coord_init = -1;
			this.coord_final = -1;
		} else {
			this.coord_init = meilleurCoups.getInitiale();
			this.coord_final = meilleurCoups.getFinale();
		}
		this.exact = exact; 
		this.borne = borne;
	}
	
	/**
	 * @return Le flag  : exact
	 */
	public boolean isExact() {
		return this.exact == EntreeT.ExactVal;
	}
	
	/**
	 * @return Le flag  : borne
	 */
	public boolean isInf() {
		return this.exact != EntreeT.ExactVal && this.borne == EntreeT.BInf;
	}
	
	/**
	 * @return Le flag  : borne
	 */
	public boolean isSup() {
		return this.exact != EntreeT.ExactVal && this.borne == EntreeT.BSup;
	}
	
	/**
	 * @return La profondeur
	 */
	public byte getProf() {
		return this.prof;
	}
	
	/**
	 * @return La valeur
	 */
	public short getVal() {
		return this.val;
	}
	
	/**
	 * @return Le plateau
	 */
	public boolean[] getPosition() {
		return this.position;
	}
	
	/**
	 * @return Le meilleur coup
	 */
	public CoupCavaliers getMeilleurCoup() {
		return new CoupCavaliers(this.coord_init, this.coord_final);
	}

	@Override
	public String toString() {
		return "EntreeT [position=" + positionToString() + ",prof=" + prof + ", val=" + val + ", coord_init="
				+ coord_init + ", coord_final=" + coord_final + ", exact="
				+ exact + ", borne=" + borne + "]";
	}
	
	public String positionToString() {
		String retstr = new String("");
		for(Boolean bit : this.position) {
			if(bit) {
				retstr += "1";
			} else {
				retstr += "0";
			}
		}
		return retstr;
	}
	
	@Override
	public boolean equals(Object object) {
		if(object == null) {
			return false;
		}
		if(object instanceof EntreeT) {
			if(this.position.length == ((EntreeT)object).getPosition().length) {
				boolean resulat = true;
				int cpt = 0; 
				while (cpt < this.position.length && resulat) {
					resulat = this.position[cpt] == ((EntreeT)object).getPosition()[cpt];
					cpt++;
				}
				return resulat;
			} 
		} else if(object instanceof boolean[]) {
			if(this.position.length == ((boolean[])object).length) {
				boolean resulat = true;
				int cpt = 0; 
				while (cpt < this.position.length && resulat) {
					resulat = this.position[cpt] == ((boolean[])object)[cpt];
					cpt++;
				}
				return resulat;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return 10;
	}
}
