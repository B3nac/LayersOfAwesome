package b3nac.LayersOfAwesome

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security

class GenerateWalletActivity : AppCompatActivity(), GenerateContract.View {

    private var walletPresenter: GenerateContract.Presenter? = null
    private var walletAddress: String? = null
    private var passwordField: EditText? = null
    private var progressBar: ProgressBar? = null
    private val secure = SecureSharedPrefs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_wallet)

        SecureSharedPrefs.setContext(this)

        setupBouncyCastle()
        val mGenerateWalletButton = findViewById<View>(R.id.generate_wallet_button) as Button
        passwordField = findViewById<View>(R.id.password) as EditText
        progressBar = findViewById<View>(R.id.generate_wallet_progress) as ProgressBar
        progressBar!!.visibility = View.INVISIBLE

        mGenerateWalletButton.setOnClickListener {
            val permissionCheck = ContextCompat.checkSelfPermission(this@GenerateWalletActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
            when {
                permissionCheck != PackageManager.PERMISSION_GRANTED -> {
                    ActivityCompat.requestPermissions(
                            this@GenerateWalletActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            REQUEST_PERMISSION_WRITE_STORAGE)
                }
                passwordField!!.text.toString().isEmpty() -> {
                    Toast.makeText(this@GenerateWalletActivity, "Password field cannot be empty", Toast.LENGTH_SHORT).show()
                }
                isPasswordValid(passwordField!!.text.toString()) -> {
                    Toast.makeText(this@GenerateWalletActivity, "Password has to be at least 8 characters long", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    progressBar!!.visibility = View.VISIBLE

                    Thread {
                        walletPresenter = GeneratePresenter(this@GenerateWalletActivity,
                                passwordField!!.text.toString(), applicationContext)
                        (walletPresenter as GeneratePresenter).generateWallet(passwordField!!.text.toString())

                        runOnUiThread {
                            val intent = Intent(this@GenerateWalletActivity, ProfileActivity::class.java)
                            intent.putExtra("WalletAddress", walletAddress)
                            startActivity(intent)
                            progressBar!!.visibility = View.GONE
                        }
                    }.start()
                }
            }
        }
    }

    override fun setPresenter(presenter: GenerateContract.Presenter?) {
        walletPresenter = presenter
    }

    override fun showGeneratedWallet(walletPublicAddress: String?) {
        walletAddress = walletPublicAddress
        val editor = secure.getSecurePrefs().edit()
        editor.putString("address", walletAddress)
        editor.apply()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION_WRITE_STORAGE -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@GenerateWalletActivity, "Permission granted.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@GenerateWalletActivity, "Permission denied.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupBouncyCastle() {
        val provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME)
                ?:
                return
        if (provider.javaClass == BouncyCastleProvider::class.java) {
            return
        }
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
        Security.insertProviderAt(BouncyCastleProvider(), 1)
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length < 8
    }

    companion object {
        private const val REQUEST_PERMISSION_WRITE_STORAGE = 0
    }

}