package Chapter15;

import org.junit.Assert;
import org.junit.Test;
import java.io.FileNotFoundException;

public class exercise5 {

    @Test(expected=FileNotFoundException.class)
    public void textFromFile()
            throws FileNotFoundException {

        System.out.println(
            Annotations_Exercises.ex5().textFromFile("/tmp/test.txt")
        );
    }
}
