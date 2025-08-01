package com.example.myapplication;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import java.util.ArrayList;
import java.util.List;

public class SubAssetFilterAdapter extends ArrayAdapter<String> {

    private List<String> originalList;
    private List<String> filteredList;
    private Filter filter;

    public enum MatchMode {
        STARTS_WITH, CONTAINS, ENDS_WITH
    }

    private MatchMode matchMode = MatchMode.CONTAINS; // Change as needed


    public SubAssetFilterAdapter(Context context, int resource, List<String> items) {
        super(context, resource, new ArrayList<>(items));
        this.originalList = new ArrayList<>(items);
        this.filteredList = new ArrayList<>(items);
    }

    public void setMatchMode(MatchMode mode) {
        this.matchMode = mode;
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public String getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    List<String> suggestions = new ArrayList<>();

                    if (constraint == null || constraint.length() == 0) {
                        suggestions.addAll(originalList);
                    } else {
                        String query = constraint.toString().toLowerCase();

                        for (String item : originalList) {
                            String itemLower = item.toLowerCase();

                            switch (matchMode) {
                                case STARTS_WITH:
                                    if (itemLower.startsWith(query)) {
                                        suggestions.add(item);
                                    }
                                    break;
                                case CONTAINS:
                                    if (itemLower.contains(query)) {
                                        suggestions.add(item);
                                    }
                                    break;
                                case ENDS_WITH:
                                    if (itemLower.endsWith(query)) {
                                        suggestions.add(item);
                                    }
                                    break;
                            }
                        }
                    }

                    results.values = suggestions;
                    results.count = suggestions.size();
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    filteredList.clear();
                    if (results.values != null) {
                        filteredList.addAll((List<String>) results.values);
                    }
                    notifyDataSetChanged();
                }
            };
        }
        return filter;
    }
}

