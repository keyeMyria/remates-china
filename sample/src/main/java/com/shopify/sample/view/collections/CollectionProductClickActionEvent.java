package com.shopify.sample.view.collections;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.shopify.sample.domain.model.Product;
import com.shopify.sample.view.ScreenActionEvent;

import java.math.BigDecimal;

import static com.shopify.sample.util.Util.checkNotNull;

public final class CollectionProductClickActionEvent extends ScreenActionEvent implements Parcelable {
  public static final Creator<CollectionProductClickActionEvent> CREATOR = new Creator<CollectionProductClickActionEvent>() {
    @Override
    public CollectionProductClickActionEvent createFromParcel(Parcel in) {
      return new CollectionProductClickActionEvent(in);
    }

    @Override
    public CollectionProductClickActionEvent[] newArray(int size) {
      return new CollectionProductClickActionEvent[size];
    }
  };

  public static final String ACTION = CollectionProductClickActionEvent.class.getSimpleName();
  private static final String EXTRAS_ID = "product_id";
  private static final String EXTRAS_IMAGE_URL = "product_image_url";
  private static final String EXTRAS_TITLE = "product_title";
  private static final String EXTRAS_PRICE = "product_price";

  CollectionProductClickActionEvent(@NonNull final Product product) {
    super(ACTION);
    checkNotNull(product, "collectionProduct == null");
    payload.putString(EXTRAS_ID, product.id);
    payload.putString(EXTRAS_IMAGE_URL, product.image);
    payload.putString(EXTRAS_TITLE, product.title);
    payload.putDouble(EXTRAS_PRICE, product.price.doubleValue());
  }

  @SuppressWarnings("WeakerAccess")
  CollectionProductClickActionEvent(Parcel in) {
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

  public String id() {
    return payload().getString(EXTRAS_ID);
  }

  public String imageUrl() {
    return payload().getString(EXTRAS_IMAGE_URL);
  }

  public String title() {
    return payload().getString(EXTRAS_TITLE);
  }

  public BigDecimal price() {
    return BigDecimal.valueOf(payload().getDouble(EXTRAS_PRICE, 0));
  }
}
