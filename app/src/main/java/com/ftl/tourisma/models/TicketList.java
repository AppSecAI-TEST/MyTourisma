package com.ftl.tourisma.models;

import java.io.Serializable;

/**
 * Created by Vinay on 6/8/2017.
 */

public class TicketList implements Serializable {

    public String ticket_type;

    public TicketList() {

    }

    public TicketList(String ticket_type) {
        this.ticket_type = ticket_type;
    }

    public String getTicket_type() {
        return ticket_type;
    }

    public void setTicket_type(String ticket_type) {
        this.ticket_type = ticket_type;
    }
}
