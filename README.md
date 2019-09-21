Kartları Eşleştir - Android / Anıl Furkan Ökçün
=======

## Mevcut Özellikler (Supported Features)

* `MVVM` architecture patterni kullanılmıştır.
* `Firebase Authentication`, `Firebase Realtime Database`, `Firebase Storage`,
* `Kotlin Extensions`, `Higher Order Functions`, `Type aliases`, `Data classes`, `Kotlin Synthetic Import`
* `Navigation Component`, `ViewModel`, `LiveData` kullanılmıştır.
* Düşük Apk Boyutu: Görseller için `Vector Drawables`, Fontlar için `Downloadable Fonts`, Gereksiz kodların temizliği için `ProGuard` kullanılmıştır.
* Güvenlik: `ProGuard` ile kod gizleme, `Firebase Realtime Database Rules` ile data güvenliği sağlanmıştır.
* Telefondan görsel yüklemek için **Kotlin ile geliştirdiğim kütüphanem** [UWMediaPicker](https://github.com/AnilFurkanOkcun/UWMediaPicker-Android) kullanılmıştır.
* Farklı ekran boyutlarına uyum sağlayabilmek ve tek bir noktadan tüm boyutları değiştirebilmek için `dimens.xml` kullanılmıştır.
* Avatar için görsel upload edilmeden önce kare şeklinde kırpılır, 200x200 px olarak yeniden boyutlandırılır.
* Gerektiğinde oyun süresi, kart kapanma bekleme süresi, liderler sayfası lider limiti gibi sayısal sabitleri tek bir noktadan değiştirebilmek için `integers.xml` kullanılmıştır.
* Kullanıcı adı ve şifre girdi alanlarına karakter sınırı koyulmuş ve alfanumerik olmayan karakterlerin girilmesi engellenmiştir. Şifre girdi alanında özel karakterlere izin verilmiştir.
* Uygulama hem İngilizce hem Türkçe olarak çalışmaktadır. (Splash ekranındaki görsel dahil)
* Uygulama açılırken Splash ekranından önce bir an için beyaz bir ekranın gözükmemesi için Splash ekranında `layout` yerine `theme` kullanılmıştır.

![picture](https://bitbucket.org/AnilFurkanOkcun/matchcards-android/raw/5fb00c126d36ec1de5df4b03a95040549f797591/Screenshots/14-Screenshots-Collage.jpg)

[Tüm ekran görüntülerini inceleyebilirsiniz.](https://bitbucket.org/AnilFurkanOkcun/matchcards-android/src/master/Screenshots/)

* Kullanıcılar, avatar, takma ad ve şifre ile hesap oluşturabilmektedirler.
* Kullanıcılar, uygulamada varsayılan olarak sunulan 8 avatardan birini ya da telefonlarından herhangi bir görseli avatar olarak kullanabilirler.
* Kullanıcılar, takma ad ve şifre ile uygulamaya giriş yapabilirler.
* Anasayfada, kullanıcının avatarı, takma adı, son skoru ve en yüksek kişisel skoru gösterilmektedir.
* Kart eşleştirme oyunu 4 seviyeden oluşur ve maksimum 3 dakika süre verilir.
* Kullanıcılar, oyunu verilen süre içinde tamamlarlar ise artan sürenin 3 katı skorlarına eklenir.
* Kullanıcılar, oyun sonunda skorlarını ve uygulamanın linkini içeren otomatik oluşturulmuş bir yazıyı sosyal medyada paylaşabilirler. 
* Kullanıcılar, liderler sayfasından ilk 20 lideri görebilirler.

## Eklenebilecek Özellikler (Feature List)

* Liderler listesindeki ilk üçte bulunan kullanıcıların, skorları başka bir kullanıcı tarafından elimine edilir ise sırası değişen kullancılara bildirim atılır. Bu özellik için `Firebase Cloud Functions`'a gerekli fonksiyon eklenir ve gerektiğinde `Firebase Cloud Messaging` ile kullanıcya bildirim atılabilir.
* Ses efektleri eklenebilir.
* Daha hızlı, IDE dostu, Type-Safe, statik yazımlı dependency yönetimi için `Kotlin DSL` kullanılabilir.

## Bağımlılıklar (Dependencies)

* [UWMediaPicker](https://github.com/AnilFurkanOkcun/UWMediaPicker-Android): Görsel seçimi için kendi geliştirdiğim kütüphane.
* AndroidX : Android support kütüphaneleri yerine.
* Kotlin
* Architecture Lifecycle Components
* Navigation Component
* Firebase Core, Fireabse Auth, Firebase Storage, Firebase Database
* Glide
* [RoundedImageView](https://github.com/vinc3m1/RoundedImageView): Yuvarlak ImageView. 

## Güvenlik (Security)

**1. Kod Gizleme**

Apk decompile edildiğinde kodların okunmasını zorlaştırmak için Proguard kullanılmıştır. Data Transfer Object classlarının isimlerinin değişmemesi için ProGuard kuralları tanımlanmıştır.

**2. Firebase**

Apk decompile olduğunda veya proje halihazırda açık kaynak olarak paylaşıldığı için Firebase api key'leri kolayca elde edilebilir. Bu key'lerin kötüye kullanımını engellemek için Firebase Realtime Database'de kurallar tanımlanmıştır. Doğrulanmış(authenticated) kullanıcılara "Users" tablosunda sadece kendi verilerini okuma ve sadece kendi verilerine yazma izni verilmiştir. "Leaders" tablosunda ise tüm tabloyu okuma izni ve sadece kendi verilerini güncelleme izni verilmiştir.
```
{
  "rules": {
    "users": {
      "$uid": {
        ".write": "$uid === auth.uid",
        ".read": "$uid === auth.uid"
      }
    },
    "leaders": {
      ".read": "true",
      "$uid": {
        ".write": "$uid === auth.uid"
      }
    }
  }
}
```

## Kullanılan Görsel Lisansları

[Lego avatarları](https://www.flaticon.com/packs/lego-avatars-5), [Smashicons](https://www.flaticon.com/authors/smashicons) tarafından [Flaticon Temel Lisansı](https://file000.flaticon.com/downloads/license/license.pdf) altında www.flaticon.com üzerinde paylaşılmıştır.

[Ülke bayrakları](https://www.flaticon.com/packs/countrys-flags), [Freepik](https://www.flaticon.com/authors/freepik) tarafından [Flaticon Temel Lisansı](https://file000.flaticon.com/downloads/license/license.pdf) altında www.flaticon.com üzerinde paylaşılmıştır.
