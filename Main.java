package com.company;
import java.util.*;

class EventService {
    private ArrayList<Subscription> subscriptions;
    private static EventService singleton = null;

    private EventService() {
        subscriptions = new ArrayList<Subscription>();
    }

    public static EventService getInstance() {
        if (singleton == null)
            singleton = new EventService();
        return singleton;
    }

    public void publish(Event event) {
        int i;
        for (i = 0; i < subscriptions.size(); i++)
            if ((subscriptions.get(i).filter == null || subscriptions.get(i).filter.apply(event) )&& subscriptions.get(i).getEvent().getClass().equals(event.getClass()))	// +++++
                subscriptions.get(i).getSubscriber().inform(event);	//+++++++++
    }

    public void subscribe(Event anEvent, Filter aFilter, SubscriberPublisher aSubscriber) {
        Subscription subscription = new Subscription(anEvent, aFilter, aSubscriber);
        if (!subscriptions.contains(subscription))
            subscriptions.add(subscription);
    }

    public void unsubscribe(Event anEvent, Filter aFilter, SubscriberPublisher aSubscriber) {
        Subscription subscription = new Subscription(anEvent, aFilter, aSubscriber);
        subscriptions.remove(subscription);
    }
}

class Subscription {
    private Event event;
    public Filter filter;
    private SubscriberPublisher subscriber;

    public Subscription(Event anEvent, Filter aFilter, SubscriberPublisher aSubscriber) {
        this.event = anEvent;
        this.filter = aFilter;
        this.subscriber = aSubscriber;
    }

    public Event getEvent() {
        return event;
    }

    public Filter getFilter() {
        return filter;
    }

    public SubscriberPublisher getSubscriber() {
        return subscriber;
    }
}

abstract class Event {
    abstract public String toString();
    abstract public Oferta getOferta();
}

class Oferta {
    String name, data;
    int valoare;

    public Oferta(String name, String data,int valoare)
    {
        this.name = name;
        this.data = data;
        this.valoare = valoare;
    }

    public void setData(String data)
    {
        this.data= data;
    }
    public String getData(){
        return data;
    }

    public void setValoare(int valoare)
    {
        this.valoare = valoare;
    }

    public int getValoare(){
        return valoare;
    }

    public String getName(){
        return name;
    }

    public  String toString(){
        return " Oferta " + getName() + " " + getData() + " " + getValoare();
    }

}

class NouaOfertaSapun extends Event {
    private Oferta noua;

    public NouaOfertaSapun(String name, String data, int valoare) {
        this.noua = new Oferta(name, data,valoare);
    }

    public NouaOfertaSapun(){}
    public Oferta getOferta() {
        return noua;
    }

    public String toString() { return "O noua oferta! " + noua.toString();}

}


class NouaOfertaMaieu extends Event {
    private Oferta noua;

    public NouaOfertaMaieu() {

    }

    public NouaOfertaMaieu(String name, String data, int valoare) {
        this.noua = new Oferta(name, data,valoare);
    }

    public Oferta getOferta() {
        return noua;
    }

    public String toString() { return "O noua oferta! " + noua.toString();}

}

class ModificatOfertaSapun extends Event {
    private Oferta curenta;

    public ModificatOfertaSapun(Oferta curenta, String data) {
        this.curenta = curenta;
        this.curenta.setData(data);
    }

    public ModificatOfertaSapun(Oferta curenta, int valoare) {
        this.curenta = curenta;
        this.curenta.setValoare(valoare);
    }

    public ModificatOfertaSapun(Oferta curenta, String data, int valoare) {
        this.curenta = curenta;
        this.curenta.setData(data);
        this.curenta.setValoare(valoare);
    }

    public ModificatOfertaSapun(){}

    public Oferta getOferta(){
        return curenta;
    }

    public String toString() {
        return "S-a modificat oferta " + curenta.toString();
    }
}

abstract class Filter {
    abstract public boolean apply(Event event);
}

class Data extends Filter {
    private String data;

    public Data(String data){
        this.data=data;
    }
    public boolean apply(Event event){
        if (event instanceof ModificatOfertaSapun || event instanceof NouaOfertaSapun)
            if(event.getOferta().getData().equals(data))
                return true;
        return false;
    }
}

class Valoare extends Filter {
    private int valoare;

    public Valoare(int valoare){
        this.valoare=valoare;
    }

    public boolean apply(Event event){
        if (event instanceof ModificatOfertaSapun || event instanceof NouaOfertaSapun)
            if(event.getOferta().getValoare() == valoare)
                return true;
        return false;
    }
}


interface SubscriberPublisher {
    void inform(Event event);
    void createEvent(Oferta oferta);
}

class Cumparator1 implements SubscriberPublisher {

    private String name;
    private EventService service;

    public Cumparator1(String name, EventService service) {
        this.name = name;
        this.service = service;
    }
    public void inform(Event event) {
        if( event.getOferta().getValoare()>10  )
            System.out.println("Clientul " + name + " a fost anuntat de oferta " + event.getOferta().getName());
    }

    public void createEvent(Oferta oferta)
    {
        Event e1 = new NouaOfertaSapun("rio","AZI",34);
        service.publish(e1);
    }
}

class Cumparator2 implements SubscriberPublisher {

    private String name;
    private EventService service;

    public Cumparator2(String name, EventService service) {
        this.name = name;
        this.service = service;
    }

    public void inform(Event event) {
            System.out.println("Clientul " + name + " a fost anuntat de oferta " + event.getOferta().getName());
    }

    public void createEvent(Oferta oferta)
    {
        Event e2 = new ModificatOfertaSapun(oferta, " hai");
        service.publish(e2);
    }
}

class Main{
    public static void main(String argv[])
    {
        EventService service = EventService.getInstance();

        SubscriberPublisher a1 = new Cumparator1("ION",service);
        SubscriberPublisher a2 = new Cumparator2("ANA",service);

        Event o1 = new NouaOfertaSapun("A","A",17);
        service.subscribe(o1,new Data("AZI"),a1);
        service.subscribe(new NouaOfertaMaieu(),null,a2);
        service.subscribe(new ModificatOfertaSapun(o1.getOferta()," hai"),null,a1);

        service.publish(new NouaOfertaSapun("ROZ","IERI",90));
        service.publish(new NouaOfertaSapun("ALB","AZI",34));
        service.publish(new NouaOfertaMaieu("SAMSD","GFDS",34));

        a2.createEvent(o1.getOferta());
    }
}
