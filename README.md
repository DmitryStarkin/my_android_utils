# A small Kotlin library Util for Android

The library for internal myself use
contains functions usually used in my projects
the library is not intended for distribution, you can use it at your own risk


## Current Version:

0.17.6b

## Installation:

1 in project level build.gradle add:
```
allprojects {
repositories {
........
        maven { url "https://jitpack.io" }
        }
   }
```

or in setting.gradle add:

```
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
    ......
    maven { url 'https://jitpack.io' }
    }
}
```

2 in module level build.gradle add:
```
dependencies {
...........
         implementation 'com.github.DmitryStarkin:my_android_utils:version'
   }
```

