package br.com.socialconnect.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIf;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.DockerClientFactory;

@DisabledIf("isDockerNotAvailable")
@Import(TestcontainersConfiguration.class)
@SpringBootTest
class ApiApplicationTests {

	static boolean isDockerNotAvailable() {
		try {
			return !DockerClientFactory.instance().isDockerAvailable();
		} catch (Throwable t) {
			return true;
		}
	}

	@Test
	void contextLoads() {
	}

}
