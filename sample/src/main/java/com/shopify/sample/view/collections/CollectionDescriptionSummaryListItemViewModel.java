package com.shopify.sample.view.collections;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.shopify.sample.R;
import com.shopify.sample.domain.model.Collection;
import com.shopify.sample.view.base.ListItemViewHolder;
import com.shopify.sample.view.base.ListItemViewModel;

import butterknife.BindView;

final class CollectionDescriptionSummaryListItemViewModel extends ListItemViewModel<Collection> {
    CollectionDescriptionSummaryListItemViewModel(final Collection payload) {
      super(payload, R.layout.collection_description_summary_list_item);
    }

    @Override
	public ListItemViewHolder<Collection, ListItemViewModel<Collection>> createViewHolder(final ListItemViewHolder.OnClickListener onClickListener) {
    	return new ItemViewHolder(onClickListener);
    }

    @Override
	public boolean equalsById(@NonNull final ListItemViewModel other) {
    	if (other instanceof CollectionDescriptionSummaryListItemViewModel) {
    		Collection otherPayload = ((CollectionDescriptionSummaryListItemViewModel) other).payload();

    		return payload().equalsById(otherPayload);
    	}

    	return false;
    }

  @Override public boolean equalsByContent(@NonNull final ListItemViewModel other) {
    if (other instanceof CollectionDescriptionSummaryListItemViewModel) {
      Collection otherPayload = ((CollectionDescriptionSummaryListItemViewModel) other).payload();
      return payload().equals(otherPayload);
    }
    return false;
  }

  static final class ItemViewHolder extends ListItemViewHolder<Collection, ListItemViewModel<Collection>> {
    @BindView(R.id.description) TextView descriptionView;

    ItemViewHolder(@NonNull final OnClickListener onClickListener) {
      super(onClickListener);
    }

    @Override public void bindModel(@NonNull final ListItemViewModel<Collection> listViewItemModel, final int position) {
      super.bindModel(listViewItemModel, position);
      boolean descriptionVisible = !TextUtils.isEmpty(listViewItemModel.payload().description);
      descriptionView.setText(listViewItemModel.payload().description);
      descriptionView.setVisibility(descriptionVisible ? View.VISIBLE : View.GONE);
    }
  }
}
