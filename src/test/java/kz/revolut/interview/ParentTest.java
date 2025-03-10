package kz.revolut.interview;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.BeforeEach;

import java.nio.charset.StandardCharsets;
import java.util.Random;

public class ParentTest {

    protected EasyRandomParameters parameters;
    protected EasyRandom randomizer;

    protected static final Random rnd = new Random();

    @BeforeEach
    protected void initParentTest_rnd() {

        parameters = new EasyRandomParameters()
                .charset(StandardCharsets.UTF_8)
                .seed(rnd.nextLong())
                .objectPoolSize(100)
                .randomizationDepth(6);

        randomizer = new EasyRandom(parameters);

    }

    protected <T> T rnd(Class<T> clazz) {
        return randomizer.nextObject(clazz);
    }

}
