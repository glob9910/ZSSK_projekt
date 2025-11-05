/*
SCHEMAT ALGORYTMU - WIELOWĄTKOWE SITO ERATOSTENESA (WIELOWĄTKOWE WYKREŚLANIE WIELOKROTNOŚCI):

1. Utwórz tablicę isPrime[2, end] = true
2. Wykonaj klasyczne sito do pierwiastka z end w jednym wątku:
   - dla każdej liczby i ≤ end^(1/2):
       jeśli i jest pierwsza:
           usuń jej wielokrotności
   -> po tym etapie mamy listę basePrimes — to są liczby pierwsze ≤ end^(1/2), które posłużą do wykreślania w dalszej czesci zakresu.
3. Podziel zakres [end^(1/2), end] na kilka chunków, po jednym dla każdego wątku.
4. Uruchom kilka wątków równolegle.
   Każdy wątek:
   - dla każdej liczby prime z basePrimes:
       oblicza startMultiple = pierwsza wielokrotność prime w jego fragmencie (czyli najniższa liczba ≥ "from", która jest wielokrotnością prime,ale nie mniejsza niż prime*prime),
   - następnie wykreśla wszystkie wielokrotności prime w zakresie watku.
5. Po zakończeniu pracy wszystkich wątków (join), w tablicy isPrime[] pozostają wartości true tylko dla liczb pierwszych.
6. Zbierz wszystkie liczby, które pozostały jako true.
*/

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class SieveMultiThread {
    private final int start;
    private final int end;
    private final int threadCount;

    public SieveMultiThread(int start, int end, int threadCount) {
        this.start = start;
        this.end = end;
        this.threadCount = threadCount;
    }

    public List<Integer> findPrimes() throws InterruptedException, ExecutionException {
        boolean[] isPrime = new boolean[end + 1];
        for(int i = 2; i <= end; i++) {
            isPrime[i] = true;
        }

        // Najpierw sito dla malych liczb (do pierwiastka z end) w jednym wątku
        // Pozwala to na znalezienie dzielnikow liczb nie-pierwszych. Wielokrotnosci sa wykreslane pozniej na kilku watkach rownolegle
        int sqrt = (int) Math.sqrt(end);
        for(int i = 2; i <= sqrt; i++) {
            if(isPrime[i]) {
                for(int j = i*i; j <= sqrt; j += i) {
                    isPrime[j] = false;
                }
            }
        }

        List<Integer> basePrimes = new ArrayList<>();   // podstawowe dzielniki
        for(int i = 2; i <= sqrt; i++) {
            if(isPrime[i]) basePrimes.add(i);
        }

        // Wielowątkowe usuwanie wielokrotności
        List<Thread> threads = new ArrayList<>();

        int chunkSize = (end - sqrt) / threadCount + 1;

        for(int t = 0; t < threadCount; t++) {
            final int from = sqrt + t * chunkSize;
            final int to = Math.min(end, from + chunkSize - 1);

            Thread thread = new Thread(() -> {
                // Każdy wątek: dla każdej liczby z basePrimes (dzielniki) usuwa jej wielokrotności w przydzielonym zakresie
                for(int prime : basePrimes) {
                    // startMultiple blicza pierwszą wielokrotność liczby prime, która należy do aktualnego fragment zakresu [from, to] (Żeby 2 wątki nie wykreślały tych samych liczb)
                    // prime * prime = pierwsza liczba, od której zaczynamy wykreślanie wielokrotności
                    // ((from + prime - 1) / prime) * prime = najmniejsza wielokrotność prime większa lub równa from
                    int startMultiple = Math.max(prime * prime, ((from + prime - 1) / prime) * prime);
                    for(int j = startMultiple; j <= to; j += prime) {
                        isPrime[j] = false;
                    }
                }
            });

            threads.add(thread);
            thread.start();
        }

        // Czekamy, aż wszystkie wątki zakończą pracę
        for(Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        List<Integer> primes = new ArrayList<>();   // Na razie nie zapisujemy nigdzie wyników
//        for (int i = start; i <= end; i++) {
//            if (isPrime[i]) primes.add(i);
//        }
        return primes;
    }
}
