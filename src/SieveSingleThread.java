/*
SCHEMAT ALGORYTMU - JEDNOWĄTKOWE SITO ERATOSTENESA:

1. Utwórz tablicę isPrime[2, end] = true
2. Dla każdej liczby i ≤ pierwiastek z end:
     jeśli i jest pierwsza:
         usuń jej wielokrotności
3. Zbierz wszystkie liczby, które pozostały jako true
 */

import java.util.ArrayList;
import java.util.List;

public class SieveSingleThread {
    private final int start;
    private final int end;

    public SieveSingleThread(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public List<Integer> findPrimes() {
        if (end < 2) {
            return new ArrayList<>();
        }

        boolean[] isPrime = new boolean[end + 1];

        isPrime[2] = true;
        for (int i = 3; i <= end; i += 2) {
            isPrime[i] = true;
        }

        int sqrt = (int) Math.sqrt(end);
        for (int i = 3; i <= sqrt; i += 2) {
            if (isPrime[i]) {
                for (int j = i * i; j <= sqrt; j += 2 * i) {
                    isPrime[j] = false;
                }
            }
        }

        List<Integer> basePrimes = new ArrayList<>();
        basePrimes.add(2);
        for (int i = 3; i <= sqrt; i += 2) {
            if (isPrime[i]) {
                basePrimes.add(i);
            }
        }

        int from = sqrt + 1;
        int to = end;

        for (int prime : basePrimes) {
            if (prime * prime > to) break;

            int startMultiple = Math.max(prime * prime,
                    ((from + prime - 1) / prime) * prime);

            if (prime == 2) {
                for (int j = startMultiple; j <= to; j += prime) {
                    if (j <= end) isPrime[j] = false;
                }
            } else {
                if (startMultiple % 2 == 0) {
                    startMultiple += prime;
                }
                for (int j = startMultiple; j <= to; j += 2 * prime) {
                    if (j <= end) isPrime[j] = false;
                }
            }
        }

        List<Integer> primes = new ArrayList<>();
        /*if (start <= 2 && end >= 2) {
            primes.add(2);
        }

        int startIndex = Math.max(start, 3);
        if (startIndex % 2 == 0) {
            startIndex++;
        }

        for (int i = startIndex; i <= end; i += 2) {
            if (isPrime[i]) {
                primes.add(i);
            }
        }*/

        return primes;
    }
}
