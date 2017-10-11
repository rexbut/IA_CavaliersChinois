# IA_CavaliersChinois

Les cavaliers chinois sont un jeu qui se joue sur une grille de 9 fois 9 lignes. Chaque ligne verticale est référencée par une lettre de A à I. Chaque ligne horizontale est référencée par un chiffre entre 1 et 9. Chaque intersection peut donc être référencée par un couple composé d’une lettre et d’un chiffre.

Chaque joueur est identifié par une couleur et possède au départ 6 cavaliers. La configuration initiale est la suivant : les cavaliers blancs sont situés sur la ligne numéro 9, les cavaliers noirs sur la ligne 1, la position centrale et les deux extrémités de la ligne étant laissées libre. Le but du jeu est de faire traverser le plateau à trois de ses cavaliers, ou de faire traverser plus de cavaliers que l’adversaire.

La représentation du jeu et ces règles sont simples mais intelligence artificielle cela peut rapidement devenir compliqué. Sachant qu’il y au maximum 24 coups possibles par tour et qu’il peut y avoir 96 tours, cela signifie qu’il y a un trop grand nombre de possibilité pour réussir à jouer parfaitement bien.

([Plus d'informations](RAPPORT.pdf)