package b3nac.LayersOfAwesome

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import org.web3j.crypto.WalletUtils


class ImportWalletActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_import_wallet)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    fun restoreWallet(view: View?) {

        val secure = SecureSharedPrefs()
        val seedPhraseField = findViewById<EditText>(R.id.editTextSeedPhrase)
        val seedPhrasePasswordField = findViewById<EditText>(R.id.editTextSeedPhrasePassword)
        val seedPhrase = seedPhraseField.text.toString()
        val seedPhrasePassword = seedPhrasePasswordField.text.toString()

        val path = applicationContext.filesDir

        //val mnemonic = "monkey scorpion blue bread divert avocado gadget concert notice bunker idea subway"
        //val password = null

        val fileName = WalletUtils.generateBip39WalletFromMnemonic(seedPhrasePassword, seedPhrase, path)
        val mnemonic = fileName.mnemonic

        Log.e("generateWalletFile:", fileName.toString())

        val credentials = WalletUtils.loadBip39Credentials(seedPhrasePassword, seedPhrase)

        //Store values to SecureSharedPreferences on import

        val editor = secure.getSecurePrefs().edit()
        editor.putString("address", credentials.address)
        editor.apply()
        editor.putString("wallet_private_key", credentials.ecKeyPair.privateKey.toString(16))
        editor.apply()
        editor.putString("wallet_mnemonic", mnemonic.toString())
        editor.apply()

        Log.e("TAG", "WalletAddress: " + credentials.address)
        Log.e("TAG", "PublicKey: " + credentials.ecKeyPair.publicKey)
        Log.e("TAG", "PrivateKey: " + credentials.ecKeyPair.privateKey)
        Log.e("TAG", "Seed Phrase: $mnemonic")
    }
}