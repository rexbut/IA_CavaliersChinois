package cavaliers.jeu;

import iia.jeux.alg.Heuristique;


public class HeuristiquesCavaliers{

	public static  Heuristique hblanc = new Heuristique() {
		@Override
		public short eval(PlateauCavaliers plateau) {			
			int cavalierFiniBlanc = plateau.nbCavalierFiniBlanc();
			int cavalierFiniNoir = plateau.nbCavalierFiniNoir();
			//System.out.println(plateau);
			
			if(plateau.finDePartie()) {
				// Gagnant
				if(cavalierFiniBlanc >= PlateauCavaliers.MIN_CAVALIER) {
					return Short.MAX_VALUE;
				// Perdu
				} else if(cavalierFiniNoir >= PlateauCavaliers.MIN_CAVALIER) {
					return -Short.MAX_VALUE;
				}
			}
			//System.out.println("nbPointBlanc : " + plateau.nbPointBlanc());
			//System.out.println("nbPointNoir : " + plateau.nbPointNoir());
			return (short) ((plateau.nbPointBlanc() - plateau.nbPointNoir()) + Math.exp(cavalierFiniBlanc - cavalierFiniNoir));
		}
	};
}
