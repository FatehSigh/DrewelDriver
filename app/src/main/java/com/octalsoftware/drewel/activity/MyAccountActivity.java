package com.octalsoftware.drewel.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.octalsoftware.drewel.AppDelegate;
import com.octalsoftware.drewel.R;
import com.octalsoftware.drewel.application.DrewelApplication;
import com.octalsoftware.drewel.constant.Tags;
import com.octalsoftware.drewel.fragment.Adapter.CountryListAdapter;
import com.octalsoftware.drewel.interfaces.OnClickItemListener;
import com.octalsoftware.drewel.model.CountryModel;
import com.octalsoftware.drewel.model.UserDataModel;
import com.octalsoftware.drewel.retrofitService.ApiConstant;
import com.octalsoftware.drewel.retrofitService.ExecuteService;
import com.octalsoftware.drewel.retrofitService.RequestModel;
import com.octalsoftware.drewel.retrofitService.ResponseInterface;
import com.octalsoftware.drewel.retrofitService.RestError;
import com.octalsoftware.drewel.utils.Prefs;
import com.octalsoftware.drewel.utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import pl.aprilapps.easyphotopicker.EasyImage;

public class MyAccountActivity extends AppCompatActivity implements ResponseInterface, OnClickItemListener {
    @BindView(R.id.btn_save)
    Button btn_save;
    private Toolbar mToolbar;

    @BindView(R.id.txt_firstname)
    AppCompatEditText txt_firstname;
    @BindView(R.id.txt_lastname)
    AppCompatEditText txt_lastname;
    @BindView(R.id.et_email)
    AppCompatTextView et_email;
    @BindView(R.id.txt_country_code)
    AppCompatTextView txt_country_code;
    @BindView(R.id.et_phone)
    AppCompatTextView et_phone;

    @BindView(R.id.txt_country)
    AppCompatTextView txt_country;
    @BindView(R.id.et_zipcode)
    AppCompatEditText et_zipcode;
    @BindView(R.id.img_edit)
    AppCompatImageView img_edit;
    @BindView(R.id.civ_userimg)
    CircleImageView civ_userimg;
    @BindView(R.id.et_vehicle_number)
    AppCompatEditText et_vehicle_number;
    Prefs prefs;
    private String countryCode;
    boolean isShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//            Window window = getWindow();
//            Drawable background = getResources().getDrawable(R.color.white);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(getResources().getColor(android.R.color.transparent));
//            window.setBackgroundDrawable(background);
//        }
        setContentView(R.layout.my_account);
        ButterKnife.bind(this);
        prefs = new Prefs(this);
        initView();
        callCountriesListApi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppDelegate.Companion.setLocale(new Prefs(this).getDefaultLanguage(), this);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setData();
       /* tv_status.setText(getText(R.string.accepted));
        btn_decline_order.setVisibility(View.GONE);
        btn_accept_order.setText(getText(R.string.pickup_from_vendor));*/
    }

    private void setData() {
        txt_firstname.setText(Objects.requireNonNull(prefs.getUserdata()).first_name);
        txt_lastname.setText(prefs.getUserdata().last_name);
        txt_country_code.setText(prefs.getUserdata().country_code);
//        txt_country.setText(prefs.getUserdata().country_code);
        et_phone.setText(prefs.getUserdata().mobile_number);
        et_email.setText(prefs.getUserdata().email);
        et_vehicle_number.setText(prefs.getUserdata().vehicle_number);
        ImageLoader.getInstance().displayImage(prefs.getUserdata().img, civ_userimg, DrewelApplication.options);
    }

    @OnClick({R.id.btn_save, R.id.cv_change_password, R.id.btn_change_password, R.id.img_edit, R.id.txt_country})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_save:
                if (!AppDelegate.Companion.isValidString(txt_firstname.getText().toString())) {
                    AppDelegate.Companion.showToast(this, getString(R.string.please_enter_firstname));
                } else if (!AppDelegate.Companion.isValidString(txt_lastname.getText().toString())) {
                    AppDelegate.Companion.showToast(this, getString(R.string.please_enter_lastname));
                } else if (!AppDelegate.Companion.isValidString(et_vehicle_number.getText().toString())) {
                    AppDelegate.Companion.showToast(this, getString(R.string.please_enter_vehicle_number));
                } else if (!AppDelegate.Companion.haveNetworkConnection(this)) {
                    AppDelegate.Companion.showToast(this, getString(R.string.please_check_your_internet_connection));
                } else
                    callSaveDetailApi();
                break;
            case R.id.img_edit:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 10);
                } else {
                    selectImage();
                }
                break;
