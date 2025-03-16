package focusApp.models.timer;

import focusApp.controllers.BlockedController;
import focusApp.controllers.TimerController;
import focusApp.database.UserDAO;
import focusApp.models.user.*;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This class creates a timer and the methods to control it.
 */
public class Timer {
    public boolean Speak = false;

    /**
     * Need the Current state the timer is in?
     * @return The Current state of the time.
     */
    public TimerState getTimerState() {
        return Timer_State;
    }
    private static TimerState Timer_State;


    // Main information needed for the timer.
    private LocalTime Timer_End;
    private LocalTime Timer_Start;
    private final TimerController Timer_Controller;



    /**
     * Stores the value of the timer offset.
     */
    private final double Timer_Offset;

    /**
     * Stores the value of the timer duration.
     */
    private final double Timer_Duration;

    /**
     * Global scheduler for the timer.
     */
    private static ScheduledExecutorService TimeScheduler;

    /**
     * The duration that is left on a timer when the user pauses it.
     */
    private Duration Paused_Counting_Duration;


    /**
     * The duration form current time to start time (Delayed), can be altered.
     */
    private Duration Delayed_Counting_Duration;
    /**
     * The duration form current time to start time (Delayed), can not be altered.
     */
    private Duration Delayed_Total_Duration;

    /**
     * Get the Delayed Counting Duration in Milliseconds.
     * @return Delayed Counting Duration in Milliseconds.
     */
    public double getDCDinMS(){
        return Delayed_Counting_Duration.toMillis();
    }

    /**
     * Get the Delayed Total Duration in Milliseconds.
     * @return Delayed Total Duration in Milliseconds.
     */
    public double getDTDinMS(){
        return Delayed_Total_Duration.toMillis();
    }

    /**
     * The duration from start time to end time (Running), can be altered.
     */
    public Duration Running_Counting_Duration;
    /**
     * The duration from start time to end time (Running), can not be altered.
     */
    private Duration Running_Total_Duration;

    /**
     * Get the Running Counting Duration in Milliseconds.
     * @return Running Counting Duration in Milliseconds.
     */
    public double getRCDinMS(){
        return Running_Counting_Duration.toMillis();
    }
    /**
     * Get the Running Total Duration in Milliseconds.
     * @return Running Total Duration in Milliseconds.
     */
    public double getRTDinMS(){
        return Running_Total_Duration.toMillis();
    }


    /**
     * The format of time as 12 hour time(hh:mm:ss a).
     */
    private final DateTimeFormatter Timer_12_Format = DateTimeFormatter.ofPattern("h:mm:ss a");
    private final Notification Alarm;


    /**
     * user class holding important user data
     * in this class used to update the users total focus time
     */
    private final User user;

    /**
     * class used to interact with the user in the database
     */
    private final UserDAO userDAO;

    /**
     * Basic constructor for the timer class.
     * @param Timer_Controller The calling time controller.
     * @param TimerOffset   The offset of minutes before the timer starts.
     * @param TimerDuration The duration of the timer in minutes (0 to 1439).
     */
    public Timer (Double TimerOffset, Double TimerDuration, TimerController Timer_Controller, Notification Alarm){
        Timer_Offset = TimerOffset == null ? 0.0 : (TimerOffset * 60);
        Timer_Duration = TimerDuration == null ? 0.0 : (TimerDuration * 60);
        this.Timer_Controller = Timer_Controller;
        this.Alarm = Alarm;

        /* initialise the user */
        userDAO = new UserDAO();
        user = UserHolder.getInstance().getUser();

        CreateTimer();
    }

    public void TurnOnTTS(boolean value){
        Speak = value;
    }

    /**
     * This method acts as a factory method to update the fields with new values effectively restarting the timer.
     */
    private void CreateTimer(){
        Timer_State = (Timer_Offset != 0.0) ? TimerState.Delayed : TimerState.Running;

        this.Timer_Start = LocalTime.now().plusSeconds((long) Timer_Offset);
        this.Timer_End = Timer_Start.plusSeconds((long) Timer_Duration);

        boolean TimeCheck = Timer_End.isBefore(Timer_Start);
        Running_Counting_Duration = Duration.between(Timer_Start, Timer_End).plusHours(TimeCheck ? 24 : 0);
        Running_Total_Duration = Running_Counting_Duration;

        if(Timer_State == TimerState.Delayed){
            Delayed_Counting_Duration = Duration.between(LocalTime.now(),Timer_Start).plusHours(TimeCheck ? 24 : 0);
            Delayed_Total_Duration = Delayed_Counting_Duration;
        }
    }

