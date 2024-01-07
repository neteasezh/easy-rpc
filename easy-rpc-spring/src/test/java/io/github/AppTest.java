package io.github;


import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AppTest {
    @Test
    public void test() {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("application-easy-rpc.xml");
        HelloService helloService = (HelloService) applicationContext.getBean("helloService");
        String result = helloService.sayHello("zhuhai");
        System.out.println(result);
    }
}
