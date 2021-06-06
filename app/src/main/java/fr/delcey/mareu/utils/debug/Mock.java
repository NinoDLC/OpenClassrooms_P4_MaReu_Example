package fr.delcey.mareu.utils.debug;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fr.delcey.mareu.data.meeting.model.Room;

public class Mock {

    public static String getRandomMeetingTopic() {
        char meetingLetter = (char) ('A' + new Random().nextInt(26));

        return "Réunion " + meetingLetter;
    }

    public static LocalTime getRandomMeetingHour() {
        int hour = new Random().nextInt(24);
        int minutes = new Random().nextInt(60);

        return LocalTime.of(hour, minutes);
    }

    public static List<String> getRandomMeetingParticipants() {
        List<String> participants = new ArrayList<>();

        int randomCount = new Random().nextInt(10) + 1;

        for (int i = 0; i < randomCount; i++) {
            participants.add(
                FIRST_NAMES[new Random().nextInt(FIRST_NAMES.length)] +
                    "." +
                    LAST_NAMES[new Random().nextInt(LAST_NAMES.length)] +
                    "@" +
                    EMAIL_PROVIDERS[new Random().nextInt(EMAIL_PROVIDERS.length)]
            );
        }

        return participants;
    }

    public static Room getRandomMeetingRoom() {
        return Room.values()[new Random().nextInt(Room.values().length - 1) + 1];
    }

    private static final String[] FIRST_NAMES = new String[]{
        "veola",
        "morris",
        "maryellen",
        "misti",
        "madalene",
        "alaine",
        "thea",
        "traci",
        "windy",
        "jessie",
        "adah",
        "catherine",
        "alana",
        "kari",
        "bennett",
        "elmira",
        "elicia",
        "wally",
        "gregg",
        "luna"
    };

    private static final String[] LAST_NAMES = new String[]{
        "cotnoir",
        "yingst",
        "rasnick",
        "bondy",
        "iverson",
        "barfoot",
        "streiff",
        "shive",
        "carruthers",
        "maltos",
        "tisher",
        "kohnke",
        "woolum",
        "pickel",
        "cannon",
        "macintosh",
        "kuhns",
        "molino",
        "steed",
        "rigdon"
    };

    private static final String[] EMAIL_PROVIDERS = new String[]{
        "gmail.com",
        "hotmail.com",
        "yahoo.com",
        "outlook.com",
        "one.com"
    };
}
