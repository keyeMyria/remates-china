package com.shopify.sample.domain.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.shopify.sample.domain.model.Auth;

/**
 * Class SessionPrefs
 */
public class SessionPrefs {
	/**
	 * String preferences
	 */
	private static final String PREFS_NAME = "REMATECHINA_PREFS";
	private static final String PREF_USER_EMAIL = "PREF_USER_EMAIL";
	private static final String PREF_USER_NAME = "PREF_USER_NAME";
	private static final String PREF_USER_TOKEN = "PREF_USER_TOKEN";

	/**
	 * Components
	 */
	private final SharedPreferences mPrefs;
	private boolean mIsLoggedIn = false;
	private static SessionPrefs INSTANCE;

	/**
	 * Method get
	 * @param context context
	 * @return instance
	 */
	public static SessionPrefs get(Context context) {
		if (INSTANCE == null) {
			INSTANCE = new SessionPrefs(context);
		}

		return INSTANCE;
	}

	/**
	 * Constructor SessionPrefs
	 * @param context context
	 */
	private SessionPrefs(Context context) {
		mPrefs = context.getApplicationContext()
				.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

		mIsLoggedIn = !TextUtils.isEmpty(mPrefs.getString(PREF_USER_TOKEN, null));
	}

	/**
	 * Method ismIsLoggedIn
	 * @return bool
	 */
	public boolean ismIsLoggedIn() {
		return mIsLoggedIn;
	}

	/**
	 * Method saveAuth
	 * @param auth object
	 */
	public void saveAuth(Auth auth) {
		if (auth != null) {
			SharedPreferences.Editor editor = mPrefs.edit();

			editor.putString(PREF_USER_EMAIL, auth.getName());
			editor.putString(PREF_USER_NAME, auth.getName());
			editor.putString(PREF_USER_TOKEN, auth.getToken());
			editor.apply();

			mIsLoggedIn = true;
		}
	}

	/**
	 * Method getPrefUserToken
	 * @return string
	 */
	public String getPrefUserToken() {
		return mPrefs.getString(PREF_USER_TOKEN, null);
	}

	/**
	 * Method setPrefUserToken
	 * @param token string
	 */
	public void setPrefUserToken(String token) {
		if (token != null) {
			SharedPreferences.Editor editor = mPrefs.edit();

			editor.putString(PREF_USER_TOKEN, token);
			editor.apply();
		}
	}

	/**
	 * Method logOut
	 */
	public void logOut() {
		mIsLoggedIn = false;

		SharedPreferences.Editor editor = mPrefs.edit();

		editor.putString(PREF_USER_EMAIL, null);
		editor.putString(PREF_USER_NAME, null);
		editor.putString(PREF_USER_TOKEN, null);
		editor.apply();
	}
}
