package b3nac.LayersOfAwesome

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecureSharedPrefs : Application(){

    companion object {

        private lateinit var context: Context

        fun setContext(con: Context) {
            context = con
        }
    }

    private val preferencesName = "c0c00n.encrypted"

    var masterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()

    val securePreferences = EncryptedSharedPreferences.create(
            context,
            preferencesName,
            masterKey, // masterKey created above
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);

    fun saveUserData(context: Context, string: String, boolean: Boolean) {

        val editor = securePreferences.edit()
        editor.putBoolean(string, boolean).apply()
        editor.clear()
    }

    fun getSecurePrefs(): SharedPreferences {

        return securePreferences

    }
}
