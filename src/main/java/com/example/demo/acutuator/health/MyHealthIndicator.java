package com.example.demo.acutuator.health;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MyHealthIndicator extends AbstractHealthIndicator {  //名字必须是xxxHealthIndicator

    /**
     * 检查方法
     */
    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {

        Map<String,Object> map=new HashMap<>();
        if (1==1){
            builder.status(Status.UP);//健康
            map.put("成功",1);
        }else {
            builder.status(Status.DOWN);//不健康
            map.put("失败",2);
        }
        builder.withDetail("待定",100)
                .withDetails(map);

    }
}
