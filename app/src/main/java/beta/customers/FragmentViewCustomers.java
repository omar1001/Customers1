package beta.customers;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Comparator;

import beta.customers.data.DBHandler;

/**
 * Created by omar on 3/18/16.
 */
public class FragmentViewCustomers extends Fragment{

    private View rootView;
    private DBHandler mDBHandler;
    ListView listViewCustomers;
    CustomersAdapter customersAdapter;
    boolean codeIsIncreasing = false;
    boolean nameIsIncreasing = false;
    boolean lineIsIncreasing = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_view_customers_layout, container, false);

        listViewCustomers = (ListView) rootView.findViewById(R.id.Customers_listView);
        customersAdapter = new CustomersAdapter(getActivity());
        listViewCustomers.setAdapter(customersAdapter);

        mDBHandler = new DBHandler(getActivity());

        customersAdapter.addAll(mDBHandler.getCustomers());

        listViewCustomers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((Callback) getActivity())
                        .showCustomerDetails(customersAdapter.getItem(position));
            }
        });

        (rootView.findViewById(R.id.new_customer_view_customers))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((Callback) getActivity())
                                .newCustomer();
                    }
                });

        (rootView.findViewById(R.id.code_sort_button))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customersAdapter.sort(new Comparator<Customer>() {
                            @Override
                            public int compare(Customer x, Customer y) {
                                if(codeIsIncreasing)
                                    return x.code.compareTo(y.code);
                                else
                                    return y.code.compareTo(x.code);
                            }
                        });
                        codeIsIncreasing = !codeIsIncreasing;
                    }
                });

        (rootView.findViewById(R.id.name_sort_button))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customersAdapter.sort(new Comparator<Customer>() {
                            @Override
                            public int compare(Customer x, Customer y) {
                                if(nameIsIncreasing)
                                    return x.name.compareTo(y.name);
                                else
                                    return y.name.compareTo(x.name);
                            }
                        });
                        nameIsIncreasing = !nameIsIncreasing;
                    }
                });

        (rootView.findViewById(R.id.line_sort_button))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customersAdapter.sort(new Comparator<Customer>() {
                            @Override
                            public int compare(Customer x, Customer y) {
                                if(lineIsIncreasing)
                                    return x.line.compareTo(y.line);
                                else
                                    return y.line.compareTo(x.line);
                            }
                        });
                        lineIsIncreasing = !lineIsIncreasing;
                    }
                });

        return rootView;
    }

    public interface Callback {
        void showCustomerDetails(Customer customer);
        void newCustomer();
    }

    public class CustomersAdapter extends ArrayAdapter<Customer> {

        public CustomersAdapter(Activity context){
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = convertView;
            if(view == null) {
                LayoutInflater inflater = (LayoutInflater) getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.customer_listview_item, null);
            }
            ((TextView) view.findViewById(R.id.customer_code))
                    .setText(getItem(position).code);
            ((TextView) view.findViewById(R.id.customer_name))
                    .setText(getItem(position).name);
            ((TextView) view.findViewById(R.id.customer_line))
                    .setText(getItem(position).line);

            return view;
        }
    }

}
