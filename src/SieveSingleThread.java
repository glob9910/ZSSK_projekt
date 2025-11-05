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
        boolean[] isPrime = new boolean[end + 1];   // Utworzenie tablicy o rozmiarze end+1 (dzięki temu nr indeksu = badana liczba)
        for(int i = 2; i <= end; i++) {
            isPrime[i] = true;                      // Początkowo ustawiamy wszystkie liczby z zakresu jako pierwsze (w dalszej części wykreślamy liczby nie-pierwsze)
        }

        for(int i = 2; i*i <= end; i++) {
            if(isPrime[i]) {                        // Sprawdzamy wielokrotności wszystkich liczb (pierwszych) od 2 do pierwiastka z end
                for(int j = i*i; j <= end; j += i) {
                    isPrime[j] = false;             // Wykreślamy kolejne wielokrotności
                }
            }
        }

        List<Integer> primes = new ArrayList<>();   // Zapisanie wyników (na razie donikąd)
//        for(int i = start; i <= end; i++) {
//            if(isPrime[i]) {
//                primes.add(i);
//            }
//        }
        return primes;
    }
}
