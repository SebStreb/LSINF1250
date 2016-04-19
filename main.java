import Jama.*;
import java.io.*;
import java.util.*;

public class main
{
    public static void main(String[] args) {
        System.out.println("Bienvenue dans ce programme de calcul PageRank !");
        switch (args.length) {
            case 0:
                System.out.println("Il n'y a pas de fichier à lire (Vous devez le spécifier en argument de la main).");
                break;
            case 1:
                System.out.println("Il y a un fichier à lire.");
                break;
            default:
                System.out.println("Il y a " + args.length + " fichiers à lire.");
        }
        for (int i = 0; i < args.length; i++) {
            try {
                System.out.println("Fichier n°" + (i+1));
                openFile(args[i]);
            } catch (IOException e) {
                System.err.println("Error in file : " + args[i]);
            }
        }
    }

    /**
    * Fonction qui lit un fichier contenant une matrice formatée et lance le calcul de pageRank sur celle-ci
    */
    public static void openFile(String filename) throws IOException {
        BufferedReader bf = new BufferedReader(new FileReader(filename)); //Initialisations des variables pour la lecture
        String line = bf.readLine();
        String[] tmp = line.split(","); //On sépare la chaine sur ',' pour obtenir toutes les valeurs
        int l = tmp.length;
        double[][] mat = new double[l][l];
        for (int y = 0; y < l; y++) { //Boucle for pour la première ligne déjà lue à l'initialisation
            try {
                mat[0][y] = Double.parseDouble(tmp[y]); //On essaye de lire un nombre et de l'insérer
            } catch (NumberFormatException e) { //S'il est mal encodé, remplacer par un 0 et le signaler
                System.err.println("Nombre à l'index (0," + y + ") mal encodé. Remplaçé par un 0.");
                mat[0][y] = 0;
            }
        }
        for (int x = 1; x < l; x++) { //Boucle for pour le reste de la matrice
            line = bf.readLine();
            tmp = line.split(",");
            for (int y = 0; y < l; y++) {
                try {
                    mat[x][y] = Double.parseDouble(tmp[y]);
                } catch (NumberFormatException e) {
                    System.err.println("Nombre à l'index (" + x + "," + y + ") mal encodé. Remplaçé par un 0.");
                    mat[x][y] = 0;
                }
            }
        }
        double[] q = new double[l];
        for (int x = 0; x < l; x++) { //On crée le vecteur de personnalisation, par défaut rempli de 1
            q[x] = 1;
        }
        bf.close();//fermer le buffer
        double[] ranked = pageRank(mat, 1, q); //Lancement du calcul de pageRank
        print(ranked); //Affichage du résultat
        classement(ranked); //Affichage du classement
    }

    public static double[] pageRank(double[][] adj, double alpha, double[] pers) {
        normalize(adj); //Normalisation
        Matrix p = new Matrix(adj); //Création des matrices à envoyer à la fonction récusive
        Matrix v = (new Matrix(pers, 1)).transpose(); //Vecteur de personalisation
        Matrix x = new Matrix(p.getRowDimension(), 1, 1); //Vecteur de résultat en t=0
        Matrix result = rec(x, alpha, p, v, false, 0); //Calcul du résultat en t=1000
        return normalize(result.getRowPackedCopy()); //Renvoie un vecteur colonne
    }

    /**
    * Fonction récursive pour calculer le pagerank
    * x(t+1)^T = apha * x(t)^T * P + (1-aplha)*pers^T
    */
    public static Matrix rec(Matrix x, double alpha, Matrix p, Matrix v, boolean flag, int count){
        if (flag || count > 5000) { //Fin de la récursion
            System.out.println("Il y a eu " + count + " récursions");
            return x;
        } else {
            Matrix xT = x.transpose(); //Calcul mathématique de l'algorithme
            Matrix vT = v.transpose();
            double minAplh = (1-alpha);
            Matrix temp = p.times(alpha);
            Matrix left = xT.times(temp);
            Matrix right = vT.times(minAplh);
            Matrix nXt = left.plus(right);
            flag=converge(x, nXt.transpose());
            return rec(nXt.transpose(), alpha, p, v, flag, count+1); //Récursion
        }
    }

