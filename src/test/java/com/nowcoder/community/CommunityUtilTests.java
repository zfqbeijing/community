package com.nowcoder.community;

import com.nowcoder.community.util.CommunityUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class CommunityUtilTests {

    @Test
    public void test(){
        String stal = CommunityUtil.generateUUID().substring(3, 8);
        String password = CommunityUtil.md5("1234" + stal);
        System.out.println(stal);
        System.out.println(password);

    }
}
