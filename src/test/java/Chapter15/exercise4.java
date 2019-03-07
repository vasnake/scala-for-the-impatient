package Chapter15;

import org.junit.Assert;
import org.junit.Test;

// 4. Write a Scala method 'sum' with variable integer arguments that returns the sum of its
// arguments. Call it from Java.
public class exercise4 {
    @Test
    public void varargsSum() {
        Assert.assertEquals(
                Annotations_Exercises.ex4().sum(1, 2, 3),
                6
        );
    }
}
