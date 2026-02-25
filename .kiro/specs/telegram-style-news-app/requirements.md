# Requirements Document

## Giriş

Telegram-Style News App, kullanıcılara Telegram kanalları benzeri bir deneyimle anlık haber akışı sunan modern bir Android uygulamasıdır. Uygulama, ücretsiz RSS/XML beslemelerinden haber çekerek, arka planda düzenli kontroller yaparak ve yeni içerik algılandığında push bildirimleri göndererek kullanıcıları güncel tutar. Xiaomi HyperOS estetiğinde dark mode öncelikli bir tasarıma sahiptir ve Dynamic Island benzeri (HyperIsle) animasyonlu başlık gösterimi içerir.

## Sözlük

- **Uygulama**: Telegram-Style News App Android uygulaması
- **RSS_Besleyici**: RSS/XML formatında haber içeriği sağlayan harici kaynak
- **Haber_Öğesi**: Başlık, özet, görsel, yayın tarihi ve kaynak URL içeren tekil haber içeriği
- **Haber_Akışı**: Kronolojik sırayla gösterilen haber öğeleri listesi
- **Arka_Plan_Worker**: Belirli aralıklarla RSS besleyicilerini kontrol eden arka plan servisi
- **Yerel_Veritabanı**: Room Database kullanılarak cihazda saklanan haber verileri
- **Push_Bildirimi**: Yeni haber içeriği algılandığında kullanıcıya gönderilen sistem bildirimi
- **HyperIsle**: Dynamic Island benzeri, ekranın üst kısmında animasyonlu başlık gösteren UI bileşeni
- **Kaynak**: Haber sağlayan medya kuruluşu (NTV, Webtekno, ShiftDelete, Sözcü vb.)
- **Çevrimdışı_Mod**: İnternet bağlantısı olmadan yerel veritabanından içerik okuma durumu

## Gereksinimler

### Gereksinim 1: RSS Besleme Yönetimi

**Kullanıcı Hikayesi:** Bir kullanıcı olarak, farklı haber kaynaklarından RSS beslemeleri ekleyebilmek istiyorum, böylece ilgilendiğim kaynaklardan haber alabileyim.

#### Kabul Kriterleri


1. THE Uygulama SHALL RSS besleyici URL'lerini kabul edebilmelidir
2. WHEN bir RSS besleyici URL'si eklendiğinde, THE Uygulama SHALL URL'nin geçerliliğini doğrulamalıdır
3. IF bir RSS besleyici URL'si geçersizse, THEN THE Uygulama SHALL kullanıcıya hata mesajı göstermelidir
4. THE Uygulama SHALL en az 10 farklı RSS besleyiciyi desteklemelidir
5. THE Uygulama SHALL eklenen RSS besleyicilerini Yerel_Veritabanı'nda saklamalıdır
6. THE Uygulama SHALL kullanıcının RSS besleyicilerini kaldırmasına izin vermelidir

### Gereksinim 2: RSS İçerik Çekme ve Ayrıştırma

**Kullanıcı Hikayesi:** Bir kullanıcı olarak, RSS beslemelerinden haberlerin otomatik olarak çekilmesini istiyorum, böylece manuel güncelleme yapmama gerek kalmasın.

#### Kabul Kriterleri

1. WHEN Uygulama başlatıldığında, THE Uygulama SHALL tüm kayıtlı RSS besleyicilerden içerik çekmelidir
2. THE Uygulama SHALL RSS/XML formatını ayrıştırarak Haber_Öğesi nesnelerine dönüştürmelidir
3. THE Uygulama SHALL her Haber_Öğesi için başlık, özet, görsel URL, yayın tarihi ve kaynak URL bilgilerini çıkarmalıdır
4. IF bir RSS besleyici erişilemezse, THEN THE Uygulama SHALL diğer besleyicileri çekmeye devam etmelidir
5. THE Uygulama SHALL çekilen Haber_Öğesi verilerini Yerel_Veritabanı'na kaydetmelidir
6. THE Uygulama SHALL aynı Haber_Öğesi'nin tekrar eklenmesini önlemelidir
7. WHEN RSS içeriği ayrıştırıldığında, THE Uygulama SHALL eksik alanları (görsel, özet) boş değer olarak işlemelidir

### Gereksinim 3: Arka Plan Güncelleme Mekanizması

**Kullanıcı Hikayesi:** Bir kullanıcı olarak, uygulamayı açmadığım zamanlarda bile yeni haberlerin kontrol edilmesini istiyorum, böylece son dakika haberlerini kaçırmayayım.

#### Kabul Kriterleri

1. THE Arka_Plan_Worker SHALL 15 dakikada bir RSS besleyicilerini kontrol etmelidir
2. THE Arka_Plan_Worker SHALL cihaz pil tasarrufu modundayken çalışmaya devam etmelidir
3. WHEN yeni Haber_Öğesi algılandığında, THE Arka_Plan_Worker SHALL Haber_Öğesi'ni Yerel_Veritabanı'na eklemelidir
4. WHEN yeni Haber_Öğesi algılandığında, THE Arka_Plan_Worker SHALL Push_Bildirimi tetiklemelidir
5. THE Arka_Plan_Worker SHALL ağ bağlantısı olmadığında güncelleme denemesini ertelemelidir
6. THE Arka_Plan_Worker SHALL başarısız güncelleme denemelerini loglayarak hata takibi yapmalıdır

### Gereksinim 4: Push Bildirim Sistemi

**Kullanıcı Hikayesi:** Bir kullanıcı olarak, yeni haberler yayınlandığında bildirim almak istiyorum, böylece önemli gelişmelerden anında haberdar olabileyim.

#### Kabul Kriterleri

1. WHEN yeni bir Haber_Öğesi algılandığında, THE Uygulama SHALL Push_Bildirimi göndermelidir
2. THE Push_Bildirimi SHALL Haber_Öğesi'nin başlığını ve Kaynak adını içermelidir
3. WHEN kullanıcı Push_Bildirimi'ne tıkladığında, THE Uygulama SHALL ilgili Haber_Öğesi detay sayfasını açmalıdır
4. THE Uygulama SHALL kullanıcının bildirim tercihlerini kaynak bazında yönetmesine izin vermelidir
5. THE Uygulama SHALL bildirimleri tamamen devre dışı bırakma seçeneği sunmalıdır
6. THE Push_Bildirimi SHALL Android bildirim kanalları (notification channels) kullanmalıdır

### Gereksinim 5: Haber Akışı Görüntüleme

**Kullanıcı Hikayesi:** Bir kullanıcı olarak, Telegram kanalları gibi akıcı bir arayüzde haberleri görmek istiyorum, böylece rahatça içerik tüketebiliyim.

#### Kabul Kriterleri

1. THE Uygulama SHALL Haber_Akışı'nı en yeni haberlerin en üstte olduğu kronolojik sırada göstermelidir
2. THE Uygulama SHALL her Haber_Öğesi'ni kart yapısında, görsel, başlık, özet ve yayın tarihi ile göstermelidir
3. THE Uygulama SHALL görseli olmayan Haber_Öğesi'leri için placeholder görsel kullanmalıdır
4. THE Uygulama SHALL sonsuz kaydırma (infinite scroll) desteği sunmalıdır
5. WHEN kullanıcı Haber_Akışı'nı aşağı kaydırdığında, THE Uygulama SHALL ek Haber_Öğesi'lerini yüklemelidir
6. THE Uygulama SHALL Haber_Akışı'nı yenilemek için aşağı çekme (pull-to-refresh) hareketi desteklemelidir
7. WHEN kullanıcı bir Haber_Öğesi'ne tıkladığında, THE Uygulama SHALL haber detay sayfasını açmalıdır


