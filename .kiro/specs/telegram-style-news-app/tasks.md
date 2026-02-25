# Implementation Plan: Telegram-Style News App

## Overview

Bu implementasyon planı, Telegram tarzı haber uygulamasının tüm özelliklerini kapsayan adım adım kodlama görevlerini içerir. Uygulama Kotlin ve Jetpack Compose kullanılarak geliştirilecek, Clean Architecture ve MVVM pattern'i uygulanacaktır.

## Tasks

- [x] 1. Proje yapısı ve temel konfigürasyon
  - [x] 1.1 Android projesi oluştur ve Gradle bağımlılıklarını yapılandır
    - compileSdk 34, minSdk 26, targetSdk 34
    - Jetpack Compose, Room, Hilt, Retrofit, Firebase, Coil, WorkManager bağımlılıkları
    - Kotest property testing bağımlılıkları
    - Google Play Billing ve AdMob bağımlılıkları
    - _Requirements: Teknik Kısıtlamalar_

  - [x] 1.2 Clean Architecture modül yapısını oluştur
    - presentation/, domain/, data/, worker/, di/ klasör yapısı
    - Her katman için base sınıfları ve interface'leri tanımla
    - _Requirements: Architecture_

  - [x] 1.3 Hilt dependency injection modüllerini yapılandır
    - AppModule, DatabaseModule, NetworkModule, FirebaseModule, RepositoryModule
    - _Requirements: Architecture_


- [x] 2. Domain katmanı - Temel modeller ve interface'ler
  - [x] 2.1 Domain modellerini oluştur
    - NewsItem, RssFeed, UserProfile, Comment, Reaction, ReportedComment, AppStatistics
    - ReactionType enum (LIKE, LOVE, WOW, SAD, ANGRY, THINKING)
    - SubscriptionPlan, SubscriptionStatus, ThemePreference modelleri
    - _Requirements: 2.2, 2.3, 21.1-21.16, 22.6, 23.5, 26.2_

  - [x] 2.2 Property test: NewsItem round-trip
    - **Property 1: RSS Besleme Round-Trip**
    - **Validates: Requirements 1.1, 1.5**

  - [x] 2.3 Repository interface'lerini tanımla
    - NewsRepository, RssFeedRepository, UserRepository, CommentRepository, ReactionRepository
    - AdminRepository, SubscriptionRepository, ReadingListRepository, StatisticsRepository
    - _Requirements: Architecture_

- [x] 3. Data katmanı - Room Database
  - [x] 3.1 Room Entity'lerini oluştur
    - NewsItemEntity, RssFeedEntity, SearchHistoryEntity, AppSettingsEntity
    - Favori durumu, son dakika işareti, yayın tarihi alanları
    - _Requirements: 1.5, 2.5, 7.1, 11.2, 12.6_

  - [x] 3.2 Room DAO'larını implement et
    - NewsItemDao: getNewsPaged, getFavorites, searchNews, insertAll, deleteOldNews, update
    - RssFeedDao: getActiveFeeds, insert, delete, update
    - SearchHistoryDao: getHistory, insert, clearHistory
    - _Requirements: 5.1, 7.2, 11.4, 12.2, 12.7_

  - [x] 3.3 Property test: Duplicate haber önleme
    - **Property 5: Duplicate Haber Önleme**
    - **Validates: Requirements 2.6**

  - [x] 3.4 AppDatabase sınıfını oluştur ve migration stratejisini belirle
    - _Requirements: 7.1, 7.6_

- [x] 4. Checkpoint - Veritabanı katmanı tamamlandı
  - Ensure all tests pass, ask the user if questions arise.


- [x] 5. Data katmanı - RSS Parsing ve Network
  - [x] 5.1 RSS Parser servisini implement et (Rome RSS Parser)
    - URL validasyonu, XML ayrıştırma, hata toleransı
    - Timeout: 10 saniye, Retry: 3 deneme
    - _Requirements: 1.2, 1.3, 2.2, 2.3, 2.4, 2.7_

  - [x] 5.2 Property test: RSS URL validasyonu
    - **Property 2: RSS URL Validasyonu**
    - **Validates: Requirements 1.2, 1.3**

  - [x] 5.3 Property test: RSS parsing doğruluğu
    - **Property 4: RSS Parsing Doğruluğu**
    - **Validates: Requirements 2.2, 2.3, 2.7**

  - [x] 5.4 Summary Generator'ı implement et
    - TextRank algoritması (arka plan için)
    - Extractive summarization (anlık işlem için)
    - HTML temizleme, 300 karakter sınırı
    - _Requirements: RSS Özet Çıkarma Stratejisi_

  - [x] 5.5 RssFeedManager'ı implement et
    - addFeed, removeFeed, fetchAllFeeds, fetchFeed, validateFeedUrl
    - _Requirements: 1.1, 1.4, 1.5, 1.6, 2.1_

  - [x] 5.6 Property test: RSS besleme silme
    - **Property 3: RSS Besleme Silme**
    - **Validates: Requirements 1.6**

  - [x] 5.7 Property test: Hata toleransı
    - **Property 6: Hata Toleransı - RSS Çekme**
    - **Validates: Requirements 2.4**

