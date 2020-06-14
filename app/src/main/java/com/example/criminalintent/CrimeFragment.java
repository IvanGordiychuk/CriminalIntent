package com.example.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
// Create class CrimeFragment extends Fragment
//with support lib v4.app.Fragment
public class CrimeFragment extends Fragment {

    private static final String TAG="CrimeFragment";
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final DateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mTimeButton;


    public static CrimeFragment newInstance(UUID crimeId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
            //новый экземпляр CrimeFragment
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);

        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime=CrimeLab.get(getActivity()).getCrime(crimeId);

    }
    @Override
    public void onPause(){
        super.onPause();
        CrimeLab.get(getActivity())
                .updateCrime(mCrime);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK){
            Log.i(TAG,"requestCode "+requestCode+" != "+Activity.RESULT_OK+" Activity.RESULT_OK");
           // mDateButton.setText("result_ok");
            return;
        }
        if (requestCode == REQUEST_DATE){
            Log.i(TAG,"requestCode "+requestCode+" == "+REQUEST_DATE+" REQUEST_DATE");
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
           // mDateButton.setText(DateFormat.getDateInstance(DateFormat.FULL).format(mCrime.getDate()));
            updateDate();
        }
        if (requestCode ==REQUEST_TIME){
            Date time=(Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            Log.i(TAG,time.getHours()+time.getMinutes()+" time");
            mCrime.setDate(time);
          // mTimeButton.setText(mCrime.getDate().getHours()+" Hours "+mCrime.getDate().getMinutes()+" Minutes");
           updateTime();
        }
    }

    private void updateDate() {
        Log.i(TAG,"updateDate");
        mDateButton.setText(DateFormat.getDateInstance(DateFormat.FULL).format(mCrime.getDate()));

    }
    private void updateTime(){
        Log.i(TAG,"updateTime");
        mTimeButton.setText(timeFormat.format(mCrime.getDate()));;
    }

    //
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_crime,container,false);//реализация заполняет разметку fragment_crime

        mTitleField=(EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //free space
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                //free space
            }
        });
        //обработка кнопки даты
        mDateButton=(Button) v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setText(DateFormat.getDateInstance(DateFormat.FULL).format(mCrime.getDate()));
        //блокировка кнопки даты
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);

            }
        });
        getArguments();
        //обработка кнопки вермени
        mTimeButton=(Button) v.findViewById(R.id.crime_time);
        mTimeButton.setText("time");
        updateTime();
        mTimeButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                FragmentManager managerTime = getFragmentManager();

              TimePickerFragment dialogTime = TimePickerFragment
                      .newInstance(mCrime.getDate());
              dialogTime.setTargetFragment(CrimeFragment.this,REQUEST_TIME);
              dialogTime.show(managerTime,DIALOG_TIME);
            }
        });
        //обработка чекбокса
        mSolvedCheckBox=(CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });




        return v;
    }
}
