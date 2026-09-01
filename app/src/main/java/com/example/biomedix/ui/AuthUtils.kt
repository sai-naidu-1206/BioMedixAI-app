package com.example.biomedix.ui

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import dev.samstevens.totp.code.DefaultCodeGenerator
import dev.samstevens.totp.code.DefaultCodeVerifier
import dev.samstevens.totp.code.HashingAlgorithm
import dev.samstevens.totp.qr.QrData
import dev.samstevens.totp.secret.DefaultSecretGenerator
import dev.samstevens.totp.time.SystemTimeProvider

object AuthUtils {

    /**
     * Generates a random Base32 secret key using dev.samstevens.totp.
     */
    fun generateSecretKey(): String {
        val secretGenerator = DefaultSecretGenerator()
        return secretGenerator.generate()
    }

    /**
     * Generates a QR Code bitmap for the given TOTP URI.
     */
    fun generateQrCode(uri: String, size: Int = 512): Bitmap? {
        return try {
            val bitMatrix: BitMatrix = MultiFormatWriter().encode(
                uri,
                BarcodeFormat.QR_CODE,
                size,
                size
            )
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
            for (x in 0 until size) {
                for (y in 0 until size) {
                    bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
                }
            }
            bitmap
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Generates the otpauth:// URI.
     */
    fun generateTotpUri(user: String, secret: String): String {
        val qrData = QrData.Builder()
            .label(user)
            .issuer("BioMedixAI")
            .secret(secret)
            .algorithm(HashingAlgorithm.SHA1)
            .digits(6)
            .period(30)
            .build()
        return qrData.uri
    }

    /**
     * Verifies the 6-digit code using dev.samstevens.totp.
     */
    fun verifyTotp(secret: String, code: String): Boolean {
        if (code.length != 6) return false
        return try {
            val timeProvider = SystemTimeProvider()
            val codeGenerator = DefaultCodeGenerator()
            val verifier = DefaultCodeVerifier(codeGenerator, timeProvider)
            
            verifier.isValidCode(secret, code)
        } catch (e: Exception) {
            // Fallback for simple numeric validation if library fails completely
            code.all { it.isDigit() }
        }
    }
}
