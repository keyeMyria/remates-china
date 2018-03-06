package com.shopify.sample.view.main;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.shopify.buy3.GraphCall;
import com.shopify.buy3.GraphClient;
import com.shopify.buy3.GraphError;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.HttpCachePolicy;
import com.shopify.buy3.MutationGraphCall;
import com.shopify.buy3.QueryGraphCall;
import com.shopify.buy3.Storefront;
import com.shopify.sample.BuildConfig;
import com.shopify.sample.R;
import com.shopify.sample.domain.data.SessionPrefs;
import com.shopify.sample.domain.model.Auth;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * Class LoginActivity
 * A login screen that offers login via email/password
 */
public final class LoginActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
	/**
	 * GraphQl Adapter
	 */
	private GraphClient mGraphAdapter;

	/**
	 * Id to identity READ_CONTACTS permission request
	 */
	private static final int REQUEST_READ_CONTACTS = 0;

	/**
	 * UI references
	 */
	private ImageView mLogoView;
	private AutoCompleteTextView mEmailView;
	private EditText mPasswordView;
	private TextInputLayout mFloatLabelEmail;
	private TextInputLayout mFloatLabelPassword;
	private View mProgressView;
	private View mLoginFormView;

	/**
	 * UI variables
	 */
	private String token = null;

	/**
	 * Method onCreate
	 * Initialize the view and elements
	 * @param savedInstanceState intent
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		mGraphAdapter = GraphClient.builder(this)
				.shopDomain(BuildConfig.SHOP_DOMAIN)
				.accessToken(BuildConfig.API_KEY)
				.httpCache(new File(getApplicationContext().getCacheDir(), "/http"), 10 * 1024 * 1024)
				.defaultHttpCachePolicy(HttpCachePolicy.CACHE_FIRST.expireAfter(5, TimeUnit.MINUTES))
				.build();

		mLogoView = (ImageView) findViewById(R.id.image_logo);
		mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
		populateAutoComplete();
		mPasswordView = (EditText) findViewById(R.id.password);

		mPasswordView.setOnEditorActionListener((textView, id, keyEvent) -> {
			if (id == R.id.login || id == EditorInfo.IME_NULL) {
				if (!isOnline()) {
					showLoginError(getString(R.string.error_network));

					return false;
				}

				attemptLogin();

				return true;
			}

			return false;
		});

		mFloatLabelEmail = (TextInputLayout) findViewById(R.id.float_label_email);
		mFloatLabelPassword = (TextInputLayout) findViewById(R.id.float_label_password);

		Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
		mEmailSignInButton.setOnClickListener(view -> {
			if (!isOnline()) {
				showLoginError(getString(R.string.error_network));

				return;
			}

			attemptLogin();
		});

		Button mRegisterButton = (Button) findViewById(R.id.btn_link_signup);
		mRegisterButton.setOnClickListener(view -> {
			if (!isOnline()) {
				showLoginError(getString(R.string.error_network));

				return;
			}
			showRegisterScreen();
		});

		mLoginFormView = findViewById(R.id.login_form);
		mProgressView = findViewById(R.id.login_progress);
	}

	/**
	 * Method populateAutoComplete
	 */
	private void populateAutoComplete() {
		if (!mayRequestContacts()) {
			return;
		}

		getLoaderManager().initLoader(0, null, this);
	}

	/**
	 * Method mayRequestContacts
	 * @return bool
	 */
	private boolean mayRequestContacts() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			return true;
		}
		if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
			return true;
		}
		if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
			Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
					.setAction(android.R.string.ok, v -> requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS));
		} else {
			requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
		}
		return false;
	}

	/**
	 * Callback received when a permissions request has been completed.
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == REQUEST_READ_CONTACTS) {
			if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				populateAutoComplete();
			}
		}
	}

	/**
	 * Method attemptLogin
	 * Serve the main logic for create the customer token and get his information
	 */
	private void attemptLogin() {
		mFloatLabelEmail.setError(null);
		mFloatLabelPassword.setError(null);

		String email = mEmailView.getText().toString();
		String password = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		if (TextUtils.isEmpty(password)) {
			mFloatLabelPassword.setError(getString(R.string.error_field_required));
			focusView = mFloatLabelPassword;
			cancel = true;
		} else if (!isPasswordValid(password)) {
			mFloatLabelPassword.setError(getString(R.string.error_invalid_password));
			focusView = mFloatLabelPassword;
			cancel = true;
		}

		if (TextUtils.isEmpty(email)) {
			mFloatLabelEmail.setError(getString(R.string.error_field_required));
			focusView = mFloatLabelEmail;
			cancel = true;
		} else if (!isEmailValid(email)) {
			mFloatLabelEmail.setError(getString(R.string.error_invalid_email));
			focusView = mFloatLabelEmail;
			cancel = true;
		}

		if (cancel) {
			focusView.requestFocus();
		} else {
			showProgress(true);

			Storefront.CustomerAccessTokenCreateInput input = new Storefront.CustomerAccessTokenCreateInput(email, password);

			Storefront.MutationQuery mutationQuery = Storefront.mutation(mutation -> mutation
					.customerAccessTokenCreate(input, query -> query
							.customerAccessToken(customerAccessToken -> customerAccessToken
									.accessToken()
									.expiresAt()
							)
							.userErrors(userError -> userError
									.field()
									.message()
							)
					)
			);

			MutationGraphCall call = mGraphAdapter.mutateGraph(mutationQuery);

			call.enqueue(new GraphCall.Callback<Storefront.Mutation>() {
				@Override
				public void onResponse(@NonNull final GraphResponse<Storefront.Mutation> response) {
					runOnUiThread(() -> showProgress(false));

					if (!response.data().getCustomerAccessTokenCreate().getUserErrors().isEmpty()) {
						for (Storefront.UserError apiError: response.data().getCustomerAccessTokenCreate().getUserErrors()) {
							runOnUiThread(() -> showLoginError(apiError.getMessage()));
							Log.e("LoginActivity", apiError.getMessage());
						}
					} else {
						Storefront.CustomerAccessToken customerAccessToken = response.data().getCustomerAccessTokenCreate().getCustomerAccessToken();
						token = customerAccessToken.getAccessToken();
						getCustomer(token);
						showMainScreen();
					}
				}

				@Override
				public void onFailure(@NonNull GraphError error) {
					runOnUiThread(() -> showProgress(false));
					runOnUiThread(() -> showLoginError(error.getMessage()));
					Log.e("login", "Failed to execute query", error);
				}
			});
		}
	}

	/**
	 * Method getCustomer
	 * @param token string
	 */
	private void getCustomer(String token) {
		Storefront.QueryRootQuery query = Storefront.query(root -> root
				.customer(token, customer -> customer
						.displayName()
						.email()
				)
		);

		QueryGraphCall call = mGraphAdapter.queryGraph(query);

		call.enqueue(new GraphCall.Callback<Storefront.QueryRoot>() {

			@Override public void onResponse(@NonNull GraphResponse<Storefront.QueryRoot> response) {
				String name = response.data().getCustomer().getDisplayName();
				String email = response.data().getCustomer().getEmail();
				Auth customer = auth(name, email, token);
				SessionPrefs.get(LoginActivity.this).saveAuth(customer);
			}

			@Override public void onFailure(@NonNull GraphError error) {
				runOnUiThread(() -> showProgress(false));
				runOnUiThread(() -> showLoginError(error.getMessage()));
				Log.e("error", "Failed to execute query", error);
			}
		});
	}

	/**
	 * Mathod auth
	 * @param name string
	 * @param email string
	 * @param token string
	 * @return object
	 */
	public Auth auth(String name, String email, String token) {
		Auth customer = new Auth(name, email, token);

		customer.setName(name);
		customer.setEmail(email);
		customer.setToken(token);

		return customer;
	}

	/**
	 * Method isEmailValid
	 * @param email string
	 * @return bool
	 */
	private boolean isEmailValid(String email) {
		return email.contains("@");
	}

	/**
	 * Method isPasswordValid
	 * @param password string
	 * @return bool
	 */
	private boolean isPasswordValid(String password) {
		return password.length() > 5;
	}

	/**
	 * Method showProgress
	 * @param show bool
	 */
	private void showProgress(boolean show) {
		mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);

		int visibility = show ? View.GONE : View.VISIBLE;
		mLogoView.setVisibility(visibility);
		mLoginFormView.setVisibility(visibility);
	}

	/**
	 * Method onCreateLoader
	 * @param i int
	 * @param bundle object
	 * @return object
	 */
	@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
		return new CursorLoader(
				this,
				Uri.withAppendedPath(
						ContactsContract.Profile.CONTENT_URI,
						ContactsContract.Contacts.Data.CONTENT_DIRECTORY
				),
				ProfileQuery.PROJECTION,
				ContactsContract.Contacts.Data.MIMETYPE + " = ?", new String[] {
						ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE
				},
				ContactsContract.Contacts.Data.IS_PRIMARY + " DESC"
		);
	}

	/**
	 * Method onLoadFinished
	 * @param loader object
	 * @param cursor object
	 */
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		List<String> emails = new ArrayList<>();
		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			emails.add(cursor.getString(ProfileQuery.ADDRESS));
			cursor.moveToNext();
		}

		addEmailsToAutoComplete(emails);
	}

	/**
	 * Method onLoaderReset
	 * @param loader object
	 */
	@Override
	public void onLoaderReset(Loader<Cursor> loader) {

	}

	/**
	 * Method addEmailsToAutoComplete
	 * Add the email string to the field
	 * @param emailAddressCollection list
	 */
	private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
		ArrayAdapter<String> adapter = new ArrayAdapter<> (
				LoginActivity.this,
				android.R.layout.simple_dropdown_item_1line,
				emailAddressCollection
		);

		mEmailView.setAdapter(adapter);
	}

	/**
	 * Method ProfileQuery
	 * Search the inner contact list
	 */
	private interface ProfileQuery {
		String[] PROJECTION = {
				ContactsContract.CommonDataKinds.Email.ADDRESS,
				ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
		};

		int ADDRESS = 0;
	}

	/**
	 * Method showMainScreen
	 * Close this intent and show the main layout
	 */
	private void showMainScreen() {
		startActivity(new Intent(this, com.shopify.sample.view.main.MainListActivity.class));
		finish();
	}

	/**
	 * Method showRegisterScreen
	 * Close this intent and show the register form
	 */
	private void showRegisterScreen() {
		startActivity(new Intent(this, com.shopify.sample.view.main.RegisterActivity.class));
		finish();
	}

	/**
	 * Method showLoginError
	 * Show the dialog for error message
	 * @param error string
	 */
	private void showLoginError(String error) {
		AlertDialog.Builder dialog1 = new AlertDialog.Builder(LoginActivity.this);
		dialog1.setTitle("Error");
		dialog1.setMessage(error);

		dialog1.setPositiveButton(R.string.accept, (dialog, which) -> {});

		dialog1.show();
	}

	/**
	 * Method isOnline
	 * Validate the network port
	 * @return bool
	 */
	private boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		assert cm != null;
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

		return activeNetwork != null && activeNetwork.isConnected();
	}
}
