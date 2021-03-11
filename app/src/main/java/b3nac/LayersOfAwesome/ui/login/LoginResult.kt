package b3nac.LayersOfAwesome.ui.login

/**
 * Authentication result : success (user details) or error message.
 */
internal class LoginResult {
    var success: LoggedInUserView? = null
        private set
    var error: Int? = null
        private set

    constructor(error: Int?) {
        this.error = error
    }

    constructor(success: LoggedInUserView?) {
        this.success = success
    }

}