//            case R.id.txt_country_code:
//                isShow = true;
//                if (countryModelList != null && countryModelList.size() > 0)
//                    showCountryCodedialog(isShow);
//                else callCountriesListApi();
//                break;
            case R.id.txt_country:
                isShow = true;
                if (countryModelList != null && countryModelList.size() > 0)
                    showCountrydialog(isShow);
                else callCountriesListApi();
                break;
            case R.id.btn_change_password:
                startActivity(new Intent(this, ChangePasswordActivity.class));
                break;
            case R.id.cv_change_password:
                startActivity(new Intent(this, ChangePasswordActivity.class));
                break;
        }
    }

    private void selectImage() {

        final CharSequence[] items = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    cameraIntent();
                } else if (items[item].equals("Choose from Gallery")) {
                    galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent() {
        EasyImage.openCamera(this, ApiConstant.CAMERA_REQUEST);
    }

    private void galleryIntent() {
        EasyImage.openGallery(this, ApiConstant.GALARY_REQUEST);
    }

    private Uri imageURI;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new EasyImage.Callbacks() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                System.out.print("error");
            }

            @Override
            public void onImagesPicked(@NonNull List<File> imageFiles, EasyImage.ImageSource source, int type) {
                String imagePath = Utils.getUtils().getCompressImagePath(Uri.fromFile(imageFiles.get(0)), MyAccountActivity.this);
                File file = new File(imagePath);
                imageURI = Uri.fromFile(file);
                civ_userimg.setImageURI(imageURI);
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            selectImage();
        }
    }

    private void callCountriesListApi() {
        if (isShow)
            AppDelegate.Companion.showProgressDialog(this);
        HashMap<String, String> paramsHashMap = new HashMap<String, String>();
        paramsHashMap.put(Tags.email, et_email.getText().toString());
//        paramsHashMap.put(Tags.password, et_password.getText().toString());
        paramsHashMap.put(Tags.language, new Prefs(this).getDefaultLanguage());
        paramsHashMap.put(Tags.device_id, new Prefs(this).getFcMtokeninTemp());
        paramsHashMap.put(Tags.device_type, "android");

        RequestModel requestModel = new RequestModel();
        requestModel.setWebServiceName(ApiConstant.countries_list);
        requestModel.setWebServiceTag(ApiConstant.countries_list);
        requestModel.setWebServiceType(ApiConstant.POST);
        requestModel.setParamsHashmap(paramsHashMap);
        new ExecuteService().execute(this, true, this, requestModel);
    }

    private void callSaveDetailApi() {

        AppDelegate.Companion.showProgressDialog(this);
        HashMap<String, String> paramsHashMap = new HashMap<String, String>();
        HashMap<String, File> fileHashMap = new HashMap<String, File>();
//        paramsHashMap.put(Tags.email, et_email.getText().toString());
        paramsHashMap.put(Tags.user_id, Objects.requireNonNull(prefs.getUserdata()).user_id);
        paramsHashMap.put(Tags.language, new Prefs(this).getDefaultLanguage());
        paramsHashMap.put(Tags.vehicle_number, et_vehicle_number.getText().toString());
        paramsHashMap.put(Tags.first_name, txt_firstname.getText().toString());
        paramsHashMap.put(Tags.last_name, txt_lastname.getText().toString());
        if (imageURI != null) {
            File file = new File(Objects.requireNonNull(imageURI.getPath()));
            fileHashMap.put(Tags.image, file);
        }

        RequestModel requestModel = new RequestModel();
        requestModel.setWebServiceName(ApiConstant.edit_delivery_boy_profile);
        requestModel.setWebServiceTag(ApiConstant.edit_delivery_boy_profile);
        requestModel.setWebServiceType(ApiConstant.POST);
        requestModel.setParamsHashmap(paramsHashMap);
        requestModel.setQueryFiles(fileHashMap);
        new ExecuteService().execute(this, true, this, requestModel);
    }

    @Override
    public void onNoNetwork(String message, String webServiceTag) {
        AppDelegate.Companion.hideProgressDialog(this);
        AppDelegate.Companion.showToast(this, message);
    }

    List<CountryModel> countryModelList;

    @Override
    public void onSuccess(String message, String webServiceTag, String successMsg) {
        AppDelegate.Companion.hideProgressDialog(this);
        switch (webServiceTag) {
            case ApiConstant.countries_list:
                Type listType = new TypeToken<List<CountryModel>>() {
                }.getType();
                countryModelList = new Gson().fromJson(message, listType);
//                AppDelegate.Companion.showSnackBar(et_email, successMsg);
                showCountrydialog(isShow);
                break;
            case ApiConstant.edit_delivery_boy_profile:
                AppDelegate.Companion.hideProgressDialog(this);
                AppDelegate.Companion.LogT("Response ==>" + message);
                UserDataModel userDataModel = new Gson().fromJson(message, UserDataModel.class);
                AppDelegate.Companion.LogT("userDataModel==>" + userDataModel);
                AppDelegate.Companion.showToast(this, successMsg);
                new Prefs(this).setUserdata(userDataModel);
                finish();
                break;
        }
    }

    @Override
    public void onFailure(String message, String webServiceTag) {
        AppDelegate.Companion.hideProgressDialog(this);
        AppDelegate.Companion.showToast(this, message);
    }

    @Override
    public void onFailureRetro(RestError message, String webServiceTag) {
        AppDelegate.Companion.hideProgressDialog(this);
        AppDelegate.Companion.showToast(this, message.getstrMessage());
    }

    Dialog dialogue;

    void dialogsearch() {
        dialogue = new Dialog(this);
        dialogue.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogue.setContentView(R.layout.country_dialog);
        Objects.requireNonNull(dialogue.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public CountryListAdapter countryAdapter;
//    public CountryCodeListAdapter countryCodeAdapter;

    private void showCountrydialog(Boolean isShow) {
        if (countryModelList != null && countryModelList.size() > 0) {
            dialogsearch();
            RecyclerView dialog_list = dialogue.findViewById(R.id.dialog_list);
            AppCompatEditText et_search = dialogue.findViewById(R.id.et_search);
            dialog_list.setLayoutManager(new LinearLayoutManager(this));
            dialog_list.setHasFixedSize(true);
            dialog_list.setItemAnimator(new DefaultItemAnimator());
            countryAdapter = new CountryListAdapter(this, this, countryModelList);
            dialog_list.setAdapter(countryAdapter);

            et_search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    countryAdapter.filter(et_search.getText().toString());
                }
            });
            if (isShow)
                if (dialogue != null) {
                    dialogue.dismiss();
                    dialogue.show();
                }
        }
    }

