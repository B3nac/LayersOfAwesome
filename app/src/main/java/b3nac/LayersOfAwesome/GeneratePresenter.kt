package b3nac.LayersOfAwesome

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import org.web3j.crypto.CipherException
import org.web3j.crypto.WalletUtils
import java.io.File
import java.io.IOException
import java.security.InvalidAlgorithmParameterException
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException


class GeneratePresenter(private val walletGenerationView: GenerateContract.View, private val walletPassword: String, context: Context) : GenerateContract.Presenter {

        private val c = context
        private var fileExists = false

     override fun generateWallet(password: String?) {

         val path = c.applicationContext.filesDir

        try {
            when {
                fileExists -> {
                    return
                }
            }

            Log.e("PATH!!!", path.toString())

            val fileName = WalletUtils.generateBip39Wallet(password, path)
            val mnemonic = fileName.mnemonic
            var mnemonicIncrement = 0

            Log.e("generateWalletFile:", fileName.toString())
            Log.e("generateWalletPhrase:", mnemonic.toString())

            val credentials = WalletUtils.loadBip39Credentials(
                    password,
                    mnemonic)
            walletGenerationView.showGeneratedWallet(credentials.address)

            SecureSharedPrefs.setContext()
            val secure = SecureSharedPrefs()
            val editor = secure.getSecurePrefs().edit()

            if (!secure.getSecurePrefs().contains("mnemonicIncrement")) {
                editor.putInt("mnemonicIncrement", mnemonicIncrement)
                editor.apply()
                Log.e("TAG", "mnemonicIncrement: " + mnemonicIncrement)

            } else {
                mnemonicIncrement += 1
                editor.putInt("mnemonicIncrement", mnemonicIncrement)
                editor.apply()
                Log.e("TAG", "mnemonicIncrement: " + mnemonicIncrement)
            }

            //Put boolean setting here if no file checks work and check for a created address true WOOT!
            editor.putString("wallet_private_key", credentials.ecKeyPair.privateKey.toString(16))
            editor.apply()
            editor.putString("wallet_mnemonic_$mnemonicIncrement", mnemonic.toString())
            editor.apply()
            //Need to save file increment in an array after deleting save array with new file increments wallet_mnemonic_$mnemonicIncrement

            Log.e("TAG", "MnemonicFilename: " + "wallet_mnemonic_$mnemonicIncrement")

        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchProviderException) {
            e.printStackTrace()
        } catch (e: InvalidAlgorithmParameterException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: CipherException) {
            e.printStackTrace()
        }
    }

    override fun start() {
        generateWallet(walletPassword)
    }

    private fun checkFiles(path: String) {
        File(path).walk().forEach {
            if (it.toString() != path) {
                Log.e("File exists!!!", it.toString())
                fileExists = true
                Handler(Looper.getMainLooper()).post {
                    run {
                        Toast.makeText(c.applicationContext, "File exists!!!", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}

private fun SecureSharedPrefs.Companion.setContext() {

}
