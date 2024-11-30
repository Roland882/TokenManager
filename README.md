<h1>TokenManager</h1> 

[![](https://jitpack.io/v/Nexatrix-Studios/TokenManager.svg)](https://jitpack.io/#Nexatrix-Studios/TokenManager)

### Please note that this is not the original plugin this a forked version that supports 1.21.

---

* **[Discord](https://discord.gg/vA4Xg9KqeJ)**
* **[Wiki](https://github.com/Realizedd/TokenManager/wiki)**
* **[Commands](https://github.com/Realizedd/TokenManager/wiki/commands)**
* **[Permissions](https://github.com/Realizedd/TokenManager/wiki/permissions)**
* **[config.yml](https://github.com/Nexatrix-Studios/TokenManager/blob/master/src/main/resources/config.yml)**
* **[lang.yml](https://github.com/Nexatrix-Studios/TokenManager/blob/master/src/main/resources/lang.yml)**
* **[shops.yml](https://github.com/Nexatrix-Studios/TokenManager/blob/master/src/main/resources/shops.yml)**


### Getting the dependency

#### Repository
Gradle:
```groovy
maven {
    name 'jitpack-repo'
    url 'https://jitpack.io'
}
```

Maven:
```xml
<repository>
  <id>jitpack-repo</id>
  <url>https://jitpack.io</url>
</repository>
```

#### Dependency
Gradle:
```groovy
dependencies {
    implementation 'com.github.Nexatrix-Studios:TokenManager:VERSION'
}
```  
Replace 'VERSION' with version

Maven:
```xml
<dependency>
    <groupId>com.github.Nexatrix-Studios</groupId>
    <artifactId>TokenManager</artifactId>
    <version>VERSION</version>
</dependency>
```
Replace 'VERSION' with version

### plugin.yml
Add TokenManager as a soft-depend to ensure TokenManager is fully loaded before your plugin.
```yaml
soft-depend: [TokenManager]
```

### Getting the API instance

```java
@Override
public void onEnable() {
  TokenManager api = (TokenManager) Bukkit.getServer().getPluginManager().getPlugin("TokenManager");
}
```
