package Chapter15;

import org.junit.Assert;
import org.junit.Test;
import java.io.FileNotFoundException;
import java.io.IOException;

public class exercise5 {

    @Test(expected= IOException.class)
    public void textFromFile()
            throws IOException {

        System.out.println(
            Annotations_Exercises.ex5().textFromFile("/tmp/test.txt")
        );
    }
}
