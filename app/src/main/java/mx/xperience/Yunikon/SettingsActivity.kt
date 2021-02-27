/*
 * Copyright (C) 2017 The LineageOS Project
 * Copyright (C) 2021 The XPerience Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mx.xperience.Yunikon

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.preference.Preference
import android.preference.Preference.OnPreferenceChangeListener
import android.preference.Preference.OnPreferenceClickListener
import android.preference.PreferenceCategory
import android.preference.PreferenceFragment
import android.preference.SwitchPreference
import android.view.View
import android.webkit.CookieManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import mx.xperience.Yunikon.utils.IntentUtils
import mx.xperience.Yunikon.utils.NetworkSecurityPolicyUtils.isSupported
import mx.xperience.Yunikon.utils.NetworkSecurityPolicyUtils.setCleartextTrafficPermitted
import mx.xperience.Yunikon.utils.PrefsUtils
import mx.xperience.Yunikon.utils.UiUtils

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener { v: View? -> finish() }
    }

    class MyPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstance: Bundle?) {
            super.onCreate(savedInstance)
            addPreferencesFromResource(R.xml.settings)
            val securityCategory = findPreference("category_security") as PreferenceCategory
            val homePage = findPreference("key_home_page")
            homePage.summary = PrefsUtils.getHomePage(context)
            homePage.onPreferenceClickListener = OnPreferenceClickListener { preference: Preference ->
                editHomePage(preference)
                true
            }
            val clearCookie = findPreference("key_cookie_clear")
            clearCookie.onPreferenceClickListener = OnPreferenceClickListener { preference: Preference? ->
                CookieManager.getInstance().removeAllCookies(null)
                Toast.makeText(context, getString(R.string.pref_cookie_clear_done),
                        Toast.LENGTH_LONG).show()
                true
            }
            val reachMode = findPreference("key_reach_mode") as SwitchPreference
            if (UiUtils.isTablet(context)) {
                preferenceScreen.removePreference(reachMode)
            } else {
                reachMode.onPreferenceClickListener = OnPreferenceClickListener { preference: Preference? ->
                    val intent = Intent(IntentUtils.EVENT_CHANGE_UI_MODE)
                    intent.putExtra(IntentUtils.EVENT_CHANGE_UI_MODE, reachMode.isChecked)
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
                    true
                }
            }
            val clearTextTraffic = findPreference("key_clear_text_traffic") as SwitchPreference
            if (isSupported) {
                clearTextTraffic.onPreferenceChangeListener = OnPreferenceChangeListener { preference: Preference?, value: Any? ->
                    setCleartextTrafficPermitted((value as Boolean?)!!)
                    true
                }
            } else {
                securityCategory.removePreference(clearTextTraffic)
            }
        }

        private fun editHomePage(preference: Preference) {
            val context = context
            val builder = AlertDialog.Builder(context)
            val alertDialog = builder.create()
            val inflater = alertDialog.layoutInflater
            val homepageView = inflater.inflate(R.layout.dialog_homepage_edit,
                    LinearLayout(context))
            val editText = homepageView.findViewById<EditText>(R.id.homepage_edit_url)
            editText.setText(PrefsUtils.getHomePage(context))
            builder.setTitle(R.string.pref_start_page_dialog_title)
                    .setMessage(R.string.pref_start_page_dialog_message)
                    .setView(homepageView)
                    .setPositiveButton(android.R.string.ok
                    ) { dialog: DialogInterface?, which: Int ->
                        val url = if (editText.text.toString().isEmpty()) getString(R.string.default_home_page) else editText.text.toString()
                        PrefsUtils.setHomePage(context, url)
                        preference.summary = url
                    }
                    .setNeutralButton(R.string.pref_start_page_dialog_reset
                    ) { dialog: DialogInterface?, which: Int ->
                        val url = getString(R.string.default_home_page)
                        PrefsUtils.setHomePage(context, url)
                        preference.summary = url
                    }
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
        }
    }
}