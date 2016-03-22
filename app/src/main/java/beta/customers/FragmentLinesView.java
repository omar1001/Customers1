package beta.customers;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import beta.customers.data.DBHandler;

/**
 * Created by omar on 3/21/16.
 * Hello
 */
public class FragmentLinesView extends Fragment {

    DBHandler mDBHandler;
    ArrayAdapter arrayAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_line_view, null);

        mDBHandler = new DBHandler(getActivity());

        ListView lineListView = (ListView)rootView.findViewById(R.id.line_listview);

        arrayAdapter =
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.line_item, // The name of the layout ID.
                        R.id.line_item,// The ID of the textview to populate.
                        new ArrayList<String>());
        lineListView.setAdapter(arrayAdapter);

        updateLinesListView();

        (rootView.findViewById(R.id.add_line))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialogForInput();
                    }
                });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void updateLinesListView() {
        arrayAdapter.clear();
        arrayAdapter.addAll(mDBHandler.getLines());
    }

    private void showDialogForInput() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Line");
        int maxLength = 50;
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);

        final EditText input = new EditText(getActivity());
        input.setFilters(fArray);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDBHandler.addLine(input.getText().toString());
                updateLinesListView();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
