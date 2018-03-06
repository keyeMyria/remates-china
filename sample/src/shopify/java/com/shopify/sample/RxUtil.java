package com.shopify.sample;

import com.shopify.buy3.GraphCall;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.Storefront;
import com.shopify.graphql.support.AbstractResponse;

import io.reactivex.Single;
import io.reactivex.SingleTransformer;
import io.reactivex.exceptions.Exceptions;

import static com.shopify.sample.util.Util.fold;

public final class RxUtil {

  public static Single<Storefront.QueryRoot> rxGraphQueryCall(final GraphCall<Storefront.QueryRoot> call) {
    return Single.<GraphResponse<Storefront.QueryRoot>>create(emitter -> {
      emitter.setCancellable(call::cancel);
      try {
        emitter.onSuccess(call.execute());
      } catch (Exception e) {
        Exceptions.throwIfFatal(e);
        emitter.onError(e);
      }
    }).compose(queryResponseDataTransformer());
  }

  public static Single<Storefront.Mutation> rxGraphMutationCall(final GraphCall<Storefront.Mutation> call) {
    return Single.<GraphResponse<Storefront.Mutation>>create(emitter -> {
      emitter.setCancellable(call::cancel);
      try {
        emitter.onSuccess(call.execute());
      } catch (Exception e) {
        Exceptions.throwIfFatal(e);
        emitter.onError(e);
      }
    }).compose(queryResponseDataTransformer());
  }

  private static <T extends AbstractResponse<T>> SingleTransformer<GraphResponse<T>, T> queryResponseDataTransformer() {
    return upstream -> upstream.flatMap(response -> {
      if (response.errors().isEmpty()) {
        return Single.just(response.data());
      } else {
        String errorMessage = fold(new StringBuilder(), response.errors(),
          (builder, error) -> builder.append(error.message()).append("\n")).toString();
        return Single.error(new RuntimeException(errorMessage));
      }
    });
  }

  private RxUtil() {
  }
}
