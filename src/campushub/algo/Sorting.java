package campushub.algo;

/**
 * Module M4 - sorting engine. Selection, insertion, merge and quicksort, all
 * from scratch. Satisfies §7's sorting rows.
 *
 * Stability & in-place notes (for the report / defense):
 *   selectionSort - in-place, NOT stable, always O(n^2) comparisons.
 *   insertionSort - in-place, STABLE, O(n) best case (already-sorted input),
 *                   O(n^2) worst case (reverse-sorted).
 *   mergeSort     - STABLE, NOT in-place (O(n) auxiliary array), always
 *                   O(n log n). Classic divide-and-conquer: T(n)=2T(n/2)+O(n).
 *   quickSort     - in-place, NOT stable, O(n log n) average but O(n^2) worst
 *                   case (already-sorted input with a naive pivot); we use a
 *                   median-of-three pivot to make that worst case unlikely.
 */
public final class Sorting {

    private Sorting() {}

    public static <T extends Comparable<T>> void selectionSort(T[] a) {
        for (int i = 0; i < a.length - 1; i++) {
            int min = i;
            for (int j = i + 1; j < a.length; j++) {
                if (a[j].compareTo(a[min]) < 0) min = j;
            }
            swap(a, i, min);
        }
    }

    public static <T extends Comparable<T>> void insertionSort(T[] a) {
        for (int i = 1; i < a.length; i++) {
            T key = a[i];
            int j = i - 1;
            while (j >= 0 && a[j].compareTo(key) > 0) { a[j + 1] = a[j]; j--; }
            a[j + 1] = key;
        }
    }

    public static <T extends Comparable<T>> void mergeSort(T[] a) {
        if (a.length < 2) return;
        @SuppressWarnings("unchecked")
        T[] aux = (T[]) new Comparable[a.length];
        mergeSort(a, aux, 0, a.length - 1);
    }

    private static <T extends Comparable<T>> void mergeSort(T[] a, T[] aux, int lo, int hi) {
        if (lo >= hi) return;
        int mid = lo + (hi - lo) / 2;
        mergeSort(a, aux, lo, mid);
        mergeSort(a, aux, mid + 1, hi);
        merge(a, aux, lo, mid, hi);
    }

    private static <T extends Comparable<T>> void merge(T[] a, T[] aux, int lo, int mid, int hi) {
        for (int k = lo; k <= hi; k++) aux[k] = a[k];
        int i = lo, j = mid + 1;
        for (int k = lo; k <= hi; k++) {
            if (i > mid) a[k] = aux[j++];
            else if (j > hi) a[k] = aux[i++];
            else if (aux[j].compareTo(aux[i]) < 0) a[k] = aux[j++]; // '<' keeps stability
            else a[k] = aux[i++];
        }
    }

    public static <T extends Comparable<T>> void quickSort(T[] a) {
        quickSort(a, 0, a.length - 1);
    }

    private static <T extends Comparable<T>> void quickSort(T[] a, int lo, int hi) {
        while (lo < hi) {
            int p = partition(a, lo, hi);
            // recurse on the smaller side, loop on the larger -> O(log n) stack depth
            if (p - lo < hi - p) { quickSort(a, lo, p - 1); lo = p + 1; }
            else { quickSort(a, p + 1, hi); hi = p - 1; }
        }
    }

    private static <T extends Comparable<T>> int partition(T[] a, int lo, int hi) {
        int mid = lo + (hi - lo) / 2;
        medianOfThree(a, lo, mid, hi);   // put a good pivot at hi
        T pivot = a[hi];
        int i = lo - 1;
        for (int j = lo; j < hi; j++) {
            if (a[j].compareTo(pivot) <= 0) { i++; swap(a, i, j); }
        }
        swap(a, i + 1, hi);
        return i + 1;
    }

    private static <T extends Comparable<T>> void medianOfThree(T[] a, int lo, int mid, int hi) {
        if (a[mid].compareTo(a[lo]) < 0) swap(a, lo, mid);
        if (a[hi].compareTo(a[lo]) < 0) swap(a, lo, hi);
        if (a[hi].compareTo(a[mid]) < 0) swap(a, mid, hi);
        swap(a, mid, hi); // move median to hi as pivot
    }

    private static <T> void swap(T[] a, int i, int j) { T t = a[i]; a[i] = a[j]; a[j] = t; }

    public static <T extends Comparable<T>> boolean isSorted(T[] a) {
        for (int i = 1; i < a.length; i++) if (a[i].compareTo(a[i - 1]) < 0) return false;
        return true;
    }
}
