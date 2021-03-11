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


class GeneratePresenter(private val mWalletGenerationView: GenerateContract.View, private val mPassword: String, context: Context) : GenerateContract.Presenter {

        private val c = context
        private var fileExists = false

     override fun generateWallet(password: String) {

         val path = c.applicationContext.filesDir

        try {

            //checkFiles(path.toString())
            when {
                fileExists -> {
                    return
                }
            }

            Log.e("PATH!!!", path.toString())

            val fileName = WalletUtils.generateBip39Wallet(password, path)
            val mnemonic = fileName.mnemonic

            Log.e("generateWalletFile:", fileName.toString())
            Log.e("generateWalletPhrase:", mnemonic.toString())

            val credentials = WalletUtils.loadBip39Credentials(
                    password,
                    mnemonic)
            mWalletGenerationView.showGeneratedWallet(credentials.address)

            SecureSharedPrefs.setContext(this)
            val secure = SecureSharedPrefs()
            val editor = secure.getSecurePrefs().edit()
            //Put boolean setting here if no file checks work and check for a created address true WOOT!
            editor.putString("wallet_private_key", credentials.ecKeyPair.privateKey.toString(16))
            editor.apply()
            editor.putString("wallet_mnemonic", mnemonic.toString())
            editor.apply()

            Log.e("TAG", "WalletAddress: " + credentials.address)
            Log.e("TAG", "PublicKey: " + credentials.ecKeyPair.publicKey)
            Log.e("TAG", "PrivateKey: " + credentials.ecKeyPair.privateKey)

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
        generateWallet(mPassword)
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

private fun SecureSharedPrefs.Companion.setContext(con: GeneratePresenter) {

}
