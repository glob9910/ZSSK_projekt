public class Main {
    public static void main(String[] args) throws Exception {
        int start = 2;
        int end = 100000000;
        System.out.println("Zakres: " + start + " - " + end);

        long t1 = System.currentTimeMillis();
        var primes1 = new SieveSingleThread(start, end).findPrimes();
        long t2 = System.currentTimeMillis();
        System.out.println("Jednowątkowo: znaleziono " + primes1.size() + " liczb pierwszych w " + (t2 - t1) + " ms");

        long t3 = System.currentTimeMillis();
        var primes2 = new SieveMultiThread(start, end, 4).findPrimes();
        long t4 = System.currentTimeMillis();
        System.out.println("Wielowątkowo: znaleziono " + primes2.size() + " liczb pierwszych w " + (t4 - t3) + " ms");
    }
}
