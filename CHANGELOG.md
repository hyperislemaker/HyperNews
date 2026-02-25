# Changelog

Tüm önemli değişiklikler bu dosyada belgelenecektir.

Format [Keep a Changelog](https://keepachangelog.com/en/1.0.0/) standardına uygundur.

## [0.0.9] - 2026-02-25

### Eklendi
- Erişilebilirlik özellikleri (TalkBack, font ölçekleme, 48dp touch target)
- Hata yönetimi UI bileşenleri (ErrorScreen, RetryButton, LoadingIndicator)
- AppError sealed class ile merkezi hata yönetimi

### Düzeltildi
- Test dosyalarındaki Result.error property düzeltmesi
- ProfileImagePropertyTest Android bağımlılıkları kaldırıldı

## [0.0.8] - 2026-02-25

### Eklendi
- Premium özellikler (tema, istatistikler)
- StatisticsManager ve StatisticsScreen
- 5 özel renk teması (HyperOS Blue, Midnight Purple, Forest Green, Sunset Orange, Rose Pink)
- Okuma takibi ve haftalık/aylık istatistikler

## [0.0.7] - 2026-02-25

### Eklendi
- Premium abonelik sistemi (SubscriptionManager)
- Google Play Billing v6 entegrasyonu
- PremiumPaywallScreen
- AdMob reklam entegrasyonu (Native, Banner)
- GDPR/KVKK onay dialogu
- RSS kaynak limiti (ücretsiz: 5, premium: sınırsız)
- ReadingListManager (Firestore senkronizasyonu)

## [0.0.6] - 2026-02-25

### Eklendi
- Veri temizleme mekanizması (DataCleanupManager)
- 30/90 gün haber arşivi (ücretsiz/premium)
- Önbellek temizleme özelliği
- Navigasyon sistemi (MainNavigation, Bottom Navigation)
- Onboarding flow (hoş geldiniz, RSS seçimi, bildirim izni)
- Varsayılan RSS kaynakları (NTV, Webtekno, ShiftDelete, Sözcü)

## [0.0.5] - 2026-02-25

### Eklendi
- Ayarlar ekranı (SettingsScreen)
- Profil ekranı (ProfileScreen)
- RSS yönetim ekranı (RssManagementScreen)
- Auth flow ekranları (LoginScreen, ProfileSetupScreen)
- Admin paneli ekranları (AdminPanelScreen)

## [0.0.4] - 2026-02-25

### Eklendi
- UI Tema sistemi (HyperOS Dark Theme)
- Tipografi sistemi (Inter/Roboto)
- NewsCard bileşeni
- HyperIsleOverlay bileşeni
- ReactionBottomSheet bileşeni
- CommentSection bileşeni
- Ana ekranlar (NewsFeedScreen, NewsDetailScreen, FavoritesScreen, SearchScreen)

## [0.0.3] - 2026-02-25

### Eklendi
- Son dakika haber sistemi (BreakingNewsDetector)
- HyperIsle overlay (5 saniye görünürlük, marquee animasyonu)
- Arka plan senkronizasyonu (NewsSyncWorker, 15 dakika periyot)
- Bildirim sistemi (NotificationManager, Android Channels)
- Çevrimdışı destek (ImageCacheManager, ConnectivityObserver)

## [0.0.2] - 2026-02-25

### Eklendi
- Firebase entegrasyonu (Auth, Firestore, Storage)
- Yorum sistemi (CommentManager)
- Küfür filtresi (ProfanityFilter)
- Tepki sistemi (ReactionManager)
- Admin paneli (AdminManager)
- Kullanıcı profil yönetimi (UserProfileManager)
- Profil resmi işleme (ProfileImageManager)

## [0.0.1] - 2026-02-25

### Eklendi
- Proje yapısı ve Clean Architecture
- Hilt dependency injection
- Room Database (NewsItemEntity, RssFeedEntity, SearchHistoryEntity)
- DAO'lar (NewsItemDao, RssFeedDao, SearchHistoryDao)
- Domain modelleri (NewsItem, RssFeed, UserProfile, Comment, Reaction)
- RSS Parser servisi (Rome RSS)
- Summary Generator (extractive summarization)
- RssFeedManager
- Property-based testler (Kotest)

---

## Versiyon Notları

- **0.0.x**: Alpha geliştirme sürümleri
- Stabil sürüm için 1.0.0'ı bekleyin
