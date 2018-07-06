/*
 * Copyright 2014-2017 Eduard Ereza Martínez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.per.epx.easytrain.helpers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Deque;
import java.util.Locale;

public class CrashAssistant {
    private final static String TAG = "CrashAssistant";
    //Package names
    private static final String CUSTOM_HANDLER_PACKAGE_NAME = "com.per.epx.easytrain.helpers";
    private static final String DEFAULT_HANDLER_PACKAGE_NAME = "com.android.internal.os";
    //Shared preferences
    private static final String SHARED_PREFERENCES_FILE = "custom_activity_on_crash";
    private static final String SHARED_PREFERENCES_FIELD_TIMESTAMP = "last_crash_timestamp";
    public static final String EXTRA_STACK_TRACE = "com.per.epx.easytrain.helpers.CrashAssistant.EXTRA_STACK_TRACE";
    private static final String EXTRA_ACTIVITY_LOG = "com.per.epx.easytrain.helpers.CrashAssistant.EXTRA_ACTIVITY_LOG";
    private static final String EXTRA_CONFIG = "com.per.epx.easytrain.helpers.CrashAssistant.EXTRA_CONFIG";
    private static final int MAX_STACK_TRACE_SIZE = 131071; //128 KB - 1
    private static final int MAX_ACTIVITIES_IN_LOG = 50;
    private static final Deque<String> activityLog = new ArrayDeque<>(MAX_ACTIVITIES_IN_LOG);
    private static WeakReference<Activity> lastActivityCreated = new WeakReference<>(null);
    private static boolean isInBackground = true;

    public static void install(@Nullable final Context context, @NonNull final CrashConfig config){
        try{
            final Thread.UncaughtExceptionHandler oldHandler = Thread.getDefaultUncaughtExceptionHandler();
            if (oldHandler != null && oldHandler.getClass().getName().startsWith(CUSTOM_HANDLER_PACKAGE_NAME)) {
                Log.e(TAG, "CrashAssistant was already installed, doing nothing!");
            } else {
                if (oldHandler != null && !oldHandler.getClass().getName().startsWith(DEFAULT_HANDLER_PACKAGE_NAME)) {
                    Log.e(TAG, "IMPORTANT WARNING! You already have an UncaughtExceptionHandler, are you sure this is correct? If you use a custom UncaughtExceptionHandler, you must initialize it AFTER CrashAssistant! Installing anyway, but your original handler will not be called.");
                }
                final Application application = (Application)context.getApplicationContext();
                Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread thread, Throwable throwable) {
                        Log.e(TAG, "App has crashed, executing CrashAssistant's UncaughtExceptionHandler", throwable);

                        if (hasCrashedInTheLastSeconds(application, config)) {
                            Log.e(TAG, "App already crashed recently, not starting custom error activity because we could enter a restart loop. Are you sure that your app does not crash directly on init?", throwable);
                            if (oldHandler != null) {
                                oldHandler.uncaughtException(thread, throwable);
                                return;
                            }
                        } else {
                            setLastCrashTimestamp(application, new Date().getTime());
                            if (isStackTraceLikelyConflictive(throwable, config.getErrorActivityClass())) {
                                Log.e(TAG, "Your application class or your error activity have crashed, the custom activity will not be launched!");
                                if (oldHandler != null) {
                                    oldHandler.uncaughtException(thread, throwable);
                                    return;
                                }
                            } else if (config.getBackgroundMode() == CrashConfig.BACKGROUND_MODE_SHOW_CUSTOM || !isInBackground) {

                                final Intent intent = new Intent(application, config.getErrorActivityClass());
                                StringWriter sw = new StringWriter();
                                PrintWriter pw = new PrintWriter(sw);
                                throwable.printStackTrace(pw);
                                String stackTraceString = sw.toString();

                                //Reduce data to 128KB so we don't get a TransactionTooLargeException when sending the intent.
                                //The limit is 1MB on Android but some devices seem to have it lower.
                                //See: http://developer.android.com/reference/android/os/TransactionTooLargeException.html
                                //And: http://stackoverflow.com/questions/11451393/what-to-do-on-transactiontoolargeexception#comment46697371_12809171
                                if (stackTraceString.length() > MAX_STACK_TRACE_SIZE) {
                                    String disclaimer = " [stack trace too large]";
                                    stackTraceString = stackTraceString.substring(0, MAX_STACK_TRACE_SIZE - disclaimer.length()) + disclaimer;
                                }
                                intent.putExtra(EXTRA_STACK_TRACE, stackTraceString);

                                if (config.isTrackActivities()) {
                                    StringBuilder activityLogStringBuilder = new StringBuilder();
                                    while (!activityLog.isEmpty()) {
                                        activityLogStringBuilder.append(activityLog.poll());
                                    }
                                    intent.putExtra(EXTRA_ACTIVITY_LOG, activityLogStringBuilder.toString());
                                }

                                intent.putExtra(EXTRA_CONFIG, config);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                if (config.getEventListener() != null) {
                                    config.getEventListener().onLaunchErrorActivity();
                                }
                                application.startActivity(intent);
                            } else if (config.getBackgroundMode() == CrashConfig.BACKGROUND_MODE_CRASH) {
                                if (oldHandler != null) {
                                    oldHandler.uncaughtException(thread, throwable);
                                    return;
                                }
                                //If it is null (should not be), we let it continue and kill the process or it will be stuck
                            }
                            //Else (BACKGROUND_MODE_SILENT): do nothing and let the following code kill the process
                        }
                        final Activity lastActivity = lastActivityCreated.get();
                        if (lastActivity != null) {
                            //We finish the activity, this solves a bug which causes infinite recursion.
                            //See: https://github.com/ACRA/acra/issues/42
                            lastActivity.finish();
                            lastActivityCreated.clear();
                        }
                        killCurrentProcess();
                    }
                });
                application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                    int currentlyStartedActivities = 0;
                    final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

                    @Override
                    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                        if (activity.getClass() != config.getErrorActivityClass()) {
                            // Copied from ACRA:
                            // Ignore activityClass because we want the last
                            // application Activity that was started so that we can
                            // explicitly kill it off.
                            lastActivityCreated = new WeakReference<>(activity);
                        }
                        if (config.isTrackActivities()) {
                            activityLog.add(dateFormat.format(new Date()) + ": " + activity.getClass().getSimpleName() + " created\n");
                        }
                    }

                    @Override
                    public void onActivityStarted(Activity activity) {
                        currentlyStartedActivities++;
                        isInBackground = (currentlyStartedActivities == 0);
                        //Do nothing
                    }

                    @Override
                    public void onActivityResumed(Activity activity) {
                        if (config.isTrackActivities()) {
                            activityLog.add(dateFormat.format(new Date()) + ": " + activity.getClass().getSimpleName() + " resumed\n");
                        }
                    }

                    @Override
                    public void onActivityPaused(Activity activity) {
                        if (config.isTrackActivities()) {
                            activityLog.add(dateFormat.format(new Date()) + ": " + activity.getClass().getSimpleName() + " paused\n");
                        }
                    }

                    @Override
                    public void onActivityStopped(Activity activity) {
                        //Do nothing
                        currentlyStartedActivities--;
                        isInBackground = (currentlyStartedActivities == 0);
                    }

                    @Override
                    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                        //Do nothing
                    }

                    @Override
                    public void onActivityDestroyed(Activity activity) {
                        if (config.isTrackActivities()) {
                            activityLog.add(dateFormat.format(new Date()) + ": " + activity.getClass().getSimpleName() + " destroyed\n");
                        }
                    }
                });
            }} catch (Throwable t) {
            Log.e(TAG, "An unknown error occurred while installing CrashAssistant, it may not have been properly initialized. Please report this as a bug if needed.", t);
        }
    }

    private static long getLastCrashTimestamp(@NonNull Context context) {
        return context.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE).getLong(SHARED_PREFERENCES_FIELD_TIMESTAMP, -1);
    }

    /**
     * INTERNAL method that stores the last crash timestamp
     *
     * @param timestamp The current timestamp.
     */
    @SuppressLint("ApplySharedPref") //This must be done immediately since we are killing the app
    private static void setLastCrashTimestamp(@NonNull Context context, long timestamp) {
        context.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE).edit().putLong(SHARED_PREFERENCES_FIELD_TIMESTAMP, timestamp).commit();
    }

    /**
     * INTERNAL method that tells if the app has crashed in the last seconds.
     * This is used to avoid restart loops.
     *
     * @return true if the app has crashed in the last seconds, false otherwise.
     */
    private static boolean hasCrashedInTheLastSeconds(@NonNull Context context, @NonNull CrashConfig config) {
        long lastTimestamp = getLastCrashTimestamp(context);
        long currentTimestamp = new Date().getTime();

        return (lastTimestamp <= currentTimestamp && currentTimestamp - lastTimestamp < config.getMinTimeBetweenCrashesMs());
    }

    /**
     * INTERNAL method that checks if the stack trace that just crashed is conflictive. This is true in the following scenarios:
     * - The application has crashed while initializing (handleBindApplication is in the stack)
     * - The error activity has crashed (activityClass is in the stack)
     *
     * @param throwable     The throwable from which the stack trace will be checked
     * @param activityClass The activity class to launch when the app crashes
     * @return true if this stack trace is conflictive and the activity must not be launched, false otherwise
     */
    private static boolean isStackTraceLikelyConflictive(@NonNull Throwable throwable, @NonNull Class<? extends Activity> activityClass) {
        do {
            StackTraceElement[] stackTrace = throwable.getStackTrace();
            for (StackTraceElement element : stackTrace) {
                if ((element.getClassName().equals("android.app.ActivityThread") && element.getMethodName().equals("handleBindApplication")) || element.getClassName().equals(activityClass.getName())) {
                    return true;
                }
            }
        } while ((throwable = throwable.getCause()) != null);
        return false;
    }

    /**
     * INTERNAL method that kills the current process.
     * It is used after restarting or killing the app.
     */
    private static void killCurrentProcess() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }

    public static class CrashConfig implements Serializable{
        private boolean enabled = true;
        private boolean showErrorDetails = true;
        private boolean trackActivities = false;
        private int minTimeBetweenCrashesMs = 3000;
        private Class<? extends Activity> errorActivityClass = null;
        private Class<? extends Activity> restartActivityClass = null;
        private EventListener eventListener = null;

        public static final int BACKGROUND_MODE_SILENT = 0;
        public static final int BACKGROUND_MODE_SHOW_CUSTOM = 1;
        public static final int BACKGROUND_MODE_CRASH = 2;
        @IntDef({BACKGROUND_MODE_CRASH, BACKGROUND_MODE_SHOW_CUSTOM, BACKGROUND_MODE_SILENT})
        @Retention(RetentionPolicy.SOURCE)
        private @interface BackgroundMode {
            //I hate empty blocks
        }

        private int backgroundMode = BACKGROUND_MODE_SHOW_CUSTOM;

        public boolean isTrackActivities() {
            return trackActivities ;
        }

        public void setTrackActivities(boolean trackActivities) {
            this.trackActivities = trackActivities;
        }

        public int getBackgroundMode() {
            return backgroundMode;
        }

        public void setBackgroundMode(int backgroundMode) {
            this.backgroundMode = backgroundMode;
        }

        public int getMinTimeBetweenCrashesMs() {
            return minTimeBetweenCrashesMs;
        }

        public void setShowErrorDetails(boolean showErrorDetails) {
            this.showErrorDetails = showErrorDetails;
        }

        public boolean isShowErrorDetails() {
            return showErrorDetails;
        }

        public void setMinTimeBetweenCrashesMs(int minTimeBetweenCrashesMs) {
            this.minTimeBetweenCrashesMs = minTimeBetweenCrashesMs;
        }

        public Class<? extends Activity> getErrorActivityClass() {
            return errorActivityClass;
        }

        public void setErrorActivityClass(Class<? extends Activity> errorActivityClass) {
            this.errorActivityClass = errorActivityClass;
        }

        public Class<? extends Activity> getRestartActivityClass() {
            return restartActivityClass;
        }

        public void setRestartActivityClass(Class<? extends Activity> restartActivityClass) {
            this.restartActivityClass = restartActivityClass;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public EventListener getEventListener() {
            return eventListener;
        }

        public void setEventListener(EventListener eventListener) {
            this.eventListener = eventListener;
        }

        public interface EventListener{
            void onLaunchErrorActivity();
        }
    }
}