### Gereksinim 6: Haber Detay Sayfası

**Kullanıcı Hikayesi:** Bir kullanıcı olarak, bir haberin tam içeriğini görmek istiyorum, böylece detaylı bilgi edinebiliyim.

#### Kabul Kriterleri

1. THE Uygulama SHALL Haber_Öğesi detay sayfasında başlık, tam görsel, özet, yayın tarihi ve Kaynak bilgilerini göstermelidir
2. THE Uygulama SHALL kullanıcının orijinal haber kaynağına gitmek için "Kaynağa Git" butonu sunmalıdır
3. WHEN kullanıcı "Kaynağa Git" butonuna tıkladığında, THE Uygulama SHALL kaynak URL'yi uygulama içi tarayıcıda veya harici tarayıcıda açmalıdır
4. THE Uygulama SHALL kullanıcının haberi paylaşmasına izin vermelidir
5. THE Uygulama SHALL kullanıcının haberi favorilere eklemesine izin vermelidir

### Gereksinim 7: Çevrimdışı Okuma Desteği

**Kullanıcı Hikayesi:** Bir kullanıcı olarak, internet bağlantım olmadığında daha önce yüklenmiş haberleri okuyabilmek istiyorum, böylece her zaman içeriğe erişebiliyim.

#### Kabul Kriterleri

1. THE Uygulama SHALL tüm çekilen Haber_Öğesi'lerini Yerel_Veritabanı'nda saklamalıdır
2. WHEN internet bağlantısı olmadığında, THE Uygulama SHALL Yerel_Veritabanı'ndan Haber_Akışı'nı göstermelidir
3. WHILE Çevrimdışı_Mod aktifken, THE Uygulama SHALL kullanıcıya çevrimdışı olduğunu belirten bir gösterge sunmalıdır
4. THE Uygulama SHALL görselleri önbellekte (cache) saklamalıdır
5. WHEN internet bağlantısı geri geldiğinde, THE Uygulama SHALL otomatik olarak yeni içerik çekmelidir
6. THE Uygulama SHALL en az son 500 Haber_Öğesi'ni yerel olarak saklamalıdır

### Gereksinim 8: HyperIsle Entegrasyonu

**Kullanıcı Hikayesi:** Bir kullanıcı olarak, son dakika haberlerinin ekranın üst kısmında animasyonlu olarak gösterilmesini istiyorum, böylece önemli haberleri hemen fark edebiliyim.

#### Kabul Kriterleri

1. WHEN yeni bir son dakika Haber_Öğesi algılandığında, THE HyperIsle SHALL ekranın üst kısmında animasyonlu olarak görünmelidir
2. THE HyperIsle SHALL Haber_Öğesi'nin başlığını kayan metin (marquee) olarak göstermelidir
3. THE HyperIsle SHALL 5 saniye boyunca görünür kalmalıdır
4. WHEN kullanıcı HyperIsle'a tıkladığında, THE Uygulama SHALL ilgili Haber_Öğesi detay sayfasını açmalıdır
5. THE HyperIsle SHALL kullanıcı başka bir ekrandayken de görünmelidir
6. THE Uygulama SHALL kullanıcının HyperIsle özelliğini devre dışı bırakmasına izin vermelidir

### Gereksinim 9: Material Design 3 UI Bileşenleri

**Kullanıcı Hikayesi:** Bir kullanıcı olarak, modern ve tutarlı bir arayüz deneyimi yaşamak istiyorum, böylece uygulamayı kullanırken görsel olarak keyif alabiliyim.

#### Kabul Kriterleri

1. THE Uygulama SHALL Material Design 3 standartlarına uygun bileşenler kullanmalıdır
2. THE Uygulama SHALL 16dp yuvarlak köşeli kart bileşenleri kullanmalıdır
3. THE Uygulama SHALL gölge efekti yerine ince stroke çizgiler kullanmalıdır
4. THE Uygulama SHALL sistem fontu olarak Inter veya Roboto kullanmalıdır
5. THE Uygulama SHALL Material Design 3 renk sistemini (color scheme) uygulamalıdır
6. THE Uygulama SHALL dokunma hedeflerinin en az 48dp boyutunda olmasını sağlamalıdır

### Gereksinim 10: Dark Mode ve Tema Yönetimi

**Kullanıcı Hikayesi:** Bir kullanıcı olarak, gözlerimi yormayan karanlık tema ile uygulama kullanmak istiyorum, böylece gece okuma deneyimim daha konforlu olsun.

#### Kabul Kriterleri

1. THE Uygulama SHALL varsayılan olarak dark mode teması kullanmalıdır
2. THE Uygulama SHALL Xiaomi HyperOS estetiğine uygun renk paleti kullanmalıdır
3. THE Uygulama SHALL kullanıcının light mode ve dark mode arasında geçiş yapmasına izin vermelidir
4. THE Uygulama SHALL sistem tema ayarını takip etme seçeneği sunmalıdır
5. THE Uygulama SHALL tema tercihini Yerel_Veritabanı'nda saklamalıdır
6. WHEN tema değiştirildiğinde, THE Uygulama SHALL tüm UI bileşenlerini yeni temaya göre güncellemelidir

### Gereksinim 11: Favori Haberler

**Kullanıcı Hikayesi:** Bir kullanıcı olarak, beğendiğim haberleri favorilere ekleyebilmek istiyorum, böylece daha sonra kolayca erişebiliyim.

#### Kabul Kriterleri

1. THE Uygulama SHALL kullanıcının Haber_Öğesi'ni favorilere eklemesine izin vermelidir
2. THE Uygulama SHALL favori Haber_Öğesi'lerini Yerel_Veritabanı'nda saklamalıdır
3. THE Uygulama SHALL ayrı bir "Favoriler" sekmesi sunmalıdır
4. WHEN kullanıcı "Favoriler" sekmesine gittiğinde, THE Uygulama SHALL tüm favori Haber_Öğesi'lerini göstermelidir
5. THE Uygulama SHALL kullanıcının Haber_Öğesi'ni favorilerden kaldırmasına izin vermelidir
6. THE Uygulama SHALL favori durumunu görsel olarak (ikon değişimi) belirtmelidir


### Gereksinim 12: Arama Fonksiyonu

**Kullanıcı Hikayesi:** Bir kullanıcı olarak, belirli konulardaki haberleri arayabilmek istiyorum, böylece ilgilendiğim içeriği hızlıca bulabiliyim.

#### Kabul Kriterleri

1. THE Uygulama SHALL arama çubuğu sunmalıdır
2. WHEN kullanıcı arama terimi girdiğinde, THE Uygulama SHALL Yerel_Veritabanı'nda başlık ve özet alanlarında arama yapmalıdır
3. THE Uygulama SHALL arama sonuçlarını Haber_Akışı formatında göstermelidir
4. THE Uygulama SHALL en az 3 karakter girildiğinde arama yapmalıdır
5. WHEN arama sonucu bulunamazsa, THE Uygulama SHALL "Sonuç bulunamadı" mesajı göstermelidir
6. THE Uygulama SHALL arama geçmişini saklamalıdır
7. THE Uygulama SHALL kullanıcının arama geçmişini temizlemesine izin vermelidir

### Gereksinim 13: Kaynak Filtreleme

**Kullanıcı Hikayesi:** Bir kullanıcı olarak, belirli haber kaynaklarını filtreleyebilmek istiyorum, böylece sadece ilgilendiğim kaynaklardan gelen haberleri görebiliyim.

