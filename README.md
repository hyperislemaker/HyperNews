# HyperNews 📰

Telegram tarzı modern haber okuma uygulaması. HyperOS tasarım diliyle geliştirilmiş, Kotlin ve Jetpack Compose kullanan Android uygulaması.

## ✨ Özellikler

### 📱 Temel Özellikler
- **RSS Besleme Yönetimi** - Sınırsız haber kaynağı ekleyin
- **Akıllı Özet Çıkarma** - Haberlerin otomatik özetlenmesi
- **Çevrimdışı Destek** - İnternet olmadan da haberlerinizi okuyun
- **Son Dakika Bildirimleri** - HyperIsle overlay ile anlık haberler
- **Arama ve Filtreleme** - Kaynak bazlı filtreleme ve geçmiş

### 💬 Sosyal Özellikler
- **Yorum Sistemi** - Haberler hakkında yorum yapın
- **Tepki Sistemi** - 6 farklı emoji ile tepki verin (👍❤️😮😢😠🤔)
- **Profil Yönetimi** - Özelleştirilebilir kullanıcı profilleri
- **Küfür Filtresi** - Otomatik içerik moderasyonu

### 👑 Premium Özellikler
- Reklamsız deneyim
- Sınırsız RSS kaynağı
- 90 gün haber arşivi
- Gelişmiş arama filtreleri
- Özel bildirim anahtar kelimeleri
- Okuma listesi senkronizasyonu
- Detaylı okuma istatistikleri
- Özel tema seçenekleri

### 🎨 Tasarım
- HyperOS Dark Theme
- Material 3 tasarım sistemi
- Smooth animasyonlar
- Erişilebilirlik desteği (TalkBack, font ölçekleme)

## 🏗️ Mimari

```
app/
├── data/           # Veri katmanı
│   ├── local/      # Room Database
│   ├── remote/     # Firebase, RSS
│   ├── cache/      # Image & Data cache
│   └── mapper/     # Entity-Domain dönüşümleri
├── domain/         # İş mantığı
│   ├── model/      # Domain modelleri
│   ├── repository/ # Repository interfaces
│   └── common/     # Result, AppError
├── presentation/   # UI katmanı
│   ├── screens/    # Compose ekranları
│   ├── ui/         # Tema, bileşenler
│   └── navigation/ # Navigation graph
├── di/             # Hilt modülleri
└── worker/         # WorkManager
```

## 🛠️ Teknolojiler

| Kategori | Teknoloji |
|----------|-----------|
| UI | Jetpack Compose, Material 3 |
| DI | Hilt |
| Database | Room |
| Network | OkHttp, Rome RSS |
| Backend | Firebase (Auth, Firestore, Storage) |
| Async | Kotlin Coroutines, Flow |
| Image | Coil |
| Background | WorkManager |
| Billing | Google Play Billing v6 |
| Ads | AdMob |
| Testing | Kotest (Property-based) |

## 📋 Gereksinimler

- Android 8.0 (API 26) veya üzeri
- Google Play Services
- İnternet bağlantısı (ilk kurulum için)

## 🚀 Kurulum

1. Projeyi klonlayın:
```bash
git clone https://github.com/hyperislemaker/HyperNews.git
```

2. `google-services.json` dosyanızı `app/` klasörüne ekleyin

3. Android Studio'da projeyi açın

4. Gradle sync yapın ve çalıştırın

## 🔧 Yapılandırma

### Firebase
- Authentication (Google, Email/Password)
- Firestore Database
- Cloud Storage

### AdMob
`AndroidManifest.xml` içinde AdMob App ID'nizi güncelleyin.

## 📊 Test

Property-based testleri çalıştırmak için:
```bash
./gradlew :app:testDebugUnitTest --tests "*PropertyTest"
```

## 📄 Lisans

Bu proje MIT lisansı altında lisanslanmıştır.

## 👨‍💻 Geliştirici

HyperIsle Maker

---

⭐ Projeyi beğendiyseniz yıldız vermeyi unutmayın!
