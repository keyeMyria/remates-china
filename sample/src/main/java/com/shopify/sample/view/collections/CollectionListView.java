package com.shopify.sample.view.collections;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.shopify.sample.R;
import com.shopify.sample.domain.model.Collection;
import com.shopify.sample.view.Constant;
import com.shopify.sample.view.ScreenRouter;
import com.shopify.sample.view.ViewUtils;
import com.shopify.sample.view.ViewUtils.OnNextPageListener;
import com.shopify.sample.view.base.LifecycleFrameLayout;
import com.shopify.sample.view.base.ListItemViewModel;
import com.shopify.sample.view.base.RecyclerViewAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class CollectionListView extends LifecycleFrameLayout implements OnNextPageListener, SwipeRefreshLayout.OnRefreshListener, RecyclerViewAdapter.OnItemClickListener {
  @BindView(R.id.list)
  RecyclerView listView;

  @BindView(R.id.swipe_refresh_layout)
  SwipeRefreshLayout swipeRefreshLayoutView;

  private RecyclerViewAdapter adapter;
  private CollectionListViewModel viewModel;

  public CollectionListView(@NonNull final Context context) {
    super(context);
  }

  public CollectionListView(@NonNull final Context context, @Nullable final AttributeSet attrs) {
    super(context, attrs);
  }

  public CollectionListView(@NonNull final Context context, @Nullable final AttributeSet attrs, @AttrRes final int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  public void onItemClick(@NonNull final ListItemViewModel itemViewModel) {
    if (itemViewModel.payload() instanceof Collection) {
      ScreenRouter.route(getContext(), new CollectionClickActionEvent((Collection) itemViewModel.payload()));
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

  public void bindViewModel(@NonNull final CollectionListViewModel viewModel) {
    this.viewModel = viewModel;
    viewModel.fetchDataIfNecessary();
    viewModel
      .state()
      .observe(this, state -> swipeRefreshLayoutView.setRefreshing(state == CollectionListViewModel.State.FETCHING));
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
    listView.setLayoutManager(new LinearLayoutManager(getContext()));
    listView.setHasFixedSize(true);
    listView.setAdapter(adapter);

    ViewUtils.setOnNextPageListener(listView, Constant.THRESHOLD, this);
    swipeRefreshLayoutView.setOnRefreshListener(this);
  }
}
