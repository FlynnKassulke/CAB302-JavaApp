package focusApp.models.timer;

import javax.sound.sampled.*;
import java.io.File;
import java.util.HashMap;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

public class Notification {
    private static final String VoiceDirectory = "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory";
    private static final String SoundDirectory = "src/main/resources/focusApp/Sounds/";
    private static Voice SpeakingVoice;

    private final File Alarm;
    private final float Volume;

    public static HashMap<String, File> SoundList = new HashMap<>(){{
        put("Alarm 1", new File(SoundDirectory + "alarm1.wav"));
        put("Alarm 2", new File(SoundDirectory + "alarm2.wav"));
        put("Alarm 3", new File(SoundDirectory + "alarm3.wav"));
    }};

    public Notification(String Alarm, float Volume){
        this.Alarm = SoundList.get(Alarm);
        this.Volume = Volume;
    }

    /**
     * Plays a sound of the supported audio format (wav).
     */
    public void PlaySound(){
        try {
            if (Alarm.exists()){

                // Get the audio from the file.
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(Alarm);
                Clip alarmClip = AudioSystem.getClip();
                alarmClip.open(audioInput);

                // Volume control.
                FloatControl VolumeControl = (FloatControl) alarmClip.getControl(FloatControl.Type.MASTER_GAIN);
                VolumeControl.setValue(Volume);

                // Play the audio.
                alarmClip.start();

            } else {
              System.out.println("Alarm File Not Found!");
            }
        } catch (Exception e){
            System.out.println(e);
        }
    }

    public static void SpeakText(String TextToSpeak){

        Thread SpeakThread = new Thread(() -> {
            if (SpeakingVoice != null){ SpeakingVoice.deallocate(); }
            System.setProperty("freetts.voices", VoiceDirectory);
            VoiceManager VM = VoiceManager.getInstance();
            SpeakingVoice = VM.getVoice("kevin16");

            SpeakingVoice.allocate();
            SpeakingVoice.speak(TextToSpeak);
            SpeakingVoice.deallocate();
        });
        SpeakThread.start();
    }
}
