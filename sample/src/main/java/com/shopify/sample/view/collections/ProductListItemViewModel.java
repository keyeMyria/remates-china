package com.shopify.sample.view.collections;

import android.support.annotation.NonNull;
import android.view.View;

import com.shopify.sample.R;
import com.shopify.sample.domain.model.Product;
import com.shopify.sample.view.base.ListItemViewHolder;
import com.shopify.sample.view.base.ListItemViewModel;
import com.shopify.sample.view.widget.image.ShopifyDraweeView;

import butterknife.BindView;
import butterknife.OnClick;

final class ProductListItemViewModel extends ListItemViewModel<Product> {

  ProductListItemViewModel(final Product payload) {
    super(payload, R.layout.collection_product_list_item);
  }

  @Override
  public ListItemViewHolder<Product, ListItemViewModel<Product>> createViewHolder(
    final ListItemViewHolder.OnClickListener onClickListener) {
    return new ItemViewHolder(onClickListener);
  }

  static final class ItemViewHolder extends ListItemViewHolder<Product, ListItemViewModel<Product>> {
    @BindView(R.id.image) ShopifyDraweeView imageView;

    ItemViewHolder(@NonNull final OnClickListener onClickListener) {
      super(onClickListener);
    }

    @Override public void bindModel(@NonNull final ListItemViewModel<Product> listViewItemModel, final int position) {
      super.bindModel(listViewItemModel, position);
      imageView.loadShopifyImage(listViewItemModel.payload().image);
    }

    @SuppressWarnings("unchecked")
    @OnClick(R.id.image)
    void onImageClick(final View v) {
      onClickListener().onClick(itemModel());
    }
  }
}
