# Michas Droid

Yet another F-Droid client.

[![Release](https://img.shields.io/github/v/release/michatec/michas-droid)](https://github.com/michatec/michas-droid/releases/latest)

## Description

Unofficial F-Droid client in the style of the classic one.

Jump over the lazy dog, manage repositories, and install software quickly. No privileged extension, root installation,
or sharing local repositories nearby.

### Features

* Classic F-Droid style
* No cards or inappropriate animations
* Fast repository syncing
* Standard Android components and minimal dependencies

### Screenshots

<p>
<img src="metadata/en-US/images/phoneScreenshots/1.png" width="15%" />
<img src="metadata/en-US/images/phoneScreenshots/2.png" width="15%" />
<img src="metadata/en-US/images/phoneScreenshots/3.png" width="15%" />
<img src="metadata/en-US/images/phoneScreenshots/4.png" width="15%" />
<img src="metadata/en-US/images/phoneScreenshots/5.png" width="15%" />
<img src="metadata/en-US/images/phoneScreenshots/6.png" width="15%" />
</p>

## Building and Installing

Specify your Android SDK path either using the `ANDROID_HOME` environment variable, or by filling out the `sdk.dir`
property in `local.properties`.

Signing can be done automatically using `keystore.properties` as follows:

```properties
store.file=/path/to/keystore
store.password=key-store-password
key.alias=key-alias
key.password=key-password
```

Run `./gradlew assembleRelease` to build the package, which can be installed using the Android package manager.

## License

Michas Droid is available under the terms of the GNU General Public License v3 or later. Copyright © 2026 Michatec.
