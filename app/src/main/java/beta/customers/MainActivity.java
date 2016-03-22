package beta.customers;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements FragmentNewCustomer.Callback,
                                                                FragmentViewCustomers.Callback,
                                                                 FragmentCustomer.Callback   {
    private String FRAGMENT_NEW_CUSTOMER = "1";
    private String FRAGMENT_VIEW_CUSTOMERS = "2";
    private String FRAGMENT_LINES_VIEW = "3";
    private String FRAGMENT_CUSTOMER = "4";
    private String MAIN_FRAGMENT = "0";

    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState == null) {
            mainFragment();
        }

//        (new DBHandler(this)).removeTables();

    }

    @Override
    public void onBackPressed() {
        if(getFragmentManager().findFragmentByTag(FRAGMENT_CUSTOMER)
                != null) {
            viewCustomers();
        } else if (getFragmentManager().findFragmentByTag(MAIN_FRAGMENT)
                == null) {
            mainFragment();
        } else {
            super.onBackPressed();
        }
    }

    public void mainFragment() {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new MainFragment(), MAIN_FRAGMENT)
                .commit();
    }

    public void lineFragment() {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new FragmentLinesView(), FRAGMENT_LINES_VIEW)
                .commit();
    }

    @Override
    public void newCustomer() {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new FragmentNewCustomer(), FRAGMENT_NEW_CUSTOMER)
                .commit();
    }

    @Override
    public void viewCustomers() {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new FragmentViewCustomers(), FRAGMENT_VIEW_CUSTOMERS)
                .commit();
    }

    @Override
    public void showCustomerDetails(Customer customer) {
        FragmentCustomer fc = new FragmentCustomer();
        Bundle args = new Bundle();
        args.putSerializable(FragmentNewCustomer.CUSTOMER, customer);
        fc.setArguments(args);

        getFragmentManager().beginTransaction()
                .replace(R.id.container, fc, FRAGMENT_CUSTOMER)
                .commit();
    }

    @Override
    public void editCustomer(Customer customer) {
        FragmentNewCustomer fn = new FragmentNewCustomer();

        Bundle bundle = new Bundle();
        bundle.putSerializable(FragmentNewCustomer.CUSTOMER, customer);

        fn.setArguments(bundle);

        getFragmentManager().beginTransaction()
                .replace(R.id.container, fn)
                .commit();

    }
}
