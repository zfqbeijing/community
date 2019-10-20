package com.nowcoder.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTemplateTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testStrings() {
        String redisKey = "test:count";
        redisTemplate.opsForValue().set(redisKey, 1);
        System.out.println(redisTemplate.opsForValue().get(redisKey));
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));
    }

    @Test
    public void testHashs() {
        String redisKey = "test:user";
        redisTemplate.opsForHash().put(redisKey, "id", "1");
        redisTemplate.opsForHash().put(redisKey, "username", "zhangsan");
        redisTemplate.opsForHash().put(redisKey, "age", 15);
        System.out.println(redisTemplate.opsForHash().get(redisKey, "id"));
        System.out.println(redisTemplate.opsForHash().get(redisKey, "username"));
        System.out.println(redisTemplate.opsForHash().get(redisKey, "age"));

    }

    @Test
    public void testLists() {
        String redisKey = "test:ids";
        redisTemplate.opsForList().leftPush(redisKey, 1001);
        redisTemplate.opsForList().leftPush(redisKey, 1002);
        redisTemplate.opsForList().leftPush(redisKey, 1003);
        redisTemplate.opsForList().leftPush(redisKey, 1004);
        redisTemplate.opsForList().leftPush(redisKey, 1005);
        System.out.println(redisTemplate.opsForList().size(redisKey));
        System.out.println(redisTemplate.opsForList().index(redisKey, 2));
        System.out.println(redisTemplate.opsForList().range(redisKey, 0, 3));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().rightPop(redisKey));

    }

    @Test
    public void testSets() {
        String redisKey = "test:teacher";
        redisTemplate.opsForSet().add(redisKey, "樊玲", "李元玲", "美娜", "刘亦菲", "林志玲");
        System.out.println(redisTemplate.opsForSet().size(redisKey));
        System.out.println(redisTemplate.opsForSet().pop(redisKey));
        System.out.println(redisTemplate.opsForSet().members(redisKey));

    }

    @Test
    public void testSortedSets() {
        String redisKey = "test:students";
        redisTemplate.opsForZSet().add(redisKey, "樊玲", 81);
        redisTemplate.opsForZSet().add(redisKey, "李元玲", 90);
        redisTemplate.opsForZSet().add(redisKey, "美娜", 80);
        redisTemplate.opsForZSet().add(redisKey, "刘亦菲", 85);
        redisTemplate.opsForZSet().add(redisKey, "林志玲", 84);
        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));
        System.out.println(redisTemplate.opsForZSet().score(redisKey, "刘亦菲"));
        System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey, "刘亦菲"));
        System.out.println(redisTemplate.opsForZSet().reverseRange(redisKey, 0, 3));
    }

    @Test
    public void testkeys() {
        redisTemplate.delete("test:user");
        System.out.println(redisTemplate.hasKey("test:user"));
        redisTemplate.expire("test:students", 10, TimeUnit.SECONDS);
    }

    @Test
    public void testBoundOperations() {
        String redisKey = "test:count";
        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);
        operations.increment();
        operations.get();
        operations.increment();
        operations.increment();
        operations.increment();
        System.out.println(operations.get());

    }

//    编程试事物
    @Test
    public void testTransactional(){
        Object execute = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String redisKey = "test:tx";
                redisOperations.multi(); // 启用事物

                redisOperations.opsForSet().add(redisKey,"张三");
                redisOperations.opsForSet().add(redisKey,"李四");
                redisOperations.opsForSet().add(redisKey,"王二");
                System.out.println(redisOperations.opsForSet().members(redisKey));
                return redisOperations.exec(); //提交事物

            }
        });
        System.out.println(execute);
    }
}
