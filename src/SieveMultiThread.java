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
import java.util.BitSet;
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
        if (end < 2) return new ArrayList<>();

        BitSet isPrime = new BitSet(end + 1);
        isPrime.set(2);
        for (int i = 3; i <= end; i += 2) {
            isPrime.set(i);
        }

        int sqrt = (int) Math.sqrt(end);

        for (int i = 3; i <= sqrt; i += 2) {
            if (isPrime.get(i)) {
                for (int j = i * i; j <= sqrt; j += 2 * i) {
                    isPrime.clear(j);
                }
            }
        }

        List<Integer> basePrimes = new ArrayList<>();
        basePrimes.add(2);
        for (int i = 3; i <= sqrt; i += 2) {
            if (isPrime.get(i)) basePrimes.add(i);
        }

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<BitSet>> futures = new ArrayList<>();

        int segmentSize = (end - sqrt) / threadCount + 1;

        for (int t = 0; t < threadCount; t++) {
            final int segmentStart = sqrt + 1 + t * segmentSize;
            final int segmentEnd = Math.min(end, segmentStart + segmentSize - 1);

            if (segmentStart > end) continue;

            Callable<BitSet> task = () -> {
                BitSet segment = new BitSet(segmentEnd - segmentStart + 1);
                segment.set(0, segmentEnd - segmentStart + 1);

                for (int prime : basePrimes) {
                    if (prime * prime > segmentEnd) break;

                    int startMultiple = Math.max(prime * prime,
                            ((segmentStart + prime - 1) / prime) * prime);

                    if (prime == 2) {
                        for (int j = startMultiple; j <= segmentEnd; j += prime) {
                            if (j >= segmentStart) {
                                segment.clear(j - segmentStart);
                            }
                        }
                    } else {
                        if (startMultiple % 2 == 0) startMultiple += prime;
                        for (int j = startMultiple; j <= segmentEnd; j += 2 * prime) {
                            if (j >= segmentStart) {
                                segment.clear(j - segmentStart);
                            }
                        }
                    }
                }
                return segment;
            };

            futures.add(executor.submit(task));
        }

        int currentSegmentStart = sqrt + 1;
        for (Future<BitSet> future : futures) {
            BitSet segment = future.get();
            for (int i = segment.nextSetBit(0); i >= 0; i = segment.nextSetBit(i + 1)) {
                int actualNumber = currentSegmentStart + i;
                isPrime.set(actualNumber);
            }
            currentSegmentStart += segmentSize;
        }

        executor.shutdown();

        List<Integer> primes = new ArrayList<>();
        if (start <= 2) primes.add(2);

        int startIndex = Math.max(start, 3);
        if (startIndex % 2 == 0) startIndex++;

        for (int i = startIndex; i <= end; i += 2) {
            if (isPrime.get(i)) primes.add(i);
        }

        return primes;
    }
}
