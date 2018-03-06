package com.shopify.sample.view.main;

import android.support.annotation.NonNull;
import android.widget.TextView;

import com.shopify.sample.R;
import com.shopify.sample.domain.model.Collection;
import com.shopify.sample.view.base.ListItemViewHolder;
import com.shopify.sample.view.base.ListItemViewModel;
import com.shopify.sample.view.widget.image.ShopifyDraweeView;

import butterknife.BindView;
import butterknife.OnClick;

final class MainImageListItemViewModel extends ListItemViewModel<Collection> {
    MainImageListItemViewModel(final Collection payload) {
        super(payload, R.layout.main_collection_list_item);
    }

    @Override
    public ListItemViewHolder<Collection, ListItemViewModel<Collection>> createViewHolder(final ListItemViewHolder.OnClickListener onClickListener) {
        return new ItemViewHolder(onClickListener);
    }

    @Override
    public boolean equalsById(@NonNull final ListItemViewModel other) {
        if (other instanceof MainImageListItemViewModel) {
            Collection otherPayload = ((MainImageListItemViewModel) other).payload();

            return payload().equalsById(otherPayload);
        }

        return false;
    }

    @Override
    public boolean equalsByContent(@NonNull final ListItemViewModel other) {
        if (other instanceof MainImageListItemViewModel) {
            Collection otherPayload = ((MainImageListItemViewModel) other).payload();

            return payload().equals(otherPayload);
        }

        return false;
    }

    static final class ItemViewHolder extends ListItemViewHolder<Collection, ListItemViewModel<Collection>> {
        @BindView(R.id.image)
        ShopifyDraweeView imageView;

        @BindView((R.id.title))
        TextView title;

        ItemViewHolder(@NonNull final ListItemViewHolder.OnClickListener onClickListener) {
            super(onClickListener);
        }

        @Override
        public void bindModel(@NonNull final ListItemViewModel<Collection> listItemViewModel, final int position) {
            super.bindModel(listItemViewModel, position);

            imageView.loadShopifyImage(listItemViewModel.payload().image);
            title.setText(listItemViewModel.payload().title);
        }

        @SuppressWarnings("unchecked")
        @OnClick(R.id.image)
        void onImageClick() {
            onClickListener().onClick(itemModel());
        }
    }
}
