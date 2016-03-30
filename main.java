import Jama.*;
/**
 * Write a description of class PageRank here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class main
{
    public static void main(String[] args){
        //TODO, tests
        double[][] a = {{0, 1, 1, 0, 0, 0}, {0, 0, 0, 0, 0, 0}, {1, 0, 1, 0, 1, 0}, {0, 0, 0, 0, 1, 1},{0, 0, 0, 1, 0, 1}, {0, 0, 0, 1, 0, 0}};
        double[] q = {1, 1, 1, 1, 1, 1};
        double[][] finish=pageRank(a, 0.9, q);
        print(finish);
    }

    public static double[][] pageRank(double[][]adj, double alpha, double[] pers) {
        normalize(adj);//Normalisation
        Matrix p = new Matrix(adj); //Créations des matrices à envoyer à la fonction récusive
        Matrix v = new Matrix(pers, 1);//vecteur de personalisation
        Matrix x = new Matrix(1, p.getRowDimension(), 0);//Verteur de résultat en t=0
        //x.set(0, 0, 0.5);
        Matrix result=rec(alpha, x, p, v, 1000);
        //Algo
        //G = aplha*P + (1-aplha) pers^T*e et pas e*pers^T
        //x(t+1)^T = x^T*G
        //x(t+1)^T = apha * x(t)^T * P + (1-aplha)*pers^T

        return result.getArray();
    }

    /**
     * Fonction récursive pour calculer le pagerank
     */
    public static Matrix rec(double alpha, Matrix x, Matrix p, Matrix v, int stop){
        if(stop==0)
            return x;
        else{
            Matrix xT = x.transpose();
            Matrix vT = v.transpose();
            double minAplh = (1-alpha);
            Matrix temp = p.times(alpha);
            Matrix left = temp.times(xT);
            Matrix right = vT.times(minAplh);
            Matrix nXt = left.plus(right);
            return rec(alpha, nXt.transpose(), p, v, stop-1);
        }
    }

    /**
     * @ pre :  a est une matrice d'adjacence valide (carrée)
     * @ post : renvoie la version normalisée de a, c'est-à-dire avec chaque ligne
     *          divisiée par le degré du noeud qu'elle représente
     *          Si la ligne valait 0, chaque valeur est remplacée par 1/N où N vaut le nombre de noeud du graphe (téléportation possible)
     */
    public static double[][] normalize(double[][] a){
        double count;
        double[] vector = new double[a.length];
        for(int i=0; i<a.length; i++){
            count=0;
            for(int j=0; j<a[0].length; j++){
                count+=a[i][j];//Pour chaque colonne, compter le degré du noeud qu'elle représente
            }
            vector[i]=count; //Stocker dans un vecteur
        }
        for(int i=0; i<a.length; i++){
            for(int j=0; j<a[0].length; j++){
                if(vector[i]!=0){
                    a[i][j]=(a[i][j])/vector[i]; //Normaliser en divisant
                }
                else{
                    a[i][j]=1.0/a.length;
                }
            }
        }
        return a;
    }

    public static void print(double[] a){
        for(int i=0; i<a.length; i++){
            System.out.println(a[i]);
        }
    }

    public static void print(double[][] mat){
        for(int i=0; i<mat.length; i++){
            for(int j=0; j<mat[0].length; j++){
                System.out.print(mat[i][j]+ ", ");
            }
            System.out.println("");
        }
    }
}
