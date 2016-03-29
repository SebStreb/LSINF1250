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
        double[][] a = {{0, 1, 1, 1}, {0, 0, 1, 1}, {1, 0, 0, 0}, {1, 0, 1, 0}};
        double[] q = {1, 1, 1, 1};
        double[][] finish=pageRank(a, 0.85, q);
        print(finish);
    }

    public static double[][] pageRank(double[][]a, double alpha,double[] q){
        normalize(a);//Normalisation
        Matrix adj = new Matrix(a); //Début du calcul de G
        Matrix pers = new Matrix(q, 1);
        Matrix persT = pers.transpose();
        Matrix e = new Matrix(1, q.length,1);
        Matrix eTimesPers = persT.times(e);
        Matrix G = adj.timesEquals(alpha).plus(eTimesPers.timesEquals(1-alpha)); //Fin du calcul de G
        Matrix x = new Matrix(1, G. getRowDimension(), 0);//Créons un vecteur ligne de base
        x.set(0, 0, 1.0);
        Matrix result=rec(G, x, 50);
        //Algo
        //G = aplha*P + (1-aplha) pers^T*e et pas e*pers^T
        //x(t+1)^T = x^T*G
        //x(t+1)^T = apha * x(t)^T * P + (1-aplha)*pers^T

        return result.getArray();
    }

    /**
     * Actuellement, utilise la matrice G parce que fuck it
     */
    public static Matrix rec(Matrix G, Matrix x, int stop){
        if(stop==0)
            return x;
        else{
            Matrix xT = x.transpose();
            //Matrix xA=xT.times(alpha).times(a); //Tests du "vrai" algo, infructueux
            //xA.print(1, 1);
            //Matrix p = pers.times(1-alpha);
            //p.print(1, 1);
            //x=xA.plus(p);
            xT=G.times(xT);
            return rec(G, xT.transpose(), stop-1);
        }
    }

    /**
     * @ pre :  a est une matrice d'adjacence valide (carrée)
     * @ post : renvoie la version normalisée de a, c'est-à-dire avec chaque ligne
     *          divisiée par le degré du noeud qu'elle représente
     *
     *          [Remarque] La technique ligne par ligne ne donnait rien, j'ai changé par diviser chaque terme par N
     */
    public static double[][] normalize(double[][] a){
        double count;
        double[] vector = new double[a.length];
        for(int i=0; i<a.length; i++){
            count=0;
            for(int j=0; j<a[0].length; j++){
                count+=a[i][j];//Pour chaque ligne, compter le degré du noeud qu'elle représente
            }
            vector[i]=count; //Stocker dans un vecteur
        }
        for(int i=0; i<a.length; i++){
            for(int j=0; j<a[0].length; j++){
                a[i][j]=(a[i][j])/a.length; //Normaliser en divisant
            }
        }
        return a;
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
