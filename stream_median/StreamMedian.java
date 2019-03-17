/*
 * Name:        Nevarez-Lira, Antero (nevarez)
 * Date:        February 28, 2019
 * Course:      CSCI 340
 * Instructor:  Dr. Hansen
 * Description: 
 *              This program focuses on finding the median of a stream of random
 *              data. The data is processed into 2 different heaps, implemented
 *              as priority queues.
 *              
 * Bugs:        None.
 */
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;
public class StreamMedian {

    private PriorityQueue<Integer> minHeap;  //holds the larger half of the data aka bigger
    private PriorityQueue<Integer> maxHeap;  //holds the smaller half of the data aka smaller
    
    /*
     * Constructor: Takes no params. Initializes both heaps as priority queues.
     * Implements Comparator and overrides compare, in order to implement the maxheap
    */
    public StreamMedian()
    {
        minHeap = new PriorityQueue<Integer>();
        maxHeap = new PriorityQueue<Integer>(new Comparator<Integer>(){
            public int compare(Integer first, Integer second){ return second - first;}
        });
    }
    
    /*
     * insert(Integer): takes an Integer wrapper as a parameter and adds it to
     * the appropriate heap. By default, the first piece of data is added to
     * the minheap. If the heaps are not null, check to see if the parameter is
     * greater than the current value at the root of maxheap, if so, add it to
     * the maxheap, else add it to the minheap.
     * Size constraints: 
     *     1. If the size of the entire data is even, both heaps should be of equal size
     *     2. If the size of the entire data is odd, maxheap is allowed to have
     *        the extra piece of data
    */
    public void insert(Integer target)
    {
        //add target to appropriate heap
        if((minHeap.size() != 0) && (maxHeap.size() != 0))
        {
            if(maxHeap.peek() < target)
                minHeap.add(target);
            else
                maxHeap.add(target);
        }
        else if((minHeap.size() == 1) && (maxHeap.size() < 1))
            maxHeap.add(target);
        else if( (minHeap.size() < 1) && (maxHeap.size() >= 1))
            minHeap.add(target);
        else
            minHeap.add(target);            
        
        //check the sizes of the heaps. Make sure to maintain the two constraints
        if(  (minHeap.size() + maxHeap.size()) % 2 == 0 )
        {
            if(minHeap.size() > maxHeap.size())
            {
                while( minHeap.size() != maxHeap.size())
                    maxHeap.add(minHeap.poll());
            }
            else if(minHeap.size() < maxHeap.size())
            {
                while(maxHeap.size() != minHeap.size())
                    minHeap.add(maxHeap.poll());
            }
        }
        else
        {
            if( minHeap.size() > maxHeap.size())
            {
                while(minHeap.size() != (maxHeap.size() - 1))
                    maxHeap.add(minHeap.poll());
            }
            else if(minHeap.size() < maxHeap.size())
            {
                while( minHeap.size() != (maxHeap.size() - 1))
                    minHeap.add(maxHeap.poll());
            }
        }
    }
    
    /*
     * getMedian(): Takes no params. Calculates the median of the data. If the 
     * heaps are of equal size, then the median is the addition of the largest
     * value of the maxheap and the smallest value of the minheap, and divide by 2.
     * If the heaps are not of equal size, then the median is the largest value
     * in the maxheap. return median
    */
    public double getMedian()
    {
        double median = 0.0;        
        if(minHeap.size() == maxHeap.size())
            median = ((minHeap.peek() + maxHeap.peek())/2.0);
        else
            median = maxHeap.peek();
        
        return median;
    }
}