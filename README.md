ImageSearchWithVolley
=====================

Android用のVolleyという通信ライブラリを使った画像検索アプリです。

このアプリを動かすためには、
appディレクトリの下にgradle.propertiesというファイルを作って、
```
AWS_ENDPOINT="ecs.amazonaws.jp"
AWS_ACCESS_KEY_ID="XXX"
AWS_SECRET_KEY="XXX"
AWS_ASSOCIATE_TAG="XXX-22"
BING_API_KEY="XXX"
FLICKR_API_KEY="XXX"
```
という感じでAmazon Product Advertising APIのキーとBing Search APIのキーとFlickrのAPIのキーを指定する必要があります。
（これらのキーの詳細説明については、とりあえずここでは割愛させて頂きます。）

また、リリースビルド作成用に、gradle.propertiesには
```
storeFile=release.keystore
storePassword=*****
keyAlias=*****
keyPassword=*****
```
というのも入れていたりします。

その他注意すべき所は、Crashlyticsを導入しているので、そこらへんではまるかもしれません。
ビルドする際に、AndroidManifest.xmlの一番下の方にあるCrachlyticsのAPIのキーを入れていないとビルドに失敗します。
```
<meta-data android:name="com.crashlytics.ApiKey" android:value="YOUR_CRASHLYTICS_APIKEY"/>
```
Crashlyticsを使わない人はここの行は削除した方がいいかも。
その他のCrashlytics関連で削るべき所は、
* app/build.gradle
```
maven { url 'http://download.crashlytics.com/maven' }
```
```
classpath 'com.crashlytics.tools.gradle:crashlytics-gradle:1.+'
```
```
compile 'com.crashlytics.android:crashlytics:1.+'
```
```
buildConfigField "boolean", "USE_CRASHLYTICS", "false"
ext.enableCrashlytics = false
```
```
buildConfigField "boolean", "USE_CRASHLYTICS", "true"
ext.enableCrashlytics = true
```
* app/src/main/java/org/nkoriyama/imagesearchwithvolley/ImageSearchWithVolley.java
```java
if (BuildConfig.USE_CRASHLYTICS) {
  Crashlytics.start(this);
}
```
といったところでしょうか。