//    private void showCountryCodedialog(Boolean isShow) {
//        if (countryModelList != null && countryModelList.size() > 0) {
//            dialogsearch();
//            RecyclerView dialog_list = dialogue.findViewById(R.id.dialog_list);
//            AppCompatEditText et_search = dialogue.findViewById(R.id.et_search);
//            dialog_list.setLayoutManager(new LinearLayoutManager(this));
//            dialog_list.setHasFixedSize(true);
//            dialog_list.setItemAnimator(new DefaultItemAnimator());
//            countryCodeAdapter = new CountryCodeListAdapter(this, this, countryModelList);
//            dialog_list.setAdapter(countryCodeAdapter);
//
//            et_search.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                }
//
//                @Override
//                public void afterTextChanged(Editable editable) {
//                    countryAdapter.filter(et_search.getText().toString());
//                }
//            });
//            if (isShow)
//                if (dialogue != null) {
//                    dialogue.dismiss();
//                    dialogue.show();
//                }
//        }
//    }

    @Override
    public void setOnItemClick(@NotNull String tag, int position) {
        if (tag.equals(Tags.country)) {
            for (CountryModel cityItems : countryModelList) {
                if (cityItems.name.equals(countryAdapter.getItem(position).name + "")) {
                    AppDelegate.Companion.LogT("Clicked item ==>" + cityItems);
                    txt_country.setText(cityItems.name);
//                    countryCode = cityItems.id;
                    if (dialogue != null && dialogue.isShowing()) {
                        dialogue.dismiss();
                    }
                    break;
                }
            }
        }/* else if (tag.equals(Tags.country_code)) {
            for (CountryModel cityItems : countryModelList) {
                if (cityItems.name.equals(countryAdapter.getItem(position).name + "")) {
                    AppDelegate.Companion.LogT("Clicked item ==>" + cityItems);
                    txt_country_code.setText("+" + cityItems.phonecode);
                    countryCode = cityItems.id;
                    if (dialogue != null && dialogue.isShowing()) {
                        dialogue.dismiss();
                    }
                    break;
                }
            }

        }*/
    }
}