- [x] 6. Data katmanı - Firebase entegrasyonu
  - [x] 6.1 Firebase Authentication servisini implement et
    - Google Sign-In, Email/Password authentication
    - AuthManager interface implementasyonu
    - _Requirements: 21.1_

  - [x] 6.2 Firestore servislerini implement et
    - UserProfileManager: createProfile, updateProfile, checkUserNameAvailability, suggestUserNames
    - Kullanıcı adı benzersizlik kontrolü (/usernames/{userName})
    - _Requirements: 21.3-21.16_

  - [x] 6.3 Property test: Kullanıcı adı validasyonu
    - **Property 17: Kullanıcı Adı Validasyonu**
    - **Validates: Requirements 21.3, 21.4, 21.15, 21.16**

  - [x] 6.4 Property test: Kullanıcı adı benzersizliği
    - **Property 18: Kullanıcı Adı Benzersizliği**
    - **Validates: Requirements 21.5**

  - [x] 6.5 Profil resmi işleme servisini implement et
    - 2 MB boyut kontrolü, 512x512 resize
    - Avatar oluşturma (baş harflerden)
    - _Requirements: 21.7-21.10_

  - [x] 6.6 Property test: Profil resmi boyut kontrolü
    - **Property 19: Profil Resmi Boyut Kontrolü**
    - **Validates: Requirements 21.8, 21.9**

  - [x] 6.7 Property test: Avatar oluşturma
    - **Property 20: Avatar Oluşturma**
    - **Validates: Requirements 21.10**


- [x] 7. Checkpoint - Data katmanı temel servisleri tamamlandı
  - Ensure all tests pass, ask the user if questions arise.

- [x] 8. Yorum sistemi implementasyonu
  - [x] 8.1 CommentManager'ı implement et
    - addComment, editComment, deleteComment, getComments, reportComment
    - Firestore /news_items/{newsId}/comments/ koleksiyonu
    - Sayfalama (20 yorum/sayfa), gerçek zamanlı güncelleme
    - _Requirements: 22.1, 22.6-22.11, 22.19-22.26_

  - [x] 8.2 Küfür filtresi (ProfanityFilter) implement et
    - 100+ Türkçe küfür/hakaret kelimesi
    - Kelime varyasyonları kontrolü (k*fir, k.ü.f.ü.r)
    - Firestore /app_settings/profanity_filter array
    - _Requirements: 22.15-22.18_

  - [x] 8.3 Property test: Yorum karakter sınırı
    - **Property 22: Yorum Karakter Sınırı**
    - **Validates: Requirements 22.12, 22.14**

  - [x] 8.4 Property test: Küfür filtresi
    - **Property 23: Küfür Filtresi**
    - **Validates: Requirements 22.15, 22.17, 22.18**

  - [x] 8.5 Property test: Yorum düzenleme yetkisi
    - **Property 24: Yorum Düzenleme Yetkisi**
    - **Validates: Requirements 22.9**

- [x] 9. Tepki sistemi implementasyonu
  - [x] 9.1 ReactionManager'ı implement et
    - addReaction, removeReaction, getReactionCounts, getUsersWhoReacted
    - Toggle davranışı, kullanıcı başına tek tepki
    - Offline destek ve senkronizasyon
    - _Requirements: 23.1-23.18_

  - [x] 9.2 Property test: Tepki kuralları
    - **Property 25: Tepki Kuralları**
    - **Validates: Requirements 23.8, 23.9, 23.10**

  - [x] 9.3 Property test: Tepki sayısı tutarlılığı
    - **Property 26: Tepki Sayısı Tutarlılığı**
    - **Validates: Requirements 23.2**

  - [x] 9.4 Property test: Giriş kontrolü - sosyal özellikler
    - **Property 21: Giriş Kontrolü - Sosyal Özellikler**
    - **Validates: Requirements 22.2, 23.3**


