
import com.alibaba.fastjson2.JSON;
import com.liu.Main8001;
import com.liu.entity.Newstype;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;

@SpringBootTest(classes = Main8001.class)
public class testRedis {
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Test
    public void test() {
        Newstype newstype = new Newstype();
        newstype.setTypeid(0);
        newstype.setDel(0);
        newstype.setCreatetime(new Date());
        newstype.setUpdatetime(new Date());
        newstype.setTypename("0");
        String jsonString = JSON.toJSONString(newstype);
        redisTemplate.opsForValue().set("key",jsonString);
        System.out.println(redisTemplate.opsForValue().get("key"));
    }

    @Test
    public void test1() {
        redisTemplate.opsForValue().set("key",null);
        System.out.println(redisTemplate.opsForValue().get("key2"));
    }
}
