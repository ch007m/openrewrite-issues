package org.openrewrite.issue.maven;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.assertj.core.api.ThrowingConsumer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openrewrite.InMemoryExecutionContext;
import org.openrewrite.maven.MavenExecutionContextView;
import org.openrewrite.maven.model.MavenSettings;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/*
   Version of jackson reporting the issue:
     [INFO] +- com.fasterxml.jackson.core:jackson-core:jar:2.19.2:compile
     [INFO] +- com.fasterxml.jackson.core:jackson-databind:jar:2.19.2:compile

   Version of jackson without the issue
     +--- com.fasterxml.jackson.datatype:jackson-datatype-jdk8 -> 2.17.2
     |    +--- com.fasterxml.jackson.core:jackson-core:2.17.2
     |    \--- com.fasterxml.jackson.core:jackson-databind:2.17.2

 */
public class UserMavenSettingsWithServerAndHttpHeaderTest {

	@Test
	void serverHttpHeaders() throws IOException {
		var settingsXml = """
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
</settings>""";

        XmlMapper xmlMapper = new XmlMapper();
        MavenSettings settings = xmlMapper.readValue(settingsXml, MavenSettings.class);

		MavenSettings.Server server = settings.getServers().getServers().getFirst();
        System.out.println("Configuration: " + server.getConfiguration());

        Assertions.assertNotNull(server.getConfiguration().getHttpHeaders());
        System.out.println("HttpHeaders: " + server.getConfiguration().getHttpHeaders());

        assertThat(server.getConfiguration().getHttpHeaders().getFirst().getName()).isEqualTo("X-JFrog-Art-Api");
	}
}
