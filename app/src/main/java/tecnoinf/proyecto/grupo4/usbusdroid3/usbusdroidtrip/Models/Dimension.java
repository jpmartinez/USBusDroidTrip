package tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Models;

/**
 * Created by Kavesa on 19/08/16.
 */
public class Dimension {
    private Double height;
    private Double width;
    private Double depth;

    public Dimension(){
    }

    public Dimension(Double height, Double width, Double depth) {
        this.height = height;
        this.width = width;
        this.depth = depth;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getDepth() {
        return depth;
    }

    public void setDepth(Double depth) {
        this.depth = depth;
    }

    @Override
    public String toString() {
        return this.getWidth().toString()+" x "+this.getDepth().toString()+" x "+this.getHeight().toString();
    }
}
