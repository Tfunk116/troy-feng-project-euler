/* -------- SOLVED -------- */

/* This is a NASTY problem. Much of the code I wrote is more intuitive on paper than in code. */

/* Consider all integer combinations of a^b for 2 <= a,b <= 5:
 * 
 * 2^2 = 4, 2^3 = 8, 2^4 = 16, 2^5 = 32
 * 3^2 = 9, 3^3 = 27, 3^4 = 81, 3^5 = 243
 * 4^2 = 16, 4^3 = 64, 4^4 = 256, 4^5 = 1024
 * 5^2 = 25, 5^3 = 125, 5^4 = 625, 5^5 = 3125
 * 
 * If they are then placed in numerical order, with any repeats removed, we get the following 
 * sequence of 15 distinct terms:
 * 
 * 4,8,9,16,25,27,32,64,81,125,243,256,625,1024,3125
 * 
 * How many distinct terms are in the sequence generated by a^b for 2 <= a,b <= N? 
 * 
 * INPUT: 2 <= N <= 10^5 */

import java.util.Scanner;

public class Euler029 {
	
	/* Thoughts/approach: Start with (N-1)^2 (all possible pairs of a^b). Then loop from 2 to N,
	 * subtracting all duplicates.
	 * 
	 * Duplicates appear for powers of each a. But they also appear for pairs like 4 and 8.
	 * 
	 * So for non-perfect power, for each of its powers, we have to subtract stuff. */
	
	final static int MAX_N = 100000;
	
	/* isPower[k] is 0 if k cannot be written as a^b. If k can be written as a^b, isPower[k] stores
	 * the smallest such a. */
	static int[] isPower;
	
	/* Count multiples of numbers from n1 to n2 between l and r, both inclusive. */
	public static int countMultiples(int n1, int n2, int l, int r) {
		int ans = 0;
		for (int x = l; x <= r; x++) {
			for (int m = n1; m <= n2; m++) {
				if (x % m == 0) {
					ans++;
					break;
				}
			}
		}
		return ans;
	}
	
	/* Given that k = a^b, return b. */
	public static int log(int a, int k) {
		int ans = 0;
		while (k != 1) {
			k /= a;
			ans++;
		}
		return ans;
	}
	
	
	public static int gcd(int a, int b) {
		if (a == b) return a;
		if (a == 0) return b;
		if (b == 0) return a;
		return gcd(b, a%b);
	}
	
	/* Count multiples of numbers of n1 between l and r, both inclusive. Only counts multiples that
	 * haven't yet been found. */
	public static int countMultiples(int n1, boolean[] searched, int l, int r) {
		int ans = 0;
		for (int x = l; x <= r; x++) {
			if (x % n1 == 0 && !searched[x]) {
				ans++;
				searched[x] = true;
			}
		}
		return ans;
	}
	
	/* Given N such that 2 <= a,b, <= N, and a power b such that a^b <= N, return number of
	 * duplicates in base a^b. Duplicates must come from lesser powers (a, a^2,...) */
	public static int countDups(int b, int n) {
		int ans = 0;
		boolean[] searched = new boolean[n+1];
		int start = 2;
		int startSearch = n/b+1;
		if (b % 2 == 0) {
			ans += n/2 - 1;
			start = Math.max(2, b/2+1);
			startSearch = n/2+1;
		}
		else {
			ans += n/b - 1;
		}
		for (int i = start; i < b; i++) {
			int gcd = gcd(i,b);
			if (gcd == 1) {
				ans += countMultiples(i, searched, startSearch, i*n/b);
			}
			else {
				ans += countMultiples(i/gcd, searched, startSearch, i*n/b);
			}
		}
		return ans;
	}

	
	public static void main(String[] args) {
		/* Precalculate numbers of the form a^b less than max input. */
		isPower = new int[MAX_N+1];
		for (int i = 2; i <= MAX_N; i++) {
			if (isPower[i] == 0 && i <= 317) {
				for (int j = i*i; j <= MAX_N; j*=i) {
					isPower[j] = i;
				}
			}
		}
		
		Scanner s = new Scanner(System.in);
		long n = Long.parseLong(s.nextLine());
		long ans = (n-1) * (n-1);
		
	/* -------- FIRST ATTEMPT. Worked for n <= 100, but under-counted some duplicates for higher powers. -------- */
		
		/*for (int k = 2; k <= n; k++) {
			if (isPower[k] != 0) {
				System.out.println("-------- CHECKING K = " + k + " -------- ");
				int a = isPower[k];
				int b = log(a,k);
				int start = 2;
				if (b % 2 == 0) {
					start = Math.max(2, b/2+1);
					ans -= n/2 - 1;
					System.out.println("Subtracted " + (n/2-1) + " for even b");
				}
				else {
					ans -= n/b - 1;
					System.out.println("Subtracted " + (n/b-1) + " for odd b");
				}
				for (int i = start; i < b; i++) {
					int y = countMultiples(i, b-1, (i-1)*n/b+1, i*n/b);
					ans -= y;
					if (gcd(b,i) != 1) {
						int gcd = gcd(b,i);
						ans -= countMultiples(i/gcd,i/gcd,(i-1)*n/b+1, i*n/b);
						ans += countMultiples(i,i,(i-1)*n/b+1, i*n/b);
						System.out.println("ADJUSTMENT: subtracted " + countMultiples(i/gcd,i/gcd,(i-1)*n/b+1, i*n/b) + " added " + countMultiples(i,i,(i-1)*n/b+1, i*n/b));
					}
					System.out.println("Subtracted " + y + " multiples of " + i + " to " + (b-1) + " between " + ((i-1)*n/b+1) + " and " + (i*n/b));
				}
			}
		}*/
		
	/* -------- FINAL SOLUTION -------- */
		for (int k = 2; k <= n; k++) {
			if (isPower[k] != 0) {
				System.out.println("-------- CHECKING K = " + k + " -------- ");
				int a = isPower[k];
				int b = log(a,k);
				int y = countDups(b, (int) n);
				System.out.println("Removed " + y + " duplicates");
				ans -= countDups(b, (int) n);
			}
		}
		System.out.println(ans);
		s.close();
	}
}