import java.io.*;
import java.util.*;

public class main {

	public static void main(String[] args) {
		System.out.println("test");
	}



	public static float[] pagerank(int[][] adj, float alpha, float[] pers) {
		//G = aplha*P + (1-aplha) e*pers^T
		//x(t+1)^T = x^T*G
		//x(t+1)^T = apha * x(t)^T * P + (1-aplha)*pers^T
		//OU
		//(I - aplha*P)^T * x = (1-aplha) * v
		return null;
	}

}