- [x] 10. Admin paneli ve moderasyon
  - [x] 10.1 AdminManager'ı implement et
    - isAdmin, getReportedComments, deleteComment, rejectReport
    - banUser, unbanUser, getAppStatistics
    - Firestore /admins/{userId} koleksiyonu
    - _Requirements: 24.1-24.12_

  - [x] 10.2 Property test: Admin yetki kontrolü
    - **Property 27: Admin Yetki Kontrolü**
    - **Validates: Requirements 24.2, 24.3**

  - [x] 10.3 Property test: Kullanıcı engelleme
    - **Property 28: Kullanıcı Engelleme**
    - **Validates: Requirements 24.9, 24.10**

- [x] 11. Checkpoint - Sosyal özellikler tamamlandı
  - Ensure all tests pass, ask the user if questions arise.

- [x] 12. Son dakika haber sistemi
  - [x] 12.1 BreakingNewsDetector'ı implement et
    - Anahtar kelime kontrolü (son dakika, breaking, acil, flaş)
    - RSS kategori kontrolü
    - Yayın tarihi kontrolü (son 30 dakika)
    - Kullanıcı tanımlı anahtar kelimeler
    - _Requirements: 25.1-25.10_

  - [x] 12.2 Property test: Son dakika haber tespiti
    - **Property 29: Son Dakika Haber Tespiti**
    - **Validates: Requirements 25.1**

  - [x] 12.3 HyperIsleManager'ı implement et
    - Overlay window, 5 saniye görünürlük
    - Marquee animasyonu, spam önleme (15 dakika)
    - Sadece arka planda tetikleme
    - _Requirements: 8.1-8.6, 25.3-25.5_

  - [x] 12.4 Property test: HyperIsle spam önleme
    - **Property 30: HyperIsle Spam Önleme**
    - **Validates: Requirements 25.5**

  - [x] 12.5 Property test: Son dakika öncelik sıralaması
    - **Property 31: Son Dakika Öncelik Sıralaması**
    - **Validates: Requirements 25.3**


- [x] 13. Arka plan işleme ve bildirimler
  - [x] 13.1 NewsSyncWorker'ı implement et (WorkManager)
    - 15 dakikalık periyodik güncelleme
    - Ağ bağlantısı kontrolü, pil tasarrufu modu desteği
    - Exponential backoff retry policy
    - _Requirements: 3.1-3.6_

  - [x] 13.2 NotificationManager'ı implement et
    - Android Notification Channels (Genel Haberler, Son Dakika)
    - Bildirim içeriği: başlık + kaynak adı
    - Deep link ile haber detayına yönlendirme
    - _Requirements: 4.1-4.6_

  - [x] 13.3 Property test: Bildirim içerik doğruluğu
    - **Property 8: Bildirim İçerik Doğruluğu**
    - **Validates: Requirements 4.1, 4.2**

- [x] 14. Çevrimdışı destek ve cache
  - [x] 14.1 ImageCacheManager'ı implement et (Coil)
    - Disk cache: 100 MB, Memory cache: %25
    - LRU eviction policy
    - _Requirements: 7.4, 15.2, 15.3_

  - [x] 14.2 Property test: Görsel cache round-trip
    - **Property 11: Görsel Cache Round-Trip**
    - **Validates: Requirements 7.4**

  - [x] 14.3 Çevrimdışı mod göstergesi ve otomatik senkronizasyon
    - Bağlantı durumu izleme
    - Otomatik içerik çekme (bağlantı geldiğinde)
    - _Requirements: 7.2, 7.3, 7.5_

  - [x] 14.4 Property test: Çevrimdışı veri erişimi
    - **Property 10: Çevrimdışı Veri Erişimi**
    - **Validates: Requirements 7.1, 7.2**

- [x] 15. Checkpoint - Backend servisleri tamamlandı
  - Ensure all tests pass, ask the user if questions arise.


