package com.lh.kete.activity.pref

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
    }

    private fun onPreferenceChanged(pref: Preference) {
        if (pref is ListPreference) {
            pref.setSummary(pref.entry)
            pref.setOnPreferenceChangeListener { preference, value ->
                if (preference is ListPreference) {
                    val displayText = preference.entries[preference.findIndexOfValue(value as String)]
                    preference.setSummary(displayText)
                }
                true
            }
        }
    }


}