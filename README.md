# Depending on Lootr

These instructions are for Lootr for 1.21 and later. If you wish to depend on Lootr for earlier versions, use `noobanidus.mods.lootr:lootr-<MinecraftVersion>:<LootrVersion>`. Please note that only the Forge version of Lootr is available via Maven.

If you want to include Lootr in your mod workspace, BlameJared has graciously hosted it for us on Maven.

```gradle
repossitories {
    maven {
        url = 'https://maven.blamejared.com'
        content {
            includeGroup 'noobanidus.mods.lootr'
        }
    }
}
```

# Depending on Lootr in NeoForge (non-Architectury Loom)

If you only wish to include the code in your development environment but you *do not* want to include it in run-time, you can use the following, filling in `<LootrVersion>` with the relevant version:

```gradle
dependencies {
    compileOnly "noobanidus.mods.lootr:lootr-neoforge:<LootrVersion>"
}
```

If you also wish to include it at run-time, you can use the following:

```gradle
dependencies {
    implementation "noobanidus.mods.lootr:lootr-neoforge:<LootrVersion>"
}
```

# Depending on Lootr in Fabric/Architectury Loom

If you are using Fabric, or you are using Architectury Loom, the syntax is different:

```gradle
dependencies {
   modCompileOnly "noobanidus.mods.lootr:lootr-fabric:<LootrVersion>"
}
```

If you also wish to include it at run-time, you can use the following:

```gradle
dependencies {
    // For convenience, both compileOnly and runtimeOnly are included so you can comment out the runtimeOnly for testing purposes
    modCompileOnly "noobanidus.mods.lootr:lootr-fabric:<LootrVersion>"
    modRuntimeOnly "noobanidus.mods.lootr:lootr-fabric:<LootrVersion>"
    
    // Alternately you can use 
}
```

If you are using NeoForge within an Architectury Loom environment ,use the same syntax as for Fabric but replace `fabric` with `neoforge`:

```gradle
dependencies {
   modCompileOnly "noobanidus.mods.lootr:lootr-neoforge:<LootrVersion>"
   // modRuntimeOnly "noobanidus.mods.lootr:lootr-neoforge:<LootrVersion>"
}
```

# Multi-project Common Source Set

If you are developing in a multi-project set-up, you can also include the "common" source set containing the shared code between the Fabric and NeoForge versions of Lootr. This is useful if you are developing a mod that is intended to be used in both environments.

```gradle
dependencies {
    // Run-time is never relevant for the common source set
    modCompileOnly "noobanidus.mods.lootr:lootr-common:<LootrVersion>"
}
```

# Available Versions

You can [browse the available versions](https://maven.blamejared.com/noobanidus/mods/lootr/) on BlameJared's maven repository. 

Versions are formatted as `MinecraftVersion-LootrVersion`.

For Minecraft 1.21, you can use (for example): `1.21-1.10.33.80`.

For Minecraft 1.21.4, you can use (for example): `1.21.4-1.12.36.87`.

Please check the available versions for the latest version of Lootr for your Minecraft version.

