package com.lh.kete.activity.pref

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.ListPreference
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import com.lh.kete.R

class SettingFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.prefs)
        for (i in 0 until preferenceScreen.preferenceCount) {
            onPreferenceChanged(preferenceScreen.getPreference(i))
        }
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener{ a, b ->

        }
    }

    private fun onPreferenceChanged(pref: Preference) {
        if (pref is ListPreference) {
            pref.setSummary(preferenceManager.sharedPreferences.getString(pref.key, ""))
            pref.setOnPreferenceChangeListener { preference, key ->
                if (preference is ListPreference) {
                    preference.setSummary(key as String)
                }
                true
            }
        }
    }


}