#### Kabul Kriterleri

1. THE Uygulama SHALL tüm aktif Kaynak listesini göstermelidir
2. THE Uygulama SHALL kullanıcının görüntülemek istediği Kaynak'ları seçmesine izin vermelidir
3. WHEN kullanıcı Kaynak filtresi uyguladığında, THE Uygulama SHALL sadece seçili Kaynak'lardan gelen Haber_Öğesi'lerini göstermelidir
4. THE Uygulama SHALL filtre tercihlerini Yerel_Veritabanı'nda saklamalıdır
5. THE Uygulama SHALL kullanıcının tüm filtreleri kaldırmasına izin vermelidir
6. THE Uygulama SHALL aktif filtre sayısını görsel olarak belirtmelidir

### Gereksinim 14: Haber Paylaşımı

**Kullanıcı Hikayesi:** Bir kullanıcı olarak, ilginç bulduğum haberleri sosyal medyada veya mesajlaşma uygulamalarında paylaşabilmek istiyorum, böylece arkadaşlarımla içerik paylaşabiliyim.

#### Kabul Kriterleri

1. THE Uygulama SHALL her Haber_Öğesi için paylaşım butonu sunmalıdır
2. WHEN kullanıcı paylaşım butonuna tıkladığında, THE Uygulama SHALL Android paylaşım menüsünü açmalıdır
3. THE Uygulama SHALL paylaşım içeriğinde Haber_Öğesi başlığı ve kaynak URL'yi içermelidir
4. THE Uygulama SHALL kullanıcının paylaşım yöntemini seçmesine izin vermelidir
5. THE Uygulama SHALL paylaşım işlemini başarıyla tamamlamalıdır

### Gereksinim 15: Performans ve Optimizasyon

**Kullanıcı Hikayesi:** Bir kullanıcı olarak, uygulamanın hızlı ve akıcı çalışmasını istiyorum, böylece kesintisiz bir deneyim yaşayabiliyim.

#### Kabul Kriterleri

1. THE Uygulama SHALL başlangıçta 2 saniye içinde Haber_Akışı'nı göstermelidir
2. THE Uygulama SHALL görsel yükleme için lazy loading kullanmalıdır
3. THE Uygulama SHALL görsel önbellekleme (image caching) mekanizması kullanmalıdır
4. THE Uygulama SHALL liste kaydırma sırasında 60 FPS performans sağlamalıdır
5. THE Uygulama SHALL bellek kullanımını 200 MB altında tutmalıdır
6. THE Uygulama SHALL ağ isteklerini optimize ederek gereksiz veri kullanımını önlemelidir

### Gereksinim 16: Hata Yönetimi ve Kullanıcı Geri Bildirimi

**Kullanıcı Hikayesi:** Bir kullanıcı olarak, bir hata oluştuğunda ne olduğunu anlamak istiyorum, böylece durumu kavrayabiliyim ve gerekirse aksiyon alabilirim.

#### Kabul Kriterleri

1. IF ağ bağlantısı başarısız olursa, THEN THE Uygulama SHALL kullanıcıya anlaşılır hata mesajı göstermelidir
2. IF RSS besleyici erişilemezse, THEN THE Uygulama SHALL ilgili Kaynak için hata durumu belirtmelidir
3. IF veri ayrıştırma hatası oluşursa, THEN THE Uygulama SHALL hatayı loglayarak sessizce atlamalıdır
4. THE Uygulama SHALL yükleme durumlarını progress indicator ile göstermelidir
5. THE Uygulama SHALL başarılı işlemleri kısa toast mesajları ile onaylamalıdır
6. IF kritik bir hata oluşursa, THEN THE Uygulama SHALL kullanıcıya "Tekrar Dene" seçeneği sunmalıdır

### Gereksinim 17: Veri Saklama ve Temizleme

**Kullanıcı Hikayesi:** Bir kullanıcı olarak, uygulamanın cihazımda ne kadar yer kapladığını kontrol edebilmek istiyorum, böylece depolama alanımı yönetebiliyim.

#### Kabul Kriterleri

1. THE Uygulama SHALL 30 günden eski Haber_Öğesi'lerini otomatik olarak silmelidir
2. THE Uygulama SHALL favori Haber_Öğesi'lerini otomatik silme işleminden muaf tutmalıdır
3. THE Uygulama SHALL kullanıcıya önbellek temizleme seçeneği sunmalıdır
4. WHEN kullanıcı önbellek temizlediğinde, THE Uygulama SHALL tüm görsel önbelleğini silmelidir
5. THE Uygulama SHALL ayarlar sayfasında toplam veri kullanımını göstermelidir
6. THE Uygulama SHALL kullanıcının tüm yerel verileri silmesine izin vermelidir


### Gereksinim 18: Ayarlar ve Konfigürasyon

**Kullanıcı Hikayesi:** Bir kullanıcı olarak, uygulamayı kendi tercihlerime göre özelleştirebilmek istiyorum, böylece kişiselleştirilmiş bir deneyim yaşayabiliyim.

#### Kabul Kriterleri

1. THE Uygulama SHALL ayarlar sayfası sunmalıdır
2. THE Uygulama SHALL kullanıcının Arka_Plan_Worker güncelleme aralığını değiştirmesine izin vermelidir (15, 30, 60 dakika seçenekleri)
3. THE Uygulama SHALL kullanıcının bildirim tercihlerini kaynak bazında yönetmesine izin vermelidir
4. THE Uygulama SHALL kullanıcının tema tercihini değiştirmesine izin vermelidir
5. THE Uygulama SHALL kullanıcının HyperIsle özelliğini açıp kapatmasına izin vermelidir
6. THE Uygulama SHALL tüm ayarları Yerel_Veritabanı'nda saklamalıdır
7. THE Uygulama SHALL uygulama versiyonu ve hakkında bilgilerini göstermelidir

### Gereksinim 19: İlk Kurulum ve Onboarding

**Kullanıcı Hikayesi:** Bir kullanıcı olarak, uygulamayı ilk açtığımda nasıl kullanacağımı öğrenmek istiyorum, böylece hızlıca başlayabiliyim.

#### Kabul Kriterleri

1. WHEN Uygulama ilk kez açıldığında, THE Uygulama SHALL hoş geldiniz ekranı göstermelidir
2. THE Uygulama SHALL kullanıcıya önceden tanımlı popüler RSS besleyicileri önermelidir
3. THE Uygulama SHALL kullanıcının en az bir RSS besleyici seçmesini istemelidir
4. THE Uygulama SHALL bildirim izni istemelidir
5. WHEN onboarding tamamlandığında, THE Uygulama SHALL ilk RSS çekme işlemini başlatmalıdır
6. THE Uygulama SHALL onboarding durumunu saklamalıdır
7. THE Uygulama SHALL kullanıcının giriş yapmadan haberleri görüntülemesine izin vermelidir
8. WHEN kullanıcı yorum veya tepki özelliğini kullanmak istediğinde, THEN THE Uygulama SHALL giriş ekranını göstermelidir
9. THE Uygulama SHALL kullanıcıya "Şimdi Giriş Yap" veya "Daha Sonra" seçenekleri sunmalıdır

### Gereksinim 20: Erişilebilirlik

**Kullanıcı Hikayesi:** Bir kullanıcı olarak, görme veya motor beceri kısıtlamalarım olsa bile uygulamayı kullanabilmek istiyorum, böylece herkes gibi haberlere erişebiliyim.

#### Kabul Kriterleri