    /**
     * Delayed Time is when the timer state is delayed, all code in this method will execute every millisecond.
     */
    public void DelayedTime(){
        Delayed_Counting_Duration = Delayed_Counting_Duration.minusMillis(1);

        if(Delayed_Counting_Duration.getSeconds() < 0) Start();

        Timer_Controller.UpdateMiniArc();
    }

    /**
     * Running Time is when the timer state is running, all code in this method will execute every millisecond.
     */
    public void RunningTime(){
        Running_Counting_Duration = Running_Counting_Duration.minusMillis(1);

        if(Running_Counting_Duration.getSeconds() < 0) Finish();

        Timer_Controller.UpdateArc();
        Timer_Controller.UpdateStopWatch();
    }

    /**
     * Formats the timer as (hh:mm:ss).
     * @return Formatted time (hh:mm:ss)
     */
    public String FormatTime(){
        Duration T = Running_Counting_Duration;

        long Hours = T.toHours();
        long Minutes = T.minusHours(Hours).toMinutes();
        long Seconds = T.minusHours(Hours).minusMinutes(Minutes).toSeconds();

        switch (Hours >= 1 ? 0 : Minutes >= 1 ? 1 : Seconds >= 0 ? 2 : -1){
            // Case 0: If Hours is >= 1, returns a time format like this (hh:mm:ss)
            case 0 -> { return String.format("%02d:%02d:%02d", Hours, Minutes, Seconds ); }

            // Case 1: if Minutes is >= 1, returns a time format like this (mm:ss)
            case 1 -> { return String.format("%02d:%02d", Minutes, Seconds); }

            // Case 2: if Seconds is >= 0, returns a time format like this (ss)
            case 2 -> { return String.format("%02d", Seconds); }

            default -> { return "--:--:--"; }
        }
    }

    /**
     * Enforces a state upon the timer.
     * @param EnforceThis The state that the timer will enforce.
     */
    private void EnforceState(TimerState EnforceThis){
        Timer_State = EnforceThis;
    }

    /**
     * Get the status based on the current timer state.
     * @return the status for the current timer state
     */
    public String getStatus(){
        switch (Timer_State){
            case Running -> { return "Timer Running"; }
            case Paused -> { return "Timer Paused"; }
            case Finished -> { return "Timer Finished"; }
            case Stopped -> { return "Timer Stopped"; }
            case Delayed -> { return Timer_Start.format(Timer_12_Format); }
            default -> throw new IllegalArgumentException("Invalid Time TimerState: " + Timer_State);
        }
    }

    /**
     * Issues a command to the timer, check Command Enum for valid commands.
     * @param command The command to run.
     */
    public void Control(Command command){
        switch (command) {
            case Start -> { if(Timer_State == TimerState.Delayed) DelayedStart(); else Start(); }
            case Stop -> Stop();
            case Finish -> Finish();
            case Pause -> Pause();
            case Resume -> Resume();
            case Restart -> Restart();
            default -> throw new IllegalArgumentException("Command: " + command + " is invalid.");
        }
    }

    /**
     * Puts the timer in a Delayed state and updates the GUI based on the state.
     */
    private void DelayedStart(){
        // State is enforced in the constructor.

        // Update GUI elements to their Pre-State for the current state.
        Timer_Controller.UpdateGUI();
        Timer_Controller.UpdateTimerStatus();
        Timer_Controller.ButtonStateManager();

        // If the text to speech is on.
        if (Speak) Notification.SpeakText("Timer starting at:" + Timer_Start.format(Timer_12_Format));

        // Start the Scheduler.
        TimeScheduler = Executors.newScheduledThreadPool(1);
        TimeScheduler.scheduleAtFixedRate(this::DelayedTime, 0, 1, TimeUnit.MILLISECONDS);
    }

    /**
     * Puts the timer in a Running state and updates the GUI based on the state.
     */
    private void Start(){
        // If timer is in a state of Delayed, shutdown the existing scheduler and enforce Running state,
        // otherwise state is enforced in the constructor.
        if(TimeScheduler != null && !TimeScheduler.isShutdown() && Timer_State == TimerState.Delayed) {
            EnforceState(TimerState.Running);
            TimeScheduler.shutdownNow();
        }

        // Update GUI elements to their Pre-State for the current state.
        Timer_Controller.UpdateGUI();
        Timer_Controller.UpdateTimerStatus();
        Timer_Controller.ButtonStateManager();

        // If the text to speech is on.
        if(Speak) Notification.SpeakText("Timer running.");

        // Start the Scheduler.
        TimeScheduler = Executors.newScheduledThreadPool(1);
        TimeScheduler.scheduleAtFixedRate(this::RunningTime, 0, 1, TimeUnit.MILLISECONDS);

        // Block URLS.
        BlockedController.blockCurrentUrls();
    }

