package org.openrewrite.maven.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;

import java.util.List;

import static java.util.Collections.emptyList;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class RawRepositories {
    @JacksonXmlProperty(localName = "repository")
    @JacksonXmlElementWrapper(useWrapping = false)
    List<RawRepositories.Repository> repositories = emptyList();

    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    @Data
    public static class Repository {
        @Nullable
        String id;

        @Nullable
        String name;

        @With
        String url;

        RawRepositories.ArtifactPolicy releases;

        RawRepositories.ArtifactPolicy snapshots;

        @JsonCreator
        public Repository(@JsonProperty("id") @Nullable String id,
                         @JsonProperty("name") @Nullable String name,
                         @JsonProperty("url") String url,
                         @JsonProperty("releases") RawRepositories.ArtifactPolicy releases,
                         @JsonProperty("snapshots") RawRepositories.ArtifactPolicy snapshots) {
            this.id = id;
            this.name = name;
            this.url = url;
            this.releases = releases;
            this.snapshots = snapshots;
        }
    }

    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    @EqualsAndHashCode
    @Getter
    public static class ArtifactPolicy {

        @Nullable
        String enabled;

        public ArtifactPolicy(@Nullable String enabled) {
            this.enabled = enabled;
        }

        /**
         * Used by Jackson in the event there is an empty tag in the POM.
         */
        @SuppressWarnings("unused")
        public ArtifactPolicy() {
            this("true");
        }
    }
}
