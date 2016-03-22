package beta.customers;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by omar on 3/20/16.
 */
public class FragmentCustomer extends Fragment {
    Customer customer;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_customer, container, false);

        if(getArguments().containsKey(FragmentNewCustomer.CUSTOMER)) {
            customer = (Customer)getArguments()
                    .getSerializable(FragmentNewCustomer.CUSTOMER);

            ((TextView)rootView.findViewById(R.id.name_view))
                    .setText(customer.name);
            ((TextView)rootView.findViewById(R.id.code_view))
                    .setText(customer.code);
            ((TextView)rootView.findViewById(R.id.address_view))
                    .setText(customer.address);
            ((TextView)rootView.findViewById(R.id.phone_view))
                    .setText(customer.phone);
            ((TextView)rootView.findViewById(R.id.line_view))
                    .setText(customer.line);

            (rootView.findViewById(R.id.edit_customer_button))
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((Callback) getActivity())
                                    .editCustomer(customer);
                        }
                    });

            (rootView.findViewById(R.id.location_view))
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Uri geoLocation = Uri.parse("geo:" + customer.locationLat + "," +
                                    customer.locationLong);

                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(geoLocation);
                            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                                startActivity(intent);
                            } else {
                                Toast.makeText(getActivity(), "No app instaled to show maps",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            (rootView.findViewById(R.id.photo_view))
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Dialog settingsDialog = new Dialog(getActivity());
                            settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

                            View view = inflater.inflate(R.layout.image_viewer, null);
                            ((ImageView) view.findViewById(R.id.image_view_dialog))
                                    .setImageBitmap(FragmentNewCustomer.loadImageFromStorage(
                                            getActivity(), customer.picturePath
                                    ));

                            settingsDialog.setContentView(view);
                            settingsDialog.show();
                        }
                    });
            (rootView.findViewById(R.id.call_button))
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String uri = "tel:" + customer.phone.trim() ;
                            Intent intent = new Intent(Intent.ACTION_CALL);
                            intent.setData(Uri.parse(uri));
                            startActivity(intent);
                        }
                    });

        }

        return rootView;
    }

    public interface Callback {
        void editCustomer(Customer customer);
    }
}
