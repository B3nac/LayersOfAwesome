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

    private var mWalletPresenter: GenerateContract.Presenter? = null
    private var mWalletAddress: String? = null
    private var mPassword: EditText? = null
    private var mProgressBar: ProgressBar? = null
    private val secure = SecureSharedPrefs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_wallet)

        SecureSharedPrefs.setContext(this)

        //val toolbar = findViewById<Toolbar>(R.id.toolbar)
        //setSupportActionBar(toolbar)
        setupBouncyCastle()
        val mGenerateWalletButton = findViewById<View>(R.id.generate_wallet_button) as Button
        mPassword = findViewById<View>(R.id.password) as EditText
        mProgressBar = findViewById<View>(R.id.generate_wallet_progress) as ProgressBar
        mProgressBar!!.visibility = View.INVISIBLE

        mGenerateWalletButton.setOnClickListener { v: View? ->
            val permissionCheck = ContextCompat.checkSelfPermission(this@GenerateWalletActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
            when {
                permissionCheck != PackageManager.PERMISSION_GRANTED -> {
                    ActivityCompat.requestPermissions(
                            this@GenerateWalletActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            REQUEST_PERMISSION_WRITE_STORAGE)
                }
                mPassword!!.text.toString().isEmpty() -> {
                    Toast.makeText(this@GenerateWalletActivity, "Password field cannot be empty", Toast.LENGTH_SHORT).show()
                }
                isPasswordValid(mPassword!!.text.toString()) -> {
                    Toast.makeText(this@GenerateWalletActivity, "Password has to be at least 8 characters long", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    mProgressBar!!.visibility = View.VISIBLE

                    Thread {
                        mWalletPresenter = GeneratePresenter(this@GenerateWalletActivity,
                                mPassword!!.text.toString(), applicationContext)
                        (mWalletPresenter as GeneratePresenter).generateWallet(mPassword!!.text.toString())

                        runOnUiThread {
                            val intent = Intent(this@GenerateWalletActivity, ProfileActivity::class.java)
                            intent.putExtra("WalletAddress", mWalletAddress)
                            startActivity(intent)
                            mProgressBar!!.visibility = View.GONE
                        }
                    }.start()
                }
            }
        }
    }

    override fun setPresenter(presenter: GenerateContract.Presenter) {
        mWalletPresenter = presenter
    }

    override fun showGeneratedWallet(address: String) {
        mWalletAddress = address
        val editor = secure.getSecurePrefs().edit()
        editor.putString("address", address)
        editor.apply()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION_WRITE_STORAGE -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Granted.
            } else {
                //Denied.
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