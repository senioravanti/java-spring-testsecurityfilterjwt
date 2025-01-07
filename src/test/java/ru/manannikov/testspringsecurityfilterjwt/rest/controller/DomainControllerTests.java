package ru.manannikov.testspringsecurityfilterjwt.rest.controller;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.manannikov.testspringsecurityfilterjwt.config.UnsecuredWebMvcTest;
import ru.manannikov.testspringsecurityfilterjwt.config.WithCustomUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Log4j2
@ExtendWith(MockitoExtension.class)
@UnsecuredWebMvcTest(
    controllers = DomainController.class,
    properties = {
        "logging.level.ru.manannikov.testspringsecurityfilterjwt=DEBUG",
    }
)
@AutoConfigureMockMvc
public class DomainControllerTests {
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DomainController domainController;

//    @MockBean
//    private JwtFilter jwtFilter;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithCustomUser(2L)
    public void testGetPrincipal()
        throws Exception
    {
        String authorizationHeader = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzZW5pb3JhdmFudGkiLCJ1c2VyX2lkIjoxLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwMDEiLCJpYXQiOjE3MzE0MjMwOTAsImV4cCI6MTczMTQyMzk5MH0.ai5R-59sFOILikAlmGpReZ0ny8ildI7RjO1pAWftMU0";

//        logger.info("Application context:");
//        for (String name : applicationContext.getBeanDefinitionNames()) {
//            logger.info(name);
//        }
//        logger.info("------");

        var result = mockMvc.perform(
            get("/Test")
                .header("Authorization", authorizationHeader)
            )
            .andExpect(status().isOk())
            .andReturn()
        ;

    }
}
