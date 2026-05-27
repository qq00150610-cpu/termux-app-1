package com.termux.shared.android;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.termux.shared.termux.settings.preferences.TermuxAppSharedPreferences;
import com.termux.shared.termux.TermuxConstants;

import java.util.Locale;

/**
 * Utility class for managing app language settings.
 */
public class LanguageUtils {

    /**
     * Apply the saved language setting to the context.
     *
     * @param context The context to apply language to.
     * @return The context with language applied.
     */
    @NonNull
    public static Context applyLanguage(@NonNull Context context) {
        String language = getLanguageSetting(context);
        return updateResources(context, language);
    }

    /**
     * Get the current language setting from preferences.
     *
     * @param context The context.
     * @return The language code (e.g., "system", "zh", "en").
     */
    public static String getLanguageSetting(@NonNull Context context) {
        TermuxAppSharedPreferences prefs = TermuxAppSharedPreferences.build(context);
        if (prefs != null) {
            return prefs.getLanguage();
        }
        return "system";
    }

    /**
     * Update the resources with the specified language.
     *
     * @param context   The context.
     * @param language  The language code ("system", "zh", or "en").
     * @return The updated context.
     */
    @NonNull
    public static Context updateResources(@NonNull Context context, String language) {
        Locale locale;

        if ("system".equals(language)) {
            locale = Locale.getDefault();
        } else if ("zh".equals(language)) {
            locale = Locale.SIMPLIFIED_CHINESE;
        } else if ("en".equals(language)) {
            locale = Locale.ENGLISH;
        } else {
            locale = Locale.getDefault();
        }

        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration configuration = new Configuration(resources.getConfiguration());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocales(new LocaleList(locale));
        } else {
            configuration.locale = locale;
        }

        return context.createConfigurationContext(configuration);
    }

    /**
     * Attach the base context with language applied.
     *
     * @param activity The activity.
     */
    public static void attachBaseContext(@NonNull Activity activity) {
        Context context = applyLanguage(activity.getBaseContext());
        superAttachBaseContext(activity, context);
    }

    private static void superAttachBaseContext(@NonNull Activity activity, @NonNull Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            activity.attachBaseContext(context);
        } else {
            activity.attachBaseContext(context);
        }
    }

    /**
     * Create an application context with language applied.
     *
     * @param application The application.
     * @return The application context with language applied.
     */
    @NonNull
    public static Context createApplicationContext(@NonNull Application application) {
        Context context = applyLanguage(application.getBaseContext());
        return context;
    }
}
