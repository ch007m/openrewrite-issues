package org.openrewrite.issue;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.assertj.core.api.ThrowingConsumer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openrewrite.InMemoryExecutionContext;
import org.openrewrite.Parser;
import org.openrewrite.maven.MavenExecutionContextView;
import org.openrewrite.quarkus.maven.model.MavenSettings;

import java.io.IOException;
import java.nio.file.Path;

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

public class UserRewriteMavenSettingsTest {

	private final MavenExecutionContextView ctx = MavenExecutionContextView
			.view(new InMemoryExecutionContext((ThrowingConsumer<Throwable>) input -> {
				throw input;
			}));

	@Test
	void serverHttpHeaders() throws IOException {
		var settingsXmlfile = Parser.Input.fromString(Path.of("settings.xml"),
				// language=xml
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
</settings>""");

        /* To inspect the code
        ObjectMapper mapper = new XmlMapper();
        JavaType type = mapper.getTypeFactory().constructType(MavenSettings.HttpHeader.class);
        BeanDescription desc = mapper.getSerializationConfig().introspect(type);

        desc.findProperties().forEach(p -> {
            System.out.println("Property: " + p.getName());
            System.out.println(" -> Has Constructor Parameter: " + p.hasConstructorParameter());
            System.out.println(" -> Has Field: " + p.hasField());
        });
        */

        XmlMapper xmlMapper = new XmlMapper();
        MavenSettings settings = xmlMapper.readValue(settingsXmlfile.getSource(ctx), MavenSettings.class);

		MavenSettings.Server server = settings.getServers().getServers().getFirst();
        System.out.println("Configuration: " + server.getConfiguration());

        Assertions.assertNotNull(server.getConfiguration().getHttpHeaders());
        System.out.println("HttpHeaders: " + server.getConfiguration().getHttpHeaders());

        assertThat(server.getConfiguration().getHttpHeaders().getFirst().getName()).isEqualTo("X-JFrog-Art-Api");
	}
}
