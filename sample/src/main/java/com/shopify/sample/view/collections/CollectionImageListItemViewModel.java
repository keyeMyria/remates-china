package com.shopify.sample.view.collections;

import android.support.annotation.NonNull;

import com.shopify.sample.R;
import com.shopify.sample.domain.model.Collection;
import com.shopify.sample.view.base.ListItemViewHolder;
import com.shopify.sample.view.base.ListItemViewModel;
import com.shopify.sample.view.widget.image.ShopifyDraweeView;

import butterknife.BindView;
import butterknife.OnClick;

final class CollectionImageListItemViewModel extends ListItemViewModel<Collection> {
  CollectionImageListItemViewModel(final Collection payload) {
    super(payload, R.layout.collection_image_list_item);
  }

  @Override
  public ListItemViewHolder<Collection, ListItemViewModel<Collection>> createViewHolder(final ListItemViewHolder.OnClickListener onClickListener) {
    return new ItemViewHolder(onClickListener);
  }

  @Override
  public boolean equalsById(@NonNull final ListItemViewModel other) {
    if (other instanceof CollectionImageListItemViewModel) {
      Collection otherPayload = ((CollectionImageListItemViewModel) other).payload();

      return payload().equalsById(otherPayload);
    }

    return false;
  }

  @Override
  public boolean equalsByContent(@NonNull final ListItemViewModel other) {
    if (other instanceof CollectionImageListItemViewModel) {
      Collection otherPayload = ((CollectionImageListItemViewModel) other).payload();

      return payload().equals(otherPayload);
    }

    return false;
  }

  static final class ItemViewHolder extends ListItemViewHolder<Collection, ListItemViewModel<Collection>> {
    @BindView(R.id.image)
    ShopifyDraweeView imageView;

    ItemViewHolder(@NonNull final ListItemViewHolder.OnClickListener onClickListener) {
      super(onClickListener);
    }

    @Override
    public void bindModel(@NonNull final ListItemViewModel<Collection> listViewItemModel, final int position) {
      super.bindModel(listViewItemModel, position);

      imageView.loadShopifyImage(listViewItemModel.payload().image);
    }

    @SuppressWarnings("unchecked")
    @OnClick(R.id.image)
    void onImageClick() {
      onClickListener().onClick(itemModel());
    }
  }
}
