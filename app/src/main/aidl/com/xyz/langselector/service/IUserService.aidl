// IUserService.aidl
package com.xyz.langselector.service;

interface IUserService {

    /**
     * Set the per-app language for a given package.
     * @param packageName target app package name
     * @param localeTag BCP 47 language tag (e.g. "en-US"), empty string for system default
     */
    void setApplicationLocales(String packageName, String localeTag);

    /**
     * Get the current per-app language for a given package.
     * @param packageName target app package name
     * @return locale tag string, empty if system default
     */
    String getApplicationLocales(String packageName);

    /**
     * Force stop a package to apply language changes immediately.
     * @param packageName target app package name
     */
    void forceStopPackage(String packageName);

    /**
     * Check if the locale service is available.
     * @return true if the service can set per-app locales
     */
    boolean isLocaleServiceAvailable();

    /**
     * Called when the service is being destroyed.
     */
    void destroy();
}
