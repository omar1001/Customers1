package beta.customers;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import beta.customers.data.DBHandler;

/**
 * Created by omar on 3/19/16.
 *          Hello
 */
public class FragmentNewCustomer extends Fragment {
    public static String CUSTOMER = "customer";
    private static final int CAMERA_REQUEST = 1207;
    public static final String PHOTOS_DIRECTORY = "Customers_photos";
    public static final String COUNTER = "photo_name_counter";

    Customer customer = null;
    DBHandler mDBHandler;
    ImageView cameraResult;
    LocationManager locationManager;
    View rootView;
    ArrayAdapter<String> mSpinnerAdapter;
    SharedPreferences sharedPreferences;
    boolean isEditing = false;
    String editingPrevCode;
    String lastImagePath = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_newcustomer, container, false);

        if (getArguments() != null) {
            customer = (Customer) getArguments().getSerializable(CUSTOMER);
            ((EditText)rootView.findViewById(R.id.name_entry))
                    .setText(customer.name);
            ((EditText)rootView.findViewById(R.id.code_entry))
                    .setText(customer.code);
            ((EditText)rootView.findViewById(R.id.address_entry))
                    .setText(customer.address);
            ((EditText)rootView.findViewById(R.id.phone_entry))
                    .setText(customer.phone);
            ((TextView)rootView.findViewById(R.id.location_entry_result))
                    .setText(customer.locationLat + ", " + customer.locationLong);

            ((ImageView)rootView.findViewById(R.id.photo_entry_result))
                    .setImageBitmap(loadImageFromStorage(getActivity(),
                            customer.picturePath));

            isEditing = true;
            lastImagePath = customer.picturePath;
            editingPrevCode = customer.code;
        } else {
            customer = new Customer();
        }

        mDBHandler = new DBHandler(getActivity());

        Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner_entry);

        //Spinner Adapter
        List<String> array = mDBHandler.getLines();
        mSpinnerAdapter =
                new ArrayAdapter<>(
                        getActivity(), // The current context (this activity)
                        R.layout.spinner_item, // The name of the layout ID.
                        R.id.spinner_item,// The ID of the textview to populate.
                        array);
        spinner.setAdapter(mSpinnerAdapter);

        if(mSpinnerAdapter.getCount() == 0) {
            if(!isEditing)
                mSpinnerAdapter.add("No line");
            else
                mSpinnerAdapter.add(customer.line);
        }

        //Camera button listener & result showing
        Button captureButton = (Button) rootView.findViewById(R.id.photo_entry);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
        if(isEditing) {
            spinner.setSelection(mSpinnerAdapter.getPosition(customer.line));
        }
        cameraResult = (ImageView) rootView.findViewById(R.id.photo_entry_result);

        //Location listener
        Button locationButton = (Button) rootView.findViewById(R.id.location_entry);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager = (LocationManager) getActivity()
                        .getSystemService(Context.LOCATION_SERVICE);
                LocationListener locationListener = new LocationListener() {
                    public void onLocationChanged(Location location) {

                        customer.locationLat = Double.toString(location.getLatitude());
                        customer.locationLong = Double.toString(location.getLongitude());
                        ((TextView) rootView.findViewById(R.id.location_entry_result)).setText(
                                customer.locationLat + ", " + customer.locationLong
                        );
                        try {
                            locationManager.removeUpdates(this);
                        } catch(SecurityException e) {
                            Toast.makeText(getActivity(), "GPS permission disabled \n please enable it.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                    public void onStatusChanged(String provider, int  status, Bundle extras) {}
                    public void onProviderEnabled(String provider) {}
                    public void onProviderDisabled(String provider) {}
                };
                // Register the listener with the Location Manager to receive location updates
                try {
                    locationManager.
                            requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER, 1000, 0, locationListener
                            );
                } catch(SecurityException e) {
                    Toast.makeText(getActivity(), "GPS permission disabled \n please enable it.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
        );

        (rootView.findViewById(R.id.submit_entry))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        submitCustomerDataAndSaveToDB();
                    }
                });

        return rootView;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == getActivity().RESULT_OK) {
            Bitmap photoBm = (Bitmap) data.getExtras().get("data");
            customer.picturePath = saveImageToStorage(photoBm);
            cameraResult.setImageBitmap(loadImageFromStorage(getActivity(),
                    customer.picturePath));
        }
    }


    private String saveImageToStorage(Bitmap bitmapImage) {

        ContextWrapper cw = new ContextWrapper(getActivity());

        File directory = cw.getDir(PHOTOS_DIRECTORY, Context.MODE_PRIVATE);

        String imageName = getNameForNewImage();

        File photoPath = new File(directory, imageName);

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(photoPath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            if(lastImagePath != null) {
                deleteLastImageIfExists();
            }
            lastImagePath = imageName;

            return imageName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private boolean deleteLastImageIfExists() {
        ContextWrapper cw = new ContextWrapper(getActivity());

        File directory = cw.getDir(PHOTOS_DIRECTORY, Context.MODE_PRIVATE);

        return (new File(directory, lastImagePath)).delete();
    }

    private String getNameForNewImage() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String newName = null;
        if(sharedPreferences.contains(COUNTER)) {
            newName = sharedPreferences
                    .getString(COUNTER,
                            Integer.toString(1000));
            newName = Integer.toString(Integer.parseInt(newName) + 1);
            sharedPreferences.edit().putString(COUNTER, newName).apply();
            sharedPreferences.edit().apply();
        } else {
            newName = Integer.toString(1000);
            sharedPreferences.edit().putString(COUNTER, newName).apply();
        }

        return newName;
    }

    public static Bitmap loadImageFromStorage(Activity activity, String picutrePath) {
        Bitmap bm = null;
        try {
            ContextWrapper cw = new ContextWrapper(activity);
            File directory = cw.getDir(PHOTOS_DIRECTORY, Context.MODE_PRIVATE);
            File f = new File(directory, picutrePath);
            bm = BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bm;
    }

    public void submitCustomerDataAndSaveToDB() {

        customer.code = ((TextView) rootView.findViewById(R.id.code_entry))
                .getText().toString();
        customer.phone = ((TextView) rootView.findViewById(R.id.phone_entry))
                .getText().toString();
        customer.name = ((TextView) rootView.findViewById(R.id.name_entry))
                .getText().toString();
        customer.address = ((TextView) rootView.findViewById(R.id.address_entry))
                .getText().toString();
        customer.line = ((Spinner)rootView.findViewById(R.id.spinner_entry))
                .getSelectedItem().toString();

        if(!validInputs())
            return;

        if(isEditing) {
            mDBHandler.replaceCustomer(customer, editingPrevCode);
        } else {
            mDBHandler.addCustomer(customer);
        }

        ((Callback) getActivity())
                .viewCustomers();

    }

    private boolean validInputs() {

        if(customer.code.length() != 10) {
            toastInvalidInput("Code must be 10 characters");
            return false;
        }
        if(customer.name.length() == 0) {
            toastInvalidInput("No name entered!");
            return false;
        }
        if(customer.phone.length() == 0) {
            toastInvalidInput("No phone entered!");
            return false;
        }
        if(customer.address.length() == 0) {
            toastInvalidInput("No address entered!");
            return false;
        }

        if(!isEditing) {
            if(mDBHandler.codeIsFound(customer.code)) {
                toastInvalidInput("Code must be unique, " +
                        " another customer using this code.");
                return false;
            }
        }

        return true;
    }

    private void toastInvalidInput(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    public interface Callback {
        void viewCustomers();
    }

}