- [x] 16. UI Tema ve Stil sistemi
  - [x] 16.1 HyperOS Dark Theme renk paletini tanımla
    - Primary, Background, Surface, Text renkleri
    - Reaction renkleri, Breaking News badge
    - _Requirements: 9.5, 10.1, 10.2_

  - [x] 16.2 Tipografi sistemini tanımla
    - HeadlineLarge, HeadlineMedium, TitleLarge, BodyLarge, BodyMedium, LabelMedium, Caption
    - Inter/Roboto font ailesi
    - _Requirements: 9.4_

  - [x] 16.3 Tema yönetimi (ThemeManager) implement et
    - Dark/Light/System tema seçenekleri
    - Tema tercihi kaydetme ve yükleme
    - _Requirements: 10.3-10.6_

  - [x] 16.4 Property test: Tema tercihi round-trip
    - **Property 16: Tema Tercihi Round-Trip**
    - **Validates: Requirements 10.3, 10.5**

- [x] 17. Temel UI bileşenleri
  - [x] 17.1 NewsCard bileşenini implement et
    - 16dp yuvarlak köşe, stroke çizgi
    - Görsel (16:9), başlık, özet, metadata
    - Son dakika badge, tepki özeti, yorum sayısı
    - _Requirements: 5.2, 5.3, 9.2, 9.3, 25.10_

  - [x] 17.2 HyperIsleOverlay bileşenini implement et
    - Slide animasyonu, marquee text
    - 5 saniye otomatik kapanma
    - _Requirements: 8.1-8.5_

  - [x] 17.3 ReactionBottomSheet bileşenini implement et
    - 6 emoji tepkisi, sayılar, seçim durumu
    - Animasyonlu tepki seçimi
    - _Requirements: 23.4-23.7, 23.14_

  - [x] 17.4 CommentSection bileşenini implement et
    - Yorum input alanı, karakter sayacı
    - Yorum listesi, düzenle/sil butonları
    - Bildir butonu, loading indicator
    - _Requirements: 22.3-22.8, 22.13, 22.19-22.25_


- [x] 18. Ana ekranlar - Haber akışı
  - [x] 18.1 NewsFeedScreen'i implement et
    - Kronolojik sıralama (en yeni üstte)
    - Sonsuz kaydırma, pull-to-refresh
    - Çevrimdışı göstergesi
    - _Requirements: 5.1, 5.4-5.7_

  - [x] 18.2 Property test: Haber kronolojik sıralama
    - **Property 7: Haber Kronolojik Sıralama**
    - **Validates: Requirements 5.1, 22.7**

  - [x] 18.3 NewsFeedViewModel'i implement et
    - UI state yönetimi, sayfalama
    - Kaynak filtreleme
    - _Requirements: 5.5, 13.2-13.6_

- [x] 19. Ana ekranlar - Haber detay
  - [x] 19.1 NewsDetailScreen'i implement et
    - Başlık, görsel, özet, yayın tarihi, kaynak
    - Kaynağa git butonu (in-app/external browser)
    - Paylaş, favorilere ekle butonları
    - _Requirements: 6.1-6.5_

  - [x] 19.2 Yorum ve tepki bölümlerini entegre et
    - Giriş kontrolü, yorum yazma
    - Tepki bottom sheet
    - _Requirements: 22.1-22.26, 23.1-23.18_

- [x] 20. Ana ekranlar - Favoriler ve Arama
  - [x] 20.1 FavoritesScreen'i implement et
    - Favori haberler listesi
    - Favoriden kaldırma
    - _Requirements: 11.1-11.6_

  - [x] 20.2 Property test: Favori round-trip
    - **Property 9: Favori Round-Trip**
    - **Validates: Requirements 6.5, 11.1, 11.2, 11.4, 11.5**

  - [x] 20.3 SearchScreen'i implement et
    - Arama çubuğu, minimum 3 karakter
    - Arama geçmişi, sonuç bulunamadı mesajı
    - _Requirements: 12.1-12.7_

  - [x] 20.4 Property test: Arama fonksiyonu doğruluğu
    - **Property 12: Arama Fonksiyonu Doğruluğu**
    - **Validates: Requirements 12.2, 12.4**

  - [x] 20.5 Property test: Arama geçmişi round-trip
    - **Property 13: Arama Geçmişi Round-Trip**
    - **Validates: Requirements 12.6, 12.7**

  - [x] 20.6 Property test: Kaynak filtreleme
    - **Property 14: Kaynak Filtreleme**
    - **Validates: Requirements 13.2, 13.3**


- [x] 21. Checkpoint - Ana ekranlar tamamlandı
  - Ensure all tests pass, ask the user if questions arise.

