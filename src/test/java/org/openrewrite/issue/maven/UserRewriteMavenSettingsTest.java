package org.openrewrite.issue.maven;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.assertj.core.api.ThrowingConsumer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openrewrite.InMemoryExecutionContext;
import org.openrewrite.Parser;
import org.openrewrite.maven.MavenExecutionContextView;
import org.openrewrite.maven.internal.MavenXmlMapper;
import org.openrewrite.maven.model.MavenSettings;
import org.openrewrite.xml.SemanticallyEqual;
import org.openrewrite.xml.XmlParser;
import org.openrewrite.xml.tree.Xml;

import java.io.ByteArrayOutputStream;
import java.nio.file.Path;

import static java.util.Collections.singletonList;
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

    @Test
        // Test is failing as no <property> encapsulates <name> and <value>
    void canDeserializeSettingsCorrectly() throws Exception {
        Xml.Document parsed = (Xml.Document) XmlParser.builder().build().parse("""
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
                  <timeout>10000</timeout>
                </configuration>
              </server>
            </servers>
          </settings>
          """).findFirst().get();

        org.openrewrite.maven.MavenSettings.HttpHeader httpHeader = new org.openrewrite.maven.MavenSettings.HttpHeader("X-JFrog-Art-Api", "myApiToken");
        org.openrewrite.maven.MavenSettings.ServerConfiguration configuration = new org.openrewrite.maven.MavenSettings.ServerConfiguration(singletonList(httpHeader), 10000L);
        org.openrewrite.maven.MavenSettings.Server server = new org.openrewrite.maven.MavenSettings.Server("maven-snapshots", null, null, configuration);
        org.openrewrite.maven.MavenSettings.Servers servers = new org.openrewrite.maven.MavenSettings.Servers(singletonList(server));
        org.openrewrite.maven.MavenSettings settings = new org.openrewrite.maven.MavenSettings(null, null, null, null, servers);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MavenXmlMapper.writeMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
            .writerWithDefaultPrettyPrinter()
            .writeValue(baos, settings);

        assertThat(XmlParser.builder().build().parse(baos.toString()).findFirst())
            .isPresent()
            .get(InstanceOfAssertFactories.type(Xml.Document.class))
            .isNotNull()
            .satisfies(serialized -> assertThat(SemanticallyEqual.areEqual(parsed, serialized)).isTrue())
            .satisfies(serialized -> assertThat(serialized.printAll().replace("\r", "")).isEqualTo(parsed.printAll()));
    }

}
