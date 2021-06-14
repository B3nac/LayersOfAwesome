package b3nac.LayersOfAwesome

interface GenerateContract {
    interface View : BaseView<Presenter?> {
        fun showGeneratedWallet(walletPublicAddress: String?)
    }

    interface Presenter : BasePresenter {
        fun generateWallet(password: String?)
    }
}
