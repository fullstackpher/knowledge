package io.github.talelin.knowledge;

import io.github.talelin.knowledge.module.file.FileProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class KnowledgeApplicationTests {

    @Autowired
    private FileProperties fileProperties;

    @Test
    public void contextLoads() {
        System.out.println();
    }

}
