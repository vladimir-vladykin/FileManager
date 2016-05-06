package net.vladykin.filemanager.presenter;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Base presenter implementation.
 *
 * @author Vladimir Vladykin.
 */
public abstract class Presenter<V> {

    @NonNull
    private final CompositeSubscription subscriptionsToUnsubscribe = new CompositeSubscription();
    private V view;

    @CallSuper
    public void bindView(@NonNull V view) {
        V previousView = this.view;

        if (previousView != null) {
            throwViewShouldBeUnbounded(previousView);
        }

        this.view = view;
    }

    @CallSuper
    public void unbindView(@NonNull V view) {
        V previousView = this.view;

        if (previousView == view) {
            this.view = null;
        } else {
            throwUnexpectedView(previousView, view);
        }

        subscriptionsToUnsubscribe.clear();
    }

    protected V view() {
        return view;
    }

    protected final void unsubcribeAfterUnbind(@NonNull Subscription subscription) {
        subscriptionsToUnsubscribe.add(subscription);
    }

    private void throwUnexpectedView(V previousView, V viewToUnbind) {
        throw new IllegalStateException("Unexpected view! previousView = " + previousView
                + ", view to unbind = " + viewToUnbind);
    }

    private void throwViewShouldBeUnbounded(V previousView) {
        throw new IllegalStateException("Previous view is not unbounded! previousView = " + previousView);
    }
}
