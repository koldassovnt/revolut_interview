package kz.revolut.interview;

import kz.revolut.interview.url_shortener.service.RandomStrGeneratorService;
import kz.revolut.interview.url_shortener.service.impl.RandomStrGeneratorServiceImpl;
import org.assertj.core.util.Strings;

public class RandomStrGeneratorServiceForTest implements RandomStrGeneratorService {

    private String generateStr;

    public RandomStrGeneratorServiceForTest(String generateStr) {
        this.generateStr = generateStr;
    }

    public RandomStrGeneratorServiceForTest() {

    }

    @Override
    public String generate(int length) {

        if (Strings.isNullOrEmpty(generateStr)) {
            return new RandomStrGeneratorServiceImpl().generate(length);
        }

        return generateStr;

    }


}
