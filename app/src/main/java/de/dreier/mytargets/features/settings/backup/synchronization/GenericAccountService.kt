/*
 * Copyright (C) 2018 Florian Dreier
 *
 * This file is part of MyTargets.
 *
 * MyTargets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * MyTargets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package de.dreier.mytargets.features.settings.backup.synchronization

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.NetworkErrorException
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.IBinder

import timber.log.Timber

class GenericAccountService : Service() {
    private var mAuthenticator: Authenticator? = null

    override fun onCreate() {
        Timber.i("Service created")
        mAuthenticator = Authenticator(this)
    }

    override fun onDestroy() {
        Timber.i("Service destroyed")
    }

    override fun onBind(intent: Intent): IBinder? {
        return mAuthenticator!!.iBinder
    }

    inner class Authenticator(context: Context) : AbstractAccountAuthenticator(context) {

        override fun editProperties(accountAuthenticatorResponse: AccountAuthenticatorResponse,
                                    s: String): Bundle {
            throw UnsupportedOperationException()
        }

        @Throws(NetworkErrorException::class)
        override fun addAccount(accountAuthenticatorResponse: AccountAuthenticatorResponse,
                                s: String, s2: String, strings: Array<String>, bundle: Bundle): Bundle? {
            return null
        }

        @Throws(NetworkErrorException::class)
        override fun confirmCredentials(accountAuthenticatorResponse: AccountAuthenticatorResponse,
                                        account: Account, bundle: Bundle): Bundle? {
            return null
        }

        @Throws(NetworkErrorException::class)
        override fun getAuthToken(accountAuthenticatorResponse: AccountAuthenticatorResponse,
                                  account: Account, s: String, bundle: Bundle): Bundle {
            throw UnsupportedOperationException()
        }

        override fun getAuthTokenLabel(s: String): String {
            throw UnsupportedOperationException()
        }

        @Throws(NetworkErrorException::class)
        override fun updateCredentials(accountAuthenticatorResponse: AccountAuthenticatorResponse,
                                       account: Account, s: String, bundle: Bundle): Bundle {
            throw UnsupportedOperationException()
        }

        @Throws(NetworkErrorException::class)
        override fun hasFeatures(accountAuthenticatorResponse: AccountAuthenticatorResponse,
                                 account: Account, strings: Array<String>): Bundle {
            throw UnsupportedOperationException()
        }
    }

    companion object {

        private const val ACCOUNT_NAME = "MyTargets"
        private const val ACCOUNT_TYPE = "mytargets.dreier.de"

        /**
         * Obtain a handle to the [Account] used for sync in this application.
         *
         * @return Handle to application's account (not guaranteed to resolve unless createSyncAccount()
         * has been called)
         */
        val account: Account
            get() = Account(ACCOUNT_NAME, ACCOUNT_TYPE)
    }
}