    /**
    * Fonction qui vérifie si les deux matrices convergent, return true si elles sont égale, false sinon
    * @ pre : a et b sont deux matrices ligne valides et de même tailles
    * @ post : renvoie true si les valeurs de a et b convergent, false sinon
    */
    public static boolean converge(Matrix a, Matrix b){
        double[] xA = a.getRowPackedCopy(); //Récupérer les deux matrices
        double[] xB = b.getRowPackedCopy();
        for(int i=0; i<xA.length; i++){
            if(xB[i]-xA[i]>0.0001) //Si on trouve deux valeurs avec plus de 0.0001 d'écart, les valeurs ne convergent pas, renvoyer false
                return false;
        }
        return true; //Si on est sorti de la boucle, les valeurs convergent
    }

    /**
    * @ pre : a est un vecteur valide
    * @ post : renvoie la version normalisée de a, c'est-à-dire que la somme
    *          des éléments du vecteur renvoyée est égale à 1
    */
    public static double[] normalize(double[] a) {
        double count = 0;
        for (int i = 0; i < a.length; i++)
            count += a[i];
        for (int i = 0; i < a.length; i++) {
            if (count != 0)
                a[i] = a[i] / count;
            else
                a[i] = 1.0 / a.length;
        }
        return a;
    }

    /**
    * @ pre : a est une matrice d'adjacence valide (carrée)
    * @ post : renvoie la version normalisée de a, c'est-à-dire avec chaque ligne
    *          divisiée par le degré du noeud qu'elle représente
    *          Si la ligne valait 0, chaque valeur est remplacée par 1/N où N vaut le nombre de noeud du graphe (téléportation possible)
    */
    public static double[][] normalize(double[][] a) {
        double count;
        double[] vector = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            count = 0;
            for (int j = 0; j < a[0].length; j++) {
                count += a[i][j]; //Pour chaque colonne, compter le degré du noeud qu'elle représente
            }
            vector[i] = count; //Stocker dans un vecteur
        }
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                if (vector[i] != 0) {
                    a[i][j] = a[i][j] / vector[i]; //Normaliser en divisant
                } else {
                    a[i][j] = 1.0 / a.length;
                }
            }
        }
        return a;
    }

    /**
    * Classe les valeurs du vecteur par ordre croissant
    * @ pre : un tableau de double
    * @ post : Affiche sur la sortie standard les indices du vecteur par ordre croissant
    */
    public static void classement(double[] a) {
        System.out.print("\tClassement :");
        for (int i = 0; i < a.length; i++) {
            int imax = maxIndice(a); //Chercher l'indice de la valeur maximum
            System.out.print(" " + (imax+1) + " "); //Afficher le résultat
            a[imax] = Double.MIN_VALUE; //Stocker la valeur minimale dans la case pour ne plus qu'elle soit choisie
        }
        System.out.println();
    }

    /**
    * Retourne l'indice de la valeur maximum contenue dans un tableau de double
    */
    public static int maxIndice(double[] a) {
        double max = Double.MIN_VALUE; //Initialisation
        int imax = 0;
        for (int i = 0; i < a.length; i++) {
            if (a[i] > max) { //Si la valeur de cette case est meilleure que celle qu'on a actuellement
                imax = i; //Stocker l'indice
                max = a[i]; //Stocker la valeur
            }
        }
        return imax;
    }

    /**
    * Affiche la repréentation d'un vecteur (en colonne)
    */
    public static void print(double[] a) {
        for (int i = 0; i < a.length; i++) {
            System.out.println("\tNœud n°" + (i+1) + " : " + a[i]);
        }
    }

    /**
    * Affiche la repréentation d'une matrice
    */
    public static void print(double[][] mat) {
        for (int i = 0; i < mat.length; i++) {
            for(int j = 0; j < mat[0].length; j++){
                System.out.print(mat[i][j] + ", ");
            }
            System.out.println();
        }
    }
}
