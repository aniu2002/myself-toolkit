package com.sparrow.pushlet;

import com.sparrow.pushlet.event.Event;
import com.sparrow.pushlet.tools.Rand;

/**
 * Created by IntelliJ IDEA.
 * User: YZC
 * Date: 13-3-18
 * Time: 下午9:23
 * To change this template use File | Settings | File Templates.
 */
public class Subscription {
    public static final int ID_SIZE = 5;
    public static final String SUBJECT_SEPARATOR = ",";
    private String id = Rand.randomName(ID_SIZE);
    private String subject;
    private String[] subjects;
    private String label;

    protected Subscription() {
    }

    public static Subscription create(String aSubject) {
        return create(aSubject, null);
    }

    public static Subscription create(String aSubject, String aLabel) {
        if (aSubject == null || aSubject.length() == 0) {
            throw new IllegalArgumentException("Null or emtpy subject");
        }
        Subscription subscription = new Subscription();
        subscription.subject = aSubject;
        subscription.subjects = aSubject.split(SUBJECT_SEPARATOR);
        subscription.label = aLabel;
        return subscription;
    }


    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getSubject() {
        return subject;
    }

    /**
     * Determine if Event matches subscription.
     */
    public boolean match(Event event) {
        String eventSubject = event.getField(Protocol.P_SUBJECT);
        if (eventSubject == null || eventSubject.length() == 0) {
            return false;
        }
        for (int i = 0; i < subjects.length; i++) {
            if (eventSubject.startsWith(subjects[i])) {
                return true;
            }
        }
        return false;
    }
}
