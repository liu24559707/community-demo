package com.liu.ws;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.liu.entity.Message;
import com.liu.utils.JwtHelper;
import jakarta.annotation.Resource;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 聊天端点
 * 当使用websocket服务自动注入为null的原因，因为spring为单例的，自动注入只进行一次，
 * 而websocket是多对象的，每进行一次聊天，就会创建一个对象，与spring相矛盾，即spring只给websocket注入一次，之后的对象将不会被再注入，
 * 两种注入方法，
 *   1.创建一个非静态的setter方法，并在该方法上使用@Autowired/@Resource注解。
 *   Spring在启动时会自动调用这个方法并传入一个依赖的实例，然后在这个setter方法内部再将注入的依赖赋值给静态变量
 *   2.使用上下文引用
 */
@Slf4j
@Component
//@ServerEndpoint 注解中的路径 /chat/{username}/{to:.*} 使用了正则表达式 .* 来匹配任意字符（包括空字符串），这使得 to 参数可以为空。
@ServerEndpoint(value="/chat/{username}/{to}")
public class ChatEndPoint {

    private static Map<String,Session> onlineUser = new ConcurrentHashMap<>();


    private static RedisTemplate<String,Object> redisTemplate;

    @Resource
    public void setStringRedisTemplate(RedisTemplate<String,Object> RedisTemplate) {
        ChatEndPoint.redisTemplate = RedisTemplate;
    }


     private Session session;

      @OnOpen
    public void onOpen(Session session, @PathParam("username") String username,@PathParam("to") String to) throws IOException {
          if ("null".equals(username)){
              return;
          }
          //建立连接会直接从redis
            session.getUserProperties().put("username", username);
            this.session = session;
            //此时该用户还没在map中
          List<String> collect = onlineUser.keySet().stream()
                  .filter(key->!key.equals(username))//过滤掉元素
                  .collect(Collectors.toList());

          //由于session没有实现Serializable接口，无法完成序列化，要想存入redis必须实现Serializable接口，
          //故不能使用redis来代替ConcurrentHashMap，
          //所以只用它存放聊天记录和信息
          onlineUser.put(username,session);
          Message message = new Message();
          if (!collect.isEmpty()) {
          message.setMessage(JSON.toJSONString(collect));
          message.setType(3);
          this.session.getBasicRemote().sendText(JSON.toJSONString(message));
          }
          //向所有的在线用户推送该用户
          collect.forEach(user->{
              Session session1 = onlineUser.get(user);
                  message.setType(4);
                  message.setMessage(username);
              if(session1!=null&&session1.isOpen()){
                 session1.getAsyncRemote().sendText(JSON.toJSONString(message));
              }
          });

          //向该用户发送今天的聊天记录
          if (!"undefined".equals(to)&&!StringUtils.isEmpty(to)) {
          String key = generateKey(username, to);
          List<Object> range = redisTemplate.opsForList().range("mes:" + key, 0, -1);
          this.session.getBasicRemote().sendText(JSON.toJSONString(range));
          }

      }

      @OnMessage
    public void onMessage(String message, Session session) {
          log.info("onMessage: " + message);
          Message message1 = JSONObject.parseObject(message, Message.class);
          String message1To = message1.getTo();
          Session session1 = onlineUser.get(message1To);
          String jsonString = JSON.toJSONString(message1);
          session1.getAsyncRemote().sendText(jsonString);
          //存放在redis中
          String key = generateKey(message1.getFrom(),message1.getTo());
          if (key == null) {
              log.info("该消息不是聊天记录");
              return;
          }
          redisTemplate.opsForList().rightPush("mes:"+key,jsonString);
          redisTemplate.opsForList().rightPush("new_mes:"+key,jsonString);
      }

    /**
     * 按字典顺序生成键值
     */
    public static String generateKey(String user1,String user2){
          if(StringUtils.isEmpty(user1) && StringUtils.isEmpty(user2)){//空白字符串无法生成键
            return null;
          }
          if(user1.compareTo(user2)<0){
             return user1+"_"+user2;
          }else {
              return user2+"_"+user1;
          }
      }



    @OnClose
    public void onClose(Session session) {
          //防止因为js的垃圾回收导致之前的session不可用
          onlineUser.remove(this.session.getUserProperties().get("username"));
      }
}
