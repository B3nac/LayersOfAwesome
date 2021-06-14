package b3nac.LayersOfAwesome

class WalletContract {
    internal interface View : BaseView<Presenter?> {
        fun showBalance()
        fun showWalletAddress()
    }

    internal interface Presenter : BasePresenter {
        val walletBalance: Unit
        val walletAddress: Unit
    }
}