ImageSearchWithVolley
=====================

Android用のVolleyという通信ライブラリを使った画像検索アプリです。

このアプリを動かすためには、
ImageSearchWithVolleyディレクトリの下にgradle.propertiesというファイルを作って、
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
