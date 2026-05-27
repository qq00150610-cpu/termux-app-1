package com.termux.app.fragments.settings;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.termux.R;
import com.termux.app.TermuxActivity;
import com.termux.shared.android.AndroidUtils;
import com.termux.shared.termux.settings.preferences.TermuxAppSharedPreferences;

import java.util.Locale;

public class LanguagePreferencesFragment extends PreferenceFragmentCompat {

    public static final String LANGUAGE_PREFERENCE_KEY = "language";

    private static final String[] LANGUAGE_VALUES = {"system", "zh", "en"};
    private static final String[] LANGUAGE_NAMES = {"跟随系统", "中文", "English"};

    private String currentLanguage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentLanguage = getCurrentLanguage();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context context = getContext();
        if (context == null) return;

        setPreferencesFromResource(R.xml.termux_language_preferences, rootKey);

        Preference languagePref = findPreference(LANGUAGE_PREFERENCE_KEY);
        if (languagePref != null) {
            updateLanguageSummary(languagePref, currentLanguage);
            languagePref.setOnPreferenceClickListener(preference -> {
                showLanguageDialog(languagePref);
                return true;
            });
        }
    }

    private void showLanguageDialog(Preference languagePref) {
        int selectedIndex = getIndexOfLanguage(currentLanguage);
        if (selectedIndex < 0) selectedIndex = 0;

        new AlertDialog.Builder(requireContext())
            .setTitle(R.string.termux_language_preferences_title)
            .setSingleChoiceItems(LANGUAGE_NAMES, selectedIndex, (dialog, which) -> {
                String selectedLanguage = LANGUAGE_VALUES[which];
                if (!selectedLanguage.equals(currentLanguage)) {
                    currentLanguage = selectedLanguage;
                    saveLanguagePreference(selectedLanguage);
                    updateLanguageSummary(languagePref, selectedLanguage);
                    AndroidUtils.restartApp(requireContext());
                }
                dialog.dismiss();
            })
            .setNegativeButton(android.R.string.cancel, null)
            .show();
    }

    private void saveLanguagePreference(String language) {
        Context context = getContext();
        if (context == null) return;
        AppSharedPreferences.get(context).setLanguage(language);
    }

    private String getCurrentLanguage() {
        Context context = getContext();
        if (context == null) return "system";
        TermuxAppSharedPreferences prefs = TermuxAppSharedPreferences.build(context);
        if (prefs != null) {
            String savedLanguage = prefs.getLanguage();
            if (savedLanguage != null && !savedLanguage.isEmpty()) {
                return savedLanguage;
            }
        }
        return "system";
    }

    private int getIndexOfLanguage(String language) {
        for (int i = 0; i < LANGUAGE_VALUES.length; i++) {
            if (LANGUAGE_VALUES[i].equals(language)) {
                return i;
            }
        }
        return 0;
    }

    private void updateLanguageSummary(Preference preference, String language) {
        int index = getIndexOfLanguage(language);
        if (index >= 0) {
            preference.setSummary(LANGUAGE_NAMES[index]);
        }
    }

}
