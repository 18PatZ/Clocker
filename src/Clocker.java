import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by patrickzhong on 6/29/18.
 */
public class Clocker {

    /*
Patrick Zhong [10:46 AM]
start


Patrick Zhong [11:19 AM]
pause
(ManaMining) Decompiled source and added blocked block list

Patrick Zhong [3:26 PM]
start

Patrick Zhong [3:54 PM]
(PlayerPoints) Block offline mana pay

Patrick Zhong [4:06 PM]
(boosCooldowns) Jack up the priority to prevent cooldowns for cancelled commands

Patrick Zhong [5:20 PM]
(ManaClaimBox) Fully functional claim box with give and claim commands
stop

Patrick Zhong [10:16 PM]
>60+31+48+59-31+25-15+63+60+41-14+26+54-32+79-46+118

Patrick Zhong [12:51 PM]
start

Patrick Zhong [1:34 PM]
(ManaGlow) Preserve NTE tablist sort priority

Patrick Zhong [2:12 PM]
(ManaPlaceholders) Full functionality, with reload and test commands

Patrick Zhong [2:25 PM]
(ManaLevel) Format XP and ranking and display correct (one-indexed) level for placeholders
stop

Patrick Zhong [12:56 PM]
start

Patrick Zhong [3:05 PM]
(ManaGuilds) Large config system and basic skeleton

Patrick Zhong [3:12 PM]
stop
     */


    public static void main(String[] args){

        Scanner scanner = new Scanner(System.in);

        List<Entry> entries = new ArrayList<>();

        Entry current = new Entry();
        String pendingTime = null;
        int pendPlus = 0;

        while(scanner.hasNextLine()){

            String line = scanner.nextLine().replace(" (edited)", "");

            if(line.equalsIgnoreCase("END"))
                break;

            if(line.endsWith("AM]") || line.endsWith("PM]")){ // time line

                line = line.substring(line.indexOf("[") + 1, line.indexOf("M]")).replace(" ", ""); // ie 12:51P
                pendingTime = line;

            }
            else {
                boolean s = line.startsWith("start") || line.startsWith("cont");         // start cue
                boolean e = !s && (line.startsWith("stop") || line.startsWith("pause")); // stop cue

                if (s || e) {
                    if (pendingTime != null) {
                        if (s) current.setStart(pendingTime);
                        if (e) {
                            if(current.getStart() == null) current.setStart(pendingTime);
                            current.setEnd(pendingTime);
                            current.setComplete(true);
                            current.setDuration(calculateMinutes(current.getStart(), current.getEnd()) + pendPlus);
                            if(current.getLines().isEmpty())
                                current.getLines().add("Unspecified development work.");
                            entries.add(current);

                            current = new Entry();

                            pendPlus = 0;
                        }

                        pendingTime = null;
                    }
                }
                else {

                    if(line.startsWith(">") || line.startsWith("//") || line.length() == 0 || line.startsWith("*PAID*")) continue; // comment

                    if(line.startsWith("+")){
                        try {
                            pendPlus += Integer.parseInt(line.substring(1));
                            continue;
                        } catch (NumberFormatException ex){}
                    }
                    // description
                    current.getLines().add(line);

                }
            }

        }

        if(entries.size() > 0)
            if(!current.isComplete())
                entries.get(entries.size() - 1).getLines().addAll(current.getLines());

        int total = 0;

        System.out.println("_____________________________\n");

        for (Entry entry : entries) {

            total += entry.getDuration();

            System.out.println("");

            String s = entry.getStart() + "M" + " - " + entry.getEnd() + "M: " + form(entry.getDuration()) + "   ";

            int len = s.length();

            for(int i = 0; i < 90 - len; i++)
                s += "-";

            System.out.println(s + "-> TOTAL THUS FAR: " + form(total));
            entry.getLines().forEach(l -> System.out.println("  " + l));

        }

        System.out.println("\n_____________________________");

        System.out.println("\nTotal: " + form(total) + " (" + f(total / 60.0) + " hours) --> $" + f(total / 60.0 * 25));

    }

    private static String form(int time){
        int hr = time / 60;
        int min = time - hr * 60;

        return hr + " hour" + (hr==1?"":"s") + " " + min + " min" + (min==1?"":"s");
    }

    private static String f(double d){
        String s = d + "";
        if(!s.contains(".")) return s;
        return s.substring(0, Math.min(s.indexOf(".") + 3, s.length()));
    }

    private static int calculateMinutes(String start, String end){

        int time = parse(end) - parse(start);
        return time < 0 ? (time + 24 * 60) : time; // if negative add a day

    }

    private static int parse(String time){
        String mode = time.substring(time.length() - 1);

        String[] split = time.substring(0, time.length() - 1).split(":");
        int hr = Integer.parseInt(split[0]);
        int min = Integer.parseInt(split[1]);

        if(hr == 12) hr = 0;
        if(mode.equals("P")) hr += 12;

        return hr * 60 + min;
    }

}

@Data
class Entry {

    private List<String> lines = new ArrayList<>();
    private String start;
    private String end;
    private boolean complete = false;

    private int duration; // minutes

}
