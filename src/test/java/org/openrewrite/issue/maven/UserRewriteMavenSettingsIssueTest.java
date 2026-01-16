package org.openrewrite.issue.maven;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openrewrite.maven.internal.MavenXmlMapper;
import org.openrewrite.xml.SemanticallyEqual;
import org.openrewrite.xml.XmlParser;
import org.openrewrite.xml.tree.Xml;

import java.io.ByteArrayOutputStream;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

public class UserRewriteMavenSettingsIssueTest {

    @Disabled
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

        org.openrewrite.maven.model.MavenSettings.HttpHeader httpHeader = new org.openrewrite.maven.model.MavenSettings.HttpHeader("X-JFrog-Art-Api", "myApiToken");
        org.openrewrite.maven.model.MavenSettings.ServerConfiguration configuration = new org.openrewrite.maven.model.MavenSettings.ServerConfiguration(singletonList(httpHeader), 10000L);
        org.openrewrite.maven.model.MavenSettings.Server server = new org.openrewrite.maven.model.MavenSettings.Server("maven-snapshots", null, null, configuration);
        org.openrewrite.maven.model.MavenSettings.Servers servers = new org.openrewrite.maven.model.MavenSettings.Servers(singletonList(server));
        org.openrewrite.maven.model.MavenSettings settings = new org.openrewrite.maven.model.MavenSettings(null, null, null, null, servers);

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

    public class MavenSettings {

    }

}
