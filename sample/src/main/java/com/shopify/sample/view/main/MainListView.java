package com.shopify.sample.view.main;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.shopify.sample.R;
import com.shopify.sample.domain.model.Collection;
import com.shopify.sample.view.Constant;
import com.shopify.sample.view.ScreenRouter;
import com.shopify.sample.view.ViewUtils;
import com.shopify.sample.view.base.LifecycleSwipeRefreshLayout;
import com.shopify.sample.view.base.ListItemViewModel;
import com.shopify.sample.view.base.RecyclerViewAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class MainListView extends LifecycleSwipeRefreshLayout implements RecyclerViewAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, ViewUtils.OnNextPageListener {
    @BindView(R.id.list)
    RecyclerView listView;

    private RecyclerViewAdapter adapter;
    private MainListViewModel viewModel;

    public MainListView(@NonNull final Context context) {
        super(context);
    }

    public MainListView(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onItemClick(@NonNull final ListItemViewModel itemViewModel) {
        if (itemViewModel.payload() instanceof Collection) {
            ScreenRouter.route(getContext(), new MainClickActionEvent((Collection) itemViewModel.payload()));
        }
    }

    @Override
    public void onNextPage() {
        viewModel.fetchData();
    }

    @Override
    public void onRefresh() {
        viewModel.reset();
        viewModel.fetchData();
    }

    public void bindViewModel(@NonNull final MainListViewModel viewModel) {
        this.viewModel = viewModel;
        viewModel.fetchDataIfNecessary();
        viewModel
                .state()
                .observe(this, state -> setRefreshing(state == MainListViewModel.State.FETCHING));
        viewModel
                .error()
                .observe(this, error -> {
                    Snackbar snackbar = Snackbar.make(this, R.string.default_error, Snackbar.LENGTH_LONG);
                    snackbar.getView().setBackgroundResource(R.color.snackbar_error_background);
                    snackbar.show();
                });
        viewModel
                .items()
                .observe(this, adapter::swapItemsAndNotify);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        adapter = new RecyclerViewAdapter(this);
		LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
		layoutManager.setInitialPrefetchItemCount(prefetchItemCount());
		layoutManager.setItemPrefetchEnabled(true);
		listView.setLayoutManager(layoutManager);
        listView.setAdapter(adapter);
		int defaultPadding = getResources().getDimensionPixelOffset(R.dimen.default_padding);

		listView.addItemDecoration(new RecyclerView.ItemDecoration() {
			@Override public void getItemOffsets(final Rect outRect, final View view, final RecyclerView parent, final RecyclerView.State state) {
				int position = parent.getChildAdapterPosition(view);

				if (position == RecyclerView.NO_POSITION) {
					return;
				}

				outRect.left = position == 0 ? defaultPadding / 2 : defaultPadding / 4;
				outRect.right = position == parent.getAdapter().getItemCount() ? defaultPadding / 2 : defaultPadding / 4;
			}
		});

		ViewUtils.setOnNextPageListener(listView, Constant.THRESHOLD, this);
		setOnRefreshListener(this);
    }

	private int prefetchItemCount() {
		return getResources().getDisplayMetrics().widthPixels / getResources().getDimensionPixelOffset(R.dimen.product_thumbnail_size) + 1;
	}
}
