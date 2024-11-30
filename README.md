<h1>TokenManager</h1> 

[![](https://jitpack.io/v/Roland882/TokenManager.svg)](https://jitpack.io/#Roland882/TokenManager)

### Please note that this is not the original plugin this a forked version that supports 1.21.

---

* **[Discord](https://discord.gg/vA4Xg9KqeJ)**
* **[Wiki](https://github.com/Realizedd/TokenManager/wiki)**
* **[Commands](https://github.com/Realizedd/TokenManager/wiki/commands)**
* **[Permissions](https://github.com/Realizedd/TokenManager/wiki/permissions)**
* **[config.yml](https://github.com/Roland882/TokenManager/blob/master/src/main/resources/config.yml)**
* **[lang.yml](https://github.com/Roland882/TokenManager/blob/master/src/main/resources/lang.yml)**
* **[shops.yml](https://github.com/Roland882/TokenManager/blob/master/src/main/resources/shops.yml)**


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
    implementation 'com.github.Roland882:TokenManager:Tag'
}
```  
Replace 'VERSION' with the tag

Maven:
```xml
<dependency>
    <groupId>com.github.Roland882</groupId>
    <artifactId>TokenManager</artifactId>
    <version>Tag</version>
</dependency>
```
Replace 'VERSION' with the tag

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
