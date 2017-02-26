# Sofia-Traffic (Градски Транспорт София)

[The app](https://play.google.com/store/apps/details?id=com.bearenterprises.sofiatraffic) allows the user to check the arrival time of trolleys, buses and trams in the SUMC system. Other features include favourites(just stops for now), search by line, show stop on the map, search for stops on the map or search by place (for e.g. you can search for Serdika Center which will give you the nearest stops to Serdika Center).

## Prerequisites
The app expects to find a file `api_keys.xml` in the `res/values` folder in the following format :
```
<resources>
    <string name="google_maps_api_key">MAPS_KEY</string>
    <string name="android_backup_api_key">ANDROID_BACKUP_KEY</string>
    <string name="ivkos_api_key">IVKOS_API_KEY</string>
</resources>
```

The app also expects to find a `keystore.properties` file in the root directory in the following format:

```
keyAlias=...
keyPassword=...
storeFile=...
storePassword=...
```

## Ivkos API
For the stops information I'm using an API provided by Ivaylo Stoyanov (https://github.com/ivkos).

## Stop coordinates
The stops' coordinates are acquired from https://www.sofiatraffic.bg/interactivecard/ using [this](https://github.com/thalvadzhi/Sofia-Traffic-Stops-Getter) script.
