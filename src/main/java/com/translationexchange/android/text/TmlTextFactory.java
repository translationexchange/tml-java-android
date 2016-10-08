package com.translationexchange.android.text;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.translationexchange.android.R;
import com.translationexchange.android.TmlAndroid;
import com.translationexchange.android.logger.Logger;

import java.lang.ref.WeakReference;

class TmlTextFactory {

    private Logger logger = new Logger();

    private final int[] mAttributeId = new int[]{android.R.attr.text};

    /**
     * Handle the created view
     *
     * @param view    nullable.
     * @param context shouldn't be null.
     * @param attrs   shouldn't be null.
     * @return null if null is passed in.
     */

    View onViewCreated(View view, Context context, AttributeSet attrs) {
        if (view != null && view.getTag(R.id.calligraphy_tag_id) != Boolean.TRUE) {
            onViewCreatedInternal(view, context, attrs);
            view.setTag(R.id.calligraphy_tag_id, Boolean.TRUE);
        }
        return view;
    }

    private void onViewCreatedInternal(View view, final Context context, AttributeSet attrs) {
        if (view instanceof TextView) {
            TextView textView = (TextView) view;

            // Check xml attrs, style attrs and text appearance for font path
            String text = resolveFontPath(context, attrs);
            if (TextUtils.isEmpty(text)) {
                text = textView.getText().toString();
            }

            if (!TextUtils.isEmpty(text)) {
                text = text.trim();
            }

            if (!TextUtils.isEmpty(text)) {
                logger.debug("onTextChanged init", text);
                textView.setText(TmlAndroid.translate(text));
            }

            textView.addTextChangedListener(new TmlTextWatcher(textView, logger));
        }

        // AppCompat API21+ The ActionBar doesn't inflate default Title/SubTitle, we need to scan the
        // Toolbar(Which underlies the ActionBar) for its children.
        if (TmlTextUtils.canCheckForV7Toolbar() && view instanceof Toolbar) {
            final Toolbar toolbar = (Toolbar) view;
            toolbar.getViewTreeObserver().addOnGlobalLayoutListener(new ToolbarLayoutListener(this, context, toolbar));
        }
    }

    /**
     * Resolving font path from xml attrs, style attrs or text appearance
     */
    private String resolveFontPath(Context context, AttributeSet attrs) {
        // Try view xml attributes
        String textViewFont = TmlTextUtils.pullFontPathFromView(context, attrs, mAttributeId);

        // Try view style attributes
        if (TextUtils.isEmpty(textViewFont)) {
            textViewFont = TmlTextUtils.pullFontPathFromStyle(context, attrs, mAttributeId);
        }

        // Try View TextAppearance
        if (TextUtils.isEmpty(textViewFont)) {
            textViewFont = TmlTextUtils.pullFontPathFromTextAppearance(context, attrs, mAttributeId);
        }

        return textViewFont;
    }

    private static class ToolbarLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {

        static String BLANK = " ";

        private final WeakReference<TmlTextFactory> mCalligraphyFactory;
        private final WeakReference<Context> mContextRef;
        private final WeakReference<Toolbar> mToolbarReference;
        private final CharSequence originalSubTitle;

        private ToolbarLayoutListener(final TmlTextFactory tmlTextFactory, final Context context, Toolbar toolbar) {
            mCalligraphyFactory = new WeakReference<>(tmlTextFactory);
            mContextRef = new WeakReference<>(context);
            mToolbarReference = new WeakReference<>(toolbar);
            originalSubTitle = toolbar.getSubtitle();
            toolbar.setSubtitle(BLANK);
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onGlobalLayout() {
            final Toolbar toolbar = mToolbarReference.get();
            final Context context = mContextRef.get();
            final TmlTextFactory factory = mCalligraphyFactory.get();
            if (toolbar == null) return;
            if (factory == null || context == null) {
                removeSelf(toolbar);
                return;
            }

            int childCount = toolbar.getChildCount();
            if (childCount != 0) {
                // Process children, defer draw as it has set the typeface.
                for (int i = 0; i < childCount; i++) {
                    factory.onViewCreated(toolbar.getChildAt(i), context, null);
                }
            }
            removeSelf(toolbar);
            toolbar.setSubtitle(originalSubTitle);
        }

        private void removeSelf(final Toolbar toolbar) {// Our dark deed is done
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                //noinspection deprecation
                toolbar.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            } else {
                toolbar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        }
    }

}
