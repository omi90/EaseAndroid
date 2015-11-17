# EaseAndroid
This is an android library. 
With this you can validate forms in android very ease, 
also a listview,gridview or form can be loaded dynamically from url that returns json array.


#Validate and Submit a Form
```
Validator validator = new Validator(context);
validator.setmValidateInterface(this);
submit.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        validator.validateAndSubmit(formContainerView, "form_submit_url");
    }
});
```

1. Create an instance of Validator Class-:<br/>
  ```
    Validator validator = new Validator(context);
  ```
2. Set Validator Interface as current class Validator will return result at validation success or form submitted via interface-:<br/>
  ```
    validator.setmValidateInterface(this);
  ```
3. set onClickListener to form submit button and call validate and submit function with container view of form ex. LinearLayout or RelativeLayout-:
  
  ```
      submit.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              validator.validateAndSubmit(formContainerView, "form_submit_url");
          }
      });
  ```
4. One More thing is to define validations in Layout XML, something like below-:
  
  ```
    <EditText
        android:id="@+id/lname"
        android:tag="vs_length_3_10_ve_hts_lname_hte_"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />
    <EditText
        android:id="@+id/phone"
        android:tag="vs_phone_ve_hts_phone_hte_"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />
    <EditText
        android:id="@+id/email"
        android:tag="vs_email_ve_hts_email_hte_"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />
  ```
  
Validations and string for submitting value in post variable are defined in tag attribute.<br/>
Validation String starts with *vs_* and ends with *_ve_* .<br/>
Post Variable name starts with *hts_* and ends with *_hte_* .<br/>
Currently supported validations are length check, email, phone, number, regular expression.
Any Java Style Regular expression will work.

#Loading a listview from URL

Loading a listview is also quite easy.
```
ListViewLoader  listviewloader = new ListViewLoader(mContext);
listviewloader.loadFromURL(urlString,imageBaseURL,postParams,listView,itemLayout,rIdClass,isPaginate,pageParamName);
```

Lets take all arguments one by one-<br/>

<b>urlString</b> is url from where u will get json as array of items.<br/>
<b>imageBaseURL</b> is base url or folder where images are store as default , ignore if using full image url path in json array.<br/>
<b>postParams</b> is additional parameters to pass url if required otherwise null.<br/>
<b>listView</b> is listview object of the layout.<br/>
<b>itemLayout</b> is resource identifier of layout of row item of listview (ex. R.layout.row_item_view)<br/>
<b>rIdClass</b> is class of your resource id (R.id) class (ex. R.id.class)<br/>
<b>isPaginate</b> is true if using pagination in sending json Array otherwise false.<br/>
<b>pageParamName</b> is parameter name for telling on which page we are currently on, more content will be loaded automatically on reaching bottom of the page.<br/>


Same can be done for GridView and Android layout forms.
