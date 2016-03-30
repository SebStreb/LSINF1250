import Jama.*;

public class main
{
    public static void main(String[] args) {
        //TODO, tests
        double[][] a = {{0, 1, 1, 0, 0, 0}, {0, 0, 0, 0, 0, 0}, {1, 0, 1, 0, 1, 0}, {0, 0, 0, 0, 1, 1},{0, 0, 0, 1, 0, 1}, {0, 0, 0, 1, 0, 0}};
        double[] q = {1, 1, 1, 1, 1, 1};
        double[] finish = pageRank(a, 0.85, q);
        print(finish);
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

    /**
    * Affiche la repréentation d'un vecteur (en colonne)
    */
    public static void print(double[] a) {
        for (int i = 0; i < a.length; i++) {
            System.out.println(a[i]);
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
