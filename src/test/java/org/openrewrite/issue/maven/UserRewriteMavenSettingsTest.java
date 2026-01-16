package org.openrewrite.issue.maven;

import org.assertj.core.api.ThrowingConsumer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openrewrite.InMemoryExecutionContext;
import org.openrewrite.Parser;
import org.openrewrite.maven.MavenExecutionContextView;
import org.openrewrite.maven.model.MavenSettings;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class UserRewriteMavenSettingsTest {

	private final MavenExecutionContextView ctx = MavenExecutionContextView
			.view(new InMemoryExecutionContext((ThrowingConsumer<Throwable>) input -> {
				throw input;
			}));

    /**
     * See the <a href="https://maven.apache.org/guides/mini/guide-http-settings.html#Taking_Control_of_Your_HTTP_Headers">Maven guide</a>
     * on HTTP headers.
     */
    @Test
    void serverHttpHeaders() {
        var settings = org.openrewrite.maven.model.MavenSettings.parse(Parser.Input.fromString(Path.of("settings.xml"),
            //language=xml
            """
              <settings>
                  <servers>
                      <server>
                          <id>maven-snapshots</id>
                          <configuration>
                              <httpHeaders>
                                  <property>
                                      <name>X-JFrog-Art-Api</name>
                                      <value>myApiToken</value>
                                  </property>
                              </httpHeaders>
                          </configuration>
                      </server>
                  </servers>
                  <profiles>
                      <profile>
                          <id>my-profile</id>
                          <repositories>
                              <repository>
                                  <id>maven-snapshots</id>
                                  <name>Private Repo</name>
                                  <url>https://repo.company.net/maven</url>
                              </repository>
                          </repositories>
                      </profile>
                  </profiles>
              </settings>
              """
        ), ctx);

        Assertions.assertNotNull(settings);
        Assertions.assertNotNull(settings.getServers());
        MavenSettings.Server server = settings.getServers().getServers().getFirst();

        Assertions.assertNotNull(server.getConfiguration().getHttpHeaders());
        assertThat(server.getConfiguration().getHttpHeaders().getFirst().getName()).isEqualTo("X-JFrog-Art-Api");
    }
}