    /**
     * Puts the timer in a Finished state and updates the GUI based on the state.
     */
    private void Finish(){
        // If timer is in a state of Running shutdown the existing scheduler and enforce Finished state.
        if(TimeScheduler != null && !TimeScheduler.isShutdown() && Timer_State == TimerState.Running){
            EnforceState(TimerState.Finished);
            TimeScheduler.shutdownNow();
        }

        // Update GUI elements to their Pre-State for the current state.
        Timer_Controller.UpdateGUI();
        Timer_Controller.UpdateStopWatch();
        Timer_Controller.UpdateTimerStatus();
        Timer_Controller.ButtonStateManager();

        Alarm.PlaySound();

        // If the text to speech is on.
        if(Speak) Notification.SpeakText("Timer finished.");
        user.setTotalFocusTime(userDAO.addToTotalTime(user.getId(), (long)Timer_Duration));

        // Unblock URLS.
        BlockedController.resetBlockedList();
    }

    /**
     * Puts the timer in a Stopped state and updates the GUI based on the state.
     */
    private void Stop(){

        if(TimeScheduler != null && !TimeScheduler.isShutdown() && (Timer_State == TimerState.Running || Timer_State == TimerState.Delayed)){
            EnforceState(TimerState.Stopped);
            TimeScheduler.shutdownNow();
        }

        /* update total focus time */
        user.setTotalFocusTime(userDAO.addToTotalTime(user.getId(), (long)Timer_Duration - Running_Counting_Duration.getSeconds()));

        // Drain the Running Counting Duration to -1.
        Running_Counting_Duration = Duration.ZERO.minusSeconds(1);

        // Update GUI elements to their Pre-State for the current state.
        Timer_Controller.UpdateGUI();
        Timer_Controller.UpdateTimerStatus();
        Timer_Controller.UpdateStopWatch();
        Timer_Controller.ButtonStateManager();

        // If the text to speech is on.
        if(Speak) Notification.SpeakText("Timer stopped.");

        // Unblock URLS.
        BlockedController.resetBlockedList();
    }

    /**
     * Puts the timer in a Restart state and updates the GUI based on the state then calls for a new timer.
     */
    private void Restart(){
        // Time scheduler has been shutdown either through the timer finishing or the timer being stopped early.
        EnforceState(TimerState.Restarting);

        Timer_Controller.UpdateGUI();
        Timer_Controller.ButtonStateManager();
        Timer_Controller.UpdateTimerStatus();

        CreateTimer();
        Control(Command.Start);
    }

    /**
     * Puts the timer in a Running state and updates the GUI based on the state,
     * Note: Resume can only be use if the timer is in a state of pause.
     */
    private void Resume(){
        // State Enforcement and Pre-State tasks.
        Timer_End = LocalTime.now().plus(Paused_Counting_Duration.toMillis(), ChronoUnit.MILLIS);
        Running_Counting_Duration = Paused_Counting_Duration;
        EnforceState(TimerState.Running);

        // Update GUI elements to their Pre-State for the current state.
        Timer_Controller.UpdateTimerStatus();
        Timer_Controller.UpdateStopWatch();
        Timer_Controller.ButtonStateManager();

        // If the text to speech is on.
        if (Speak) Notification.SpeakText("Timer resumed.");

        // Start the Scheduler.
        TimeScheduler = Executors.newScheduledThreadPool(1);
        TimeScheduler.scheduleAtFixedRate(this::RunningTime, 0, 1, TimeUnit.MILLISECONDS);
    }

    /**
     * Puts the timer in a Paused state and updates the GUI based on the state.
     */
    private void Pause(){
        // State Enforcement and Pre-State tasks.
        if(TimeScheduler != null && !TimeScheduler.isShutdown() && Timer_State == TimerState.Running){
            Paused_Counting_Duration = Duration.between(LocalTime.now(), Timer_End);
            EnforceState(TimerState.Paused);
            TimeScheduler.shutdownNow();
        }

        // Update GUI elements to their Pre-State for the current state.
        Timer_Controller.UpdateTimerStatus();
        Timer_Controller.UpdateStopWatch();
        Timer_Controller.ButtonStateManager();


        if(Speak) Notification.SpeakText("Timer paused.");
    }

    /**
     * Hard Stops the timer used for when the user closes application while timer is still running.
     */
    public static void ForceStopTimer(){
        if(TimeScheduler != null && !TimeScheduler.isShutdown()){
            TimeScheduler.shutdown();
            BlockedController.resetBlockedList();
            try{
                if(!TimeScheduler.awaitTermination(5, TimeUnit.SECONDS)){
                    TimeScheduler.shutdownNow();
                }
            } catch (InterruptedException E){
                TimeScheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}