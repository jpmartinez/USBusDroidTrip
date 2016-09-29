package tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Kavesa on 31/05/16.
 */
public class Parcel {
    private Long id;
    private Dimension dimensions;
    private Integer weight;
    private Journey journey;
    private Long journeyId;
    private BusStop origin;
    private String originName;
    private Long originId;
    private BusStop destination;
    private Long destinationId;
    private String destinationName;
    private String from;
    private String to;
    private Boolean delivered;
    private Boolean onDestination;
    private Boolean paid;
    private Date entered;
    private Date shippedDate;

    public Parcel(Long id, Dimension dimensions, Integer weight, Journey journey, Long journeyId, BusStop origin, String originName, Long originId, BusStop destination, Long destinationId, String destinationName, String from, String to, Boolean delivered, Boolean onDestination, Boolean paid, Date entered, Date shippedDate) {
        this.id = id;
        this.dimensions = dimensions;
        this.weight = weight;
        this.journey = journey;
        this.journeyId = journeyId;
        this.origin = origin;
        this.originName = originName;
        this.originId = originId;
        this.destination = destination;
        this.destinationId = destinationId;
        this.destinationName = destinationName;
        this.from = from;
        this.to = to;
        this.delivered = delivered;
        this.onDestination = onDestination;
        this.paid = paid;
        this.entered = entered;
        this.shippedDate = shippedDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Dimension getDimensions() {
        return dimensions;
    }

    public void setDimensions(Dimension dimensions) {
        this.dimensions = dimensions;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Journey getJourney() {
        return journey;
    }

    public void setJourney(Journey journey) {
        this.journey = journey;
    }

    public Long getJourneyId() {
        return journeyId;
    }

    public void setJourneyId(Long journeyId) {
        this.journeyId = journeyId;
    }

    public BusStop getOrigin() {
        return origin;
    }

    public void setOrigin(BusStop origin) {
        this.origin = origin;
    }

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    public Long getOriginId() {
        return originId;
    }

    public void setOriginId(Long originId) {
        this.originId = originId;
    }

    public BusStop getDestination() {
        return destination;
    }

    public void setDestination(BusStop destination) {
        this.destination = destination;
    }

    public Long getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(Long destinationId) {
        this.destinationId = destinationId;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Boolean getDelivered() {
        return delivered;
    }

    public void setDelivered(Boolean delivered) {
        this.delivered = delivered;
    }

    public Boolean getOnDestination() {
        return onDestination;
    }

    public void setOnDestination(Boolean onDestination) {
        this.onDestination = onDestination;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public Date getEntered() {
        return entered;
    }

    public void setEntered(Date entered) {
        this.entered = entered;
    }

    public Date getShippedDate() {
        return shippedDate;
    }

    public void setShippedDate(Date shippedDate) {
        this.shippedDate = shippedDate;
    }

    public Parcel(JSONObject object){
        try {
            from = object.getString("from");
            dimensions = new Dimension(object.getJSONObject("dimensions").getDouble("height"),
                    object.getJSONObject("dimensions").getDouble("width"),
                    object.getJSONObject("dimensions").getDouble("depth"));
            id = object.getLong("id");
            weight = object.getInt("weight");
            onDestination = object.getBoolean("onDestination");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Parcel> fromJson(JSONArray jsonObjects) {
        ArrayList<Parcel> routeStops = new ArrayList<>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                routeStops.add(new Parcel(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return routeStops;
    }

}
