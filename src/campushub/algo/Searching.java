package campushub.algo;

import campushub.ds.MyArrayList;

/**
 * Module M4 - searching engine. Implements linear and binary search from
 * scratch over Comparable arrays. Satisfies "linear and binary search - both
 * implemented and tested / binary search precondition stated and tested" (§7).
 *
 * PRECONDITION for binarySearch: the array MUST already be sorted ascending.
 * Running it on unsorted input is a documented counterexample (see the Evidence
 * squad's counterexamples) - it can return -1 for an element that is actually
 * present. linearSearch has no such precondition.
 */
public final class Searching {

    private Searching() {}

    /** O(n). Returns the index of key, or -1 if absent. No precondition. */
    public static <T extends Comparable<T>> int linearSearch(T[] a, T key) {
        for (int i = 0; i < a.length; i++) {
            if (a[i].compareTo(key) == 0) return i;
        }
        return -1;
    }

    /** O(log n). PRECONDITION: a is sorted ascending. Returns index or -1. */
    public static <T extends Comparable<T>> int binarySearch(T[] a, T key) {
        int lo = 0, hi = a.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;      // avoids (lo+hi) overflow
            int cmp = key.compareTo(a[mid]);
            if (cmp == 0) return mid;
            if (cmp < 0) hi = mid - 1;
            else lo = mid + 1;
        }
        return -1;
    }

    /** Binary search that also records the (lo, mid, hi) probes, for trace tables. */
    public static <T extends Comparable<T>> MyArrayList<String> binarySearchTrace(T[] a, T key) {
        MyArrayList<String> trace = new MyArrayList<>();
        int lo = 0, hi = a.length - 1, step = 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int cmp = key.compareTo(a[mid]);
            String verdict = cmp == 0 ? "FOUND" : (cmp < 0 ? "go left" : "go right");
            trace.add(String.format("step %d: lo=%d hi=%d mid=%d a[mid]=%s -> %s",
                    step++, lo, hi, mid, a[mid], verdict));
            if (cmp == 0) break;
            if (cmp < 0) hi = mid - 1; else lo = mid + 1;
        }
        if (lo > hi) trace.add("NOT FOUND (lo>hi)");
        return trace;
    }
}
