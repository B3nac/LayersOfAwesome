package b3nac.LayersOfAwesome

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import org.web3j.crypto.WalletUtils
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.response.EthGetBalance
import org.web3j.protocol.http.HttpService
import org.web3j.utils.Convert
import java.io.File
import java.util.concurrent.ExecutionException


class ProfileActivity : AppCompatActivity() {

    private var privateAddressView: TextView? = null
    private var tBalance: TextView? = null
    private var mWalletAddress: TextView? = null
    private var walletSeedPhraseView: TextView? = null

    private var dl: DrawerLayout? = null
    private var t: ActionBarDrawerToggle? = null
    private var nv: NavigationView? = null
    //private val result: EditText? = null
    private val data = arrayListOf<String>()
    lateinit var resultToString: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_main)
        val li = LayoutInflater.from(this)
        val promptsView: View = li.inflate(R.layout.prompts, null)

        SecureSharedPrefs.setContext(this)
        val secure = SecureSharedPrefs()
        val path = applicationContext.filesDir

        //val result = findViewById<View>(R.id.editTextDialogUserInput) as EditText

        val alertDialogBuilder = AlertDialog.Builder(
                this)
        alertDialogBuilder.setView(promptsView)
        val userInput = promptsView
                .findViewById<View>(R.id.editTextDialogUserInput) as EditText

        resultToString = userInput.text.toString()
        checkFiles(path.toString())

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK"
                ) { dialog, id -> // get user input and set it to result
                    // edit text
                    resultToString = userInput.text.toString()
                    Log.e("TEXT RESULT", "$resultToString")
                }
                .setNegativeButton("Cancel",
                        DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })

        // create alert dialog
        val alertDialog: AlertDialog = alertDialogBuilder.create()
        // show it

        val recyclerView = findViewById<RecyclerView>(R.id.wallet_view)

        val adapter = CustomAdapter(this, data)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.addOnItemTouchListener(
                RecyclerItemClickListener(applicationContext, object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        // TODO Handle item click
                        Log.e("CLICK", "$position")
                        val test = "HI"
                        var wtf = data[position]
                        Log.e("CLICK", "$wtf")
                        val pathToWallet: String = wtf.substring(wtf.indexOf("/") + 1)
                        Log.e("CLICK", "$pathToWallet")
                        try {
                            Log.e("TEXT RESULT", "$resultToString")
                            val credentials = WalletUtils.loadCredentials(resultToString, pathToWallet)
                            val address = credentials.address
                            mWalletAddress!!.text = address
                            Log.e("Ethereum address", address)
                        } catch (e: org.web3j.crypto.CipherException) {
                            alertDialog.show()
                        }
                    }
                })
        )

        dl = findViewById<View>(R.id.profile_main) as DrawerLayout
        t = ActionBarDrawerToggle(this, dl, R.string.app_name, R.string.contact)
        dl!!.addDrawerListener(t!!)
        t!!.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        nv = findViewById<View>(R.id.navigation) as NavigationView
        nv!!.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { item ->

            val id = item.itemId

            when (id) {
                R.id.account -> Toast.makeText(this@ProfileActivity, "My Account", Toast.LENGTH_SHORT).show()
                R.id.settings -> startActivity(Intent(this, GenerateWalletActivity::class.java))
                R.id.import_wallet -> startActivity(Intent(this, ImportWalletActivity::class.java))
                else -> return@OnNavigationItemSelectedListener true
            }
            true
        })

        val web3 = Web3j.build(HttpService("https://mainnet.infura.io/v3/8531ac48cf594a719e497caea76bc0d5"))

        val ethGetBalance: EthGetBalance

        try
        {
            ethGetBalance = web3.ethGetBalance("0x373BBb32A7886A2d5467b6BCc53a18d411C6b275", DefaultBlockParameterName.LATEST).sendAsync().get()
            val wei = ethGetBalance.balance
            val totalBalance = wei.toString()
            Log.e("BALANCE: ", totalBalance)
            val tokenValue = Convert.fromWei(wei.toString(), Convert.Unit.ETHER)
            val strTokenAmount = tokenValue.toString()
            //val address = secure.getSecurePrefs().getString("address", "")
            val private_wallet_key = secure.getSecurePrefs().getString("wallet_private_key", "")
            val wallet_seed_phrase = secure.getSecurePrefs().getString("wallet_mnemonic", "")
            //mBalance = findViewById<View>(R.id.ethTextView) as TextView
            tBalance = findViewById<View>(R.id.totalBalanceView) as TextView
            mWalletAddress = findViewById<View>(R.id.wallet_address) as TextView
            privateAddressView = findViewById<View>(R.id.wallet_private_address) as TextView
            walletSeedPhraseView = findViewById<View>(R.id.wallet_seed_phrase) as TextView

            privateAddressView!!.text = private_wallet_key
            walletSeedPhraseView!!.text = wallet_seed_phrase
            tBalance!!.text = strTokenAmount

        }catch (e: InterruptedException)
        {
            e.printStackTrace()
        }catch (e: ExecutionException)
        {
            e.printStackTrace()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (t!!.onOptionsItemSelected(item)) true else super.onOptionsItemSelected(item)
    }

    private fun checkFiles(path: String) {
        File(path).walk().forEach {
            if (it.toString() != path) {
                Log.e("File exists!!!", it.toString())
                data.add("Item $it")
            }
        }
    }

}