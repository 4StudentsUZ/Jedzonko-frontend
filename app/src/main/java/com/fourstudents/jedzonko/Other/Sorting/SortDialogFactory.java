package com.fourstudents.jedzonko.Other.Sorting;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.RadioButton;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.fourstudents.jedzonko.R;

public class SortDialogFactory {
    public static AlertDialog getSortDialog(Context context, LayoutInflater inflater, SortProperty sortProperty, SortOrder sortOrder, SortListener sortListener) {
        androidx.appcompat.app.AlertDialog.Builder alertBuilder = new androidx.appcompat.app.AlertDialog.Builder(context);

        alertBuilder.setTitle(R.string.dialog_sort_title);
        final ConstraintLayout layout = (ConstraintLayout) inflater.inflate(R.layout.dialog_sort, null);
        alertBuilder.setView(layout);

        RadioButton sortNothingBtn = layout.findViewById(R.id.sortNothing);
        RadioButton sortTitleBtn = layout.findViewById(R.id.sortTitle);
        RadioButton sortRatingBtn = layout.findViewById(R.id.sortRating);
        RadioButton sortCreationDateBtn = layout.findViewById(R.id.sortCreationDate);
        RadioButton sortAscendingBtn = layout.findViewById(R.id.sortAscending);
        RadioButton sortDescendingBtn = layout.findViewById(R.id.sortDescending);

        switch (sortProperty) {
            case Nothing:
                sortNothingBtn.toggle();
                break;
            case Title:
                sortTitleBtn.toggle();
                break;
            case Rating:
                sortRatingBtn.toggle();
                break;
            case CreationDate:
                sortCreationDateBtn.toggle();
                break;
        }

        switch (sortOrder) {
            case Ascending:
                sortAscendingBtn.toggle();
                break;
            case Descending:
                sortDescendingBtn.toggle();
                break;
        }

        alertBuilder.setPositiveButton(R.string.confirm, (dialog, which) -> {
            SortProperty newSortProperty = SortProperty.Nothing;
            if (sortNothingBtn.isChecked()) newSortProperty = SortProperty.Nothing;
            else if (sortTitleBtn.isChecked()) newSortProperty = SortProperty.Title;
            else if (sortRatingBtn.isChecked()) newSortProperty = SortProperty.Rating;
            else if (sortCreationDateBtn.isChecked()) newSortProperty = SortProperty.CreationDate;

            SortOrder newSortOrder = SortOrder.Ascending;
            if (sortAscendingBtn.isChecked()) newSortOrder = SortOrder.Ascending;
            else if (sortDescendingBtn.isChecked()) newSortOrder = SortOrder.Descending;

            sortListener.onSortingChanged(newSortProperty, newSortOrder);
        });

        alertBuilder.setNegativeButton(R.string.clear, (dialog, which) -> {
            sortListener.onSortingChanged(SortProperty.Nothing, SortOrder.Ascending);
        });

        return alertBuilder.show();
    }
}
