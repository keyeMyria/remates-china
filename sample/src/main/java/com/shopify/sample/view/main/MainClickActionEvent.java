package com.shopify.sample.view.main;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.shopify.sample.domain.model.Collection;
import com.shopify.sample.view.ScreenActionEvent;

import static com.shopify.sample.util.Util.checkNotNull;

public final class MainClickActionEvent extends ScreenActionEvent implements Parcelable {
    @SuppressWarnings("WeakerAccess")
    MainClickActionEvent(Parcel in) {
        super(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MainClickActionEvent> CREATOR = new Creator<MainClickActionEvent>() {
        @Override
        public MainClickActionEvent createFromParcel(Parcel in) {
            return new MainClickActionEvent(in);
        }

        @Override
        public MainClickActionEvent[] newArray(int size) {
            return new MainClickActionEvent[size];
        }
    };

    public static final String ACTION = MainClickActionEvent.class.getSimpleName();
    private static final String EXTRAS_ID = "collection_id";
    private static final String EXTRAS_IMAGE_URL = "collection_image_url";
    private static final String EXTRAS_TITLE = "collection_title";

    MainClickActionEvent(@NonNull final Collection collection) {
        super(ACTION);
        checkNotNull(collection, "collection == null");
        payload.putString(EXTRAS_ID, collection.id);
        payload.putString(EXTRAS_IMAGE_URL, collection.image);
        payload.putString(EXTRAS_TITLE, collection.title);
    }

    public String id() {
        return payload().getString(EXTRAS_ID);
    }

    public String imageUrl() {
        return payload().getString(EXTRAS_IMAGE_URL);
    }

    public String title() {
        return payload().getString(EXTRAS_TITLE);
    }
}
