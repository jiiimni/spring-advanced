package org.example.expert;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("환경 변수(DB_URL) 없이는 전체 컨텍스트 로딩이 어려워 단위 테스트 커버리지 측정에서 제외")
@SpringBootTest
class ExpertApplicationTests {

    @Test
    void contextLoads() {
    }

}
