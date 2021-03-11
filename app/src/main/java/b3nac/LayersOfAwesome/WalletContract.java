package b3nac.LayersOfAwesome;

public class WalletContract {

    interface View extends BaseView<WalletContract.Presenter> {

        void showBalance();

        void showWalletAddress();
    }

    interface Presenter extends BasePresenter {

        void getWalletBalance();

        void getWalletAddress();
    }
}
