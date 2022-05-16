package team.common;


import team.OrderDetailApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes = { OrderDetailApplication.class })
public class CucumberSpingConfiguration {
    
}
