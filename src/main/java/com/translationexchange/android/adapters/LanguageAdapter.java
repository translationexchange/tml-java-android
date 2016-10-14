package com.translationexchange.android.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.translationexchange.android.R;
import com.translationexchange.core.languages.Language;

import java.util.List;

/**
 * Created by ababenko on 4/6/16.
 */
public class LanguageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Language> languages;
    private OnLanguageListener clickListener;

    public LanguageAdapter(OnLanguageListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setLanguages(List<Language> languages) {
        this.languages = languages;
        notifyDataSetChanged();
    }

    public Language removeItem(int position) {
        Language card = languages.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, languages.size());
        return card;
    }

    public Language addItem(int position, Language card) {
        languages.add(position, card);
        notifyItemInserted(position);
        notifyItemRangeChanged(position + 1, languages.size());
        return card;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MainViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_language, parent, false), clickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((MainViewHolder) holder).initUi(languages.get(position));
    }

    @Override
    public int getItemCount() {
        return languages != null ? languages.size() : 0;
    }

    private static class MainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private OnLanguageListener clickListener;
        private Language language;

        private MainViewHolder(View itemView, OnLanguageListener clickListener) {
            super(itemView);
            this.clickListener = clickListener;
            itemView.setOnClickListener(this);
        }

        private void initUi(Language language) {
            this.language = language;
            TextView textView = (TextView) itemView.findViewById(R.id.text);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.image);
            textView.setText(language.getEnglishName() + " (" + language.getNativeName() + ")");
            ImageLoader.getInstance().displayImage(language.getFlagUrl(), imageView, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    ((ImageView) view).setImageBitmap(null);
                }
            });
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.onClick(language);
            }
        }
    }

    public interface OnLanguageListener {
        void onClick(Language language);
    }
}
