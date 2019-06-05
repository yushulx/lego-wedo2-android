package dk.lego.demo.service;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import dk.lego.demo.R;
import dk.lego.demo.utils.ConnectedDeviceHelper;
import dk.lego.devicesdk.services.PiezoTonePlayer;
import dk.lego.devicesdk.utils.ByteUtils;

public class ServicePiezoDetailsFragment extends ServiceBaseFragment {

    private static final int PIEZO_DEFAULT_FREQUENCY = 10;
    private static final int PIEZO_DEFAULT_DURATION = 100;

    private EditText editTextFrequency;
    private EditText editTextDuration;
    private Button buttonStartPlaying;
    private Button buttonStartPlayingSequence;
    private Button buttonStopPlaying;
    private Button buttonPlayNoteC;
    private Button buttonPlayNoteD;
    private Button buttonPlayNoteE;
    private Button buttonPlayNoteF;
    private Button buttonPlayNoteG;
    private Button buttonPlayNoteA;

    private Handler sequenceHandler;

    private PiezoTonePlayer piezoTonePlayer;
    private Runnable playSequenceRunnable = new Runnable() {
        @Override
        public void run() {
            piezoTonePlayer.playFrequency(currentFrequency, 0);
            currentFrequency += frequencyJump;
            if (currentFrequency > PiezoTonePlayer.PIEZO_TONE_MAX_FREQUENCY) {
                currentFrequency = 10;
            }

            sequenceHandler.postDelayed(playSequenceRunnable, duration);
        }
    };

    private int frequencyJump;
    private int currentFrequency;
    private Integer duration;

    public static Fragment newInstance() {
        return new ServicePiezoDetailsFragment();
    }

    public ServicePiezoDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Add motor specific details
        ViewGroup content = (ViewGroup) rootView.findViewById(R.id.da_content_frame);
        content.addView(inflater.inflate(R.layout.da_fragment_service_piezo_details, container, false));

        editTextFrequency = (EditText) rootView.findViewById(R.id.da_service_details_piezo_frequenzy);
        editTextFrequency.setText(String.valueOf(PIEZO_DEFAULT_FREQUENCY));
        editTextDuration = (EditText) rootView.findViewById(R.id.da_service_details_piezo_duration);
        editTextDuration.setText(String.valueOf(PIEZO_DEFAULT_DURATION));


        buttonStartPlaying = (Button) rootView.findViewById(R.id.da_service_details_piezo_button_play);
        buttonStartPlayingSequence = (Button) rootView.findViewById(R.id.da_service_details_piezo_button_sequence);
        buttonStopPlaying = (Button) rootView.findViewById(R.id.da_service_details_piezo_button_stop);

        buttonPlayNoteC = (Button) rootView.findViewById(R.id.da_service_details_piezo_button_note_c);
        buttonPlayNoteD = (Button) rootView.findViewById(R.id.da_service_details_piezo_button_note_d);
        buttonPlayNoteE = (Button) rootView.findViewById(R.id.da_service_details_piezo_button_note_e);
        buttonPlayNoteF = (Button) rootView.findViewById(R.id.da_service_details_piezo_button_note_f);
        buttonPlayNoteG = (Button) rootView.findViewById(R.id.da_service_details_piezo_button_note_g);
        buttonPlayNoteA = (Button) rootView.findViewById(R.id.da_service_details_piezo_button_note_a);

        sequenceHandler = new Handler();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        piezoTonePlayer = (PiezoTonePlayer) ConnectedDeviceHelper.getInstance().getService();

        buttonStartPlaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                piezoTonePlayer.playFrequency(Integer.parseInt(editTextFrequency.getText().toString()), Integer.parseInt(editTextDuration.getText().toString()));
            }
        });

        buttonStartPlayingSequence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sequenceHandler.removeCallbacks(playSequenceRunnable);

                duration = Integer.valueOf(editTextDuration.getText().toString());

                if (duration <= 0) {
                    Toast.makeText(getActivity(), "Cannot play sequence with duration 0", Toast.LENGTH_LONG).show();
                    return;
                }

                frequencyJump = Integer.parseInt(editTextFrequency.getText().toString());
                currentFrequency = 10;

                sequenceHandler.post(playSequenceRunnable);
            }
        });

        buttonStopPlaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sequenceHandler.removeCallbacks(playSequenceRunnable);
                piezoTonePlayer.stopPlaying();
            }
        });

        initializePianoButton(buttonPlayNoteC, PiezoTonePlayer.PiezoTonePlayerNote.PIEZO_NOTE_C);
        initializePianoButton(buttonPlayNoteD, PiezoTonePlayer.PiezoTonePlayerNote.PIEZO_NOTE_D);
        initializePianoButton(buttonPlayNoteE, PiezoTonePlayer.PiezoTonePlayerNote.PIEZO_NOTE_E);
        initializePianoButton(buttonPlayNoteF, PiezoTonePlayer.PiezoTonePlayerNote.PIEZO_NOTE_F);
        initializePianoButton(buttonPlayNoteG, PiezoTonePlayer.PiezoTonePlayerNote.PIEZO_NOTE_G);
        initializePianoButton(buttonPlayNoteA, PiezoTonePlayer.PiezoTonePlayerNote.PIEZO_NOTE_A);
    }

    @Override
    protected void didUpdateValueData(byte[] newValue) {
        textServiceValue.setText(ByteUtils.toHexString(newValue));
    }

    private void initializePianoButton(final Button pianoButton, final PiezoTonePlayer.PiezoTonePlayerNote note) {
        pianoButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    piezoTonePlayer.playNote(note, 4, 0);
                    view.setPressed(true);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                    piezoTonePlayer.stopPlaying();
                    view.setPressed(false);
                }
                return true;
            }
        });
    }
}