package b3nac.LayersOfAwesome

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.response.EthGetBalance
import org.web3j.protocol.http.HttpService
import org.web3j.utils.Convert
import java.util.concurrent.ExecutionException

class WalletActivity : AppCompatActivity() {
    private var mWalletAddress: TextView? = null
    private var mBalance: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)

        SecureSharedPrefs.setContext(this)
        val secure = SecureSharedPrefs()
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { view: View? ->
            Snackbar.make(view!!, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        val extras = intent.extras
        val web3 = Web3j.build(HttpService(""))
        val ethGetBalance: EthGetBalance
        try {
            ethGetBalance = web3.ethGetBalance("", DefaultBlockParameterName.LATEST).sendAsync().get()
            val wei = ethGetBalance.balance
            val tokenValue = Convert.fromWei(wei.toString(), Convert.Unit.ETHER)
            val strTokenAmount = tokenValue.toString()
            mBalance = findViewById(R.id.wallet_balance)
            mBalance!!.text = strTokenAmount
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }
        val walletAddress = extras!!.getString("WalletAddress")
        mWalletAddress = findViewById(R.id.ethTextView)
        val editor = secure.getSecurePrefs().edit()
        editor.putString("address", walletAddress)
        editor.apply()
        mWalletAddress!!.text = walletAddress
    }

    companion object {
        val TAG = WalletActivity::class.java.name
    }
}