- [x] 22. Ayarlar ve profil ekranları
  - [x] 22.1 SettingsScreen'i implement et
    - Profil bölümü, RSS yönetimi
    - Bildirim ayarları (kaynak bazında)
    - Tema ayarları, HyperIsle toggle
    - Güncelleme aralığı (15/30/60 dakika)
    - Veri kullanımı, önbellek temizleme
    - Uygulama versiyonu ve hakkında
    - _Requirements: 18.1-18.7_

  - [x] 22.2 ProfileScreen'i implement et
    - Profil görüntüleme ve düzenleme
    - İstatistikler (yorum sayısı, tepki sayısı)
    - Hesap silme
    - _Requirements: 21.11-21.14_

  - [x] 22.3 RSS yönetim ekranını implement et
    - Kaynak listesi, ekleme/kaldırma
    - Bildirim tercihleri (kaynak bazında)
    - _Requirements: 1.1, 1.4, 1.6, 4.4_

- [x] 23. Auth flow ekranları
  - [x] 23.1 LoginScreen'i implement et
    - Google Sign-In butonu
    - Email/Password form
    - _Requirements: 21.1_

  - [x] 23.2 ProfileSetupScreen'i implement et
    - Kullanıcı adı input, benzersizlik kontrolü
    - Alternatif öneriler
    - Profil resmi seçici, avatar önizleme
    - _Requirements: 21.2-21.10_

- [x] 24. Admin paneli ekranları
  - [x] 24.1 AdminPanelScreen'i implement et
    - Bildirilen yorumlar tab'ı
    - İstatistikler tab'ı
    - Kullanıcı yönetimi tab'ı
    - _Requirements: 24.3-24.12_


- [x] 25. Onboarding flow
  - [x] 25.1 OnboardingFlow'u implement et
    - Hoş geldiniz ekranı
    - RSS besleme seçimi (önceden tanımlı kaynaklar)
    - Bildirim izni isteme
    - _Requirements: 19.1-19.9_

  - [x] 25.2 Varsayılan RSS kaynaklarını tanımla
    - NTV, Webtekno, ShiftDelete, Sözcü
    - _Requirements: RSS Besleyici Örnekleri_

- [x] 26. Navigasyon ve ana yapı
  - [x] 26.1 Bottom navigation'ı implement et
    - Haber Akışı, Favoriler, Arama, Ayarlar
    - _Requirements: UI Specifications_

  - [x] 26.2 Navigation graph'ı yapılandır
    - Splash → Onboarding → Main flow
    - Deep link desteği (bildirimlerden)
    - _Requirements: 4.3_

- [x] 27. Checkpoint - UI katmanı tamamlandı
  - Ensure all tests pass, ask the user if questions arise.

- [x] 28. Veri temizleme ve optimizasyon
  - [x] 28.1 Otomatik haber temizleme mekanizmasını implement et
    - 30 günden eski haberleri sil (ücretsiz)
    - Favorileri koruma
    - _Requirements: 17.1, 17.2_

  - [x] 28.2 Property test: Otomatik haber temizleme
    - **Property 15: Otomatik Haber Temizleme**
    - **Validates: Requirements 17.1, 17.2**

  - [x] 28.3 Önbellek temizleme özelliğini implement et
    - Görsel cache temizleme
    - Toplam veri kullanımı gösterimi
    - _Requirements: 17.3-17.6_


- [x] 29. Premium abonelik sistemi
  - [x] 29.1 SubscriptionManager'ı implement et (Google Play Billing v6+)
    - checkSubscriptionStatus, purchaseSubscription, restorePurchases
    - Aylık (₺29.99) ve Yıllık (₺249.99) planlar
    - 7 günlük ücretsiz deneme
    - _Requirements: 26.1-26.9_

  - [x] 29.2 Property test: Abonelik durumu doğruluğu
    - **Property 32: Abonelik Durumu Doğruluğu**
    - **Validates: Requirements 26.4, 26.5**

  - [x] 29.3 Property test: Deneme süresi kontrolü
    - **Property 38: Deneme Süresi Kontrolü**
    - **Validates: Requirements 26.3**

  - [x] 29.4 PremiumPaywall ekranını implement et
    - Plan seçimi, özellik listesi
    - Satın al ve geri yükle butonları
    - _Requirements: 26.6_

  - [x] 29.5 Property test: Premium özellik erişim kontrolü
    - **Property 33: Premium Özellik Erişim Kontrolü**
    - **Validates: Requirements 26.6, 27.4, 28.2, 29.5, 30.5, 31.5, 32.5, 33.5, 34.6**

