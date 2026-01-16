package org.openrewrite.httpheader.issue;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Generated;
import org.jspecify.annotations.Nullable;

@JacksonXmlRootElement(
    localName = "settings"
)
public class MavenSettingsSimplified {
    public final @Nullable Servers servers;

    @Generated
    public String toString() {
        return "MavenSettingsSimplified()";
    }

    @Generated
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof MavenSettingsSimplified)) {
            return false;
        } else {
            MavenSettingsSimplified other = (MavenSettingsSimplified)o;
            return other.canEqual(this);
        }
    }

    @Generated
    protected boolean canEqual(final Object other) {
        return other instanceof MavenSettingsSimplified;
    }

    @Generated
    public int hashCode() {
        int result = 1;
        return 1;
    }

    @Generated
    public @Nullable Servers getServers() {
        return this.servers;
    }

    @Generated
    public MavenSettingsSimplified(final @Nullable Servers servers) {
        this.servers = servers;
    }

    @Generated
    public MavenSettingsSimplified withServers(final @Nullable Servers servers) {
        return this.servers == servers ? this : new MavenSettingsSimplified(servers);
    }

    public static class Servers {
        @JacksonXmlProperty(
            localName = "server"
        )
        @JacksonXmlElementWrapper(
            useWrapping = false
        )
        private List<Server> servers = Collections.emptyList();

        public Servers merge(@Nullable Servers servers) {
            Map<String, Server> merged = new LinkedHashMap();

            for(Server server : this.servers) {
                merged.put(server.id, server);
            }

            if (servers != null) {
                servers.getServers().forEach((serverx) -> merged.putIfAbsent(serverx.getId(), serverx));
            }

            return new Servers(new ArrayList(merged.values()));
        }

        @Generated
        public List<Server> getServers() {
            return this.servers;
        }

        @Generated
        public void setServers(final List<Server> servers) {
            this.servers = servers;
        }

        @Generated
        public Servers(final List<Server> servers) {
            this.servers = servers;
        }

        @Generated
        public Servers() {
        }

        @Generated
        public Servers withServers(final List<Server> servers) {
            return this.servers == servers ? this : new Servers(servers);
        }
    }

    public static class Server {
        private final String id;
        private final String username;
        private final String password;
        public final ServerConfiguration configuration;

        @JsonCreator
        public Server(@JsonProperty("id") String id, @JsonProperty("username") String username, @JsonProperty("password") String password, @JsonProperty("configuration") ServerConfiguration configuration) {
            this.id = id;
            this.username = username;
            this.password = password;
            this.configuration = configuration;
        }

        @Generated
        public String getId() {
            return this.id;
        }

        @Generated
        public String getUsername() {
            return this.username;
        }

        @Generated
        public String getPassword() {
            return this.password;
        }

        @Generated
        public ServerConfiguration getConfiguration() {
            return this.configuration;
        }

        @Generated
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            } else if (!(o instanceof Server)) {
                return false;
            } else {
                Server other = (Server)o;
                if (!other.canEqual(this)) {
                    return false;
                } else {
                    Object this$id = this.getId();
                    Object other$id = other.getId();
                    if (this$id == null) {
                        if (other$id != null) {
                            return false;
                        }
                    } else if (!this$id.equals(other$id)) {
                        return false;
                    }

                    Object this$username = this.getUsername();
                    Object other$username = other.getUsername();
                    if (this$username == null) {
                        if (other$username != null) {
                            return false;
                        }
                    } else if (!this$username.equals(other$username)) {
                        return false;
                    }

                    Object this$password = this.getPassword();
                    Object other$password = other.getPassword();
                    if (this$password == null) {
                        if (other$password != null) {
                            return false;
                        }
                    } else if (!this$password.equals(other$password)) {
                        return false;
                    }

                    Object this$configuration = this.getConfiguration();
                    Object other$configuration = other.getConfiguration();
                    if (this$configuration == null) {
                        if (other$configuration != null) {
                            return false;
                        }
                    } else if (!this$configuration.equals(other$configuration)) {
                        return false;
                    }

                    return true;
                }
            }
        }

        @Generated
        protected boolean canEqual(final Object other) {
            return other instanceof Server;
        }

        @Generated
        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            Object $id = this.getId();
            result = result * 59 + ($id == null ? 43 : $id.hashCode());
            Object $username = this.getUsername();
            result = result * 59 + ($username == null ? 43 : $username.hashCode());
            Object $password = this.getPassword();
            result = result * 59 + ($password == null ? 43 : $password.hashCode());
            Object $configuration = this.getConfiguration();
            result = result * 59 + ($configuration == null ? 43 : $configuration.hashCode());
            return result;
        }

        @Generated
        public String toString() {
            String var10000 = this.getId();
            return "MavenSettingsSimplified.Server(id=" + var10000 + ", username=" + this.getUsername() + ", password=" + this.getPassword() + ", configuration=" + String.valueOf(this.getConfiguration()) + ")";
        }

        @Generated
        public Server withId(final String id) {
            return this.id == id ? this : new Server(id, this.username, this.password, this.configuration);
        }

        @Generated
        public Server withUsername(final String username) {
            return this.username == username ? this : new Server(this.id, username, this.password, this.configuration);
        }

        @Generated
        public Server withPassword(final String password) {
            return this.password == password ? this : new Server(this.id, this.username, password, this.configuration);
        }

        @Generated
        public Server withConfiguration(final ServerConfiguration configuration) {
            return this.configuration == configuration ? this : new Server(this.id, this.username, this.password, configuration);
        }
    }

    public static class ServerConfiguration {
        @JacksonXmlProperty(
            localName = "property"
        )
        @JacksonXmlElementWrapper(
            localName = "httpHeaders",
            useWrapping = true
        )
        @JsonIgnore
        private final @Nullable List<HttpHeader> httpHeaders;
        private final @Nullable Long timeout;

        @JsonCreator
        public ServerConfiguration(@JsonProperty("httpHeaders") @Nullable List<HttpHeader> httpHeaders, @JsonProperty("timeout") @Nullable Long timeout) {
            this.httpHeaders = httpHeaders;
            this.timeout = timeout;
        }

        @Generated
        public @Nullable List<HttpHeader> getHttpHeaders() {
            return this.httpHeaders;
        }

        @Generated
        public @Nullable Long getTimeout() {
            return this.timeout;
        }

        @Generated
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            } else if (!(o instanceof ServerConfiguration)) {
                return false;
            } else {
                ServerConfiguration other = (ServerConfiguration)o;
                if (!other.canEqual(this)) {
                    return false;
                } else {
                    Object this$timeout = this.getTimeout();
                    Object other$timeout = other.getTimeout();
                    if (this$timeout == null) {
                        if (other$timeout != null) {
                            return false;
                        }
                    } else if (!this$timeout.equals(other$timeout)) {
                        return false;
                    }

                    Object this$httpHeaders = this.getHttpHeaders();
                    Object other$httpHeaders = other.getHttpHeaders();
                    if (this$httpHeaders == null) {
                        if (other$httpHeaders != null) {
                            return false;
                        }
                    } else if (!this$httpHeaders.equals(other$httpHeaders)) {
                        return false;
                    }

                    return true;
                }
            }
        }

        @Generated
        protected boolean canEqual(final Object other) {
            return other instanceof ServerConfiguration;
        }

        @Generated
        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            Object $timeout = this.getTimeout();
            result = result * 59 + ($timeout == null ? 43 : $timeout.hashCode());
            Object $httpHeaders = this.getHttpHeaders();
            result = result * 59 + ($httpHeaders == null ? 43 : $httpHeaders.hashCode());
            return result;
        }

        @Generated
        public String toString() {
            String var10000 = String.valueOf(this.getHttpHeaders());
            return "MavenSettingsSimplified.ServerConfiguration(httpHeaders=" + var10000 + ", timeout=" + this.getTimeout() + ")";
        }

        @Generated
        public ServerConfiguration withHttpHeaders(final @Nullable List<HttpHeader> httpHeaders) {
            return this.httpHeaders == httpHeaders ? this : new ServerConfiguration(httpHeaders, this.timeout);
        }

        @Generated
        public ServerConfiguration withTimeout(final @Nullable Long timeout) {
            return this.timeout == timeout ? this : new ServerConfiguration(this.httpHeaders, timeout);
        }
    }

    public static class HttpHeader {
        private final String name;
        private final String value;

        @JsonCreator
        public HttpHeader(@JsonProperty("name") String name, @JsonProperty("value") String value) {
            this.name = name;
            this.value = value;
        }

        @Generated
        public String getName() {
            return this.name;
        }

        @Generated
        public String getValue() {
            return this.value;
        }

        @Generated
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            } else if (!(o instanceof HttpHeader)) {
                return false;
            } else {
                HttpHeader other = (HttpHeader)o;
                if (!other.canEqual(this)) {
                    return false;
                } else {
                    Object this$name = this.getName();
                    Object other$name = other.getName();
                    if (this$name == null) {
                        if (other$name != null) {
                            return false;
                        }
                    } else if (!this$name.equals(other$name)) {
                        return false;
                    }

                    Object this$value = this.getValue();
                    Object other$value = other.getValue();
                    if (this$value == null) {
                        if (other$value != null) {
                            return false;
                        }
                    } else if (!this$value.equals(other$value)) {
                        return false;
                    }

                    return true;
                }
            }
        }

        @Generated
        protected boolean canEqual(final Object other) {
            return other instanceof HttpHeader;
        }

        @Generated
        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            Object $name = this.getName();
            result = result * 59 + ($name == null ? 43 : $name.hashCode());
            Object $value = this.getValue();
            result = result * 59 + ($value == null ? 43 : $value.hashCode());
            return result;
        }

        @Generated
        public String toString() {
            String var10000 = this.getName();
            return "MavenSettingsSimplified.HttpHeader(name=" + var10000 + ", value=" + this.getValue() + ")";
        }

        @Generated
        public HttpHeader withName(final String name) {
            return this.name == name ? this : new HttpHeader(name, this.value);
        }

        @Generated
        public HttpHeader withValue(final String value) {
            return this.value == value ? this : new HttpHeader(this.name, value);
        }
    }
}
