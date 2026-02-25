package com.example.telegramstyle.newsapp.domain.common

sealed class AppError(
    val code: String,
    override val message: String,
    override val cause: Throwable? = null
) : Throwable(message, cause) {
    class NetworkError(override val message: String = "Ağ hatası", cause: Throwable? = null) : 
        AppError("NET_001", message, cause)
    
    class ParseError(override val message: String = "Ayrıştırma hatası", cause: Throwable? = null) : 
        AppError("PARSE_001", message, cause)
    
    class AuthError(override val message: String = "Kimlik doğrulama hatası", cause: Throwable? = null) : 
        AppError("AUTH_001", message, cause)
    
    class DatabaseError(override val message: String = "Veritabanı hatası", cause: Throwable? = null) : 
        AppError("DB_001", message, cause)
    
    class ValidationError(override val message: String) : 
        AppError("VAL_001", message)
    
    class NotFoundError(override val message: String = "Bulunamadı", cause: Throwable? = null) : 
        AppError("NOT_FOUND_001", message, cause)
    
    class BillingError(override val message: String = "Ödeme hatası", cause: Throwable? = null) : 
        AppError("BILLING_001", message, cause)
    
    class UnknownError(override val message: String = "Bilinmeyen hata", cause: Throwable? = null) : 
        AppError("UNK_001", message, cause)
}