- [x] 30. Reklam entegrasyonu
  - [x] 30.1 AdMob entegrasyonunu implement et
    - Native reklam (her 5 haberde bir)
    - Banner reklam (detay sayfası altı)
    - GDPR/KVKK onay dialogu
    - _Requirements: 27.1-27.6_

  - [x] 30.2 Property test: Reklam gösterim kuralları
    - **Property 34: Reklam Gösterim Kuralları**
    - **Validates: Requirements 27.2, 27.4**

  - [x] 30.3 Premium kullanıcılar için reklam gizleme
    - _Requirements: 27.4_


- [x] 31. Premium özellikler - RSS ve Arama
  - [x] 31.1 RSS kaynak limitini implement et
    - Ücretsiz: maksimum 5 kaynak
    - Premium: sınırsız
    - Kalan hak gösterimi
    - _Requirements: 28.1-28.4_

  - [x] 31.2 Property test: RSS kaynak limiti
    - **Property 35: RSS Kaynak Limiti**
    - **Validates: Requirements 28.1, 28.2**

  - [x] 31.3 Gelişmiş arama filtrelerini implement et (Premium)
    - Tarih aralığı filtresi
    - Kaynak bazlı arama
    - Sıralama seçenekleri
    - _Requirements: 29.1-29.5_

  - [x] 31.4 Özel bildirim anahtar kelimelerini implement et (Premium)
    - Maksimum 20 kelime
    - Ayrı bildirim kanalı
    - _Requirements: 30.1-30.5_

- [x] 32. Premium özellikler - Arşiv ve Okuma Listesi
  - [x] 32.1 Haber arşivi süresini implement et
    - Ücretsiz: 30 gün
    - Premium: 90 gün
    - Arşiv sekmesi
    - _Requirements: 31.1-31.5_

  - [x] 32.2 Property test: Haber arşiv süresi
    - **Property 36: Haber Arşiv Süresi**
    - **Validates: Requirements 31.1, 31.2**

  - [x] 32.3 ReadingListManager'ı implement et (Premium)
    - Firestore senkronizasyonu
    - Okundu/okunmadı işaretleme
    - _Requirements: 32.1-32.5_

  - [x] 32.4 Property test: Okuma listesi senkronizasyonu
    - **Property 37: Okuma Listesi Senkronizasyonu**
    - **Validates: Requirements 32.2, 32.3**


- [x] 33. Premium özellikler - Tema ve İstatistikler
  - [x] 33.1 Özel tema sistemini implement et (Premium)
    - 5 renk teması (HyperOS Blue, Midnight Purple, Forest Green, Sunset Orange, Rose Pink)
    - Font boyutu ayarları
    - Kompakt/rahat kart görünümü
    - _Requirements: 33.1-33.5_

  - [x] 33.2 StatisticsManager'ı implement et (Premium)
    - Okuma takibi, haftalık/aylık istatistikler
    - En çok okunan kaynaklar grafiği
    - Günlük ortalama okuma süresi
    - _Requirements: 34.1-34.6_

  - [x] 33.3 StatisticsScreen'i implement et
    - Grafik görselleştirmeleri
    - Kategori ve kaynak dağılımı
    - _Requirements: 34.2-34.5_

- [x] 34. Checkpoint - Premium özellikler tamamlandı
  - Ensure all tests pass, ask the user if questions arise.

- [x] 35. Hata yönetimi ve kullanıcı geri bildirimi
  - [x] 35.1 Hata yönetimi sistemini implement et
    - AppError sealed class
    - ErrorLogger interface
    - Retry policy (exponential backoff)
    - _Requirements: 16.1-16.6_

  - [x] 35.2 UI hata gösterimlerini implement et
    - Toast mesajları, progress indicator
    - Tekrar dene butonu
    - Anlaşılır hata mesajları
    - _Requirements: 16.1-16.6_

- [x] 36. Erişilebilirlik
  - [x] 36.1 Erişilebilirlik özelliklerini implement et
    - Content description tüm interaktif öğeler için
    - TalkBack uyumluluğu
    - Minimum 48dp dokunma hedefi
    - WCAG AA renk kontrastı
    - Sistem font ölçeklendirme
    - Klavye navigasyonu
    - _Requirements: 20.1-20.6_

