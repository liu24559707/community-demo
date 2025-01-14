package com.liu.utils;


import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;


import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;


/**
 * 自定义的id生成策略
 */
@Component
public class IdAndTimeGeneration {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 自定义id =数据类型id（3位）+时间戳+redis的自增
     * 由于是通过redis实现自增，所以我们的id可以代表创建时间的先后
     * @param dataType
     * @return
     */
    public  long generate(dataType dataType) {
        //日期
        LocalDate localDate = LocalDate.now();
        System.out.println(localDate);

        ZoneId zoneId = ZoneId.of("Asia/Shanghai");

        ZonedDateTime zonedDateTime = localDate.atStartOfDay(zoneId);
        System.out.println(zonedDateTime);
        long epochMilli = zonedDateTime.toInstant().toEpochMilli();
        //当前日期的时间戳
        System.out.println(epochMilli);
        //我们取出后三位，转化为秒
        epochMilli = epochMilli / 1000;

        //自增键
        String key = localDate+":"+dataType.getDataTypeId();


        //redis自增
        Long increment = stringRedisTemplate.opsForValue().increment(key, 1);
        if (increment == 1) {
        stringRedisTemplate.expire(key,24,TimeUnit.HOURS);
        }

        //组合
        String id = ""+dataType.getDataTypeId()+epochMilli+increment;

        return Long.valueOf(id);
    }

    /**
     * 以pattern格式生成时间
     * @param pattern
     * @return
     */
    public static String currentTime(String pattern){
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(new Date());
    }
}
