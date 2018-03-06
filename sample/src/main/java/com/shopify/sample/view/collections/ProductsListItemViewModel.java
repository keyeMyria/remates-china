package com.shopify.sample.view.collections;

import android.support.annotation.NonNull;

import com.shopify.sample.R;
import com.shopify.sample.domain.model.Product;
import com.shopify.sample.view.base.ListItemViewHolder;
import com.shopify.sample.view.base.ListItemViewModel;

import java.util.List;

import butterknife.BindView;

final class ProductsListItemViewModel extends ListItemViewModel<List<Product>> {
  ProductsListItemViewModel(final List<Product> payload) {
    super(payload, R.layout.collection_products_list_item);
  }

  @Override
  public ListItemViewHolder<List<Product>, ListItemViewModel<List<Product>>> createViewHolder(
    final ListItemViewHolder.OnClickListener onClickListener) {
    return new ItemViewHolder(onClickListener);
  }

  @Override
  public boolean equalsById(@NonNull final ListItemViewModel other) {
    if (other instanceof ProductsListItemViewModel) {
      List<Product> payload = payload();
      List<Product> otherPayload = ((ProductsListItemViewModel) other).payload();

      if (payload.size() == otherPayload.size()) {
        for (int i = 0; i < payload.size(); i++) {
          if (!payload.get(i).equalsById(otherPayload.get(i))) {
            return false;
          }
        }

        return true;
      }
    }

    return false;
  }

  @Override
  public boolean equalsByContent(@NonNull final ListItemViewModel other) {
    if (other instanceof ProductsListItemViewModel) {
      List<Product> payload = payload();
      List<Product> otherPayload = ((ProductsListItemViewModel) other).payload();

      if (payload.size() == otherPayload.size()) {
        for (int i = 0; i < payload.size(); i++) {
          if (!payload.get(i).equals(otherPayload.get(i))) {
            return false;
          }
        }

        return true;
      }
    }

    return false;
  }

  static final class ItemViewHolder extends ListItemViewHolder<List<Product>, ListItemViewModel<List<Product>>> {
    @BindView(R.id.product_list) ProductListView productListView;

    ItemViewHolder(@NonNull final OnClickListener onClickListener) {
      super(onClickListener);
    }

    @Override public void bindModel(@NonNull final ListItemViewModel<List<Product>> listViewItemModel, final int position) {
      super.bindModel(listViewItemModel, position);

      productListView.setItems(listViewItemModel.payload());
    }
  }
}
