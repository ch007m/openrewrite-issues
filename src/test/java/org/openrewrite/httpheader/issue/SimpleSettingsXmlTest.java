package org.openrewrite.httpheader.issue;

import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.ConstructorDetector;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.xml.stream.XMLInputFactory;

import java.io.ByteArrayOutputStream;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

public class SimpleSettingsXmlTest {
    private String settingsXml = """
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
</settings>""";

    @Test
    public void shouldParseXML() throws JsonProcessingException {
        XMLInputFactory input = new WstxInputFactory();
        input.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, false);
        input.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);
        XmlFactory xmlFactory = new XmlFactory(input, new WstxOutputFactory());

        ObjectMapper m = XmlMapper.builder(xmlFactory)
            .constructorDetector(ConstructorDetector.USE_PROPERTIES_BASED)
            .enable(FromXmlParser.Feature.EMPTY_ELEMENT_AS_NULL)
            .defaultUseWrapper(true) // False to true
            .build()
            .registerModule(new ParameterNamesModule())
            .disable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
            .disable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        m.setVisibility(m.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.NONE)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY))
            .registerModule(new JavaTimeModule());

        MavenSettingsSimplified settings = m.readValue(settingsXml, MavenSettingsSimplified.class);
        Assertions.assertNotNull(settings.getServers());
        MavenSettingsSimplified.Server server = settings.getServers().getServers().getFirst();
        Assertions.assertNotNull(server.getConfiguration().getHttpHeaders());
        assertThat(server.getConfiguration().getHttpHeaders().getFirst().getName()).isEqualTo("X-JFrog-Art-Api");
    }

    @Test
    void canDeserializeSettingsCorrectly() throws Exception {
        String xmlSettings = """
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
          """;

        MavenSettingsSimplified.HttpHeader httpHeader = new MavenSettingsSimplified.HttpHeader("X-JFrog-Art-Api", "myApiToken");
        MavenSettingsSimplified.ServerConfiguration configuration = new MavenSettingsSimplified.ServerConfiguration(singletonList(httpHeader), 10000L);
        MavenSettingsSimplified.Server server = new MavenSettingsSimplified.Server("maven-snapshots", null, null, configuration);
        MavenSettingsSimplified.Servers servers = new MavenSettingsSimplified.Servers(singletonList(server));
        MavenSettingsSimplified settings = new MavenSettingsSimplified(servers);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        XMLInputFactory input = new WstxInputFactory();
        input.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, false);
        input.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);
        XmlFactory xmlFactory = new XmlFactory(input, new WstxOutputFactory());

        ObjectMapper m = XmlMapper.builder(xmlFactory)
            .defaultUseWrapper(false)
            .build()
            .registerModule(new JaxbAnnotationModule());

        m.setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
            .writerWithDefaultPrettyPrinter()
            .writeValue(baos, settings);

        Assertions.assertEquals(xmlSettings,baos.toString());
    }

}