1. THE Uygulama SHALL tüm interaktif öğeler için content description sağlamalıdır
2. THE Uygulama SHALL TalkBack ekran okuyucu ile uyumlu olmalıdır
3. THE Uygulama SHALL minimum 48dp dokunma hedefi boyutu kullanmalıdır
4. THE Uygulama SHALL yeterli renk kontrastı sağlamalıdır (WCAG AA standardı)
5. THE Uygulama SHALL metin boyutunu sistem ayarlarına göre ölçeklendirmelidir
6. THE Uygulama SHALL klavye navigasyonunu desteklemelidir

### Gereksinim 21: Kullanıcı Hesapları ve Profil Yönetimi

**Kullanıcı Hikayesi:** Bir kullanıcı olarak, uygulamaya giriş yaparak kimliğimi oluşturmak istiyorum, böylece yorum ve tepki özelliklerini kullanabileyim.

#### Kabul Kriterleri

1. THE Uygulama SHALL kullanıcıya 2 farklı giriş yöntemi sunmalıdır: Google hesabı, Email/Şifre
2. WHEN kullanıcı ilk kez giriş yaptığında, THE Uygulama SHALL profil oluşturma ekranını göstermelidir
3. THE Uygulama SHALL kullanıcıdan kullanıcı adı (nickname) istemeli ve zorunlu kılmalıdır
4. THE Uygulama SHALL kullanıcı adının 3-20 karakter arasında olmasını sağlamalıdır
5. THE Uygulama SHALL kullanıcı adının benzersiz (unique) olup olmadığını kontrol etmelidir
6. IF kullanıcı adı zaten kullanılıyorsa, THEN THE Uygulama SHALL alternatif öneriler sunmalıdır (örn: "ahmet123", "ahmet_2026")
7. THE Uygulama SHALL kullanıcıya opsiyonel profil resmi yükleme imkanı sunmalıdır
8. THE Uygulama SHALL profil resmi boyutunu maksimum 2 MB ile sınırlamalıdır
9. THE Uygulama SHALL profil resmini otomatik olarak 512x512 piksel boyutuna yeniden boyutlandırmalıdır
10. IF kullanıcı profil resmi yüklemediyse, THEN THE Uygulama SHALL kullanıcı adının baş harflerinden oluşan avatar göstermelidir
11. THE Uygulama SHALL kullanıcının profil bilgilerini (ad, resim) daha sonra düzenlemesine izin vermelidir
12. THE Uygulama SHALL kullanıcı profilinde toplam yorum sayısı, verilen tepki sayısı gibi istatistikleri göstermelidir
13. THE Uygulama SHALL kullanıcının hesabını silme seçeneği sunmalıdır
14. WHEN kullanıcı hesabını sildiğinde, THE Uygulama SHALL tüm yorumlarını ve tepkilerini de silmelidir
15. THE Uygulama SHALL kullanıcı adında sadece harf, rakam ve alt çizgi (_) karakterine izin vermelidir
16. THE Uygulama SHALL kullanıcı adını küçük harfe çevirerek kaydetmelidir (case-insensitive)

### Gereksinim 22: Haber Yorumlama ve Fikir Paylaşımı

**Kullanıcı Hikayesi:** Bir kullanıcı olarak, haberlerin altına yorum yazarak fikrimi belirtmek istiyorum, böylece diğer kullanıcılarla görüş alışverişinde bulunabiliyim.

#### Kabul Kriterleri

