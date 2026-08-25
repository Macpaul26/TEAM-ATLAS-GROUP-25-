package campushub.algo;

import campushub.ds.MyMinHeap;
import campushub.model.ServiceRequest;

/**
 * Answers: "who gets served next?"
 *
 * Backed by MyMinHeap, ordered by ServiceRequest's natural ordering
 * (urgency ascending = most critical first, then earliest arrival).
 * This is the classic heap-based priority queue used for job/task
 * scheduling, as covered in CLRS ch. 6.5 and Sedgewick & Wayne ch. 2.4.
 *
 * addRequest -> O(log n)
 * dispatchNext -> O(log n)
 * peekNext -> O(1)
 */
public class PriorityDispatcher {

    private final MyMinHeap<ServiceRequest> ticketQueue = new MyMinHeap<>();

    public void addRequest(ServiceRequest request) {
        ticketQueue.insert(request);
    }

    public boolean hasWaitingRequests() {
        return !ticketQueue.isEmpty();
    }

    public ServiceRequest peekNext() {
        return ticketQueue.peek();
    }

    /** Removes and returns the next ticket to be actioned. */
    public ServiceRequest dispatchNext() {
        return ticketQueue.extractMin();
    }

    public int waitingCount() {
        return ticketQueue.size();
    }
}
