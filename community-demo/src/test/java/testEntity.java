import com.liu.Main8001;
import com.liu.entity.News;
import com.liu.utils.CacheClient;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest(classes = Main8001.class)
public class testEntity {

    @Resource
    private CacheClient cacheClient;

    @Test
    public void test() {
        News news = new News();
        news.setNid(10L);
        news.setContent(1+"");
        Map<String, Object> map = new HashMap<>();
        BeanUtils.copyProperties(news, map);
        cacheClient.getStringRedisTemplate().opsForHash().putAll(1111+"",map);
    }
}