1. THE Uygulama SHALL her Haber_Öğesi için yorum bölümü sunmalıdır
2. WHEN kullanıcı yorum yazmak istediğinde ve giriş yapmamışsa, THEN THE Uygulama SHALL giriş ekranını göstermelidir
3. WHEN kullanıcı giriş yaptıktan sonra, THE Uygulama SHALL yorum yazma alanını aktif hale getirmelidir
4. THE Uygulama SHALL yorum yazma alanında kullanıcının profil resmini veya avatar'ını göstermelidir
5. THE Uygulama SHALL yorum yazma alanında placeholder metin göstermelidir: "Fikrinizi paylaşın..."
6. WHEN kullanıcı yorum yazdığında, THE Uygulama SHALL yorumu Firebase Firestore'a şu bilgilerle kaydetmelidir:
   - userId (Firebase UID)
   - userName (kullanıcı adı)
   - userPhotoUrl (profil resmi URL'si, varsa)
   - text (yorum metni)
   - timestamp (yorum zamanı)
   - newsId (hangi habere ait)
   - edited (düzenlenme durumu, varsayılan: false)
7. THE Uygulama SHALL yorumları en yeni yorumların en üstte olduğu kronolojik sırada göstermelidir
8. THE Uygulama SHALL her yorumda şu bilgileri göstermelidir:
   - Kullanıcı profil resmi veya avatar
   - Kullanıcı adı
   - Yorum metni
   - Yorum zamanı (örn: "2 saat önce", "dün", "3 gün önce")
   - Düzenleme durumu (düzenlendiyse "Düzenlendi" etiketi)
9. THE Uygulama SHALL kullanıcının sadece kendi yorumlarında "Düzenle" ve "Sil" butonları göstermelidir
10. WHEN kullanıcı yorumunu düzenlediğinde, THE Uygulama SHALL "edited" alanını true yapmalı ve düzenleme zamanını kaydetmelidir
11. WHEN kullanıcı yorumunu sildiğinde, THE Uygulama SHALL onay dialogu göstermelidir
12. THE Uygulama SHALL yorum uzunluğunu maksimum 500 karakter ile sınırlamalıdır
13. THE Uygulama SHALL yorum yazma alanında kalan karakter sayısını göstermelidir
14. THE Uygulama SHALL boş yorum gönderilmesini engellemeli ve uyarı göstermelidir
15. THE Uygulama SHALL spam ve uygunsuz içerik için Türkçe küfür/hakaret filtresi kullanmalıdır
16. THE Uygulama SHALL filtre listesinde en az 100 yaygın küfür ve hakaret kelimesi bulundurmalıdır
17. THE Uygulama SHALL filtreyi kelime varyasyonlarına karşı da kontrol etmelidir (örn: "k*fir", "kufur" vb.)
18. IF yorum uygunsuz kelime içeriyorsa, THEN THE Uygulama SHALL kullanıcıyı uyarmalı ve gönderimi engellemeli
19. THE Uygulama SHALL her yorumda "Bildir" (report) butonu sunmalıdır
20. WHEN kullanıcı bir yorumu bildirdiğinde, THE Uygulama SHALL bildirimi Firestore'a kaydetmeli ve teşekkür mesajı göstermelidir
21. THE Uygulama SHALL bildirilen yorumları admin panelinde görüntülenebilir hale getirmelidir
22. WHEN yeni yorum eklendiğinde, THE Uygulama SHALL yorum sayısını gerçek zamanlı olarak güncellemelidir
23. THE Uygulama SHALL haber kartında toplam yorum sayısını göstermelidir (örn: "💬 24 yorum")
24. THE Uygulama SHALL yorumları sayfalama (pagination) ile yükleyerek performansı optimize etmelidir (ilk 20 yorum, sonra "Daha fazla yükle")
25. THE Uygulama SHALL yorum yüklenirken loading indicator göstermelidir
26. WHEN internet bağlantısı yoksa, THE Uygulama SHALL kullanıcıya "Yorumlar yüklenemedi" mesajı göstermelidir

### Gereksinim 23: Haber Tepkileri (Reactions)

**Kullanıcı Hikayesi:** Bir kullanıcı olarak, haberlere emoji tepkisi vererek hızlıca fikrimi belirtmek istiyorum, böylece yorum yazmadan da etkileşimde bulunabiliyim.

#### Kabul Kriterleri

1. THE Uygulama SHALL her Haber_Öğesi için tepki (reaction) butonu sunmalıdır
2. THE Uygulama SHALL tepki butonunda toplam tepki sayısını göstermelidir (örn: "👍 142")
3. WHEN kullanıcı tepki butonuna tıkladığında ve giriş yapmamışsa, THEN THE Uygulama SHALL giriş ekranını göstermelidir
4. WHEN kullanıcı giriş yaptıktan sonra, THE Uygulama SHALL tepki seçim menüsünü bottom sheet olarak göstermelidir
5. THE Uygulama SHALL bottom sheet'te 6 farklı emoji tepkisi sunmalıdır:
   - 👍 Beğendim
   - ❤️ Sevdim
   - 😮 Şaşırdım
   - 😢 Üzüldüm
   - 😡 Kızdım
   - 🤔 Düşündürücü
6. THE Uygulama SHALL her tepki seçeneğinde o tepkiyi veren toplam kullanıcı sayısını göstermelidir
7. THE Uygulama SHALL kullanıcının daha önce verdiği tepkiyi vurgulamalıdır (farklı renk/border)
8. THE Uygulama SHALL kullanıcının bir habere sadece bir tepki vermesine izin vermelidir
9. WHEN kullanıcı yeni bir tepki seçtiğinde, THE Uygulama SHALL eski tepkiyi otomatik olarak kaldırmalı ve yeni tepkiyi kaydetmelidir
10. WHEN kullanıcı aynı tepkiye tekrar tıkladığında, THE Uygulama SHALL tepkiyi geri almalıdır (toggle davranışı)
11. WHEN kullanıcı tepki verdiğinde, THE Uygulama SHALL tepkiyi Firebase Firestore'a şu bilgilerle kaydetmelidir:
    - userId (Firebase UID)
    - userName (kullanıcı adı)
    - reactionType (like, love, wow, sad, angry, thinking)
    - timestamp (tepki zamanı)
    - newsId (hangi habere ait)
12. THE Uygulama SHALL haber kartında en popüler 3 tepkiyi emoji ve sayı ile göstermelidir (örn: "👍 45  ❤️ 23  😮 12")
13. THE Uygulama SHALL tepki sayılarını gerçek zamanlı olarak güncellemelidir
14. THE Uygulama SHALL tepki verme işleminde animasyon kullanmalıdır (emoji büyüme/küçülme efekti)
15. THE Uygulama SHALL kullanıcının bir tepkiye tıklayarak o tepkiyi veren kullanıcıları görmesine izin vermelidir
16. WHEN kullanıcı tepki listesini görüntülediğinde, THE Uygulama SHALL kullanıcı adlarını ve profil resimlerini liste halinde göstermelidir
17. THE Uygulama SHALL tepki verme işlemini offline modda da desteklemeli ve internet geldiğinde senkronize etmelidir
18. WHEN internet bağlantısı yoksa, THE Uygulama SHALL tepkiyi yerel olarak kaydetmeli ve "Senkronize edilecek" göstergesi göstermelidir

### Gereksinim 24: Admin Paneli ve Moderasyon

**Kullanıcı Hikayesi:** Bir admin olarak, bildirilen yorumları inceleyebilmek ve uygunsuz içerikleri yönetebilmek istiyorum, böylece topluluk kurallarını uygulayabiliyim.

#### Kabul Kriterleri

1. THE Uygulama SHALL admin rolü için özel bir Firestore koleksiyonu bulundurmalıdır (`/admins/{userId}`)
2. THE Uygulama SHALL admin kullanıcılarını email adresi ile tanımlamalıdır
3. WHEN admin kullanıcı giriş yaptığında, THE Uygulama SHALL ayarlar menüsünde "Admin Paneli" seçeneği göstermelidir
4. THE Uygulama SHALL admin panelinde bildirilen yorumları liste halinde göstermelidir
5. THE Uygulama SHALL her bildirimde şu bilgileri göstermelidir:
   - Bildiren kullanıcı adı
   - Bildirilen yorum metni
   - Yorumu yazan kullanıcı adı
   - Bildirim zamanı
   - Haber başlığı
   - Toplam bildirim sayısı (aynı yorum için)
6. THE Uygulama SHALL admin'e bildirilen yorumu silme yetkisi vermelidir
7. THE Uygulama SHALL admin'e bildirimi reddetme (yorum uygun) seçeneği sunmalıdır
8. WHEN admin bir yorumu sildiğinde, THE Uygulama SHALL yorumu Firestore'dan kalıcı olarak silmelidir
9. THE Uygulama SHALL admin'e kullanıcıları engelleme (ban) yetkisi vermelidir
10. WHEN bir kullanıcı engellendiğinde, THE Uygulama SHALL kullanıcının yorum ve tepki vermesini engellemeli
11. THE Uygulama SHALL admin panelinde toplam kullanıcı sayısı, toplam yorum sayısı, toplam tepki sayısı gibi istatistikleri göstermelidir
12. THE Uygulama SHALL admin panelinde en aktif kullanıcıları (en çok yorum/tepki veren) göstermelidir

### Gereksinim 25: Son Dakika Haber Tanımı ve HyperIsle Tetikleme

**Kullanıcı Hikayesi:** Bir kullanıcı olarak, gerçekten önemli ve acil haberlerin HyperIsle ile gösterilmesini istiyorum, böylece her haberde bildirim bombardımanına uğramayayım.

#### Kabul Kriterleri

1. THE Uygulama SHALL "son dakika" haberlerini aşağıdaki kriterlere göre belirlemeli:
   - Haber yayınlanma zamanı son 30 dakika içinde olmalı
   - Haber başlığında "son dakika", "breaking", "acil", "flaş" gibi anahtar kelimeler bulunmalı (case-insensitive)
   - VEYA haber RSS beslemesinde `<category>breaking</category>` etiketi bulunmalı
2. THE Uygulama SHALL son dakika haberlerini öncelikli olarak işlemeli ve HyperIsle'da göstermelidir
3. THE Uygulama SHALL aynı anda birden fazla son dakika haberi gelirse, en yeni olanı HyperIsle'da göstermelidir
4. THE Uygulama SHALL HyperIsle'ı sadece uygulama ön planda değilken tetiklemelidir
5. THE Uygulama SHALL HyperIsle'ı maksimum 15 dakikada bir tetiklemelidir (spam önleme)
6. THE Uygulama SHALL kullanıcının hangi kaynaklardan son dakika bildirimi almak istediğini seçmesine izin vermelidir
7. THE Uygulama SHALL ayarlar sayfasında "Son Dakika Anahtar Kelimeleri" özelleştirme seçeneği sunmalıdır
8. THE Uygulama SHALL kullanıcının kendi anahtar kelimelerini eklemesine izin vermelidir (örn: "deprem", "seçim", "ekonomi")
9. WHEN kullanıcı tanımlı anahtar kelime içeren haber geldiğinde, THE Uygulama SHALL HyperIsle'ı tetiklemelidir
10. THE Uygulama SHALL son dakika haberlerini haber akışında özel bir badge ile işaretlemelidir (🔴 SON DAKİKA)

---

## Ek Notlar

### RSS Besleyici Örnekleri

Uygulama aşağıdaki RSS besleyicilerini varsayılan olarak desteklemelidir:

- NTV: https://www.ntv.com.tr/gundem.rss
- Webtekno: https://www.webtekno.com/rss
- ShiftDelete: https://shiftdelete.net/feed
- Sözcü: https://www.sozcu.com.tr/feed/

### RSS Özet Çıkarma Stratejisi

RSS/XML beslemelerinden haber özeti çıkarılırken aşağıdaki öncelik sırası uygulanmalıdır:

1. **`<description>` veya `<summary>` etiketi**: Çoğu RSS beslemesi bu etiketlerde özet içerir (öncelikli kaynak)
2. **`<content:encoded>` etiketi**: Tam içerik varsa, ilk 200-300 karakteri özet olarak kullanılır
3. **HTML temizleme**: Özet içinde HTML etiketleri varsa (`<p>`, `<br>`, `<img>` vb.) temizlenmeli, sadece düz metin alınmalıdır
4. **Karakter sınırı**: Özet maksimum 300 karakter ile sınırlandırılmalı, fazlası "..." ile kesilmelidir
5. **Otomatik özet oluşturma (fallback)**: Eğer hiçbir özet alanı yoksa veya özet çok kısaysa, uygulama otomatik olarak 15-20 kelimelik özet oluşturmalıdır

**Otomatik Özet Oluşturma Algoritması (Hibrit Yaklaşım):**

Eğer RSS beslemesinde özet yoksa, uygulama akıllı bir hibrit strateji kullanmalıdır:

**Strateji: Bağlama Göre Yöntem Seçimi**

Uygulama, işlemin yapıldığı bağlama göre en uygun yöntemi seçmelidir:

1. **Arka Plan İşleme (Background Worker):**
   - TextRank algoritması kullan (yüksek kalite)
   - Maksimum 10 haber/batch işle
   - Toplam işlem süresi: ~1-2 saniye
   - Kullanıcı beklemediği için performans sorunu yok

2. **Anlık İşleme (Kullanıcı Arayüzünde):**
   - Hızlı extractive yöntem kullan (düşük gecikme)
   - İlk cümleden 15-20 kelime al
   - İşlem süresi: ~5-10ms
   - Anında sonuç göster

**Yöntem 1: TextRank Algoritması (Arka Plan İçin)**
- Açık kaynak TextRank implementasyonu kullan
- Haber metnindeki cümleleri skorla (benzerlik matrisi)
- En önemli cümleyi seç ve 15-20 kelimeye kısalt
- Tamamen offline, API gerektirmez, %100 ücretsiz
- Performans: ~100ms/haber, 10 haber için ~1 saniye

**Yöntem 2: Extractive Summarization (Anlık İşlem İçin)**
- Haber içeriğinin ilk cümlesini al
- HTML etiketlerini temizle
- 15-20 kelimeye kısalt
- Performans: ~5ms/haber, anında sonuç

**Performans Garantileri:**
- Maksimum batch boyutu: 10 haber
- Arka plan işleme süresi: ≤ 2 saniye
- UI thread bloklanmaz (Coroutines kullan)
- Paralel işleme desteği (Kotlin Flow)

**Önerilen Implementasyon:**
```kotlin
// Akıllı özet oluşturma - bağlama göre yöntem seçimi
suspend fun generateSummary(
    content: String, 
    isBackgroundTask: Boolean,
    wordLimit: Int = 20
): String {
    return if (isBackgroundTask) {
        // Arka planda = TextRank (kaliteli özet)
        withContext(Dispatchers.Default) {
            textRankSummary(content, wordLimit)
        }
    } else {
        // Kullanıcı beklerken = hızlı yöntem
        quickExtractSummary(content, wordLimit)
    }
}

// Hızlı extractive yöntem
fun quickExtractSummary(content: String, wordLimit: Int): String {
    val cleanContent = content.replace(Regex("<[^>]*>"), "") // HTML temizle
    val sentences = cleanContent.split(". ", "! ", "? ")
    val firstSentence = sentences.firstOrNull() ?: return ""
    val words = firstSentence.split(" ").take(wordLimit)
    return words.joinToString(" ") + "..."
}

// TextRank implementasyonu (arka plan için)
fun textRankSummary(content: String, wordLimit: Int): String {
    // TextRank algoritması implementasyonu
    // Cümleleri skorla, en önemlisini seç
    // 15-20 kelimeye kısalt
}
```

**Örnek RSS XML Yapısı:**
```xml
<item>
  <title>Haber Başlığı</title>
  <description>Bu haberin özeti burada yer alır...</description>
  <link>https://example.com/haber</link>
  <pubDate>Mon, 25 Feb 2026 10:00:00 GMT</pubDate>
  <enclosure url="https://example.com/image.jpg" type="image/jpeg"/>
</item>
```

Uygulama, RSS parser kütüphanesi kullanarak bu alanları otomatik olarak çıkarmalı ve Room Database'e kaydetmelidir. Özet yoksa otomatik özet oluşturma algoritması devreye girmelidir.

### Teknik Kısıtlamalar

- Minimum Android SDK: 26 (Android 8.0)
- Hedef Android SDK: 34 (Android 14)
- Kotlin versiyonu: 1.9+
- Jetpack Compose versiyonu: 1.5+

### Backend ve Sosyal Özellikler

**Firebase Entegrasyonu (Ücretsiz Tier):**

Yorum ve tepki özellikleri için Firebase kullanılmalıdır:

- **Firebase Authentication**: Kullanıcı girişi (Google, Email/Password, Anonim)
- **Firebase Firestore**: Yorum ve tepki veritabanı
- **Firebase Cloud Messaging**: Yorum bildirimleri (opsiyonel)

**Firestore Veri Yapısı:**

```
/admins/{userId}/
  - email: string
  - role: string ("admin")
  - createdAt: timestamp

/users/{userId}/
  - userName: string (unique, lowercase)
  - email: string
  - photoUrl: string (optional, max 2MB, resized to 512x512)
  - createdAt: timestamp
  - totalComments: number
  - totalReactions: number
  - isBanned: boolean (default: false)
  - bannedAt: timestamp (optional)
  - bannedBy: string (admin userId, optional)

/news_items/{newsId}/
  - title: string
  - summary: string
  - imageUrl: string
  - publishedDate: timestamp
  - sourceUrl: string
  - sourceName: string
  - isBreakingNews: boolean (son dakika mı?)
  - breakingKeywords: array (eşleşen anahtar kelimeler)
  - comments/
    - {commentId}/
      - userId: string
      - userName: string
      - userPhotoUrl: string (optional)
      - text: string
      - timestamp: timestamp
      - edited: boolean
      - editedAt: timestamp (optional)
      - reported: boolean
      - reportCount: number
  - reactions/
    - {userId}/
      - userName: string
      - reactionType: string (like, love, wow, sad, angry, thinking)
      - timestamp: timestamp
  - reactionCounts/
    - like: number
    - love: number
    - wow: number
    - sad: number
    - angry: number
    - thinking: number
    - total: number

/usernames/{userName}/
  - userId: string (kullanıcı adı benzersizliği için)

/reports/{reportId}/
  - reportedBy: string (userId)
  - reportedByUserName: string
  - commentId: string
  - commentText: string
  - commentAuthorId: string
  - commentAuthorName: string
  - newsId: string
  - newsTitle: string
  - reason: string (optional)
  - timestamp: timestamp
  - status: string ("pending", "resolved", "rejected")
  - resolvedBy: string (admin userId, optional)
  - resolvedAt: timestamp (optional)

/app_settings/
  - breaking_news_keywords: array (["son dakika", "breaking", "acil", "flaş", "deprem", "seçim"])
  - profanity_filter: array (Türkçe küfür/hakaret kelimeleri listesi, 100+ kelime)
```

**Kullanıcı Kimlik Akışı:**

1. **İlk Giriş:**
   - Kullanıcı Google veya Email/Şifre ile giriş yapar
   - Firebase Authentication UID oluşturulur
   - Profil oluşturma ekranı gösterilir
   - Kullanıcı adı seçilir (benzersizlik kontrolü)
   - Profil resmi yüklenir (opsiyonel, max 2MB, 512x512'ye resize edilir)
   - `/users/{userId}` ve `/usernames/{userName}` oluşturulur

2. **Yorum Yapma:**
   - Kullanıcı giriş yapmış mı kontrol edilir
   - Kullanıcı engellenmiş mi kontrol edilir (isBanned)
   - Giriş yoksa → giriş ekranı
   - Engellenmiş ise → "Hesabınız engellenmiştir" mesajı
   - Giriş varsa ve engel yoksa → yorum yazma alanı aktif
   - Yorum küfür filtresinden geçirilir
   - Yorum gönderildiğinde userId, userName, photoUrl ile kaydedilir

3. **Tepki Verme:**
   - Kullanıcı giriş yapmış mı kontrol edilir
   - Kullanıcı engellenmiş mi kontrol edilir
   - Giriş yoksa → giriş ekranı
   - Engellenmiş ise → "Hesabınız engellenmiştir" mesajı
   - Giriş varsa ve engel yoksa → tepki bottom sheet açılır
   - Tepki userId ve userName ile kaydedilir

4. **Admin İşlemleri:**
   - Admin email adresi `/admins/{userId}` koleksiyonunda tanımlı olmalı
   - Admin giriş yaptığında "Admin Paneli" menüsü görünür
   - Admin bildirilen yorumları inceleyebilir
   - Admin yorumları silebilir veya bildirimi reddedebilir
   - Admin kullanıcıları engelleyebilir (isBanned = true)

5. **Son Dakika Haberleri:**
   - RSS'ten haber çekildiğinde başlık ve kategori kontrol edilir
   - "son dakika", "breaking", "acil", "flaş" anahtar kelimeleri aranır
   - Kullanıcı tanımlı anahtar kelimeler de kontrol edilir
   - Eşleşme varsa `isBreakingNews = true` işaretlenir
   - HyperIsle tetikleme kuralları uygulanır (30 dakika içinde, 15 dk spam önleme)
   - Haber akışında 🔴 SON DAKİKA badge'i gösterilir

**Kullanıcı Adı Örnekleri:**
- Google girişi: "Ahmet Yılmaz" → önerilen: "ahmetyilmaz", "ahmet_yilmaz", "ahmet2026"
- Email girişi: "user@example.com" → önerilen: "user", "user123", "user_2026"

**Kullanıcı Adı Kuralları:**
- Sadece harf, rakam ve alt çizgi (_)
- 3-20 karakter arası
- Küçük harfe çevrilir (case-insensitive)
- Benzersiz olmalı

**Admin Tanımlama:**
- İlk admin: Uygulamayı geliştiren kişinin email adresi
- Yeni admin ekleme: Mevcut admin Firestore'da `/admins/` koleksiyonuna yeni kayıt ekler
- Admin email adresi Firebase Authentication'da kayıtlı olmalı
- Admin rolü: "admin" string değeri

**Küfür/Hakaret Filtresi:**
- Türkçe yaygın küfür ve hakaret kelimeleri (100+ kelime)
- Kelime varyasyonları (örn: "k*fir", "kufur", "k.ü.f.ü.r")
- Filtre listesi `/app_settings/profanity_filter` array'inde saklanır
- Admin panelinden güncellenebilir
- Kaynak: Açık kaynak Türkçe küfür listeleri (GitHub: ooguz/turkce-kufur-karaliste benzeri)

**Son Dakika Anahtar Kelimeleri:**
- Varsayılan: ["son dakika", "breaking", "acil", "flaş", "deprem", "seçim", "patlama", "kaza", "yangın"]
- Kullanıcı özelleştirilebilir (ayarlar sayfasından)
- `/app_settings/breaking_news_keywords` array'inde saklanır
- Haber başlığında case-insensitive arama yapılır

**Ücretsiz Tier Limitleri:**
- Firestore: 50,000 okuma/gün, 20,000 yazma/gün
- Authentication: Sınırsız kullanıcı
- Storage: 1 GB (profil resimleri için)

**Maliyet Optimizasyonu:**
- Yorumları sayfalama ile yükle (10-20 yorum/sayfa)
- Tepki sayılarını cache'le (5 dakika)
- Offline persistence kullan (Firestore cache)
- Gereksiz okuma/yazma işlemlerini minimize et

### Gereksinim 26: Premium Abonelik Sistemi

**Kullanıcı Hikayesi:** Bir kullanıcı olarak, premium özelliklere erişmek için aylık veya yıllık abonelik satın almak istiyorum, böylece gelişmiş özelliklerden faydalanabileyim.

#### Kabul Kriterleri

1. THE Uygulama SHALL Google Play Billing Library v6+ kullanarak uygulama içi satın alma desteklemelidir
2. THE Uygulama SHALL 2 farklı abonelik planı sunmalıdır:
   - Aylık Premium: ₺29.99/ay
   - Yıllık Premium: ₺249.99/yıl (%30 indirimli)
3. THE Uygulama SHALL 7 günlük ücretsiz deneme süresi sunmalıdır (sadece ilk kez abone olanlara)
4. THE Uygulama SHALL abonelik durumunu Firebase Firestore'da `/users/{userId}/subscription` alanında saklamalıdır
5. THE Uygulama SHALL abonelik durumunu Google Play ile senkronize etmelidir (BillingClient.queryPurchasesAsync)
6. WHEN kullanıcı premium özelliğe erişmek istediğinde ve abone değilse, THEN THE Uygulama SHALL premium paywall ekranını göstermelidir
7. THE Uygulama SHALL abonelik iptal edildiğinde mevcut dönem sonuna kadar premium erişimi sürdürmelidir
8. THE Uygulama SHALL abonelik yenileme hatalarını kullanıcıya bildirmelidir
9. THE Uygulama SHALL "Aboneliği Geri Yükle" butonu sunmalıdır (cihaz değişikliği için)

### Gereksinim 27: Reklamsız Deneyim (Premium)

**Kullanıcı Hikayesi:** Bir premium kullanıcı olarak, reklam görmeden haber okumak istiyorum, böylece kesintisiz bir deneyim yaşayabileyim.

#### Kabul Kriterleri

1. THE Uygulama SHALL ücretsiz kullanıcılara Google AdMob reklamları göstermelidir
2. THE Uygulama SHALL haber akışında her 5 haberde bir native reklam göstermelidir
3. THE Uygulama SHALL haber detay sayfasının altında banner reklam göstermelidir
4. WHEN kullanıcı premium aboneyse, THEN THE Uygulama SHALL tüm reklamları gizlemelidir
5. THE Uygulama SHALL reklam yükleme hatalarını sessizce işlemelidir (kullanıcıya göstermeden)
6. THE Uygulama SHALL GDPR/KVKK uyumlu reklam onayı istemelidir (ilk açılışta)

### Gereksinim 28: Sınırsız RSS Kaynağı (Premium)

**Kullanıcı Hikayesi:** Bir premium kullanıcı olarak, istediğim kadar RSS kaynağı eklemek istiyorum, böylece tüm ilgilendiğim kaynaklardan haber alabileyim.

#### Kabul Kriterleri

1. THE Uygulama SHALL ücretsiz kullanıcıları maksimum 5 RSS kaynağı ile sınırlamalıdır
2. WHEN ücretsiz kullanıcı 6. kaynağı eklemeye çalıştığında, THEN THE Uygulama SHALL premium paywall göstermelidir
3. THE Uygulama SHALL premium kullanıcılara sınırsız RSS kaynağı ekleme izni vermelidir
4. THE Uygulama SHALL ücretsiz kullanıcılara kalan kaynak hakkını göstermelidir (örn: "2/5 kaynak kullanıldı")

### Gereksinim 29: Gelişmiş Arama ve Filtreleme (Premium)

**Kullanıcı Hikayesi:** Bir premium kullanıcı olarak, tarih aralığına ve kategoriye göre arama yapmak istiyorum, böylece aradığım haberleri daha kolay bulabileyim.

#### Kabul Kriterleri

1. THE Uygulama SHALL ücretsiz kullanıcılara sadece basit metin araması sunmalıdır
2. THE Uygulama SHALL premium kullanıcılara tarih aralığı filtresi sunmalıdır (bugün, bu hafta, bu ay, özel aralık)
3. THE Uygulama SHALL premium kullanıcılara kaynak bazlı arama filtresi sunmalıdır
4. THE Uygulama SHALL premium kullanıcılara arama sonuçlarını sıralama seçenekleri sunmalıdır (tarih, popülerlik)
5. WHEN ücretsiz kullanıcı gelişmiş filtrelere tıkladığında, THEN THE Uygulama SHALL premium paywall göstermelidir

### Gereksinim 30: Özel Bildirim Anahtar Kelimeleri (Premium)

**Kullanıcı Hikayesi:** Bir premium kullanıcı olarak, kendi belirlediğim anahtar kelimeler için özel bildirim almak istiyorum, böylece ilgilendiğim konulardan anında haberdar olabileyim.

#### Kabul Kriterleri

1. THE Uygulama SHALL ücretsiz kullanıcılara sadece varsayılan son dakika anahtar kelimelerini sunmalıdır
2. THE Uygulama SHALL premium kullanıcılara özel anahtar kelime ekleme izni vermelidir (maksimum 20 kelime)
3. WHEN premium kullanıcının anahtar kelimesi içeren haber geldiğinde, THEN THE Uygulama SHALL özel bildirim göndermelidir
4. THE Uygulama SHALL özel anahtar kelime bildirimlerini ayrı bir bildirim kanalında göstermelidir
5. WHEN ücretsiz kullanıcı özel anahtar kelime eklemeye çalıştığında, THEN THE Uygulama SHALL premium paywall göstermelidir

### Gereksinim 31: Haber Arşivi ve Sınırsız Geçmiş (Premium)

**Kullanıcı Hikayesi:** Bir premium kullanıcı olarak, 30 günden eski haberlere de erişmek istiyorum, böylece geçmiş haberleri araştırabileyim.

#### Kabul Kriterleri

1. THE Uygulama SHALL ücretsiz kullanıcılar için 30 günden eski haberleri otomatik silmelidir
2. THE Uygulama SHALL premium kullanıcılar için haberleri 90 gün boyunca saklamalıdır
3. THE Uygulama SHALL premium kullanıcılara "Arşiv" sekmesi sunmalıdır
4. THE Uygulama SHALL arşivdeki haberleri tarih ve kaynağa göre filtreleme imkanı sunmalıdır
5. WHEN ücretsiz kullanıcı arşive erişmeye çalıştığında, THEN THE Uygulama SHALL premium paywall göstermelidir

### Gereksinim 32: Okuma Listesi ve Senkronizasyon (Premium)

**Kullanıcı Hikayesi:** Bir premium kullanıcı olarak, haberleri "sonra oku" listesine eklemek ve farklı cihazlarda senkronize etmek istiyorum.

#### Kabul Kriterleri

1. THE Uygulama SHALL premium kullanıcılara "Okuma Listesi" özelliği sunmalıdır
2. THE Uygulama SHALL okuma listesini Firebase Firestore'da `/users/{userId}/reading_list` koleksiyonunda saklamalıdır
3. THE Uygulama SHALL okuma listesini farklı cihazlar arasında gerçek zamanlı senkronize etmelidir
4. THE Uygulama SHALL okuma listesindeki haberleri okundu/okunmadı olarak işaretleme imkanı sunmalıdır
5. WHEN ücretsiz kullanıcı okuma listesine eklemeye çalıştığında, THEN THE Uygulama SHALL premium paywall göstermelidir

### Gereksinim 33: Özel Tema ve Görünüm (Premium)

**Kullanıcı Hikayesi:** Bir premium kullanıcı olarak, uygulamanın görünümünü özelleştirmek istiyorum, böylece kişiselleştirilmiş bir deneyim yaşayabileyim.

#### Kabul Kriterleri

1. THE Uygulama SHALL ücretsiz kullanıcılara sadece dark/light/system tema seçeneklerini sunmalıdır
2. THE Uygulama SHALL premium kullanıcılara 5 farklı renk teması sunmalıdır:
   - HyperOS Blue (varsayılan)
   - Midnight Purple
   - Forest Green
   - Sunset Orange
   - Rose Pink
3. THE Uygulama SHALL premium kullanıcılara font boyutu ayarlama seçeneği sunmalıdır (küçük, normal, büyük, çok büyük)
4. THE Uygulama SHALL premium kullanıcılara kompakt/rahat kart görünümü seçeneği sunmalıdır
5. WHEN ücretsiz kullanıcı özel tema seçmeye çalıştığında, THEN THE Uygulama SHALL premium paywall göstermelidir

### Gereksinim 34: İstatistikler ve Okuma Analizi (Premium)

**Kullanıcı Hikayesi:** Bir premium kullanıcı olarak, okuma alışkanlıklarımı görmek istiyorum, böylece hangi konulara ne kadar zaman harcadığımı anlayabileyim.

#### Kabul Kriterleri

1. THE Uygulama SHALL premium kullanıcılara "İstatistikler" sayfası sunmalıdır
2. THE Uygulama SHALL haftalık/aylık okunan haber sayısını göstermelidir
3. THE Uygulama SHALL en çok okunan kaynakları grafik olarak göstermelidir
4. THE Uygulama SHALL en çok ilgilenilen konuları (kategorileri) göstermelidir
5. THE Uygulama SHALL günlük ortalama okuma süresini göstermelidir
6. WHEN ücretsiz kullanıcı istatistiklere erişmeye çalıştığında, THEN THE Uygulama SHALL premium paywall göstermelidir

---

## Premium Özellik Özeti

| Özellik | Ücretsiz | Premium |
|---------|----------|---------|
| Reklam | Var | Yok |
| RSS Kaynağı | Maks 5 | Sınırsız |
| Arama | Basit metin | Gelişmiş filtreler |
| Özel Anahtar Kelime | Yok | 20 kelimeye kadar |
| Haber Arşivi | 30 gün | 90 gün |
| Okuma Listesi | Yok | Var (senkronize) |
| Tema Özelleştirme | 3 tema | 5 renk + font ayarı |
| İstatistikler | Yok | Var |

---

### Güvenlik Gereksinimleri

- Tüm ağ istekleri HTTPS üzerinden yapılmalıdır
- Kullanıcı verileri şifrelenmeli olarak saklanmalıdır
- Uygulama ProGuard/R8 ile obfuscate edilmelidir
- Premium abonelik doğrulaması sunucu tarafında yapılmalıdır (Google Play Developer API)

### Performans Hedefleri

- Uygulama başlangıç süresi: < 2 saniye
- RSS çekme süresi: < 5 saniye (kaynak başına)
- Liste kaydırma FPS: 60 FPS
- Bellek kullanımı: < 200 MB
- APK boyutu: < 20 MB (reklam SDK dahil)

