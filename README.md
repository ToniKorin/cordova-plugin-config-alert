# ConfigAlert plugin #
This [Cordova][cordova] plugin alerts configuration requirements of your __Android__ app and then opens the related settings, e.g. Protected Apps in Huawei devices. The plugin is also compatible with [PhoneGap Build][PGB].

## Supported Platforms ##
- __Android__

## Usage ##

#### Alert the 'Protected App' requirement in Huawei devices  ####
```javascript
cordova.plugins.ConfigAlert.alert();
```
#### All optional paramters with default values ####
```javascript
cordova.plugins.ConfigAlert.alert({
    title: "Huawei Protected Apps",
    message: "This app requires to be enabled in 'Protected Apps' to function properly.",
    ok: "Protected Apps",
    cancel: "Remind",
    package: "com.huawei.systemmanager",
    class: "optimize.process.ProtectActivity",
    force: false, // force alert, ignoring user selection and value of SharedPreference
    key: "skipAlert" // key to SharedPreference
});
```

## Installation ##
The plugin can either be installed from git repository, from local file system through the [Command-line Interface][CLI] or cloud based through [PhoneGap Build][PGB].

#### Local development environment ####
From master:
```bash
# ~~ from master branch ~~
cordova plugin add https://github.com/ToniKorin/cordova-plugin-config-alert.git
```
from a local folder:
```bash
# ~~ local folder ~~
cordova plugin add cordova-plugin-config-alert --searchpath path
```
or to use the latest stable version:
```bash
# ~~ stable version ~~
cordova plugin add cordova-plugin-config-alert@1.0.0
```

To remove the plug-in, run the following command:
```bash
cordova plugin rm cordova-plugin-config-alert
```

#### PhoneGap Build ####
Add the following xml line to your config.xml:
```xml
<gap:plugin platform="android" name="cordova-plugin-config-alert" version="1.0.0" source="npm"/>
```

## History ##
Check the [Change Log][changelog].

## License ##

This software is released under the [Apache 2.0 License][apache2_license].

© 2016 Toni Korin

[cordova]: https://cordova.apache.org
[CLI]: http://cordova.apache.org/docs/en/edge/guide_cli_index.md.html#The%20Command-line%20Interface
[PGB]: http://docs.build.phonegap.com/en_US/index.html
[PGB_plugin]: https://build.phonegap.com/plugins/490
[changelog]: https://github.com/ToniKorin/cordova-plugin-config-alert/blob/master/CHANGELOG.md
[apache2_license]: http://opensource.org/licenses/Apache-2.0