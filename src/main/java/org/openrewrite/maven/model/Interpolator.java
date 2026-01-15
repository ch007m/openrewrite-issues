package org.openrewrite.maven.model;

import org.jspecify.annotations.Nullable;
import org.openrewrite.internal.ListUtils;
import org.openrewrite.internal.PropertyPlaceholderHelper;

import java.util.function.UnaryOperator;

public class Interpolator {
    private static final PropertyPlaceholderHelper propertyPlaceholders = new PropertyPlaceholderHelper(
        "${", "}", null);

    private static final UnaryOperator<String> propertyResolver = key -> {
        String property = System.getProperty(key);
        if (property != null) {
            return property;
        }
        if (key.startsWith("env.")) {
            return System.getenv().get(key.substring(4));
        }
        return System.getenv().get(key);
    };

    public MavenSettings interpolate(MavenSettings mavenSettings) {
        return new MavenSettings(
            interpolate(mavenSettings.localRepository),
            mavenSettings.profiles,
            interpolate(mavenSettings.activeProfiles),
            interpolate(mavenSettings.mirrors),
            interpolate(mavenSettings.servers));
    }

    private MavenSettings.@Nullable ActiveProfiles interpolate(MavenSettings.@Nullable ActiveProfiles activeProfiles) {
        if (activeProfiles == null) return null;
        return new MavenSettings.ActiveProfiles(ListUtils.map(activeProfiles.getActiveProfiles(), this::interpolate));
    }

    private MavenSettings.@Nullable Mirrors interpolate(MavenSettings.@Nullable Mirrors mirrors) {
        if (mirrors == null) return null;
        return new MavenSettings.Mirrors(ListUtils.map(mirrors.getMirrors(), this::interpolate));
    }

    private MavenSettings.Mirror interpolate(MavenSettings.Mirror mirror) {
        return new MavenSettings.Mirror(interpolate(mirror.id), interpolate(mirror.url), interpolate(mirror.getMirrorOf()), mirror.releases, mirror.snapshots);
    }

    private MavenSettings.@Nullable Servers interpolate(MavenSettings.@Nullable Servers servers) {
        if (servers == null) return null;
        return new MavenSettings.Servers(ListUtils.map(servers.getServers(), this::interpolate));
    }

    private MavenSettings.@Nullable ServerConfiguration interpolate(MavenSettings.@Nullable ServerConfiguration configuration) {
        if (configuration == null) {
            return null;
        }
        return new MavenSettings.ServerConfiguration(
            ListUtils.map(configuration.httpHeaders, this::interpolate),
            configuration.timeout
        );
    }

    private MavenSettings.HttpHeader interpolate(MavenSettings.HttpHeader httpHeader) {
        return new MavenSettings.HttpHeader(interpolate(httpHeader.getName()), interpolate(httpHeader.getValue()));
    }

    private MavenSettings.Server interpolate(MavenSettings.Server server) {
        return new MavenSettings.Server(interpolate(server.id), interpolate(server.username), interpolate(server.password),
            interpolate(server.configuration));
    }

    private @Nullable String interpolate(@Nullable String s) {
        return s == null ? null : propertyPlaceholders.replacePlaceholders(s, propertyResolver);
    }
}