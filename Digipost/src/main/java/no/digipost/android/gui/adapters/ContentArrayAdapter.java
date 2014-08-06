/**
 * Copyright (C) Posten Norge AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.digipost.android.gui.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;

import no.digipost.android.R;

public abstract class ContentArrayAdapter<T> extends ArrayAdapter<T> {
    protected Context context;
    protected ArrayList<T> objects;
    protected ArrayList<T> filtered;

    protected boolean[] checked;
    protected boolean checkboxVisible;
    protected boolean hideContentTypeImage;

    protected TextView title;
    protected TextView subTitle;
    protected TextView metaTop;
    protected TextView metaMiddle;
    protected ImageView metaBottom;
    protected ImageView contentTypeImage;

    protected Filter contentFilter;

    protected String titleFilterText;
    protected String subTitleFilterText;
    protected String metaTopFilterText;

    public ContentArrayAdapter(final Context context, final int resource, final ArrayList<T> objects) {
        super(context, resource, objects);

        this.context = context;
        this.filtered = objects;
        this.objects = this.filtered;

        this.checked = new boolean[this.filtered.size()];

        this.titleFilterText = null;
        this.subTitleFilterText = null;
        this.metaTopFilterText = null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.content_list_item, parent, false);

        this.title = (TextView) row.findViewById(R.id.content_title);
        this.subTitle = (TextView) row.findViewById(R.id.content_subTitle);
        this.metaTop = (TextView) row.findViewById(R.id.content_meta_top);
        this.metaMiddle = (TextView) row.findViewById(R.id.content_meta_middle);
        this.metaBottom = (ImageView) row.findViewById(R.id.content_meta_bottom);
        this.contentTypeImage = (ImageView) row.findViewById((R.id.content_type_image));

        CheckBox checkBox = (CheckBox) row.findViewById(R.id.content_checkbox);
        checkBox.setFocusable(false);
        checkBox.setClickable(false);
        if (checkboxVisible) {
            if (checked[position]) {
                checkBox.setChecked(true);
            }

            checkBox.setVisibility(View.VISIBLE);
        } else {
            checkBox.setVisibility(View.GONE);
        }

        return row;
    }

    protected void setTitleAndSubTitleBold() {
        title.setTypeface(null, Typeface.BOLD);
        subTitle.setTypeface(null, Typeface.BOLD);
    }

    public void replaceAll(Collection<? extends T> collection) {
        if (collection != null) {
            this.filtered.clear();
            this.filtered.addAll(collection);
            this.objects = this.filtered;
            initializeChecked();
            notifyDataSetChanged();
        }
    }

    public void replaceAtPosition(T object, int position) {
        filtered.set(position, object);
        notifyDataSetChanged();
    }

    @Override
    public void add(final T object) {
        filtered.add(object);
        initializeChecked();
        notifyDataSetChanged();
    }

    @Override
    public T getItem(final int position) {
        return filtered.get(position);
    }

    @Override
    public int getCount() {
        return filtered.size();
    }

    @Override
    public void remove(final T object) {
        filtered.remove(object);
        initializeChecked();
        notifyDataSetChanged();
    }

    @Override
    public void addAll(Collection<? extends T> collection) {
        filtered.addAll(collection);
        initializeChecked();
        notifyDataSetChanged();
    }

    @Override
    public abstract Filter getFilter();

    protected void setFilterTextColor() {
        if (titleFilterText != null) {
            setTextViewFilterTextColor(title, titleFilterText);
        }

        if (subTitleFilterText != null) {
            setTextViewFilterTextColor(subTitle, subTitleFilterText);
        }

        if (metaTopFilterText != null) {
            setTextViewFilterTextColor(metaTop, metaTopFilterText);
        }
    }

    private void setTextViewFilterTextColor(final TextView v, final String filterText) {
        int l = filterText.length();
        int i = v.getText().toString().toLowerCase().indexOf(filterText.toLowerCase());

        if (i < 0) {
            return;
        }

        Spannable sb = new SpannableString(v.getText().toString());
        sb.setSpan(new BackgroundColorSpan(getContext().getResources().getColor(R.color.search_highlight_color)), i, i + l, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        v.setText(sb);
    }

    private void initializeChecked() {
        checked = new boolean[filtered.size()];
    }

    public void setCheckboxVisible(boolean state) {
        checkboxVisible = state;
        initializeChecked();
        notifyDataSetChanged();
    }

    public void setContentTypeImageVisible(boolean state) {
        hideContentTypeImage = !state;
        initializeChecked();
        notifyDataSetChanged();
    }

    public void clearChecked() {
        initializeChecked();
        notifyDataSetChanged();
    }

    public void setChecked(int position) {
        checked[position] = !checked[position];
    }

    public int getCheckedCount() {
        int count = 0;

        for (boolean state : checked) {
            if (state) {
                count++;
            }
        }

        return count;
    }

    public ArrayList<T> getCheckedItems() {
        ArrayList<T> checkedItems = new ArrayList<T>();

        for (int i = 0; i < checked.length; i++) {
            if (checked[i]) {
                checkedItems.add(filtered.get(i));
            }
        }

        return checkedItems;
    }

    public void clearFilter() {
        getFilter().filter("");
    }
}