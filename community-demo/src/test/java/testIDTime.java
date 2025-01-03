import com.liu.Main8001;
import com.liu.utils.IdAndTimeGeneration;
import com.liu.utils.dataType;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@SpringBootTest(classes = Main8001.class)
public class testIDTime {

    @Resource
    private IdAndTimeGeneration idGeneration;

    @Test
    public void test() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date parse = simpleDateFormat.parse("2024-01-01");
            long time = parse.getTime();
            System.out.println(time);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test2() {
//        for (int i = 0; i < 5; i++) {
//        long generate = idGeneration.generate(dataType.NewsData);
//        System.out.println(generate);
//        }
        long generate = idGeneration.generate(dataType.UserData);
        System.out.println(generate);
    }

    @Test
    public void test3() {
        LocalDateTime now = LocalDateTime.now();
        System.out.println(now);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String format = dateTimeFormatter.format(now);
        System.out.println(format);

        String s = IdAndTimeGeneration.currentTime("yyyy-MM-dd HH:mm");
        System.out.println(s);
    }


}
