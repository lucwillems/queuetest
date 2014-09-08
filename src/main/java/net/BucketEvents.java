package net;

/**
 * Created by luc on 9/3/14.
 */
public interface BucketEvents {
    void onInit(BucketQueue bucket);
    void onQuantumized(BucketQueue bucket);
    void onBorrow(BucketQueue bucket,int size);
}
