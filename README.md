## Openrewrite issues

### Jackson 2.17 vs 2.19

Test fails using `mvn test` !!
```shell
[INFO] 
[INFO] --- resources:3.3.1:testResources (default-testResources) @ openrewrite-issues ---
[INFO] skip non existing resourceDirectory /Users/cmoullia/code/application-modernisation/01_openrewrite/openrewrite-issues/src/test/resources
[INFO] 
[INFO] --- compiler:3.14.1:testCompile (default-testCompile) @ openrewrite-issues ---
[INFO] Recompiling the module because of changed dependency.
[INFO] Compiling 4 source files with javac [debug target 21] to target/test-classes
[WARNING] The following options were not recognized by any processor: '[rewrite.javaParserClasspathFrom]'
[INFO] 
[INFO] --- surefire:3.2.5:test (default-test) @ openrewrite-issues ---
[INFO] Using auto detected provider org.apache.maven.surefire.junitplatform.JUnitPlatformProvider
[INFO] 
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running org.openrewrite.issue.UserMavenSettingsTest
[ERROR] Tests run: 1, Failures: 0, Errors: 1, Skipped: 0, Time elapsed: 0.150 s <<< FAILURE! -- in org.openrewrite.issue.UserMavenSettingsTest
[ERROR] org.openrewrite.issue.UserMavenSettingsTest.serverHttpHeaders -- Time elapsed: 0.141 s <<< ERROR!
com.fasterxml.jackson.databind.exc.InvalidDefinitionException: 
Invalid definition for property 'httpHeaders' (of type `org.openrewrite.quarkus.maven.model.MavenSettings$ServerConfiguration`): Could not find creator property with name 'httpHeaders' (known Creator properties: [property, timeout])
 at [Source: (org.openrewrite.internal.EncodingDetectingInputStream); line: 1, column: 1]
        at com.fasterxml.jackson.databind.exc.InvalidDefinitionException.from(InvalidDefinitionException.java:62)
        at com.fasterxml.jackson.databind.DeserializationContext.reportBadPropertyDefinition(DeserializationContext.java:1938)
        at com.fasterxml.jackson.databind.deser.BeanDeserializerFactory.addBeanProps(BeanDeserializerFactory.java:644)
        at com.fasterxml.jackson.databind.deser.BeanDeserializerFactory.buildBeanDeserializer(BeanDeserializerFactory.java:278)
        at com.fasterxml.jackson.databind.deser.BeanDeserializerFactory.createBeanDeserializer(BeanDeserializerFactory.java:152)
        at com.fasterxml.jackson.databind.deser.DeserializerCache._createDeserializer2(DeserializerCache.java:472)
        at com.fasterxml.jackson.databind.deser.DeserializerCache._createDeserializer(DeserializerCache.java:416)
        at com.fasterxml.jackson.databind.deser.DeserializerCache._createAndCache2(DeserializerCache.java:318)
        at com.fasterxml.jackson.databind.deser.DeserializerCache._createAndCacheValueDeserializer(DeserializerCache.java:285)
        at com.fasterxml.jackson.databind.deser.DeserializerCache.findValueDeserializer(DeserializerCache.java:175)
        at com.fasterxml.jackson.databind.DeserializationContext.findNonContextualValueDeserializer(DeserializationContext.java:679)
        at com.fasterxml.jackson.databind.deser.BeanDeserializerBase.resolve(BeanDeserializerBase.java:555)
        at com.fasterxml.jackson.databind.deser.std.DelegatingDeserializer.resolve(DelegatingDeserializer.java:60)
        at com.fasterxml.jackson.databind.deser.DeserializerCache._createAndCache2(DeserializerCache.java:348)
        at com.fasterxml.jackson.databind.deser.DeserializerCache._createAndCacheValueDeserializer(DeserializerCache.java:285)
        at com.fasterxml.jackson.databind.deser.DeserializerCache.findValueDeserializer(DeserializerCache.java:175)
        at com.fasterxml.jackson.databind.DeserializationContext.findContextualValueDeserializer(DeserializationContext.java:656)
        at com.fasterxml.jackson.databind.deser.std.CollectionDeserializer.createContextual(CollectionDeserializer.java:189)
        at com.fasterxml.jackson.databind.deser.std.CollectionDeserializer.createContextual(CollectionDeserializer.java:29)
        at com.fasterxml.jackson.databind.DeserializationContext.handlePrimaryContextualization(DeserializationContext.java:884)
        at com.fasterxml.jackson.databind.deser.BeanDeserializerBase.resolve(BeanDeserializerBase.java:566)
        at com.fasterxml.jackson.databind.deser.std.DelegatingDeserializer.resolve(DelegatingDeserializer.java:60)
        at com.fasterxml.jackson.databind.deser.DeserializerCache._createAndCache2(DeserializerCache.java:348)
        at com.fasterxml.jackson.databind.deser.DeserializerCache._createAndCacheValueDeserializer(DeserializerCache.java:285)
        at com.fasterxml.jackson.databind.deser.DeserializerCache.findValueDeserializer(DeserializerCache.java:175)
        at com.fasterxml.jackson.databind.DeserializationContext.findNonContextualValueDeserializer(DeserializationContext.java:679)
        at com.fasterxml.jackson.databind.deser.BeanDeserializerBase.resolve(BeanDeserializerBase.java:555)
        at com.fasterxml.jackson.databind.deser.std.DelegatingDeserializer.resolve(DelegatingDeserializer.java:60)
        at com.fasterxml.jackson.databind.deser.DeserializerCache._createAndCache2(DeserializerCache.java:348)
        at com.fasterxml.jackson.databind.deser.DeserializerCache._createAndCacheValueDeserializer(DeserializerCache.java:285)
        at com.fasterxml.jackson.databind.deser.DeserializerCache.findValueDeserializer(DeserializerCache.java:175)
        at com.fasterxml.jackson.databind.DeserializationContext.findRootValueDeserializer(DeserializationContext.java:689)
        at com.fasterxml.jackson.databind.ObjectMapper._findRootDeserializer(ObjectMapper.java:5169)
        at com.fasterxml.jackson.databind.ObjectMapper._readMapAndClose(ObjectMapper.java:5039)
        at com.fasterxml.jackson.databind.ObjectMapper.readValue(ObjectMapper.java:3991)
        at org.openrewrite.issue.UserMavenSettingsTest.serverHttpHeaders(UserMavenSettingsTest.java:98)
        at java.base/java.lang.reflect.Method.invoke(Method.java:580)
        at java.base/java.util.ArrayList.forEach(ArrayList.java:1596)
        at java.base/java.util.ArrayList.forEach(ArrayList.java:1596)

```