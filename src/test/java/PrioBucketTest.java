import net.BucketQueue;
import org.junit.Assert;
import org.junit.Test;

public class PrioBucketTest {
    @Test
    public void testPrioBucketTest(){
        BucketQueue bucketQueue =new BucketQueue(0,null);
        Assert.assertNotNull(bucketQueue);
    }
}