import Jama.*;
import java.io.*;
import java.util.*;

public class main
{
    public static void main(String[] args) {
        System.out.println("Bienvenue dans ce programme de calcul PageRank!");
        switch (args.length) {
            case 0:
                System.out.println("Il n'y a pas de fichier à lire.");
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

    public static void openFile(String filename) throws IOException {
        BufferedReader bf = new BufferedReader(new FileReader(filename));
        String line = bf.readLine();
        String[] tmp = line.split(",");
        int l = tmp.length;
        double[][] mat = new double[l][l];
        for (int y = 0; y < l; y++) {
            try {
                mat[0][y] = Double.parseDouble(tmp[y]);
            } catch (NumberFormatException e) {
                System.err.println("Nombre à l'index (0," + y + ") mal encodé. Remplaçé par un 0.");
                mat[0][y] = 0;
            }
        }
        for (int x = 1; x < l; x++) {
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
        for (int x = 0; x < l; x++) {
            q[x] = 1;
        }
        bf.close();
        double[] ranked = pageRank(mat, 0.85, q);
        print(ranked);
        classement(ranked);
    }

    public static double[] pageRank(double[][] adj, double alpha, double[] pers) {
        normalize(adj); //Normalisation
        Matrix p = new Matrix(adj); //Créations des matrices à envoyer à la fonction récusive
        Matrix v = (new Matrix(pers, 1)).transpose(); //vecteur de personalisation
        Matrix x = new Matrix(p.getRowDimension(), 1, 0); //Vecteur de résultat en t=0
        Matrix result = rec(x, alpha, p, v, 1000); //Calcul du résultat en t=1000
        return result.getRowPackedCopy(); //renvoie un vecteur colonne
    }

    /**
     * Fonction récursive pour calculer le pagerank
     * x(t+1)^T = apha * x(t)^T * P + (1-aplha)*pers^T
     */
    public static Matrix rec(Matrix x, double alpha, Matrix p, Matrix v, int stop){
        if (stop == 0) //Fin de la récursion
            return x;
        else {
            Matrix xT = x.transpose(); //Calcul mathématique de l'algorithme
            Matrix vT = v.transpose();
            double minAplh = (1-alpha);
            Matrix temp = p.times(alpha);
            Matrix left = xT.times(temp);
            Matrix right = vT.times(minAplh);
            Matrix nXt = left.plus(right);
            return rec(nXt.transpose(), alpha, p, v, stop-1); //Récursion
        }
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
                }
                else {
                    a[i][j] = 1.0 / a.length;
                }
            }
        }
        return a;
    }

    public static void classement(double[] a) {
        System.out.print("\tClassement :");
        for (int i = 0; i < a.length; i++) {
            int imax = maxIndice(a);
            System.out.print(" " + imax + " ");
            a[imax] = 0;
        }
        System.out.println();
    }

    public static int maxIndice(double[] a) {
        double max = 0;
        int imax = 0;
        for (int i = 0; i < a.length; i++) {
            if (a[i] > max) {
                imax = i;
                max = a[i];
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
