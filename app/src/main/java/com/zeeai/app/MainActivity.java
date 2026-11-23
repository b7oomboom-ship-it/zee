package com.zeeai.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.speech.tts.TextToSpeech;
import android.widget.*;
import java.util.Locale;

public class MainActivity extends Activity {
    private TextToSpeech voice;
    private LinearLayout root;
    private final int bg=Color.rgb(16,19,34), panel=Color.rgb(30,35,55), pink=Color.rgb(255,107,157);
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        voice = new TextToSpeech(this, status -> { if(status==TextToSpeech.SUCCESS && voice!=null) voice.setLanguage(Locale.ENGLISH); });
        home();
    }
    private TextView text(String s,int size){TextView v=new TextView(this);v.setText(s);v.setTextSize(size);v.setTextColor(Color.WHITE);v.setPadding(16,15,16,15);return v;}
    private void screen(String title){
        ScrollView sc=new ScrollView(this);sc.setFillViewport(true);sc.setBackgroundColor(bg);
        root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(14,16,14,24);sc.addView(root);setContentView(sc);
        TextView h=text("✦  "+title,27);h.setTextColor(pink);h.setTypeface(null,Typeface.BOLD);root.addView(h);
        root.addView(button("⌂ Home",()->home()));
    }
    private Button button(String label,Runnable action){Button b=new Button(this);b.setText(label);b.setAllCaps(false);b.setOnClickListener(v->action.run());return b;}
    private EditText input(String hint){EditText e=new EditText(this);e.setHint(hint);e.setHintTextColor(Color.LTGRAY);e.setTextColor(Color.WHITE);e.setSingleLine(false);root.addView(e);return e;}
    private void note(String s){TextView t=text(s,15);t.setBackgroundColor(panel);root.addView(t);}
    private void home(){screen("ZEE AI");
        note("Industrial automation learning assistant. Offline demo — not connected to a real AI model.");
        EditText question=input("Ask about Siemens, Delta, Mitsubishi, VFD or Arduino");
        TextView answer=text("Hello! I'm Zee. Ask an automation question.",16);root.addView(answer);
        root.addView(button("Ask Zee",()->answer.setText(reply(question.getText().toString()))));
        root.addView(button("Speak answer",()->{if(voice!=null)voice.speak(answer.getText(),TextToSpeech.QUEUE_FLUSH,null,"zee");}));
        root.addView(button("PLC Studio",()->plc()));root.addView(button("HMI Simulator",()->hmi()));
        root.addView(button("Electrical + VFD Calculator",()->calc()));root.addView(button("Arduino Lab",()->arduino()));
        note("Safety: educational examples only. Never use this demo to control machinery or bypass safety systems.");
    }
    private String reply(String q){q=q.toLowerCase(Locale.ROOT);
        if(q.contains("siemens"))return "Siemens: In TIA Portal, create a project, select the exact S7 CPU, configure I/O, then test ladder logic in PLCSIM.";
        if(q.contains("mitsubishi"))return "Mitsubishi: Check FX or iQ CPU and GX Works version. X inputs, Y outputs and M internal bits are common device types.";
        if(q.contains("delta"))return "Delta: Check PLC series and ISPSoft or WPLSoft compatibility. X inputs, Y outputs and M internal relays are commonly used.";
        if(q.contains("vfd")||q.contains("motor"))return "VFD: verify motor nameplate, rated voltage and current, base frequency, ramp times and overload protection.";
        if(q.contains("arduino"))return "Arduino: define board, sensor, pin numbers and supply voltage; use a suitable driver for motors or relays.";
        return "This is an offline demo with fixed guidance. A real AI backend can be added later. Ask about Siemens, Mitsubishi, Delta, VFD or Arduino.";
    }
    private void plc(){screen("PLC Studio");note("Siemens S7-1200 / Mitsubishi FX / Delta — learning only");
        note("START/STOP concept: If E-stop and overload circuits are healthy, START sets the run request. STOP clears it. Hardware safety must be independent of PLC software.");
        note("Siemens SCL learning sketch:\nIF NOT Stop_OK OR NOT Overload_OK THEN\n  RunCmd := FALSE;\nELSIF StartPB THEN\n  RunCmd := TRUE;\nEND_IF;\n\nNot validated for any CPU or wiring.");
    }
    private void hmi(){screen("HMI Simulator");note("LOCAL DEMO: Buttons change a simulated motor indicator. No PLC connection.");
        TextView status=text("MOTOR STOPPED",22);root.addView(status);
        root.addView(button("START (simulation)",()->{status.setText("MOTOR RUNNING");status.setTextColor(0xff64e0c1);}));
        root.addView(button("STOP (simulation)",()->{status.setText("MOTOR STOPPED");status.setTextColor(Color.WHITE);}));
    }
    private void calc(){screen("Electrical + VFD");note("Basic DC power P = V × I");
        EditText v=input("Voltage (V) e.g. 24");EditText a=input("Current (A) e.g. 2");TextView out=text("Result",18);root.addView(out);
        root.addView(button("Calculate",()->{try{double power=Double.parseDouble(v.getText().toString())*Double.parseDouble(a.getText().toString());out.setText(String.format(Locale.US,"DC power = %.2f W",power));}catch(Exception e){out.setText("Enter valid numbers.");}}));
        note("VFD approximate speed ratio = target frequency / rated frequency. Actual speed depends on poles and slip.");
    }
    private void arduino(){screen("Arduino Lab");note("LED blink starter for Arduino Uno:\n\nvoid setup() { pinMode(13, OUTPUT); }\nvoid loop() { digitalWrite(13, HIGH); delay(500); digitalWrite(13, LOW); delay(500); }\n\nCheck your board voltage and pin limits.");}
    @Override protected void onDestroy(){if(voice!=null){voice.stop();voice.shutdown();}super.onDestroy();}
}
