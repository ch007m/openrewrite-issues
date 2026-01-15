package org.openrewrite.issue.maven;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.Test;
import org.openrewrite.maven.model.MavenSettings;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class JacksonWrappingTest {

	@Test
	public void toJson() throws JsonProcessingException {
		String xmlsettings = """
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
				""";

		XmlMapper xmlMapper = new XmlMapper();
		MavenSettings settings = xmlMapper.readValue(xmlsettings, MavenSettings.class);
		assertTrue(!settings.getServers().getServers().isEmpty());
	}

}
